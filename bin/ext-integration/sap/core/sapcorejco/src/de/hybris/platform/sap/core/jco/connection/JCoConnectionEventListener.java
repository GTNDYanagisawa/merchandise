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
package de.hybris.platform.sap.core.jco.connection;

import de.hybris.platform.sap.core.jco.connection.impl.ConnectionEventListener;


/**
 * This interface should be implemented by a class which wants to be notified about JCo Calls to the SAP system.
 */
public interface JCoConnectionEventListener extends ConnectionEventListener
{
	/**
	 * Method is called when a connection event occurs.
	 * 
	 * @param event
	 *           Event
	 */
	public void connectionEvent(JCoConnectionEvent event);
}
