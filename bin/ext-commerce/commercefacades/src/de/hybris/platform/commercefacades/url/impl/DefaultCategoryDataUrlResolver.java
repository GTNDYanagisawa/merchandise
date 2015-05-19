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
package de.hybris.platform.commercefacades.url.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;

import org.springframework.beans.factory.annotation.Required;


/**
 * URL resolver for CategoryData instances.
 * The pattern could be of the form:
 * /{category-path}/c/{category-code}
 */
public class DefaultCategoryDataUrlResolver extends AbstractUrlResolver<CategoryData>
{
	private final String CACHE_KEY = DefaultCategoryDataUrlResolver.class.getName();

	private CommerceCategoryService commerceCategoryService;
	private UrlResolver<CategoryModel> categoryModelUrlResolver;

	protected CommerceCategoryService getCommerceCategoryService()
	{
		return commerceCategoryService;
	}

	@Required
	public void setCommerceCategoryService(final CommerceCategoryService commerceCategoryService)
	{
		this.commerceCategoryService = commerceCategoryService;
	}

	protected UrlResolver<CategoryModel> getCategoryModelUrlResolver()
	{
		return categoryModelUrlResolver;
	}

	@Required
	public void setCategoryModelUrlResolver(final UrlResolver<CategoryModel> categoryModelUrlResolver)
	{
		this.categoryModelUrlResolver = categoryModelUrlResolver;
	}

	@Override
	protected String getKey(final CategoryData source)
	{
		return CACHE_KEY + "." + source.getCode();
	}

	@Override
	protected String resolveInternal(final CategoryData source)
	{
		final CategoryModel categoryModel = getCommerceCategoryService().getCategoryForCode(source.getCode());
		return getCategoryModelUrlResolver().resolve(categoryModel);
	}
}
