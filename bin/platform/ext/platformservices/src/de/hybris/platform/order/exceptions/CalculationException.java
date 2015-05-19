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
package de.hybris.platform.order.exceptions;

import de.hybris.platform.order.CalculationService;
import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * A general exception used by {@link CalculationService} extensions if an (expected) error occurs during price
 * calculation or requesting price informations.
 */
public class CalculationException extends BusinessException
{

	public CalculationException(final String message)
	{
		super(message);
	}

	public CalculationException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public CalculationException(final Throwable cause)
	{
		super(cause);
	}

}
