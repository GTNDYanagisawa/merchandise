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

import org.easymock.EasyMock;
import org.junit.Test;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;




@SuppressWarnings("javadoc")
public class LrdCloseStrategyImplTest
{
	LrdCloseStrategyImpl classUnderTest = new LrdCloseStrategyImpl();
	private JCoConnection connection;
	private JCoFunction function;



	@Test
	public void testClose() throws BackendException
	{
		mockJCOCal("");
		classUnderTest.close(connection);
	}

	@Test(expected = BackendException.class)
	public void testCloseWhenException() throws BackendException
	{
		mockJCOCal("X");
		classUnderTest.close(connection);
	}


	/**
	 * @throws BackendException
	 */
	void mockJCOCal(final String stillLoaded) throws BackendException
	{
		connection = EasyMock.createMock(JCoConnection.class);
		function = EasyMock.createMock(JCoFunction.class);
		final JCoStructure structureError = EasyMock.createMock(JCoStructure.class);

		final JCoParameterList exportParameterList = EasyMock.createMock(JCoParameterList.class);
		EasyMock.expect(function.getExportParameterList()).andReturn(exportParameterList);
		connection.execute(function);
		EasyMock.expect(exportParameterList.getString("EF_LOADED")).andReturn(stillLoaded);
		EasyMock.expect(exportParameterList.getStructure("ES_ERROR")).andReturn(structureError);
		EasyMock.expect(structureError.getString("ERRKZ")).andReturn("");
		EasyMock.expect(connection.getFunction(ConstantsR3Lrd.FM_LO_API_CLOSE)).andReturn(function);
		EasyMock.replay(connection, function, exportParameterList, structureError);
	}

}
