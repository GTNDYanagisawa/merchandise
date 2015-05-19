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
package de.hybris.platform.commercefacades.catalog.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CatalogData;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.converter.PageablePopulator;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Default implementation of {@link CatalogFacade}
 */
public class DefaultCatalogFacade implements CatalogFacade
{

	private static final String SLASH = "/";
	private CatalogService catalogService;
	private CategoryService categoryService;
	private CatalogVersionService catalogVersionService;
	private ConfigurablePopulator<CatalogModel, CatalogData, CatalogOption> catalogHierarchyPopulator;
	private ConfigurablePopulator<CatalogVersionModel, CatalogVersionData, CatalogOption> catalogVersionPopulator;
	private PageablePopulator<CategoryModel, CategoryHierarchyData, CatalogOption> categoryHierarchyPopulator;
	private BaseSiteService baseSiteService;

	@Override
	public List<CatalogData> getAllCatalogsWithOptions(final Set<CatalogOption> opts)
	{
		final List<CatalogData> catalogs = new ArrayList<CatalogData>();
		for (final CatalogModel catalog : getCatalogService().getAllCatalogs())
		{
			catalogs.add(getCatalogByIdAndOptions(catalog.getId(), opts));
		}
		return catalogs;
	}

	@Override
	public CatalogData getCatalogByIdAndOptions(final String id, final Set<CatalogOption> options)
	{
		Preconditions.checkNotNull(id, "id cannot be null");

		final CatalogModel catalogModel = getCatalogService().getCatalogForId(id);
		final CatalogData catalog = new CatalogData();
		catalog.setCatalogVersions(new ArrayList<CatalogVersionData>());
		catalog.setId(id);

		getCatalogHierarchyPopulator().populate(catalogModel, catalog, options);

		return catalog;
	}

	@Override
	public CatalogVersionData getCatalogVersionByIdAndOptions(final String catalogId, final String catalogVersionId,
			final Set<CatalogOption> opts)
	{
		Preconditions.checkNotNull(catalogId, "catalogId cannot be null");
		Preconditions.checkNotNull(catalogVersionId, "catalogVersionId cannot be null");

		final CatalogVersionModel catalogVersionModel = catalogVersionService.getCatalogVersion(catalogId, catalogVersionId);
		final CatalogVersionData catalog = new CatalogVersionData();
		catalog.setUrl(SLASH + catalogId + SLASH + catalogVersionId);

		getCatalogVersionPopulator().populate(catalogVersionModel, catalog, opts);

		return catalog;
	}

	@Override
	public CategoryHierarchyData getCategoryById(final String catalogId, final String catalogVersionId, final String categoryId,
			final PageOption page, final Set<CatalogOption> opts)
	{
		final CategoryHierarchyData data = new CategoryHierarchyData();
		data.setUrl(catalogId + SLASH + catalogVersionId);

		final CatalogVersionModel catalogVersionModel = getProductCatalogVersionModelForBaseSite(catalogId, catalogVersionId);
		final CategoryModel category = getCategoryService().getCategoryForCode(catalogVersionModel, categoryId);

		getCategoryHierarchyPopulator().populate(category, data, opts, page);

		return data;
	}


	@Override
	public List<CatalogData> getAllProductCatalogsForCurrentSite(final Set<CatalogOption> opts)
	{
		final List<CatalogModel> productCatalogs = getProductCatalogModelsForBaseSite();
		final List<CatalogData> result = new ArrayList<CatalogData>(productCatalogs.size());
		for (final CatalogModel catalog : productCatalogs)
		{
			result.add(getCatalogByIdAndOptions(catalog.getId(), opts));
		}
		return result;
	}


	@Override
	public CatalogData getProductCatalogForCurrentSite(final String catalogId, final Set<CatalogOption> opts)
	{
		final CatalogModel catalogModel = getProductCatalogModelForBaseSite(catalogId);
		if (catalogModel != null)
		{
			return getCatalogByIdAndOptions(catalogId, opts);
		}
		throw new UnknownIdentifierException("Catalog with code " + catalogId + " not found for the current BaseStore");
	}

	@Override
	public CatalogVersionData getProductCatalogVersionForTheCurrentSite(final String catalogId, final String catalogVersionId,
			final Set<CatalogOption> opts)
	{
		final CatalogVersionModel catalogVersion = getProductCatalogVersionModelForBaseSite(catalogId, catalogVersionId);
		if (catalogVersion != null)
		{
			return getCatalogVersionByIdAndOptions(catalogId, catalogVersionId, opts);
		}
		throw new UnknownIdentifierException("Catalog Version " + catalogId + ":" + catalogVersionId
				+ " not found for the current BaseStore");
	}

	protected List<CatalogModel> getProductCatalogModelsForBaseSite()
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		return getBaseSiteService().getProductCatalogs(currentBaseSite);
	}

	protected CatalogModel getProductCatalogModelForBaseSite(final String catalogId)
	{
		final List<CatalogModel> productCatalogs = getProductCatalogModelsForBaseSite();
		if (productCatalogs != null)
		{
			for (final CatalogModel productCatalog : productCatalogs)
			{
				if (catalogId.equals(productCatalog.getId()))
				{
					return productCatalog;
				}
			}
		}
		return null;
	}

	protected CatalogVersionModel getProductCatalogVersionModelForBaseSite(final String catalogId, final String catalogVersionId)
	{
		final CatalogModel catalog = getProductCatalogModelForBaseSite(catalogId);
		if (catalog != null)
		{
			for (final CatalogVersionModel catalogVersion : catalog.getCatalogVersions())
			{
				if (catalogVersionId.equals(catalogVersion.getVersion()))
				{
					return catalogVersion;
				}
			}
		}
		return null;
	}

	@Required
	public void setCatalogHierarchyPopulator(
			final ConfigurablePopulator<CatalogModel, CatalogData, CatalogOption> catalogHierarchyPopulator)
	{
		this.catalogHierarchyPopulator = catalogHierarchyPopulator;
	}

	@Required
	public void setCatalogService(final CatalogService catalogService)
	{
		this.catalogService = catalogService;
	}

	@Required
	public void setCatalogVersionPopulator(
			final ConfigurablePopulator<CatalogVersionModel, CatalogVersionData, CatalogOption> catalogVersionPopulator)
	{
		this.catalogVersionPopulator = catalogVersionPopulator;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	@Required
	public void setCategoryHierarchyPopulator(
			final PageablePopulator<CategoryModel, CategoryHierarchyData, CatalogOption> categoryHierarchyPopulator)
	{
		this.categoryHierarchyPopulator = categoryHierarchyPopulator;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected CatalogService getCatalogService()
	{
		return catalogService;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	protected ConfigurablePopulator<CatalogModel, CatalogData, CatalogOption> getCatalogHierarchyPopulator()
	{
		return catalogHierarchyPopulator;
	}

	protected ConfigurablePopulator<CatalogVersionModel, CatalogVersionData, CatalogOption> getCatalogVersionPopulator()
	{
		return catalogVersionPopulator;
	}

	protected PageablePopulator<CategoryModel, CategoryHierarchyData, CatalogOption> getCategoryHierarchyPopulator()
	{
		return categoryHierarchyPopulator;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

}
