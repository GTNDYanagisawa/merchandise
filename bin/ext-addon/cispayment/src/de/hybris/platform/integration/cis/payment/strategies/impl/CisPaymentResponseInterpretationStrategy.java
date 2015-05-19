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
package de.hybris.platform.integration.cis.payment.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.acceleratorservices.payment.strategies.PaymentResponseInterpretationStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.impl.AbstractPaymentResponseInterpretationStrategy;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.integration.cis.payment.constants.CispaymentConstants;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.api.exception.AbstractCisServiceException;
import com.hybris.cis.api.exception.codes.ServiceExceptionDetail;
import com.hybris.cis.api.payment.model.CisExternalPaymentRequest;
import com.hybris.cis.api.payment.model.CisPaymentProfileResult;
import com.hybris.cis.client.rest.payment.PaymentClient;
import com.hybris.commons.client.RestResponse;


/**
 * CIS specific payment-profile-creation response interpretation strategy.
 */
public class CisPaymentResponseInterpretationStrategy extends AbstractPaymentResponseInterpretationStrategy
{
	private static final Logger LOG = Logger.getLogger(CisPaymentResponseInterpretationStrategy.class);

	private PaymentClient paymentClient;
	private Converter<RestResponse<CisPaymentProfileResult>, CreateSubscriptionResult> cisCreateSubscriptionResultConverter;
	private OndemandHystrixCommandConfiguration hystrixCommandConfig;
	private PaymentResponseInterpretationStrategy paymentResponseInterpretationStrategy;
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;

	@Override
	public CreateSubscriptionResult interpretResponse(final Map<String, String> responseParams, final String clientRef,
			final Map<String, PaymentErrorField> errors)
	{

		responseParams.put("VerifyTransactionSignature()", "true");
		final CisExternalPaymentRequest externalPaymentRequest = new CisExternalPaymentRequest(responseParams);

		return getOndemandHystrixCommandFactory().newCommand(getHystrixCommandConfig(),
				new HystrixExecutable<CreateSubscriptionResult>()
				{
					@Override
					public CreateSubscriptionResult runEvent()
					{

						// there where errors in the payment form, skip the rest call.
						if ("REJECT".equalsIgnoreCase(responseParams.get("decision")))
						{
							return defaultEvent();
						}
						else
						{
							try
							{
								final CreateSubscriptionResult createSubscriptionResult = new CreateSubscriptionResult();
								final RestResponse<CisPaymentProfileResult> response = getPaymentClient().addCustomerProfile(clientRef,
										StringUtils.EMPTY, externalPaymentRequest);
								return getCisCreateSubscriptionResultConverter().convert(response, createSubscriptionResult);
							}
							catch (final AbstractCisServiceException exception)
							{
								final CreateSubscriptionResult createSubscriptionResult = new CreateSubscriptionResult();
								createSubscriptionResult.setReasonCode(CispaymentConstants.GENERAL_PAYMENT_ERR_CODE);
								createSubscriptionResult.setDecision(DecisionsEnum.REJECT.name());
								final List<ServiceExceptionDetail> errorCodes = exception.getErrorCodes();
								final Map<Integer, String> errorCodeMap = new HashMap<Integer, String>(errorCodes.size());
								for (final ServiceExceptionDetail errorCode : errorCodes)
								{
									errorCodeMap.put(Integer.valueOf(errorCode.getCode()), errorCode.getMessage());
								}
								createSubscriptionResult.setErrors(errorCodeMap);
								return createSubscriptionResult;
							}
						}
					}

					@Override
					public CreateSubscriptionResult defaultEvent()
					{
						return getPaymentResponseInterpretationStrategy().interpretResponse(responseParams, clientRef, errors);
					}

					@Override
					public CreateSubscriptionResult fallbackEvent()
					{
						LOG.warn("An error occurred while attempting to add a customer profile through CIS. Executing Hystrix fall-back event.");
						return createSubscriptionResult();
					}
				}).execute();
	}

	protected CreateSubscriptionResult createSubscriptionResult()
	{
		final CreateSubscriptionResult createSubscriptionResult = new CreateSubscriptionResult();
		createSubscriptionResult.setDecision(DecisionsEnum.ERROR.name());
		createSubscriptionResult.setReasonCode(CispaymentConstants.HYSTRIX_FALLBACK_ERR_CODE);
		return createSubscriptionResult;
	}

	protected PaymentClient getPaymentClient()
	{
		return paymentClient;
	}

	@Required
	public void setPaymentClient(final PaymentClient paymentClient)
	{
		this.paymentClient = paymentClient;
	}

	protected Converter<RestResponse<CisPaymentProfileResult>, CreateSubscriptionResult> getCisCreateSubscriptionResultConverter()
	{
		return cisCreateSubscriptionResultConverter;
	}

	@Required
	public void setCisCreateSubscriptionResultConverter(
			final Converter<RestResponse<CisPaymentProfileResult>, CreateSubscriptionResult> cisCreateSubscriptionResultConverter)
	{
		this.cisCreateSubscriptionResultConverter = cisCreateSubscriptionResultConverter;
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

	protected PaymentResponseInterpretationStrategy getPaymentResponseInterpretationStrategy()
	{
		return paymentResponseInterpretationStrategy;
	}

	@Required
	public void setPaymentResponseInterpretationStrategy(
			final PaymentResponseInterpretationStrategy paymentResponseInterpretationStrategy)
	{
		this.paymentResponseInterpretationStrategy = paymentResponseInterpretationStrategy;
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
