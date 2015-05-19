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
package de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import de.hybris.platform.sap.core.common.TechKey;



/**
 * Represents the backend's view of a PartnerList.
 * 
 */
public interface PartnerList extends Iterable<Entry<String, PartnerListEntry>>, Cloneable, Serializable
{

	/**
	 * Creates an empty <code>PartnerListEntryData</code> for the basket.
	 * 
	 * @return PartnerListEntry which can added to the partnerList
	 */
	public PartnerListEntry createPartnerListEntry();

	/**
	 * Creates a initialized <code>PartnerListEntryData</code> for the basket.
	 * 
	 * @param partnerGUID
	 *           techkey of the business partner
	 * @param partnerId
	 *           id of the business partner
	 * @return PartnerListEntry which can added to the partnerList
	 */
	public PartnerListEntry createPartnerListEntry(TechKey partnerGUID, String partnerId);

	/**
	 * Get the business partner for the given role
	 * 
	 * @param bpRole
	 *           String the role of the business partner
	 * @return PartnerListEntryData of the business partner, with the given role or null if not found
	 */
	public PartnerListEntry getPartnerData(String bpRole);

	/**
	 * Convenience method to Get the soldto business partner, or null, if not available
	 * 
	 * @return PartnerListEntryData of the soldTo business partner, or null if not found
	 */
	public PartnerListEntry getSoldToData();

	/**
	 * Convenience method to Get the contact business partner, or null, if not available
	 * 
	 * @return PartnerListEntryData of the contact business partner, or null if not found
	 */
	public PartnerListEntry getContactData();

	/**
	 * Convenience method to Get the contact business partner, or null, if not available
	 * 
	 * @return PartnerListEntryData of the contac business partner, or null if not found
	 */
	public PartnerListEntry getSoldFromData();

	/**
	 * Convenience method to Get the reseller business partner, or null, if not available
	 * 
	 * @return PartnerListEntryData of the reseller business partner, or null if not found
	 */
	public PartnerListEntry getResellerData();

	/**
	 * Convenience method to Get the payer business partner, or null, if not available
	 * 
	 * @return PartnerListEntryData of the payer business partner, or null if not found
	 */
	public PartnerListEntry getPayerData();

	/**
	 * Set the business partner for the given role
	 * 
	 * @param bpRole
	 *           String the role of the business partner
	 * @param entry
	 *           the PartnerListEntry for the business partner
	 */
	public void setPartnerData(String bpRole, PartnerListEntry entry);

	/**
	 * Convenience method to set the soldto business partner
	 * 
	 * @param entry
	 *           of the soldTo business partner
	 */
	public void setSoldToData(PartnerListEntry entry);

	/**
	 * Convenience method to set the contact business partner
	 * 
	 * @param entry
	 *           of the contact business partner
	 */
	public void setContactData(PartnerListEntry entry);

	/**
	 * Convenience method to set the soldfrom business partner
	 * 
	 * @param entry
	 *           of the soldfrom business partner
	 */
	public void setSoldFromData(PartnerListEntry entry);

	/**
	 * Convenience method to set the payer business partner
	 * 
	 * @param entry
	 *           of the payer business partner
	 */
	public void setPayerData(PartnerListEntry entry);

	/**
	 * remove the business partner for the given role
	 * 
	 * @param bpRole
	 *           String the role of the business partner
	 */
	public void removePartnerData(String bpRole);

	/**
	 * remove the business partner with the given GUID
	 * 
	 * @param bpTechKey
	 *           TechKey of the Partner, to dlete
	 */
	public void removePartnerData(TechKey bpTechKey);

	/**
	 * clear the business partner list
	 */
	public void clearList();

	/**
	 * @return clone
	 * @see Object#clone
	 */
	public PartnerList clone();

	/**
	 * get the business partner list
	 * 
	 * @return MashMap list of the business partners
	 */
	public Map<String, PartnerListEntry> getList();

	/**
	 * Get a string representation of this partnerList which is unique for these partners
	 * 
	 * @return string
	 */
	public String getAllToString();

}