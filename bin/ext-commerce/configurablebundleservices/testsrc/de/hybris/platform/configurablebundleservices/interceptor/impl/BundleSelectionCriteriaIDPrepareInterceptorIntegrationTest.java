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
package de.hybris.platform.configurablebundleservices.interceptor.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import java.util.Collections;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link AbstractBundleRuleIDPrepareInterceptor}
 * 
 */
@IntegrationTest
public class BundleSelectionCriteriaIDPrepareInterceptorIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(BundleSelectionCriteriaIDPrepareInterceptorIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BaseSiteService baseSiteService;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for BundleSelectionCriteriaIDPrepareInterceptorIntegrationTest ...");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for BundleSelectionCriteriaIDPrepareInterceptorIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
	}

	@Test
	public void testGenerateBundleSelectionCriteriaID()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getSessionCatalogVersions().iterator().next();

		final AutoPickBundleSelectionCriteriaModel selecCrit1 = modelService.create(AutoPickBundleSelectionCriteriaModel.class);
		Assert.assertNull(selecCrit1.getId());
		selecCrit1.setCatalogVersion(catalogVersion);
		modelService.save(selecCrit1);
		Assert.assertNotNull(selecCrit1.getId());
		LOG.info("selecCrit1.id: " + selecCrit1.getId());

		final PickExactlyNBundleSelectionCriteriaModel selecCrit2 = modelService
				.create(PickExactlyNBundleSelectionCriteriaModel.class);
		Assert.assertNull(selecCrit2.getId());
		selecCrit2.setCatalogVersion(catalogVersion);
		selecCrit2.setN(Integer.valueOf(1));
		modelService.save(selecCrit2);
		Assert.assertNotNull(selecCrit2.getId());
		Assert.assertTrue(!selecCrit1.getId().equals(selecCrit2.getId()));
		LOG.info("selecCrit2.id: " + selecCrit2.getId());

		final PickNToMBundleSelectionCriteriaModel selecCrit3 = modelService.create(PickNToMBundleSelectionCriteriaModel.class);
		Assert.assertNull(selecCrit3.getId());
		selecCrit3.setCatalogVersion(catalogVersion);
		selecCrit3.setN(Integer.valueOf(1));
		selecCrit3.setM(Integer.valueOf(2));
		modelService.save(selecCrit3);
		Assert.assertNotNull(selecCrit3.getId());
		Assert.assertTrue(!selecCrit1.getId().equals(selecCrit3.getId()));
		Assert.assertTrue(!selecCrit2.getId().equals(selecCrit3.getId()));
		LOG.info("selecCrit3.id: " + selecCrit3.getId());

		final AutoPickBundleSelectionCriteriaModel selecCrit4 = modelService.create(AutoPickBundleSelectionCriteriaModel.class);
		Assert.assertNull(selecCrit4.getId());
		selecCrit4.setCatalogVersion(catalogVersion);
		selecCrit4.setId("MyOwnSelecCritID");
		modelService.save(selecCrit4);
		Assert.assertEquals("MyOwnSelecCritID", selecCrit4.getId());
		LOG.info("selecCrit4.id: " + selecCrit4.getId());

		final PickNToMBundleSelectionCriteriaModel selecCrit5 = modelService.create(PickNToMBundleSelectionCriteriaModel.class);
		Assert.assertNull(selecCrit5.getId());
		selecCrit5.setCatalogVersion(catalogVersion);
		selecCrit5.setN(Integer.valueOf(1));
		selecCrit5.setM(Integer.valueOf(2));
		modelService.save(selecCrit5);
		Assert.assertNotNull(selecCrit5.getId());
		Assert.assertTrue(!selecCrit1.getId().equals(selecCrit5.getId()));
		Assert.assertTrue(!selecCrit2.getId().equals(selecCrit5.getId()));
		Assert.assertTrue(!selecCrit3.getId().equals(selecCrit5.getId()));
		LOG.info("selecCrit5.id: " + selecCrit5.getId());
	}
}
