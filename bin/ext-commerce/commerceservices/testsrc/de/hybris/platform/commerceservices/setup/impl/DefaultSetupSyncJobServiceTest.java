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
package de.hybris.platform.commerceservices.setup.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * Test suite for {@link DefaultSetupSyncJobService}
 */
@IntegrationTest
public class DefaultSetupSyncJobServiceTest extends ServicelayerTest
{
	private static final String TEST_PRODUCT_CATALOG = "productCatalog";
	private static final String TEST_CONTENT_CATALOG = "contentCatalog";
	private static final String ONLINE_PRODUCT_CODE = "product2";

	private static final String PRODUCT_CATALOG_SYNC_JOB = "sync " + TEST_PRODUCT_CATALOG + ":Staged->Online";
	private static final String CONTENT_CATALOG_SYNC_JOB = "sync " + TEST_CONTENT_CATALOG + ":Staged->Online";

	@Resource
	private SetupSyncJobService setupSyncJobService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		final Map params = new HashMap();
		params.put("test", new String[]
		{ "value1", "value2" });
		params.put("initmethod", new String[]
		{ "init" });

		importCsv("/commerceservices/test/testSystemSetup.impex", "utf8");
	}

	@Test
	public void testCreateProductCatalogSyncJob()
	{
		setupSyncJobService.createProductCatalogSyncJob(TEST_PRODUCT_CATALOG);
		assertNotNull("Catalog was null", getCatalogSyncJob(TEST_PRODUCT_CATALOG));
	}

	@Test
	public void testCreateContentCatalogSyncJob()
	{
		setupSyncJobService.createContentCatalogSyncJob(TEST_CONTENT_CATALOG);
		assertNotNull("Catalog was null", getCatalogSyncJob(TEST_CONTENT_CATALOG));
	}


	@Test
	public void testExecuteCatalogSyncJob()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(TEST_PRODUCT_CATALOG,
				CatalogManager.ONLINE_VERSION);
		setupSyncJobService.createProductCatalogSyncJob(TEST_PRODUCT_CATALOG);
		final PerformResult result = setupSyncJobService.executeCatalogSyncJob(TEST_PRODUCT_CATALOG);
		assertNotNull("Product was not synchronized!", productService.getProductForCode(catalogVersion, ONLINE_PRODUCT_CODE));
		assertEquals(CronJobResult.SUCCESS, result.getResult());
		assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}


	@Test
	public void testAssignDependantSyncJob()
	{

		setupSyncJobService.assignDependentSyncJobs(TEST_PRODUCT_CATALOG, Collections.singleton(TEST_CONTENT_CATALOG));//perform assign

		final CatalogVersionSyncJobModel modelExample = new CatalogVersionSyncJobModel();
		modelExample.setCode(PRODUCT_CATALOG_SYNC_JOB);
		final CatalogVersionSyncJobModel productCatalogSyncJob = flexibleSearchService.getModelByExample(modelExample);

		assertNotNull(productCatalogSyncJob);
		assertNotNull(PRODUCT_CATALOG_SYNC_JOB + " should have a dependent sync jobs ",
				productCatalogSyncJob.getDependentSyncJobs());
		assertEquals(PRODUCT_CATALOG_SYNC_JOB + " should have one dependent sync jobs ", 1, productCatalogSyncJob
				.getDependentSyncJobs().size());
		assertEquals(CONTENT_CATALOG_SYNC_JOB, productCatalogSyncJob.getDependentSyncJobs().iterator().next().getCode());
	}

	protected SyncItemJobModel getCatalogSyncJob(final String catalogId)
	{
		final List<SyncItemJobModel> synchronizations = catalogVersionService.getCatalogVersion(catalogId,
				CatalogManager.OFFLINE_VERSION).getSynchronizations();
		if (CollectionUtils.isNotEmpty(synchronizations))
		{
			return synchronizations.get(0);
		}
		return null;
	}
}
