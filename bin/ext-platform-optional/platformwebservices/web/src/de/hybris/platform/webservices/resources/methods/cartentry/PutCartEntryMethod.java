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
package de.hybris.platform.webservices.resources.methods.cartentry;

import de.hybris.platform.core.dto.order.CartEntryDTO;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.webservices.HttpPutResponseBuilder;


/**
 *
 */
public class PutCartEntryMethod extends HttpPutResponseBuilder<CartEntryModel, CartEntryDTO>
{
	@Override
	public void afterProcessing(final CartEntryDTO dto, final CartEntryModel resource, final boolean wasCreated)
	{
		final CartModel order = resource.getOrder();
		if (resource.getQuantity().longValue() <= 0)
		{
			getResource().getServiceLocator().getModelService().remove(resource);
		}
		// recalculates cart, you can moove it elsewhere, but DO NOT remove!
		getResource().getServiceLocator().getCartService().calculateCart(order);
	}
}
