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
package de.hybris.platform.order.strategies.saving;

/**
 * Strategy for saving order and order entries. Should define a logic that saves the order and order entries and
 * returned refreshed order model. Such strategy may be usefull for some special cases where you need a special routine
 * in order to handle order entries save. I.e if you need to save an order/cart entry at a specifiv entryNumber
 * position.
 */
public interface SaveAbstractOrderStrategy<O>
{
	/**
	 * Saves the given order model and the order entries.
	 * 
	 * @param order
	 *           order model
	 * @return saved and refreshed order model
	 */
	O saveOrder(final O order);
}
