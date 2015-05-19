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
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.sap.conn.jco.JCoTable;


@UnitTest
@SuppressWarnings("javadoc")
public class IncompletionMapperImplTest extends JCORecTestBase
{
	IncompletionMapperImpl classUnderTest = null;

	private static JCoTable incompLog;
	private static JCoTable messages;
	ItemList items = new ItemListImpl();

	private final Item item = new ItemSalesDoc();
	private final String posnr = "000010";
	private final String product = "WEC_DRAGON_CAR";


	@Before
	public void init()
	{
		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_INCOMPLETION_MAPPER);
		readTablesFromRepository();
		messages.clear();
		item.setProductId(product);
		item.setNumberInt(new BigDecimal(posnr).intValue());
		items.add(item);
	}

	@Test
	public void testInstance()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void testMapLogToMessage()
	{
		classUnderTest.mapLogToMessage(incompLog, messages, items);
		final int size = messages.getNumRows();
		assertTrue(size == 1);
	}

	@Test
	public void testMessageAttributes()
	{
		classUnderTest.mapLogToMessage(incompLog, messages, items);
		final int size = messages.getNumRows();
		assertTrue(size > 0);
		messages.firstRow();
		assertEquals("I", messages.getString("MSGTY"));
		assertEquals("INCVBAP", messages.getString("MSGID"));
		assertEquals("000", messages.getString("MSGNO"));
		assertEquals("CUOBJ", messages.getString("MSGV1"));
		assertEquals(product, messages.getString("MSGV2"));
	}

	@Test
	public void testConsider()
	{
		assertTrue(classUnderTest.considerLogEntry(IncompletionMapperImpl.FIELD_CFG));
		assertFalse(classUnderTest.considerLogEntry("XXX"));
		assertFalse(classUnderTest.considerLogEntry(null));
	}

	@Test
	public void testIsHeader()
	{
		assertTrue(classUnderTest.isHeaderLevel(IncompletionMapperImpl.FIELD_HEADER));
		assertFalse(classUnderTest.isHeaderLevel("000010"));
	}



	@Test
	public void testBackendUtil()
	{
		assertNotNull(classUnderTest.getBackendUtil());
	}

	private void readTablesFromRepository()
	{

		try
		{
			final JCoMockRepository testRepository = getJCORepository("jcoReposIncompletion");


			incompLog = testRepository.getTable("TT_INCOMPLETION");
			messages = testRepository.getTable("TT_MESSAGES");

		}
		catch (final JCoRecException e)
		{
			throw new ApplicationBaseRuntimeException("Problem with reading data from XML repository", e);
		}

	}
}
