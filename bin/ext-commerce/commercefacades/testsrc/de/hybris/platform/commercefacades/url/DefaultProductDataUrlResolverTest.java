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
package de.hybris.platform.commercefacades.url;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.url.impl.DefaultProductDataUrlResolver;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultProductDataUrlResolverTest extends ServicelayerTransactionalTest
{
	private DefaultProductDataUrlResolver defaultProductDataUrlResolver;

	private static final String PRODUCT_CODE = "HW1210-3423";
	private static final String PRODUCT_NAME = "test2EN";
	private static final String CATEGORY_NAME = "testCategory0";
	private static final String SUBCATEGORY_NAME = "testCategory1";
	private static final String PATH_SEPARATOR = "/";
	private static final String TARGET_PRODUCT_URL = PATH_SEPARATOR + CATEGORY_NAME + PATH_SEPARATOR + SUBCATEGORY_NAME
			+ PATH_SEPARATOR + PRODUCT_NAME + PATH_SEPARATOR + "p" + PATH_SEPARATOR + PRODUCT_CODE;

	@Resource
	private ProductService productService;

	@Resource
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Resource
	private SearchRestrictionService searchRestrictionService;

	@Resource
	private ThreadContextService threadContextService;

	@Before
	public void setUp() throws Exception
	{
		searchRestrictionService.disableSearchRestrictions();
		defaultProductDataUrlResolver = new DefaultProductDataUrlResolver();
		defaultProductDataUrlResolver.setProductService(productService);
		defaultProductDataUrlResolver.setProductModelUrlResolver(productModelUrlResolver);
		defaultProductDataUrlResolver.setThreadContextService(threadContextService);
		importCsv("/commercefacades/test/testProductFacade.csv", "utf-8");
	}

	@Test
	public void testResolveProduct()
	{
		final ProductData productData = new ProductData();
		productData.setCode(PRODUCT_CODE);
		productData.setName(PRODUCT_NAME);
		final String resolvedUrl = defaultProductDataUrlResolver.resolve(productData);

		Assert.assertEquals("Resolved Url was not as expected", TARGET_PRODUCT_URL, resolvedUrl);
	}
}
