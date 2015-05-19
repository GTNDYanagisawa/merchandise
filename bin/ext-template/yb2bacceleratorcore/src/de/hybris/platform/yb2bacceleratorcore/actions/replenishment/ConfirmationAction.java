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
package de.hybris.platform.yb2bacceleratorcore.actions.replenishment;

import de.hybris.platform.b2bacceleratorservices.event.ReplenishmentOrderConfirmationEvent;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.event.EventService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Action for confirming orders.
 */
public class ConfirmationAction extends AbstractProceduralAction<ReplenishmentProcessModel>
{
	private EventService eventService;

	@Override
	public void executeAction(final ReplenishmentProcessModel process) throws Exception
	{
		final BusinessProcessParameterModel orderParameter = getProcessParameterHelper().getProcessParameterByName(process, "order");
		final OrderModel order = (OrderModel) orderParameter.getValue();
		getModelService().refresh(order);
		this.getEventService().publishEvent(new ReplenishmentOrderConfirmationEvent(order));
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
