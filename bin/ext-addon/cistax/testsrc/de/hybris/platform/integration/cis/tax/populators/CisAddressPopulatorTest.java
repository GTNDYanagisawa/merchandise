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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cis.api.model.CisAddress;


/**
 * 
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisAddressPopulatorTest
{
	private CisAddressPopulator cisAddressPopulator;

	@Before
	public void setUp()
	{
		cisAddressPopulator = new CisAddressPopulator();
	}

	@Test
	public void shouldPopulateAddress()
	{
		final CisAddress cisAddress = new CisAddress();
		final AddressModel addressModel = Mockito.mock(AddressModel.class);
		final CountryModel countryModel = Mockito.mock(CountryModel.class);
		final RegionModel regionModel = Mockito.mock(RegionModel.class);

		given(regionModel.getIsocodeShort()).willReturn("NY");
		given(countryModel.getIsocode()).willReturn("US");
		given(addressModel.getCountry()).willReturn(countryModel);
		given(addressModel.getTown()).willReturn("New York");
		given(addressModel.getLine1()).willReturn("1700 Broadway");
		given(addressModel.getRegion()).willReturn(regionModel);
		given(addressModel.getPostalcode()).willReturn("10119");

		cisAddressPopulator.populate(addressModel, cisAddress);

		Assert.assertEquals("NY", cisAddress.getState());
		Assert.assertEquals("US", cisAddress.getCountry());
		Assert.assertEquals("New York", cisAddress.getCity());
		Assert.assertEquals("1700 Broadway", cisAddress.getAddressLine1());
		Assert.assertEquals("10119", cisAddress.getZipCode());
	}
}
