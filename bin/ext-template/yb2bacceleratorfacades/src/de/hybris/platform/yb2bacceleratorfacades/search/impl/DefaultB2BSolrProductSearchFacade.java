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
package de.hybris.platform.yb2bacceleratorfacades.search.impl;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.yb2bacceleratorfacades.search.AbstractB2BProductSearchFacade;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;


public class DefaultB2BSolrProductSearchFacade<ITEM extends ProductData> extends AbstractB2BProductSearchFacade<ITEM>
{

	private ProductSearchFacade<ProductData> productSearchFacade;

	/**
	 * 
	 * <p>
	 * This implementation always throws an {@code UnsupportedOperationException} because the operation is not supported
	 * by SOLR.
	 * 
	 * @throws UnsupportedOperationException
	 *            {@inheritDoc}
	 */
	@Override
	public ProductSearchPageData<SearchStateData, ITEM> searchForSkus(final Collection<String> skus,
			final PageableData pageableData, final Collection<ProductOption> options, final boolean populateMatrix)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ProductSearchPageData<SearchStateData, ITEM> textSearch(final String text)
	{
		return (ProductSearchPageData<SearchStateData, ITEM>) productSearchFacade.textSearch(text);
	}

	@Override
	public ProductSearchPageData<SearchStateData, ITEM> textSearch(final SearchStateData searchState,
			final PageableData pageableData)
	{
		return (ProductSearchPageData<SearchStateData, ITEM>) productSearchFacade.textSearch(searchState, pageableData);
	}

	@Override
	public ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> categorySearch(final String categoryCode)
	{
		return (ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData>) productSearchFacade
				.categorySearch(categoryCode);
	}

	@Override
	public ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData> categorySearch(final String categoryCode,
			final SearchStateData searchState, final PageableData pageableData)
	{
		return (ProductCategorySearchPageData<SearchStateData, ITEM, CategoryData>) productSearchFacade.categorySearch(
				categoryCode, searchState, pageableData);
	}

	@Override
	public List<AutocompleteSuggestionData> getAutocompleteSuggestions(final String input)
	{
		return productSearchFacade.getAutocompleteSuggestions(input);
	}

	@Override
	public ProductSearchPageData<SearchStateData, ITEM> textSearch(final String text, final boolean populateMatrix)
	{
		final ProductSearchPageData productSearchPageData = textSearch(text);
		if (populateMatrix)
		{
			populateVariantProducts(productSearchPageData);
		}
		return productSearchPageData;
	}

	@Override
	public ProductSearchPageData<SearchStateData, ITEM> textSearch(final SearchStateData searchState,
			final PageableData pageableData, final boolean populateMatrix)
	{
		final ProductSearchPageData productSearchPageData = textSearch(searchState, pageableData);

		if (populateMatrix)
		{
			populateVariantProducts(productSearchPageData);
		}

		return productSearchPageData;
	}

    protected ProductSearchFacade<ProductData> setProductSearchFacade() {
        return this.productSearchFacade;
    }

    @Required
    public void setProductSearchFacade(ProductSearchFacade<ProductData> productSearchFacade) {
        this.productSearchFacade = productSearchFacade;
    }
}
