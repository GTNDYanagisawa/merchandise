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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider.ProductSource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;


/**
 * Find the products for a variant product and all its variants' categories.
 */
public class VariantProductSource implements ProductSource
{


	@Override
	public Collection<ProductModel> getProductsForConfigAndProperty(final IndexConfig indexConfig,
			final IndexedProperty indexedProperty, final Object model)
	{
		return getProducts(model);
	}


	/**
	 * Get all the variant products of a base product
	 */
	@Override
	public Set<ProductModel> getProducts(final Object model)
	{
		final Set<ProductModel> products = getBaseProducts(model);
		final Set<ProductModel> allProducts = new HashSet<ProductModel>();

		for (final ProductModel productModel : products)
		{
			final Collection<ProductModel> variants = (Collection) productModel.getVariants();
			if (CollectionUtils.isNotEmpty(variants))
			{
				allProducts.addAll(variants);
			}
		}

		allProducts.addAll(products);

		return allProducts;

	}

	protected Set<ProductModel> getBaseProducts(final Object model)
	{
		if (model instanceof VariantProductModel)
		{
			// Collect all the variant products and all their super variants, until the final base product is hit
			final Set<ProductModel> products = new HashSet<ProductModel>();

			ProductModel currentProduct = (ProductModel) model;
			while (currentProduct instanceof VariantProductModel)
			{
				products.add(currentProduct);
				currentProduct = ((VariantProductModel) currentProduct).getBaseProduct();
			}

			products.add(currentProduct);
			return products;
		}
		else if (model instanceof ProductModel)
		{
			return Collections.singleton((ProductModel) model);
		}
		return Collections.emptySet();
	}



}
