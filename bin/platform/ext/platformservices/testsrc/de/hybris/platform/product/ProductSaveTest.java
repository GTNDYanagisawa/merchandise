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
package de.hybris.platform.product;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.product.impl.UniqueCatalogItemInterceptor;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ProductSaveTest extends ServicelayerTransactionalTest
{

	private CatalogVersionModel onlinecv = null;

	@Resource
	private I18NService i18nService;

	@Resource
	private ModelService modelService;

	@Resource
	private ProductService productService;


	@Before
	public void setUp() throws Exception
	{
		//creating a catalog with 2 catalog versions
		final CatalogModel defaultcatalog = modelService.create(CatalogModel.class);
		onlinecv = modelService.create(CatalogVersionModel.class);
		final CatalogVersionModel stagedcv = modelService.create(CatalogVersionModel.class);

		onlinecv.setActive(Boolean.TRUE);
		stagedcv.setActive(Boolean.FALSE);
		onlinecv.setVersion("online");
		stagedcv.setVersion("staged");
		onlinecv.setCatalog(defaultcatalog);
		stagedcv.setCatalog(defaultcatalog);
		defaultcatalog.setId("default");

		modelService.saveAll();

		final Currency eur = C2LManager.getInstance().createCurrency("EUR");
		eur.setActive(true);
		eur.setName("Euro");
		eur.setBase();
		eur.setConversionFactor(1.00);
	}

	@Test
	public void testSaveAll() //getProduct(code)
	{
		final UnitModel unit = modelService.create(UnitModel.class);
		unit.setCode("specialunit");
		unit.setConversion(Double.valueOf(1.0));
		unit.setName("special Unit", Locale.ENGLISH);
		unit.setUnitType("something");

		final ProductModel product = modelService.create(ProductModel.class);
		product.setCode("prodWithPrice");
		product.setCatalogVersion(onlinecv);
		product.setApprovalStatus(ArticleApprovalStatus.APPROVED);

		final PriceRowModel pricerow = modelService.create(PriceRowModel.class);
		pricerow.setCurrency(i18nService.getCurrency("EUR"));
		pricerow.setMinqtd(Long.valueOf(1));
		pricerow.setNet(Boolean.TRUE);
		pricerow.setPrice(Double.valueOf(2.34));
		pricerow.setUnit(unit);
		pricerow.setProduct(product);
		pricerow.setCatalogVersion(onlinecv);

		modelService.saveAll(Arrays.asList(pricerow, unit, product)); //should not throw any error

		final ProductModel resprod = productService.getProduct("prodWithPrice");
		assertNotNull("prod not found", resprod);
		assertEquals("", "specialunit", productService.getOrderableUnit(resprod).getCode());

	}

	@Test
	public void testSaveOnlyPriceRow() //getProduct(code)
	{
		final UnitModel unit = modelService.create(UnitModel.class);
		unit.setCode("specialunit");
		unit.setConversion(Double.valueOf(1.0));
		unit.setName("special Unit", Locale.ENGLISH);
		unit.setUnitType("something");

		final ProductModel product = modelService.create(ProductModel.class);
		product.setCode("prodWithPrice");
		product.setCatalogVersion(onlinecv);
		product.setApprovalStatus(ArticleApprovalStatus.APPROVED);

		final PriceRowModel pricerow = modelService.create(PriceRowModel.class);
		pricerow.setCurrency(i18nService.getCurrency("EUR"));
		pricerow.setMinqtd(Long.valueOf(1));
		pricerow.setNet(Boolean.TRUE);
		pricerow.setPrice(Double.valueOf(2.34));
		pricerow.setUnit(unit);
		pricerow.setProduct(product);
		pricerow.setCatalogVersion(onlinecv);

		modelService.save(pricerow); //should not throw any error, automatically saves product and unit too

		final ProductModel resprod = productService.getProduct("prodWithPrice");
		assertNotNull("prod not found", resprod);
		assertEquals("", "specialunit", productService.getOrderableUnit(resprod).getCode());
	}

	@Test
	public void testSaveMoreModelsTogether1()
	{
		//pricerow1 -> product1 -> unit1
		//pricerow2 -> product2 -> unit1
		final UnitModel unit1 = modelService.create(UnitModel.class);
		unit1.setCode("specialunit1");
		unit1.setConversion(Double.valueOf(1.0));
		unit1.setName("special Unit", Locale.ENGLISH);
		unit1.setUnitType("something");

		final ProductModel product1 = modelService.create(ProductModel.class);
		product1.setCode("prodWithPrice1");
		product1.setCatalogVersion(onlinecv);
		product1.setApprovalStatus(ArticleApprovalStatus.APPROVED);

		final ProductModel product2 = modelService.create(ProductModel.class);
		product2.setCode("prodWithPrice2");
		product2.setCatalogVersion(onlinecv);
		product2.setApprovalStatus(ArticleApprovalStatus.APPROVED);

		final PriceRowModel pricerow1 = modelService.create(PriceRowModel.class);
		pricerow1.setCurrency(i18nService.getCurrency("EUR"));
		pricerow1.setMinqtd(Long.valueOf(1));
		pricerow1.setNet(Boolean.TRUE);
		pricerow1.setPrice(Double.valueOf(2.34));
		pricerow1.setUnit(unit1);
		pricerow1.setProduct(product1);
		pricerow1.setCatalogVersion(onlinecv);

		final PriceRowModel pricerow2 = modelService.create(PriceRowModel.class);
		pricerow2.setCurrency(i18nService.getCurrency("EUR"));
		pricerow2.setMinqtd(Long.valueOf(1));
		pricerow2.setNet(Boolean.TRUE);
		pricerow2.setPrice(Double.valueOf(2.34));
		pricerow2.setUnit(unit1);
		pricerow2.setProduct(product2);
		pricerow2.setCatalogVersion(onlinecv);

		modelService.saveAll(Arrays.asList(pricerow1, pricerow2));

		final ProductModel resprod1 = productService.getProduct("prodWithPrice1");
		assertNotNull("prod not found", resprod1);
		final ProductModel resprod2 = productService.getProduct("prodWithPrice2");
		assertNotNull("prod not found", resprod2);

		assertEquals("", "specialunit1", productService.getOrderableUnit(resprod1).getCode());
		assertEquals("", "specialunit1", productService.getOrderableUnit(resprod2).getCode());
	}

	@Test
	public void testSaveAllDuplicatePriceRows()
	{
		final UnitModel unit = modelService.create(UnitModel.class);
		unit.setCode("specialunit1");
		unit.setConversion(Double.valueOf(1.0));
		unit.setName("special Unit", Locale.ENGLISH);
		unit.setUnitType("something");

		final ProductModel product = modelService.create(ProductModel.class);
		product.setCode("prodWithPrice");
		product.setCatalogVersion(onlinecv);
		product.setApprovalStatus(ArticleApprovalStatus.APPROVED);

		final PriceRowModel pricerow1 = modelService.create(PriceRowModel.class);
		pricerow1.setCurrency(i18nService.getCurrency("EUR"));
		pricerow1.setMinqtd(Long.valueOf(1));
		pricerow1.setNet(Boolean.TRUE);
		pricerow1.setPrice(Double.valueOf(2.34));
		pricerow1.setUnit(unit);
		pricerow1.setProduct(product);
		pricerow1.setCatalogVersion(onlinecv);

		final PriceRowModel pricerow2 = modelService.create(PriceRowModel.class);
		pricerow2.setCurrency(i18nService.getCurrency("EUR"));
		pricerow2.setMinqtd(Long.valueOf(1));
		pricerow2.setNet(Boolean.TRUE);
		pricerow2.setPrice(Double.valueOf(2.34));
		pricerow2.setUnit(unit);
		pricerow2.setProduct(product);
		pricerow2.setCatalogVersion(onlinecv);

		//pricerow1 and pricerow2 are identical - shares the same unique attributes 
		try
		{
			modelService.saveAll(Arrays.asList(pricerow1, pricerow2));
			fail();
		}
		catch (final ModelSavingException e)
		{
			assertTrue(e.getCause() instanceof InterceptorException);
			final InterceptorException ie = (InterceptorException) e.getCause();
			System.out.println(ie.getInterceptor());
			assertTrue(ie.getInterceptor() instanceof UniqueCatalogItemInterceptor);
			assertTrue("unique key exception does not contain unsaved product model",
					e.getMessage().contains("product=ProductModel (<unsaved>)"));
			assertTrue("unique key exception does not contain unsaved unit model",
					e.getMessage().contains("unit=UnitModel (<unsaved>)"));
		}
		catch (final Exception e)
		{
			fail();
		}
	}
}
