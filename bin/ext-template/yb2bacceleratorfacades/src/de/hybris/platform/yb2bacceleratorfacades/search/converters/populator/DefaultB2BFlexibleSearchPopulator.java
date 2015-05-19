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
 */
package de.hybris.platform.yb2bacceleratorfacades.search.converters.populator;

import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BFlexibleSearchPopulator implements
		ConfigurablePopulator<SearchPageData, ProductSearchPageData, ProductOption>
{
	private static final String ADVANCED_SEARCH_PRODUCT_IDS_DELIMITER = "storefront.advancedsearch.delimiter";
	private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

	@Override
	public void populate(final SearchPageData source, final ProductSearchPageData target, final Collection<ProductOption> options)
			throws ConversionException
	{
		final Collection<String> skus = new ArrayList<String>();

		target.setResults(getProductDataList(source, options, skus));
		target.setPagination(source.getPagination());
		target.setSorts(source.getSorts());
		target.setCurrentQuery(getSearchStateData(skus));
	}

	protected SearchStateData getSearchStateData(final Collection<String> skus)
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(StringUtils.join(skus.toArray(), Config.getParameter(ADVANCED_SEARCH_PRODUCT_IDS_DELIMITER)));

		final SearchStateData searchStateData = new SearchStateData();
		searchStateData.setQuery(searchQueryData);

		return searchStateData;
	}

	protected List<ProductData> getProductDataList(final SearchPageData searchPageData, final Collection<ProductOption> options,
			final Collection<String> skus)
	{
		final List<ProductModel> productModelList = searchPageData.getResults();
		final List<ProductData> productDataList = new ArrayList<>(productModelList.size());

		for (final ProductModel productModel : productModelList)
		{
			final ProductData productData = new ProductData();

			if (productConfiguredPopulator != null && productModel != null)
			{
				productConfiguredPopulator.populate(productModel, productData, options);
			}

			productDataList.add(productData);
			skus.add(productData.getCode());
		}

		return productDataList;
	}

	@Required
	public void setProductConfiguredPopulator(
			final ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator)
	{
		this.productConfiguredPopulator = productConfiguredPopulator;
	}
}