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
package de.hybris.platform.acceleratorservices.payment.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.strategies.ClientReferenceLookupStrategy;
import de.hybris.platform.order.CartService;
import org.springframework.beans.factory.annotation.Required;


public class DefaultClientReferenceLookupStrategy implements ClientReferenceLookupStrategy
{
	private CartService cartService;
	private static final String DEFAULT_CLIENTREF_ID = "Default_Client_Ref";


	public String lookupClientReferenceId()
	{
		if (getCartService().hasSessionCart())
		{
			return getCartService().getSessionCart().getGuid();
		}
		return DEFAULT_CLIENTREF_ID;
	}


	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}
