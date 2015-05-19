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
package de.hybris.platform.cockpit;

import static de.hybris.platform.testframework.Assert.assertCollection;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.DemoTest;
import de.hybris.platform.cockpit.model.CockpitUIComponentConfigurationModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Demonstrates usage of the CockpitConfigurationService
 */
@DemoTest
public class CockpitConfigurationServiceTest extends ServicelayerTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(CockpitConfigurationServiceTest.class);

	/**
	 * NOTE: This list depends on impex data found in testUser.csv
	 */
	private static final List<String> EXPECTED_ROLES = new ArrayList<String>(
			Arrays.asList("demo", "customergroup", "cockpitgroup"));

	@Resource
	private CockpitConfigurationService cockpitConfigurationService;

	private PrincipalModel principal;

	@Resource
	ModelService modelService;

	@Resource
	UserService userService;

	/**
	 * prepares some test data, before execution of every test:<br/>
	 * - the test principalModel will be created and saved<br/>
	 */
	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();
		createTestUICockpitConfigurations();
		principal = userService.getUserForUID("demo");
	}

	/**
	 * Demonstrates two ways to retrieve cockpit configurations given a principal:<br/>
	 * - Retrieves all component configurations based on the given principal<br/>
	 * - Retrieve a specific configuration based on principal, object template code, and code.<br/>
	 */
	@Test
	public void testGetCockpitConfigurations()
	{
		final List<CockpitUIComponentConfigurationModel> configList = cockpitConfigurationService
				.getComponentConfigurationsForPrincipal(principal);
		assertNotNull(configList);
		final CockpitUIComponentConfigurationModel config = configList.get(0);
		assertNotNull(config);
		final String objectTemplateCode = "TestProduct";
		final String code = "advancedSearch";
		final CockpitUIComponentConfigurationModel configResult = cockpitConfigurationService.getComponentConfiguration(principal,
				objectTemplateCode, code);
		assertNotNull(configResult);
	}

	/**
	 * Demonstrates how to retrieve roles names for a given principal
	 */
	@Test
	public void testGetRoleNamesForPrincipal()
	{
		final List<String> actualRoles = cockpitConfigurationService.getRoleNamesForPrincipal(principal);
		assertNotNull(actualRoles);
		LOG.info("Roles for principal " + principal.getUid() + ":");
		for (final String role : actualRoles)
		{
			LOG.info(role);
		}
		assertCollection(EXPECTED_ROLES, actualRoles);
	}

	/**
	 * Imports demo UI component configurations from testUIComponentConfigurations.csv as part of demo set up
	 */
	private void createTestUICockpitConfigurations() throws Exception
	{
		LOG.info("Creating test UI Cockpit Configurations ..");
		final long startTime = System.currentTimeMillis();
		importCsv("/servicelayer/test/testUIComponentConfigurations.csv", "windows-1252");
		LOG.info("Finished creating test users UI Cockpit Configurations in " + (System.currentTimeMillis() - startTime) + "ms");
	}

}
