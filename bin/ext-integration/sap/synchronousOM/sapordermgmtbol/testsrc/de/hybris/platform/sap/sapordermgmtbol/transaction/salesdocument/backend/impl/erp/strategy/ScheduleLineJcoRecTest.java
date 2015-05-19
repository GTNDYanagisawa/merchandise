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
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sap.conn.jco.JCoTable;


@SuppressWarnings("javadoc")
public class ScheduleLineJcoRecTest extends JCORecTestBase
{
	ItemMapper cut;

	private Item item;
	private Map<String, Item> itemMap;
	private static JCoTable ttObjInst;
	private static JCoTable slineComV;
	private static JCoTable slineComVEmpty;

	private String firstItemKey;



	@Test
	public void testScheduleLines()
	{
		final ObjectInstances objInstMap = new ObjectInstances(ttObjInst);
		cut.handleTtItemSlineComV(slineComV, objInstMap, itemMap);
		final Item itemWithScheduleLines = itemMap.get(firstItemKey);
		assertNotNull(itemWithScheduleLines);
		final List<Schedline> scheduleLines = itemWithScheduleLines.getScheduleLines();
		assertNotNull(scheduleLines);
		assertEquals("We expect 2 schedule lines", 2, scheduleLines.size());
		final Schedline schedline = scheduleLines.get(0);
		assertEquals(2, schedline.getCommittedQuantity().intValue());
	}

	@Test
	public void testScheduleLinesNoLines()
	{
		final ObjectInstances objInstMap = new ObjectInstances(ttObjInst);
		cut.handleTtItemSlineComV(slineComVEmpty, objInstMap, itemMap);
		final Item itemWithScheduleLines = itemMap.get(firstItemKey);
		assertNotNull(itemWithScheduleLines);
		final List<Schedline> scheduleLines = itemWithScheduleLines.getScheduleLines();
		assertNotNull(scheduleLines);
		assertEquals("We expect zero schedule lines", scheduleLines.size(), 0);
	}

	@Test
	public void testScheduleLineListInitial()
	{
		final Item newItem = itemMap.get(firstItemKey);
		assertNotNull(newItem);
		assertNotNull(newItem.getScheduleLines());
		assertEquals(0, newItem.getScheduleLines().size());
	}







	@Override
	public void setUp()
	{
		super.setUp();

		cut = (ItemMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER);

		if (slineComV == null)
		{
			readTablesFromRepository();
		}

		firstItemKey = "A";
		final String secondItemKey = "E";
		itemMap = new HashMap<String, Item>();

		addItem(firstItemKey, 10);
		addItem(secondItemKey, 20);

	}

	private void addItem(final String key, final int numberInt)
	{
		item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		item.setTechKey(new TechKey(key));
		item.setNumberInt(numberInt);
		itemMap.put(key, item);
	}

	private void readTablesFromRepository()
	{

		try
		{
			final JCoMockRepository testRepository = getJCORepository("jcoReposScheduleLine");


			slineComV = testRepository.getTable("TT_SLINE_COMV");
			slineComVEmpty = testRepository.getTable("TT_SLINE_COMVE");
			ttObjInst = testRepository.getTable("TT_OBJINST");

		}
		catch (final JCoRecException e)
		{
			throw new ApplicationBaseRuntimeException("Problem with reading data from XML repository", e);
		}

	}

}
