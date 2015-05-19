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
package de.hybris.platform.integration.oms.ats.dataexport;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.acceleratorservices.dataexport.generic.event.ExportDataEvent;
import de.hybris.platform.acceleratorservices.dataexport.generic.query.impl.FlexibleSearchExportQuery;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.PK;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.integration.Message;
import org.springframework.integration.message.GenericMessage;


@ManualTest
public class OmsDataExportQueryTest extends ServicelayerTest
{
	private static final String SITE_NAME = "testSite";

	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private BaseStoreService baseStoreService;
	@Resource
	private FlexibleSearchExportQuery exportOMSLocationQuery;
	@Resource
	private FlexibleSearchExportQuery exportOmsInventoryQuery;

	@Before
	public void prepare() throws ImpExException
	{
		importCsv("/omsats/test/testStockLevels.csv", "UTF-8");
		MockitoAnnotations.initMocks(this);

		final BaseSiteModel site = baseSiteService.getBaseSiteForUID(SITE_NAME);
		Assert.assertNotNull(String.format("no baseSite with uid: %s", SITE_NAME), site);
		site.setChannel(SiteChannel.B2C);
		baseSiteService.setCurrentBaseSite(site, false);
	}

	@Test
	public void testLocationsExportQuery() throws Throwable
	{
		final ExportDataEvent dataEvent = new ExportDataEvent();
		dataEvent.setBaseStore(baseStoreService.getCurrentBaseStore());

		final Message<ExportDataEvent> message = new GenericMessage(dataEvent, null);

		final List<PK> results = exportOMSLocationQuery.search(message, dataEvent);

		Assert.assertNotNull(results);
		Assert.assertNotSame(Integer.valueOf(results.size()), Integer.valueOf(0));
	}

	@Test
	public void testInventoryExportQuery() throws Throwable
	{
		final ExportDataEvent dataEvent = new ExportDataEvent();
		dataEvent.setBaseStore(baseStoreService.getCurrentBaseStore());

		final Message<ExportDataEvent> message = new GenericMessage(dataEvent, null);

		final List<PK> results = exportOmsInventoryQuery.search(message, dataEvent);

		Assert.assertNotNull(results);
		Assert.assertNotSame(Integer.valueOf(results.size()), Integer.valueOf(0));
	}
}
