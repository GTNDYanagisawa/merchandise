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

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisAddressType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


public class CisAvsAddressPopulator implements Populator<AddressModel, CisAddress>
{
	@Override
	public void populate(final AddressModel source, final CisAddress target) throws ConversionException
	{
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source != null)
		{
			target.setType(CisAddressType.SHIP_TO);
			target.setCompany(source.getCompany());
			target.setFirstName(source.getFirstname());
			target.setLastName(source.getLastname());
			target.setAddressLine1(source.getLine1());
			target.setAddressLine2(source.getLine2());
			target.setZipCode(source.getPostalcode());
			target.setCity(source.getTown());
			target.setState(getRegionIsoCode(source));
			target.setCountry(source.getCountry() == null ? null : source.getCountry().getIsocode());
		}
	}

	protected String getRegionIsoCode(final AddressModel source)
	{
		if (source.getRegion() != null && StringUtils.isNotEmpty(source.getRegion().getIsocode()))
		{
			String regionIsocode = source.getRegion().getIsocode();
			if (regionIsocode.startsWith(source.getCountry().getIsocode())) //CIS needs only second part of region isocode
			{
				regionIsocode = regionIsocode.substring(source.getCountry().getIsocode().length() + "-".length());
			}
			return regionIsocode;
		}
		return null;
	}
}
