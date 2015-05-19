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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator implementation for {@link CisAddress} as source and {@link AddressModel} as target type.
 */
public class CisAvsReverseAddressPopulator implements Populator<CisAddress, AddressModel>
{
	private static final Logger LOG = Logger.getLogger(CisAvsReverseAddressPopulator.class);
	private static final int REGION_ISOCODE_LENGTH = 2;
	private CommonI18NService i18nService;

	@Override
	public void populate(final CisAddress source, final AddressModel target) throws ConversionException
	{
		if (source != null)
		{
			target.setCompany(source.getCompany());
			target.setFirstname(source.getFirstName());
			target.setLastname(source.getLastName());

			// set streets
			target.setLine1(source.getAddressLine1());
			target.setLine2(source.getAddressLine2());
			target.setTown(source.getCity());
			target.setPostalcode(source.getZipCode());

			final CountryModel countryModel = getI18nService().getCountry(source.getCountry());
			target.setCountry(countryModel);

			if (source.getState() != null && source.getState().length() == REGION_ISOCODE_LENGTH)
			{
				try
				{
					final RegionModel regionModel = getI18nService().getRegion(countryModel,
							source.getCountry() + "-" + source.getState());
					target.setRegion(regionModel);
				}
				catch (final UnknownIdentifierException e)
				{
					LOG.warn("Region model with isocode, " + source.getCountry() + "-" + source.getState()
							+ ", is undefined.  It may not be supported by CIS.");
				}
			}
		}
	}

	protected CommonI18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final CommonI18NService i18nService)
	{
		this.i18nService = i18nService;
	}
}
