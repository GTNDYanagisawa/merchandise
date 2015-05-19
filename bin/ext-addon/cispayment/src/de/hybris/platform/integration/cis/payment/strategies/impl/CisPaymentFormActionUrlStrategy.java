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

import de.hybris.platform.acceleratorservices.payment.strategies.PaymentFormActionUrlStrategy;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.client.rest.payment.PaymentClient;
import com.hybris.commons.client.RestResponse;


/**
 * Strategy for retrieving the post URL for payment forms using CIS web service.
 */
public class CisPaymentFormActionUrlStrategy implements PaymentFormActionUrlStrategy
{
	private PaymentClient paymentClient;
	private OndemandHystrixCommandConfiguration hystrixCommandConfig;
	private PaymentFormActionUrlStrategy paymentFormActionUrlStrategy;
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;

	@Override
	public String getHopRequestUrl()
	{
		return paymentFormActionUrlStrategy.getHopRequestUrl();
	}

	@Override
	public String getSopRequestUrl(final String clientRef)
	{
		return getOndemandHystrixCommandFactory().newCommand(getHystrixCommandConfig(), new HystrixExecutable<String>()
		{
			@Override
			public String runEvent()
			{
				final RestResponse<Void> response = getPaymentClient().pspUrl(clientRef);
				return String.valueOf(response.getLocation());
			}

			@Override
			public String fallbackEvent()
			{
				return null;
			}

			@Override
			public String defaultEvent()
			{
				return getPaymentFormActionUrlStrategy().getSopRequestUrl(clientRef);
			}
		}).execute();
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

	public OndemandHystrixCommandConfiguration getHystrixCommandConfig()
	{
		return hystrixCommandConfig;
	}

	@Required
	public void setHystrixCommandConfig(final OndemandHystrixCommandConfiguration hystrixCommandConfig)
	{
		this.hystrixCommandConfig = hystrixCommandConfig;
	}

	protected PaymentFormActionUrlStrategy getPaymentFormActionUrlStrategy()
	{
		return paymentFormActionUrlStrategy;
	}

	@Required
	public void setPaymentFormActionUrlStrategy(final PaymentFormActionUrlStrategy paymentFormActionUrlStrategy)
	{
		this.paymentFormActionUrlStrategy = paymentFormActionUrlStrategy;
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
