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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populate the product date with the product's categories
 */
public class ProductCategoriesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET>
{
	private Converter<CategoryModel, CategoryData> categoryConverter;
	private CommerceProductService commerceProductService;

	protected Converter<CategoryModel, CategoryData> getCategoryConverter()
	{
		return categoryConverter;
	}

	@Required
	public void setCategoryConverter(final Converter<CategoryModel, CategoryData> categoryConverter)
	{
		this.categoryConverter = categoryConverter;
	}

	protected CommerceProductService getCommerceProductService()
	{
		return commerceProductService;
	}

	@Required
	public void setCommerceProductService(final CommerceProductService commerceProductService)
	{
		this.commerceProductService = commerceProductService;
	}

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{
		final Collection<CategoryModel> categories = getCommerceProductService()
				.getSuperCategoriesExceptClassificationClassesForProduct(productModel);
		productData.setCategories(Converters.convertAll(categories, getCategoryConverter()));
	}
}
