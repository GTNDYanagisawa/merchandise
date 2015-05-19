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
package de.hybris.platform.acceleratorservices.email.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.email.dao.EmailAddressDao;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 */
@IntegrationTest
public class DefaultEmailAddressDaoTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private EmailAddressDao emailAddressDao;

	private EmailAddressModel emailId1;
	private EmailAddressModel emailId2;
	private EmailAddressModel emailId3;

	@Before
	public void setUp()
	{
		emailId1 = modelService.create(EmailAddressModel.class);
		emailId1.setEmailAddress("1@1.com");
		emailId1.setDisplayName("1 1");
		modelService.save(emailId1);
		emailId2 = modelService.create(EmailAddressModel.class);
		emailId2.setEmailAddress("2@2.com");
		emailId2.setDisplayName(" ");
		modelService.save(emailId2);
		emailId3 = modelService.create(EmailAddressModel.class);
		emailId3.setEmailAddress("3@3.com");
		emailId3.setDisplayName("3 3");
		modelService.save(emailId3);
	}

	@Test
	public void testFindEmailAddressByEmailIdWithEmptyDisplay()
	{
		final EmailAddressModel email = emailAddressDao.findEmailAddressByEmailAndDisplayName("2@2.com", " ");
		assertNotNull(email);
		assertEquals(email, emailId2);
	}

	@Test
	public void testFindEmailAddressByEmailIdWithNullDisplay()
	{
		final EmailAddressModel email = emailAddressDao.findEmailAddressByEmailAndDisplayName("2@2.com", null);
		assertNull(email);
	}

	@Test
	public void testFindEmailAddressByEmailIdName()
	{
		final EmailAddressModel email = emailAddressDao.findEmailAddressByEmailAndDisplayName("3@3.com", "3 3");
		assertNotNull(email);
		assertEquals(email, emailId3);
	}

	@Test
	public void testFindEmailAddressByEmailNull()
	{
		final EmailAddressModel email = emailAddressDao.findEmailAddressByEmailAndDisplayName("4@4.com", null);
		assertNull(email);
	}

}
