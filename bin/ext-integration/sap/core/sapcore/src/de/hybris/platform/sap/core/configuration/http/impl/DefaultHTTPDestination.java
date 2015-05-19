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
package de.hybris.platform.sap.core.configuration.http.impl;

import de.hybris.platform.sap.core.configuration.http.HTTPDestination;


/**
 * 
 */
public class DefaultHTTPDestination implements HTTPDestination
{

	/**
	 * The name of the destination.
	 */
	private String httpDestinationName;

	/**
	 * The target URL.
	 */
	private String targetURL;

	/**
	 * The authentication type.
	 */
	private String authenticationType;

	/**
	 * The user ID.
	 */
	private String userid;

	/**
	 * The password.
	 */
	private String password;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.configuration.jco.impl.testIF#getRfcDestinationName()
	 */
	@Override
	public String getHttpDestinationName()
	{
		return httpDestinationName;
	}

	/**
	 * @param httpDestinationName
	 *           the rfcDestinationName to set
	 */
	public void setHttpDestinationName(final String httpDestinationName)
	{
		this.httpDestinationName = httpDestinationName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.configuration.jco.impl.testIF#getTargetHost()
	 */
	@Override
	public String getTargetURL()
	{
		return targetURL;
	}

	/**
	 * @param targetURL
	 *           the targetHost to set
	 */
	public void setTargetURL(final String targetURL)
	{
		this.targetURL = targetURL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.configuration.jco.impl.testIF#getAuthentication()
	 */
	@Override
	public String getAuthenticationType()
	{
		return authenticationType;
	}

	/**
	 * @param authenticationType
	 *           the authentication to set
	 */
	public void setAuthenticationType(final String authenticationType)
	{
		this.authenticationType = authenticationType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.configuration.jco.impl.testIF#getUserid()
	 */
	@Override
	public String getUserid()
	{
		return userid;
	}

	/**
	 * @param userid
	 *           the userid to set
	 */
	public void setUserid(final String userid)
	{
		this.userid = userid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.configuration.jco.impl.testIF#getPassword()
	 */
	@Override
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 *           the password to set
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}

}
