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
package de.hybris.platform.b2b.interceptor;

import com.google.common.collect.Lists;
import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantCategoryModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.b2b.testframework.ModelFactory;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;


public class VariantCategoryValidateInterceptorTest extends ServicelayerTransactionalTest
{
	static Logger LOG = LoggerFactory.getLogger(VariantCategoryValidateInterceptorTest.class);

	@Resource
	ModelFactory modelFactory;

	@Resource
	VariantCategoryValidateInterceptor variantCategoryValidateInterceptor;

	@Resource
	CategoryService categoryService;

	CatalogVersionModel catalogVersion;

	UnitModel unit;

	@Before
	public void setUp() throws Exception
	{
		catalogVersion = modelFactory.createCatalogVersion("myCatalog", "Standard");
		unit = modelFactory.createUnit("PCS");
	}

	@Test
	public void shouldInitializeTheInterceptor()
	{
		Assert.assertNotNull(variantCategoryValidateInterceptor);
	}

	@Test
	public void shouldEnsureVariantCategoryOrdering()
	{
		final VariantCategoryModel color = modelFactory.createVariantCategory("color", catalogVersion);

		final VariantCategoryModel size = modelFactory.createVariantCategory("size", catalogVersion);
		size.setSupercategories(Lists.<CategoryModel>newArrayList(color));

		final VariantCategoryModel fit = modelFactory.createVariantCategory("fit", catalogVersion);
		fit.setSupercategories(Lists.<CategoryModel>newArrayList(size));

		final ProductModel base = modelFactory.createProduct("base", catalogVersion, GenericVariantProductModel._TYPECODE, unit, color, size, fit);

		final VariantValueCategoryModel red = modelFactory.createVariantValueCategory("red", color, 1, catalogVersion);
		final VariantValueCategoryModel medium = modelFactory.createVariantValueCategory("M", size, 1, catalogVersion);
		final VariantValueCategoryModel wide = modelFactory.createVariantValueCategory("wide", fit, 1, catalogVersion);

		modelFactory.createGenericVariantProduct("variantProduct", base, catalogVersion, red, medium, wide);


		Assert.assertTrue("The order should have been 'color (red), size (M), fit (wide)'.", getVariantCategoryPriority(red) < getVariantCategoryPriority(medium)
				&& getVariantCategoryPriority(medium) < getVariantCategoryPriority(wide));


	}

	@Test
	public void shouldOnlyAllowOneVariantCategoryIsSetInSupercategories()
	{
		final VariantCategoryModel color = modelFactory.createVariantCategory("color", catalogVersion);
		final VariantCategoryModel size = modelFactory.createVariantCategory("size", catalogVersion);
		final VariantCategoryModel fit = modelFactory.createVariantCategory("fit", catalogVersion);

		fit.setSupercategories(Lists.<CategoryModel>newArrayList(color, size));

		try {
			modelFactory.save(fit);
			Assert.fail("It shouldn't be possible to save a VariantCategory with multiple superctegories");
		}
		catch (Exception e)
		{
			// 'ight
		}
	}

	@Test
	public void shouldOnlyAllowOneVariantCategoryIsSetInSubcategories()
	{
		final VariantCategoryModel color = modelFactory.createVariantCategory("color", catalogVersion);
		final VariantCategoryModel size = modelFactory.createVariantCategory("size", catalogVersion);
		final VariantCategoryModel fit = modelFactory.createVariantCategory("fit", catalogVersion);

		color.setCategories(Lists.<CategoryModel>newArrayList(size, fit));

		try {
			modelFactory.save(color);
			Assert.fail("It shouldn't be possible to save a VariantCategory with multiple superctegories");
		}
		catch (Exception e)
		{
			// 'ight
		}
	}

	private int getVariantCategoryPriority(final VariantValueCategoryModel variantValueCategory)
	{
		return getPathToRoot(variantValueCategory).size();
	}

	private LinkedList<CategoryModel> getPathToRoot(final VariantValueCategoryModel variantValueCategory)
	{
		final LinkedList<CategoryModel> pathToRoot = new LinkedList<>(categoryService.getPathForCategory(variantValueCategory));

		while (!categoryService.isRoot(pathToRoot.get(0)))
		{
			pathToRoot.addAll(0, categoryService.getPathForCategory(pathToRoot.get(0)));
		}

		return pathToRoot;
	}

}
