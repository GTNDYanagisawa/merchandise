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
package de.hybris.platform.sap.core.configuration.rfc.dao;

import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Data access object for SAP RFC Destination.
 */
public interface SAPRFCDestinationDao extends Dao
{
	/**
	 * Return a list of RFC destination models that are currently persisted. If none are found an empty list is returned.
	 * 
	 * @return all RFC destinations of system
	 */
	List<SAPRFCDestinationModel> findRfcDestinations();

	/**
	 * Find SAP RFC Destination by destination name.
	 * 
	 * @param rfcDestinationName
	 *           RFC destination name
	 * 
	 * @return All RFC destinations for the given destination name.
	 */
	public List<SAPRFCDestinationModel> findRfcDestinationByName(String rfcDestinationName);

}
