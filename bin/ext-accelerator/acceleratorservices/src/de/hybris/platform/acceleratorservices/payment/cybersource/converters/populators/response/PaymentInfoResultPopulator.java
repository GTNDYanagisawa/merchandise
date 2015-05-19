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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


public class PaymentInfoResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final PaymentInfoData data = new PaymentInfoData();
		data.setCardAccountNumber(source.get("card_accountNumber"));
		data.setCardCardType(source.get("card_cardType"));
		data.setCardAccountHolderName(source.get("card_nameOnCard"));
		data.setCardExpirationMonth(getIntegerForString(source.get("card_expirationMonth")));
		data.setCardExpirationYear(getIntegerForString(source.get("card_expirationYear")));
		data.setCardStartMonth(source.get("card_startMonth"));
		data.setCardStartYear(source.get("card_startYear"));
		data.setPaymentOption(source.get("paymentOption"));

		target.setPaymentInfoData(data);
	}
}
