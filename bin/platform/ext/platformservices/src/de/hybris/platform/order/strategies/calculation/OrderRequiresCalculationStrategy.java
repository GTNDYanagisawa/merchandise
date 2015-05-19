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
package de.hybris.platform.order.strategies.calculation;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * Strategy answers if order or order entry requires calculation
 */
public interface OrderRequiresCalculationStrategy
{

	/**
	 * Method checks if the order need to be calculated.
	 * 
	 * @param order
	 *           {@link AbstractOrderModel} to check
	 * @return <code>true</code> if order requires calculation
	 */
	boolean requiresCalculation(AbstractOrderModel order);

	/**
	 * Method checks if the order entry need to be calculated.
	 * 
	 * @param orderEntry
	 *           {@link AbstractOrderEntryModel} to check
	 * @return <code>true</code> if order entry requires calculation
	 */
	boolean requiresCalculation(AbstractOrderEntryModel orderEntry);
}
