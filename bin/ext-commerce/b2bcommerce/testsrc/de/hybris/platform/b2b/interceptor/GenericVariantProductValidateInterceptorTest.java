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

import de.hybris.platform.b2b.model.GenericVariantProductModel;
import de.hybris.platform.b2b.model.VariantCategoryModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.b2b.testframework.ModelFactory;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;


public class GenericVariantProductValidateInterceptorTest extends ServicelayerTransactionalTest
{
	static Logger LOG = LoggerFactory.getLogger(GenericVariantProductValidateInterceptorTest.class);

	@Resource
	ModelFactory modelFactory;

	@Resource
	ModelService modelService;

	@Resource
	GenericVariantProductValidateInterceptor genericVariantProductValidateInterceptor;

	@Resource
	TypeService typeService;

	CatalogVersionModel catalogVersion;

	UnitModel unit;

	@Before
	public void setUp() throws Exception
	{
		catalogVersion = modelFactory.createCatalogVersion("myCatalog", "Standard");
		unit = modelFactory.createUnit("PCS");
	}

	@Test
	public void shouldSaveValidGenericVariant()
	{
		final VariantCategoryModel color = modelFactory.createVariantCategory("color", catalogVersion);

		final VariantCategoryModel size = modelFactory.createVariantCategory("size", catalogVersion);
		size.setSupercategories(Lists.<CategoryModel>newArrayList(color));
		modelService.save(size);

		final VariantCategoryModel fit = modelFactory.createVariantCategory("fit", catalogVersion);
		fit.setSupercategories(Lists.<CategoryModel>newArrayList(size));
		modelService.save(fit);

		final ProductModel base = modelFactory.createProduct("base", catalogVersion, GenericVariantProductModel._TYPECODE, unit, color, size, fit);

		final VariantValueCategoryModel red = modelFactory.createVariantValueCategory("red", color, 1, catalogVersion);
		final VariantValueCategoryModel medium = modelFactory.createVariantValueCategory("M", size, 1, catalogVersion);
		final VariantValueCategoryModel wide = modelFactory.createVariantValueCategory("wide", fit, 1, catalogVersion);

		modelFactory.createGenericVariantProduct("variantProduct", base, catalogVersion, red, medium, wide);
	}

	@Test
	public void shouldPreventAssigningToAWrongMultidimensionalBaseProduct()
	{

		final CategoryModel shoesNormalCategory = modelFactory.createCategory("shoes", catalogVersion);
		final CategoryModel furnitureNormalCategory = modelFactory.createCategory("furniture", catalogVersion);

		final VariantCategoryModel shoeColor = modelFactory.createVariantCategory("color", catalogVersion);
		final VariantValueCategoryModel red = modelFactory.createVariantValueCategory("red", shoeColor, 1, catalogVersion);

		final VariantCategoryModel furnitureMaterial = modelFactory.createVariantCategory("mat", catalogVersion);
		final VariantValueCategoryModel wood = modelFactory.createVariantValueCategory("wood", furnitureMaterial, 1, catalogVersion);

		final ProductModel shoeBase = modelFactory.createProduct("shoeBase", catalogVersion, GenericVariantProductModel._TYPECODE, unit, shoeColor,
				shoesNormalCategory);
		modelFactory.createGenericVariantProduct("shoe1", shoeBase, catalogVersion, red);

		final ProductModel deskBase = modelFactory.createProduct("deskBase", catalogVersion, GenericVariantProductModel._TYPECODE, unit,
				furnitureMaterial, furnitureNormalCategory);
		modelFactory.createGenericVariantProduct("desk1", deskBase, catalogVersion, wood);

		final ProductModel shoeBase2 = modelFactory.createProduct("shoeBase2", catalogVersion, GenericVariantProductModel._TYPECODE, unit, shoeColor);
		final GenericVariantProductModel shoe2 = modelFactory.createGenericVariantProduct("shoe2", shoeBase2, catalogVersion, red);

		try
		{
			shoe2.setBaseProduct(deskBase);
			modelFactory.save(shoe2);
			Assert.fail("A shoe product variant can not be assigned to a desk base product!");
		}
		catch (final Exception e)
		{
			LOG.info("Expected java exception message is" + e.getMessage());
			//ok validation works
		}
		try
		{
			shoe2.setBaseProduct(shoeBase);
			modelFactory.save(shoe2);
		}
		catch (final Exception e)
		{
			Assert.fail("A valid shoe with the same variant value categories should be assigned to a base shoe product instance!");
			//ok validation works
		}
	}

