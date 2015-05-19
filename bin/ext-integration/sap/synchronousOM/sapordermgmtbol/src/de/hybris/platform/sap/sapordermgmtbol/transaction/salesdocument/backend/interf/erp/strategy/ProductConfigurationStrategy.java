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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;

import java.util.List;


/**
 * Deals with reading and writing product configuration data to ERP (via LO-API).
 */
public interface ProductConfigurationStrategy
{

	/**
	 * Writes a configuration to the backend
	 * 
	 * @param connection
	 *           JCO connection to the backend
	 * @param configModel
	 * @param handle
	 * @param bo
	 */
	void writeConfiguration(JCoConnection connection, ConfigModel configModel, String handle, BusinessObject bo);

	/**
	 * Reads configurations from backend for all configurable items specified
	 * 
	 * @param connection
	 *           JCO connection to the back end
	 * @param salesDoc
	 *           Cart or order
	 * @param configurableItems
	 *           List of configurable items, identified by their handles
	 */
	void readConfiguration(JCoConnection connection, SalesDocument salesDoc, List<String> configurableItems);


}
