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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.services.config.UIRole;

import java.util.List;

import org.junit.Test;


@IntegrationTest
public class UIConfigurationServiceTest extends UIConfigurationTestBase
{

	@Test
	public void testGetPossibleRolesWithoutUser()
	{
		final List<UIRole> roles = uiConfigurationService.getPossibleRoles();
		assertNotNull("Possible roles", roles);
		assertEquals("Possible roles collection size", 1, roles.size());
	}


	@Test
	public void testGetPossibleRolesWithUser()
	{
		final List<UIRole> roles = uiConfigurationService.getPossibleRoles(productManagerUser);
		assertNotNull("Possible roles", roles);
		assertEquals("Possible roles collection size", 1, roles.size());
	}

	@Test
	public void testGetSessionRoleFromPossibleRols()
	{
		uiConfigurationService.setSessionRole((UIRole) null);
		final UIRole role = uiConfigurationService.getSessionRole();
		assertNotNull("Role", role);
		final UIRole firstPossibleRole = uiConfigurationService.getPossibleRoles().get(0);
		assertEquals("Roles", firstPossibleRole, role);
	}

}
