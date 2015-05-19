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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * List of business partners and their partner functions. <br>
 * 
 */
public class PartnerListImpl implements PartnerList
{

	private static final long serialVersionUID = 1L;

	/**
	 * Map of partner list entries
	 */
	protected HashMap<String, PartnerListEntry> partnerList;

	private GenericFactory genericFactory;

	/**
	 * @param genericFactory
	 *           Factory to access SAP session beans
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * Creates a new <code>PartnerList</code> object.
	 */
	public PartnerListImpl()
	{
		partnerList = new HashMap<String, PartnerListEntry>();
	}

	/**
	 * Creates an empty <code>PartnerListEntryData</code> for the basket.
	 * 
	 * @returns PartnerListEntry which can added to the partnerList
	 */
	@Override
	public PartnerListEntry createPartnerListEntry()
	{
		return (PartnerListEntry) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY);
	}

	/**
	 * Creates a initialised <code>PartnerListEntryData</code> for the basket.
	 * 
	 * @param partnerGUID
	 *           techkey of the business partner
	 * @param partnerId
	 *           id of the business partner
	 * @returns PartnerListEntry which can added to the partnerList
	 */
	@Override
	public PartnerListEntry createPartnerListEntry(final TechKey partnerGUID, final String partnerId)
	{
		final PartnerListEntry listEntry = (PartnerListEntry) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY);
		listEntry.setPartnerTechKey(partnerGUID);
		listEntry.setPartnerId(partnerId);
		return listEntry;
	}

	/**
	 * Get the business partner for the given partner function(bpRole). <br>
	 * 
	 * @param bpRole
	 *           Name of the partner function
	 * @return PartnerListEntry of the business partner, with the given partner function (role) or null if not found
	 */
	public PartnerListEntryImpl getPartner(final String bpRole)
	{
		PartnerListEntryImpl entry = null;

		// try to find requeuested business partner
		entry = (PartnerListEntryImpl) partnerList.get(bpRole);

		return entry;
	}

	/**
	 * Get the business partner for the given partner function(role).
	 * 
	 * @param bpRole
	 *           Name of the partner function
	 * @return PartnerListEntryData of the business partner, with the given partner function (role) or null if not found
	 */
	@Override
	public PartnerListEntry getPartnerData(final String bpRole)
	{
		return getPartner(bpRole);
	}

	/**
	 * Convenience method to Get the soldto business partner, or null, if not available
	 * 
	 * @return PartnerListEntry of the soldTo business partner, or null if not found
	 */
	public PartnerListEntryImpl getSoldTo()
	{
		return getPartner(PartnerFunctionData.SOLDTO);
	}

	/**
	 * Convenience method to Get the soldto business partner, or null, if not available
	 * 
	 * @return PartnerListEntryData of the soldTo business partner, or null if not found
	 */
	@Override
	public PartnerListEntry getSoldToData()
	{
		return getSoldTo();
	}

	/**
	 * Convenience method to Get the reseller business partner, or null, if not available
	 * 
	 * @return PartnerListEntry of the reseller business partner, or null if not found
	 */
	public PartnerListEntryImpl getReseller()
	{
		return getPartner(PartnerFunctionData.RESELLER);
	}

	/**
	 * Convenience method to Get the reseller business partner, or null, if not available
	 * 
	 * @return PartnerListEntryData of the reseller business partner, or null if not found
	 */
	@Override
	public PartnerListEntry getResellerData()
	{
		return getReseller();
	}

	/**
	 * Convenience method to Get the contact business partner, or null, if not available
	 * 
	 * @return PartnerListEntry of the contact business partner, or null if not found
	 */
	public PartnerListEntryImpl getContact()
	{
		return getPartner(PartnerFunctionData.CONTACT);
	}

	/**
	 * Convenience method to Get the contact business partner, or null, if not available
	 * 
	 * @return PartnerListEntry of the contact business partner, or null if not found
	 */
	@Override
	public PartnerListEntry getContactData()
	{
		return getContact();
	}

	/**
	 * Convenience method to Get the soldfrom business partner, or null, if not available
	 * 
	 * @return PartnerListEntry of the soldfrom business partner, or null if not found
	 */
	public PartnerListEntryImpl getSoldFrom()
	{
		return getPartner(PartnerFunctionData.SOLDFROM);
	}

	/**
	 * Convenience method to Get the soldfrom business partner, or null, if not available
	 * 
	 * @return PartnerListEntry of the soldfrom business partner, or null if not found
	 */
	@Override
	public PartnerListEntry getSoldFromData()
	{
		return getSoldFrom();
	}

	/**
	 * Convenience method to Get the payer business partner, or null, if not available
	 * 
	 * @return PartnerListEntry of the payer business partner, or null if not found
	 */
	public PartnerListEntryImpl getPayer()
	{
		return getPartner(PartnerFunctionData.PAYER);
	}

	/**
	 * Convenience method to Get the payer business partner, or null, if not available
	 * 
	 * @return PartnerListEntryData of the payer business partner, or null if not found
	 */
	@Override
	public PartnerListEntry getPayerData()
	{
		return getPayer();
	}

	/**
	 * Set the business partner for the given partner function (role). <br>
	 * 
	 * @param bpRole
	 *           Name of the partner function
	 * @param entry
	 *           the PartnerListEntry for the business partner
	 */
	public void setPartner(final String bpRole, final PartnerListEntryImpl entry)
	{
		partnerList.put(bpRole, entry);
	}

	/**
	 * Set the business partner for the given function (role). <br>
	 * 
	 * @param bpRole
	 *           Name of the partner function
	 * @param entry
	 *           the PartnerListEntry for the business partner
	 */
	@Override
	public void setPartnerData(final String bpRole, final PartnerListEntry entry)
	{
		partnerList.put(bpRole, entry);
	}

	/**
	 * Convenience method to set the soldto business partner
	 * 
	 * @param entry
	 *           of the soldTo business partner
	 */
	public void setSoldTo(final PartnerListEntryImpl entry)
	{
		setPartner(PartnerFunctionData.SOLDTO, entry);
	}

	/**
	 * Convenience method to set the soldto business partner
	 * 
	 * @param entry
	 *           of the soldTo business partner
	 */
	@Override
	public void setSoldToData(final PartnerListEntry entry)
	{
		setSoldTo((PartnerListEntryImpl) entry);
	}

	/**
	 * Convenience method to set the contact business partner
	 * 
	 * @param entry
	 *           of the contact business partner
	 */
	public void setContact(final PartnerListEntryImpl entry)
	{
		setPartner(PartnerFunctionData.CONTACT, entry);
	}

	/**
	 * Convenience method to set the contact business partner
	 * 
	 * @param entry
	 *           of the contact business partner
	 */
	@Override
	public void setContactData(final PartnerListEntry entry)
	{
		setContact((PartnerListEntryImpl) entry);
	}

	/**
	 * Convenience method to set the sold-from business partner
	 * 
	 * @param entry
	 *           of the sold-from business partner
	 */
	public void setSoldFrom(final PartnerListEntryImpl entry)
	{
		setPartner(PartnerFunctionData.SOLDFROM, entry);
	}

	/**
	 * Convenience method to set the sold-from business partner
	 * 
	 * @param entry
	 *           of the sold-from business partner
	 */
	@Override
	public void setSoldFromData(final PartnerListEntry entry)
	{
		setSoldFrom((PartnerListEntryImpl) entry);
	}

	/**
	 * Convenience method to set the reseller business partner
	 * 
	 * @param entry
	 *           of the reseller business partner
	 */
	public void setReseller(final PartnerListEntryImpl entry)
	{
		setPartner(PartnerFunctionData.RESELLER, entry);
	}

	/**
	 * Convenience method to set the reseller business partner
	 * 
	 * @param entry
	 *           of the reseller business partner
	 */
	public void setResellerData(final PartnerListEntry entry)
	{
		setReseller((PartnerListEntryImpl) entry);
	}

	/**
	 * Convenience method to set the payer business partner
	 * 
	 * @param entry
	 *           of the payer business partner
	 */
	public void setPayer(final PartnerListEntryImpl entry)
	{
		setPartner(PartnerFunctionData.PAYER, entry);
	}

	/**
	 * Convenience method to set the payer business partner
	 * 
	 * @param entry
	 *           of the payer business partner
	 */
	@Override
	public void setPayerData(final PartnerListEntry entry)
	{
		setPayer((PartnerListEntryImpl) entry);
	}

	/**
	 * remove the business partner for the given partner function (role). <br>
	 * 
	 * @param bpRole
	 *           Name of the partner function
	 */
	public void removePartner(final String bpRole)
	{
		partnerList.remove(bpRole);
	}

	/**
	 * remove the business partner for the given partner function (role). <br>
	 * 
	 * @param bpRole
	 *           Name of the partner function
	 */
	@Override
	public void removePartnerData(final String bpRole)
	{
		removePartner(bpRole);
	}

	/**
	 * Remove the business partner with the given techKey. <br>
	 * 
	 * @param bpTechKey
	 *           TechKey of the businesspartner, to delete
	 */
	public void removePartner(final TechKey bpTechKey)
	{
		final Iterator<Entry<String, PartnerListEntry>> entries = partnerList.entrySet().iterator();
		while (entries.hasNext())
		{
			final PartnerListEntry entry = entries.next().getValue();
			if (bpTechKey.equals(entry.getPartnerTechKey()))
			{
				entries.remove();
			}
		}
	}

	/**
	 * remove the business partner with the given techKey. <br>
	 * 
	 * @param bpTechKey
	 *           TechKey of the businesspartner, to delete
	 */
	@Override
	public void removePartnerData(final TechKey bpTechKey)
	{
		removePartner(bpTechKey);
	}

	/**
	 * clear the business partner list
	 */
	@Override
	public void clearList()
	{
		partnerList.clear();
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return object as string
	 */
	@Override
	public String toString()
	{
		return partnerList.toString();
	}

	@Override
	public String getAllToString()
	{
		final StringBuffer buffer = new StringBuffer();

		for (final Entry<String, PartnerListEntry> entry : partnerList.entrySet())
		{
			buffer.append(entry.getKey());
			buffer.append(entry.getValue().getPartnerId());
		}

		return buffer.toString();
	}

	/**
	 * get the business partner list
	 * 
	 * @return MashMap list of the business partners
	 */
	@Override
	public Map<String, PartnerListEntry> getList()
	{
		return partnerList;
	}

	/**
	 * set the business partner list
	 * 
	 * @param partnerList
	 *           new list of the business partners
	 */
	public void setList(final Map<String, PartnerListEntry> partnerList)
	{
		this.partnerList.clear();
		this.partnerList.putAll(partnerList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PartnerList clone()
	{
		PartnerListImpl myClone;
		try
		{
			myClone = (PartnerListImpl) super.clone();
		}
		catch (final CloneNotSupportedException ex)
		{
			throw new ApplicationBaseRuntimeException("Clone not supported, although cloneable Interface implemented", ex);
		}
		myClone.partnerList = (HashMap<String, PartnerListEntry>) partnerList.clone();
		return myClone;

	}

	/**
	 * Returns en iterator over the Entrys of Partner List in form of Map.Entry The keys are the partner function and the
	 * values are <code>ParterListEntry</code> objects.
	 * 
	 * @return iterator over the partners and their parternfunctions
	 */
	@Override
	public Iterator<Entry<String, PartnerListEntry>> iterator()
	{

		final Set<Entry<String, PartnerListEntry>> partnerSet = partnerList.entrySet();
		return partnerSet.iterator();

	}

}
