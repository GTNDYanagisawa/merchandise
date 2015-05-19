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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import de.hybris.platform.integration.cis.tax.CisTaxDocOrder;
import de.hybris.platform.integration.cis.tax.strategies.ShippingIncludedStrategy;


public class DefaultCisShippingIncludedStrategy implements ShippingIncludedStrategy
{
	/**
	 * Method to determine if shipping was sent as a line item to the tax calculation. If there is an extra line item on
	 * the tax document, that will be the shipping.
	 * 
	 * @param - cisTaxDocOrder - Holds the calculated taxDoc and the order it is related to
	 * @return - boolean of whether or not shipping was included in the tax calculation
	 */
	@Override
	public boolean isShippingIncluded(final CisTaxDocOrder cisTaxDocOrder)
	{
		return cisTaxDocOrder.getTaxDoc().getLineItems().size() > cisTaxDocOrder.getAbstractOrder().getEntries().size();
	}

}
