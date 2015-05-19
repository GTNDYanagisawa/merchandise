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
package de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.url.UrlResolver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * SearchBreadcrumbBuilder implementation for
 * {@link de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData}
 */
public class SearchBreadcrumbBuilder
{
	private static final String LAST_LINK_CLASS = "active";

	private CommerceCategoryService commerceCategoryService;
	private UrlResolver<CategoryModel> categoryModelUrlResolver;

	public List<Breadcrumb> getBreadcrumbs(final String categoryCode,
			final ProductSearchPageData<SearchStateData, ProductData> searchPageData) throws IllegalArgumentException
	{
		final boolean emptyBreadcrumbs = CollectionUtils.isEmpty(searchPageData.getBreadcrumbs());
		final String searchText = searchPageData.getFreeTextSearch();

		return getBreadcrumbs(categoryCode, searchText, emptyBreadcrumbs);
	}


	public List<Breadcrumb> getBreadcrumbs(final String categoryCode, final String searchText, final boolean emptyBreadcrumbs)
			throws IllegalArgumentException
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<>();

		if (categoryCode == null)
		{
			final Breadcrumb breadcrumb = new Breadcrumb("/search?text=" + getEncodedUrl(searchText),
					StringEscapeUtils.escapeHtml(searchText), (emptyBreadcrumbs ? LAST_LINK_CLASS : ""));
			breadcrumbs.add(breadcrumb);
		}
		else
		{
			// Create category hierarchy path for breadcrumb
			final List<Breadcrumb> categoryBreadcrumbs = new ArrayList<>();
			final Collection<CategoryModel> categoryModels = new ArrayList<>();
			final CategoryModel lastCategoryModel = getCommerceCategoryService().getCategoryForCode(categoryCode);
			categoryModels.addAll(lastCategoryModel.getSupercategories());
			categoryBreadcrumbs.add(getCategoryBreadcrumb(lastCategoryModel, (!emptyBreadcrumbs ? LAST_LINK_CLASS : "")));

			while (!categoryModels.isEmpty())
			{
				final CategoryModel categoryModel = categoryModels.iterator().next();

				if (!(categoryModel instanceof ClassificationClassModel))
				{
					if (categoryModel != null)
					{
						categoryBreadcrumbs.add(getCategoryBreadcrumb(categoryModel));
						categoryModels.clear();
						categoryModels.addAll(categoryModel.getSupercategories());
					}
				}
				else
				{
					categoryModels.remove(categoryModel);
				}
			}
			Collections.reverse(categoryBreadcrumbs);
			breadcrumbs.addAll(categoryBreadcrumbs);
		}
		return breadcrumbs;
	}

	protected String getEncodedUrl(final String url)
	{
		try
		{
			return URLEncoder.encode(url, "utf-8");
		}
		catch (final UnsupportedEncodingException e)
		{
			return url;
		}
	}

	protected Breadcrumb getCategoryBreadcrumb(final CategoryModel category)
	{
		return getCategoryBreadcrumb(category, null);
	}

	protected Breadcrumb getCategoryBreadcrumb(final CategoryModel category, final String linkClass)
	{
		final String categoryUrl = getCategoryModelUrlResolver().resolve(category);
		return new Breadcrumb(categoryUrl, category.getName(), linkClass);
	}

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
}
