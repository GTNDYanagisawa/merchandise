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
package de.hybris.platform.b2b.punchout.order;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;

import org.apache.commons.lang.BooleanUtils;


/**
 * Allows for calculation to be enabled only if the order is not a PunchOut order and the default strategy allows it.
 */
public class PunchOutOrderRequiresCalculationStrategy implements OrderRequiresCalculationStrategy
{

	private OrderRequiresCalculationStrategy defaultStrategy;

	/**
	 * @return true only if both default strategy and this strategy are fulfilled
	 */
	@Override
	public boolean requiresCalculation(final AbstractOrderModel order)
	{
		return (order.getPunchOutOrder() == null || BooleanUtils.isFalse(order.getPunchOutOrder()))
				&& defaultStrategy.requiresCalculation(order);
	}

	/**
	 * @return true only if both default strategy and this strategy are fulfilled
	 */
	@Override
	public boolean requiresCalculation(final AbstractOrderEntryModel orderEntry)
	{
		return requiresCalculation(orderEntry.getOrder()) && defaultStrategy.requiresCalculation(orderEntry);
	}

	public OrderRequiresCalculationStrategy getDefaultStrategy()
	{
		return defaultStrategy;
	}

	public void setDefaultStrategy(final OrderRequiresCalculationStrategy defaultStrategy)
	{
		this.defaultStrategy = defaultStrategy;
	}

}
