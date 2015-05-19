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
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.daos.BundleTemplateDao;
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
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Integration test suite for {@link DefaultBundleTemplateDao}
 * 
 */
@IntegrationTest
public class DefaultBundleTemplateDaoIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleTemplateDaoIntegrationTest.class);
	private static final String TEST_BASESITE_UID = "testSite";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Resource
	private UserService userService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private BundleTemplateDao bundleTemplateDao;

	@Resource
	private ProductService productService;

	@Resource
	private BundleTemplateService bundleTemplateService;

	@Resource
	private UnitService unitService;

	@Resource
	private BundleCommerceCartService bundleCommerceCartService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private ModelService modelService;

	private ProductModel galaxyNexus;
	private ProductModel noBundleProduct;
	private ProductModel planStandard1Y;
	private BundleTemplateModel bundleSmartPhonePlanSelection;
	private BundleTemplateModel bundleSmartPhoneDeviceSelection;

	@Before
	public void setUp() throws Exception
	{
		// final Create data for tests
		LOG.info("Creating data for DefaultBundleTemplateDaoIntegrationTest ...");
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

		LOG.info("Finished data for DefaultBundleTemplateDaoIntegrationTest " + (System.currentTimeMillis() - startTime) + "ms");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		catalogVersionService.setSessionCatalogVersion("testCatalog", "Online");

		galaxyNexus = productService.getProductForCode("GALAXY_NEXUS");
		noBundleProduct = productService.getProductForCode("2047052");
		planStandard1Y = productService.getProductForCode("PLAN_STANDARD_1Y");
		bundleSmartPhonePlanSelection = bundleTemplateService.getBundleTemplateForCode("SmartPhonePlanSelection");
		bundleSmartPhoneDeviceSelection = bundleTemplateService.getBundleTemplateForCode("SmartPhoneDeviceSelection");

		modelService.detachAll();
	}

	@Test
	public void testFindBundleTemplatesByProduct()
	{

		final List<BundleTemplateModel> templates1 = bundleTemplateDao.findBundleTemplatesByProduct(galaxyNexus);
		Assert.assertEquals(1, templates1.size());
		for (final BundleTemplateModel template : templates1)
		{
			Assert.assertTrue(!template.getProducts().isEmpty());
			Assert.assertTrue(template.getProducts().contains(galaxyNexus));
		}

		int counter = 0;
		final List<BundleTemplateModel> templates2 = bundleTemplateDao.findBundleTemplatesByProduct(planStandard1Y);
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

		final List<BundleTemplateModel> templates0 = bundleTemplateDao.findBundleTemplatesByProduct(noBundleProduct);
		Assert.assertTrue(templates0.isEmpty());
	}

	@Test
	public void testFindTemplatesByMasterOrderAndBundleNo() throws CommerceCartModificationException
	{
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
		final List<BundleTemplateModel> templates = bundleTemplateDao.findTemplatesByMasterOrderAndBundleNo(telcoMasterCart, entry
				.getBundleNo().intValue());
		assertEquals(2, templates.size());
		assertTrue(templates.contains(bundleSmartPhoneDeviceSelection));
		assertTrue(templates.contains(bundleSmartPhonePlanSelection));
	}

	@Test
	public void testFindBundleByIdAndVersion()
	{
		final BundleTemplateModel validSmartPhoneDeviceSelection = bundleTemplateDao.findBundleTemplateByIdAndVersion(
				"SmartPhonePlanSelection", "1.0");
		Assert.assertNotNull(validSmartPhoneDeviceSelection);

	}

	@Test
	public void testCannotFindBundleByIdAndVersion()
	{
		thrown.expect(ModelNotFoundException.class);
		bundleTemplateDao.findBundleTemplateByIdAndVersion("SmartPhonePlanSelection", "1.1");

	}
}
