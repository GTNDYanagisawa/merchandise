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
import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class CustomerInfoResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final CustomerInfoData data = new CustomerInfoData();
		data.setBillToCity(source.get("billTo_city"));
		data.setBillToCompany(source.get("billTo_company"));
		data.setBillToCompanyTaxId(source.get("billTo_companyTaxID"));

		final String countryIso = source.get("billTo_country");
		if (StringUtils.isNotBlank(countryIso))
		{
			data.setBillToCountry(countryIso.toUpperCase(Locale.getDefault()));
		}
		data.setBillToCustomerIdRef(source.get("billTo_customerID"));
		data.setBillToDateOfBirth(source.get("billTo_dateOfBirth"));
		data.setBillToEmail(source.get("billTo_email"));
		data.setBillToFirstName(source.get("billTo_firstName"));
		data.setBillToLastName(source.get("billTo_lastName"));
		data.setBillToPhoneNumber(source.get("billTo_phoneNumber"));
		data.setBillToPostalCode(source.get("billTo_postalCode"));
		data.setBillToState(source.get("billTo_state"));
		data.setBillToStreet1(source.get("billTo_street1"));
		data.setBillToStreet2(source.get("billTo_street2"));

		target.setCustomerInfoData(data);
	}
}
