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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;


@UnitTest
public class ConfigurationProviderFactoryImplTest
{


	private ConfigurationProviderFactoryImpl classUnderTest;
	@Mock
	private SessionService mockedSessionService;
	@Mock
	ApplicationContext mockApplicationContext;

	final ConfigurationProvider dummyProvider = new DummyProvider();
	final ConfigurationProvider dummyLocalProvider = new DummyProvider();





	@Before
	public void setUp()
	{

		classUnderTest = new ConfigurationProviderFactoryImpl();
		classUnderTest = Mockito.spy(classUnderTest);
		MockitoAnnotations.initMocks(this);
		classUnderTest.setSessionService(mockedSessionService);
		classUnderTest.setApplicationContext(mockApplicationContext);

		Mockito.when(mockApplicationContext.getBean("sapProductConfigConfigurationProvider")).thenReturn(dummyProvider);
		Mockito.when(mockApplicationContext.getBean("sapProductConfigLocalConfigurationProvider")).thenReturn(dummyLocalProvider);

	}

	@Test
	public void testGetProvider_newSession()
	{
		Mockito.when(mockedSessionService.getAttribute(ConfigurationProviderFactoryImpl.SESSION_CACHE_KEY)).thenReturn(null);
		final ConfigurationProvider provider = classUnderTest.getProvider();
		assertNotNull(provider);
		assertSame(dummyProvider, provider);
	}

	@Test
	public void testGetProvider_existingSession()
	{
		Mockito.when(mockedSessionService.getAttribute(ConfigurationProviderFactoryImpl.SESSION_CACHE_KEY)).thenReturn(
				dummyProvider);
		final ConfigurationProvider provider = classUnderTest.getProvider();
		assertNotNull(provider);
		assertSame(dummyProvider, provider);
	}

}
