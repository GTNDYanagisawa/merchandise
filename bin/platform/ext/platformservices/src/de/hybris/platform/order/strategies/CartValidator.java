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
package de.hybris.platform.order.strategies;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;


/**
 * A cart validator.
 * 
 * @spring.bean cartValidator
 */
public interface CartValidator
{
	/**
	 * validates the {@link CartModel}. <b>Currently not in use!</b>
	 * 
	 * @param cart
	 *           the cart
	 * @throws InvalidCartException
	 *            an exception
	 */
	void validateCart(CartModel cart) throws InvalidCartException;
}
