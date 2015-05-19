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
package de.hybris.platform.acceleratorservices.urldecoder.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.AntPathMatcher;


@UnitTest
public class FrontendUrlDecoderTest
{

	final private static String PRODUCT_URL = "http://www.teststore.com/p/12341234";
	final private static String INCORRECT_PRODUCT_URL = "http://www.teststore.com/p/09870987";

	final private static String CATEGORY_URL = "http://www.teststore.com/c/shoes";
	final private static String INCORRECT_CATEGORY_URL = "http://www.teststore.com/c/pants";

	final private static String INCORRECT_URL = "http://www.teststore.com/t/foobar";

	final private static String PRODUCT_REGEX = "(?<=/p/)[\\.\\:\\,\\*\\(\\!\\'\\)\\/\\\\$\\%\\+\\-\\_\\#\\’A-z0-9]{0,}[^(\\?|\\&|\\;|$)]";
	final private static String CATEGORY_REGEX = "(?<=/c/)[\\.\\:\\,\\*\\(\\!\\'\\)\\/\\\\$\\%\\+\\-\\_\\#\\’A-z0-9]{0,}[^(\\?|\\&|\\;|$)]";

	final private static String PRODUCT_PATH = "/**/p/{code}";
	final private static String CATEGORY_PATH = "/**/c/{code}";

	final private static String PRODUCT_CODE = "12341234";
	final private static String CATEGORY_CODE = "shoes";

	@Mock
	private ProductService productService;

	@Mock
	private CategoryService categoryService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(product);

