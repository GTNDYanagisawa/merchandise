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
package de.hybris.platform.yb2bacceleratorcore.event;

import de.hybris.platform.b2b.event.MerchantRejectedEvent;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;

public class MerchantRejectedEventListener extends AbstractMerchantEventListener<MerchantRejectedEvent>
{
	@Override
	protected void onEvent(final MerchantRejectedEvent event)
	{
		final OrderModel order = event.getOrder();
		setSessionLocaleForOrder(order);
		createOrderHistoryEntry(event.getManager(), order, "", OrderStatus.REJECTED_BY_MERCHANT);
	}
}
