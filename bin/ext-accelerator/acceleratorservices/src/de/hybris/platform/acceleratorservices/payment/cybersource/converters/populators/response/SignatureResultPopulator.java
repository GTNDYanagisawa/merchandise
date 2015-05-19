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
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


public class SignatureResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final SignatureData data = new SignatureData();
		data.setAmount(getBigDecimalForString(source.get("orderAmount")));
		data.setAmountPublicSignature(source.get("orderAmount_publicSignature"));
		data.setCurrency(source.get("orderCurrency"));
		data.setCurrencyPublicSignature(source.get("orderCurrency_publicSignature"));
		data.setMerchantID(source.get("merchantID"));
		data.setOrderPageSerialNumber(source.get("orderPage_serialNumber"));
		data.setOrderPageVersion(source.get("orderPage_version"));
		data.setSignedFields(source.get("signedFields"));
		data.setTransactionSignature(source.get("transactionSignature"));

		target.setSignatureData(data);
	}
}
