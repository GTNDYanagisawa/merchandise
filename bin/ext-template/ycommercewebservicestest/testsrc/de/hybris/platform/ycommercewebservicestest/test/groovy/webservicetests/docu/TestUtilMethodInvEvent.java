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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.docu;



/**
 * Event representing TestUtil method call
 */
public class TestUtilMethodInvEvent
{
	public enum MethodInvEventType
	{
		GET_CONNECTION, GET_RESPONSE
	}

	private final MethodInvEventType type;
	private String resource;
	private String accept;
	private String method;
	private String response;

	/**
	 * @return the type
	 */
	public MethodInvEventType getType()
	{
		return type;
	}

	/**
	 * @return the resource
	 */
	public String getResource()
	{
		return resource;
	}

	/**
	 * @return the response
	 */
	public String getResponse()
	{
		return response;
	}

	/**
	 * @return the accept
	 */
	public String getAccept()
	{
		return accept;
	}

	/**
	 * @return the method
	 */
	public String getMethod()
	{
		return method;
	}

	public TestUtilMethodInvEvent(final String resource, final String accept, final String method)
	{
		type = MethodInvEventType.GET_CONNECTION;
		this.resource = resource;
		this.accept = accept;
		this.method = method;
	}

	public TestUtilMethodInvEvent(final String response)
	{
		type = MethodInvEventType.GET_RESPONSE;
		this.response = response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "TestUtilMethodInvEvent [type=" + type + ", resource=" + resource + ", accept=" + accept + ", method=" + method
				+ ", response=" + response + "]";
	}


}
