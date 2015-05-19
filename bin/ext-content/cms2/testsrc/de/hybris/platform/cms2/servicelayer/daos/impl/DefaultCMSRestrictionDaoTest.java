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
package de.hybris.platform.cms2.servicelayer.daos.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSCategoryRestrictionModel;
import de.hybris.platform.cms2.model.restrictions.CMSProductRestrictionModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSRestrictionDao;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests the {@link DefaultCMSRestrictionDao}
 */
public class DefaultCMSRestrictionDaoTest extends ServicelayerTransactionalTest
{

	@Resource
	private CMSRestrictionDao cmsRestrictionDao;
	@Resource
	private CatalogVersionService catalogVersionService;

	private CatalogVersionModel catVersion;
	private final String categoryRestrictionCode = "restriction01";
	private final String productRestrictionCode = "restriction91";

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/cmsRestrictionTestData.csv", "windows-1252");
		catVersion = catalogVersionService.getCatalogVersion("cms_Catalog", "Online");
	}

	@Test
	public void testFindCategoriesByRestriction()
	{
		final AbstractRestrictionModel categoryRestriction = cmsRestrictionDao.findRestrictionById(categoryRestrictionCode, catVersion);
		final List<CategoryModel> categories = cmsRestrictionDao
				.findCategoriesByRestriction((CMSCategoryRestrictionModel) categoryRestriction);
		assertEquals(2, categories.size());
		final Set<String> expectedCategoryCodes = prepareCategoryCodes();
		final Set<String> categoryCodes = new HashSet<String>(2);
		categoryCodes.add(categories.get(0).getCode());
		categoryCodes.add(categories.get(1).getCode());
		assertTrue(expectedCategoryCodes.containsAll(categoryCodes));
		assertTrue(categoryCodes.containsAll(expectedCategoryCodes));
	}

	@Test
	public void testFindProductsByRestriction()
	{
		final AbstractRestrictionModel productRestriction = cmsRestrictionDao.findRestrictionById(productRestrictionCode, catVersion);
		final List<ProductModel> products = cmsRestrictionDao.findProductsByRestriction((CMSProductRestrictionModel) productRestriction);
		assertEquals(2, products.size());
		final Set<String> expectedProductCodes = prepareProductCodes();
		final Set<String> productCodes = new HashSet<String>(2);
		productCodes.add(products.get(0).getCode());
		productCodes.add(products.get(1).getCode());
		assertTrue(expectedProductCodes.containsAll(productCodes));
		assertTrue(productCodes.containsAll(expectedProductCodes));
	}

	private Set<String> prepareCategoryCodes()
	{
		final Set<String> result = new HashSet<String>(2);
		result.add("category01");
		result.add("category02");
		return result;
	}

	private Set<String> prepareProductCodes()
	{
		final Set<String> result = new HashSet<String>(2);
		result.add("product01");
		result.add("product02");
		return result;
	}

}