	@Test
	public void shouldValidateTheVariantCategoriesToBeExactlyTheSame()
	{
		final VariantCategoryModel shoeColor = modelFactory.createVariantCategory("color", catalogVersion);
		final VariantValueCategoryModel red = modelFactory.createVariantValueCategory("red", shoeColor, 1, catalogVersion);

		final VariantCategoryModel shoeSize = modelFactory.createVariantCategory("shoeSize", catalogVersion);
		shoeSize.setSupercategories(Lists.<CategoryModel>newArrayList(shoeColor));
		modelService.save(shoeSize);

		final VariantValueCategoryModel size_8 = modelFactory.createVariantValueCategory("size_8", shoeSize, 1, catalogVersion);

		final ProductModel base1 = modelFactory.createProduct("base1", catalogVersion, GenericVariantProductModel._TYPECODE, unit, shoeColor,
				shoeSize);
		modelFactory.createGenericVariantProduct("shoe1", base1, catalogVersion, red, size_8);

		final ProductModel base2 = modelFactory.createProduct("base2", catalogVersion, GenericVariantProductModel._TYPECODE, unit, shoeColor);
		final GenericVariantProductModel shoe2 = modelFactory.createGenericVariantProduct("shoe2", base2, catalogVersion, red);

		try
		{
			shoe2.setSupercategories((Collection) Lists.<VariantValueCategoryModel> newArrayList(red, size_8));
			modelFactory.save(shoe2);
			Assert.fail("shoeBase2 does not have the same variant categories and can not be assigned!");
		}
		catch (final Exception e)
		{
			LOG.info("Expected java exception message is" + e.getMessage());
			//ok validation works
		}

		final ProductModel base3 = modelFactory.createProduct("base3", catalogVersion, GenericVariantProductModel._TYPECODE, unit, shoeColor,
				shoeSize);

		try
		{
			modelFactory.createGenericVariantProduct("shoe3", base3, catalogVersion, red, size_8);
		}
		catch (final Exception e)
		{
			Assert.fail("shoe3 has exactly the same variant categories as its base product");
			//ok validation works
		}
	}

	@Test
	public void shouldValidateTheVariantCategoriesToBeExactlyTheSameType()
	{
		final VariantCategoryModel shoeColor = modelFactory.createVariantCategory("shoeColor", catalogVersion);
		final VariantValueCategoryModel red = modelFactory.createVariantValueCategory("red", shoeColor, 1, catalogVersion);

		final VariantCategoryModel shoeSize = modelFactory.createVariantCategory("shoeSize", catalogVersion);
		shoeSize.setSupercategories(Lists.<CategoryModel>newArrayList(shoeColor));
		modelService.save(shoeSize);

		final VariantCategoryModel shoeFit = modelFactory.createVariantCategory("shoeFit", catalogVersion);
		shoeFit.setSupercategories(Lists.<CategoryModel>newArrayList(shoeSize));
		modelService.save(shoeFit);
		final VariantValueCategoryModel narrow = modelFactory.createVariantValueCategory("narrow", shoeFit, 1, catalogVersion);

		final ProductModel base4 = modelFactory.createProduct("base4", catalogVersion, GenericVariantProductModel._TYPECODE, unit, shoeColor,
				shoeSize);

		try
		{
			modelFactory.createGenericVariantProduct("shoe4", base4, catalogVersion, red, narrow);
			Assert.fail("shoe4 has not the same categories, shoeFit is not included in the base product!");
		}
		catch (final Exception e)
		{
			//ok validation works
		}
	}

}
