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
package de.hybris.platform.sap.core.extensibility.request;


/**
 * Thread local container used for storing the Request Context.
 */
public class RequestContextContainer extends ThreadLocal<RequestContext>
{

	private static RequestContextContainer instance = new RequestContextContainer();

	/**
	 * Gets the RequestContextContainer instance.
	 * 
	 * @return RequestContextContainer
	 */
	public static RequestContextContainer getInstance()
	{
		return instance;
	}

	@Override
	protected RequestContext initialValue()
	{
		return new RequestContext();
	}

	/**
	 * Returns a request context. Scope is the current thread
	 * 
	 * @return a request context
	 */
	public RequestContext getRequestContext()
	{
		return get();
	}
}
