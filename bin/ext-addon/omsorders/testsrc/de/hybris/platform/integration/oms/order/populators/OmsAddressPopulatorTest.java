/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package de.hybris.platform.integration.oms.order.populators;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.oms.domain.address.Address;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OmsAddressPopulatorTest
{
	@Mock
	private CustomerNameStrategy customerNameStrategy;

	private OmsAddressPopulator omsAddressPopulator;

	@Before
	public void beforeTest() throws Exception
	{
		MockitoAnnotations.initMocks(this.getClass());
		omsAddressPopulator = new OmsAddressPopulator();
		omsAddressPopulator.setCustomerNameStrategy(customerNameStrategy);
	}

	@Test
	public void testOmsAddressPopulator() throws Exception
	{
		final Address omsAddress = new Address();
		final AddressModel addressModel = Mockito.mock(AddressModel.class);
		final CountryModel countryModel = Mockito.mock(CountryModel.class);

		BDDMockito.when(addressModel.getCountry()).thenReturn(countryModel);
		BDDMockito.when(countryModel.getName()).thenReturn("Syria");
		BDDMockito.when(addressModel.getTown()).thenReturn("Latakia");

		omsAddressPopulator.populate(addressModel, omsAddress);

		Assert.assertEquals(omsAddress.getCountryName(), addressModel.getCountry().getName());
		Assert.assertEquals(omsAddress.getCityName(), addressModel.getTown());
	}


}
