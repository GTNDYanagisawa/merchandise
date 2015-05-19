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
package de.hybris.platform.chinaaccelerator.services.daos.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinaaccelerator.services.customer.daos.CustomerDao;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * DefaultCustomerDaoTest
 */
@IntegrationTest
public class DefaultCustomerDaoTest extends ServicelayerTransactionalTest
{
	private static final String TEST_CUSTOMER_UID = "test_customer_uid";
	private static final String TEST_CUSTOMER_MOBILENUMBER = "13800138000";
	private static final String TEST_CUSTOMER_MOBILENUMBER_1 = "13800138001";

	@Resource
	private CustomerDao customerDao;
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;

	private CustomerModel customerModel;

	@Before
	public void setUp()
	{
		customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(TEST_CUSTOMER_UID);
		customerModel.setName("abc");
		customerModel.setMobileNumber(TEST_CUSTOMER_MOBILENUMBER);
		modelService.save(customerModel);
	}

	@Test
	public void testFindCustomerByMobileNumber()
	{
		final List<CustomerModel> result = customerDao.findCustomerByMobileNumber(TEST_CUSTOMER_MOBILENUMBER);
		assertNotNull("findCustomerByMobileNumber returned not null", result);
		assertEquals("result is not empty", result.isEmpty(), false);
		assertEquals("result count diff", result.size(), 1);
		final CustomerModel other = result.get(0);
		assertEquals("mobile No differ", other.getMobileNumber(), TEST_CUSTOMER_MOBILENUMBER);
		assertEquals("customer model diff", other, customerModel);
	}

	@Test
	public void testFindCustomerByMobileNumberWithWrongNo()
	{
		final List<CustomerModel> result = customerDao.findCustomerByMobileNumber(TEST_CUSTOMER_MOBILENUMBER_1);
		assertNotNull("findCustomerByMobileNumber returned not null", result);
		assertEquals("result is empty", result.isEmpty(), true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindCustomerByMobileNumberNull()
	{
		customerDao.findCustomerByMobileNumber(null);
	}
}
