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
 */
package de.hybris.platform.ycommercewebservices.exceptions;

import de.hybris.platform.order.exceptions.CalculationException;

import javax.servlet.ServletException;


/**
 * Thrown when recalculation of cart is impossible due to any reasons.
 */
public class RecalculationException extends ServletException
{
	public RecalculationException(final CalculationException exception, final String currencyIso)
	{
		super("Cannot recalculate cart for currency: " + currencyIso, exception);
	}
}
