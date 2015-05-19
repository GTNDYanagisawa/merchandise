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
package de.hybris.platform.commercefacades.address.converters.populator;

import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


@UnitTest
public class SingleLineAddressFormatPopulatorTest
{

	private SingleLineAddressFormatPopulator singleLineAddressFormatPopulator;
	private AddressModel addressModel;

	@Before
	public void setup()
	{
		final RegionModel regionModel = Mockito.mock(RegionModel.class);
		BDDMockito.given(regionModel.getName()).willReturn("region");

		final CountryModel countryModel = Mockito.mock(CountryModel.class);
		BDDMockito.given(countryModel.getName()).willReturn("country");

		addressModel = new AddressModel();
		addressModel.setTown("town");
		addressModel.setRegion(regionModel);
		addressModel.setPostalcode("postalCode");
		addressModel.setCountry(countryModel);

		singleLineAddressFormatPopulator = new SingleLineAddressFormatPopulator();
	}

	@Test
	public void testBuildSingleLineAddress()
	{
		final StringBuilder builder = new StringBuilder();
		final List<String> commands = new LinkedList<String>();
		commands.add("town");
		commands.add("region.name");
		commands.add("postalcode");
		commands.add("country.name");

		singleLineAddressFormatPopulator.setAddressFormatList(commands);
		singleLineAddressFormatPopulator.populate(addressModel, builder);

		assertTrue("town, region, postalCode, country".equals(builder.toString()));
	}

}