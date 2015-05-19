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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

public class ErrorMessage
{
	private String path;
	private String code;
	private String message;
	private Object[] args;

	public ErrorMessage()
	{
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(final String path)
	{
		this.path = path;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(final String message)
	{
		this.message = message;
	}

	public Object[] getArgs()
	{
		return this.args;
	}

	public void setArgs(final Object[] args)
	{
		this.args = args;
	}
}
