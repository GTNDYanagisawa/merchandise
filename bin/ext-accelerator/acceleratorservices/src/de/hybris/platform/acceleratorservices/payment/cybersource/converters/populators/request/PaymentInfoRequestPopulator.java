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
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


public class PaymentInfoRequestPopulator extends AbstractRequestPopulator<CreateSubscriptionRequest, PaymentData>
{
	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CreateSubscriptionRequest] source cannot be null");
		validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

		final PaymentInfoData paymentInfoData = source.getPaymentInfoData();
		Assert.notNull(paymentInfoData, "[PaymentInfoData] source cannot be null");

		addRequestQueryParam(target, "card_accountNumber", paymentInfoData.getCardAccountNumber());
		addRequestQueryParam(target, "card_cardType", paymentInfoData.getCardCardType());
		addRequestQueryParam(target, "card_cvNumber", paymentInfoData.getCardCvNumber());

		if (paymentInfoData.getCardExpirationMonth() != null && paymentInfoData.getCardExpirationMonth().intValue() > 0)
		{
			addRequestQueryParam(target, "card_expirationMonth", String.valueOf(paymentInfoData.getCardExpirationMonth()));
		}
		if (paymentInfoData.getCardExpirationYear() != null && paymentInfoData.getCardExpirationYear().intValue() > 0)
		{
			addRequestQueryParam(target, "card_expirationYear", String.valueOf(paymentInfoData.getCardExpirationYear()));
		}
		addRequestQueryParam(target, "card_issueNumber", paymentInfoData.getCardIssueNumber());
		addRequestQueryParam(target, "card_startMonth", paymentInfoData.getCardStartMonth());
		addRequestQueryParam(target, "card_startYear", paymentInfoData.getCardStartYear());
		addRequestQueryParam(target, "paymentOption", paymentInfoData.getPaymentOption());
	}
}
