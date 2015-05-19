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

import de.hybris.platform.b2b.model.VariantCategoryModel;
import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.b2b.testframework.ModelFactory;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;


public class VariantValueCategoryValidateInterceptorTest extends ServicelayerTransactionalTest
{

	static Logger LOG = LoggerFactory.getLogger(VariantValueCategoryValidateInterceptorTest.class);

	@Resource
	ModelFactory modelFactory;

	@Resource
	VariantValueCategoryValidateInterceptor variantValueCategoryValidateInterceptor;

	VariantValueCategoryModel variantValueCategory;

	CatalogVersionModel catalogVersion;

	VariantCategoryModel variantCategory;

	@Before
	public void setUp() throws Exception
	{
		catalogVersion = modelFactory.createCatalogVersion("myCatalog", "Standard");
		variantCategory = modelFactory.createVariantCategory("variantCategory", catalogVersion);
		variantValueCategory = modelFactory.createVariantValueCategory("RED", variantCategory, 1, catalogVersion);
	}

	@Test
	public void shouldSaveAValidVariantValueCategory()
	{
		try
		{
			variantValueCategoryValidateInterceptor.onValidate(variantValueCategory, null);
		}
		catch (final InterceptorException e)
		{
			Assert.fail("This model should have been properly saved");
		}

		Assert.assertEquals("RED", variantValueCategory.getCode());
	}

	@Test
	public void shouldShowErrorIfNoSupercategory()
	{
		variantValueCategory.setSupercategories(Lists.<CategoryModel> newArrayList());

		try
		{
			modelFactory.save(variantValueCategory);
			Assert.fail("More then one category was added as super category! Exception should come.");
		}
		catch (final Exception e)
		{
			LOG.info("Validation message for no supercategory is '{}'", e.getMessage());
		}
	}

	@Test
	public void shouldShowErrorIfMoreThanOneCategory()
	{
		final VariantCategoryModel variantCategory = modelFactory.createVariantCategory("myVariantCategory", catalogVersion);

		final CategoryModel standardCategory = modelFactory.createCategory("standardCategory", catalogVersion);

		variantValueCategory.setSupercategories(Lists.<CategoryModel> newArrayList(variantCategory, standardCategory));

		try
		{
			modelFactory.save(variantValueCategory);
			Assert.fail("More then one category was added as super category! Exception should come.");
		}
		catch (final Exception e)
		{
			LOG.debug("Validation message for too many categories is '{}'", e.getMessage());
		}
	}

	@Test
	public void shouldShowErrorIfWrongCategoryType()
	{

		final VariantCategoryModel variantCategory = modelFactory.createVariantCategory("variantCategory", catalogVersion);
		final CategoryModel standardCategory = modelFactory.createCategory("standardCategory", catalogVersion);

		variantValueCategory.setSupercategories(Lists.<CategoryModel> newArrayList(standardCategory));

		try
		{
			modelFactory.save(variantValueCategory);
			Assert.fail("More then one category was added as super category! Exception should come.");
		}
		catch (final Exception e)
		{
			LOG.debug("Validation message for wrong category type is '{}'", e.getMessage());
		}
	}

	@Test
	public void shouldSaveAValidVariantValueCategoryAndCreateASequenceNumber()
	{
		try
		{
			variantValueCategoryValidateInterceptor.onValidate(variantValueCategory, null);
		}
		catch (final InterceptorException e)
		{
			Assert.fail("This model should have been properly saved");
		}

		modelFactory.save(variantValueCategory);

		Assert.assertNotNull(variantValueCategory.getSequence());
		Assert.assertEquals(Integer.valueOf(1), variantValueCategory.getSequence());
	}

	@Test
	public void shouldFailOnWrongSequenceNumber()
	{
		variantCategory = modelFactory.createVariantCategory("sequenceParent", catalogVersion);
		Assert.assertEquals(0, variantCategory.getCategories().size());
		try
		{
			modelFactory.createVariantValueCategory("c1", variantCategory, -1, catalogVersion);
			Assert.fail("This model has a wrong sequence number and can not be saved");
		}
		catch (final Exception e)
		{
			//ok
		}
		Assert.assertEquals(0, variantCategory.getCategories().size());

		try
		{
			modelFactory.createVariantValueCategory("c1", variantCategory, 0, catalogVersion);
		}
		catch (final Exception e)
		{
			Assert.fail("This model should have been saved since sequence 0 is allowed");
		}
		Assert.assertEquals(1, variantCategory.getCategories().size());

		try
		{
			modelFactory.createVariantValueCategory("c2", variantCategory, 0, catalogVersion);
			Assert.fail("We can not save a model with sequence 0 again! Exception must come");
		}
		catch (final Exception e)
		{
			//ok
		}
		Assert.assertEquals(1, variantCategory.getCategories().size());

		try
		{
			modelFactory.createVariantValueCategory("c3", variantCategory, 1, catalogVersion);
		}
		catch (final Exception e)
		{
			LOG.info("This model should have been saved since sequence 1 is allowed: ", e.getMessage());
			Assert.fail("This model should have been saved since sequence 1 is allowed");
		}
		Assert.assertEquals(2, variantCategory.getCategories().size());

		try
		{
			modelFactory.createVariantValueCategory("c3", variantCategory, 2, catalogVersion);
		}
		catch (final Exception e)
		{
			Assert.fail("This model should have been saved since sequence 2 is allowed, only the variant is the same.");
		}
		Assert.assertEquals(2, variantCategory.getCategories().size());

		try
		{
			modelFactory.createVariantValueCategory("c4", variantCategory, 2, catalogVersion);
			Assert.fail("Sequence 2 is already present.");
		}
		catch (final Exception e)
		{
		}
		Assert.assertEquals(2, variantCategory.getCategories().size());

	}

}
