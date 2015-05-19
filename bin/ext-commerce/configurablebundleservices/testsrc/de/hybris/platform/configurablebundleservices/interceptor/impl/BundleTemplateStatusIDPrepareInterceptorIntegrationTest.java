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
import de.hybris.platform.configurablebundleservices.enums.BundleTemplateStatusEnum;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateStatusModel;
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
 * Integration test suite for {@link BundleTemplateStatusIDPrepareInterceptor}
 * 
 */
@IntegrationTest
public class BundleTemplateStatusIDPrepareInterceptorIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(BundleTemplateStatusIDPrepareInterceptorIntegrationTest.class);
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
		LOG.info("Creating data for BundleTemplateStatusIDPrepareInterceptorIntegrationTest ...");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for BundleTemplateStatusIDPrepareInterceptorIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
	}

	@Test
	public void testGenerateAbstractBundleRuleID()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getSessionCatalogVersions().iterator().next();

		final BundleTemplateStatusModel bundleStatus1 = modelService.create(BundleTemplateStatusModel.class);
		Assert.assertNull(bundleStatus1.getId());
		bundleStatus1.setCatalogVersion(catalogVersion);
		bundleStatus1.setStatus(BundleTemplateStatusEnum.UNAPPROVED);
		modelService.save(bundleStatus1);
		Assert.assertNotNull(bundleStatus1.getId());
		LOG.info("bundleStatus1.id: " + bundleStatus1.getId());

		final BundleTemplateStatusModel bundleStatus2 = modelService.create(BundleTemplateStatusModel.class);
		Assert.assertNull(bundleStatus2.getId());
		bundleStatus2.setCatalogVersion(catalogVersion);
		bundleStatus2.setStatus(BundleTemplateStatusEnum.CHECK);
		modelService.save(bundleStatus2);
		Assert.assertNotNull(bundleStatus2.getId());
		Assert.assertTrue(!bundleStatus1.getId().equals(bundleStatus2.getId()));
		LOG.info("bundleStatus2.id: " + bundleStatus2.getId());

		final BundleTemplateStatusModel bundleStatus3 = modelService.create(BundleTemplateStatusModel.class);
		Assert.assertNull(bundleStatus3.getId());
		bundleStatus3.setCatalogVersion(catalogVersion);
		modelService.save(bundleStatus3);
		Assert.assertNotNull(bundleStatus3.getId());
		Assert.assertTrue(!bundleStatus1.getId().equals(bundleStatus3.getId()));
		Assert.assertTrue(!bundleStatus2.getId().equals(bundleStatus3.getId()));
		LOG.info("bundleStatus3.id: " + bundleStatus3.getId());

		final BundleTemplateStatusModel bundleStatus4 = modelService.create(BundleTemplateStatusModel.class);
		Assert.assertNull(bundleStatus4.getId());
		bundleStatus4.setCatalogVersion(catalogVersion);
		bundleStatus4.setId("MyOwnStatusId");
		bundleStatus4.setStatus(BundleTemplateStatusEnum.UNAPPROVED);
		modelService.save(bundleStatus4);
		Assert.assertEquals("MyOwnStatusId", bundleStatus4.getId());
		LOG.info("bundleStatus4.id: " + bundleStatus4.getId());

		final BundleTemplateStatusModel bundleStatus5 = modelService.create(BundleTemplateStatusModel.class);
		Assert.assertNull(bundleStatus5.getId());
		bundleStatus5.setCatalogVersion(catalogVersion);
		bundleStatus5.setStatus(BundleTemplateStatusEnum.APPROVED);
		modelService.save(bundleStatus5);
		Assert.assertNotNull(bundleStatus5.getId());
		Assert.assertTrue(!bundleStatus1.getId().equals(bundleStatus5.getId()));
		Assert.assertTrue(!bundleStatus2.getId().equals(bundleStatus5.getId()));
		Assert.assertTrue(!bundleStatus3.getId().equals(bundleStatus5.getId()));
		LOG.info("bundleStatus5.id: " + bundleStatus5.getId());
	}
}
