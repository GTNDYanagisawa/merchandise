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
package de.hybris.platform.commercefacades.search.solrfacetsearch.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.ProductSearchAutocompleteService;
import de.hybris.platform.commerceservices.search.ProductSearchService;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.AutocompleteSuggestion;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.threadcontext.ThreadContextService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 */
public class DefaultSolrProductSearchFacade<ITEM extends ProductData> implements ProductSearchFacade<ITEM>
{
	private ProductSearchService<SolrSearchQueryData, SearchResultValueData, ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>> productSearchService;
	private Converter<ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>, ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData>> productCategorySearchPageConverter;
	private Converter<SearchQueryData, SolrSearchQueryData> searchQueryDecoder;
	private Converter<AutocompleteSuggestion, AutocompleteSuggestionData> autocompleteSuggestionConverter;
	private ProductSearchAutocompleteService<AutocompleteSuggestion> autocompleteService;
	private ThreadContextService threadContextService;

	protected ProductSearchService<SolrSearchQueryData, SearchResultValueData, ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>> getProductSearchService()
	{
		return productSearchService;
	}

	@Required
	public void setProductSearchService(
			final ProductSearchService<SolrSearchQueryData, SearchResultValueData, ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>> productSearchService)
	{
		this.productSearchService = productSearchService;
	}

	protected Converter<ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>, ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData>> getProductCategorySearchPageConverter()
	{
		return productCategorySearchPageConverter;
	}

	@Required
	public void setProductCategorySearchPageConverter(
			final Converter<ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>, ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData>> productCategorySearchPageConverter)
	{
		this.productCategorySearchPageConverter = productCategorySearchPageConverter;
	}

	protected Converter<SearchQueryData, SolrSearchQueryData> getSearchQueryDecoder()
	{
		return searchQueryDecoder;
	}

	@Required
	public void setSearchQueryDecoder(final Converter<SearchQueryData, SolrSearchQueryData> searchQueryDecoder)
	{
		this.searchQueryDecoder = searchQueryDecoder;
	}

	protected Converter<AutocompleteSuggestion, AutocompleteSuggestionData> getAutocompleteSuggestionConverter()
	{
		return autocompleteSuggestionConverter;
	}

	@Required
	public void setAutocompleteSuggestionConverter(
			final Converter<AutocompleteSuggestion, AutocompleteSuggestionData> autocompleteSuggestionConverter)
	{
		this.autocompleteSuggestionConverter = autocompleteSuggestionConverter;
	}

	protected ProductSearchAutocompleteService<AutocompleteSuggestion> getAutocompleteService()
	{
		return autocompleteService;
	}

	@Required
	public void setAutocompleteService(final ProductSearchAutocompleteService<AutocompleteSuggestion> autocompleteService)
	{
		this.autocompleteService = autocompleteService;
	}

	protected ThreadContextService getThreadContextService()
	{
		return threadContextService;
	}

	@Required
	public void setThreadContextService(final ThreadContextService threadContextService)
	{
		this.threadContextService = threadContextService;
	}


	@Override
	public ProductSearchPageData<SearchStateData, ITEM> textSearch(final String text)
	{
		return getThreadContextService().executeInContext(
				new ThreadContextService.Executor<ProductSearchPageData<SearchStateData, ITEM>, ThreadContextService.Nothing>()
				{
					@Override
					public ProductSearchPageData<SearchStateData, ITEM> execute()
					{
						return getProductCategorySearchPageConverter().convert(getProductSearchService().textSearch(text, null));
					}
				});
	}

	@Override
	public ProductSearchPageData<SearchStateData, ITEM> textSearch(final SearchStateData searchState,
			final PageableData pageableData)
	{
		Assert.notNull(searchState, "SearchStateData must not be null.");

		return getThreadContextService().executeInContext(
				new ThreadContextService.Executor<ProductSearchPageData<SearchStateData, ITEM>, ThreadContextService.Nothing>()
				{
					@Override
					public ProductSearchPageData<SearchStateData, ITEM> execute()
					{
						return getProductCategorySearchPageConverter().convert(
								getProductSearchService().searchAgain(decodeState(searchState, null), pageableData));
					}
				});
	}

	@Override
	public ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> categorySearch(final String categoryCode)
	{
		return getThreadContextService()
				.executeInContext(
						new ThreadContextService.Executor<ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData>, ThreadContextService.Nothing>()
						{
							@Override
							public ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> execute()
							{
								return getProductCategorySearchPageConverter().convert(
										getProductSearchService().categorySearch(categoryCode, null));
							}
						});
	}

	@Override
	public ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> categorySearch(final String categoryCode,
			final SearchStateData searchState, final PageableData pageableData)
	{
		Assert.notNull(searchState, "SearchStateData must not be null.");

		return getThreadContextService()
				.executeInContext(
						new ThreadContextService.Executor<ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData>, ThreadContextService.Nothing>()
						{
							@Override
							public ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> execute()
							{
								return getProductCategorySearchPageConverter().convert(
										getProductSearchService().searchAgain(decodeState(searchState, categoryCode), pageableData));
							}
						});
	}


	@Override
	public List<AutocompleteSuggestionData> getAutocompleteSuggestions(final String input)
	{
		return getThreadContextService().executeInContext(
				new ThreadContextService.Executor<List<AutocompleteSuggestionData>, ThreadContextService.Nothing>()
				{
					@Override
					public List<AutocompleteSuggestionData> execute()
					{
						return Converters
								.convertAll(getAutocompleteService().getAutocompleteSuggestions(input), getAutocompleteSuggestionConverter());
					}
				});

	}


	protected SolrSearchQueryData decodeState(final SearchStateData searchState, final String categoryCode)
	{
		final SolrSearchQueryData searchQueryData = getSearchQueryDecoder().convert(searchState.getQuery());
		if (categoryCode != null)
		{
			searchQueryData.setCategoryCode(categoryCode);
		}
		return searchQueryData;
	}
}
