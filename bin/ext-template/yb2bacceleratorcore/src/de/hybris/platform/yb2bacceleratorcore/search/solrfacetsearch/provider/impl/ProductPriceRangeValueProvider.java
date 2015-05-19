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
package de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.impl;

import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.AbstractMultidimensionalProductFieldValueProvider;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.entity.SolrPriceRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Value Provider for price range of multidimensional products.
 */
public class ProductPriceRangeValueProvider extends AbstractMultidimensionalProductFieldValueProvider
{

	@Override
	public Object getFieldValue(final ProductModel product)
	{
		String priceRange = null;
		// make sure you have the baseProduct because variantProducts won't have other variants
		final ProductModel baseProduct = getBaseProduct(product);

		final Collection<VariantProductModel> variants = baseProduct.getVariants();
		if (CollectionUtils.isNotEmpty(variants))
		{
			final List<PriceRowModel> allPricesInfos = new ArrayList<PriceRowModel>();

			// collect all price infos
			for (final VariantProductModel variant : variants)
			{
				allPricesInfos.addAll(variant.getEurope1Prices());
			}

			if (!allPricesInfos.isEmpty())
			{
				priceRange = getPriceRangeString(allPricesInfos);
			}
		}

		return priceRange;
	}

	protected String getPriceRangeString(final List<PriceRowModel> allPricesInfos)
	{
		Collections.sort(allPricesInfos, PriceRangeComparator.INSTANCE);

		final PriceRowModel lowest = allPricesInfos.get(0);
		final PriceRowModel highest = allPricesInfos.get(allPricesInfos.size() - 1);
		return SolrPriceRange.buildSolrPropertyFromPriceRows(lowest.getPrice(), lowest.getCurrency().getIsocode(),
				highest.getPrice(), highest.getCurrency().getIsocode());
	}

	public static class PriceRangeComparator extends AbstractComparator<PriceRowModel>
	{
		public static final PriceRangeComparator INSTANCE = new PriceRangeComparator();

		@Override
		protected int compareInstances(final PriceRowModel price1, final PriceRowModel price2)
		{
			if (price1 == null || price1.getPrice() == null)
			{
				return BEFORE;
			}
			if (price2 == null || price2.getPrice() == null)
			{
				return AFTER;
			}

			return Double.compare(price1.getPrice(), price2.getPrice());
		}
	}

}
