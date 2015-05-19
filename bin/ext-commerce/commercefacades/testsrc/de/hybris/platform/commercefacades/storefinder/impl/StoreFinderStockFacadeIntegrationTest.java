/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.commercefacades.storefinder.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storefinder.StoreFinderStockFacade;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceStockData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.site.BaseSiteService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class StoreFinderStockFacadeIntegrationTest extends ServicelayerTest
{
	private static final String SITE_NAME = "testSite";
	private static final String POS_NAME = "Nakano";
	private static final String PRODUCT_CODE = "product1";

	@Resource
	private ProductService productService;
	@Resource
	private StoreFinderStockFacade<PointOfServiceStockData, StoreFinderStockSearchPageData<PointOfServiceStockData>> storeFinderStockFacade;
	@Resource
	private BaseSiteService baseSiteService;


	@Before
	public void prepare() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		importCsv("/commercefacades/test/testStockService.impex", "UTF-8");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(SITE_NAME), false);
	}

	@After
	public void unPrepare()
	{
		//
	}

	@Test
	public void testProductSearchForPos()
	{
		final StoreFinderStockSearchPageData<PointOfServiceStockData> result = storeFinderStockFacade.productPOSSearch(POS_NAME,
				getProductData(), createPageableData(0, 3, null));

		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.getResults().size());
		Assert.assertNull(result.getResults().get(0).getStockData().getStockLevel());
		Assert.assertEquals("inStock", result.getResults().get(0).getStockData().getStockLevelStatus().getCode());
		Assert.assertEquals(12, result.getResults().get(2).getStockData().getStockLevel().intValue());
		Assert.assertEquals("13" + getDecimalSeparator() + "4", result.getResults().get(2).getFormattedDistance().substring(0, 4));
	}


	@Test
	public void testProductSearchForPosSecondPage()
	{
		final StoreFinderStockSearchPageData<PointOfServiceStockData> result = storeFinderStockFacade.productPOSSearch(POS_NAME,
				getProductData(), createPageableData(1, 5, null));

		Assert.assertNotNull(result);
		Assert.assertEquals(3, result.getResults().size());
		Assert.assertEquals("lowStock", result.getResults().get(0).getStockData().getStockLevelStatus().getCode());
		Assert.assertEquals("25" + getDecimalSeparator() + "3", result.getResults().get(0).getFormattedDistance().substring(0, 4));
		Assert.assertEquals(99, result.getResults().get(1).getStockData().getStockLevel().intValue());
		Assert.assertEquals("102" + getDecimalSeparator() + "6", result.getResults().get(2).getFormattedDistance().substring(0, 5));
	}



	protected PageableData createPageableData(final int page, final int size, final String sort)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(page);
		pageableData.setPageSize(size);
		pageableData.setSort(sort);
		return pageableData;
	}


	protected ProductData getProductData()
	{
		final ProductModel productModel = productService.getProductForCode(PRODUCT_CODE);
		final ProductData productData = new ProductData();
		productData.setCode(productModel.getCode());
		return productData;
	}

	protected char getDecimalSeparator()
	{
		final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
		final DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
		return symbols.getDecimalSeparator();
	}

}
