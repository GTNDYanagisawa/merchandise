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
package de.hybris.platform.sap.core.configuration.hmc.extension;

import de.hybris.platform.hmc.AbstractEditorMenuChip;
import de.hybris.platform.hmc.AbstractExplorerMenuTreeNodeChip;
import de.hybris.platform.hmc.EditorTabChip;
import de.hybris.platform.hmc.extension.HMCExtension;
import de.hybris.platform.hmc.extension.MenuEntrySlotEntry;
import de.hybris.platform.hmc.generic.ClipChip;
import de.hybris.platform.hmc.generic.ToolbarActionChip;
import de.hybris.platform.hmc.util.action.ActionResult;
import de.hybris.platform.hmc.webchips.Chip;
import de.hybris.platform.hmc.webchips.DisplayState;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;
import de.hybris.platform.sap.core.configuration.jalo.SAPRFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationJCoTraceEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationRemoveEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationUpdateEvent;
import de.hybris.platform.servicelayer.event.EventService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


/**
 * Provides necessary meta information about the sapcoreconfiguration hmc extension.
 * 
 * @version ExtGen v5.0.3.0
 */
@SuppressWarnings("deprecation")
public class SapcoreconfigurationHMCExtension extends HMCExtension
{
	/**
	 * Edit the local|project.properties to change logging behavior (properties log4j.*).
	 */
	private static final Logger LOG = Logger.getLogger(SapcoreconfigurationHMCExtension.class.getName());

	/** Path to the resource bundles. */
	public final static String RESOURCE_PATH = "localization.sapcoreconfigurationhmc-locales";

	@SuppressWarnings(
	{ "unchecked", "javadoc" })
	@Override
	public List<AbstractExplorerMenuTreeNodeChip> getTreeNodeChips(final DisplayState displayState, final Chip parent)
	{
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings(
	{ "unchecked", "javadoc" })
	@Override
	public List<MenuEntrySlotEntry> getMenuEntrySlotEntries(final DisplayState displayState, final Chip parent)
	{
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings(
	{ "unchecked", "javadoc" })
	@Override
	public List<ClipChip> getSectionChips(final DisplayState displayState, final ClipChip parent)
	{
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings(
	{ "unchecked", "javadoc" })
	@Override
	public List<EditorTabChip> getEditorTabChips(final DisplayState displayState, final AbstractEditorMenuChip parent)
	{
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings(
	{ "unchecked", "javadoc" })
	@Override
	public List<ToolbarActionChip> getToolbarActionChips(final DisplayState displayState, final Chip parent)
	{
		return Collections.EMPTY_LIST;
	}

	@SuppressWarnings("javadoc")
	@Override
	public ResourceBundle getLocalizeResourceBundle(final Locale locale)
	{
		return null;
	}

	@SuppressWarnings("javadoc")
	@Override
	public String getResourcePath()
	{
		return RESOURCE_PATH;
	}

	@SuppressWarnings(
	{ "rawtypes", "javadoc" })
	@Override
	public ActionResult afterRemove(final ComposedType itemType, final DisplayState displayState, final Map values,
			final ActionResult actionResult)
	{
		if (actionResult.getResult() == ActionResult.OK && values != null
				&& values.containsKey(SAPRFCDestinationEvent.RFC_DESTINATION_NAME)
				&& values.get(SAPRFCDestinationEvent.RFC_DESTINATION_NAME) != null)
		{
			getEventService().publishEvent(
					new SAPRFCDestinationRemoveEvent(values.get(SAPRFCDestinationEvent.RFC_DESTINATION_NAME).toString()));
		}
		return super.afterRemove(itemType, displayState, values, actionResult);

	}

	@SuppressWarnings(
	{ "rawtypes", "javadoc" })
	@Override
	public ActionResult afterSave(final Item item, final DisplayState displayState, final Map currentValues,
			final ActionResult actionResult)
	{
		afterSaveRFCDestination(item, currentValues, actionResult);
		return super.afterSave(item, displayState, currentValues, actionResult);

	}

	/**
	 * Perform after saving RFC destination actions.
	 * 
	 * @param item
	 *           item to be saved
	 * @param currentValues
	 *           current values
	 * @param actionResult
	 *           {@link ActionResult}
	 */
	@SuppressWarnings("rawtypes")
	private void afterSaveRFCDestination(final Item item, final Map currentValues, final ActionResult actionResult)
	{
		if (actionResult.getResult() == ActionResult.OK && item instanceof SAPRFCDestination)
		{
			try
			{
				if ((currentValues != null && !currentValues.isEmpty())
						&& (item != null && item.getAttribute(SAPRFCDestinationEvent.RFC_DESTINATION_NAME) != null))
				{
					// don't publish event in case of turn on / off JCo trace
					// and / or set JCo trace path
					if ((currentValues.size() > 2)
							|| ((currentValues.size() <= 2 && !currentValues.containsKey(SAPRFCDestinationEvent.JCO_TRACE_LEVEL) && !currentValues
									.containsKey(SAPRFCDestinationEvent.JCO_TRACE_PATH))))
					{
						getEventService().publishEvent(
								new SAPRFCDestinationUpdateEvent(item.getAttribute(SAPRFCDestinationEvent.RFC_DESTINATION_NAME)
										.toString()));
					}
					else
					{
						// trigger the SAPRFCDestinationJCoTraceEvent if trace level has changed
						if (currentValues.containsKey(SAPRFCDestinationEvent.JCO_TRACE_LEVEL))
						{
							final SAPRFCDestinationJCoTraceEvent event = new SAPRFCDestinationJCoTraceEvent(currentValues.get(
									SAPRFCDestinationEvent.JCO_TRACE_LEVEL).toString());
							if (currentValues.containsKey(SAPRFCDestinationEvent.JCO_TRACE_PATH))
							{
								String path = null;
								if (currentValues.get(SAPRFCDestinationEvent.JCO_TRACE_PATH) != null)
								{
									path = currentValues.get(SAPRFCDestinationEvent.JCO_TRACE_PATH).toString();
								}
								event.setJCoTracePath(path);
							}
							getEventService().publishEvent(event);
						}
					}
				}
			}
			catch (final JaloInvalidParameterException e)
			{
				LOG.error("JaloInvalidParameterException in afterSave method", e.getThrowable());

			}
			catch (final JaloSecurityException e)
			{
				LOG.error("JaloSecurityException in afterSave method", e.getThrowable());
			}
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
