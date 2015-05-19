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

/**
 * Restricted interface for internal use to react on JCoException.JCO_ERROR_COMMUNICATION.
 */
public interface JCoManagedConnectionContainerRestricted
{

	/**
	 * Removes the Stateful connection from managedConnection container.
	 * 
	 * @param connection
	 *           JCo Connection
	 */
	public void removeConnection(final JCoConnection connection);
}
