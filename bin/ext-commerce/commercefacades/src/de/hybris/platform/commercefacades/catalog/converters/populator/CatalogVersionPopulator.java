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
package de.hybris.platform.commercefacades.catalog.converters.populator;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.converter.PageablePopulator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link CatalogVersionData} from {@link CatalogVersionModel} using specific {@link CatalogOption}s
 */
public class CatalogVersionPopulator implements ConfigurablePopulator<CatalogVersionModel, CatalogVersionData, CatalogOption>
{
	private CategoryService categoryService;
	private String categoriesUrl = "/categories";
	private PageablePopulator<CategoryModel, CategoryHierarchyData, CatalogOption> categoryHierarchyPopulator;

	@Override
	public void populate(final CatalogVersionModel source, final CatalogVersionData target,
			final Collection<CatalogOption> options) throws ConversionException
	{
		target.setId(source.getVersion());
		target.setLastModified(source.getModifiedtime());
		target.setName(source.getCategorySystemName());
		target.setCategoriesHierarchyData(new ArrayList<CategoryHierarchyData>());

		if (options.contains(CatalogOption.CATEGORIES))
		{
			final Collection<CategoryModel> rootCategories = getCategoryService().getRootCategoriesForCatalogVersion(source);
			for (final CategoryModel category : rootCategories)
			{
				final String catUrl = target.getUrl() + getCategoriesUrl();
				final CategoryHierarchyData categoryData = new CategoryHierarchyData();
				categoryData.setUrl(catUrl);
				getCategoryHierarchyPopulator().populate(category, categoryData, options, PageOption.createWithoutLimits());
				target.getCategoriesHierarchyData().add(categoryData);
			}
		}
	}

	@Required
	public void setCategoryHierarchyPopulator(
			final PageablePopulator<CategoryModel, CategoryHierarchyData, CatalogOption> categoryHierarchyPopulator)
	{
		this.categoryHierarchyPopulator = categoryHierarchyPopulator;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	@Required
	public void setCategoriesUrl(final String categoriesUrl)
	{
		this.categoriesUrl = categoriesUrl;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	protected String getCategoriesUrl()
	{
		return categoriesUrl;
	}

	protected PageablePopulator<CategoryModel, CategoryHierarchyData, CatalogOption> getCategoryHierarchyPopulator()
	{
		return categoryHierarchyPopulator;
	}


}
