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

import de.hybris.platform.sap.core.common.TechKey;


/**
 * Represents the backend's view of a PartnerListEntry.
 * 
 */
public interface PartnerListEntry extends Serializable
{

	/**
	 * Get the TechKey of the PartnerListEntry
	 * 
	 * @return TechKey of entry
	 */
	public TechKey getPartnerTechKey();

	/**
	 * Set the TechKey of the PartnerListEntry
	 * 
	 * @param partnerGUID
	 *           techkey of the business partner
	 */
	public void setPartnerTechKey(TechKey partnerGUID);

	/**
	 * get the Id of the PartnerListEntry
	 * 
	 * @return id of entry
	 */
	public String getPartnerId();

	/**
	 * Set the TechKey of the PartnerListEntry
	 * 
	 * @param partnerId
	 *           theckey of the business partner
	 */
	public void setPartnerId(String partnerId);

	/**
	 * This method returns the handle, as an alternative key for the business object, because at the creation point no
	 * techkey for the object exists. Therefore maybay the handle is needed to identify the object in backend return the
	 * handle of business object which is needed to identify the object in the backend, if the techkey still not exists
	 * 
	 * @return string representing this entry
	 */
	public String getHandle();

	/**
	 * This method sets the handle, as an alternative key for the business object, because at the creation point no
	 * techkey for the object exists. Therefore maybay the handle is needed to identify the object in backend
	 * 
	 * @param handle
	 *           the handle of business object which identifies the object in the backend, if the techkey still not
	 *           exists
	 */
	public void setHandle(String handle);
}