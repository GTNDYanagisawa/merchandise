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
package de.hybris.platform.webservices.price;

import de.hybris.platform.core.model.product.UnitModel;

import java.util.List;
import java.util.Set;


/**
 * This interface provides the methods for all possible prices.
 */
public interface Prices
{

	/**
	 * Checks if this list contains elements.
	 * 
	 * @return true if not empty
	 */
	boolean isAvailable();

	/**
	 * Returns a list of all available {@link Price}.
	 * 
	 * @return list of {@link Price}
	 */
	List<Price> getPricingList();

	/**
	 * Returns the {@link Price} which shall be used as default one from all available {@link Price}.<br/>
	 * 
	 * @return default price {@link Price}
	 */
	Price getDefaultPricing();

	/**
	 * Searches for all available units which are bound to the prices.
	 * 
	 * @return set of unit {@link UnitModel}
	 */
	Set<UnitModel> getOrderableUnits();

	/**
	 * In general this is the price which is the best in relation to the quantity. (price per unit))
	 * 
	 * @return best-value price {@link Price}
	 */
	Price getBestValuePrice();

	/**
	 * Returns the {@link Price} for the minimum amount which must be taken.
	 * 
	 * @return the lowest {@link Price}
	 */
	Price getLowestQuantityPrice();

}
