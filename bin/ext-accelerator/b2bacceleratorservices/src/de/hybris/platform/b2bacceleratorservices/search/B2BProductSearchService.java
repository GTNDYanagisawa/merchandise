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
package de.hybris.platform.b2bacceleratorservices.search;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;


public interface B2BProductSearchService<ITEM extends ProductData>
{
	/**
	 * Gets all visible {@link de.hybris.platform.core.model.product.ProductModel} for a given list of SKUs
	 * 
	 * @param SKUs
	 *           String collection of product IDs
	 * @param pageableData
	 *           Pagination information
	 * @return List of paginated {@link ProductModel} objects
	 */
	SearchPageData<ProductModel> findProductsBySkus(Collection<String> skus, PageableData pageableData);

}
