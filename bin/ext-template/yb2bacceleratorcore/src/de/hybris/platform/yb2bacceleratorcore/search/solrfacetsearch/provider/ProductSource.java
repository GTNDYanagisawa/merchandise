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
package de.hybris.platform.yb2bacceleratorcore.search.solrfacetsearch.provider;


import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;

import java.util.Collection;
import java.util.Set;


/**
 * ProductSource. Retrieves a collection of product to index for a specific model & index configuration.
 */
public interface ProductSource
{
	/**
	 * Returns a collection of {@link ProductModel} of a given indexedProperty that are fetched from the model based on
	 * the indexConfig.
	 * 
	 * @param indexConfig
	 *           index config
	 * @param indexedProperty
	 *           indexed property
	 * @param model
	 *           model
	 * @return Collection of products
	 */
	Collection<ProductModel> getProductsForConfigAndProperty(IndexConfig indexConfig, IndexedProperty indexedProperty, Object model);


	/**
	 * Gets all the products in the a model this could include all its variants.
	 * 
	 * @param model
	 *           productModel
	 * @return
	 */
	Set<ProductModel> getProducts(final Object productModel);


}
