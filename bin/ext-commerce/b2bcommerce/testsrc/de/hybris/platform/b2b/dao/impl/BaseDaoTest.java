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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


public class BaseDaoTest extends ServicelayerTransactionalBaseTest
{

	@Resource
	private BaseDao baseDao;
	@Resource
	private ModelService modelService;

	@Test
	public void testFindFirstByAttribute() throws Exception
	{

		final Class<? extends ItemModel> modelClass = UserModel.class;
		Assert.assertEquals("User", modelService.getModelType(modelClass));
		Assert.assertNotNull(baseDao.findFirstByAttribute(UserModel.UID, "admin", modelClass));

		de.hybris.platform.core.Registry.getApplicationContext().getBean("itemModelCloneCreator");
	}
}
