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
package de.hybris.platform.cockpit.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cockpit.CockpitConfigurationService;
import de.hybris.platform.cockpit.model.CockpitUIComponentConfigurationModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for cockpit configrations.
 */
@IntegrationTest
public class DefaultCockpitConfigurationServiceIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultCockpitConfigurationServiceIntegrationTest.class);

	@Resource
	private CockpitConfigurationService cockpitConfigurationService;

	@Resource
	private UserService userService;


	@Before
	public void setUp() throws Exception
	{
		// Create data for tests
		LOG.info("Creating data for configurations ..");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/test/testConfigurations.csv", "utf-8");

		LOG.info("Finished data for configurations " + (System.currentTimeMillis() - startTime) + "ms");
	}



	@Test
	public void testGetComponentConfigurationsForPrincipal()
	{
		final List<CockpitUIComponentConfigurationModel> componentConfigurationModels = cockpitConfigurationService
				.getComponentConfigurationsForPrincipal(userService.getUserForUID("ahertz"));

		assertThat(componentConfigurationModels.size()).isEqualTo(5);
	}


	@Test
	public void testGetDedicatedComponentConfigurationsForPrincipal()
	{
		final List<CockpitUIComponentConfigurationModel> componentConfigurationModels = cockpitConfigurationService
				.getDedicatedComponentConfigurationsForPrincipal(userService.getUserForUID("ahertz"));

		assertThat(componentConfigurationModels.size()).isEqualTo(3);
	}


	@Test
	public void testGetComponentConfiguration()
	{
		final PrincipalModel ahertz = userService.getUserForUID("ahertz");
		final PrincipalModel abrode = userService.getUserForUID("abrode");

		CockpitUIComponentConfigurationModel configurationModel = cockpitConfigurationService.getComponentConfiguration(ahertz,
				"TestProduct", "advancedSearch");

		assertThat(configurationModel).isNotNull();
		assertThat(configurationModel.getCode()).isEqualTo("advancedSearch");

		configurationModel = cockpitConfigurationService.getComponentConfiguration(abrode, "TestProduct", "advancedSearch");

		assertThat(configurationModel).isNotNull();
		assertThat(configurationModel.getCode()).isEqualTo("advancedSearch");

		try
		{
			configurationModel = cockpitConfigurationService.getComponentConfiguration(null, "TestProduct", "advancedSearch");
			Assert.fail("Should not get result here!");
		}
		catch (final UnknownIdentifierException uie)
		{
			// Expected
		}

		try
		{
			configurationModel = cockpitConfigurationService.getComponentConfiguration(abrode, "notExisting", "advancedSearch");
			Assert.fail("Should not get result here!");
		}
		catch (final UnknownIdentifierException uie)
		{
			// Expected
		}
	}


}
