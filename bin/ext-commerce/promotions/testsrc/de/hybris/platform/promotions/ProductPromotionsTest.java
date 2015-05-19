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
package de.hybris.platform.promotions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for PromotionsManager.getProductPromotions()
 */
public class ProductPromotionsTest extends AbstractPromotionServiceTest
{

	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private ProductService productService;
	@Resource
	private PromotionsService promotionsService;

	private CatalogVersionModel catVersion;
	private ProductModel product;

	private ProductModel independentProduct;


	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		catVersion = catalogVersionService.getCatalogVersion("hwcatalog", "Online");
		catalogVersionService.addSessionCatalogVersion(catVersion);

		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));
		product = productService.getProductForCode(catVersion, "HW1210-3411");
		independentProduct = productService.getProductForCode(catVersion, "independentProduct");
	}

	/**
	 * Tests the promotionsService.getProductPromotions(...)
	 * <ul>
	 * <li>for product "HW1210-3411", tests the size of it and each promotion code:
	 * <li>only one promotion in group1,</li>
	 * <li>two promotions in group1 and group2,</li>
	 * <li>four promotions in group1, group2, and group4.</li>
	 * </ul>
	 * and in the end, test the product which doesn't belong to any categories.
	 */
	@Test
	public void testGetProductPromotions()
	{
		//also see SUP-3575
		final PromotionGroupModel promotionGroup1 = promotionsService.getPromotionGroup("prGroup1");
		List<ProductPromotionModel> productPromotions = promotionsService.getProductPromotions(
				Collections.singleton(promotionGroup1), product);
		testPromotionCodes(productPromotions, Arrays.asList("FixedPriceGr1"));

		final PromotionGroupModel promotionGroup2 = promotionsService.getPromotionGroup("prGroup2");
		productPromotions = promotionsService.getProductPromotions(Arrays.asList(promotionGroup1, promotionGroup2), product);
		testPromotionCodes(productPromotions, Arrays.asList("FixedPriceGr1", "FixedPriceGr2"));

		final PromotionGroupModel promotionGroup4 = promotionsService.getPromotionGroup("prGroup4");
		productPromotions = promotionsService.getProductPromotions(
				Arrays.asList(promotionGroup1, promotionGroup2, promotionGroup4), product);
		testPromotionCodes(productPromotions,
				Arrays.asList("FixedPriceGr1", "FixedPriceGr2", "PercentageDiscount_15", "PercentageDiscount_18"));

		//test product promotions for product which doesn't belong to any categories, and no exception should be thrown
		final PromotionGroupModel independentGroup = promotionsService.getPromotionGroup("independentGroup");
		productPromotions = promotionsService.getProductPromotions(
				Arrays.asList(independentGroup, promotionGroup1, promotionGroup2, promotionGroup4), independentProduct);
		testPromotionCodes(productPromotions, Arrays.asList("independentProductPromotion"));
	}

	private void testPromotionCodes(final List<ProductPromotionModel> productPromotions, final List<String> expectedCodes)
	{
		assertEquals("wrong number of product promotions for product in groups", productPromotions.size(), expectedCodes.size());
		final List<String> codes = new ArrayList<String>();
		for (final ProductPromotionModel productPromotion : productPromotions)
		{
			codes.add(productPromotion.getCode());
		}
		assertTrue(codes.containsAll(expectedCodes));
		assertTrue(expectedCodes.containsAll(codes));
	}

}
