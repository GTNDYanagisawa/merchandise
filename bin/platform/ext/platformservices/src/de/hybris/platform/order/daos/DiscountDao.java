/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.order.daos;

import de.hybris.platform.core.model.order.price.DiscountModel;

import java.util.Collection;
import java.util.List;


public interface DiscountDao
{

	/**
	 * Finds the {@link DiscountModel} with the specified code.
	 * 
	 * @param code
	 *           the discount code
	 * @return the found {@link DiscountModel}s with the specified code
	 */
	List<DiscountModel> findDiscountsByCode(String code);

	/**
	 * Finds all {@link DiscountModel}s which match the specified code.
	 * 
	 * @param matchedCode
	 *           an SQL-Like statement as String, such as <b>%discountCode%</b>
	 * @return the <code>Collection</code> of all {@link DiscountModel}s which match the specified code
	 */
	Collection<DiscountModel> findDiscountsByMatchedCode(String matchedCode);


	/**
	 * Finds all {@link DiscountModel}s which match the specified currency.
	 * 
	 * @param currencyIsoCode
	 *           isoCode of the matching currency
	 * @return the <code>Collection</code> of all {@link DiscountModel}s with the specified currency.
	 * 
	 * @throws IllegalArgumentException
	 *            if currency is null
	 */
	Collection<DiscountModel> findDiscountsByCurrencyIsoCode(String currencyIsoCode);



}
