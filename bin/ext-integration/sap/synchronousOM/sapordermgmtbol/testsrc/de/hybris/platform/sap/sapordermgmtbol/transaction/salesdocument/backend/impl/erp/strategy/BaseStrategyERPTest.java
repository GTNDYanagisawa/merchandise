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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.BackendExceptionECOERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageMapper;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import org.junit.Before;
import org.junit.Test;

import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


@UnitTest
@SuppressWarnings("javadoc")
public class BaseStrategyERPTest extends JCORecTestBase
{
	private final BaseStrategyERP classUnderTest = new BaseStrategyERP();
	final String attributeName = "attribute";
	JCoTable tableForLogging = null;
	JCoTable tableForPreprocessing = null;
	JCoStructure recordForLogging = null;
	private JCoStructure esError = null;
	private final String functionName = "Function";
	private final Log4JWrapper log = Log4JWrapper.getInstance(GetAllStrategyERP.class.getName());
	private final BusinessObject bob = new BasketImpl();

	private void readTablesFromRepository()
	{

		try
		{
			final JCoMockRepository testRepository = getJCORepository("jcoReposBase");


			tableForLogging = testRepository.getTable("TT_MESSAGES");
			tableForPreprocessing = testRepository.getTable("TT_MESSAGES_PREPROCESS");
			recordForLogging = testRepository.getStructure("S_MESSAGE");
			esError = testRepository.getStructure("S_ERROR");

		}
		catch (final JCoRecException e)
		{
			throw new ApplicationBaseRuntimeException("Problem with reading data from XML repository", e);
		}

	}

	@Before
	public void init()
	{
		readTablesFromRepository();
		classUnderTest.setMessageMapper((BackendMessageMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BACKEND_MESSAGE_MAPPER));
		classUnderTest.setCustExit((ERPLO_APICustomerExits) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BE_CUST_EXIT));
		classUnderTest.setGenericFactory(genericFactory);
	}

	@Test(expected = BackendExceptionECOERP.class)
	public void testCheckAttributeEmptyNull() throws BackendExceptionECOERP
	{
		BaseStrategyERP.checkAttributeEmpty(null, attributeName);
	}

	@Test(expected = BackendExceptionECOERP.class)
	public void testCheckAttributeEmptyStringEmpty() throws BackendExceptionECOERP
	{
		BaseStrategyERP.checkAttributeEmpty("", attributeName);
	}

	@Test(expected = BackendExceptionECOERP.class)
	public void testCheckAttributeTechKeyEmpty() throws BackendExceptionECOERP
	{

		BaseStrategyERP.checkAttributeEmpty(TechKey.EMPTY_KEY, attributeName);
	}

	@Test
	public void testCreateBufferForLogging()
	{
		final String direction = "A";
		final StringBuffer buffer = new StringBuffer();
		BaseStrategyERP.createBufferForLogging(functionName, tableForLogging, direction, buffer);
		assertTrue(buffer.indexOf(functionName) > 0);
		assertTrue(buffer.indexOf("T_PROBCLSS") > 0);

		BaseStrategyERP.creatRecordLogEntry(direction, tableForLogging, direction, log);
	}

	@Test
	public void testDispatchMessages() throws BackendException
	{
		classUnderTest.dispatchMessages(bob, tableForLogging, recordForLogging);
		assertEquals(1, bob.getMessageList().size());
	}

	@Test
	public void testDispatchMessagesItemList() throws BackendException
	{
		classUnderTest.dispatchMessages(bob, tableForLogging, recordForLogging);
		assertEquals(1, bob.getMessageList().size());
	}

	@Test
	public void testGetCustExit()
	{
		assertNotNull(classUnderTest.getCustExit());
	}

	@Test
	public void testGetGenFac()
	{
		assertNotNull(classUnderTest.getGenericFactory());
	}

	@Test
	public void testIsRecoverableHeaderErrorInitial()
	{
		assertFalse(classUnderTest.isRecoverableHeaderError(esError));
	}

	@Test
	public void testIsRecoverableHeaderError()
	{
		esError.setValue("MSGID", "SLS_LORD");
		esError.setValue("MSGNO", "018");
		esError.setValue("MSGV1", "VDATU");

		assertTrue(classUnderTest.isRecoverableHeaderError(esError));
	}

	@Test
	public void testHasMessage()
	{
		final String msgId = "LRD";
		final String msgNo = "123";
		assertTrue(BaseStrategyERP.hasMessage("E", msgId, msgNo, "", "", "", "", tableForLogging, esError));
	}






}
