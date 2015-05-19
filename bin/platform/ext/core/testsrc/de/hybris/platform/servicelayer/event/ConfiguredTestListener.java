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
package de.hybris.platform.servicelayer.event;

import de.hybris.platform.servicelayer.event.MockEventServiceTest.CustomClusterEvent;
import de.hybris.platform.servicelayer.event.MockEventServiceTest.CustomEvent;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;



public class ConfiguredTestListener extends AbstractEventListener<AbstractEvent>
{
	public AbstractEvent lastEvent;



	public AbstractEvent getLastEvent()
	{
		return lastEvent;
	}


	@Override
	protected void onEvent(final AbstractEvent event)
	{
		if (event instanceof CustomEvent || event instanceof CustomClusterEvent)
		{
			this.lastEvent = event;
		}
	}
}
