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
package de.hybris.platform.acceleratorservices.dataimport.batch.stock;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.stock.StockService;


/**
 * Adapter to translate a stock import row into a service call to {@link StockService}.
 */
public interface StockImportAdapter
{
	/**
	 * Import a stock value by calling the {@link StockService}.
	 * 
	 * @param cellValue
	 * @param product
	 * @throws IllegalArgumentException
	 *            if the cellValue is empty, null or invalid or the product is null
	 */
	void performImport(String cellValue, Item product);

}
