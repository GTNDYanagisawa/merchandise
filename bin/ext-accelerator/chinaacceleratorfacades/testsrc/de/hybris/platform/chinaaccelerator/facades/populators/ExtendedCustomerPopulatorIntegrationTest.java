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

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 * ExtendedCustomerPopulatorIntegrationTest
 */
public class ExtendedCustomerPopulatorIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource(name = "extendedChinaCustomerPopulator")
	private ExtendedCustomerPopulator extendedCustomerPopulator;

	private final String DUMMY = "dummy";


	@Test
	public void testChinaCustomerPopulator()
	{
		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		final CustomerData exCustomerData = new CustomerData();

		customerModel.setMobileNumber(DUMMY);

		extendedCustomerPopulator.populate(customerModel, exCustomerData);

		Assert.assertEquals(customerModel.getMobileNumber(), exCustomerData.getMobileNumber());
	}

}
