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
package de.hybris.platform.print.jalo;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.print.model.CometConfigurationModel;
import de.hybris.platform.print.model.PageFormatModel;
import de.hybris.platform.print.model.PublicationModel;
import de.hybris.platform.print.util.PriceContainer;
import de.hybris.platform.print.util.PriceUtils;
import de.hybris.platform.print.util.PriceUtilsImpl;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests {@link de.hybris.platform.print.util.PriceUtils}
 */
public class PriceUtilsTest extends ServicelayerTransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PriceUtilsTest.class.getName());

	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;


	private ProductModel priceProduct;
	private PublicationModel publicationModel;

	@Before
	public void setUp() throws Exception
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId("testPriceCatalog");
		modelService.save(catalog);
		final CatalogVersionModel version = modelService.create(CatalogVersionModel.class);
		version.setCatalog(catalog);
		version.setVersion("testVersion");
		modelService.save(version);

		priceProduct = modelService.create(ProductModel.class);

		final CometConfigurationModel configuration = modelService.create(CometConfigurationModel.class);
		configuration.setCode("testConfiguration");
		modelService.save(configuration);
		final PageFormatModel pageFormat = modelService.create(PageFormatModel.class);
		pageFormat.setQualifier("testFormat");
		pageFormat.setHeight(Double.valueOf(10.0));
		pageFormat.setWidth(Double.valueOf(10.0));
		modelService.save(pageFormat);

		publicationModel = modelService.create(PublicationModel.class);
		publicationModel.setCode("testPublication");
		publicationModel.setConfiguration(configuration);
		publicationModel.setPageFormat(pageFormat);
		publicationModel.setPricesAreNet(Boolean.TRUE);

		priceProduct.setCode("testPriceProduct");
		priceProduct.setCatalogVersion(version);

		final UnitModel unit = modelService.create(UnitModel.class);
		unit.setCode("pc");
		unit.setName("piece");
		unit.setUnitType("base");

		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setBase(Boolean.TRUE);
		currency.setIsocode("PLN");
		currency.setSymbol("pln");
		currency.setName("PLN");

		final PriceRowModel price1 = modelService.create(PriceRowModel.class);
		price1.setCatalogVersion(version);
		price1.setNet(Boolean.TRUE);
		price1.setProduct(priceProduct);
		price1.setPrice(Double.valueOf(120.0));
		price1.setUnit(unit);
		price1.setCurrency(currency);
		modelService.save(price1);

		final PriceRowModel price2 = modelService.create(PriceRowModel.class);
		price2.setCatalogVersion(version);
		price2.setNet(Boolean.FALSE);
		price2.setProduct(priceProduct);
		price2.setPrice(Double.valueOf(130.0));
		price2.setUnit(unit);
		price2.setCurrency(currency);
		price2.setMinqtd(Long.valueOf(10));
		modelService.save(price2);

		modelService.save(priceProduct);
		modelService.save(publicationModel);
	}


	@Test
	public void testPriceUtils()
	{
		final Product productItem = modelService.getSource(priceProduct);
		final PrintPublication printPublication = modelService.getSource(publicationModel);

		Assert.assertNotNull("Fetched product is null!", productItem);
		Assert.assertNotNull("Fetched publication is null!", printPublication);

		final PriceUtils priceUtils = new PriceUtilsImpl();
		final PriceContainer container = priceUtils.getPriceContainer(productItem, printPublication);

		Assert.assertNotNull("Price is null after calculations!", container);
		Assert.assertTrue("Not using net prices!", container.isCalculationNetStatus());

		final Object[] expecteds = new Object[2];
		final Object[] actuals = new Object[2];

		expecteds[0] = productItem;
		actuals[0] = container.getCalculationProduct();

		expecteds[1] = modelService.getSource(userService.getCurrentUser());
		actuals[1] = container.getCalculationUser();

		Assert.assertArrayEquals("Expected and actuals objects in container are different!", expecteds, actuals);

		Assert.assertTrue("Not calculating net prices!", container.isNet());

		Assert.assertTrue("There should be two prices!", container.getAllPrices().size() == 2);
		Assert.assertEquals("Prices are different!", 120.0, container.getPrimaryPriceValue(), 0.001);
	}


}
