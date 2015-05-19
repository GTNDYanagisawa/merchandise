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
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.url.impl.DefaultCategoryDataUrlResolver;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCategoryDataUrlResolverTest extends ServicelayerTransactionalTest
{
	private DefaultCategoryDataUrlResolver defaultCategoryDataUrlResolver;

	private static final String CATEGORY_NAME = "testCategory0";
	private static final String SUBCATEGORY_CODE = "testCategory1";
	private static final String SUBCATEGORY_NAME = "testCategory1";
	private static final String PATH_SEPARATOR = "/";
	private static final String TARGET_CATEGORY_URL = PATH_SEPARATOR + CATEGORY_NAME + PATH_SEPARATOR + SUBCATEGORY_NAME
			+ PATH_SEPARATOR + "c" + PATH_SEPARATOR + SUBCATEGORY_CODE;

	@Resource
	private UrlResolver<CategoryModel> categoryModelUrlResolver;

	@Resource
	private CommerceCategoryService commerceCategoryService;

	@Resource
	private SearchRestrictionService searchRestrictionService;

	@Resource
	private ThreadContextService threadContextService;

	@Before
	public void setUp() throws Exception
	{
		searchRestrictionService.disableSearchRestrictions();
		defaultCategoryDataUrlResolver = new DefaultCategoryDataUrlResolver();
		defaultCategoryDataUrlResolver.setCommerceCategoryService(commerceCategoryService);
		defaultCategoryDataUrlResolver.setCategoryModelUrlResolver(categoryModelUrlResolver);
		defaultCategoryDataUrlResolver.setThreadContextService(threadContextService);

		importCsv("/commercefacades/test/testProductFacade.csv", "utf-8");
	}

	@Test
	public void testResolveCategory()
	{
		final CategoryData categoryData = new CategoryData();
		categoryData.setCode(SUBCATEGORY_CODE);
		categoryData.setName(SUBCATEGORY_NAME);
		final String resolvedUrl = defaultCategoryDataUrlResolver.resolve(categoryData);

		Assert.assertEquals("Resolved Url was not as expected", TARGET_CATEGORY_URL, resolvedUrl);
	}
}
