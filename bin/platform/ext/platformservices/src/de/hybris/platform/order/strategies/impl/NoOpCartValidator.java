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
package de.hybris.platform.order.strategies.impl;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.CartValidator;


/**
 * This validator does nothing TODO: remove this interface or fill with business code
 */
public class NoOpCartValidator implements CartValidator
{
	@Override
	public void validateCart(final CartModel cart) throws InvalidCartException
	{
		// DOCTODO Document reason, why this block is empty
	}

}
