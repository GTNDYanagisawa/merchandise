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
package de.hybris.platform.chinaaccelerator.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.commercefacades.user.converters.populator.CustomerPopulatorTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * ExtendedCustomerPopulatorUnitTest
 */
public class ExtendedCustomerPopulatorUnitTest extends CustomerPopulatorTest
{

	private AbstractConverter<UserModel, CustomerData> extendedCustomerConverter;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		final ExtendedCustomerPopulator cnCustomerPopulator = new ExtendedCustomerPopulator();
		extendedCustomerConverter = new ConverterFactory<UserModel, CustomerData, ExtendedCustomerPopulator>().create(
				CustomerData.class, cnCustomerPopulator);
	}

	@Test
	public void testPopulate()
	{
		final CustomerModel userModel = mock(CustomerModel.class);

		given(userModel.getMobileNumber()).willReturn("mobileNumber");

		final CustomerData customerData = extendedCustomerConverter.convert(userModel);

		Assert.assertNotNull(customerData);

		Assert.assertEquals("mobileNumber", customerData.getMobileNumber());
	}

}
