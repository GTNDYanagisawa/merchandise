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
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractOrderEventListener<T extends AbstractEvent> extends AbstractEventListener<T>
{
	private boolean createSnapshot;

	private ModelService modelService;
	private OrderHistoryService orderHistoryService;
	private I18NService i18NService;
	private BusinessProcessService businessProcessService;

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected OrderHistoryService getOrderHistoryService()
	{
		return orderHistoryService;
	}

	@Required
	public void setOrderHistoryService(final OrderHistoryService orderHistoryService)
	{
		this.orderHistoryService = orderHistoryService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	protected boolean isCreateSnapshot()
	{
		return createSnapshot;
	}

	public void setCreateSnapshot(final boolean createSnapshot)
	{
		this.createSnapshot = createSnapshot;
	}

	protected void setSessionLocaleForOrder(final OrderModel order)
	{
		if (order.getLocale() != null)
		{
			getI18NService().setCurrentLocale(new Locale(order.getLocale()));
		}
	}

	protected void createOrderHistoryEntry(final PrincipalModel owner, final OrderModel order, final OrderStatus status,
			final String description)
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
