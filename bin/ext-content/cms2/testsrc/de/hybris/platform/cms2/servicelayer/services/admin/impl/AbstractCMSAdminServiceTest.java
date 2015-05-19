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
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.testframework.TestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class AbstractCMSAdminServiceTest // NOPMD Junit4
{
	private class CMSAdminServiceForTest extends AbstractCMSAdminService
	{// No need to implement
	}

	private static final String ACTIVE_SITE_KEY = "activeSite";
	private static final String ACTIVE_CATALOG_VERSION_KEY = "activeCatalogVersion";
	@InjectMocks
	private final CMSAdminServiceForTest cmsAdminService = new CMSAdminServiceForTest();
	@Mock
	private ModelService modelServiceMock;
	@Mock
	private SessionService sessionServiceMock;
	@Mock
	private CatalogVersionModel catalogVersionMock;
	@Mock
	private CMSSiteModel cmsSiteMock;

	private PK pk;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		// Create fake PK
		pk = PK.createFixedUUIDPK(1, System.nanoTime());
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.AbstractCMSAdminService#getActiveCatalogVersion()}
	 * .
	 */
	@Test
	public void shouldReturnCatalogVersionWhenActiveCatalogVersionIsPresentInSession()
	{
		// given
		given(sessionServiceMock.getAttribute(ACTIVE_CATALOG_VERSION_KEY)).willReturn(pk);
		given(modelServiceMock.get(pk)).willReturn(catalogVersionMock);

		// when
		final CatalogVersionModel activeCatalogVersion = cmsAdminService.getActiveCatalogVersion();

		// then
		assertThat(activeCatalogVersion).isNotNull();
		verify(modelServiceMock, times(1)).get(pk);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.AbstractCMSAdminService#getActiveSite()}.
	 */
	@Test
	public void shouldReturnCmsSiteWhenActiveSiteIsPresentInSession()
	{
		// given
		given(sessionServiceMock.getAttribute(ACTIVE_SITE_KEY)).willReturn(pk);
		given(modelServiceMock.get(pk)).willReturn(cmsSiteMock);

		// when
		final CMSSiteModel cmsSite = cmsAdminService.getActiveSite();

		// then
		assertThat(cmsSite).isNotNull();
		verify(modelServiceMock, times(1)).get(pk);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.AbstractCMSAdminService#getActiveCatalogVersion()}
	 * .
	 */
	@Test
	public void shouldReturnNullWhenActiveCatalogVersionIsNotPresentInSession()
	{
		// given
		given(sessionServiceMock.getAttribute(ACTIVE_CATALOG_VERSION_KEY)).willReturn(null);

		// when
		final CatalogVersionModel activeCatalogVersion = cmsAdminService.getActiveCatalogVersion();

		// then
		assertThat(activeCatalogVersion).isNull();
		verify(sessionServiceMock, times(1)).getAttribute(ACTIVE_CATALOG_VERSION_KEY);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.AbstractCMSAdminService#getActiveSite()}.
	 */
	@Test
	public void shouldReturnNullWhenActiveSiteIsNotPresentInSession()
	{
		// given
		given(sessionServiceMock.getAttribute(ACTIVE_SITE_KEY)).willReturn(null);

		// when
		final CMSSiteModel cmsSite = cmsAdminService.getActiveSite();

		// then
		assertThat(cmsSite).isNull();
		verify(sessionServiceMock, times(1)).getAttribute(ACTIVE_SITE_KEY);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.AbstractCMSAdminService#getActiveCatalogVersion()}
	 * .
	 */
	@Test
	public void shouldReturnNullWhenExceptionWasThrownDuringGettingCatalogVersion()
	{
		TestUtils.disableFileAnalyzer("Throwing Fake Exceptions");
		// given
		given(sessionServiceMock.getAttribute(ACTIVE_CATALOG_VERSION_KEY)).willReturn(pk);
		given(modelServiceMock.get(pk)).willThrow(new RuntimeException("FooBar"));

		// when
		final CatalogVersionModel activeCatalogVersion = cmsAdminService.getActiveCatalogVersion();

		// then
		assertThat(activeCatalogVersion).isNull();
		verify(modelServiceMock, times(1)).get(pk);
		TestUtils.enableFileAnalyzer();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.admin.impl.AbstractCMSAdminService#getActiveSite()} .
	 */
	@Test
	public void shouldReturnNullWhenExceptionWasThrownDuringGettingCmsSite()
	{
		TestUtils.disableFileAnalyzer("Throwing Fake Exceptions");
		// given
		given(sessionServiceMock.getAttribute(ACTIVE_SITE_KEY)).willReturn(pk);
		given(modelServiceMock.get(pk)).willThrow(new RuntimeException("FooBar"));

		// when
		final CMSSiteModel cmsSite = cmsAdminService.getActiveSite();

		// then
		assertThat(cmsSite).isNull();
		verify(modelServiceMock, times(1)).get(pk);
		TestUtils.enableFileAnalyzer();
	}
}
