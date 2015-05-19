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
package de.hybris.platform.webservices.impl;

import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent;
import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.CRUD_METHOD;
import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.TRIGGER;
import de.hybris.platform.webservices.AbstractResource;
import de.hybris.platform.webservices.EventActionFactory;


/**
 * Creates {@link AbstractWebserviceActionEvent} instances upon certain resource, triggered before or after on a certain
 * method.
 */
public class DefaultEventActionFactory implements EventActionFactory
{

	@Override
	public AbstractWebserviceActionEvent createEventAction(final AbstractResource resource, final CRUD_METHOD method,
			final TRIGGER trigger)
	{
		return new AbstractWebserviceActionEvent(resource.getResourceId(),
				"GET".equals(method.name()) ? resource.getResourceValue() : null, resource.getParentResource(), resource
						.getSecurityContext().getUserPrincipal(), resource.getUriInfo().getRequestUri())
		{

			@Override
			public de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.CRUD_METHOD getCrudMethod()
			{
				return method;
			}

			@Override
			public de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.TRIGGER getTriggered()
			{
				return trigger;
			}
		};
	}

}
