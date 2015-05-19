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
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionSignatureData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


public class SubscriptionSignatureResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final SubscriptionSignatureData data = new SubscriptionSignatureData();
		data.setRecurringSubscriptionInfoAmount(getBigDecimalForString(source.get("recurringSubscriptionInfo_amount")));
		data.setRecurringSubscriptionInfoAmountPublicSignature(source.get("recurringSubscriptionInfo_amountPublicSignature"));
		data.setRecurringSubscriptionInfoAutomaticRenew(Boolean.valueOf(source.get("recurringSubscriptionInfo_automaticRenew")));
		data.setRecurringSubscriptionInfoAutomaticRenewPublicSignature(source
				.get("recurringSubscriptionInfo_automaticRenewPublicSignature"));
		data.setRecurringSubscriptionInfoFrequency(source.get("recurringSubscriptionInfo_frequency"));
		data.setRecurringSubscriptionInfoFrequencyPublicSignature(source.get("recurringSubscriptionInfo_frequencyPublicSignature"));
		data.setRecurringSubscriptionInfoNumberOfPayments(getIntegerForString(source
				.get("recurringSubscriptionInfo_numberOfPayments")));
		data.setRecurringSubscriptionInfoNumberOfPaymentsPublicSignature(source
				.get("recurringSubscriptionInfo_numberOfPaymentsPublicSignature"));
		data.setRecurringSubscriptionInfoStartDate(source.get("recurringSubscriptionInfo_startDate"));
		data.setRecurringSubscriptionInfoStartDatePublicSignature(source.get("recurringSubscriptionInfo_startDatePublicSignature"));

		target.setSubscriptionSignatureData(data);
	}
}
