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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


public class OrderInfoRequestPopulator extends AbstractRequestPopulator<CreateSubscriptionRequest, PaymentData>
{

	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CreateSubscriptionRequest] source cannot be null");
		validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

		final OrderInfoData orderInfoData = source.getOrderInfoData();
		Assert.notNull(orderInfoData, "[OrderInfoData] cannot be null");

		addRequestQueryParam(target, "comments", orderInfoData.getComments());
		addRequestQueryParam(target, "orderNumber", orderInfoData.getOrderNumber());
		addRequestQueryParam(target, "orderPage_ignoreAVS", String.valueOf(orderInfoData.getOrderPageIgnoreAVS()));
		addRequestQueryParam(target, "orderPage_ignoreCVN", String.valueOf(orderInfoData.getOrderPageIgnoreCVN()));
		addRequestQueryParam(target, "orderPage_requestToken", orderInfoData.getOrderPageRequestToken());
		addRequestQueryParam(target, "orderPage_transactionType", orderInfoData.getOrderPageTransactionType());
		addRequestQueryParam(target, "recurringSubscriptionInfo_publicSignature",
				orderInfoData.getRecurringSubscriptionInfoPublicSignature());
		addRequestQueryParam(target, "subscription_title", orderInfoData.getSubscriptionTitle());
		addRequestQueryParam(target, "taxAmount", orderInfoData.getTaxAmount());
	}
}
