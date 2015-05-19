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
package de.hybris.platform.importcockpit.services.login.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for LoginServiceImpl
 */
@UnitTest
public class LoginServiceImplTest
{
	private LoginServiceImpl loginServiceImpl;

	private static final String DEFAULT_USER_NAME = "defUserName";
	private static final String DEFAULT_USER_PASS = "defUserPass";

	@Mock
	private ConfigurationService configurationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		loginServiceImpl = new LoginServiceImpl();
		loginServiceImpl.setConfigurationService(configurationService);
	}

	@Test
	public void testGetDefaultUsername()
	{
		final Configuration mockedConfiguration = Mockito.mock(Configuration.class);
		given(configurationService.getConfiguration()).willReturn(mockedConfiguration);
		given(mockedConfiguration.getString("importcockpit.default.login")).willReturn(DEFAULT_USER_NAME);
		Assert.assertEquals(DEFAULT_USER_NAME, loginServiceImpl.getDefaultUsername());
	}

	@Test
	public void testGetDefaultUserPassword()
	{
		final Configuration mockedConfiguration = Mockito.mock(Configuration.class);
		given(configurationService.getConfiguration()).willReturn(mockedConfiguration);
		given(mockedConfiguration.getString("importcockpit.default.password")).willReturn(DEFAULT_USER_PASS);
		Assert.assertEquals(DEFAULT_USER_PASS, loginServiceImpl.getDefaultUserPassword());
	}
}
