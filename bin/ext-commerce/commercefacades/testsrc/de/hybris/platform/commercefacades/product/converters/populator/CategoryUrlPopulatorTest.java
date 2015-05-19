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
package de.hybris.platform.commercefacades.product.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.url.impl.DefaultCategoryModelUrlResolver;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CategoryUrlPopulatorTest
{
	private static final String CATEGORY_CODE = "CategoryCode";
	private static final String CATEGORY_NAME = "CategoryName";
	private static final String CATEGORY_URL = "CategoryUrl";

	@Mock
	private DefaultCategoryModelUrlResolver categoryModelUrlResolver;
	@Mock
	private CategoryModel categoryModel;

	private final CategoryUrlPopulator categoryUrlPopulator = new CategoryUrlPopulator();
	private AbstractPopulatingConverter<CategoryModel, CategoryData> categoryUrlConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		given(categoryModelUrlResolver.resolve(any(CategoryModel.class))).willReturn(CATEGORY_URL);
		given(categoryModel.getCode()).willReturn(CATEGORY_CODE);
		given(categoryModel.getName()).willReturn(CATEGORY_NAME);

		categoryUrlPopulator.setCategoryModelUrlResolver(categoryModelUrlResolver);

		categoryUrlConverter = new ConverterFactory<CategoryModel, CategoryData, CategoryUrlPopulator>().create(CategoryData.class,
				categoryUrlPopulator);
	}

	@Test
	public void testConvert()
	{
		final CategoryData categoryData = categoryUrlConverter.convert(categoryModel);
		Assert.assertNotNull(categoryData);
		Assert.assertEquals(CATEGORY_CODE, categoryData.getCode());
		Assert.assertEquals(CATEGORY_NAME, categoryData.getName());
		Assert.assertEquals(CATEGORY_URL, categoryData.getUrl());

	}
}