		final CategoryModel category = new CategoryModel();
		category.setCode(CATEGORY_CODE);
		given(categoryService.getCategoryForCode(CATEGORY_CODE)).willReturn(category);
	}

	@Test
	public void testProductRegexDecoder()
	{
		final ProductFrontendRegexUrlDecoder productRegexDecoder = new ProductFrontendRegexUrlDecoder();
		productRegexDecoder.setRegex(PRODUCT_REGEX);
		productRegexDecoder.setProductService(productService);
		productRegexDecoder.afterPropertiesSet();

		final ProductModel model = productRegexDecoder.decode(PRODUCT_URL);

		assertEquals(PRODUCT_CODE, model.getCode());
	}

	@Test
	public void testMatchingBadProductRegexDecoder()
	{
		final ProductFrontendRegexUrlDecoder productRegexDecoder = new ProductFrontendRegexUrlDecoder();
		productRegexDecoder.setRegex(PRODUCT_REGEX);
		productRegexDecoder.setProductService(productService);
		productRegexDecoder.afterPropertiesSet();

		final ProductModel model = productRegexDecoder.decode(INCORRECT_PRODUCT_URL);

		assertNull(model);
	}

	@Test
	public void testNonMatchingBadProductRegexDecoder()
	{
		final ProductFrontendRegexUrlDecoder productRegexDecoder = new ProductFrontendRegexUrlDecoder();
		productRegexDecoder.setRegex(PRODUCT_REGEX);
		productRegexDecoder.setProductService(productService);
		productRegexDecoder.afterPropertiesSet();

		final ProductModel model = productRegexDecoder.decode(INCORRECT_URL);

		assertNull(model);
	}

	@Test
	public void testProductPathDecoder()
	{
		final ProductFrontendPathMatcherUrlDecoder productPathDecoder = new ProductFrontendPathMatcherUrlDecoder();
		final AntPathMatcher pathMatcher = new AntPathMatcher();
		productPathDecoder.setPathMatcher(pathMatcher);
		productPathDecoder.setPathMatchPattern(PRODUCT_PATH);
		productPathDecoder.setProductService(productService);

		final ProductModel model = productPathDecoder.decode(PRODUCT_URL);

		assertEquals(PRODUCT_CODE, model.getCode());
	}

	@Test
	public void testBadMatchingProductPathDecoder()
	{
		final ProductFrontendPathMatcherUrlDecoder productPathDecoder = new ProductFrontendPathMatcherUrlDecoder();
		final AntPathMatcher pathMatcher = new AntPathMatcher();
		productPathDecoder.setPathMatcher(pathMatcher);
		productPathDecoder.setPathMatchPattern(PRODUCT_PATH);
		productPathDecoder.setProductService(productService);

		final ProductModel model = productPathDecoder.decode(INCORRECT_PRODUCT_URL);
		assertNull(model);
	}

	@Test
	public void testBadNonMatchingProductPathDecoder()
	{
		final ProductFrontendPathMatcherUrlDecoder productPathDecoder = new ProductFrontendPathMatcherUrlDecoder();
		final AntPathMatcher pathMatcher = new AntPathMatcher();
		productPathDecoder.setPathMatcher(pathMatcher);
		productPathDecoder.setPathMatchPattern(PRODUCT_PATH);
		productPathDecoder.setProductService(productService);

		final ProductModel model = productPathDecoder.decode(INCORRECT_URL);

		assertNull(model);
	}

	@Test
	public void testCategoryRegexDecoder()
	{
		final CategoryFrontendRegexUrlDecoder categoryRegexDecoder = new CategoryFrontendRegexUrlDecoder();
		categoryRegexDecoder.setRegex(CATEGORY_REGEX);
		categoryRegexDecoder.setCategoryService(categoryService);
		categoryRegexDecoder.afterPropertiesSet();

		final CategoryModel model = categoryRegexDecoder.decode(CATEGORY_URL);
		assertEquals(CATEGORY_CODE, model.getCode());
	}

	@Test
	public void testMatchingBadCategoryRegexDecoder()
	{
		final CategoryFrontendRegexUrlDecoder categoryRegexDecoder = new CategoryFrontendRegexUrlDecoder();
		categoryRegexDecoder.setRegex(CATEGORY_REGEX);
		categoryRegexDecoder.setCategoryService(categoryService);
		categoryRegexDecoder.afterPropertiesSet();

		final CategoryModel model = categoryRegexDecoder.decode(INCORRECT_CATEGORY_URL);
		assertNull(model);
	}

	@Test
	public void testNonMatchingBadCategoryRegexDecoder()
	{
		final CategoryFrontendRegexUrlDecoder categoryRegexDecoder = new CategoryFrontendRegexUrlDecoder();
		categoryRegexDecoder.setRegex(CATEGORY_REGEX);
		categoryRegexDecoder.setCategoryService(categoryService);
		categoryRegexDecoder.afterPropertiesSet();

		final CategoryModel model = categoryRegexDecoder.decode(INCORRECT_URL);
		assertNull(model);
	}

	@Test
	public void testCategoryPathDecoder()
	{
		final CategoryFrontendPathMatcherUrlDecoder categoryRegexDecoder = new CategoryFrontendPathMatcherUrlDecoder();
		final AntPathMatcher pathMatcher = new AntPathMatcher();
		categoryRegexDecoder.setPathMatcher(pathMatcher);
		categoryRegexDecoder.setPathMatchPattern(CATEGORY_PATH);
		categoryRegexDecoder.setCategoryService(categoryService);

		final CategoryModel model = categoryRegexDecoder.decode(CATEGORY_URL);
		assertEquals(CATEGORY_CODE, model.getCode());
	}

	@Test
	public void testMatchingBadCategoryPathDecoder()
	{
		final CategoryFrontendPathMatcherUrlDecoder categoryRegexDecoder = new CategoryFrontendPathMatcherUrlDecoder();
		final AntPathMatcher pathMatcher = new AntPathMatcher();
		categoryRegexDecoder.setPathMatcher(pathMatcher);
		categoryRegexDecoder.setPathMatchPattern(CATEGORY_PATH);
		categoryRegexDecoder.setCategoryService(categoryService);

		final CategoryModel model = categoryRegexDecoder.decode(INCORRECT_CATEGORY_URL);
		assertNull(model);
	}

	@Test
	public void testNonMatchingBadCategoryPathDecoder()
	{
		final CategoryFrontendPathMatcherUrlDecoder categoryRegexDecoder = new CategoryFrontendPathMatcherUrlDecoder();
		final AntPathMatcher pathMatcher = new AntPathMatcher();
		categoryRegexDecoder.setPathMatcher(pathMatcher);
		categoryRegexDecoder.setPathMatchPattern(CATEGORY_PATH);
		categoryRegexDecoder.setCategoryService(categoryService);

		final CategoryModel model = categoryRegexDecoder.decode(INCORRECT_URL);
		assertNull(model);
	}
}