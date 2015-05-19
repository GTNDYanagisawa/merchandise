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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.subscription.impl.DefaultSubscriptionCommerceCartService;
import de.hybris.platform.subscriptionservices.subscription.impl.DefaultSubscriptionCommerceCartServiceIntegrationTest;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link DefaultBundleCommerceCartService}. This test class inherits from
 * {@link DefaultSubscriptionCommerceCartService} in order to run the super class' test cases against the new
 * DefaultBundleCommerceCartService to guarantee compatibility
 */
@IntegrationTest
public class DefaultBundleCommerceCartServiceIntegrationTest extends DefaultSubscriptionCommerceCartServiceIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleCommerceCartServiceIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private BundleCommerceCartService bundleCommerceCartService;

	@Resource
	private UserService userService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private UnitService unitService;

	@Resource
	private CartService cartService;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private BaseSiteService baseSiteService;

	private static final String PRODUCT_MODEL_CODE_GALAXY_NEXUS = "GALAXY_NEXUS";
	private static final String PRODUCT_MODEL_CODE_IPHONE = "APPLE_IPHONE_4S_16GB";
	private static final String PRODUCT_MODEL_CODE_3417125 = "3417125";
	private static final String PRODUCT_MODEL_CODE_3417130 = "3417130";
	private static final String PRODUCT_MODEL_CODE_MOTOROLA_RAZR = "MOTOROLA_RAZR";
	private static final String PRODUCT_MODEL_CODE_HTC_INCREDIBLE_S = "HTC_INCREDIBLE_S";
	private static final String NO_STOCK_PRODUCT_MODEL_CODE = "NO_STOCK_PRODUCT";
	private static final String SUBSCRIPTION_PRODUCT_CODE_MONTHLY = "Y_STARTER_100_1Y";
	private static final String SUBSCRIPTION_PRODUCT_CODE_QUARTERLY = "Y_STARTER_200_2Y";
	private static final String SUBSCRIPTION_PRODUCT_CODE_YEARLY = "Y_STARTER_300_2Y";
	private static final String SUBSCRIPTION_PRODUCT_CODE_PAYNOW = "Y_STARTER_100_2Y";
	private static final String SUBSCRIPTION_PRODUCT_Y_STARTER_200_1Y = "Y_STARTER_200_1Y";
	private static final String ACTIVATION_FEE_PRODUCT_CODE = "ACTIVATION_FEE";
	private static final String SERVICE_FEE_PRODUCT_CODE = "SERVICE_FEE_MONTHLY";

	private ProductModel productModelGalaxyNexus;
	private ProductModel productModelMotorolaRazr;
	private ProductModel productModelHtcIncredibleS;
	private ProductModel productModelIPhone;
	private ProductModel productModel3417125;
	private ProductModel productModel3417130;
	private ProductModel productModelNoStock;
	private SubscriptionProductModel subscriptionProductModelActivationFee;
	private SubscriptionProductModel subscriptionProductModelServiceFeeMonth;
	private SubscriptionProductModel subscriptionProductModelMonthly;
	private SubscriptionProductModel subscriptionProductModelYearly;
	private SubscriptionProductModel subscriptionProductModelPaynow;
	private SubscriptionProductModel subscriptionProductModelQuarterly;
	private SubscriptionProductModel subscriptionProductModelYStarter200_1y;
	private UnitModel unitModel;
	private CartModel telcoMasterCart;


	private BundleTemplateModel bundleSmartPhoneDeviceSelection;
	private BundleTemplateModel bundleSmartPhonePlanSelection;
	private BundleTemplateModel bundleSmartPhoneValuePackSelection2;
	private BundleTemplateModel bundleIPhoneDeviceSelection;
	private BundleTemplateModel bundleIPhonePlanSelection;
	private BundleTemplateModel bundleIPhoneAddonSelection;
	private BundleTemplateModel bundlePayAsYouGoDeviceSelection;
	private BundleTemplateModel bundlePayAsYouGoPlanSelection;
	private BundleTemplateModel bundlePayAsYouGoActivationFee;
	private BundleTemplateModel bundlePayAsYouGoAddOnSelection;
	private BundleTemplateModel bundlePayAsYouGoInternetSelection;

	@Override
	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultBundleCommerceCartServiceIntegrationTest ...");
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

		LOG.info("Finished data for DefaultBundleCommerceCartServiceIntegrationTest " + (System.currentTimeMillis() - startTime)
				+ "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		setupProducts();
		setupBundleTemplates();
		unitModel = productModelGalaxyNexus.getUnit();
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals(1, cartModels.size());
		telcoMasterCart = cartModels.iterator().next();

		modelService.detachAll();
	}

	//@formatter:off
	//	  Multi cart to be created (final):
	//	  
	//	  - master cart (pay now):
	//	    - 2 PRODUCT_MODEL_CODE (Standalone)
	//	    - 1 PRODUCT_MODEL_CODE (bundle 1)
	//     - 1 PRODUCT_MODEL_CODE (bundle 2)
	//     - 1 SUBSCRIPTION_PRODUCT_CODE_PAYNOW (bundle 2)
	//	  - child cart (monthly):
	//	    - 1 SUBSCRIPTION_PRODUCT_CODE_MONTHLY (bundle 1)
	//	  - child cart (yearly):
	//	    - 1 SUBSCRIPTION_PRODUCT_CODE_YEARLY (bundle 1)
	//   - child cart (quarterly):
	//     - 1 SUBSCRIPTION_PRODUCT_CODE_QUARTERLY (bundle 2)
	//	  
	//@formatter:on
	@Test
	public void testAddToCartBundle() throws CommerceCartModificationException
	{
		// Add new entry (device product, standalone)
		bundleCommerceCartService.addToCart(telcoMasterCart, productModelGalaxyNexus, 2, unitModel, false, NO_BUNDLE, null, false,
				"<no xml>");
		assertEquals(1, telcoMasterCart.getEntries().size());
		assertEquals(0, telcoMasterCart.getChildren().size());
		AbstractOrderEntryModel cartEntryModel = telcoMasterCart.getEntries().iterator().next();
		assertEquals(PRODUCT_MODEL_CODE_GALAXY_NEXUS, cartEntryModel.getProduct().getCode());
		assertEquals(unitModel, cartEntryModel.getUnit());
		assertEquals(2, cartEntryModel.getQuantity().longValue());
		assertEquals(Integer.valueOf(0), cartEntryModel.getBundleNo());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(cartEntryModel, telcoMasterCart.getLastModifiedEntries().iterator().next());

		// Add new entry (device product, new bundle (number=1))
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart,
				productModelGalaxyNexus, 1, unitModel, false, NEW_BUNDLE, bundleSmartPhoneDeviceSelection, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		Integer entryNo = modifications.iterator().next().getEntry().getEntryNumber();
		assertEquals(2, telcoMasterCart.getEntries().size());
		assertEquals(0, telcoMasterCart.getChildren().size());
		cartEntryModel = cartService.getEntryForNumber(telcoMasterCart, entryNo.intValue());
		assertNotNull(cartEntryModel);
		assertEquals(PRODUCT_MODEL_CODE_GALAXY_NEXUS, cartEntryModel.getProduct().getCode());
		assertEquals(unitModel, cartEntryModel.getUnit());
		assertEquals(1, cartEntryModel.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), cartEntryModel.getBundleNo());
		assertEquals(bundleSmartPhoneDeviceSelection, cartEntryModel.getBundleTemplate());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(cartEntryModel, telcoMasterCart.getLastModifiedEntries().iterator().next());

		// Add new entry (plan product with billing freq = monthly, existing bundle (number=1))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, true, 1,
				bundleSmartPhonePlanSelection, false, "<no xml>");
		assertEquals(3, telcoMasterCart.getEntries().size());
		assertEquals(1, telcoMasterCart.getChildren().size());
		CartModel childCartModel = (CartModel) telcoMasterCart.getChildren().iterator().next();
		assertEquals(childCartModel.getBillingTime(), subscriptionProductModelMonthly.getSubscriptionTerm().getBillingPlan()
				.getBillingFrequency());
		AbstractOrderEntryModel childCartEntryModel = childCartModel.getEntries().iterator().next();
		assertEquals(SUBSCRIPTION_PRODUCT_CODE_MONTHLY, childCartEntryModel.getProduct().getCode());
		assertEquals(unitModel, childCartEntryModel.getUnit());
		assertEquals(1, childCartEntryModel.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), childCartEntryModel.getMasterEntry().getBundleNo());
		assertEquals(bundleSmartPhonePlanSelection, childCartEntryModel.getMasterEntry().getBundleTemplate());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(childCartEntryModel.getMasterEntry(), telcoMasterCart.getLastModifiedEntries().iterator().next());

		// Add new entry (plan product with billing freq = yearly, existing bundle (number=1))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYearly, 1, unitModel, true, 1,
				bundleSmartPhoneValuePackSelection2, false, "<no xml>");
		assertEquals(4, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());
		childCartModel = getChildCartForBillingTime(telcoMasterCart, subscriptionProductModelYearly.getSubscriptionTerm()
				.getBillingPlan().getBillingFrequency());
		assertNotNull(childCartModel);
		childCartEntryModel = childCartModel.getEntries().iterator().next();
		assertEquals(SUBSCRIPTION_PRODUCT_CODE_YEARLY, childCartEntryModel.getProduct().getCode());
		assertEquals(unitModel, childCartEntryModel.getUnit());
		assertEquals(1, childCartEntryModel.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), childCartEntryModel.getMasterEntry().getBundleNo());
		assertEquals(bundleSmartPhoneValuePackSelection2, childCartEntryModel.getMasterEntry().getBundleTemplate());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(childCartEntryModel.getMasterEntry(), telcoMasterCart.getLastModifiedEntries().iterator().next());

		// Add new entry (device product, new bundle (number=2))
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, productModelIPhone, 1, unitModel, false, NEW_BUNDLE,
				bundleIPhoneDeviceSelection, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		entryNo = modifications.iterator().next().getEntry().getEntryNumber();
		assertEquals(5, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());
		cartEntryModel = cartService.getEntryForNumber(telcoMasterCart, entryNo.intValue());
		assertNotNull(cartEntryModel);
		assertEquals(PRODUCT_MODEL_CODE_IPHONE, cartEntryModel.getProduct().getCode());
		assertEquals(unitModel, cartEntryModel.getUnit());
		assertEquals(1, cartEntryModel.getQuantity().longValue());
		assertEquals(Integer.valueOf(2), cartEntryModel.getBundleNo());
		assertEquals(bundleIPhoneDeviceSelection, cartEntryModel.getBundleTemplate());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(cartEntryModel, telcoMasterCart.getLastModifiedEntries().iterator().next());

		// Add new entry (plan product with billing freq = paynow, existing bundle (number=2))
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelPaynow, 1, unitModel, true, 2,
				bundleIPhoneAddonSelection, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		entryNo = modifications.iterator().next().getEntry().getEntryNumber();
		assertEquals(6, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());
		cartEntryModel = cartService.getEntryForNumber(telcoMasterCart, entryNo.intValue());
		assertNotNull(cartEntryModel);
		assertEquals(SUBSCRIPTION_PRODUCT_CODE_PAYNOW, cartEntryModel.getProduct().getCode());
		assertEquals(unitModel, cartEntryModel.getUnit());
		assertEquals(1, cartEntryModel.getQuantity().longValue());
		assertEquals(Integer.valueOf(2), cartEntryModel.getBundleNo());
		assertEquals(bundleIPhoneAddonSelection, cartEntryModel.getBundleTemplate());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(cartEntryModel, telcoMasterCart.getLastModifiedEntries().iterator().next());

		// Add new entry (plan product with billing freq = quarterly, existing bundle (number=2))
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelQuarterly, 1, unitModel, true,
				2, bundleIPhonePlanSelection, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		entryNo = modifications.iterator().next().getEntry().getEntryNumber();
		assertEquals(7, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());
		childCartModel = getChildCartForBillingTime(telcoMasterCart, subscriptionProductModelQuarterly.getSubscriptionTerm()
				.getBillingPlan().getBillingFrequency());
		assertNotNull(childCartModel);
		cartEntryModel = cartService.getEntryForNumber(telcoMasterCart, entryNo.intValue());
		assertNotNull(cartEntryModel);
		assertEquals(SUBSCRIPTION_PRODUCT_CODE_QUARTERLY, cartEntryModel.getProduct().getCode());
		assertEquals(unitModel, cartEntryModel.getUnit());
		assertEquals(1, cartEntryModel.getQuantity().longValue());
		assertEquals(Integer.valueOf(2), cartEntryModel.getBundleNo());
		assertEquals(bundleIPhonePlanSelection, cartEntryModel.getBundleTemplate());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(cartEntryModel, telcoMasterCart.getLastModifiedEntries().iterator().next());
	}

	@Test
	public void testAddToCart2BundleProducts() throws CommerceCartModificationException
	{
		// Add new entry (device product + subscription product, new bundle (number=1))
		final List<CommerceCartModification> results = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				productModelGalaxyNexus, bundleSmartPhoneDeviceSelection, subscriptionProductModelMonthly,
				bundleSmartPhonePlanSelection, "<no xml>", "<no xml>");

		assertEquals(2, telcoMasterCart.getEntries().size());
		assertEquals(1, telcoMasterCart.getChildren().size());
		assertEquals(2, results.size());
		assertEquals(2, telcoMasterCart.getLastModifiedEntries().size());

		for (final CommerceCartModification mod : results)
		{
			assertNotNull(mod.getEntry());
			assertEquals(Integer.valueOf(1), mod.getEntry().getBundleNo());
			assertEquals(1, mod.getEntry().getQuantity().longValue());
			assertEquals(CommerceCartModificationStatus.SUCCESS, mod.getStatusCode());
			assertTrue(telcoMasterCart.getLastModifiedEntries().contains(mod.getEntry()));

			if (mod.getEntry().getProduct() instanceof SubscriptionProductModel)
			{
				final AbstractOrderModel childCart = mod.getEntry().getChildEntries().iterator().next().getOrder();
				final AbstractOrderEntryModel childCartEntry = mod.getEntry().getChildEntries().iterator().next();
				assertEquals(((SubscriptionProductModel) childCartEntry.getProduct()).getSubscriptionTerm().getBillingPlan()
						.getBillingFrequency(), childCart.getBillingTime());
				assertEquals(SUBSCRIPTION_PRODUCT_CODE_MONTHLY, mod.getEntry().getProduct().getCode());
				assertEquals(bundleSmartPhonePlanSelection, mod.getEntry().getBundleTemplate());
			}
			else
			{
				assertEquals(PRODUCT_MODEL_CODE_GALAXY_NEXUS, mod.getEntry().getProduct().getCode());
				assertEquals(bundleSmartPhoneDeviceSelection, mod.getEntry().getBundleTemplate());
			}
		}

		// Add new entry (device product + subscription product, new bundle (number=2))
		final List<CommerceCartModification> results2 = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				productModelIPhone, bundleIPhoneDeviceSelection, subscriptionProductModelQuarterly, bundleIPhonePlanSelection,
				"<no xml>", "<no xml>");

		assertEquals(4, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());
		assertEquals(2, results2.size());
		assertEquals(2, telcoMasterCart.getLastModifiedEntries().size());

		for (final CommerceCartModification mod : results2)
		{
			assertNotNull(mod.getEntry());
			assertEquals(Integer.valueOf(2), mod.getEntry().getBundleNo());
			assertEquals(1, mod.getEntry().getQuantity().longValue());
			assertEquals(CommerceCartModificationStatus.SUCCESS, mod.getStatusCode());
			assertTrue(telcoMasterCart.getLastModifiedEntries().contains(mod.getEntry()));

			if (mod.getEntry().getProduct() instanceof SubscriptionProductModel)
			{
				final AbstractOrderModel childCart = mod.getEntry().getChildEntries().iterator().next().getOrder();
				final AbstractOrderEntryModel childCartEntry = mod.getEntry().getChildEntries().iterator().next();
				assertEquals(((SubscriptionProductModel) childCartEntry.getProduct()).getSubscriptionTerm().getBillingPlan()
						.getBillingFrequency(), childCart.getBillingTime());
				assertEquals(SUBSCRIPTION_PRODUCT_CODE_QUARTERLY, mod.getEntry().getProduct().getCode());
				assertEquals(bundleIPhonePlanSelection, mod.getEntry().getBundleTemplate());
			}
			else
			{
				assertEquals(PRODUCT_MODEL_CODE_IPHONE, mod.getEntry().getProduct().getCode());
				assertEquals(bundleIPhoneDeviceSelection, mod.getEntry().getBundleTemplate());
			}
		}
	}

	@Test
	public void testAddToCart2BundleProductsNoStock() throws CommerceCartModificationException
	{
		// Add new entry (product (no stock) + subscription product, new bundle (number=1))
		final List<CommerceCartModification> results = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				productModelNoStock, bundleSmartPhoneDeviceSelection, subscriptionProductModelMonthly, bundleSmartPhonePlanSelection,
				"<no xml>", "<no xml>");

		assertEquals(1, telcoMasterCart.getEntries().size());
		assertEquals(1, telcoMasterCart.getChildren().size());
		assertEquals(2, results.size());

		final AbstractOrderModel childCart = telcoMasterCart.getChildren().iterator().next();
		final AbstractOrderEntryModel childCartEntry = childCart.getEntries().iterator().next();
		assertEquals(telcoMasterCart.getEntries().iterator().next().getChildEntries().iterator().next(), childCartEntry);

		for (final CommerceCartModification mod : results)
		{
			if (mod.getQuantityAdded() == 0)
			{
				assertEquals(productModelNoStock, mod.getEntry().getProduct());
				assertEquals(CommerceCartModificationStatus.NO_STOCK, mod.getStatusCode());
			}
			else
			{
				assertEquals(((SubscriptionProductModel) childCartEntry.getProduct()).getSubscriptionTerm().getBillingPlan()
						.getBillingFrequency(), childCart.getBillingTime());
				assertEquals(subscriptionProductModelMonthly, mod.getEntry().getProduct());
				assertEquals(bundleSmartPhonePlanSelection, mod.getEntry().getBundleTemplate());
				assertEquals(Integer.valueOf(1), mod.getEntry().getBundleNo());
				assertEquals(1, mod.getEntry().getQuantity().longValue());
				assertEquals(CommerceCartModificationStatus.SUCCESS, mod.getStatusCode());
			}
		}

		// Add new entry (subscription product + product (no stock), new bundle (number=2))
		final List<CommerceCartModification> results2 = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				subscriptionProductModelQuarterly, bundleIPhonePlanSelection, productModelNoStock, bundleIPhoneDeviceSelection,
				"<no xml>", "<no xml>");

		assertEquals(2, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());
		assertEquals(2, results2.size());

		for (final CommerceCartModification mod : results2)
		{
			if (mod.getQuantityAdded() == 0)
			{
				assertEquals(productModelNoStock, mod.getEntry().getProduct());
				assertEquals(CommerceCartModificationStatus.NO_STOCK, mod.getStatusCode());
			}
			else
			{
				final AbstractOrderModel childCart2 = mod.getEntry().getChildEntries().iterator().next().getOrder();
				final AbstractOrderEntryModel childCartEntry2 = childCart2.getEntries().iterator().next();
				assertEquals(((SubscriptionProductModel) childCartEntry2.getProduct()).getSubscriptionTerm().getBillingPlan()
						.getBillingFrequency(), childCart2.getBillingTime());
				assertEquals(subscriptionProductModelQuarterly, mod.getEntry().getProduct());
				assertEquals(bundleIPhonePlanSelection, mod.getEntry().getBundleTemplate());
				assertEquals(Integer.valueOf(2), mod.getEntry().getBundleNo());
				assertEquals(1, mod.getEntry().getQuantity().longValue());
				assertEquals(CommerceCartModificationStatus.SUCCESS, mod.getStatusCode());
			}
		}

		// Add new entry (product (no stock) + product (no stock), new bundle (number=3))
		final List<CommerceCartModification> results3 = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				productModelNoStock, bundleIPhoneDeviceSelection, productModelNoStock, bundleIPhoneDeviceSelection, "<no xml>",
				"<no xml>");

		assertEquals(2, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());
		assertEquals(2, results3.size());

		for (final CommerceCartModification mod : results3)
		{
			assertEquals(0, mod.getQuantityAdded());
			assertEquals(CommerceCartModificationStatus.NO_STOCK, mod.getStatusCode());
		}
	}

	@Test
	public void testAddToCartAutoPick() throws CommerceCartModificationException
	{
		// Add new entry (device product, new bundle (number=1))
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart,
				productModelMotorolaRazr, 1, unitModel, false, NEW_BUNDLE, bundlePayAsYouGoDeviceSelection, false, "<no xml>");

		assertEquals(3, modifications.size());
		assertEquals(3, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());

		final CartEntryModel prodEntry = (CartEntryModel) telcoMasterCart.getEntries().iterator().next();
		assertNotNull(prodEntry);
		cartService.getEntriesForProduct(telcoMasterCart, subscriptionProductModelActivationFee).iterator().next();
		final CartEntryModel actFeeEntry = cartService.getEntriesForProduct(telcoMasterCart, subscriptionProductModelActivationFee)
				.iterator().next();
		assertNotNull(actFeeEntry);

		final CartEntryModel serviceFeeEntry = cartService
				.getEntriesForProduct(telcoMasterCart, subscriptionProductModelServiceFeeMonth).iterator().next();

		assertEquals(PRODUCT_MODEL_CODE_MOTOROLA_RAZR, prodEntry.getProduct().getCode());
		assertEquals(unitModel, prodEntry.getUnit());
		assertEquals(1, prodEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), prodEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoDeviceSelection, prodEntry.getBundleTemplate());

		assertEquals(ACTIVATION_FEE_PRODUCT_CODE, actFeeEntry.getProduct().getCode());
		assertEquals(unitModel, actFeeEntry.getUnit());
		assertEquals(1, actFeeEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), actFeeEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoActivationFee, actFeeEntry.getBundleTemplate());

		assertEquals(SERVICE_FEE_PRODUCT_CODE, serviceFeeEntry.getProduct().getCode());
		assertEquals(unitModel, serviceFeeEntry.getUnit());
		assertEquals(1, serviceFeeEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), serviceFeeEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoActivationFee, serviceFeeEntry.getBundleTemplate());

		// Add another entry to existing bundle (device product, new bundle (number=1)); auto-picks should not be inserted again
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, productModel3417125, 1, unitModel, false, 1,
				bundlePayAsYouGoDeviceSelection, false, "<no xml>");

		assertEquals(4, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());
		assertEquals(1, modifications.size());

		for (final AbstractOrderEntryModel cartEntry : telcoMasterCart.getEntries())
		{
			assertEquals(unitModel, cartEntry.getUnit());
			assertEquals(1, cartEntry.getQuantity().longValue());
			assertEquals(Integer.valueOf(1), cartEntry.getBundleNo());
		}
	}

	@Test
	public void testAddToCart2ProductsAutoPick() throws CommerceCartModificationException
	{
		// Add new entry (device product + plan product, new bundle (number=1))
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				productModelMotorolaRazr, bundlePayAsYouGoDeviceSelection, subscriptionProductModelYearly,
				bundlePayAsYouGoPlanSelection, "<no xml>", "<no xml>");

		assertEquals(4, modifications.size());
		assertEquals(4, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());

		final CartEntryModel prodEntry = cartService.getEntriesForProduct(telcoMasterCart, productModelMotorolaRazr).iterator()
				.next();
		assertNotNull(prodEntry);

		final CartEntryModel actFeeEntry = cartService.getEntriesForProduct(telcoMasterCart, subscriptionProductModelActivationFee)
				.iterator().next();
		assertNotNull(actFeeEntry);

		final CartEntryModel serviceFeeEntry = cartService
				.getEntriesForProduct(telcoMasterCart, subscriptionProductModelServiceFeeMonth).iterator().next();
		assertNotNull(serviceFeeEntry);

		final CartEntryModel planEntry = cartService.getEntriesForProduct(telcoMasterCart, subscriptionProductModelYearly)
				.iterator().next();
		assertNotNull(planEntry);

		assertEquals(4, telcoMasterCart.getLastModifiedEntries().size());
		assertTrue(telcoMasterCart.getLastModifiedEntries().contains(prodEntry));
		assertTrue(telcoMasterCart.getLastModifiedEntries().contains(actFeeEntry));
		assertTrue(telcoMasterCart.getLastModifiedEntries().contains(serviceFeeEntry));
		assertTrue(telcoMasterCart.getLastModifiedEntries().contains(planEntry));

		assertEquals(PRODUCT_MODEL_CODE_MOTOROLA_RAZR, prodEntry.getProduct().getCode());
		assertEquals(unitModel, prodEntry.getUnit());
		assertEquals(1, prodEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), prodEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoDeviceSelection, prodEntry.getBundleTemplate());

		assertEquals(ACTIVATION_FEE_PRODUCT_CODE, actFeeEntry.getProduct().getCode());
		assertEquals(unitModel, actFeeEntry.getUnit());
		assertEquals(1, actFeeEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), actFeeEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoActivationFee, actFeeEntry.getBundleTemplate());

		assertEquals(SERVICE_FEE_PRODUCT_CODE, serviceFeeEntry.getProduct().getCode());
		assertEquals(unitModel, serviceFeeEntry.getUnit());
		assertEquals(1, serviceFeeEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), serviceFeeEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoActivationFee, serviceFeeEntry.getBundleTemplate());

		assertEquals(SUBSCRIPTION_PRODUCT_CODE_YEARLY, planEntry.getProduct().getCode());
		assertEquals(unitModel, planEntry.getUnit());
		assertEquals(1, planEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), planEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoPlanSelection, planEntry.getBundleTemplate());

		// Add another entry to existing bundle (device product, new bundle (number=1)); auto-picks should not be inserted again
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, productModel3417125, 1, unitModel, false, 1,
				bundlePayAsYouGoDeviceSelection, false, "<no xml>");

		assertEquals(5, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());
		assertEquals(1, modifications.size());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(modifications.iterator().next().getEntry(), telcoMasterCart.getLastModifiedEntries().iterator().next());

		for (final AbstractOrderEntryModel cartEntry : telcoMasterCart.getEntries())
		{
			assertEquals(unitModel, cartEntry.getUnit());
			assertEquals(1, cartEntry.getQuantity().longValue());
			assertEquals(Integer.valueOf(1), cartEntry.getBundleNo());
		}
	}

	@Test
	public void testAddToCartWithSelectionDependency() throws CommerceCartModificationException
	{
		// Add new entry (device product + plan product, new bundle (number=1))
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				productModelMotorolaRazr, bundlePayAsYouGoDeviceSelection, subscriptionProductModelYearly,
				bundlePayAsYouGoPlanSelection, "<no xml>", "<no xml>");

		assertEquals(4, modifications.size());
		assertEquals(4, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());

		// Add another entry as add-on to existing bundle (device product, bundle (number=1));  PayAsYouGoAddOn is dependent on PayAsYouGoPlanSelection and PayAsYouGoActivationFee
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, productModelMotorolaRazr, 1, unitModel, false, 1,
				bundlePayAsYouGoAddOnSelection, false, "<no xml>");

		assertEquals(1, modifications.size());
		assertEquals(5, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());
	}

	@Test
	public void testAddToCartRemoveEntriesTrue() throws CommerceCartModificationException
	{
		// Add new entry (device product + plan product, new bundle (number=1))
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE,
				productModelHtcIncredibleS, bundlePayAsYouGoDeviceSelection, subscriptionProductModelYearly,
				bundlePayAsYouGoPlanSelection, "<no xml>", "<no xml>");

		assertEquals(4, modifications.size());
		assertEquals(4, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());
		final int bundleNo = telcoMasterCart.getEntries().iterator().next().getBundleNo().intValue();

		// add an add-on product 
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, productModel3417130, 1, unitModel, false, bundleNo,
				bundlePayAsYouGoAddOnSelection, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertEquals(5, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());

		// switch the plan (removes existing plan and dependent components, adds new plan)
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYStarter200_1y, 1, unitModel,
				false, bundleNo, bundlePayAsYouGoPlanSelection, true, "<no xml>");

		assertEquals(1, modifications.size());
		assertEquals(4, telcoMasterCart.getEntries().size());
		assertTrue(isProductInBundleCartEntries(telcoMasterCart, subscriptionProductModelYStarter200_1y, bundleNo));
		assertFalse(isProductInBundleCartEntries(telcoMasterCart, subscriptionProductModelYearly, bundleNo));
		// add-on should also be gone as it was dependent on the plan component which was deleted
		assertFalse(isProductInBundleCartEntries(telcoMasterCart, productModel3417130, bundleNo));
	}

	@Test
	public void testAddToCartPlanFirst() throws CommerceCartModificationException
	{
		// Add new entry (plan product, new bundle (number=1))
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart,
				subscriptionProductModelYearly, 1, unitModel, false, NEW_BUNDLE, bundlePayAsYouGoPlanSelection, false, "<no xml>");

		assertEquals(3, modifications.size());
		assertEquals(3, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());

		final CartModel childCartYearly = getChildCartForBillingTime(telcoMasterCart, subscriptionProductModelYearly
				.getSubscriptionTerm().getBillingPlan().getBillingFrequency());
		final CartEntryModel planEntry = (CartEntryModel) childCartYearly.getEntries().iterator().next();
		assertNotNull(planEntry);

		final CartEntryModel actFeeEntry = cartService.getEntriesForProduct(telcoMasterCart, subscriptionProductModelActivationFee)
				.iterator().next();
		assertNotNull(actFeeEntry);

		final CartModel childCartMonthly = getChildCartForBillingTime(telcoMasterCart, subscriptionProductModelServiceFeeMonth
				.getSubscriptionTerm().getBillingPlan().getBillingFrequency());
		final CartEntryModel serviceFeeEntry = (CartEntryModel) childCartMonthly.getEntries().iterator().next();
		assertNotNull(serviceFeeEntry);

		assertEquals(SUBSCRIPTION_PRODUCT_CODE_YEARLY, planEntry.getProduct().getCode());
		assertEquals(unitModel, planEntry.getUnit());
		assertEquals(1, planEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), planEntry.getMasterEntry().getBundleNo());
		assertEquals(bundlePayAsYouGoPlanSelection, planEntry.getMasterEntry().getBundleTemplate());

		assertEquals(ACTIVATION_FEE_PRODUCT_CODE, actFeeEntry.getProduct().getCode());
		assertEquals(unitModel, actFeeEntry.getUnit());
		assertEquals(1, actFeeEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), actFeeEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoActivationFee, actFeeEntry.getBundleTemplate());

		assertEquals(SERVICE_FEE_PRODUCT_CODE, serviceFeeEntry.getProduct().getCode());
		assertEquals(unitModel, serviceFeeEntry.getUnit());
		assertEquals(1, serviceFeeEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), serviceFeeEntry.getMasterEntry().getBundleNo());
		assertEquals(bundlePayAsYouGoActivationFee, serviceFeeEntry.getMasterEntry().getBundleTemplate());

		// Add another entry to existing bundle (device product, new bundle (number=1)); auto-picks should not be inserted again
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, productModelMotorolaRazr, 1, unitModel, false, 1,
				bundlePayAsYouGoDeviceSelection, false, "<no xml>");

		assertEquals(1, modifications.size());
		assertEquals(4, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());

		final CartEntryModel prodEntry = cartService.getEntriesForProduct(telcoMasterCart, productModelMotorolaRazr).iterator()
				.next();
		assertNotNull(prodEntry);

		assertEquals(PRODUCT_MODEL_CODE_MOTOROLA_RAZR, prodEntry.getProduct().getCode());
		assertEquals(unitModel, prodEntry.getUnit());
		assertEquals(1, prodEntry.getQuantity().longValue());
		assertEquals(Integer.valueOf(1), prodEntry.getBundleNo());
		assertEquals(bundlePayAsYouGoDeviceSelection, prodEntry.getBundleTemplate());

	}

	@Test
	public void testUpdateQuantityForCartEntryBundle() throws CommerceCartModificationException
	{
		// Add new entry (device product, standalone)
		List<CommerceCartModification> modifications = bundleCommerceCartService.addToCart(telcoMasterCart,
				productModelGalaxyNexus, 2, unitModel, false, NO_BUNDLE, null, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		final Integer entryMasterNoBundle = modifications.iterator().next().getEntry().getEntryNumber();
		// Add new entry (device product, new bundle (number=1))
		bundleCommerceCartService.addToCart(telcoMasterCart, productModelGalaxyNexus, 1, unitModel, false, NEW_BUNDLE,
				bundleSmartPhoneDeviceSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = monthly, existing bundle (number=1))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, true, 1,
				bundleSmartPhonePlanSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = yearly, existing bundle (number=1))
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYearly, 1, unitModel, true, 1,
				bundleSmartPhoneValuePackSelection2, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertNotNull(modifications.iterator().next().getEntry());
		final Integer entryChildYearlyBundle1 = modifications.iterator().next().getEntry().getEntryNumber();
		// Add new entry (device product, new bundle (number=2))
		bundleCommerceCartService.addToCart(telcoMasterCart, productModelIPhone, 1, unitModel, false, NEW_BUNDLE,
				bundleIPhoneDeviceSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = paynow, existing bundle (number=2))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelPaynow, 1, unitModel, true, 2,
				bundleIPhoneAddonSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = quarterly, existing bundle (number=2))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelQuarterly, 1, unitModel, true, 2,
				bundleIPhonePlanSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = quarterly, standalone)
		modifications.clear();
		modifications = bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelQuarterly, 1, unitModel, true,
				NO_BUNDLE, null, false, "<no xml>");
		assertEquals(1, modifications.size());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertNotNull(modifications.iterator().next().getEntry());

		//Multi cart looks like this now :
		//@formatter:off
		//	  
		//	  - master cart (pay now):
		//	    - 2 PRODUCT_MODEL_CODE (Standalone)
		//	    - 1 PRODUCT_MODEL_CODE (bundle 1)
		//     - 1 PRODUCT_MODEL_CODE (bundle 2)
		//     - 1 SUBSCRIPTION_PRODUCT_CODE_PAYNOW (bundle 2)
		//	  - child cart (monthly):
		//	    - 1 SUBSCRIPTION_PRODUCT_CODE_MONTHLY (bundle 1)
		//	  - child cart (yearly):
		//	    - 1 SUBSCRIPTION_PRODUCT_CODE_YEARLY (bundle 1)
		//   - child cart (quarterly):
		//     - 1 SUBSCRIPTION_PRODUCT_CODE_QUARTERLY (bundle 2)
		//     - 1 SUBSCRIPTION_PRODUCT_CODE_QUARTERLY (Standalone)
		//
		//@formatter:on

		assertEquals(8, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());

		// Update standalone product in master cart
		CommerceCartModification result = bundleCommerceCartService.updateQuantityForCartEntry(telcoMasterCart,
				entryMasterNoBundle.longValue(), 4);
		assertEquals(Long.valueOf(4), result.getEntry().getQuantity());
		assertEquals(3, telcoMasterCart.getChildren().size());
		assertEquals(8, telcoMasterCart.getEntries().size());
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		assertEquals(result.getEntry(), telcoMasterCart.getLastModifiedEntries().iterator().next());

		// Update bundle product in child cart (yearly) to 0, child cart should be removed
		result = bundleCommerceCartService.updateQuantityForCartEntry(telcoMasterCart, entryChildYearlyBundle1.longValue(), 0);
		assertEquals(-1, result.getQuantityAdded());
		assertEquals(CommerceCartModificationStatus.SUCCESS, result.getStatusCode());
		assertEquals(2, telcoMasterCart.getChildren().size());
		assertNull(getChildCartForBillingTime(telcoMasterCart, subscriptionProductModelYearly.getSubscriptionTerm()
				.getBillingPlan().getBillingFrequency()));
		assertEquals(7, telcoMasterCart.getEntries().size());
		assertTrue(CollectionUtils.isEmpty(telcoMasterCart.getLastModifiedEntries()));

		// Update standalone product child cart (quarterly) to 0
		final List<CartEntryModel> quarterlyEntries = cartService.getEntriesForProduct(telcoMasterCart,
				subscriptionProductModelQuarterly);
		assertEquals(2, quarterlyEntries.size());

		for (final CartEntryModel quarterlyEntry : quarterlyEntries)
		{
			if (quarterlyEntry.getBundleNo().intValue() == 0)
			{
				result = bundleCommerceCartService.updateQuantityForCartEntry(telcoMasterCart, quarterlyEntry.getEntryNumber()
						.intValue(), 0);
			}
		}
		assertEquals(-1, result.getQuantityAdded());
		assertEquals(CommerceCartModificationStatus.SUCCESS, result.getStatusCode());
		assertEquals(2, telcoMasterCart.getChildren().size());
		final CartModel cartModel = getChildCartForBillingTime(telcoMasterCart, subscriptionProductModelQuarterly
				.getSubscriptionTerm().getBillingPlan().getBillingFrequency());
		assertNotNull(cartModel);
		assertEquals(1, cartModel.getEntries().size());
		assertEquals(6, telcoMasterCart.getEntries().size());
		assertTrue(CollectionUtils.isEmpty(telcoMasterCart.getLastModifiedEntries()));
	}

	@Test
	public void testRemoveAllEntriesBundle() throws CommerceCartModificationException, CalculationException
	{
		// Add new entry (device product, standalone)
		bundleCommerceCartService.addToCart(telcoMasterCart, productModelGalaxyNexus, 2, unitModel, false, NO_BUNDLE, null, false,
				"<no xml>");
		// Add new entry (device product, new bundle (number=1))
		bundleCommerceCartService.addToCart(telcoMasterCart, productModelGalaxyNexus, 1, unitModel, false, NEW_BUNDLE,
				bundleSmartPhoneDeviceSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = monthly, existing bundle (number=1))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, true, 1,
				bundleSmartPhonePlanSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = yearly, existing bundle (number=1))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYearly, 1, unitModel, true, 1,
				bundleSmartPhoneValuePackSelection2, false, "<no xml>");
		// Add new entry (device product, new bundle (number=2))
		bundleCommerceCartService.addToCart(telcoMasterCart, productModelIPhone, 1, unitModel, false, NEW_BUNDLE,
				bundleIPhoneDeviceSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = paynow, existing bundle (number=2))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelPaynow, 1, unitModel, true, 2,
				bundleIPhoneAddonSelection, false, "<no xml>");
		// Add new entry (plan product with billing freq = quarterly, existing bundle (number=2))
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelQuarterly, 1, unitModel, true, 2,
				bundleIPhonePlanSelection, false, "<no xml>");
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());
		// Add new entry (plan product with billing freq = quarterly, standalone)
		bundleCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelQuarterly, 1, unitModel, true, NO_BUNDLE,
				null, false, "<no xml>");
		assertEquals(1, telcoMasterCart.getLastModifiedEntries().size());

		//Multi cart looks like this now :
		//@formatter:off
		//	  
		//	  - master cart (pay now):
		//	    - 2 PRODUCT_MODEL_CODE (Standalone)
		//	    - 1 PRODUCT_MODEL_CODE (bundle 1)
		//     - 1 PRODUCT_MODEL_CODE (bundle 2)
		//     - 1 SUBSCRIPTION_PRODUCT_CODE_PAYNOW (bundle 2)
		//	  - child cart (monthly):
		//	    - 1 SUBSCRIPTION_PRODUCT_CODE_MONTHLY (bundle 1)
		//	  - child cart (yearly):
		//	    - 1 SUBSCRIPTION_PRODUCT_CODE_YEARLY (bundle 1)
		//   - child cart (quarterly):
		//     - 1 SUBSCRIPTION_PRODUCT_CODE_QUARTERLY (bundle 2)
		//     - 1 SUBSCRIPTION_PRODUCT_CODE_QUARTERLY (Standalone)
		//
		//@formatter:on

		assertEquals(8, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());

		// Remove bundle 2
		bundleCommerceCartService.removeAllEntries(telcoMasterCart, 2);
		assertEquals(5, telcoMasterCart.getEntries().size());
		assertEquals(3, telcoMasterCart.getChildren().size());
		assertTrue(CollectionUtils.isEmpty(telcoMasterCart.getLastModifiedEntries()));

		// Remove stand-alone
		bundleCommerceCartService.removeAllEntries(telcoMasterCart, 0);
		assertEquals(3, telcoMasterCart.getEntries().size());
		assertEquals(2, telcoMasterCart.getChildren().size());
		assertTrue(CollectionUtils.isEmpty(telcoMasterCart.getLastModifiedEntries()));

		// Remove bundle 1
		bundleCommerceCartService.removeAllEntries(telcoMasterCart, 1);
		assertEquals(0, telcoMasterCart.getEntries().size());
		assertEquals(0, telcoMasterCart.getChildren().size());
		assertTrue(CollectionUtils.isEmpty(telcoMasterCart.getLastModifiedEntries()));
	}

	@Test
	public void testCascadingDeleteForOneLevelOfChildren() throws CommerceCartModificationException
	{
		// Add new entry (device product + plan product, new bundle (number=1))
		bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, NEW_BUNDLE, productModelMotorolaRazr,
				bundlePayAsYouGoDeviceSelection, subscriptionProductModelYearly, bundlePayAsYouGoPlanSelection, "<no xml>",
				"<no xml>");

		final List<CommerceCartModification> addOnModifications = bundleCommerceCartService.addToCart(telcoMasterCart,
				productModelMotorolaRazr, 1, unitModel, true, 1, bundlePayAsYouGoAddOnSelection, false, "<no xml>");
		assertEquals(1, addOnModifications.size());
		final AbstractOrderEntryModel addOnEntry = addOnModifications.iterator().next().getEntry();

		final List<CommerceCartModification> internetModifications = bundleCommerceCartService.addToCart(telcoMasterCart,
				productModel3417130, 1, unitModel, true, 1, bundlePayAsYouGoInternetSelection, false, "<no xml>");
		assertEquals(1, internetModifications.size());
		final AbstractOrderEntryModel internetEntry = internetModifications.iterator().next().getEntry();

		assertEquals(6, telcoMasterCart.getEntries().size());
		assertTrue(telcoMasterCart.getEntries().contains(addOnEntry));
		assertTrue(telcoMasterCart.getEntries().contains(internetEntry));
		assertEquals(3, telcoMasterCart.getChildren().size());

		// remove the addon component: this should also remove the internet component
		bundleCommerceCartService.updateQuantityForCartEntry(telcoMasterCart, addOnEntry.getEntryNumber().intValue(), 0);

		assertEquals(4, telcoMasterCart.getEntries().size());
		assertFalse(telcoMasterCart.getEntries().contains(addOnEntry));
		assertFalse(telcoMasterCart.getEntries().contains(internetEntry));
		assertEquals(3, telcoMasterCart.getChildren().size());

	}

	@Test
	public void testGetFirstInvalidComponentInCart() throws CommerceCartModificationException
	{
		List<CommerceCartModification> results = bundleCommerceCartService.addToCart(telcoMasterCart,
				subscriptionProductModelYearly, 1, unitModel, false, NEW_BUNDLE, bundlePayAsYouGoPlanSelection, false, "<no xml>");
		// 1 plan + 2 auto-picks 
		assertEquals(3, results.size());
		final int bundleNo = results.iterator().next().getEntry().getBundleNo().intValue();

		// device is missing
		BundleTemplateModel invalidComponent = bundleCommerceCartService.getFirstInvalidComponentInCart(telcoMasterCart);
		assertEquals(bundlePayAsYouGoDeviceSelection, invalidComponent);

		results.clear();
		results = bundleCommerceCartService.addToCart(telcoMasterCart, productModelMotorolaRazr, 1, unitModel, false, bundleNo,
				bundlePayAsYouGoDeviceSelection, false, "<no xml>");
		assertEquals(1, results.size());
		invalidComponent = bundleCommerceCartService.getFirstInvalidComponentInCart(telcoMasterCart);
		assertNull(invalidComponent);

	}

	private void setupProducts()
	{
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		productModelGalaxyNexus = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE_GALAXY_NEXUS);
		productModelIPhone = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE_IPHONE);
		productModel3417125 = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE_3417125);
		productModel3417130 = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE_3417130);
		subscriptionProductModelMonthly = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE_MONTHLY);
		subscriptionProductModelYearly = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE_YEARLY);
		subscriptionProductModelPaynow = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE_PAYNOW);
		subscriptionProductModelQuarterly = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE_QUARTERLY);
		subscriptionProductModelYStarter200_1y = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_Y_STARTER_200_1Y);
		productModelNoStock = productService.getProductForCode(catalogVersionModel, NO_STOCK_PRODUCT_MODEL_CODE);
		productModelMotorolaRazr = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE_MOTOROLA_RAZR);
		productModelHtcIncredibleS = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE_HTC_INCREDIBLE_S);
		subscriptionProductModelActivationFee = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				ACTIVATION_FEE_PRODUCT_CODE);
		subscriptionProductModelServiceFeeMonth = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SERVICE_FEE_PRODUCT_CODE);
		unitModel = unitService.getUnitForCode("pieces");
	}

	private void setupBundleTemplates()
	{
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getSessionCatalogVersions().iterator().next();

		bundleSmartPhoneDeviceSelection = getBundleTemplateByIdAndCatalogVersion("SmartPhoneDeviceSelection", catalogVersionModel);
		bundleSmartPhonePlanSelection = getBundleTemplateByIdAndCatalogVersion("SmartPhonePlanSelection", catalogVersionModel);
		//bundleSmartPhoneValuePackSelection1 = getBundleTemplateByIdAndCatalogVersion("SmartPhoneValuePackSelection1", catalogVersionModel);
		bundleSmartPhoneValuePackSelection2 = getBundleTemplateByIdAndCatalogVersion("SmartPhoneValuePackSelection2",
				catalogVersionModel);
		bundleIPhoneDeviceSelection = getBundleTemplateByIdAndCatalogVersion("IPhoneDeviceSelection", catalogVersionModel);
		bundleIPhonePlanSelection = getBundleTemplateByIdAndCatalogVersion("IPhonePlanSelection", catalogVersionModel);
		bundleIPhoneAddonSelection = getBundleTemplateByIdAndCatalogVersion("IPhoneAddonSelection", catalogVersionModel);
		bundlePayAsYouGoDeviceSelection = getBundleTemplateByIdAndCatalogVersion("PayAsYouGoDeviceSelection", catalogVersionModel);
		bundlePayAsYouGoActivationFee = getBundleTemplateByIdAndCatalogVersion("PayAsYouGoActivationFee", catalogVersionModel);
		bundlePayAsYouGoPlanSelection = getBundleTemplateByIdAndCatalogVersion("PayAsYouGoPlanSelection", catalogVersionModel);
		bundlePayAsYouGoAddOnSelection = getBundleTemplateByIdAndCatalogVersion("PayAsYouGoAddOnSelection", catalogVersionModel);
		bundlePayAsYouGoInternetSelection = getBundleTemplateByIdAndCatalogVersion("PayAsYouGoInternetSelection",
				catalogVersionModel);
	}

	private BundleTemplateModel getBundleTemplateByIdAndCatalogVersion(final String bundleId,
			final CatalogVersionModel catalogVersionModel)
	{
		final BundleTemplateModel exampleModel = new BundleTemplateModel();
		exampleModel.setId(bundleId);
		exampleModel.setCatalogVersion(catalogVersionModel);

		return flexibleSearchService.getModelByExample(exampleModel);
	}

	private boolean isProductInBundleCartEntries(final CartModel masterCart, final ProductModel product, final int bundleNo)
	{
		for (final AbstractOrderEntryModel entry : masterCart.getEntries())
		{
			if (Integer.valueOf(bundleNo).equals(entry.getBundleNo()) && product.equals(entry.getProduct()))
			{
				return true;
			}
		}
		return false;
	}
}
