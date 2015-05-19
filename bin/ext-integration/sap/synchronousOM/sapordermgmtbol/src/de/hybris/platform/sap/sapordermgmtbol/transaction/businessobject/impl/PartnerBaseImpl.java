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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.PartnerBase;


/**
 * Partner Base: Address information of the business partner
 * 
 */
public class PartnerBaseImpl extends BusinessObjectBase implements PartnerBase
{

	private static final long serialVersionUID = 1L;
	private Address address;
	private String shortAddress;
	private String id;
	private boolean idX;

	//	/**
	//	 * Creates a new instance of the ship to, without providing any initialization data.
	//	 */
	//	public PartnerBaseImpl()
	//	{
	//		System.out.println();
	//	}


	@Override
	public void setTechKey(final TechKey techKey)
	{
		this.techKey = techKey;
	}

	/**
	 * Sets the fully qualified address for the ship to
	 * 
	 * @param address
	 *           The address to be set
	 */
	@Override
	public void setAddress(final Address address)
	{
		this.address = address;
	}

	/**
	 * Returns the address for the ship to
	 * 
	 * @return Address
	 */
	@Override
	public Address getAddress()
	{
		return address;
	}

	/**
	 * Returns the short address for the ship to
	 * 
	 * @return The short address
	 */
	@Override
	public String getShortAddress()
	{
		return shortAddress;
	}

	/**
	 * Sets the short address for the ship to. The short address is used in dialogs to select a ship to from a list of
	 * ship tos
	 * 
	 * @param shortAddress
	 *           The short address for the ship to
	 */
	@Override
	public void setShortAddress(final String shortAddress)
	{
		this.shortAddress = shortAddress;
	}

	/**
	 * Get a string representation of the object.
	 * 
	 * @return string representation
	 */
	@Override
	public String toString()
	{
		return "partner [techKey=\"" + techKey + "\", shortAddress=\"" + shortAddress + "\"]";

	}

	/**
	 * Sets the business partner id of the ship to
	 * 
	 * @param id
	 *           The id to be set
	 */
	@Override
	public void setId(final String id)
	{

		if (this.id == null)
		{
			this.id = id;
			return;
		}

		if (!this.id.equals(id))
		{
			this.id = id;
			idX = true;
		}
	}

	/**
	 * Returns the id of the the ship to
	 * 
	 * @return id
	 */
	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public PartnerBaseImpl clone()
	{
		PartnerBaseImpl partnerToClone = null;
		try
		{
			partnerToClone = (PartnerBaseImpl) super.clone();
		}
		catch (final CloneNotSupportedException ex)
		{
			// should not happen, because we are cloneable
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented", ex);
		}
		// primitives / immutable are copied by objects clone
		if (null != address)
		{
			final Address partnerToCloneAddress = address.clone();
			partnerToClone.setAddress(partnerToCloneAddress);
		}

		return partnerToClone;
	}

	@Override
	public boolean isIdX()
	{
		return idX;
	}

	@Override
	public void setIdX(final boolean idX)
	{
		this.idX = idX;
	}

}
