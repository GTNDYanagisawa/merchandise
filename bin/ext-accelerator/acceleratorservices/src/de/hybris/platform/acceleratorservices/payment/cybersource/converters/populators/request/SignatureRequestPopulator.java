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
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


public class SignatureRequestPopulator extends AbstractRequestPopulator<CreateSubscriptionRequest, PaymentData>
{
	private static final Logger LOG = Logger.getLogger(SignatureRequestPopulator.class);
	private AcceleratorDigestUtils digestUtils;

	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CreateSubscriptionRequest] source cannot be null");
		validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

		final SignatureData signatureData = source.getSignatureData();
		Assert.notNull(signatureData, "signatureData cannot be null");

		final String time = String.valueOf(System.currentTimeMillis());
		final String merchantId = signatureData.getMerchantID();
		final String currency = signatureData.getCurrency();
		final String amount = String.valueOf(signatureData.getAmount());

		validateParameterNotNull(merchantId, "Merchant ID cannot be null");
		validateParameterNotNull(amount, "Amount cannot be null");
		validateParameterNotNull(currency, "Currency cannot be null");

		// To make sure your transactions are processed correctly, insert the signature data in this order:
		// merchantID, amount, currency, orderPage_timestamp.
		// The transaction will be declined if any of the data in the signature does not match the data in the order.
		final String data = merchantId + amount + currency + time;

		try
		{
			addRequestQueryParam(target, "merchantID", merchantId);
			addRequestQueryParam(target, "amount", amount);
			addRequestQueryParam(target, "currency", currency);
			addRequestQueryParam(target, "orderPage_serialNumber", signatureData.getOrderPageSerialNumber());
			addRequestQueryParam(target, "orderPage_version", signatureData.getOrderPageVersion());
			addRequestQueryParam(target, "orderPage_signaturePublic",
					getDigestUtils().getPublicDigest(data, signatureData.getSharedSecret()));
			addRequestQueryParam(target, "orderPage_timestamp", time);
		}
		catch (final Exception e)
		{
			LOG.error("Error inserting CyberSource Hosted Order Page signature", e);
			throw new ConversionException("Error inserting CyberSource Hosted Order Page signature", e);
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
