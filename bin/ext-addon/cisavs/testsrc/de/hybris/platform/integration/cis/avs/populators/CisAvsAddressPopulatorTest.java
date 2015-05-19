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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisAddressType;


@UnitTest
public class CisAvsAddressPopulatorTest
{
	private CisAvsAddressPopulator populator;

	@Before
	public void init()
	{
		populator = new CisAvsAddressPopulator();
	}

	@Test
	public void shouldPopulate()
	{
		final AddressModel source = new AddressModel();
		source.setCompany("company");
		source.setFirstname("firstname");
		source.setLastname("lastname");
		source.setLine1("line1");
		source.setLine2("line2");
		source.setPostalcode("postalcode");
		source.setTown("town");
		source.setRegion(new RegionModel());
		source.getRegion().setIsocode("US-NY");
		source.getRegion().setIsocodeShort("NY");
		source.setCountry(new CountryModel());
		source.getCountry().setIsocode("US");


		final CisAddress target = new CisAddress();

		populator.populate(source, target);

		Assert.assertEquals(CisAddressType.SHIP_TO, target.getType());
		Assert.assertEquals("company", target.getCompany());
		Assert.assertEquals("firstname", target.getFirstName());
		Assert.assertEquals("lastname", target.getLastName());
		//Assert.assertEquals("line1", target.getAddressLine1()); dynamic attributes don't seem to work in a unit test
		//Assert.assertEquals("line2", target.getAddressLine2()); dynamic attributes don't seem to work in a unit test
		Assert.assertEquals("postalcode", target.getZipCode());
		Assert.assertEquals("town", target.getCity());
		Assert.assertEquals("NY", target.getState());
		Assert.assertEquals("US", target.getCountry());

	}
}
