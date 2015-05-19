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
package de.hybris.platform.cockpit.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.jalo.CockpitManager;
import de.hybris.platform.cockpit.jalo.CockpitTest;
import de.hybris.platform.cockpit.jalo.CockpitUIComponentAccessRight;
import de.hybris.platform.cockpit.services.security.UIAccessRightService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.security.Principal;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class UIComponentAccessRightTest extends CockpitTest
{
	protected final static String UI_COMP_1 = "testComponent";
	protected final static String UI_COMP_2 = "testComponent2";

	protected UserModel productManagerUser;
	protected UserModel testUser;
	protected UserGroup cockpitGroup;
	protected UIAccessRightService uiAccessRightService;
	protected ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		final UserManager userManager = UserManager.getInstance();
		this.cockpitGroup = userManager.createUserGroup("cockpitusergroup");
		final User productManagerUserItem = userManager.createUser("productmanager");
		productManagerUserItem.setPassword("pm");
		userManager.getAdminUserGroup().addMember(productManagerUserItem);
		this.cockpitGroup.addMember(productManagerUserItem);
		final User testUserItem = userManager.createUser("testuser");
		testUserItem.setPassword("test");
		userManager.getAdminUserGroup().addMember(testUserItem);

		this.productManagerUser = (UserModel) modelService.get(productManagerUserItem);
		this.testUser = (UserModel) modelService.get(testUserItem);
		final CockpitManager cockpitManager = CockpitManager.getInstance();

		final Map<String, Object> initValues = new HashMap<String, Object>();
		initValues.put(CockpitUIComponentAccessRight.CODE, UI_COMP_1);
		CockpitUIComponentAccessRight accessRight = cockpitManager.createCockpitUIComponentAccessRight(initValues);

		initValues.put(CockpitUIComponentAccessRight.CODE, UI_COMP_2);
		accessRight = cockpitManager.createCockpitUIComponentAccessRight(initValues);
		accessRight.setReadPrincipals(Arrays.asList(new Principal[]
		{ productManagerUserItem, testUserItem }));
		accessRight.setWritePrincipals(Arrays.asList(new Principal[]
		{ productManagerUserItem }));
	}

	@Test
	public void testUIAccessRights() throws Exception
	{
		assertFalse(testUser.getUid() + " should have read rigths to component " + UI_COMP_1,
				this.uiAccessRightService.canRead(testUser, UI_COMP_1));
		assertFalse(productManagerUser.getUid() + " should have read rigths to component " + UI_COMP_1,
				this.uiAccessRightService.canRead(productManagerUser, UI_COMP_1));
		assertFalse(testUser.getUid() + " should have write rigths to component " + UI_COMP_1,
				this.uiAccessRightService.canWrite(testUser, UI_COMP_1));
		assertFalse(productManagerUser.getUid() + " should have write rigths to component " + UI_COMP_1,
				this.uiAccessRightService.canWrite(productManagerUser, UI_COMP_1));

		assertTrue(testUser.getUid() + " should have read rigths to component " + UI_COMP_2,
				this.uiAccessRightService.canRead(testUser, UI_COMP_2));
		assertTrue(productManagerUser.getUid() + " should have read rigths to component " + UI_COMP_2,
				this.uiAccessRightService.canRead(productManagerUser, UI_COMP_2));
		assertFalse(testUser.getUid() + " should NOT have write rigths to component " + UI_COMP_2,
				this.uiAccessRightService.canWrite(testUser, UI_COMP_2));
		assertTrue(productManagerUser.getUid() + " should have write rigths to component " + UI_COMP_2,
				this.uiAccessRightService.canWrite(productManagerUser, UI_COMP_2));
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public void setUiAccessRightService(final UIAccessRightService uiAccessRightService)
	{
		this.uiAccessRightService = uiAccessRightService;
	}
}
