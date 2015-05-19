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
package de.hybris.platform.integration.cis.avs.populators;

import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections.CollectionUtils;


public class CisAvsAddressMatchingPopulator
		implements
		Populator<AddressModel, AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>>>
{
	@Override
	public void populate(
			final AddressModel source,
			final AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> target)
			throws ConversionException
	{
		if (!CollectionUtils.isEmpty(target.getSuggestedAddresses()))
		{
			for (final AddressModel addy : target.getSuggestedAddresses())
			{
				addy.setTitle(source.getTitle());
				addy.setFirstname(source.getFirstname());
				addy.setLastname(source.getLastname());
				addy.setCompany(source.getCompany());
				addy.setCellphone(source.getCellphone());
				addy.setEmail(source.getEmail());
			}
		}

	}
}
