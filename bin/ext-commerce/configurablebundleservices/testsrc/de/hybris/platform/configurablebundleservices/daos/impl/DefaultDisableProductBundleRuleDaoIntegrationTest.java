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
package de.hybris.platform.configurablebundleservices.daos.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.daos.BundleRuleDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link DefaultDisableProductBundleRuleDao}
 * 
 */
@IntegrationTest
public class DefaultDisableProductBundleRuleDaoIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultDisableProductBundleRuleDaoIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BundleRuleDao disableProductBundleRuleDao;

	@Resource
	private ProductService productService;

	@Resource
	private BundleTemplateService bundleTemplateService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	private ProductModel galaxynexus;
	private ProductModel htcIncredibleS;
	private BundleTemplateModel smartPhoneDeviceBundleTemplate;
	private BundleTemplateModel payAsYouGoAddOnSelection;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultDisableProductBundleRuleDaoIntegrationTest ..");
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

		LOG.info("Finished data for DefaultDisableProductBundleRuleDaoIntegrationTest " + (System.currentTimeMillis() - startTime)
				+ "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		galaxynexus = productService.getProductForCode("GALAXY_NEXUS");
		htcIncredibleS = productService.getProductForCode("HTC_INCREDIBLE_S");
		payAsYouGoAddOnSelection = bundleTemplateService.getBundleTemplateForCode("PayAsYouGoAddOnSelection");
		smartPhoneDeviceBundleTemplate = bundleTemplateService.getBundleTemplateForCode("SmartPhoneDeviceSelection");

		modelService.detachAll();
	}

	@Test
	public void testFindDisableProductBundleRulesByProductAndRootTemplate()
	{

		BundleTemplateModel rootTemplate = bundleTemplateService.getRootBundleTemplate(smartPhoneDeviceBundleTemplate);
		List<DisableProductBundleRuleModel> disableRulesDevice = disableProductBundleRuleDao
				.findBundleRulesByProductAndRootTemplate(galaxynexus, rootTemplate);
		Assert.assertEquals(0, disableRulesDevice.size());

		rootTemplate = bundleTemplateService.getRootBundleTemplate(payAsYouGoAddOnSelection);
		disableRulesDevice = disableProductBundleRuleDao.findBundleRulesByProductAndRootTemplate(htcIncredibleS, rootTemplate);
		Assert.assertEquals(3, disableRulesDevice.size());

		for (final DisableProductBundleRuleModel rule : disableRulesDevice)
		{
			if (rule.getConditionalProducts().contains(htcIncredibleS))
			{
				assertFalse(rule.getTargetProducts().contains(htcIncredibleS));
			}
			else if (rule.getTargetProducts().contains(htcIncredibleS))
			{
				assertFalse(rule.getConditionalProducts().contains(htcIncredibleS));
			}
			else
			{
				fail("searched product htcIncredibleS not in result set");
			}

			final BundleTemplateModel ruleTemplate = rule.getBundleTemplate();
			assertEquals(rootTemplate, bundleTemplateService.getRootBundleTemplate(ruleTemplate));
		}
	}
}
