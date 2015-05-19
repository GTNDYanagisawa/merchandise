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

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.util.Date;


public abstract class AbstractMerchantEventListener<T extends AbstractEvent> extends AbstractOrderEventListener<T>
{
	protected void createOrderHistoryEntry(final PrincipalModel owner, final OrderModel order, final String description,
			final OrderStatus status)
	{
		final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);
		historyEntry.setTimestamp(new Date());
		historyEntry.setOrder(order);
		historyEntry.setDescription(description);
		historyEntry.setOwner(owner);
		if (this.isCreateSnapshot())
		{
			final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
			snapshot.setStatus(status);
			historyEntry.setPreviousOrderVersion(snapshot);
			getOrderHistoryService().saveHistorySnapshot(snapshot);
		}
		getModelService().save(historyEntry);
	}
}
