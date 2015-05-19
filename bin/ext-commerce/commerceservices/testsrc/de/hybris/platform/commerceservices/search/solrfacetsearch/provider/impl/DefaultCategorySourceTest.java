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
package de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCategorySourceTest
{
	private static final String TEST_ROOT_CAT_CODE = "TestRootCat";
	private static final String TEST_CATS_QUALIFIER = "supercategories";


	private DefaultCategorySource defaultCategorySource;
	@Mock
	private ModelService modelService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private VariantProductModel model;
	@Mock
	private ProductModel baseProduct;

	@Mock
	private IndexedProperty indexedProperty;
	@Mock
	private IndexConfig indexConfig;
	@Mock
	private CatalogVersionModel catalogVersion;
	// |-rootCategory (Brands)
	// |----classificationClass
	// |----category1
	// |-------category2
	// |----category3
	@Mock
	private CategoryModel category1;
	@Mock
	private CategoryModel category2;
	@Mock
	private CategoryModel category3;
	@Mock
	private CategoryModel rootCategory;
	@Mock
	private ClassificationClassModel classificationClass;

	private boolean includeClassificationClasses;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		configure();
	}

	protected void configure()
	{
		defaultCategorySource = new DefaultCategorySource();

		defaultCategorySource.setModelService(modelService);
		defaultCategorySource.setCategoriesQualifier(TEST_CATS_QUALIFIER);
		defaultCategorySource.setIncludeClassificationClasses(includeClassificationClasses);
		defaultCategorySource.setRootCategory(TEST_ROOT_CAT_CODE);
		defaultCategorySource.setCategoryService(categoryService);

		given(rootCategory.getSupercategories()).willReturn(null);
		given(rootCategory.getAllSupercategories()).willReturn(null);
		final List<CategoryModel> superCats = new ArrayList<CategoryModel>();
		superCats.add(rootCategory);
		superCats.add(classificationClass);
		given(category1.getSupercategories()).willReturn(superCats);
		given(category1.getAllSupercategories()).willReturn(superCats);
		given(category2.getSupercategories()).willReturn(singletonList(category1));
		given(category2.getAllSupercategories()).willReturn(singletonList(category1));
		given(category3.getSupercategories()).willReturn(singletonList(rootCategory));
		given(category3.getAllSupercategories()).willReturn(singletonList(rootCategory));

		given(model.getBaseProduct()).willReturn(baseProduct);
		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER))).willReturn(
				singletonList(category2));

		given(indexConfig.getCatalogVersions()).willReturn(singletonList(catalogVersion));
		given(categoryService.getCategoryForCode(catalogVersion, TEST_ROOT_CAT_CODE)).willReturn(rootCategory);

	}

	@Test
	public void testGetCategories()
	{
		//Test ProductModel
		Collection<CategoryModel> result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty,
				model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains(rootCategory));
		Assert.assertTrue(result.contains(category1));
		Assert.assertTrue(result.contains(category2));

		//Test VariantProductModel
		result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, new ProductModel());
		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains(rootCategory));
		Assert.assertTrue(result.contains(category1));
		Assert.assertTrue(result.contains(category2));
	}

	@Test
	public void testGetCategoriesNoDirectCats()
	{
		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER))).willReturn(null);
		Collection<CategoryModel> result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty,
				model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());

		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER))).willReturn(
				Collections.<Object> emptyList());
		result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testGetCategoriesIsBlockedCategory()
	{
		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER))).willReturn(
				singletonList(classificationClass));
		Collection<CategoryModel> result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty,
				model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());

		defaultCategorySource.setIncludeClassificationClasses(true);
		given(classificationClass.getSupercategories()).willReturn(singletonList(rootCategory));
		given(classificationClass.getAllSupercategories()).willReturn(singletonList(rootCategory));
		result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains(rootCategory));
		Assert.assertTrue(result.contains(classificationClass));
	}

	@Test
	public void testGetCategoriesNoRootCats()
	{
		defaultCategorySource.setRootCategory(null);
		Collection<CategoryModel> result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty,
				model);
		Assert.assertNotNull(result);

		defaultCategorySource.setRootCategory("");
		result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
	}

	@Test
	public void testCollectSuperCategories()
	{
		//null category
		given(categoryService.getCategoryForCode(catalogVersion, TEST_ROOT_CAT_CODE)).willReturn(classificationClass);
		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER))).willReturn(
				singletonList(null));
		Collection<CategoryModel> result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty,
				model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());

		// |classificationClass2
		// |--classificationClass
		// |----category1
		//(invalid category branch1)
		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER))).willReturn(
				singletonList(category1));
		final ClassificationClassModel classificationClass2 = mock(ClassificationClassModel.class);
		given(category1.getSupercategories()).willReturn(singletonList((CategoryModel) classificationClass));
		given(category1.getAllSupercategories()).willReturn(singletonList((CategoryModel) classificationClass));
		given(classificationClass.getSupercategories()).willReturn(singletonList((CategoryModel) classificationClass2));
		given(classificationClass.getAllSupercategories()).willReturn(singletonList((CategoryModel) classificationClass2));
		given(categoryService.getCategoryForCode(catalogVersion, TEST_ROOT_CAT_CODE)).willReturn(classificationClass);

		result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());

		//Mock Loop in Category structure (invalid category branch2)
		given(categoryService.getCategoryForCode(catalogVersion, TEST_ROOT_CAT_CODE)).willReturn(rootCategory);
		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER))).willReturn(
				singletonList(category2));
		given(category1.getSupercategories()).willReturn(singletonList(category2));
		given(category1.getAllSupercategories()).willReturn(singletonList(category2));
		result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());

		//Null Super Category
		given(modelService.getAttributeValue(Matchers.<Object> anyObject(), eq(TEST_CATS_QUALIFIER))).willReturn(
				singletonList(classificationClass));
		defaultCategorySource.setIncludeClassificationClasses(true);
		result = defaultCategorySource.getCategoriesForConfigAndProperty(indexConfig, indexedProperty, model);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isEmpty());
	}
}
