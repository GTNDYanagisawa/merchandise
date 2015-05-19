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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.avs.constants.CisavsConstants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Config;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;


/**
 * @author florent
 */
@IntegrationTest
public class CountryPropertyCheckVerificationRequiredStrategyTest extends ServicelayerTest
{
	private final CountryPropertyCheckVerificationRequiredStrategy strategy = new CountryPropertyCheckVerificationRequiredStrategy();

	@Before
	public void setProperty()
	{
		Config.setParameter(CisavsConstants.AVS_COUNTRIES_PROP, "US");
	}

	@Test
	public void shouldValidateForUS()
	{
		final CountryModel country = new CountryModel();
		country.setIsocode("US");
		final AddressModel address = new AddressModel();
		address.setCountry(country);
		final boolean verificationRequired = strategy.isVerificationRequired(address);
		Assert.assertTrue(verificationRequired);
	}

	@Test
	public void shouldValidateForCanada()
	{
		final CountryModel country = new CountryModel();
		country.setIsocode("CA");
		final AddressModel address = new AddressModel();
		address.setCountry(country);
		final boolean verificationRequired = strategy.isVerificationRequired(address);
		Assert.assertFalse(verificationRequired);
	}
}
