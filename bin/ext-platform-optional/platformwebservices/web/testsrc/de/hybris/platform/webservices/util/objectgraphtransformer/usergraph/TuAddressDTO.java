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

import de.hybris.platform.webservices.util.objectgraphtransformer.GraphNode;

import java.util.List;


@GraphNode(target = TuAddressModel.class)
public class TuAddressDTO
{
	private String firstname;
	private String lastname;

	private TuUserDTO owner = null;
	private TuCountryDTO country = null;

	private List<TuAddressDTO> moreAddresses = null;

	/**
	 * @return the moreAddresses
	 */
	public List<TuAddressDTO> getMoreAddresses()
	{
		return moreAddresses;
	}

	/**
	 * @param moreAddresses
	 *           the moreAddresses to set
	 */
	public void setMoreAddresses(final List<TuAddressDTO> moreAddresses)
	{
		this.moreAddresses = moreAddresses;
	}

	public TuAddressDTO()
	{
		//
	}

	public TuAddressDTO(final String firstName)
	{
		this.firstname = firstName;
	}

	public TuAddressDTO(final String firstName, final String lastName)
	{
		this.firstname = firstName;
		this.lastname = lastName;
	}

	public TuAddressDTO(final String firstName, final String lastName, final TuUserDTO owner)
	{
		this.firstname = firstName;
		this.lastname = lastName;
		this.owner = owner;
	}



	/**
	 * @return the media
	 */
	public TuCountryDTO getCountry()
	{
		return country;
	}

	/**
	 * @param media
	 *           the media to set
	 */
	public void setCountry(final TuCountryDTO media)
	{
		this.country = media;
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

	public TuUserDTO getOwner()
	{
		return owner;
	}

	/**
	 * @param owner
	 *           the owner to set
	 */
	public void setOwner(final TuUserDTO owner)
	{
		this.owner = owner;
	}

}
