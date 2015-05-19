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
package de.hybris.platform.commerceservices.dataimport.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.commerceservices.setup.SetupSolrIndexerService;
import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.commerceservices.setup.data.ImportData;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.validation.services.ValidationService;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SampleDataImportServiceTest
{
	private static final String TEST_EXTENSION_NAME = "testExtension";
	private static final String TEST_PRODUCT_CATALOG_NAME = "test";
	private static final String TEST_CONTENT_CATALOG_NAME = "test";
	private static final String TEST_STORE_NAME = "test";

	private SampleDataImportService sampleDataImportService;

	@Mock
	private AbstractSystemSetup setup;

	@Mock
	private SystemSetupContext ctx;

	@Mock
	private ValidationService validation;

	@Mock
	private SetupImpexService setupImpexService;

	@Mock
	private SetupSyncJobService setupSyncJobService;

	@Mock
	private SetupSolrIndexerService setupSolrIndexerService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private EventService eventService;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		sampleDataImportService = Mockito.spy(new SampleDataImportService());

		Mockito.doReturn(setupImpexService).when(sampleDataImportService).getSetupImpexService();
		Mockito.doReturn(setupSyncJobService).when(sampleDataImportService).getSetupSyncJobService();
		Mockito.doReturn(setupSolrIndexerService).when(sampleDataImportService).getSetupSolrIndexerService();
		Mockito.doReturn(catalogVersionService).when(sampleDataImportService).getCatalogVersionService();
		Mockito.doReturn(eventService).when(sampleDataImportService).getEventService();

		Mockito.doReturn(TEST_EXTENSION_NAME).when(ctx).getExtensionName();

		Mockito.doReturn(Boolean.TRUE).when(sampleDataImportService)
				.isExtensionLoaded(SampleDataImportService.CMS_COCKPIT_EXTENSION_NAME);
		Mockito.doReturn(Boolean.TRUE).when(sampleDataImportService)
				.isExtensionLoaded(SampleDataImportService.PRODUCT_COCKPIT_EXTENSION_NAME);
		Mockito.doReturn(Boolean.TRUE).when(sampleDataImportService)
				.isExtensionLoaded(SampleDataImportService.CS_COCKPIT_EXTENSION_NAME);
		Mockito.doReturn(Boolean.TRUE).when(sampleDataImportService)
				.isExtensionLoaded(SampleDataImportService.REPORT_COCKPIT_EXTENSION_NAME);
		Mockito.doReturn(Boolean.TRUE).when(sampleDataImportService).isExtensionLoaded(SampleDataImportService.MCC_EXTENSION_NAME);
		Mockito.doReturn(Boolean.TRUE).when(sampleDataImportService).isExtensionLoaded(SampleDataImportService.BTG_EXTENSION_NAME);

		Mockito.doReturn(Boolean.TRUE).when(setup).getBooleanSystemSetupParameter(ctx, SampleDataImportService.IMPORT_SAMPLE_DATA);
		Mockito.doReturn(Boolean.TRUE).when(setup)
				.getBooleanSystemSetupParameter(ctx, SampleDataImportService.ACTIVATE_SOLR_CRON_JOBS);
	}

	@Test
	public void testImportExecution()
	{
		final InOrder order = Mockito.inOrder(setupImpexService, setupSyncJobService, setupSolrIndexerService, validation);

		final ImportData sampleImportData = new ImportData();
		sampleImportData.setProductCatalogName(TEST_PRODUCT_CATALOG_NAME);
		sampleImportData.setContentCatalogNames(Arrays.asList(TEST_CONTENT_CATALOG_NAME));
		sampleImportData.setStoreNames(Arrays.asList(TEST_STORE_NAME));

		sampleDataImportService.execute(setup, ctx, Arrays.asList(sampleImportData));

		verifyImportCommonData(order);
		verifyImportProductCatalog(order);
		verifyImportContentCatalog(order);
		verifySynchronizeProductCatalog(order);
		verifySynchronizeContentCatalog(order);
		verifyImportStore(order);
		verifyImportSolrIndex(order);
	}

	private void verifyImportCommonData(final InOrder order)
	{
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/cockpits/cmscockpit/cmscockpit-users.impex", TEST_EXTENSION_NAME), false);
		order.verify(setupImpexService)
				.importImpexFile(
						String.format("/%s/import/sampledata/cockpits/productcockpit/productcockpit-users.impex", TEST_EXTENSION_NAME),
						false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/cockpits/cscockpit/cscockpit-users.impex", TEST_EXTENSION_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/cockpits/reportcockpit/reportcockpit-users.impex", TEST_EXTENSION_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/cockpits/reportcockpit/reportcockpit-mcc-links.impex", TEST_EXTENSION_NAME),
				false);
	}

	private void verifyImportProductCatalog(final InOrder order)
	{
		// Load Units
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/classifications-units.impex",
						TEST_EXTENSION_NAME, TEST_PRODUCT_CATALOG_NAME), false);

		// Load Categories
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/categories.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);

		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/categories-classifications.impex",
						TEST_EXTENSION_NAME, TEST_PRODUCT_CATALOG_NAME), false);

		// Load Suppliers
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/suppliers.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/suppliers-media.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);

		// Load medias for Categories as Suppliers loads some new Categories
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/categories-media.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);

		// Load Products
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-media.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-classifications.impex",
						TEST_EXTENSION_NAME, TEST_PRODUCT_CATALOG_NAME), false);

		// Load Products Relations
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-relations.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);

		// Load Products Fixes
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-fixup.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);

		// Load Prices
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-prices.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);

		// Load Stock Levels
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-stocklevels.impex",
						TEST_EXTENSION_NAME, TEST_PRODUCT_CATALOG_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-pos-stocklevels.impex",
						TEST_EXTENSION_NAME, TEST_PRODUCT_CATALOG_NAME), false);

		// Load Taxes
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/products-tax.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);
	}

	private void verifyImportContentCatalog(final InOrder order)
	{
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-content.impex", TEST_EXTENSION_NAME,
						TEST_CONTENT_CATALOG_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/cms-mobile-content.impex", TEST_EXTENSION_NAME,
						TEST_CONTENT_CATALOG_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/contentCatalogs/%sContentCatalog/email-content.impex", TEST_EXTENSION_NAME,
						TEST_CONTENT_CATALOG_NAME), false);
	}

	private void verifySynchronizeProductCatalog(final InOrder order)
	{
		order.verify(setupSyncJobService).createProductCatalogSyncJob(String.format("%sProductCatalog", TEST_PRODUCT_CATALOG_NAME));

		order.verify(setupSyncJobService).executeCatalogSyncJob(String.format("%sProductCatalog", TEST_PRODUCT_CATALOG_NAME));
	}

	private void verifySynchronizeContentCatalog(final InOrder order)
	{
		order.verify(setupSyncJobService).createContentCatalogSyncJob(String.format("%sContentCatalog", TEST_CONTENT_CATALOG_NAME));

		order.verify(setupSyncJobService).executeCatalogSyncJob(String.format("%sContentCatalog", TEST_CONTENT_CATALOG_NAME));
	}

	private void verifyImportStore(final InOrder order)
	{
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/points-of-service-media.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME),
				false);
		order.verify(setupImpexService)
				.importImpexFile(
						String.format("/%s/import/sampledata/stores/%s/points-of-service.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME),
						false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/btg.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/warehouses.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/productCatalogs/%sProductCatalog/reviews.impex", TEST_EXTENSION_NAME,
						TEST_PRODUCT_CATALOG_NAME), false);
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/promotions.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME), false);
	}

	private void verifyImportSolrIndex(final InOrder order)
	{
		order.verify(setupImpexService).importImpexFile(
				String.format("/%s/import/sampledata/stores/%s/solr.impex", TEST_EXTENSION_NAME, TEST_STORE_NAME), false);

		order.verify(setupSolrIndexerService).createSolrIndexerCronJobs(String.format("%sIndex", TEST_STORE_NAME));
	}
}
