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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link DefaultSubscriptionCommerceCheckoutService}.
 */
@IntegrationTest
public class DefaultSubscriptionCommerceCheckoutServiceIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultSubscriptionCommerceCheckoutServiceIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ProductService productService;

	@Resource
	private UnitService unitService;

	@Resource
	private SubscriptionCommerceCartService subscriptionCommerceCartService;

	@Resource
	private CommerceCheckoutService subscriptionCommerceCheckoutService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	private ProductModel galaxyNexus;
	private ProductModel planStandard1Y;
	private CartModel telcoMasterCart;
	private UnitModel unitModel;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultSubscriptionCommerceCheckoutServiceIntegrationTest ..");
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

		LOG.info("Finished data for DefaultSubscriptionCommerceCheckoutServiceIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals("", 1, cartModels.size());
		telcoMasterCart = cartModels.iterator().next();
		unitModel = unitService.getUnitForCode("pieces");

		galaxyNexus = productService.getProductForCode("GALAXY_NEXUS");
		planStandard1Y = productService.getProductForCode("PLAN_STANDARD_1Y");

		modelService.detachAll();
	}

	@Test
	public void testPlaceOrder() throws CommerceCartModificationException, InvalidCartException
	{
		final CommerceCartModification modProduct = subscriptionCommerceCartService.addToCart(telcoMasterCart, galaxyNexus, 1,
				unitModel, false, "<no xml>");
		assertNotNull("", modProduct);
		final CommerceCartModification modPlan = subscriptionCommerceCartService.addToCart(telcoMasterCart, planStandard1Y, 1,
				unitModel, false, "<no xml>");
		assertNotNull("", modPlan);

		assertEquals("", 1, telcoMasterCart.getChildren().size());
		assertEquals("", 2, telcoMasterCart.getEntries().size());
		final CartModel childCart = (CartModel) telcoMasterCart.getChildren().iterator().next();

		final OrderModel masterOrder = subscriptionCommerceCheckoutService.placeOrder(telcoMasterCart);
		assertEquals("", 1, masterOrder.getChildren().size());
		assertEquals("", 2, masterOrder.getEntries().size());
		final OrderModel childOrder = (OrderModel) masterOrder.getChildren().iterator().next();

		assertEquals("", masterOrder, childOrder.getParent());
		assertTrue("", masterOrder.getChildren().contains(childOrder));
		assertEquals("", masterOrder.getDate(), childOrder.getDate());

		compareCartToOrder(telcoMasterCart, masterOrder, true);
		compareCartToOrder(childCart, childOrder, false);
	}

	private void compareCartToOrder(final CartModel cartModel, final OrderModel orderModel, final boolean isMasterCart)
	{
		assertEquals("", cartModel.getCalculated(), orderModel.getCalculated());
		assertEquals("", cartModel.getTotalPrice(), orderModel.getTotalPrice());
		assertEquals("", cartModel.getSubtotal(), orderModel.getSubtotal());
		assertEquals("", cartModel.getTotalDiscounts(), orderModel.getTotalDiscounts());
		assertEquals("", cartModel.getTotalTax(), orderModel.getTotalTax());
		assertEquals("", cartModel.getUser(), orderModel.getUser());
		if (isMasterCart)
		{
			assertEquals("", 2, cartModel.getEntries().size());
		}
		else
		{
			assertEquals("", 1, cartModel.getEntries().size());
		}
		assertEquals("", cartModel.getEntries().size(), orderModel.getEntries().size());
		assertEquals("", cartModel.getBillingTime(), orderModel.getBillingTime());
		assertEquals("", cartModel.getChildren().size(), orderModel.getChildren().size());
		assertEquals("", cartModel.getEntries().size(), orderModel.getEntries().size());

		final CartEntryModel cartEntry = (CartEntryModel) cartModel.getEntries().iterator().next();
		final OrderEntryModel orderEntry = (OrderEntryModel) orderModel.getEntries().iterator().next();

		assertEquals("", cartEntry.getProduct(), orderEntry.getProduct());
		assertEquals("", cartEntry.getCalculated(), orderEntry.getCalculated());
		assertEquals("", cartEntry.getTotalPrice(), orderEntry.getTotalPrice());
		assertEquals("", cartEntry.getBasePrice(), orderEntry.getBasePrice());
		assertEquals("", cartEntry.getQuantity(), orderEntry.getQuantity());
		assertEquals("", cartEntry.getEntryNumber(), orderEntry.getEntryNumber());
		assertEquals("", cartEntry.getOrder().getBillingTime(), orderEntry.getOrder().getBillingTime());
		if (isMasterCart)
		{
			assertNull("", orderEntry.getMasterEntry());
		}
		else
		{
			assertNotNull("", orderEntry.getMasterEntry());
			assertEquals("", orderEntry.getProduct(), orderEntry.getMasterEntry().getProduct());
			assertEquals("", orderEntry.getQuantity(), orderEntry.getMasterEntry().getQuantity());
		}
	}
}
