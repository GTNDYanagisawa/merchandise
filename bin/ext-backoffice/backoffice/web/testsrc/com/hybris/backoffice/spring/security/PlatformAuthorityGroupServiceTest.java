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
package com.hybris.backoffice.spring.security;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import com.hybris.backoffice.cockpitng.core.user.impl.DefaultPlatformAuthorityGroupService;
import com.hybris.backoffice.daos.BackofficeRoleDao;
import com.hybris.backoffice.model.user.BackofficeRoleModel;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.core.user.impl.AuthorityGroup;
import com.hybris.cockpitng.util.CockpitSessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PlatformAuthorityGroupServiceTest
{
	private DefaultPlatformAuthorityGroupService authorityGroupService;
	@Mock
	private CockpitSessionService cockpitSessionService;
	@Mock
	private CockpitUserService cockpitUserService;
	@Mock
	private UserService userService;
	@Mock
	private BackofficeRoleDao backofficeRoleDao;

	@Before
	public void setUp()
	{
		authorityGroupService = new DefaultPlatformAuthorityGroupService();
		authorityGroupService.setUserService(userService);
		authorityGroupService.setCockpitUserService(cockpitUserService);
		authorityGroupService.setCockpitSessionService(cockpitSessionService);
		authorityGroupService.setBackofficeRoleDao(backofficeRoleDao);
	}

	@Test
	public void testGetActiveAuthorityGroupForUser()
	{

		final AuthorityGroup simpleGroup = new AuthorityGroup();
		simpleGroup.setAuthorities(Collections.singletonList("role_simple"));
		simpleGroup.setName("simple");

		final AuthorityGroup advancedGroup = new AuthorityGroup();
		advancedGroup.setAuthorities(Collections.singletonList("role_advanced"));
		advancedGroup.setName("advanced");

		Mockito.when(cockpitUserService.getCurrentUser()).thenReturn("simple");
		Mockito.when(cockpitSessionService.getAttribute("cockpitActiveAuthorityGroup")).thenReturn(simpleGroup);
		final AuthorityGroup group1 = authorityGroupService.getActiveAuthorityGroupForUser(simpleGroup.getName());
		Assert.assertTrue("role_simple".equals(group1.getAuthorities().get(0)));

		// Whenever the userNames are not the same null should be return
		Mockito.when(cockpitSessionService.getAttribute("cockpitActiveAuthorityGroup")).thenReturn(advancedGroup);
		final AuthorityGroup group2 = authorityGroupService.getActiveAuthorityGroupForUser(advancedGroup.getName());
		Assert.assertNull(group2);

	}

	@Test
	public void testGetAllAuthorityGroups()
	{
		final AuthorityGroup fullGroup = new AuthorityGroup();
		fullGroup.setAuthorities(Arrays.asList("role_simple", "role_advanced"));
		fullGroup.setName("full");

		final Set<BackofficeRoleModel> backOfficeRoles = new LinkedHashSet<BackofficeRoleModel>();
		final BackofficeRoleModel roleSimple = new BackofficeRoleModel();
		roleSimple.setUid("role_simple");
		final BackofficeRoleModel roleAdvanced = new BackofficeRoleModel();
		roleAdvanced.setUid("role_advanced");

		backOfficeRoles.add(roleSimple);
		backOfficeRoles.add(roleAdvanced);

		Mockito.when(backofficeRoleDao.findAllBackofficeRoles()).thenReturn(backOfficeRoles);

		final List<AuthorityGroup> allGroups = authorityGroupService.getAllAuthorityGroups();
		Assert.assertEquals(backOfficeRoles.size(), allGroups.size());

		for (int i = 0; i < allGroups.size(); i++)
		{
			final String authorityCode = allGroups.get(i).getCode();
			final String expectedAuthorityCode = fullGroup.getAuthorities().get(i);
			Assert.assertTrue(expectedAuthorityCode.equals(authorityCode));
		}
	}

	@Test
	public void testGetAllAuthorityGroupsForUser()
	{
		final AuthorityGroup fullGroup = new AuthorityGroup();
		fullGroup.setAuthorities(Arrays.asList("role_simple", "role_advanced"));
		fullGroup.setCode("full");

		final BackofficeRoleModel roleSimple = new BackofficeRoleModel();
		roleSimple.setUid("role_simple");
		final BackofficeRoleModel roleAdvanced = new BackofficeRoleModel();
		roleAdvanced.setUid("role_advanced");
		final Set<BackofficeRoleModel> backOfficeRoles = new HashSet();
		backOfficeRoles.add(roleAdvanced);
		backOfficeRoles.add(roleSimple);
		final UserModel userModel = new UserModel();
		userModel.setUid("full");

		Mockito.when(userService.getUserForUID("full")).thenReturn(userModel);
		Mockito.when(userService.getAllUserGroupsForUser(userModel, BackofficeRoleModel.class)).thenReturn(backOfficeRoles);

		final List<AuthorityGroup> allGroups = authorityGroupService.getAllAuthorityGroupsForUser(fullGroup.getCode());
		Assert.assertEquals(backOfficeRoles.size(), allGroups.size());

		final List<String> roleUids = new ArrayList<String>();
		for (final BackofficeRoleModel role : new ArrayList<BackofficeRoleModel>(backOfficeRoles))
		{
			roleUids.add(role.getUid());
		}

		final List<String> authoritiesCode = new ArrayList<String>();
		for (final AuthorityGroup authorityGroup : allGroups)
		{
			authoritiesCode.add(authorityGroup.getCode());
		}

		Assert.assertTrue(authoritiesCode.containsAll(roleUids));

	}

	@Test
	public void testGetAuthorityGroup()
	{
		final BackofficeRoleModel roleModel = new BackofficeRoleModel();
		roleModel.setUid("role_advanced");
		roleModel.setDescription("This is an advanced user");
		Mockito.when(userService.getUserGroupForUID("role_advanced", BackofficeRoleModel.class)).thenReturn(roleModel);
		final AuthorityGroup group1 = authorityGroupService.getAuthorityGroup(roleModel.getUid());

		Assert.assertTrue("role_advanced".equals(group1.getCode()));
	}

}
