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
package de.hybris.platform.webservices.util.objectgraphtransformer.usergraph;

import java.util.Collection;


public class TuUserModel extends TuItemModel
{
	private String uid = null;
	private String login = null;
	private String password = null;
	private TuAddressModel mainAddress = null;
	private TuAddressModel secondAddress = null;
	private Collection<TuAddressModel> addresses = null;

	public TuUserModel()
	{
		super();
	}

	public TuUserModel(final String uid)
	{
		super();
		this.uid = uid;
	}


	/**
	 * @return the secondAddress
	 */
	public TuAddressModel getSecondAddress()
	{
		return secondAddress;
	}

	/**
	 * @param secondAddress
	 *           the secondAddress to set
	 */
	public void setSecondAddress(final TuAddressModel secondAddress)
	{
		this.secondAddress = secondAddress;
	}


	/**
	 * @return the mainAddress
	 */
	public TuAddressModel getMainAddress()
	{
		return mainAddress;
	}

	/**
	 * @param mainAddress
	 *           the mainAddress to set
	 */
	public void setMainAddress(final TuAddressModel mainAddress)
	{
		this.mainAddress = mainAddress;
	}

	/**
	 * @return the addresses
	 */
	public Collection<TuAddressModel> getAddresses()
	{
		return addresses;
	}

	/**
	 * @param addresses
	 *           the addresses to set
	 */
	public void setAddresses(final Collection<TuAddressModel> addresses)
	{
		this.addresses = addresses;
	}

	/**
	 * @return the uid
	 */
	public String getUid()
	{
		return uid;
	}

	/**
	 * @param uid
	 *           the uid to set
	 */
	public void setUid(final String uid)
	{
		this.uid = uid;
	}

	/**
	 * @return the login
	 */
	public String getLogin()
	{
		return login;
	}

	/**
	 * @param login
	 *           the login to set
	 */
	public void setLogin(final String login)
	{
		this.login = login;
	}

	/**
	 * @return the password
	 */
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
