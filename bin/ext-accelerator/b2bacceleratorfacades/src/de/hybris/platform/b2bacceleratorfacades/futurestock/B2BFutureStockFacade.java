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
package de.hybris.platform.b2bacceleratorfacades.futurestock;

import de.hybris.platform.commercefacades.product.data.FutureStockData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;
import java.util.Map;


/**
 * Facade for 'Future Stock Management'.
 */
public interface B2BFutureStockFacade
{

	/**
	 * Gets the future product availability for the specified product, for each future date.
	 * 
	 * @param product
	 *           the product
	 * @return A list of quantity ordered by date. If there is no availability for this product in the future, an empty
	 *         list is returned. If the external future stock system is completely not available a null value will be
	 *         returned.
	 */
	List<FutureStockData> getFutureAvailability(ProductModel product);


	/**
	 * Gets the future product availability for the list of specified products, for each future date.
	 * 
	 * @param products
	 *           the products
	 * @return A map of product codes with a list of quantity ordered by date. If the external future stock system is
	 *         completely not available a null value will be returned.
	 */
	Map<String, List<FutureStockData>> getFutureAvailability(List<ProductModel> products);


}
