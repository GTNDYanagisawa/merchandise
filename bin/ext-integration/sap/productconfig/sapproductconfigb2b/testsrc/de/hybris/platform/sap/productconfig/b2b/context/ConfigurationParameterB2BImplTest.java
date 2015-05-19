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

package de.hybris.platform.sap.productconfig.b2b.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationParameterB2BImplTest
{
	private static final String COUNTRY_SAP_CODE_EXAMPLE = "US";
	private static final String PRICE_GROUP_EXAMPLE = "XX";
	private static final String CUSTOMER_NUMBER_EXAMPLE = "1234567890";

	@Mock
	protected B2BCustomerService b2bCustomerService;

	@Mock
	protected B2BUnitService b2bUnitService;

	@Mock
	protected UserPriceGroup userPriceGroup;

	private ConfigurationParameterB2BImpl configurationParameter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		when(b2bCustomerService.getCurrentB2BCustomer()).thenReturn(b2bCustomer);


		when(userPriceGroup.getCode()).thenReturn(PRICE_GROUP_EXAMPLE);

		final B2BUnitModel b2bUnit = new B2BUnitModel();

		final CountryModel countryModel = new CountryModel();
		countryModel.setSapCode(COUNTRY_SAP_CODE_EXAMPLE);
		b2bUnit.setCountry(countryModel);
		b2bUnit.setUserPriceGroup(userPriceGroup);

		when(b2bUnitService.getParent(b2bCustomer)).thenReturn(b2bUnit);

		final B2BUnitModel rootUnit = new B2BUnitModel();
		rootUnit.setUid(CUSTOMER_NUMBER_EXAMPLE);
		when(b2bUnitService.getRootUnit(b2bUnit)).thenReturn(rootUnit);

		configurationParameter = new ConfigurationParameterB2BImpl();
		configurationParameter.setB2bCustomerService(b2bCustomerService);
		configurationParameter.setB2bUnitService(b2bUnitService);
	}

	@Test
	public void testRetrieveB2BUnitModel()
	{
		final B2BUnitModel unitModel = configurationParameter.retrieveB2BUnitModel();
		assertNotNull(unitModel);
	}

	@Test
	public void testGetCountrySapCode()
	{
		final String countrySapCode = configurationParameter.getCountrySapCode();
		assertEquals(COUNTRY_SAP_CODE_EXAMPLE, countrySapCode);
	}

	@Test
	public void testGetCustomerPriceGroup()
	{
		final String priceGroup = configurationParameter.getCustomerPriceGroup();
		assertEquals(PRICE_GROUP_EXAMPLE, priceGroup);
	}

	@Test
	public void testGetCustomerNumber()
	{
		final String customerNumber = configurationParameter.getCustomerNumber();
		assertEquals(CUSTOMER_NUMBER_EXAMPLE, customerNumber);
	}
}
