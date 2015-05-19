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
package de.hybris.platform.configurablebundleservices.bundle.impl;

import static de.hybris.platform.configurablebundleservices.bundle.impl.DefaultBundleCommerceCartService.NEW_BUNDLE;
import static de.hybris.platform.configurablebundleservices.bundle.impl.DefaultBundleCommerceCartService.NO_BUNDLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link FindBundlePricingWithCurrentPriceFactoryStrategy} and
 * {@link DefaultBundleRuleService}
 */
@IntegrationTest
public class DefaultBundleRuleServiceIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(DefaultBundleRuleServiceIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private BundleCommerceCartService bundleCommerceCartService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ProductService productService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private BundleTemplateService bundleTemplateService;

	@Resource
	private ModelService modelService;

	@Resource
	private BaseSiteService baseSiteService;

	private BundleRuleService bundleRuleService = new DefaultBundleRuleService();

	private ProductModel galaxynexus;
	private ProductModel galaxys2;
	private ProductModel iphone4s16gb;
	private ProductModel standardplan1y;
	private ProductModel standardplan2y;
	private ProductModel standardplan3y;
	private ProductModel activationfee;
	private ProductModel htcIncredibleS;
	private ProductModel motorolaRazr;
	private ProductModel product3417130;
	private ProductModel yStarter300_2y;
	private ProductModel yStarter200_1y;

	private UnitModel unitModel;

	private BundleTemplateModel smartPhonePlanBundleTemplate;
	private BundleTemplateModel smartPhoneDeviceBundleTemplate;
	private BundleTemplateModel iPhoneDeviceSelectionBundleTemplate;
	private BundleTemplateModel payAsYouGoDeviceSelection;
	private BundleTemplateModel payAsYouGoPlanSelection;
	private BundleTemplateModel payAsYouGoAddOnSelection;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultBundleRuleServiceIntegrationTest ...");
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

		LOG.info("Finished data for DefaultBundleRuleServiceIntegrationTest " + (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		galaxynexus = productService.getProductForCode("GALAXY_NEXUS");
		galaxys2 = productService.getProductForCode("GALAXY_S2");
		iphone4s16gb = productService.getProductForCode("APPLE_IPHONE_4S_16GB");
		standardplan1y = productService.getProductForCode("PLAN_STANDARD_1Y");
		standardplan2y = productService.getProductForCode("PLAN_STANDARD_2Y");
		standardplan3y = productService.getProductForCode("PLAN_STANDARD_3Y");
		activationfee = productService.getProductForCode("ACTIVATION_FEE");
		htcIncredibleS = productService.getProductForCode("HTC_INCREDIBLE_S");
		motorolaRazr = productService.getProductForCode("MOTOROLA_RAZR");
		product3417130 = productService.getProductForCode("3417130");
		yStarter300_2y = productService.getProductForCode("Y_STARTER_300_2Y");
		yStarter200_1y = productService.getProductForCode("Y_STARTER_200_1Y");

		smartPhonePlanBundleTemplate = bundleTemplateService.getBundleTemplateForCode("SmartPhonePlanSelection");
		smartPhoneDeviceBundleTemplate = bundleTemplateService.getBundleTemplateForCode("SmartPhoneDeviceSelection");
		iPhoneDeviceSelectionBundleTemplate = bundleTemplateService.getBundleTemplateForCode("IPhoneDeviceSelection");
		payAsYouGoDeviceSelection = bundleTemplateService.getBundleTemplateForCode("PayAsYouGoDeviceSelection");
		payAsYouGoPlanSelection = bundleTemplateService.getBundleTemplateForCode("PayAsYouGoPlanSelection");
		payAsYouGoAddOnSelection = bundleTemplateService.getBundleTemplateForCode("PayAsYouGoAddOnSelection");

		final DefaultBundleRuleService defaultBundleRuleService = (DefaultBundleRuleService) Registry.getApplicationContext()
				.getBean("defaultBundleRuleService");
		setBundleRuleService(defaultBundleRuleService);

		modelService.detachAll();
	}

	@Test
	public void testAdd2CartWithSmartPhonePackage() throws CommerceCartModificationException, CalculationException
	{
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals(1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		AbstractOrderEntryModel planEntry;
		AbstractOrderEntryModel planEntry2;
		AbstractOrderEntryModel productEntry;
		ChangeProductPriceBundleRuleModel priceRule;

		// add standalone
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1,
				unitModel, false, NO_BUNDLE, null, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		final UnitModel entryUnit = productEntry.getUnit();
		assertPriceAfterCalculation(600, productEntry);
		assertEquals(Double.valueOf(600), telcoMasterCart.getTotalPrice());
		assertEquals(1, telcoMasterCart.getEntries().size());

		//create bundle 1
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1, entryUnit, false, NEW_BUNDLE,
				smartPhoneDeviceBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(650, productEntry); // bundle rule with no conditional products raises price to 650
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(650, priceRule.getPrice().intValue());
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, standardplan1y, 1, entryUnit, false, productEntry
				.getBundleNo().intValue(), smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		planEntry = modifications.iterator().next().getEntry();
		final AbstractOrderModel childCartMonthly = planEntry.getChildEntries().iterator().next().getOrder();
		assertPriceAfterCalculation(300, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(300, priceRule.getPrice().intValue());
		//YTODO assertPriceAfterCalculation(40, planEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(planEntry);
		assertNull(priceRule);
		assertEquals(3, telcoMasterCart.getEntries().size());
		assertEquals(Double.valueOf(900), telcoMasterCart.getTotalPrice());
		assertEquals(Double.valueOf(40), childCartMonthly.getTotalPrice());

		//create bundle 2
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1, entryUnit, false, NEW_BUNDLE,
				smartPhoneDeviceBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(650, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(650, priceRule.getPrice().intValue());
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, standardplan2y, 1, entryUnit, false, productEntry
				.getBundleNo().intValue(), smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		planEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(200, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(200, priceRule.getPrice().intValue());
		//YTODO assertPriceAfterCalculation(40, planEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(planEntry);
		assertNull(priceRule);
		assertEquals(5, telcoMasterCart.getEntries().size());
		assertEquals(Double.valueOf(1100), telcoMasterCart.getTotalPrice());
		assertEquals(Double.valueOf(80), childCartMonthly.getTotalPrice());

		//create bundle 3
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1, unitModel, false, NEW_BUNDLE,
				smartPhoneDeviceBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(650, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(650, priceRule.getPrice().intValue());
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, standardplan3y, 1, unitModel, false, productEntry
				.getBundleNo().intValue(), smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		planEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(100, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(100, priceRule.getPrice().intValue());
		// YTODO assertPriceAfterCalculation(40, planEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(planEntry);
		assertNull(priceRule);
		assertEquals(7, telcoMasterCart.getEntries().size());
		assertEquals(Double.valueOf(1200), telcoMasterCart.getTotalPrice());
		assertEquals(Double.valueOf(120), childCartMonthly.getTotalPrice());

		//create bundle 4
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1, unitModel, false, NEW_BUNDLE,
				smartPhoneDeviceBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		final int bundleNo4 = productEntry.getBundleNo().intValue();
		assertPriceAfterCalculation(650, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(650, priceRule.getPrice().intValue());
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, standardplan3y, 1, unitModel, false, productEntry
				.getBundleNo().intValue(), smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		planEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(100, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(100, priceRule.getPrice().intValue());
		// YTODO assertPriceAfterCalculation(40, planEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(planEntry);
		assertNull(priceRule);
		// activation fee makes smartphone and plan cheaper
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, activationfee, 1, unitModel, false, productEntry
				.getBundleNo().intValue(), smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		planEntry2 = modifications.iterator().next().getEntry();
		final AbstractOrderModel childCartOnFirstBill = planEntry2.getChildEntries().iterator().next().getOrder();
		assertPriceAfterCalculation(50, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(50, priceRule.getPrice().intValue());
		// YTODO assertPriceAfterCalculation(30, planEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(planEntry);
		assertEquals(30, priceRule.getPrice().intValue());
		// YTODO assertPriceAfterCalculation(15, planEntry2);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(planEntry2);
		assertNull(priceRule);
		assertEquals(10, telcoMasterCart.getEntries().size());
		assertEquals(Double.valueOf(1250), telcoMasterCart.getTotalPrice());
		assertEquals(Double.valueOf(150), childCartMonthly.getTotalPrice());
		assertEquals(Double.valueOf(15), childCartOnFirstBill.getTotalPrice());
		// remove activation fee again
		bundleCommerceCartService.updateQuantityForCartEntry(telcoMasterCart, planEntry2.getEntryNumber().longValue(), 0);
		assertPriceAfterCalculation(100, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(100, priceRule.getPrice().intValue());
		// YTODO assertPriceAfterCalculation(40, planEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(planEntry);
		assertNull(priceRule);

		assertEquals(9, telcoMasterCart.getEntries().size());
		assertEquals(Double.valueOf(1300), telcoMasterCart.getTotalPrice());
		assertEquals(Double.valueOf(160), childCartMonthly.getTotalPrice());

		// add standalone (same as above), updates quantity of existing standalone
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1, entryUnit, false, NO_BUNDLE, null,
				false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();

		assertPriceAfterCalculation(1200, productEntry);
		assertEquals(9, telcoMasterCart.getEntries().size());
		assertEquals(Double.valueOf(1900), telcoMasterCart.getTotalPrice());
		assertEquals(Double.valueOf(160), childCartMonthly.getTotalPrice());

		// remove bundle 4
		bundleCommerceCartService.removeAllEntries(telcoMasterCart, bundleNo4);

		assertEquals(7, telcoMasterCart.getEntries().size());
		assertEquals(Double.valueOf(1800), telcoMasterCart.getTotalPrice());
		assertEquals(Double.valueOf(120), childCartMonthly.getTotalPrice());

	}

	@Test
	public void testAdd2CartWithSmartPhonePackageAndStandalone() throws CommerceCartModificationException, CalculationException
	{
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals(1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		// YTODO AbstractOrderEntryModel planEntry;
		// YTODO AbstractOrderEntryModel planEntry2;
		AbstractOrderEntryModel productEntry;

		//create bundle 1
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1,
				unitModel, false, NEW_BUNDLE, smartPhoneDeviceBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		final UnitModel entryUnit = productEntry.getUnit();
		assertPriceAfterCalculation(650, productEntry);
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, standardplan3y, 1, entryUnit, false, productEntry
				.getBundleNo().intValue(), smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		// YTODO planEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(100, productEntry);
		// YTODO assertPriceAfterCalculation(40, planEntry);
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, activationfee, 1, entryUnit, false, productEntry
				.getBundleNo().intValue(), smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		// YTODO planEntry2 = modifications.iterator().next().getEntry();
		// YTODO assertPriceAfterCalculation(50, productEntry);
		// YTODO assertPriceAfterCalculation(30, planEntry);
		// YTODO assertPriceAfterCalculation(15, planEntry2);

		assertEquals(Double.valueOf(50), telcoMasterCart.getTotalPrice());

		// add standalone (same product as in bundle above): it must not update the bundle entry but create a new cart entry
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1, entryUnit, false, NO_BUNDLE, null,
				false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(600, productEntry);

		// add standalone (same product as in bundle above): it should update the other standalone entry
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1, entryUnit, false, NO_BUNDLE, null,
				false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		assertPriceAfterCalculation(1200, productEntry);

		assertEquals(Double.valueOf(1250), telcoMasterCart.getTotalPrice());
	}

	@Test
	public void testAdd2Cart2Products() throws CommerceCartModificationException, CalculationException
	{
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals(1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		AbstractOrderEntryModel planEntry = null;
		AbstractOrderEntryModel productEntry = null;
		ChangeProductPriceBundleRuleModel priceRule = null;

		// add standalone
		final List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart, galaxynexus, 1,
				unitModel, false, NO_BUNDLE, null, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		productEntry = modifications.iterator().next().getEntry();
		final UnitModel entryUnit = productEntry.getUnit();
		assertPriceAfterCalculation(600, productEntry);
		assertEquals(Double.valueOf(600), telcoMasterCart.getTotalPrice());
		assertEquals(1, telcoMasterCart.getEntries().size());
		assertEquals(0, telcoMasterCart.getChildren().size());

		//create bundle 1
		final List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(telcoMasterCart, entryUnit, NEW_BUNDLE,
				galaxynexus, smartPhoneDeviceBundleTemplate, standardplan1y, smartPhonePlanBundleTemplate, "<no xml>", "<no xml>");

		assertEquals(2, mods.size());
		for (final CommerceCartModification mod : mods)
		{
			assertNotNull(mod.getEntry());
			if (galaxynexus.equals(mod.getEntry().getProduct()))
			{
				productEntry = mod.getEntry();
			}
			else if (standardplan1y.equals(mod.getEntry().getProduct()))
			{
				planEntry = mod.getEntry();
			}
			else
			{
				Assert.fail("unknown products");
			}
		}
		assertEquals(3, telcoMasterCart.getEntries().size());
		assertEquals(Double.valueOf(900), telcoMasterCart.getTotalPrice());
		assertEquals(1, telcoMasterCart.getChildren().size());
		final AbstractOrderModel childCartMonthly = telcoMasterCart.getChildren().iterator().next();
		assertEquals(Double.valueOf(40), childCartMonthly.getTotalPrice());

		assertPriceAfterCalculation(300, productEntry);
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(productEntry);
		assertEquals(300, priceRule.getPrice().intValue());

		assertPriceAfterCalculation(40, childCartMonthly.getEntries().iterator().next());
		priceRule = getBundleRuleService().getChangePriceBundleRuleForOrderEntry(planEntry);
		assertNull(priceRule);

	}

	@Test
	public void testGetChangePriceBundleRule()
	{
		final CurrencyModel currency = commonI18NService.getCurrency("USD");

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				smartPhoneDeviceBundleTemplate, galaxynexus, standardplan1y, currency);
		assertNotNull(priceRule);
		assertEquals(300, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		priceRule = getBundleRuleService().getChangePriceBundleRule(smartPhonePlanBundleTemplate, standardplan1y, galaxynexus,
				currency);
		assertNull(priceRule);

		priceRule = getBundleRuleService().getChangePriceBundleRule(smartPhoneDeviceBundleTemplate, galaxynexus, standardplan3y,
				currency);
		assertNotNull(priceRule);
		assertEquals(100, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		priceRule = getBundleRuleService().getChangePriceBundleRule(smartPhonePlanBundleTemplate, standardplan3y, activationfee,
				currency);
		assertNotNull(priceRule);
		assertEquals(30, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		priceRule = getBundleRuleService().getChangePriceBundleRule(smartPhonePlanBundleTemplate, standardplan3y, galaxys2,
				currency);
		assertNotNull(priceRule);
		assertEquals(9, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	@Test
	public void testGetChangePriceBundleRuleALLvsANY()
	{
		final CurrencyModel currency = commonI18NService.getCurrency("USD");

		// rule price_NON_IPHONE_with_PLAN_STANDARD_3Y (100 USD) is picked
		// rule price_NON_IPHONE_with_PLAN_STANDARD_3Y_ACTIVATION_FEE (50 USD) cannot be selected because its type is ALL and the activation fee is missing
		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(
				smartPhoneDeviceBundleTemplate, galaxys2, standardplan3y, currency);
		assertNotNull(priceRule);
		assertEquals(100, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		// rule price_IPHONE_with_PLAN_STANDARD_3Y (275 USD) is not selected because there is a cheaper price
		// price_IPHONE_with_PLAN_STANDARD_3Y_ACTIVATION_FEE (175 USD) is picked although the activation fee is missing, but it's an ANY rule
		priceRule = getBundleRuleService().getChangePriceBundleRule(iPhoneDeviceSelectionBundleTemplate, iphone4s16gb,
				standardplan3y, currency);
		assertNotNull(priceRule);
		assertEquals(175, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		// same as above but this time the standardplan3y is missing
		priceRule = getBundleRuleService().getChangePriceBundleRule(iPhoneDeviceSelectionBundleTemplate, iphone4s16gb,
				activationfee, currency);
		assertNotNull(priceRule);
		assertEquals(175, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

	}

	@Test
	public void testGetDisableRuleForBundleProductWhenBundleExists() throws CommerceCartModificationException
	{
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals(1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		List<CommerceCartModification> mods = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				htcIncredibleS, payAsYouGoDeviceSelection, yStarter300_2y, payAsYouGoPlanSelection, "<no xml>", "<no xml>");
		assertEquals(4, mods.size());
		final int bundleNo1 = mods.iterator().next().getEntry().getBundleNo().intValue();

		// Rules allow that 2 htc's or 2 motorola's are added to the bundle, but not a htc in combination with a motorola or vice versa
		// MOTOROLA_RAZR as addon (should not be possible as htc is already in bundle)
		DisableProductBundleRuleModel disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart,
				motorolaRazr, payAsYouGoAddOnSelection, bundleNo1, false);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(motorolaRazr));
		assertTrue(disableRule.getConditionalProducts().contains(htcIncredibleS));

		// add another htc as addon (should be possible)
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, htcIncredibleS,
				payAsYouGoAddOnSelection, bundleNo1, false);
		assertNull(disableRule);


		// new bundle with motorolaRazr
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, motorolaRazr, 1, unitModel, false, NEW_BUNDLE,
				payAsYouGoDeviceSelection, false, "<no xml>");
		assertEquals(3, mods.size());
		final int bundleNo2 = mods.iterator().next().getEntry().getBundleNo().intValue();

		// cheap plan not possible with motorolaRazr
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, yStarter200_1y,
				payAsYouGoPlanSelection, bundleNo2, false);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(motorolaRazr));
		assertTrue(disableRule.getConditionalProducts().contains(yStarter200_1y));

		// new bundle with cheap plan
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, yStarter200_1y, 1, unitModel, false, NEW_BUNDLE,
				payAsYouGoPlanSelection, false, "<no xml>");
		assertEquals(3, mods.size());
		final int bundleNo3 = mods.iterator().next().getEntry().getBundleNo().intValue();

		// motorolaRazr not possible with cheap plan
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, motorolaRazr,
				payAsYouGoDeviceSelection, bundleNo3, false);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(motorolaRazr));
		assertTrue(disableRule.getConditionalProducts().contains(yStarter200_1y));

		//new bundle with cheap plan
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, yStarter200_1y, 1, unitModel, false, NEW_BUNDLE,
				payAsYouGoPlanSelection, false, "<no xml>");
		assertEquals(3, mods.size());
		final int bundleNo4 = mods.iterator().next().getEntry().getBundleNo().intValue();

		// check adding htc (should be possible), but do not actually add it to cart 
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, htcIncredibleS,
				payAsYouGoAddOnSelection, bundleNo4, false);
		assertNull(disableRule);

		// check and add product3417130 as addon (should be possible)
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, product3417130,
				payAsYouGoAddOnSelection, bundleNo4, false);
		assertNull(disableRule);
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, product3417130, 1, unitModel, false, bundleNo4,
				payAsYouGoAddOnSelection, false, "<no xml>");
		assertEquals(1, mods.size());
		// check adding htc (should not be possible any more due to cheap plan and product3417130 in bundle)
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, htcIncredibleS,
				payAsYouGoAddOnSelection, bundleNo4, false);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(htcIncredibleS));
		assertTrue(disableRule.getConditionalProducts().contains(yStarter200_1y));
		assertTrue(disableRule.getConditionalProducts().contains(product3417130));

		// check adding htc again (this time it should be possible as we ignore product3417130 in add-on component)
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, htcIncredibleS,
				payAsYouGoAddOnSelection, bundleNo4, true);
		assertNull(disableRule);

		//new bundle with cheap plan and htc
		mods = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE, htcIncredibleS,
				payAsYouGoDeviceSelection, yStarter200_1y, payAsYouGoPlanSelection, "<no xml>", "<no xml>");
		assertEquals(4, mods.size());
		final int bundleNo5 = mods.iterator().next().getEntry().getBundleNo().intValue();
		// check adding product3417130 (should not be possible due to cheap plan and htc in bundle)
		// same as test case before but the devices are added the other way round so that target and conditional products are added in a different sequence
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, product3417130,
				payAsYouGoAddOnSelection, bundleNo5, false);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(htcIncredibleS));
		assertTrue(disableRule.getConditionalProducts().contains(yStarter200_1y));
		assertTrue(disableRule.getConditionalProducts().contains(product3417130));

		// setting the "ignoreCurrentProducts" flag does not make a difference as the product that needs to be ignored is in the device component not in the current add-on component
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, product3417130,
				payAsYouGoAddOnSelection, bundleNo5, true);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(htcIncredibleS));
		assertTrue(disableRule.getConditionalProducts().contains(yStarter200_1y));
		assertTrue(disableRule.getConditionalProducts().contains(product3417130));

		// if adding to the device component, the "ignoreCurrentProducts" flag makes the difference
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, product3417130,
				payAsYouGoDeviceSelection, bundleNo5, false);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(htcIncredibleS));
		assertTrue(disableRule.getConditionalProducts().contains(yStarter200_1y));
		assertTrue(disableRule.getConditionalProducts().contains(product3417130));

		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(telcoMasterCart, product3417130,
				payAsYouGoDeviceSelection, bundleNo5, true);
		assertNull(disableRule);
	}

	@Test
	public void testGetDisableRuleForBundleProduct()
	{
		// motorolaRazr with expensive plan is allowed
		DisableProductBundleRuleModel disableRule = getBundleRuleService().getDisableRuleForBundleProduct(
				payAsYouGoDeviceSelection, motorolaRazr, yStarter300_2y);
		assertNull(disableRule);

		// motorolaRazr not allowed with cheap plan
		disableRule = getBundleRuleService()
				.getDisableRuleForBundleProduct(payAsYouGoDeviceSelection, motorolaRazr, yStarter200_1y);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(motorolaRazr));
		assertTrue(disableRule.getConditionalProducts().contains(yStarter200_1y));
		// same as above, but product parameters sequence switched
		disableRule = getBundleRuleService()
				.getDisableRuleForBundleProduct(payAsYouGoDeviceSelection, yStarter200_1y, motorolaRazr);
		assertNotNull(disableRule);
		assertTrue(disableRule.getTargetProducts().contains(motorolaRazr));
		assertTrue(disableRule.getConditionalProducts().contains(yStarter200_1y));

		// htcIncredibleS with cheap plan is allowed as the disable rule PayAsYouGo_DisableHtcWith3417130AndCheapPlan is of type ALL and also needs product 3417130 to be applicable
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(payAsYouGoDeviceSelection, htcIncredibleS,
				yStarter200_1y);
		assertNull(disableRule);
		// same as above, but product parameters sequence switched
		disableRule = getBundleRuleService().getDisableRuleForBundleProduct(payAsYouGoDeviceSelection, yStarter200_1y,
				htcIncredibleS);
		assertNull(disableRule);

	}

	@Test
	public void testGetChangePriceBundleRuleWhenBundleExists() throws CommerceCartModificationException, CalculationException
	{
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals(1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		// bundle does not exist yet
		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRule(telcoMasterCart,
				smartPhoneDeviceBundleTemplate, galaxynexus, 1);
		assertNotNull(priceRule);
		assertEquals(650, priceRule.getPrice().intValue());

		// create bundle with plan product
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart, standardplan3y, 1,
				unitModel, false, NEW_BUNDLE, smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());
		final AbstractOrderEntryModel planEntry = modifications.iterator().next().getEntry();
		final int bundleNo = planEntry.getBundleNo().intValue();

		priceRule = getBundleRuleService().getChangePriceBundleRule(telcoMasterCart, smartPhoneDeviceBundleTemplate, galaxynexus,
				bundleNo);
		assertNotNull(priceRule);
		assertEquals(100, priceRule.getPrice().intValue());
		//YTODO assertEquals(planEntry.getMasterAbstractOrder().getCurrency(), priceRule.getCurrency());

		// add activation fee which makes device even cheaper
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, activationfee, 1, unitModel, false, bundleNo,
				smartPhonePlanBundleTemplate, false, "<no xml>");
		assertEquals(1, modifications.size());

		priceRule = getBundleRuleService().getChangePriceBundleRule(telcoMasterCart, smartPhoneDeviceBundleTemplate, galaxynexus,
				bundleNo);
		assertNotNull(priceRule);
		assertEquals(50, priceRule.getPrice().intValue());
		//YTODO assertEquals(planEntry.getMasterAbstractOrder().getCurrency(), priceRule.getCurrency());

	}

	@Test
	public void testGetChangePriceBundleRuleWithLowestPrice()
	{
		final CurrencyModel currency = commonI18NService.getCurrency("USD");

		ChangeProductPriceBundleRuleModel priceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(galaxynexus,
				currency);
		assertNotNull(priceRule);
		assertEquals("price_NON_IPHONE_with_PLAN_STANDARD_3Y_ACTIVATION_FEE", priceRule.getId());
		assertEquals(50, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		priceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(standardplan1y, currency);
		assertNull(priceRule);

		priceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(standardplan3y, currency);
		assertNotNull(priceRule);
		assertEquals("price_NON_IPHONE_with_FANCY_GALAXY_S2", priceRule.getId());
		assertEquals(9, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());

		priceRule = getBundleRuleService().getChangePriceBundleRuleWithLowestPrice(galaxys2, currency);
		assertNotNull(priceRule);
		assertEquals("price_NON_IPHONE_with_PLAN_STANDARD_3Y_ACTIVATION_FEE", priceRule.getId());
		assertEquals(50, priceRule.getPrice().intValue());
		assertEquals(currency, priceRule.getCurrency());
	}

	private void assertPriceAfterCalculation(final double expectedPrice, final AbstractOrderEntryModel orderEntry)
			throws CalculationException
	{
		Double foundPrice = null;
		final AbstractOrderModel cartModel = orderEntry.getOrder();

		for (final AbstractOrderEntryModel curOrderEntry : cartModel.getEntries())
		{
			if (curOrderEntry.getEntryNumber().equals(orderEntry.getEntryNumber()))
			{
				foundPrice = curOrderEntry.getTotalPrice();
				break;
			}
		}

		if (foundPrice == null)
		{
			Assert.fail("No price found for : " + orderEntry.getOrder().getCode() + "/" + orderEntry.getEntryNumber());
		}

		assertEquals(Double.valueOf(expectedPrice), foundPrice);
	}

	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}
}
