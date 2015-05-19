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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.daos.ChangeProductPriceBundleRuleDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
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
 * Integration test suite for {@link DefaultChangeProductPriceBundleRuleDao}
 * 
 */
@IntegrationTest
public class DefaultChangeProductPriceBundleRuleDaoIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultChangeProductPriceBundleRuleDaoIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ChangeProductPriceBundleRuleDao changeProductPriceBundleRuleDao;

	@Resource
	private ProductService productService;

	@Resource
	private BundleTemplateService bundleTemplateService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	private ProductModel galaxynexus;
	private ProductModel standardplan1y;
	private ProductModel standardplan3y;
	private BundleTemplateModel smartPhonePlanBundleTemplate;
	private BundleTemplateModel smartPhoneDeviceBundleTemplate;
	private CurrencyModel currencyUSD;
	private CurrencyModel currencyEUR;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultChangeProductPriceBundleRuleDaoIntegrationTest ..");
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

		LOG.info("Finished data for DefaultChangeProductPriceBundleRuleDaoIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		currencyUSD = commonI18NService.getCurrency("USD");
		currencyEUR = commonI18NService.getCurrency("EUR");
		galaxynexus = productService.getProductForCode("GALAXY_NEXUS");
		standardplan1y = productService.getProductForCode("PLAN_STANDARD_1Y");
		standardplan3y = productService.getProductForCode("PLAN_STANDARD_3Y");
		smartPhonePlanBundleTemplate = bundleTemplateService.getBundleTemplateForCode("SmartPhonePlanSelection");
		smartPhoneDeviceBundleTemplate = bundleTemplateService.getBundleTemplateForCode("SmartPhoneDeviceSelection");

		modelService.detachAll();
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndTemplate()
	{
		final List<ChangeProductPriceBundleRuleModel> priceRulesDevice = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndTemplate(galaxynexus, smartPhoneDeviceBundleTemplate);

		assertEquals(6, priceRulesDevice.size());
		for (final ChangeProductPriceBundleRuleModel priceRule : priceRulesDevice)
		{
			Assert.assertEquals(smartPhoneDeviceBundleTemplate, priceRule.getBundleTemplate());
			assertNotNull(priceRule.getTargetProducts());
			assertTrue(priceRule.getTargetProducts().contains(galaxynexus));
		}

		final List<ChangeProductPriceBundleRuleModel> priceRulesPlan = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndTemplate(standardplan1y, smartPhonePlanBundleTemplate);
		assertEquals(0, priceRulesPlan.size());

		final List<ChangeProductPriceBundleRuleModel> priceRulesPlan2 = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndTemplate(standardplan3y, smartPhonePlanBundleTemplate);

		assertEquals(2, priceRulesPlan2.size());
		for (final ChangeProductPriceBundleRuleModel priceRule : priceRulesPlan2)
		{
			assertEquals(smartPhonePlanBundleTemplate, priceRule.getBundleTemplate());
			assertNotNull(priceRule.getTargetProducts());
			assertTrue(priceRule.getTargetProducts().contains(standardplan3y));
		}
	}

	@Test
	public void testFindPriceBundleRulesByTargetProductAndTemplateAndCurrency()
	{
		final List<ChangeProductPriceBundleRuleModel> priceRulesDevice = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndTemplateAndCurrency(galaxynexus, smartPhoneDeviceBundleTemplate, currencyUSD);

		assertEquals(5, priceRulesDevice.size());
		for (final ChangeProductPriceBundleRuleModel priceRule : priceRulesDevice)
		{
			Assert.assertEquals(smartPhoneDeviceBundleTemplate, priceRule.getBundleTemplate());
			assertNotNull(priceRule.getTargetProducts());
			assertTrue(priceRule.getTargetProducts().contains(galaxynexus));
		}

		final List<ChangeProductPriceBundleRuleModel> priceRulesPlan = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndTemplateAndCurrency(standardplan1y, smartPhonePlanBundleTemplate, currencyUSD);
		assertEquals(0, priceRulesPlan.size());

		final List<ChangeProductPriceBundleRuleModel> priceRulesPlan2 = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndTemplateAndCurrency(standardplan3y, smartPhonePlanBundleTemplate, currencyUSD);

		assertEquals(2, priceRulesPlan2.size());
		for (final ChangeProductPriceBundleRuleModel priceRule : priceRulesPlan2)
		{
			assertEquals(smartPhonePlanBundleTemplate, priceRule.getBundleTemplate());
			assertNotNull(priceRule.getTargetProducts());
			assertTrue(priceRule.getTargetProducts().contains(standardplan3y));
		}

		final List<ChangeProductPriceBundleRuleModel> priceRulesDeviceEUR = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndTemplateAndCurrency(galaxynexus, smartPhoneDeviceBundleTemplate, currencyEUR);

		assertEquals(1, priceRulesDeviceEUR.size());
		assertTrue(priceRulesDeviceEUR.iterator().next().getTargetProducts().contains(galaxynexus));

	}

	@Test
	public void testFindBundleRulesByTargetProduct()
	{
		final List<ChangeProductPriceBundleRuleModel> priceRulesDevice = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProduct(galaxynexus);

		assertEquals(6, priceRulesDevice.size());
		for (final ChangeProductPriceBundleRuleModel priceRule : priceRulesDevice)
		{
			assertNotNull(priceRule.getTargetProducts());
			assertTrue(priceRule.getTargetProducts().contains(galaxynexus));
		}

		final List<ChangeProductPriceBundleRuleModel> priceRulesPlan = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProduct(standardplan1y);
		assertEquals(0, priceRulesPlan.size());

		final List<ChangeProductPriceBundleRuleModel> priceRulesPlan2 = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProduct(standardplan3y);

		assertEquals(2, priceRulesPlan2.size());
		for (final ChangeProductPriceBundleRuleModel priceRule : priceRulesPlan2)
		{
			assertNotNull(priceRule.getTargetProducts());
			assertTrue(priceRule.getTargetProducts().contains(standardplan3y));
		}
	}

	@Test
	public void testFindBundleRulesByTargetProductAndCurrency()
	{
		final List<ChangeProductPriceBundleRuleModel> priceRulesDevice = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndCurrency(galaxynexus, currencyUSD);

		assertEquals(5, priceRulesDevice.size());
		for (final ChangeProductPriceBundleRuleModel priceRule : priceRulesDevice)
		{
			assertNotNull(priceRule.getTargetProducts());
			assertTrue(priceRule.getTargetProducts().contains(galaxynexus));
		}

		final List<ChangeProductPriceBundleRuleModel> priceRulesPlan = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndCurrency(standardplan1y, currencyUSD);
		assertEquals(0, priceRulesPlan.size());

		final List<ChangeProductPriceBundleRuleModel> priceRulesPlan2 = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndCurrency(standardplan3y, currencyUSD);

		assertEquals(2, priceRulesPlan2.size());
		for (final ChangeProductPriceBundleRuleModel priceRule : priceRulesPlan2)
		{
			assertNotNull(priceRule.getTargetProducts());
			assertTrue(priceRule.getTargetProducts().contains(standardplan3y));
		}

		final List<ChangeProductPriceBundleRuleModel> priceRulesDeviceEUR = changeProductPriceBundleRuleDao
				.findBundleRulesByTargetProductAndCurrency(galaxynexus, currencyEUR);

		assertEquals(1, priceRulesDeviceEUR.size());
		assertTrue(priceRulesDeviceEUR.iterator().next().getTargetProducts().contains(galaxynexus));
	}

}
