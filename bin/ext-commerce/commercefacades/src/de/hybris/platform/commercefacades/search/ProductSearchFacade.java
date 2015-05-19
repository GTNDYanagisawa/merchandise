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
package de.hybris.platform.commercefacades.search;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.List;

/**
 * Product search facade interface.
 * Used to retrieve products of type {@link ProductData} (or subclasses of).
 *
 * @param <ITEM> The type of the product result items
 */
public interface ProductSearchFacade<ITEM extends ProductData>
{
	/**
	 * Initiate a new search using simple free text query.
	 *
	 * @param text the search text
	 * @return the search results
	 */
	ProductSearchPageData<SearchStateData, ITEM> textSearch(String text);

	/**
	 * Refine an exiting search. The query object allows more complex queries using facet selection. The SearchStateData
	 * must have been obtained from the results of a call to {@link #textSearch(String)}.
	 *
	 * @param searchState  the search query object
	 * @param pageableData the page to return
	 * @return the search results
	 */
	ProductSearchPageData<SearchStateData, ITEM> textSearch(SearchStateData searchState, PageableData pageableData);

	/**
	 * Initiate a new search in category.
	 *
	 * @param categoryCode     the category to search in
	 * @return the search results
	 */
	ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> categorySearch(String categoryCode);

	/**
	 * Refine an exiting search. The query object allows more complex queries using facet selection. The SearchStateData
	 * must have been obtained from the results of a call to {@link #categorySearch(String)}.
	 *
	 * @param categoryCode     the category to search in
	 * @param searchState  the search query object
	 * @param pageableData the page to return
	 * @return the search results
	 */
	ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> categorySearch(String categoryCode, SearchStateData searchState, PageableData pageableData);

	/**
	 * Get the auto complete suggestions for the provided input.
	 *
	 * @param input	the user's input
	 * @return a list of suggested search terms
	 */
	List<AutocompleteSuggestionData> getAutocompleteSuggestions(String input);
}
