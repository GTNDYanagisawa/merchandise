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
package de.hybris.platform.yb2bacceleratorstorefront.controllers.util;

import java.util.Collection;

public class GlobalMessage
{
	private String code;
	private Collection<Object> attributes;

	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public Collection<Object> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(final Collection<Object> attributes)
	{
		this.attributes = attributes;
	}
}
