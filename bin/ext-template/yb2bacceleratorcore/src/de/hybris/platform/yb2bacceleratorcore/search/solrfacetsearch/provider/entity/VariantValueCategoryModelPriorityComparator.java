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
package de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.entity;

import de.hybris.platform.b2b.model.VariantValueCategoryModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;

import java.util.Comparator;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Required;


/**
 * Comparator for type {@link VariantValueCategoryModel}.
 */
public class VariantValueCategoryModelPriorityComparator implements Comparator<VariantValueCategoryModel>
{
	private CategoryService categoryService;

	@Override
	public int compare(final VariantValueCategoryModel variantValueCategory1, final VariantValueCategoryModel variantValueCategory2)
	{
		final LinkedList<CategoryModel> pathToRoot1 = getPathToRoot(variantValueCategory1);
		final LinkedList<CategoryModel> pathToRoot2 = getPathToRoot(variantValueCategory2);

		return Integer.compare(pathToRoot1.size(), pathToRoot2.size());
	}

	private LinkedList<CategoryModel> getPathToRoot(final VariantValueCategoryModel variantValueCategory)
	{
		final LinkedList<CategoryModel> pathToRoot = new LinkedList<>(getCategoryService().getPathForCategory(variantValueCategory));

		while (!getCategoryService().isRoot(pathToRoot.get(0)))
		{
			pathToRoot.addAll(0, getCategoryService().getPathForCategory(pathToRoot.get(0)));
		}

		return pathToRoot;
	}

	public CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
}
