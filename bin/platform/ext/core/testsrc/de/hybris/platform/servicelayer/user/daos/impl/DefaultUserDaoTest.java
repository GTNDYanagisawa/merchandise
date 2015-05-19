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
 * 
 *  
 */
package de.hybris.platform.servicelayer.user.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Tests the implementation of the UserDao.
 */
@IntegrationTest
public class DefaultUserDaoTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private DefaultUserDao userDao;

	@Test
	public void testFoundNothing()
	{
		assertNull("does not match expected result", userDao.findUserByUID("xxx"));
	}

	@Test
	public void testFoundOneUser()
	{
		final UserModel user = userDao.findUserByUID("anonymous");
		assertNotNull("does not match expected result", user);
		assertEquals("anonymous is not a customer", CustomerModel.class, user.getClass());
	}
}
