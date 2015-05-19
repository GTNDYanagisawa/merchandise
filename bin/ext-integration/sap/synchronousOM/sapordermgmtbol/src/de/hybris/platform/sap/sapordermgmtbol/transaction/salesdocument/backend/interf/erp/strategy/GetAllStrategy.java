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

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ItemBuffer;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.GetAllReadParameters;

import com.sap.conn.jco.JCoFunction;


/**
 * Strategy interface for reading a sales document from ERP with all aspects. Respective function module is
 * {@link ConstantsR3Lrd#FM_LO_API_WEC_ORDER_GET}
 * 
 */
public interface GetAllStrategy
{

	/**
	 * Execute the read call to fetch sales document details from the ERP backend. Is able to take a buffered state of
	 * items into account to optimize performance (i.e. those items won't be requested from the backend).
	 * 
	 * @param backendState
	 *           ERP specific state of the document
	 * @param salesDocument
	 *           BO representation of the sales document
	 * @param itemBuffer
	 *           State of previously read items (performance improvement)
	 * @param readParams
	 *           control read call (i.e. do we need to take IPC pricing into account)
	 * @param connection
	 *           connection to backend system
	 * @return result of backend call
	 * @throws BackendException
	 *            exception from R/3
	 */
	public BackendCallResult execute(BackendState backendState, SalesDocument salesDocument, ItemBuffer itemBuffer,
			GetAllReadParameters readParams, JCoConnection connection) throws BackendException;

	/**
	 * Performsm the actual JCO call
	 * 
	 * @param connection
	 *           connection to the backend system
	 * @param function
	 *           JCO function
	 * @throws BackendException
	 *            exception from R/3
	 */
	public void executeRfc(JCoConnection connection, JCoFunction function) throws BackendException;

}
