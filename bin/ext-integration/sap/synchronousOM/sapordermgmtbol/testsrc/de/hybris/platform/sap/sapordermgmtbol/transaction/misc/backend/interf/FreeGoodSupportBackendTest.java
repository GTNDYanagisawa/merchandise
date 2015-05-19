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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.interf;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.SchedlineImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase.ItemUsage;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl.TransactionConfigurationImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;



@SuppressWarnings("javadoc")
public class FreeGoodSupportBackendTest extends TestCase
{


	private SalesDocument salesDoc;

	@Override
	public void setUp()
	{

		salesDoc = new BasketImpl();
		final TransactionConfigurationImpl transConf = new TransactionConfigurationImpl();
		salesDoc.setTransactionConfiguration(transConf);
	}

	public void testAdjustSalesDocument_emptyDoc() throws BackendException
	{
		final boolean freeGoodFound = FreeGoodSupportBackend.adjustSalesDocument(salesDoc);
		assertFalse(freeGoodFound);
		assertEquals(salesDoc.getItemList().size(), 0);
	}

	public void testAdjustSalesDocument_InclsuiveWithItemGen() throws BackendException
	{
		// prepare
		final TechKey parentKey = new TechKey("parentKey");
		final Item mainItem = new ItemSalesDoc();
		mainItem.setTechKey(parentKey);
		final Item subItem = new ItemSalesDoc();
		final Item anotherItem = new ItemSalesDoc();
		salesDoc.addItem(mainItem);
		salesDoc.addItem(subItem);
		salesDoc.addItem(anotherItem);

		mainItem.setQuantity(new BigDecimal(9));

		subItem.setQuantity(BigDecimal.ONE);
		subItem.setParentId(parentKey);
		subItem.setItemUsage(ItemUsage.FREE_GOOD_INCL);

		// execute test
		final boolean freeGoodFound = FreeGoodSupportBackend.adjustSalesDocument(salesDoc);
		assertTrue(freeGoodFound);
		assertEquals(mainItem, salesDoc.getItemList().get(0));
		assertEquals(subItem, salesDoc.getItemList().get(1));

		assertEquals(anotherItem, salesDoc.getItemList().get(2));
		//        BigDecimal freeQuantity = salesDoc.getItemList().get(0).getFreeQuantity();
		//        assertEquals("FreeQuantity: Actual!=Expected (" + freeQuantity.toPlainString() + "!=1)", 0,
		//                BigDecimal.ONE.compareTo(freeQuantity));

		final BigDecimal quantity = salesDoc.getItemList().get(0).getQuantity();
		assertEquals("Quantity: Actual!=Expected (" + quantity.toPlainString() + "!=10)", 0, new BigDecimal(9).compareTo(quantity));

	}

	public void testAddScheduleLines()
	{
		final Date date = new Date();

		final Item child = createItemWithSchedline(date, new BigDecimal(1));
		final Item parent = createItemWithSchedline(date, new BigDecimal(9));

		FreeGoodSupportBackend.addScheduleLines(child, parent);

		final BigDecimal resultedQuantity = parent.getScheduleLines().get(0).getCommittedQuantity();
		assertEquals("Schedlines must have been sumed up", new BigDecimal(10), resultedQuantity);

	}

	private Item createItemWithSchedline(final Date date, final BigDecimal quantity)
	{
		final Item child = new ItemSalesDoc();
		final List<Schedline> childScheduleLines = new ArrayList<Schedline>(1);
		final SchedlineImpl schedline = createSchedline(date, quantity);
		childScheduleLines.add(schedline);
		child.setScheduleLines(childScheduleLines);
		return child;
	}

	private SchedlineImpl createSchedline(final Date date, final BigDecimal quantity)
	{
		final SchedlineImpl schedline = new SchedlineImpl();
		schedline.setCommittedDate(date);
		schedline.setCommittedQuantity(quantity);
		return schedline;
	}
}
