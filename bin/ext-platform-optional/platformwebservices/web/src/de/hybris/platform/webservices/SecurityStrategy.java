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

/**
 * The interface for security strategies.
 * <p>
 * Allowed access operations: read, change, create, remove. AccessManager operations.
 */
public interface SecurityStrategy
{
	/**
	 * Method checks whether current session user has permission to work with current resource for the specified access
	 * operation.
	 * 
	 * @param resource
	 *           The requested resource which will be checked.
	 * 
	 * @param operation
	 *           The read, change, create or remove access operations.
	 * 
	 * @return true if allowed.
	 */
	boolean isResourceOperationAllowed(final RestResource resource, final String operation);

	/**
	 * Method checks whether current session user has permission to execute specified command on current resource.
	 * 
	 * @param resource
	 *           The requested resource which will be checked.
	 * 
	 * @param command
	 *           The command which will be checked.
	 * 
	 * @return true if allowed.
	 */
	boolean isResourceCommandAllowed(final RestResource resource, final String command);

	/**
	 * Method checks whether current session user has permission to work with current processed dto for the specified
	 * access operation.
	 * 
	 * @param objectClass
	 *           The dto class which will be checked.
	 * 
	 * @param operation
	 *           The read, change, create or remove access operations.
	 * 
	 * @return true if allowed.
	 */
	boolean isDtoOperationAllowed(final Class<?> objectClass, final String operation);

	/**
	 * Method checks whether current session user has permission to work with specified attribute for the specified
	 * access operation.
	 * 
	 * @param attrObjectClass
	 *           The dto class that contains the attribute which will be checked.
	 * 
	 * @param attrQualifier
	 *           The attribute which will be checked.
	 * 
	 * @param attrOperation
	 *           The read or change operations.
	 * 
	 * @return true if allowed.
	 */
	boolean isAttributeOperationAllowed(final Class<?> attrObjectClass, final String attrQualifier, final String attrOperation);
}
