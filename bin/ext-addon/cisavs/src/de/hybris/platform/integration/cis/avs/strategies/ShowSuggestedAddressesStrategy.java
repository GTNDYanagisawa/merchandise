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
 * Strategy to decide if the suggested addresses should be displayed to the customer.
 */
public interface ShowSuggestedAddressesStrategy
{
	/**
	 * @return true if suggestions should be show
	 */
	boolean shouldAddressSuggestionsBeShown();
}
