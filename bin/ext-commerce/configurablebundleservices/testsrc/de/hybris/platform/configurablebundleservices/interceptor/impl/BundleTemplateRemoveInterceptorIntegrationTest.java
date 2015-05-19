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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateStatusModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Integration tests for the bundle template remove interceptor {@link BundleTemplateRemoveInterceptor}
 */
@IntegrationTest
public class BundleTemplateRemoveInterceptorIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(BundleTemplateRemoveInterceptorIntegrationTest.class);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BundleTemplateService bundleTemplateService;

	@Resource
	private ModelService modelService;

	private BundleTemplateModel parentTemplate;

	private BundleTemplateStatusModel bundleStatus;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for BundleTemplateRemoveInterceptorTest ...");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);

		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
		importCsv("/subscriptionservices/test/testSubscriptionCommerceCartService.impex", "utf-8");
		importCsv("/configurablebundleservices/test/testBundleCommerceCartService.impex", "utf-8");
		importCsv("/configurablebundleservices/test/testApproveAllBundleTemplates.impex", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for BundleTemplateRemoveInterceptorTest " + (System.currentTimeMillis() - startTime) + "ms");

		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
		parentTemplate = bundleTemplateService.getBundleTemplateForCode("SyncPackage");
		bundleStatus = parentTemplate.getStatus();
	}

	@Test
	public void testRemoveParentTemplate() throws InterceptorException
	{
		assertEquals(3, parentTemplate.getChildTemplates().size());
		BundleTemplateModel planComponent = bundleTemplateService.getBundleTemplateForCode("SyncPlanSelection");
		assertNotNull(planComponent);

		modelService.remove(parentTemplate);

		thrown.expect(ModelNotFoundException.class);
		thrown.expectMessage("No result for the given query");

		planComponent = bundleTemplateService.getBundleTemplateForCode("SyncPlanSelection");
	}

	@Test
	public void testRemoveChildTemplate() throws InterceptorException
	{
		assertEquals(3, parentTemplate.getChildTemplates().size());
		final BundleTemplateModel deviceComponent = bundleTemplateService.getBundleTemplateForCode("SyncDeviceSelection");
		assertNotNull(deviceComponent);
		BundleTemplateModel planComponent = bundleTemplateService.getBundleTemplateForCode("SyncPlanSelection");
		assertNotNull(planComponent);

		modelService.remove(deviceComponent);

		parentTemplate = bundleTemplateService.getBundleTemplateForCode("SyncPackage");
		assertNotNull(parentTemplate);
		planComponent = bundleTemplateService.getBundleTemplateForCode("SyncPlanSelection");
		assertNotNull(planComponent);
	}

	@Test
	public void testRemoveBundleTemplateCheckBundleStatus() throws InterceptorException
	{
		final PK pk = bundleStatus.getPk();
		modelService.remove(parentTemplate);

		thrown.expect(ModelLoadingException.class);
		thrown.expectMessage("No item found for given pk");

		modelService.get(pk);
	}
}
