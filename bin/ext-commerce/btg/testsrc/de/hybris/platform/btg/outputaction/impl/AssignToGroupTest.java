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
package de.hybris.platform.btg.outputaction.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.btg.model.BTGAssignToGroupDefinitionModel;
import de.hybris.platform.btg.outputaction.OutputActionContext;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the AssignToGroup.
 */
public class AssignToGroupTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;
	@Resource
	private AssignToGroup assignToGroup;
	@Resource
	private CatalogVersionService catalogVersionService;

	private CatalogVersionModel catalogVersion;

	private UserModel user1;
	private UserModel user2;
	private UserGroupModel group1;
	private UserGroupModel group2;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		prepareData();
	}

	private void prepareData()
	{
		user1 = createUser("user1");
		user2 = createUser("user2");
		group1 = createGroup("group1", user1);
		group2 = createGroup("group2", user2);
	}

	private UserModel createUser(final String uid)
	{
		final UserModel user = modelService.create(UserModel.class);
		user.setUid(uid);
		modelService.save(user);
		return user;
	}

	private UserGroupModel createGroup(final String uid, final UserModel user)
	{
		final UserGroupModel group = modelService.create(UserGroupModel.class);
		group.setUid(uid);
		final Set<PrincipalModel> users = new HashSet<PrincipalModel>();
		users.add(user);
		group.setMembers(users);
		modelService.save(group);
		return group;
	}

	private OutputActionContext createOutputActionContext()
	{
		final BTGAssignToGroupDefinitionModel assignToGroup = modelService.create(BTGAssignToGroupDefinitionModel.class);
		assignToGroup.setUserGroups(Collections.singleton(group2));
		assignToGroup.setCatalogVersion(catalogVersion);
		modelService.save(assignToGroup);
		final OutputActionContext<BTGAssignToGroupDefinitionModel> context = new DefaultOutputActionContext<BTGAssignToGroupDefinitionModel>(
				assignToGroup, user1);
		return context;
	}

	/**
	 * Assume that user1 is in group1, and user2 is in group2. <li>the BTGAssignToGroupDefinitionModel is defined with
	 * group2,</li> <li>user1 takes the action,</li> <li>tests the group member size before and after the action.</li>
	 */
	@Test
	public void testUserAndGroup()
	{
		assertEquals("member size of group1", 1, group1.getMembers().size());
		assertEquals("member size of group2", 1, group2.getMembers().size());

		final OutputActionContext context = createOutputActionContext();
		assignToGroup.performAction(null, context);

		modelService.refresh(group2);
		assertEquals("member size of group1", 1, group1.getMembers().size());
		assertEquals("member size of group2", 2, group2.getMembers().size());
	}

}
