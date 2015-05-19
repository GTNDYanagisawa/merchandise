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
package de.hybris.platform.webservices.processor.impl;

import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent;
import de.hybris.platform.webservices.AbstractResponseBuilder;
import de.hybris.platform.webservices.EventActionFactory;
import de.hybris.platform.webservices.processchain.RequestProcessChain;
import de.hybris.platform.webservices.processor.RequestProcessor;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * Notification processor responsible for publishing events after/before every CRUD method for
 * {@link de.hybris.platform.webservices.AbstractResource}.
 * 
 */
public class NotificationRequestProcessor<RESOURCE> implements RequestProcessor<RESOURCE>
{
	private EventActionFactory eventActionFactory;


	@Override
	public void doProcess(final RequestProcessor.RequestType type, final Object dto,
			final AbstractResponseBuilder<RESOURCE, ?, ?> responseBuilder, final RequestProcessChain chain)
	{
		try
		{
			responseBuilder.getServiceLocator().getEventService().publishEvent(eventActionFactory.createEventAction(//
					responseBuilder.getResource(),//
					toCrudEnum(type),//
					AbstractWebserviceActionEvent.TRIGGER.BEFORE//
					));

			chain.doProcess();
		}
		finally
		{
			responseBuilder.getServiceLocator().getEventService().publishEvent(eventActionFactory.createEventAction(//
					responseBuilder.getResource(),//
					toCrudEnum(type),//
					AbstractWebserviceActionEvent.TRIGGER.AFTER//
					));
		}
	}


	private de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.CRUD_METHOD toCrudEnum(
			final RequestProcessor.RequestType type)
	{
		return de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.CRUD_METHOD.valueOf(type.name());
	}

	@Required
	public void setEventActionFactory(final EventActionFactory eventActionFactory)
	{
		this.eventActionFactory = eventActionFactory;
	}

}
