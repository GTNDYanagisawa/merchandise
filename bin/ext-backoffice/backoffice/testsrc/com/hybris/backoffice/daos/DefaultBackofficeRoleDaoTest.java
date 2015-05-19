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
package com.hybris.backoffice.daos;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.daos.impl.DefaultBackofficeRoleDao;
import com.hybris.backoffice.model.user.BackofficeRoleModel;


/**
 * Test for {@link DefaultBackofficeRoleDao}.
 */
@IntegrationTest
public class DefaultBackofficeRoleDaoTest extends ServicelayerTransactionalTest
{
	private BackofficeRoleModel roleSimple;
	private BackofficeRoleModel roleAdvanced;

	@Resource
	private BackofficeRoleDao backofficeRoleDao;

	@Resource
	private ModelService modelService;

	@Before
	public void setUp()
	{
		roleSimple = new BackofficeRoleModel();
		roleSimple.setUid("role_simple");
		modelService.save(roleSimple);

		roleAdvanced = new BackofficeRoleModel();
		roleAdvanced.setUid("role_advanced");
		modelService.save(roleAdvanced);
	}

	/**
	 * Tests findAllBackofficeRoles(). There will be 2 BackofficeRoles system found.
	 */
	@Test
	public void testFindAllBackofficeRoles()
	{
		final Set<BackofficeRoleModel> backOfficeRoles = backofficeRoleDao.findAllBackofficeRoles();

		Assert.assertNotNull("The object should not be null at this point", backOfficeRoles);

		Assert.assertEquals("There should be 2 BackofficeRoleModel found", 2, backOfficeRoles.size());

		final List<String> roleUids = new ArrayList<String>();
		for (final BackofficeRoleModel role : new ArrayList<BackofficeRoleModel>(backOfficeRoles))
		{
			roleUids.add(role.getUid());
		}
		Assert.assertTrue("The configured Uids should exists in the returned Set",
				roleUids.containsAll(Arrays.asList(roleSimple.getUid(), roleAdvanced.getUid())));

	}
}
