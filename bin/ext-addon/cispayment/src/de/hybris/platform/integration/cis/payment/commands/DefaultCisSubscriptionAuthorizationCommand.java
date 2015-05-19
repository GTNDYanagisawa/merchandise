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
package de.hybris.platform.integration.cis.payment.commands;

import de.hybris.platform.integration.cis.payment.converter.CisAuthorizationResultConverter;
import de.hybris.platform.integration.cis.payment.converter.SubscriptionAuthorizationRequestConverter;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;
import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.api.exception.AbstractCisServiceException;
import com.hybris.cis.api.exception.ServiceRequestException;
import com.hybris.cis.api.exception.codes.ServiceExceptionDetail;
import com.hybris.cis.api.payment.model.CisPaymentAuthorization;
import com.hybris.cis.api.payment.model.CisPaymentTransactionResult;
import com.hybris.cis.client.rest.payment.PaymentClient;
import com.hybris.commons.client.RestResponse;


/**
 * Implementation of SubscriptionAuthorizationCommand using CIS web services to call the payment provider.
 */
public class DefaultCisSubscriptionAuthorizationCommand implements SubscriptionAuthorizationCommand
{
	private static final Logger LOG = Logger.getLogger(DefaultCisSubscriptionAuthorizationCommand.class);
	private PaymentClient paymentClient;
	private SubscriptionAuthorizationRequestConverter subscriptionAuthorizationRequestConverter;
	private CisAuthorizationResultConverter cisAuthorizationResultConverter;
	private OndemandHystrixCommandConfiguration hystrixCommandConfig;
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;

	@Override
	public AuthorizationResult perform(final SubscriptionAuthorizationRequest request)
	{
		return getOndemandHystrixCommandFactory().newCommand(getHystrixCommandConfig(),
				new HystrixExecutable<AuthorizationResult>()
				{
					@Override
					public AuthorizationResult runEvent()
					{
						try
						{
							final CisPaymentAuthorization cisRequest = getSubscriptionAuthorizationRequestConverter().convert(request);
							final UriBuilder uriBuilder = UriBuilder.fromUri(request.getSubscriptionID());
							final RestResponse<CisPaymentTransactionResult> authorization = getPaymentClient().authorizeWithProfile(
									request.getMerchantTransactionCode(), cisRequest, uriBuilder.build());
							return getCisAuthorizationResultConverter().convert(authorization);
						}
						catch (final ServiceRequestException e)
						{
							final Map<Integer, String> errors = extractErrorCodes(e);
							if (LOG.isDebugEnabled())
							{
								LOG.debug("Authorization errors " + errors);
							}
							return createAuthorizationResult(TransactionStatusDetails.AUTHORIZATION_REJECTED_BY_PSP,
									TransactionStatus.REJECTED);
						}
					}

					@Override
					public AuthorizationResult defaultEvent()
					{
						return createAuthorizationResult(TransactionStatusDetails.SUCCESFULL, TransactionStatus.ACCEPTED);
					}

					@Override
					public AuthorizationResult fallbackEvent()
					{
						return createAuthorizationResult(TransactionStatusDetails.UNKNOWN_CODE, TransactionStatus.ERROR);
					}
				}).execute();
	}

	protected AuthorizationResult createAuthorizationResult(final TransactionStatusDetails transactionStatusDetails,
			final TransactionStatus transactionStatus)
	{
		final AuthorizationResult authorizationResult = new AuthorizationResult();
		authorizationResult.setTransactionStatusDetails(transactionStatusDetails);
		authorizationResult.setTransactionStatus(transactionStatus);
		return authorizationResult;
	}

	protected Map<Integer, String> extractErrorCodes(final AbstractCisServiceException failedExecutionException)
	{
		final List<ServiceExceptionDetail> errorCodes = failedExecutionException.getErrorCodes();
		final Map<Integer, String> errorCodeMap = new HashMap<Integer, String>(errorCodes.size());
		for (final ServiceExceptionDetail errorCode : errorCodes)
		{
			errorCodeMap.put(Integer.valueOf(errorCode.getCode()), errorCode.getMessage());
		}
		return errorCodeMap;
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

	protected SubscriptionAuthorizationRequestConverter getSubscriptionAuthorizationRequestConverter()
	{
		return subscriptionAuthorizationRequestConverter;
	}

	@Required
	public void setSubscriptionAuthorizationRequestConverter(
			final SubscriptionAuthorizationRequestConverter subscriptionAuthorizationRequestConverter)
	{
		this.subscriptionAuthorizationRequestConverter = subscriptionAuthorizationRequestConverter;
	}

	protected CisAuthorizationResultConverter getCisAuthorizationResultConverter()
	{
		return cisAuthorizationResultConverter;
	}

	@Required
	public void setCisAuthorizationResultConverter(final CisAuthorizationResultConverter cisAuthorizationResultConverter)
	{
		this.cisAuthorizationResultConverter = cisAuthorizationResultConverter;
	}

	protected OndemandHystrixCommandConfiguration getHystrixCommandConfig()
	{
		return hystrixCommandConfig;
	}

	@Required
	public void setHystrixCommandConfig(final OndemandHystrixCommandConfiguration hystrixCommandConfig)
	{
		this.hystrixCommandConfig = hystrixCommandConfig;
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
