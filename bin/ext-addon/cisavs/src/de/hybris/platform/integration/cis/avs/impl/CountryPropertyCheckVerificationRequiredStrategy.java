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
package de.hybris.platform.integration.cis.avs.impl;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.constants.CisavsConstants;
import de.hybris.platform.integration.cis.avs.strategies.CheckVerificationRequiredStrategy;
import de.hybris.platform.util.Config;
import org.springframework.util.StringUtils;


/**
 * Implementation of {@link CheckVerificationRequiredStrategy} using the property cisavs.check.countries to get the
 * countries for which we should call the address validation service.
 */
public class CountryPropertyCheckVerificationRequiredStrategy implements CheckVerificationRequiredStrategy
{
	@Override
	public boolean isVerificationRequired(final AddressModel address)
	{
		final String checkCountriesProperty = Config.getParameter(CisavsConstants.AVS_COUNTRIES_PROP);

		final String[] checkCountries = StringUtils.commaDelimitedListToStringArray(checkCountriesProperty);

		for (final String country : checkCountries)
		{
			if (address.getCountry() != null && country.equals(address.getCountry().getIsocode()))
			{
				return true;
			}
		}
		return false;
	}
}
