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
package de.hybris.platform.yb2bacceleratorfacades.search;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.Collection;


/**
 * B2B Product search facade interface. Used to retrieve products of type
 * {@link de.hybris.platform.commercefacades.product.data.ProductData} (or subclasses of).
 * 
 * @param <ITEM>
 *           The type of the product result items
 */
public interface B2BProductSearchFacade<ITEM extends ProductData> extends ProductSearchFacade<ITEM>
{
	/**
	 * Initiate a new search using simple free text query.
	 * 
	 * @param text
	 *           the search text
	 * @param populateMatrix
	 *           if you needs the variant matrix to be populated
	 * @return the search results
	 */
	ProductSearchPageData<SearchStateData, ITEM> textSearch(String text, boolean populateMatrix);

	/**
	 * Refine an exiting search. The query object allows more complex queries using facet selection. The SearchStateData
	 * must have been obtained from the results of a call to {@link #textSearch(String, boolean)}.
	 * 
	 * @param searchState
	 *           the search query object
	 * @param pageableData
	 *           the page to return
	 * @param populateMatrix
	 *           if you needs the variant matrix to be populated
	 * 
	 * @return the search results
	 */
	ProductSearchPageData<SearchStateData, ITEM> textSearch(SearchStateData searchState, PageableData pageableData,
			boolean populateMatrix);

	/**
	 * Initiate a new search using a collection of product IDs (SKUS)
	 * 
	 * @param skus
	 *           the collection of product IDs
	 * @param options
	 *           options set that determines amount of information that will be attached for each returned product. If
	 *           empty or null default BASIC option is assumed
	 * @return the search results
	 */
	ProductSearchPageData<SearchStateData, ITEM> searchForSkus(Collection<String> skus, PageableData pageableData,
			Collection<ProductOption> options, boolean populateMatrix);
}
