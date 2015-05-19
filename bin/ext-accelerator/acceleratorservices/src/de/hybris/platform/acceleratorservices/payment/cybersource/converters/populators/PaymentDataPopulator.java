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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators;

import de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response.AbstractResultPopulator;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;

import org.springframework.util.Assert;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


public class PaymentDataPopulator extends AbstractResultPopulator<CreateSubscriptionRequest, PaymentData>
{
	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target)
	{
		//Validate parameters and related data
		validateParameterNotNull(source, "Parameter source (CreateSubscriptionRequest) cannot be null");
		validateParameterNotNull(target, "Parameter target (PaymentData) cannot be null");
		Assert.isInstanceOf(CreateSubscriptionRequest.class, source);
		Assert.notNull(source.getCustomerBillToData(), "customerBillToData cannot be null");
		Assert.notNull(source.getCustomerShipToData(), "customerShipToData cannot be null");
		Assert.notNull(source.getOrderInfoData(), "orderInfoData cannot be null");
		Assert.notNull(source.getPaymentInfoData(), "paymentInfoData cannot be null");
		Assert.notNull(source.getSignatureData(), "signatureData cannot be null");
		Assert.notNull(source.getSubscriptionSignatureData(), "subscriptionSignatureData cannot be null");

		target.setPostUrl(source.getRequestUrl());
	}
}
