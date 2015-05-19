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
package de.hybris.platform.integration.cis.avs.strategies;

/**
 * Strategy to define the number of suggested addresses to show.
 */
public interface SuggestedAddressesAmountStrategy
{
	/**
	 * Get the number of suggestions.
	 * 
	 * @return the number of suggestions
	 */
	int getSuggestedAddressesAmountToDisplay();
}
