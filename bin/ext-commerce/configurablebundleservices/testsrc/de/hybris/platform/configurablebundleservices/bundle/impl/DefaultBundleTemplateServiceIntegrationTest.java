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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test suite for {@link BundleTemplateService}.
 */
@IntegrationTest
public class DefaultBundleTemplateServiceIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleTemplateServiceIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BundleTemplateService bundleTemplateService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ProductService productService;

	@Resource
	private UnitService unitService;

	@Resource
	private BundleCommerceCartService bundleCommerceCartService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	private BundleTemplateModel bundleSmartPhonePackage;
	private BundleTemplateModel bundleSmartPhoneDeviceSelection;
	private BundleTemplateModel bundleSmartPhonePlanSelection;
	private BundleTemplateModel bundleSmartPhoneAddonSelection;
	private BundleTemplateModel bundleSmartPhoneValuePackSelection1;
	private BundleTemplateModel bundleSmartPhoneValuePackSelection2;
	private ProductModel galaxyNexus;
	private ProductModel planStandard1Y;
	private ProductModel noBundleProduct;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultBundleTemplateServiceIntegrationTest ...");
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

		LOG.info("Finished data for DefaultBundleTemplateServiceIntegrationTest " + (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		modelService.detachAll();
	}

	// bundleTemplate hierarchy: 
	// bundleSmartPhonePackage ->  bundleSmartPhoneValuePackSelection1
	// bundleSmartPhonePackage ->  bundleSmartPhoneValuePackSelection2
	@Test
	public void testGetRootBundleTemplate()
	{
		setupBundleTemplates();

		BundleTemplateModel bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleSmartPhonePackage);
		Assert.assertEquals(bundleSmartPhonePackage, bundleTemplate);

		bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleSmartPhoneValuePackSelection1);
		Assert.assertEquals(bundleSmartPhonePackage, bundleTemplate);

		bundleTemplate = bundleTemplateService.getRootBundleTemplate(bundleSmartPhoneValuePackSelection2);
		Assert.assertEquals(bundleSmartPhonePackage, bundleTemplate);
	}

	@Test
	public void testGetBundleTemplatesByProduct()
	{
		setupProducts();

		final List<BundleTemplateModel> templates1 = bundleTemplateService.getBundleTemplatesByProduct(galaxyNexus);
		Assert.assertEquals(1, templates1.size());
		for (final BundleTemplateModel template : templates1)
		{
			Assert.assertTrue(!template.getProducts().isEmpty());
			Assert.assertTrue(template.getProducts().contains(galaxyNexus));
		}

		int counter = 0;
		final List<BundleTemplateModel> templates2 = bundleTemplateService.getBundleTemplatesByProduct(planStandard1Y);
		Assert.assertEquals(2, templates2.size());
		for (final BundleTemplateModel template : templates2)
		{
			Assert.assertTrue(!template.getProducts().isEmpty());
			Assert.assertTrue(template.getProducts().contains(planStandard1Y));
			if (counter == 0)
			{
				Assert.assertEquals("IPhonePlanSelection", template.getId());
			}
			else
			{
				Assert.assertEquals("SmartPhonePlanSelection", template.getId());
			}
			counter++;
		}

		final List<BundleTemplateModel> templates0 = bundleTemplateService.getBundleTemplatesByProduct(noBundleProduct);
		Assert.assertTrue(templates0.isEmpty());
	}

	@Test
	public void testGetSubsequentBundleTemplate()
	{
		setupBundleTemplates();

		BundleTemplateModel nextChildTemplate = bundleTemplateService.getSubsequentBundleTemplate(bundleSmartPhoneDeviceSelection);
		Assert.assertEquals(bundleSmartPhonePlanSelection, nextChildTemplate);

		nextChildTemplate = bundleTemplateService.getSubsequentBundleTemplate(bundleSmartPhonePlanSelection);
		Assert.assertEquals(bundleSmartPhoneAddonSelection, nextChildTemplate);

		nextChildTemplate = bundleTemplateService.getSubsequentBundleTemplate(bundleSmartPhoneAddonSelection);
		Assert.assertEquals(bundleSmartPhoneValuePackSelection1, nextChildTemplate);

		nextChildTemplate = bundleTemplateService.getSubsequentBundleTemplate(bundleSmartPhoneValuePackSelection1);
		Assert.assertEquals(bundleSmartPhoneValuePackSelection2, nextChildTemplate);

		// provide a root bundle template
		nextChildTemplate = bundleTemplateService.getSubsequentBundleTemplate(bundleSmartPhonePackage);
		Assert.assertNull(nextChildTemplate);
	}

	@Test
	public void testGetPreviousBundleTemplate()
	{
		setupBundleTemplates();

		BundleTemplateModel previousChildTemplate = bundleTemplateService
				.getPreviousBundleTemplate(bundleSmartPhoneDeviceSelection);
		Assert.assertNull(previousChildTemplate);

		previousChildTemplate = bundleTemplateService.getPreviousBundleTemplate(bundleSmartPhoneValuePackSelection1);
		Assert.assertEquals(bundleSmartPhoneAddonSelection, previousChildTemplate);

		previousChildTemplate = bundleTemplateService.getPreviousBundleTemplate(bundleSmartPhoneAddonSelection);
		Assert.assertEquals(bundleSmartPhonePlanSelection, previousChildTemplate);

		previousChildTemplate = bundleTemplateService.getPreviousBundleTemplate(bundleSmartPhonePlanSelection);
		Assert.assertEquals(bundleSmartPhoneDeviceSelection, previousChildTemplate);

		// provide a root bundle template
		previousChildTemplate = bundleTemplateService.getPreviousBundleTemplate(bundleSmartPhonePackage);
		Assert.assertNull(previousChildTemplate);
	}

	@Test
	public void testGetRelativeBundleTemplate()
	{
		setupBundleTemplates();

		BundleTemplateModel previousChildTemplate = bundleTemplateService.getRelativeBundleTemplate(
				bundleSmartPhoneDeviceSelection, -1);
		Assert.assertNull(previousChildTemplate);

		previousChildTemplate = bundleTemplateService.getRelativeBundleTemplate(bundleSmartPhoneValuePackSelection1, -2);
		Assert.assertEquals(bundleSmartPhonePlanSelection, previousChildTemplate);

		previousChildTemplate = bundleTemplateService.getRelativeBundleTemplate(bundleSmartPhoneDeviceSelection, 3);
		Assert.assertEquals(bundleSmartPhoneValuePackSelection1, previousChildTemplate);

		// provide a root bundle template
		previousChildTemplate = bundleTemplateService.getRelativeBundleTemplate(bundleSmartPhonePackage, 1);
		Assert.assertNull(previousChildTemplate);
	}

	private void setupBundleTemplates()
	{
		final CatalogVersionModel catalogVersionModel = catalogVersionService.getSessionCatalogVersions().iterator().next();

		bundleSmartPhonePackage = getBundleTemplateByIdAndCatalogVersion("SmartPhonePackage", catalogVersionModel);
		bundleSmartPhonePlanSelection = getBundleTemplateByIdAndCatalogVersion("SmartPhonePlanSelection", catalogVersionModel);
		bundleSmartPhoneDeviceSelection = getBundleTemplateByIdAndCatalogVersion("SmartPhoneDeviceSelection", catalogVersionModel);
		bundleSmartPhoneAddonSelection = getBundleTemplateByIdAndCatalogVersion("SmartPhoneAddonSelection", catalogVersionModel);
		bundleSmartPhoneValuePackSelection1 = getBundleTemplateByIdAndCatalogVersion("SmartPhoneValuePackSelection1",
				catalogVersionModel);
		bundleSmartPhoneValuePackSelection2 = getBundleTemplateByIdAndCatalogVersion("SmartPhoneValuePackSelection2",
				catalogVersionModel);
	}

	private void setupProducts()
	{
		galaxyNexus = productService.getProductForCode("GALAXY_NEXUS");
		noBundleProduct = productService.getProductForCode("2047052");
		planStandard1Y = productService.getProductForCode("PLAN_STANDARD_1Y");
	}

	private BundleTemplateModel getBundleTemplateByIdAndCatalogVersion(final String bundleId,
			final CatalogVersionModel catalogVersionModel)
	{
		final BundleTemplateModel exampleModel = new BundleTemplateModel();
		exampleModel.setId(bundleId);
		exampleModel.setCatalogVersion(catalogVersionModel);

		return flexibleSearchService.getModelByExample(exampleModel);
	}

	@Test
	public void testGetBundleTemplateById()
	{
		setupBundleTemplates();
		final BundleTemplateModel bundleTemplate = bundleTemplateService.getBundleTemplateForCode("SmartPhonePackage");
		Assert.assertNotNull(bundleTemplate);
	}

	@Test
	public void testGetTemplatesForMasterOrderAndBundleNo() throws CommerceCartModificationException
	{
		setupProducts();
		setupBundleTemplates();
		final UserModel telco = userService.getUserForUID("telco");
		final Collection<CartModel> cartModels = telco.getCarts();
		assertEquals(1, cartModels.size());
		final CartModel telcoMasterCart = cartModels.iterator().next();

		final UnitModel unitModel = unitService.getUnitForCode("pieces");
		final List<CommerceCartModification> results = bundleCommerceCartService.addToCart(telcoMasterCart, unitModel, -1,
				galaxyNexus, bundleSmartPhoneDeviceSelection, planStandard1Y, bundleSmartPhonePlanSelection, "<no xml>", "<no xml>");

		assertEquals(2, results.size());
		final AbstractOrderEntryModel entry = results.iterator().next().getEntry();
		assertNotNull(entry);
		final List<BundleTemplateModel> templates = bundleTemplateService.getTemplatesForMasterOrderAndBundleNo(telcoMasterCart,
				entry.getBundleNo().intValue());
		assertEquals(2, templates.size());
		assertTrue(templates.contains(bundleSmartPhoneDeviceSelection));
		assertTrue(templates.contains(bundleSmartPhonePlanSelection));
	}

}
