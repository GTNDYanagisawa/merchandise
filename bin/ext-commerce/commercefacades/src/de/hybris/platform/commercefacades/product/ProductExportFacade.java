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
package de.hybris.platform.commercefacades.product;

import de.hybris.platform.commercefacades.product.data.ProductResultData;

import java.util.Collection;
import java.util.Date;


/**
 * Product export facade interface. Its main purpose is to retrieve products for export operations.
 */
public interface ProductExportFacade
{

	/**
	 * Retrieves all products
	 * 
	 * @param start
	 *           index position of the first Product, which will be included in the returned List
	 * @param count
	 *           number of Products which will be returned in the List
	 * @param options
	 *           options set that determines amount of information that will be attached to the returned product.
	 * @return {@link ProductResultData}
	 */
	ProductResultData getAllProductsForOptions(final String catalog, final String version,
			Collection<ProductOption> options, final int start, final int count);

	/**
	 * Retrieves products that were modified after timestamp
	 * 
	 * @param modifiedTime
	 *           timestamp
	 * @param start
	 *           index position of the first Product, which will be included in the returned List
	 * @param count
	 *           number of Products which will be returned in the List
	 * @param options
	 *           options set that determines amount of information that will be attached to the returned product.
	 * @return {@link ProductResultData}
	 */
	ProductResultData getOnlyModifiedProductsForOptions(String catalog, String version, Date modifiedTime,
			Collection<ProductOption> options, int start, int count);

}
