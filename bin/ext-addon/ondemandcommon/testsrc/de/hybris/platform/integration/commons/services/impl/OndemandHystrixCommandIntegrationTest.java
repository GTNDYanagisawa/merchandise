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
 */

package de.hybris.platform.integration.commons.services.impl;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@ManualTest
@Ignore("test not yet completed")
public class OndemandHystrixCommandIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(OndemandHystrixCommandIntegrationTest.class);

	@Resource
	private SessionService sessionService;

	private OndemandHystrixCommandConfiguration config;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;

	@Before
	public void beforeTest() throws Exception
	{
		createCoreData();
		// create testCatalog and set catalog version into the session
		createDefaultCatalog();
		importCsv("/ondemandcommon/test/testAcceleratorData.csv", "UTF-8");

		config = new OndemandHystrixCommandConfiguration();
		config.setGroupKey("123");
		config.setThreadPoolSize(1);
		config.setThreadTimeout(100000);

		final BaseSiteModel site = baseSiteService.getBaseSiteForUID("testSite");
		Assert.assertNotNull("no baseSite with uid 'testSite", site);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);
	}

	@Test
	public void shouldExecuteHystrixCommand() throws Exception
	{
		LOG.info(String.format("Outside Hystrix thread() session: %s", sessionService.getCurrentSession()));

		final Collection<CatalogVersionModel> result = ondemandHystrixCommandFactory.newCommand(config, new HystrixExecutable<Collection<CatalogVersionModel>>()
		{
			@Override
			public Collection<CatalogVersionModel> runEvent()
			{
				LOG.info(String.format("runEvent() session: %s", sessionService.getCurrentSession()));
				final Collection<CatalogVersionModel> sessionCatalogVersions = catalogVersionService.getSessionCatalogVersions();
				Assert.assertFalse(sessionCatalogVersions.isEmpty());
				return sessionCatalogVersions;
			}

			@Override
			public Collection<CatalogVersionModel> fallbackEvent()
			{
				LOG.info(String.format("fallbackEvent() session: %s", sessionService.getCurrentSession()));
				Assert.fail();

				return Collections.EMPTY_LIST;
			}

			@Override
			public Collection<CatalogVersionModel> defaultEvent()
			{
				return Collections.EMPTY_LIST;
			}
		}).execute();

		Assert.assertNotNull(result);
	}
}
