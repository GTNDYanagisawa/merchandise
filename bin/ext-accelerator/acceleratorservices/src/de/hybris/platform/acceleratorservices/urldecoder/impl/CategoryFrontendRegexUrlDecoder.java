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

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.springframework.beans.factory.annotation.Required;


public class CategoryFrontendRegexUrlDecoder extends BaseFrontendRegexUrlDecoder<CategoryModel>
{

	private CategoryService categoryService;

	@Override
	protected CategoryModel translateId(final String id)
	{
		try
		{
			return getCategoryService().getCategoryForCode(id);
		}
		catch (ModelNotFoundException | UnknownIdentifierException e)
		{
			return null;
		}
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	public CategoryService getCategoryService()
	{
		return this.categoryService;
	}

}
