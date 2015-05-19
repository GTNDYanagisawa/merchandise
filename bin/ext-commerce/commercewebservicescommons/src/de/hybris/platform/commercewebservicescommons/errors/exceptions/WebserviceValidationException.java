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
 */

package de.hybris.platform.commercewebservicescommons.errors.exceptions;


/**
 * Webservice validation exception used to throw validation errors. <br/>
 * Gets validation object as a parameter.
 */
public class WebserviceValidationException extends RuntimeException
{
	protected Object validationObject;

	public WebserviceValidationException(final Object validationObject)
	{
		super("Validation error");
		this.validationObject = validationObject;
	}

	public Object getValidationObject()
	{
		return validationObject;
	}
}
