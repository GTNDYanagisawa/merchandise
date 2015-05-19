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
package de.hybris.platform.b2bacceleratorservices.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;


public class ReplenishmentOrderPlacedEvent extends AbstractCommerceUserEvent<BaseSiteModel>
{
	private final CartToOrderCronJobModel cartToOrder;

	public ReplenishmentOrderPlacedEvent(final CartToOrderCronJobModel cartToOrder)
	{
		this.cartToOrder = cartToOrder;
	}

	public CartToOrderCronJobModel getCartToOrderCronJob()
	{
		return cartToOrder;
	}
}
