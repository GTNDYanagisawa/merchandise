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
package de.hybris.platform.subscriptionservices.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link PromotionBillingTimeRestriction}.
 */
@IntegrationTest
public class PromotionBillingTimeRestrictionIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(PromotionBillingTimeRestrictionIntegrationTest.class);

	private static final String SUBSCRIPTION_PRODUCT_CODE1 = "Y_STARTER_300_1Y"; // monthly price: 49 USD
	private static final String SUBSCRIPTION_PRODUCT_CODE2 = "PLAN_ADVANCED_3Y"; // monthly price: 60 USD
	private static final String PRODUCT_CODE1 = "GALAXY_NEXUS"; // paynow price: 600 USD
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private SubscriptionCommerceCartService subscriptionCommerceCartService;

	@Resource
	private UnitService unitService;

	@Resource
	private PromotionsService promotionsService;

	private SubscriptionProductModel subscriptionProduct1;
	private SubscriptionProductModel subscriptionProduct2;
	private ProductModel product1;
	private UnitModel unitModel;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for PromotionBillingTimeRestrictionIntegrationTest ...");
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
		importCsv("/subscriptionservices/test/testPromotionBillingTimeRestriction.impex", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for PromotionBillingTimeRestrictionIntegrationTest " + (System.currentTimeMillis() - startTime)
				+ "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);

		modelService.detachAll();
	}

	/**
	 * Import multiple subscription entitlements of the same type for a subscription product, for example, a trial period
	 * entitlement and a normal entitlement
	 */
	@Test
	public void testPromotionBillingTimeRestriction() throws CommerceCartModificationException
	{
		setupProducts();
		assertNotNull("", subscriptionProduct1);
		assertNotNull("", subscriptionProduct2);
		assertNotNull("", product1);

		final CartModel masterCart = setupMasterCart();
		assertNotNull("", masterCart);

		subscriptionCommerceCartService.addToCart(masterCart, subscriptionProduct1, 1, unitModel, true, "<no xml>");
		final CartModel monthlyCart = getChildCartForBillingTime(masterCart, subscriptionProduct1.getSubscriptionTerm()
				.getBillingPlan().getBillingFrequency());

		// promo "orderThresholdDiscountPromotion100_10" not applicable (monthly only)
		// promo "orderThresholdDiscountPromotion100_25" not applicable (threshold = 100 USD)
		assertEquals("", 0.0d, masterCart.getTotalPrice().doubleValue(), 0.01);
		assertEquals("", 0.0d, masterCart.getTotalDiscounts().doubleValue(), 0.01);

		// promo "orderThresholdDiscountPromotion100_10" not applicable ((threshold = 100 USD)
		// promo "orderThresholdDiscountPromotion100_25" not applicable (all billing times except monthly)
		assertEquals("", 49.0d, monthlyCart.getTotalPrice().doubleValue(), 0.01);
		assertEquals("", 0.0d, monthlyCart.getTotalDiscounts().doubleValue(), 0.01);

		subscriptionCommerceCartService.addToCart(masterCart, subscriptionProduct2, 1, unitModel, true, "<no xml>");

		// promo "orderThresholdDiscountPromotion100_10" not applicable (monthly only)
		// promo "orderThresholdDiscountPromotion100_25" not applicable (threshold = 100 USD)
		assertEquals("", 0.0d, masterCart.getTotalPrice().doubleValue(), 0.01);
		assertEquals("", 0.0d, masterCart.getTotalDiscounts().doubleValue(), 0.01);

		// promo "orderThresholdDiscountPromotion100_10" applicable (threshold = 100 USD => reduce price totalPrice from 109 to 99)
		// promo "orderThresholdDiscountPromotion100_25" not applicable (all billing times except monthly)
		assertEquals("", 99.0d, monthlyCart.getTotalPrice().doubleValue(), 0.01);
		assertEquals("", 10.0d, monthlyCart.getTotalDiscounts().doubleValue(), 0.01);
		assertTrue("", isPromoCodeInPromoResult(monthlyCart, "orderThresholdDiscountPromotion100_10"));

		subscriptionCommerceCartService.addToCart(masterCart, product1, 1, unitModel, true, "<no xml>");

		// promo "orderThresholdDiscountPromotion100_10" not applicable (monthly only)
		// promo "orderThresholdDiscountPromotion100_25" applicable (threshold = 500 USD  => reduce price totalPrice from 600 to 575)
		assertEquals("", 575.0d, masterCart.getTotalPrice().doubleValue(), 0.01);
		assertEquals("", 25.0d, masterCart.getTotalDiscounts().doubleValue(), 0.01);
		assertTrue("", isPromoCodeInPromoResult(masterCart, "orderThresholdDiscountPromotion100_25"));

		// promo "orderThresholdDiscountPromotion100_10" applicable (threshold = 100 USD => reduce price totalPrice from 109 to 99)
		// promo "orderThresholdDiscountPromotion100_25" not applicable (all billing times except monthly)
		assertEquals("", 99.0d, monthlyCart.getTotalPrice().doubleValue(), 0.01);
		assertEquals("", 10.0d, monthlyCart.getTotalDiscounts().doubleValue(), 0.01);

	}

	protected boolean isPromoCodeInPromoResult(final CartModel cartModel, final String expectedPromoCode)
	{
		final PromotionOrderResults promoResults = promotionsService.getPromotionResults(cartModel);
		final List<PromotionResult> resultList = promoResults.getAllResults();

		for (final PromotionResult result : resultList)
		{
			if (expectedPromoCode.equals(result.getPromotion().getCode()))
			{
				return true;
			}
		}

		return false;
	}

	protected void setupProducts()
	{
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		subscriptionProduct1 = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE1);
		subscriptionProduct2 = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE2);
		product1 = productService.getProductForCode(catalogVersionModel, PRODUCT_CODE1);
		unitModel = unitService.getUnitForCode("pieces");
	}

	protected CartModel setupMasterCart()
	{
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals("", 1, cartModels.size());
		return cartModels.iterator().next();
	}

	protected CartModel getChildCartForBillingTime(final CartModel masterCartModel, final BillingTimeModel billingTimeModel)
	{
		for (final AbstractOrderModel childCartModel : masterCartModel.getChildren())
		{
			if (billingTimeModel.equals(childCartModel.getBillingTime()))
			{
				return (CartModel) childCartModel;
			}
		}
		return null;
	}

}
