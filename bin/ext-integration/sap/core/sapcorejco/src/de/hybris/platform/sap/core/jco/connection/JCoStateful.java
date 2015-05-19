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

import de.hybris.platform.sap.core.jco.exceptions.BackendException;



/**
 * Interface for marking connections as stateful.
 */
public interface JCoStateful
{
	/**
	 * Gets called if connection should be destroyed.
	 * 
	 * @throws BackendException
	 *            in case of failure.
	 */
	public void destroy() throws BackendException;

}
