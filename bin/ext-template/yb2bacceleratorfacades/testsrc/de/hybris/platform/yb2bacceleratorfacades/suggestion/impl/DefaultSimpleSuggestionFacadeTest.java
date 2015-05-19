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
package de.hybris.platform.yb2bacceleratorfacades.suggestion.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.yb2bacceleratorcore.suggestion.SimpleSuggestionService;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultSimpleSuggestionFacade}
 */
@UnitTest
public class DefaultSimpleSuggestionFacadeTest
{
	@Mock
	private UserService userService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private SimpleSuggestionService simpleSuggestionService;
	@Mock
	private ProductModel productModel;
	@Mock
	private ProductData productData;
	@Mock
	private Converter<ProductModel, ProductData> productConverter;
	@Mock
	private ProductPricePopulator productPricePopulator;
	@Mock
	private ProductPrimaryImagePopulator productPrimaryImagePopulator;

	private DefaultSimpleSuggestionFacade defaultSimpleSuggestionFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultSimpleSuggestionFacade = new DefaultSimpleSuggestionFacade();
		defaultSimpleSuggestionFacade.setUserService(userService);
		defaultSimpleSuggestionFacade.setCategoryService(categoryService);
		defaultSimpleSuggestionFacade.setB2bSimpleSuggestionService(simpleSuggestionService);
		defaultSimpleSuggestionFacade.setProductConverter(productConverter);
		defaultSimpleSuggestionFacade.setProductPricePopulator(productPricePopulator);
		defaultSimpleSuggestionFacade.setProductPrimaryImagePopulator(productPrimaryImagePopulator);
	}

	@Test
	public void testGetReferencedProductsForBoughtCategory()
	{
		final UserModel user = mock(UserModel.class);
		final CategoryModel category = mock(CategoryModel.class);

		final String categoryCode = "code";
		given(categoryService.getCategoryForCode(categoryCode)).willReturn(category);
		final Integer limit = NumberUtils.INTEGER_ONE;
		final boolean excludeBoughtProducts = true;
		final ProductReferenceTypeEnum type = ProductReferenceTypeEnum.FOLLOWUP;
		given(userService.getCurrentUser()).willReturn(user);
		given(simpleSuggestionService.getReferencesForPurchasedInCategory(category, user, type, excludeBoughtProducts, limit))
				.willReturn(Collections.singletonList(productModel));
		given(productConverter.convert(productModel)).willReturn(productData);

		final List<ProductData> result = defaultSimpleSuggestionFacade.getReferencesForPurchasedInCategory(categoryCode, type,
				excludeBoughtProducts, limit);
		Assert.assertTrue(result.contains(productData));
		BDDMockito.verify(productPricePopulator, BDDMockito.times(1)).populate(productModel, productData);
		BDDMockito.verify(productPrimaryImagePopulator, BDDMockito.times(1)).populate(productModel, productData);
	}
}
