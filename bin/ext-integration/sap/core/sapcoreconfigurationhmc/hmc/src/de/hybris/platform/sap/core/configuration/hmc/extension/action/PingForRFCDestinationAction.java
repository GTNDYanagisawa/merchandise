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
package de.hybris.platform.sap.core.configuration.hmc.extension.action;

import de.hybris.platform.hmc.util.action.ActionEvent;
import de.hybris.platform.hmc.util.action.ActionResult;
import de.hybris.platform.hmc.util.action.ItemAction;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationPingEvent;
import de.hybris.platform.servicelayer.event.EventService;


/**
 * Class is used to trigger a ping request for a selected RFC destination in hMC.
 */
public class PingForRFCDestinationAction extends ItemAction
{

	private static final long serialVersionUID = 1L;

	/**
	 * Overwrites the super implementation.
	 * <p>
	 * Triggers a ping request for the given RFC destination.
	 * </p>
	 * 
	 * @param actionEvent
	 *           action event
	 * @return actionResult information
	 * @throws JaloBusinessException
	 *            jalo business exception
	 * @see de.hybris.platform.hmc.util.action.ItemAction#perform(de.hybris.platform.hmc.util.action.ActionEvent)
	 */
	@Override
	public ActionResult perform(final ActionEvent actionEvent) throws JaloBusinessException
	{
		final Item item = getItem(actionEvent);
		if (item == null)
		{
			return new ActionResult(ActionResult.FAILED, "RFC destination not created.", false);
		}
		else
		{
			final String defDestName = (String) item.getAttribute("rfcDestinationName");
			final SAPRFCDestinationPingEvent event = new SAPRFCDestinationPingEvent(defDestName);
			getEventService().publishEvent(event);
			return new ActionResult(event.getResultIndicator(), event.getMessage(), event.isNeedRefresh());
		}

	}

	/**
	 * Returns the {@link EventService}.
	 * 
	 * @return {@link EventService}
	 */
	private EventService getEventService()
	{
		return (EventService) GenericFactoryProvider.getInstance().getBean("eventService");
	}
}
