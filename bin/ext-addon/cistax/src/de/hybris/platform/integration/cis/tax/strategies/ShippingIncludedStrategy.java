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
package de.hybris.platform.integration.cis.tax.strategies;

import de.hybris.platform.integration.cis.tax.CisTaxDocOrder;


/**
 * Strategy that will determine whether shipping was sent as a line item to be taxed.
 */
public interface ShippingIncludedStrategy
{
	boolean isShippingIncluded(CisTaxDocOrder cisTaxDocOrder);

}
