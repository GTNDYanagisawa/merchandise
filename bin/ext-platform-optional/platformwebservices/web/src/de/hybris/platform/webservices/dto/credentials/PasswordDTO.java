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
package de.hybris.platform.webservices.dto.credentials;

import de.hybris.platform.webservices.resources.credentials.RetrievePasswordResource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Password Answer. Used in the {@link RetrievePasswordResource}
 */
@XmlRootElement(name = "changepassword")
public class PasswordDTO
{
	private String password = null;

	/**
	 * @param password
	 *           the password to set
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}

	/**
	 * @return the password
	 */
	@XmlElement(name = "newpassword")
	public String getPassword()
	{
		return password;
	}



}
