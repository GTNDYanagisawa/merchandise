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
package de.hybris.platform.webservices;

import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent;
import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.CRUD_METHOD;
import de.hybris.platform.servicelayer.event.events.AbstractWebserviceActionEvent.TRIGGER;


/**
 * Abstraction for a EventAction creation factory.
 */
public interface EventActionFactory
{
	AbstractWebserviceActionEvent createEventAction(AbstractResource resource, CRUD_METHOD method, TRIGGER trigger);
}
