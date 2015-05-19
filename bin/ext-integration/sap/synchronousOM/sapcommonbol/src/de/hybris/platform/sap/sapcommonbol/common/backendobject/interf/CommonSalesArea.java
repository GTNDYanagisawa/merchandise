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
package de.hybris.platform.sap.sapcommonbol.common.backendobject.interf;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMapping;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMappingKey;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMapping;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMappingKey;

import java.util.Map;


/**
 * Interface to get the backend information for the common sales area. Gets common distribution channel and division
 * from back end. <br>
 * 
 */
public interface CommonSalesArea
{

	/**
	 * Back end object for the backendobject-config
	 */
	public String BE_TYPE = "CommonSalesArea";

	/**
	 * Loads common distribution channel data
	 * 
	 * @return content of the table TVKOS
	 * @throws BackendException
	 */
	public Map<DistChannelMappingKey, DistChannelMapping> loadDistChannelMappingFromBackend() throws BackendException;

	/**
	 * Loads common division data
	 * 
	 * @return content of the table TVKOV
	 * @throws BackendException
	 */
	public Map<DivisionMappingKey, DivisionMapping> loadDivisionMappingFromBackend() throws BackendException;

	/**
	 * Get shop configuration key from BE.<br>
	 * 
	 * @return configuration key
	 * @throws BackendException
	 */
	public String getConfigurationKey() throws BackendException;
}
