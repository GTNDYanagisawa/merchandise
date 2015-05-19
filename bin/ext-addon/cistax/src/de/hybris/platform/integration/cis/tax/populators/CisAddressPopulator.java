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
package de.hybris.platform.integration.cis.tax.populators;

import com.hybris.cis.api.model.CisAddress;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


public class CisAddressPopulator implements Populator<AddressModel, CisAddress>
{
	@Override
	public void populate(final AddressModel source, final CisAddress target) throws ConversionException
	{
		if (source == null || target == null)
		{
			throw new ConversionException(String.format("Missing AddressModel source %s or CisAddress target %s ", source, target));
		}
		target.setAddressLine1(source.getLine1());
		target.setAddressLine2(source.getLine2());
		target.setZipCode(source.getPostalcode());
		target.setCity(source.getTown());
		if (source.getCountry() != null)
		{
			target.setCountry(source.getCountry().getIsocode());
		}
		if (source.getRegion() != null)
		{
			target.setState(source.getRegion().getIsocodeShort());
		}
	}
}
