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
package de.hybris.platform.sap.core.configuration.rfc.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.io.Serializable;


/**
 * Abstract RFC destination event.
 */
public abstract class SAPRFCDestinationEvent extends AbstractEvent
{

	private static final long serialVersionUID = 241109374898588797L;

	/**
	 * Constant for RFC destination name.
	 */
	public static final String RFC_DESTINATION_NAME = "rfcDestinationName";

	/**
	 * Constant for JCo trace level.
	 */
	public static final String JCO_TRACE_LEVEL = "jcotracelevel";

	/**
	 * Constant for JCo trace path.
	 */
	public static final String JCO_TRACE_PATH = "jcotracepath";

	/**
	 * Default Constructor.
	 */
	public SAPRFCDestinationEvent()
	{
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param source
	 *           source
	 */
	public SAPRFCDestinationEvent(final Serializable source)
	{
		super(source);
	}
}
