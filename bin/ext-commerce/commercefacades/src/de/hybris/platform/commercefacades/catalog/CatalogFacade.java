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
package de.hybris.platform.commercefacades.catalog;

import de.hybris.platform.commercefacades.catalog.data.CatalogData;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;

import java.util.List;
import java.util.Set;


/**
 * Catalog facade interface. Its main purpose is to retrieve catalog related DTOs using existing services.
 */
public interface CatalogFacade
{
	/**
	 * Returns catalog DTO for catalog id and options (BASIC, PRODUCTS, CATEGORIES)
	 */
	CatalogData getCatalogByIdAndOptions(String id, Set<CatalogOption> options);

	/**
	 * Returns catalog version DTO for catalog id, catalog version id and options (BASIC, PRODUCTS, CATEGORIES)
	 */
	CatalogVersionData getCatalogVersionByIdAndOptions(String catalogId, String catalogVersionId, Set<CatalogOption> opts);

	/**
	 * Returns catalog DTOs for all catalogs and options (BASIC, PRODUCTS, CATEGORIES)
	 */
	List<CatalogData> getAllCatalogsWithOptions(Set<CatalogOption> opts);

	/**
	 * Returns category DTO for catalog id, catalog version id and category code and options (BASIC, PRODUCTS)
	 */
	CategoryHierarchyData getCategoryById(String catalogId, String catalogVersionId, String categoryId, PageOption page,
			Set<CatalogOption> opts);

	/**
	 * Returns product catalogs for the current base site
	 * 
	 * @param opts
	 */
	List<CatalogData> getAllProductCatalogsForCurrentSite(Set<CatalogOption> opts);

	/**
	 * Returns current base site product catalog by id
	 * 
	 * @param opts
	 */
	CatalogData getProductCatalogForCurrentSite(String catalogId, Set<CatalogOption> opts);

	/**
	 * Returns catalog version DTO for catalog id, catalog version id, current base site and options (BASIC, PRODUCTS,
	 * CATEGORIES)
	 */
	CatalogVersionData getProductCatalogVersionForTheCurrentSite(String catalogId, String catalogVersionId, Set<CatalogOption> opts);
}
