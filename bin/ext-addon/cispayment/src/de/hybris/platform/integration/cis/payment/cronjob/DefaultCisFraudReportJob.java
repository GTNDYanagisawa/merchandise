/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.integration.cis.payment.cronjob;

import de.hybris.platform.acceleratorservices.payment.PaymentService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.integration.cis.payment.impl.DefaultCisFraudReportDao;
import de.hybris.platform.integration.cis.payment.model.CisFraudReportCronJobModel;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.api.fraud.model.CisFraudReportRequest;
import com.hybris.cis.api.fraud.model.CisFraudReportResult;
import com.hybris.cis.api.fraud.model.CisFraudTransactionResult;
import com.hybris.cis.client.rest.fraud.FraudClient;
import com.hybris.commons.client.RestResponse;


/**
 * Cronjob that gets that gets the fraud report as xml from CIS. This will process the fraud updates and update the
 * order accordingly.
 */
public class DefaultCisFraudReportJob extends AbstractJobPerformable<CisFraudReportCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultCisFraudReportJob.class);
	private static final int BLOCK_SIZE = 100;

	private OndemandHystrixCommandConfiguration hystrixCommandConfig;
	private FraudClient fraudClient;
	private Converter<CisFraudTransactionResult, PaymentTransactionEntryModel> transactionResultConverter;
	private BusinessProcessService businessProcessService;
	private ModelService modelService;
	private PaymentService paymentService;
	private DefaultCisFraudReportDao cisFraudReportDao;
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;

	@Override
	public PerformResult perform(final CisFraudReportCronJobModel cronJob)
	{
		try
		{
			final CisFraudReportRequest request = new CisFraudReportRequest();
			final Date requestStartTime = getLastCronJobEndTime();
			final Date requestEndTime = new Date();
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, -24);

			if (requestStartTime.before(calendar.getTime()))
			{
				LOG.error("CyberSource report time range is only valid for the past 24 hours");
				return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
			}

			request.setStartDateTime(requestStartTime);
			request.setEndDateTime(requestEndTime);
			final RestResponse<CisFraudReportResult> response = getOndemandHystrixCommandFactory().newCommand(
					getHystrixCommandConfig(), new HystrixExecutable<RestResponse<CisFraudReportResult>>()
					{
						@Override
						public RestResponse<CisFraudReportResult> runEvent()
						{
							return getFraudClient().generateFraudReport(cronJob.getCode(), request);
						}

						@Override
						public RestResponse<CisFraudReportResult> fallbackEvent()
						{
							return null;
						}

						@Override
						public RestResponse<CisFraudReportResult> defaultEvent()
						{
							return null;
						}
					}).execute();

			if (response != null)
			{
				final List<CisFraudTransactionResult> transactionResults = response.getResult().getTransactions();

				if (CollectionUtils.isNotEmpty(transactionResults))
				{
					//Retrieve all the transactionIds from each of the transaction results
					final List<String> transactionIds = (List<String>) CollectionUtils.collect(transactionResults, new Transformer()
					{
						@Override
						public Object transform(final Object o)
						{
							final CisFraudTransactionResult result = (CisFraudTransactionResult) o;

							return result.getClientAuthorizationId();
						}
					});

					final List<PaymentTransactionEntryModel> transactionEntries = getPaymentTransactionEntryModels(transactionIds);

					//For each transaction result, set the transaction from it's corresponding order and fire the business process event
					for (final CisFraudTransactionResult transactionResult : transactionResults)
					{
						final PaymentTransactionEntryModel transactionEntry = (PaymentTransactionEntryModel) CollectionUtils.find(
								transactionEntries, new Predicate()
								{
									@Override
									public boolean evaluate(final Object o)
									{
										return ((PaymentTransactionEntryModel) o).getCode().equalsIgnoreCase(
												transactionResult.getClientAuthorizationId());
									}
								});

						if (transactionEntry != null && transactionEntry.getPaymentTransaction() != null
								&& transactionEntry.getPaymentTransaction().getOrder() != null)
						{
							final PaymentTransactionModel transaction = transactionEntry.getPaymentTransaction();
							final String guid = transaction.getOrder().getGuid();

							final PaymentTransactionEntryModel newTransactionEntry = getTransactionResultConverter().convert(
									transactionResult);
							getPaymentService().setPaymentTransactionReviewResult(newTransactionEntry, guid);
						}
					}
				}

				//Set the LastFraudReportEndTime for use the next time this cron job runs
				cronJob.setLastFraudReportEndTime(requestEndTime);
				getModelService().save(cronJob);

				return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
			}

			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
		catch (final Exception e)
		{
			LOG.warn(String.format("Error occurred while processing the fraud reports [%s]", e.getLocalizedMessage()));
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Error occurred while processing the fraud reports", e);
			}
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	/*
	 * Retrieve the EndDate used when we last generated the fraud report
	 */
	protected Date getLastCronJobEndTime()
	{
		final Date endTime = getCisFraudReportDao().findLastFraudReportEndTime();

		if (endTime == null)
		{
			final Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, -23);
			calendar.add(Calendar.MINUTE, -55);

			return calendar.getTime();
		}

		return endTime;
	}

	/*
	 * Retrieve orders from the accelerator blocks at a time
	 */
	protected List<PaymentTransactionEntryModel> getPaymentTransactionEntryModels(final List<String> transactionIds)
	{
		final List<PaymentTransactionEntryModel> models = new ArrayList<PaymentTransactionEntryModel>();

		for (int block = 0; block < ((transactionIds.size() / BLOCK_SIZE) + 1); block++)
		{
			final int fromIndex = block * BLOCK_SIZE;
			int toIndex = (block + 1) * BLOCK_SIZE;

			if (toIndex > transactionIds.size())
			{
				toIndex = transactionIds.size();
			}

			models.addAll(getCisFraudReportDao().findTransactionsByCode(transactionIds.subList(fromIndex, toIndex)));
		}

		return models;
	}

	public OndemandHystrixCommandConfiguration getHystrixCommandConfig()
	{
		return hystrixCommandConfig;
	}

	@Required
	public void setHystrixCommandConfig(final OndemandHystrixCommandConfiguration hystrixCommandConfig)
	{
		this.hystrixCommandConfig = hystrixCommandConfig;
	}

	public FraudClient getFraudClient()
	{
		return fraudClient;
	}

	@Required
	public void setFraudClient(final FraudClient fraudClient)
	{
		this.fraudClient = fraudClient;
	}

	public Converter<CisFraudTransactionResult, PaymentTransactionEntryModel> getTransactionResultConverter()
	{
		return transactionResultConverter;
	}

	@Required
	public void setTransactionResultConverter(
			final Converter<CisFraudTransactionResult, PaymentTransactionEntryModel> transactionResultConverter)
	{
		this.transactionResultConverter = transactionResultConverter;
	}

	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Override
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public PaymentService getPaymentService()
	{
		return paymentService;
	}

	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	public DefaultCisFraudReportDao getCisFraudReportDao()
	{
		return cisFraudReportDao;
	}

	@Required
	public void setCisFraudReportDao(final DefaultCisFraudReportDao cisFraudReportDao)
	{
		this.cisFraudReportDao = cisFraudReportDao;
	}

	protected OndemandHystrixCommandFactory getOndemandHystrixCommandFactory()
	{
		return ondemandHystrixCommandFactory;
	}

	@Required
	public void setOndemandHystrixCommandFactory(final OndemandHystrixCommandFactory ondemandHystrixCommandFactory)
	{
		this.ondemandHystrixCommandFactory = ondemandHystrixCommandFactory;
	}
}
