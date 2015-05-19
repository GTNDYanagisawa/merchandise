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
package de.hybris.platform.sap.sapordermgmtbol.transaction.modulemgmt.impl;

/**
 * This exception is thrown when checking an authorization object was not successful. <br>
 * 
 */
public class MissingAuthorizationException extends Exception
{

	/**
	 * Standard constructor. <br>
	 * 
	 * @param message
	 *           message that describes the missing authorization
	 */
	public MissingAuthorizationException(final String message)
	{
		super(message);
	}

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

}
