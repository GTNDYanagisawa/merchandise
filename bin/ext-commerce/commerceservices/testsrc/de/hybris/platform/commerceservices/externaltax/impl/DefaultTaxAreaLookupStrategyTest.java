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
package de.hybris.platform.commerceservices.externaltax.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * 
 *
 */
@UnitTest
public class DefaultTaxAreaLookupStrategyTest
{
	private DefaultTaxAreaLookupStrategy defaultTaxAreaLookupStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultTaxAreaLookupStrategy = new DefaultTaxAreaLookupStrategy();
	}

	@Test
	public void shouldGetTaxArea()
	{
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class);
		final AddressModel deliveryAddress = Mockito.mock(AddressModel.class);
		final CountryModel country = Mockito.mock(CountryModel.class);
		given(country.getIsocode()).willReturn("US");
		given(deliveryAddress.getCountry()).willReturn(country);
		given(abstractOrder.getDeliveryAddress()).willReturn(deliveryAddress);
		final String taxArea = defaultTaxAreaLookupStrategy.getTaxAreaForOrder(abstractOrder);

		Assert.assertNotNull(taxArea);
		Assert.assertEquals("US", taxArea);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetTaxArea()
	{
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class);
		defaultTaxAreaLookupStrategy.getTaxAreaForOrder(abstractOrder);
	}
}
