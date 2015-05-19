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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdCloseStrategy;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;


/**
 * Default implementation: Close LO-API session in back end
 * 
 */
public class LrdCloseStrategyImpl implements LrdCloseStrategy
{


	@Override
	public void close(final JCoConnection connection) throws BackendException
	{

		final JCoFunction function = connection.getFunction(ConstantsR3Lrd.FM_LO_API_CLOSE);
		connection.execute(function);
		final JCoParameterList exportParameterList = function.getExportParameterList();
		final String stillLoaded = exportParameterList.getString("EF_LOADED");
		final JCoStructure esError = exportParameterList.getStructure("ES_ERROR");

		if (!stillLoaded.isEmpty())
		{
			throw new BackendException("LO-API still loaded");
		}

		if (!esError.getString("ERRKZ").isEmpty())
		{
			throw new BackendException("Exception from close: " + esError.getString("MSGID") + "," + esError.getString("MSGNO"));
		}

	}

}
