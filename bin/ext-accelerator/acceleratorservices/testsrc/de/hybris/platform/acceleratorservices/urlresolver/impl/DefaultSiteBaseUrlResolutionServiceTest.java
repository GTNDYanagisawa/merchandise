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
package de.hybris.platform.acceleratorservices.urlresolver.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link DefaultSiteBaseUrlResolutionService}
 */
@UnitTest
public class DefaultSiteBaseUrlResolutionServiceTest
{
	private static final String WEBSITE_URL = "http://apparel-uk.local:9001/yacceleratorstorefront";
	private static final String MEDIA_URL = "http://apparel-uk.local:9001";
	private static final String WEBSITE_URL_KEY = "website.apparel-uk.http";
	private static final String MEDIA_URL_KEY = "media.apparel-uk.http";
	private static final String B2C_STOREFRONT_CONTEXT_ROOT = "/acc";
	private static final String WEBSITE_UID = "website";
	private static final String PORT_KEY = "tomcat.http.port";
	private static final String PORT = "9001";
	private static final String SSL_PORT_KEY = "tomcat.ssl.port";
	private static final String SSL_PORT = "9003";

	private static final String URL = "http://localhost:9001/acc?clear=true&site=website";
	private static final String SSL_URL = "https://localhost:9003/acc?clear=true&site=website";
	private static final String URL_MEDIA = "http://localhost:9001";
	private static final String SSL_URL_MEDIA = "https://localhost:9003";

	private DefaultSiteBaseUrlResolutionService defaultSiteBaseUrlResolutionService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private UrlEncoderService urlEncoderService;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultSiteBaseUrlResolutionService = new DefaultSiteBaseUrlResolutionService();
		defaultSiteBaseUrlResolutionService.setConfigurationService(configurationService);
		defaultSiteBaseUrlResolutionService.setUrlEncoderService(urlEncoderService);

		final Map<SiteChannel, String> contextRoots = new HashMap<SiteChannel, String>();
		contextRoots.put(SiteChannel.B2C, B2C_STOREFRONT_CONTEXT_ROOT);
		contextRoots.put(SiteChannel.B2B, B2C_STOREFRONT_CONTEXT_ROOT);
		defaultSiteBaseUrlResolutionService.setContextRoots(contextRoots);

		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getString(WEBSITE_URL_KEY, null)).willReturn(WEBSITE_URL);
		given(configuration.getString(PORT_KEY, null)).willReturn(PORT);
		given(configuration.getString(SSL_PORT_KEY, null)).willReturn(SSL_PORT);
		given(configuration.getString(MEDIA_URL_KEY, null)).willReturn(MEDIA_URL);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testGetWebsiteUrlForSiteNull()
	{
		defaultSiteBaseUrlResolutionService.getWebsiteUrlForSite(null, false, "");
	}


	@Test
	public void testGetWebsiteUrlForSite()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);

		given(cmsSiteModel.getUid()).willReturn(WEBSITE_UID);
		given(cmsSiteModel.getChannel()).willReturn(SiteChannel.B2C);

		final String result = defaultSiteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteModel, false, "");

		Assert.assertEquals(URL, result);
	}


	@Test
	public void testGetWebsiteUrlForSiteSSL()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);

		given(cmsSiteModel.getUid()).willReturn(WEBSITE_UID);
		given(cmsSiteModel.getChannel()).willReturn(SiteChannel.B2C);

		final String result = defaultSiteBaseUrlResolutionService.getWebsiteUrlForSite(cmsSiteModel, true, "");

		Assert.assertEquals(SSL_URL, result);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testGetMediaUrlForSiteNull()
	{
		defaultSiteBaseUrlResolutionService.getMediaUrlForSite(null, false);
	}


	@Test
	public void testGetMediaUrlForSite()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);

		given(cmsSiteModel.getUid()).willReturn(WEBSITE_UID);
		given(cmsSiteModel.getChannel()).willReturn(SiteChannel.B2C);
		given(urlEncoderService.getUrlEncodingPattern()).willReturn("");

		final String result = defaultSiteBaseUrlResolutionService.getMediaUrlForSite(cmsSiteModel, false);

		Assert.assertEquals(URL_MEDIA, result);
	}


	@Test
	public void testGetMediaUrlForSiteSSL()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);

		given(cmsSiteModel.getUid()).willReturn(WEBSITE_UID);
		given(cmsSiteModel.getChannel()).willReturn(SiteChannel.B2C);

		final String result = defaultSiteBaseUrlResolutionService.getMediaUrlForSite(cmsSiteModel, true);

		Assert.assertEquals(SSL_URL_MEDIA, result);
	}
}
