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
package de.hybris.platform.basecommerce.site.dao.impl;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.basecommerce.site.dao.BaseSiteDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class BaseSiteDaoTest extends ServicelayerTransactionalTest
{

	private static final String TEST_BASE_SITE_UID = "test_base_site_uid";

	@Resource
	private BaseSiteDao baseSiteDao;
	@Resource
	private ModelService modelService;

	private BaseSiteModel model;

	@Before
	public void setUp()
	{
		model = new BaseSiteModel();
		model.setUid(TEST_BASE_SITE_UID);
		modelService.save(model);
	}

	@Test
	public void testFindAllBaseSites()
	{
		assertNotNull("findAllBaseSites returned null", baseSiteDao.findAllBaseSites());
	}

	@Test
	public void testFindBaseSiteByUID()
	{
		final BaseSiteModel other = baseSiteDao.findBaseSiteByUID(TEST_BASE_SITE_UID);
		assertNotNull("baseSite is null", other);
		assertEquals("baseSites differ", model, other);
	}
}
