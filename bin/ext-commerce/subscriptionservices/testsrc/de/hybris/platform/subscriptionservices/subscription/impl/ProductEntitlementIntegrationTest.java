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
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ProductEntitlementIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(ProductEntitlementIntegrationTest.class);

	@Resource
	private UserService userService;

	@Resource
	private ModelService modelService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	private static final String SUBSCRIPTION_PRODUCT_CODE = "SPWME";

	private SubscriptionProductModel subscriptionProduct;

	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating data for ProductEntitlementTest ...");
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

		LOG.info("Finished data for ProductEntitlementTest " + (System.currentTimeMillis() - startTime) + "ms");
		modelService.detachAll();
	}

	/**
	 * Import multiple subscription entitlements of the same type for a subscription product, for example, a trial period
	 * entitlement and a normal entitlement
	 */
	@Test
	public void testEntitlementsWithTrialPeriods()
	{
		setupProducts();

		assertNotNull(subscriptionProduct);

		final Collection<ProductEntitlementModel> entitlements = subscriptionProduct.getProductEntitlements();
		assertNotNull(entitlements);

		assertEquals(2, entitlements.size());

		final ProductEntitlementModel[] entitlementArray = entitlements.toArray(new ProductEntitlementModel[entitlements
				.size()]);

		assertEquals(entitlementArray[0].getEntitlement().getId(), entitlementArray[1].getEntitlement().getId());
	}

	private void setupProducts()
	{
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion("testCatalog", "Online");
		subscriptionProduct = (SubscriptionProductModel) productService.getProductForCode(catalogVersionModel,
				SUBSCRIPTION_PRODUCT_CODE);
	}

}
