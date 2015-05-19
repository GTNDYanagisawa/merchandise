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
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionSignatureData;
import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;



public class SubscriptionSignatureRequestPopulator extends AbstractRequestPopulator<CreateSubscriptionRequest, PaymentData>
{
	private static final Logger LOG = Logger.getLogger(SubscriptionSignatureRequestPopulator.class);
	private AcceleratorDigestUtils digestUtils;

	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CreateSubscriptionRequest] source cannot be null");
		validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

		final SubscriptionSignatureData subscriptionSignatureData = source.getSubscriptionSignatureData();
		Assert.notNull(subscriptionSignatureData, "subscriptionSignatureData cannot be null");

		final String subscriptionAmount = String.valueOf(subscriptionSignatureData.getRecurringSubscriptionInfoAmount());
		final String subscriptionStartDate = subscriptionSignatureData.getRecurringSubscriptionInfoStartDate();
		final String subscriptionFrequency = subscriptionSignatureData.getRecurringSubscriptionInfoFrequency();
		final String subscriptionNumberOfPayments = String.valueOf(subscriptionSignatureData
				.getRecurringSubscriptionInfoNumberOfPayments());
		final String subscriptionAutomaticRenew = String.valueOf(subscriptionSignatureData
				.getRecurringSubscriptionInfoAutomaticRenew());

		Assert.notNull(subscriptionAmount, "RecurringSubscriptionInfo_amount cannot be null");
		Assert.notNull(subscriptionStartDate, "RecurringSubscriptionInfo_startDate cannot be null");
		Assert.notNull(subscriptionFrequency, "RecurringSubscriptionInfo_frequency cannot be null");
		Assert.notNull(subscriptionNumberOfPayments, "subscriptionNumberOfPayments cannot be null");
		Assert.notNull(subscriptionAutomaticRenew, "subscriptionAutomaticRenew cannot be null");

		// To make sure your transactions are processed correctly, insert the signature data in this order:
		// amount, start date, frequency, number of payments, and automatic renewal.
		// The transaction will be declined if any of the data in the signature does not match the data in the order.
		final String data = subscriptionAmount + subscriptionStartDate + subscriptionFrequency + subscriptionNumberOfPayments
				+ subscriptionAutomaticRenew;

		try
		{
			addRequestQueryParam(target, "recurringSubscriptionInfo_amount", subscriptionAmount);
			addRequestQueryParam(target, "recurringSubscriptionInfo_startDate", subscriptionStartDate);
			addRequestQueryParam(target, "recurringSubscriptionInfo_frequency", subscriptionFrequency);
			addRequestQueryParam(target, "recurringSubscriptionInfo_numberOfPayments", subscriptionNumberOfPayments);
			addRequestQueryParam(target, "recurringSubscriptionInfo_automaticRenew", subscriptionAutomaticRenew);
			addRequestQueryParam(target, "recurringSubscriptionInfo_signaturePublic",
					getDigestUtils().getPublicDigest(data, subscriptionSignatureData.getSharedSecret()));
		}
		catch (final Exception e)
		{
			LOG.error("Error inserting CyberSource Hosted Order Page subscription signature", e);
			throw new ConversionException("Error inserting CyberSource Hosted Order Page subscription signature", e);
		}
	}

	protected AcceleratorDigestUtils getDigestUtils()
	{
		return digestUtils;
	}

	@Required
	public void setDigestUtils(final AcceleratorDigestUtils digestUtils)
	{
		this.digestUtils = digestUtils;
	}
}
