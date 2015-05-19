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

import de.hybris.platform.servicelayer.event.ClusterAwareEvent;



/**
 * Event is triggered if a RFC Destination gets updated.
 */
public class SAPRFCDestinationUpdateEvent extends SAPRFCDestinationEvent implements ClusterAwareEvent
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7305654596611381815L;

	/**
	 * Default constructor.
	 */
	public SAPRFCDestinationUpdateEvent()
	{
		super();
	}

	/**
	 * Default constructor with RFC destination name.
	 * 
	 * @param rfcDestinationName
	 *           RFC destination name
	 */
	public SAPRFCDestinationUpdateEvent(final String rfcDestinationName)
	{
		super(rfcDestinationName);
	}

	@Override
	public boolean publish(final int sourceNodeId, final int targetNodeId)
	{
		return true;
	}
}
