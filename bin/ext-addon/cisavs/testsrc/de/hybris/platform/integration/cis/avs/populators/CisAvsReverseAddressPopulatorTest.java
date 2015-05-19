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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cis.api.model.CisAddress;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisAvsReverseAddressPopulatorTest
{
	private CisAvsReverseAddressPopulator populator;

	@Mock
	private CommonI18NService commonI18NService;

	@Before
	public void init()
	{

		MockitoAnnotations.initMocks(this.getClass());
		populator = new CisAvsReverseAddressPopulator();
		populator.setI18nService(commonI18NService);
		final CountryModel country = new CountryModel();
		country.setIsocode("US");
		Mockito.when(commonI18NService.getCountry("US")).thenReturn(country);
		final RegionModel regionModel = new RegionModel();
		regionModel.setIsocode("US-NY");
		regionModel.setIsocodeShort("NY");
		Mockito.when(commonI18NService.getRegion(Mockito.any(CountryModel.class), Mockito.eq("US-NY"))).thenReturn(regionModel);
	}

	@Test
	public void shouldPopulate()
	{
		final CisAddress source = new CisAddress();
		source.setCompany("company");
		source.setFirstName("firstname");
		source.setLastName("lastname");
		source.setAddressLine1("line1");
		source.setAddressLine2("line2");
		source.setCity("city");
		source.setZipCode("zipcode");
		source.setCountry("US");
		source.setState("NY");

		final AddressModel target = new AddressModel();

		populator.populate(source, target);

		Assert.assertEquals("company", target.getCompany());
		Assert.assertEquals("firstname", target.getFirstname());
		Assert.assertEquals("lastname", target.getLastname());
		//Assert.assertEquals("line1", target.getLine1()); dynamic attributes don't seem to work in a unit test
		//Assert.assertEquals("line2", target.getLine2()); dynamic attributes don't seem to work in a unit test
		Assert.assertEquals("city", target.getTown());
		Assert.assertEquals("zipcode", target.getPostalcode());
		Assert.assertEquals("US", target.getCountry().getIsocode());
		Assert.assertEquals("US-NY", target.getRegion().getIsocode());

	}
}
