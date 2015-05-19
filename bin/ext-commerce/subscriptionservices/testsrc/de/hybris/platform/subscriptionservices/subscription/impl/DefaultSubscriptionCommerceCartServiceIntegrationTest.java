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
package de.hybris.platform.subscriptionservices.subscription.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.DefaultCommerceCartServiceIntegrationTest;
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
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link DefaultSubscriptionCommerceCartService}. This test class inherits from
 * DefaultCommerceCartServiceIntegrationTest in order to run the super class' test cases against the new
 * DefaultSubscriptionCommerceCartService to guarantee compatibility
 */
@IntegrationTest
public class DefaultSubscriptionCommerceCartServiceIntegrationTest extends DefaultCommerceCartServiceIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(DefaultSubscriptionCommerceCartServiceIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private SubscriptionCommerceCartService subscriptionCommerceCartService;

	@Resource
	private UserService userService;

	@Resource
	private CartService cartService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BillingTimeService billingTimeService;

	@Resource
	private UnitService unitService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	private static final String PRODUCT_MODEL_CODE = "3417123";
	private static final String SUBSCRIPTION_PRODUCT_CODE_MONTHLY = "Y_STARTER_100_1Y";
	private static final String SUBSCRIPTION_PRODUCT_CODE_QUARTERLY = "Y_STARTER_200_2Y";
	private static final String SUBSCRIPTION_PRODUCT_CODE_YEARLY = "Y_STARTER_300_2Y";
	private static final String SUBSCRIPTION_PRODUCT_CODE_PAYNOW = "Y_STARTER_100_2Y";

	private ProductModel productModel;
	private SubscriptionProductModel subscriptionProductModelMonthly;
	private SubscriptionProductModel subscriptionProductModelYearly;
	private SubscriptionProductModel subscriptionProductModelPaynow;
	private SubscriptionProductModel subscriptionProductModelQuarterly;
	private UnitModel unitModel;

	@Override
	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultSubscriptionCommerceCartServiceIntegrationTest ...");
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
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for DefaultSubscriptionCommerceCartServiceIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);

		modelService.detachAll();
	}

	@Test
	public void testUpdateQuantityForCartEntrySubscription() throws CommerceCartModificationException
	{
		setupProducts();
		final UserModel abrode = userService.getUserForUID("abrode");
		final Collection<CartModel> cartModels = abrode.getCarts();
		final BillingTimeModel masterCartBillingTime = billingTimeService.getBillingTimeForCode("paynow");

		assertEquals("", 1, cartModels.size());

		final CartModel abrodeCart = cartModels.iterator().next();

		// provided cart's billing frequency matches with provided billing frequency
		abrodeCart.setBillingTime(masterCartBillingTime);

		final CommerceCartModification result = subscriptionCommerceCartService.updateQuantityForCartEntry(abrodeCart, 2, 12);
		assertEquals("", 5L, result.getQuantityAdded());

		assertEquals("", Double.valueOf(795.4), abrodeCart.getTotalPrice());
	}

	@Test
	public void testUpdateQuantityForMultiCartEntry() throws CommerceCartModificationException
	{
		setupProducts();
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();

		assertEquals("", 1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		final Integer productEntryNo = subscriptionCommerceCartService
				.addToCart(telcoMasterCart, productModel, 1, unitModel, false, "<no xml>").getEntry().getEntryNumber();
		final Integer subscriptionProductMonthlyEntryNo1 = subscriptionCommerceCartService
				.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, true, "<no xml>").getEntry()
				.getEntryNumber();
		subscriptionCommerceCartService
				.addToCart(telcoMasterCart, subscriptionProductModelQuarterly, 1, unitModel, false, "<no xml>").getEntry()
				.getEntryNumber();
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYearly, 1, unitModel, false, "<no xml>")
				.getEntry().getEntryNumber();
		subscriptionCommerceCartService
				.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, false, "<no xml>").getEntry()
				.getEntryNumber();
		subscriptionCommerceCartService
				.addToCart(telcoMasterCart, subscriptionProductModelQuarterly, 1, unitModel, false, "<no xml>").getEntry()
				.getEntryNumber();
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYearly, 1, unitModel, false, "<no xml>")
				.getEntry().getEntryNumber();
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelPaynow, 1, unitModel, false, "<no xml>")
				.getEntry().getEntryNumber();

		assertEquals("", 3, telcoMasterCart.getChildren().size());
		assertEquals("", 8, telcoMasterCart.getEntries().size());

		// update standard product in master cart
		assertEquals("", Long.valueOf(1), telcoMasterCart.getEntries().get(productEntryNo.intValue()).getQuantity());
		CommerceCartModification result = subscriptionCommerceCartService.updateQuantityForCartEntry(telcoMasterCart,
				productEntryNo.intValue(), 5);
		assertEquals("", Long.valueOf(5), result.getEntry().getQuantity());
		assertEquals("", 3, telcoMasterCart.getChildren().size());
		assertEquals("", 8, telcoMasterCart.getEntries().size());

		// update 1st monthly subscription product (to q=0)
		final CartModel childCartModelMonthly = getChildCartForBillingTime(telcoMasterCart, subscriptionProductModelMonthly
				.getSubscriptionTerm().getBillingPlan().getBillingFrequency());
		assertEquals("", 2, childCartModelMonthly.getEntries().size());
		assertEquals("", Long.valueOf(1),
				cartService.getEntryForNumber(childCartModelMonthly, subscriptionProductMonthlyEntryNo1.intValue()).getQuantity());
		result = subscriptionCommerceCartService.updateQuantityForCartEntry(telcoMasterCart,
				subscriptionProductMonthlyEntryNo1.intValue(), 0);
		assertEquals("", 1, childCartModelMonthly.getEntries().size());
		assertEquals("", 3, telcoMasterCart.getChildren().size());
		assertEquals("", 7, telcoMasterCart.getEntries().size());

		// update 2nd monthly subscription product  (to q=0)
		assertEquals("", 1, cartService.getEntriesForProduct(telcoMasterCart, subscriptionProductModelMonthly).size());
		final CartEntryModel subscriptionProductMonthlyEntry2 = cartService
				.getEntriesForProduct(telcoMasterCart, subscriptionProductModelMonthly).iterator().next();
		assertEquals("", Long.valueOf(1), subscriptionProductMonthlyEntry2.getQuantity());
		result = subscriptionCommerceCartService.updateQuantityForCartEntry(telcoMasterCart, subscriptionProductMonthlyEntry2
				.getEntryNumber().intValue(), 0);
		assertNull(
				"",
				getChildCartForBillingTime(telcoMasterCart, subscriptionProductModelMonthly.getSubscriptionTerm().getBillingPlan()
						.getBillingFrequency()));
		assertEquals("", 2, telcoMasterCart.getChildren().size());
		assertEquals("", 6, telcoMasterCart.getEntries().size());

	}

	@Test
	public void testAddToMultiCart() throws CommerceCartModificationException
	{
		setupProducts();
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals("", 1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		// Add new entry (device product)
		subscriptionCommerceCartService.addToCart(telcoMasterCart, productModel, 1, unitModel, false, "<no xml>");
		assertEquals("", 1, telcoMasterCart.getEntries().size());
		assertEquals("", 0, telcoMasterCart.getChildren().size());
		final AbstractOrderEntryModel cartEntryModel = telcoMasterCart.getEntries().iterator().next();
		assertEquals("", PRODUCT_MODEL_CODE, cartEntryModel.getProduct().getCode());
		assertEquals("", unitModel, cartEntryModel.getUnit());
		assertEquals("", 1, cartEntryModel.getQuantity().longValue());


		// Add new entry (plan product with billing freq = paynow) -> should be added to master cart
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelPaynow, 1, unitModel, false, "<no xml>")
				.getEntry();
		assertEquals("", 2, telcoMasterCart.getEntries().size());
		assertEquals("", 0, telcoMasterCart.getChildren().size());


		// Add new entry (plan product with billing freq = monthly)
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, true, "<no xml>")
				.getEntry();
		assertEquals("", 3, telcoMasterCart.getEntries().size());
		assertEquals("", 1, telcoMasterCart.getChildren().size());
		CartModel childCartModel = (CartModel) telcoMasterCart.getChildren().iterator().next();
		assertEquals("", childCartModel.getBillingTime(), subscriptionProductModelMonthly.getSubscriptionTerm().getBillingPlan()
				.getBillingFrequency());
		final AbstractOrderEntryModel childCartEntryModel = childCartModel.getEntries().iterator().next();
		assertEquals("", SUBSCRIPTION_PRODUCT_CODE_MONTHLY, childCartEntryModel.getProduct().getCode());
		assertEquals("", unitModel, childCartEntryModel.getUnit());
		assertEquals("", 1, childCartEntryModel.getQuantity().longValue());

		// Add new entry (plan product with billing freq = monthly), forceNewEntry = false (this should be corrected by the service)
		subscriptionCommerceCartService
				.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, false, "<no xml>");
		assertEquals("", 4, telcoMasterCart.getEntries().size());
		assertEquals("", 1, telcoMasterCart.getChildren().size());
		childCartModel = (CartModel) telcoMasterCart.getChildren().iterator().next();
		assertEquals("", childCartModel.getBillingTime(), subscriptionProductModelMonthly.getSubscriptionTerm().getBillingPlan()
				.getBillingFrequency());

		for (final AbstractOrderEntryModel curChildCartEntryModel : childCartModel.getEntries())
		{
			assertEquals("", SUBSCRIPTION_PRODUCT_CODE_MONTHLY, curChildCartEntryModel.getProduct().getCode());
			assertEquals("", unitModel, curChildCartEntryModel.getUnit());
			assertEquals("", 1, curChildCartEntryModel.getQuantity().longValue());
		}

		// Add new entry (plan product with billing freq = yearly), forceNewEntry = false (this should be corrected by the service)
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYearly, 1, unitModel, false, "<no xml>")
				.getEntry();
		assertEquals("", 5, telcoMasterCart.getEntries().size());
		assertEquals("", 2, telcoMasterCart.getChildren().size());

		for (final AbstractOrderModel curCartModel : telcoMasterCart.getChildren())
		{
			for (final AbstractOrderEntryModel curCartEntryModel : curCartModel.getEntries())
			{
				assertEquals("", curCartModel.getBillingTime(), ((SubscriptionProductModel) curCartEntryModel.getProduct())
						.getSubscriptionTerm().getBillingPlan().getBillingFrequency());
				assertEquals("", unitModel, curCartEntryModel.getUnit());
				assertEquals("", 1, curCartEntryModel.getQuantity().longValue());
			}
		}

	}

	@Test
	public void testRemoveAllEntriesMultiCart() throws CommerceCartModificationException
	{
		setupProducts();
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();

		assertEquals("", 1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		subscriptionCommerceCartService.addToCart(telcoMasterCart, productModel, 1, unitModel, false, "<no xml>");
		subscriptionCommerceCartService.addToCart(telcoMasterCart, productModel, 1, unitModel, false, "<no xml>");
		subscriptionCommerceCartService.addToCart(telcoMasterCart, productModel, 1, unitModel, true, "<no xml>");
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, true, "<no xml>");
		subscriptionCommerceCartService
				.addToCart(telcoMasterCart, subscriptionProductModelMonthly, 1, unitModel, false, "<no xml>");
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYearly, 1, unitModel, false, "<no xml>");
		subscriptionCommerceCartService.addToCart(telcoMasterCart, subscriptionProductModelYearly, 1, unitModel, false, "<no xml>");

		assertEquals("", 6, telcoMasterCart.getEntries().size());
		assertEquals("", 2, telcoMasterCart.getChildren().size());

		// empty the master cart
		subscriptionCommerceCartService.removeAllEntries(telcoMasterCart);

		assertEquals("", 0, telcoMasterCart.getEntries().size());
		assertEquals("", 0, telcoMasterCart.getChildren().size());
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

	private void setupProducts()
	{
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		productModel = productService.getProductForCode(catalogVersionModel, PRODUCT_MODEL_CODE);
		subscriptionProductModelMonthly = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE_MONTHLY);
		subscriptionProductModelYearly = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE_YEARLY);
		subscriptionProductModelPaynow = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE_PAYNOW);
		subscriptionProductModelQuarterly = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE_QUARTERLY);
		unitModel = unitService.getUnitForCode("pieces");
	}


}
