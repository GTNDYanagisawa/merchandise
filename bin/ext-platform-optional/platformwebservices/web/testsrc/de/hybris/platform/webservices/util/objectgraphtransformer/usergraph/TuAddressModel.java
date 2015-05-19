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

import java.util.List;



public class TuAddressModel extends TuItemModel
{
	private String firstname;
	private String lastname;
	private TuCountryModel country = null;
	private List<TuAddressModel> moreAddresses = null;

	public TuAddressModel()
	{
		this(null, null);
	}

	public TuAddressModel(final String firstname)
	{
		this(firstname, null);
	}

	public TuAddressModel(final String firstname, final String lastname)
	{
		super();
		this.firstname = firstname;
		this.lastname = lastname;
	}


	// superclass defines 'owner'of type 'ItemModel'
	// testcases must assure that always a subinstance of itemmodel is injected

	/**
	 * @return the moreAddresses
	 */
	public List<TuAddressModel> getMoreAddresses()
	{
		return moreAddresses;
	}

	/**
	 * @param moreAddresses
	 *           the moreAddresses to set
	 */
	public void setMoreAddresses(final List<TuAddressModel> moreAddresses)
	{
		this.moreAddresses = moreAddresses;
	}

	/**
	 * @return the country
	 */
	public TuCountryModel getCountry()
	{
		return country;
	}

	/**
	 * @param country
	 *           the country to set
	 */
	public void setCountry(final TuCountryModel country)
	{
		this.country = country;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname()
	{
		return firstname;
	}

	/**
	 * @param firstname
	 *           the firstname to set
	 */
	public void setFirstname(final String firstname)
	{
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname()
	{
		return lastname;
	}

	/**
	 * @param lastname
	 *           the lastname to set
	 */
	public void setLastname(final String lastname)
	{
		this.lastname = lastname;
	}

}
