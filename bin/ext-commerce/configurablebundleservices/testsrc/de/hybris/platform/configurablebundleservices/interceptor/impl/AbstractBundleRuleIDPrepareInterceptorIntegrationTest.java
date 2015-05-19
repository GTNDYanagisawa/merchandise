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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link AbstractBundleRuleIDPrepareInterceptor}
 * 
 */
@IntegrationTest
public class AbstractBundleRuleIDPrepareInterceptorIntegrationTest extends ServicelayerTest
{

	private static final Logger LOG = Logger.getLogger(AbstractBundleRuleIDPrepareInterceptorIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private ProductService productService;

	@Resource
	private BaseSiteService baseSiteService;

	private ProductModel productModel;
	private CurrencyModel currency;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for AbstractBundleRuleIDPrepareInterceptorIntegrationTest ...");
		userService.setCurrentUser(userService.getAdminUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for AbstractBundleRuleIDPrepareInterceptorIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");
		productModel = productService.getProductForCode("HW1210-3422");
		currency = commonI18NService.getCurrency("USD");
	}

	@Test
	public void testGenerateAbstractBundleRuleID()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getSessionCatalogVersions().iterator().next();

		final ChangeProductPriceBundleRuleModel bundleRule1 = modelService.create(ChangeProductPriceBundleRuleModel.class);
		Assert.assertNull(bundleRule1.getId());
		bundleRule1.setCatalogVersion(catalogVersion);
		bundleRule1.setPrice(BigDecimal.valueOf(10));
		bundleRule1.setCurrency(currency);
		bundleRule1.setTargetProducts(createProductList());
		modelService.save(bundleRule1);
		Assert.assertNotNull(bundleRule1.getId());
		LOG.info("bundleRule1.id: " + bundleRule1.getId());

		final ChangeProductPriceBundleRuleModel bundleRule2 = modelService.create(ChangeProductPriceBundleRuleModel.class);
		Assert.assertNull(bundleRule2.getId());
		bundleRule2.setCatalogVersion(catalogVersion);
		bundleRule2.setPrice(BigDecimal.valueOf(10));
		bundleRule2.setCurrency(currency);
		bundleRule2.setTargetProducts(createProductList());
		modelService.save(bundleRule2);
		Assert.assertNotNull(bundleRule2.getId());
		Assert.assertTrue(!bundleRule1.getId().equals(bundleRule2.getId()));
		LOG.info("bundleRule2.id: " + bundleRule2.getId());

		final ChangeProductPriceBundleRuleModel bundleRule3 = modelService.create(ChangeProductPriceBundleRuleModel.class);
		Assert.assertNull(bundleRule3.getId());
		bundleRule3.setCatalogVersion(catalogVersion);
		bundleRule3.setPrice(BigDecimal.valueOf(10));
		bundleRule3.setCurrency(currency);
		bundleRule3.setTargetProducts(createProductList());
		modelService.save(bundleRule3);
		Assert.assertNotNull(bundleRule3.getId());
		Assert.assertTrue(!bundleRule1.getId().equals(bundleRule3.getId()));
		Assert.assertTrue(!bundleRule2.getId().equals(bundleRule3.getId()));
		LOG.info("bundleRule3.id: " + bundleRule3.getId());

		final ChangeProductPriceBundleRuleModel bundleRule4 = modelService.create(ChangeProductPriceBundleRuleModel.class);
		Assert.assertNull(bundleRule4.getId());
		bundleRule4.setCatalogVersion(catalogVersion);
		bundleRule4.setId("MyOwnRuleId");
		bundleRule4.setPrice(BigDecimal.valueOf(10));
		bundleRule4.setCurrency(currency);
		bundleRule4.setTargetProducts(createProductList());
		modelService.save(bundleRule4);
		Assert.assertEquals("MyOwnRuleId", bundleRule4.getId());
		LOG.info("bundleRule4.id: " + bundleRule4.getId());

		final ChangeProductPriceBundleRuleModel bundleRule5 = modelService.create(ChangeProductPriceBundleRuleModel.class);
		Assert.assertNull(bundleRule5.getId());
		bundleRule5.setCatalogVersion(catalogVersion);
		bundleRule5.setPrice(BigDecimal.valueOf(10));
		bundleRule5.setCurrency(currency);
		bundleRule5.setTargetProducts(createProductList());
		modelService.save(bundleRule5);
		Assert.assertNotNull(bundleRule5.getId());
		Assert.assertTrue(!bundleRule1.getId().equals(bundleRule5.getId()));
		Assert.assertTrue(!bundleRule2.getId().equals(bundleRule5.getId()));
		Assert.assertTrue(!bundleRule3.getId().equals(bundleRule5.getId()));
		LOG.info("bundleRule5.id: " + bundleRule5.getId());
	}

	private Collection<ProductModel> createProductList()
	{
		return Collections.singletonList(productModel);
	}
}
