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
package de.hybris.platform.ycommercewebservices.exceptions;

import javax.servlet.ServletException;


/**
 * @author KKW
 * 
 */
public class InvalidResourceException extends ServletException
{

	private final String baseSiteId;

	/**
	 * @param baseSiteUid
	 */
	public InvalidResourceException(final String baseSiteUid)
	{
		super("Base site " + baseSiteUid + " doesn't exist");
		this.baseSiteId = baseSiteUid;
	}

	/**
	 * @return the baseSiteId
	 */
	public String getBaseSiteId()
	{
		return baseSiteId;
	}
}
