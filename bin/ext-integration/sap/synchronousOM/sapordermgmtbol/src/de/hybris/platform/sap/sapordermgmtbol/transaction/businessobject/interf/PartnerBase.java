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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import java.io.Serializable;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;


/**
 * Represents the PartnerBase object. <br>
 * 
 */
public interface PartnerBase extends BusinessObject, Cloneable, Serializable
{

	/**
	 * Sets the fully qualified address for the partner.<br>
	 * 
	 * @param address
	 *           The address to be set
	 */
	public void setAddress(Address address);

	/**
	 * Sets the short address for the partner.<br>
	 * 
	 * @param address
	 *           The short address for the partner
	 */
	public void setShortAddress(String address);

	/**
	 * Returns the short address of the partner.<br>
	 * 
	 * @return shortAddress The short address of the partner
	 */
	public String getShortAddress();

	/**
	 * Returns the address of the partner.<br>
	 * 
	 * @return Address of the partner
	 */
	public Address getAddress();

	/**
	 * Sets the id of the partner.<br>
	 * 
	 * @param id
	 *           The Id of the partner
	 */
	public void setId(String id);

	/**
	 * Returns the Id of the partner.<br>
	 * 
	 * @return Id The id of the partner
	 */
	public String getId();

	/**
	 * Returns value which indicates that address was set.<br>
	 * 
	 * @return true, if address was set
	 */
	public boolean isIdX();

	/**
	 * Sets flag which indicates that address was set.<br>
	 * 
	 * @param idX
	 *           Flag indicates whether address was set
	 */
	public void setIdX(boolean idX);

	/**
	 * @see Object#clone
	 * @return clone
	 */
	public PartnerBase clone();

}