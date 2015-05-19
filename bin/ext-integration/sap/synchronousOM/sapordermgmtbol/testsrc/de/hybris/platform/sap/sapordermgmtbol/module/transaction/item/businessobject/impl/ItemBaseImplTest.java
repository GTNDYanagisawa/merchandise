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
package de.hybris.platform.sap.sapordermgmtbol.module.transaction.item.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ConnectedDocumentItemImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.SchedlineImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocumentItem;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemBaseImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListEntryImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.TextImpl;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class ItemBaseImplTest extends SapordermanagmentBolSpringJunitTest
{
	ItemBaseImpl classUnderTest = new ItemBaseImpl();

	@Before
	public void init()
	{
		classUnderTest.setGenericFactory(getGenericFactory());
	}

	@Test
	public void testAddPredecessor()
	{
		final ConnectedDocumentItem predecessor = new ConnectedDocumentItemImpl();
		classUnderTest.addPredecessor(predecessor);
		final List<ConnectedDocumentItem> predecessorList = classUnderTest.getPredecessorList();
		assertNotNull(predecessorList);
		assertTrue(predecessorList.size() == 1);
	}

	@Test
	public void testAddSuccessor()
	{
		final ConnectedDocumentItem successor = new ConnectedDocumentItemImpl();
		classUnderTest.addSuccessor(successor);
		final List<ConnectedDocumentItem> successorList = classUnderTest.getSuccessorList();
		assertNotNull(successorList);
		assertTrue(successorList.size() == 1);
	}

	@Test
	public void testClone()
	{
		final ItemBaseImpl clonedItem = (ItemBaseImpl) classUnderTest.clone();
		assertNotNull(clonedItem);
	}

	@Test
	public void testClonePartnerList()
	{
		final PartnerListImpl partnerList = new PartnerListImpl();
		partnerList.setSoldToData(new PartnerListEntryImpl());
		classUnderTest.setPartnerList(partnerList);
		final ItemBaseImpl clonedItem = (ItemBaseImpl) classUnderTest.clone();
		assertNotNull(clonedItem);
		//We expect a deep copy of the partner list
		final PartnerList clonedPartnerList = clonedItem.getPartnerListData();
		assertFalse(partnerList.equals(clonedPartnerList));
		assertTrue(clonedPartnerList.getList().size() == 1);
	}

	@Test
	public void testCloneSuccessorList()
	{
		testAddSuccessor();
		final List<ConnectedDocumentItem> successorList = classUnderTest.getSuccessorList();
		final ItemBaseImpl clonedItem = (ItemBaseImpl) classUnderTest.clone();
		assertNotNull(clonedItem);
		//We expect a deep copy of the list
		final List<ConnectedDocumentItem> clonedSuccList = clonedItem.getSuccessorList();
		assertNotSame(successorList, clonedSuccList);
		assertTrue(successorList.equals(clonedSuccList));
		assertTrue(clonedSuccList.size() == 1);
	}

	@Test
	public void testClonePredecessorList()
	{
		testAddPredecessor();
		final List<ConnectedDocumentItem> predList = classUnderTest.getPredecessorList();
		final ItemBaseImpl clonedItem = (ItemBaseImpl) classUnderTest.clone();
		assertNotNull(clonedItem);
		//We expect a deep copy of the list
		final List<ConnectedDocumentItem> clonedPredList = clonedItem.getPredecessorList();
		assertNotSame(predList, clonedPredList);
		assertTrue(predList.equals(clonedPredList));
		assertTrue(clonedPredList.size() == 1);
	}

	@Test
	public void testCloneText()
	{
		final Text itemText = new TextImpl();
		itemText.setText("Hello");
		classUnderTest.setText(itemText);
		final ItemBaseImpl clonedItem = (ItemBaseImpl) classUnderTest.clone();
		assertNotNull(clonedItem);

		//we expect a deep copy of the text
		final Text clonedText = clonedItem.getText();
		assertNotNull(clonedText);
	}

	@Test
	public void testCloneSchedLines()
	{
		classUnderTest.getScheduleLines().add(new SchedlineImpl());
		final ItemBaseImpl clonedItem = (ItemBaseImpl) classUnderTest.clone();
		assertNotNull(clonedItem);
		assertTrue(clonedItem.getScheduleLines().size() == 1);
	}

	@Test
	public void testConfirmedDelivDate()
	{
		final Date date = new Date(System.currentTimeMillis());
		classUnderTest.setConfirmedDeliveryDate(date);
		assertEquals(date, classUnderTest.getConfirmedDeliveryDate());
	}

	@Test
	public void testCreateConnectedDocumentItemData()
	{
		final ConnectedDocumentItem connectedDocumentItemData = classUnderTest.createConnectedDocumentItemData();
		assertNotNull(connectedDocumentItemData);
	}

	@Test
	public void testGetConfirmendQty()
	{
		final Schedline schedLine = new SchedlineImpl();
		schedLine.setCommittedQuantity(BigDecimal.TEN);
		classUnderTest.getScheduleLines().add(schedLine);

		//first call computes, second one just returns from attribute
		final BigDecimal confirmedQuantity = classUnderTest.getConfirmedQuantity();
		assertEquals(confirmedQuantity, BigDecimal.TEN);

		classUnderTest.setConfirmedQuantity(BigDecimal.ONE);
		assertEquals(classUnderTest.getConfirmedQuantity(), BigDecimal.ONE);
	}

	@Test
	public void testCreatedAt()
	{
		final Date createdAt = new Date(System.currentTimeMillis());
		classUnderTest.setCreatedAt(createdAt);
		assertEquals(createdAt, classUnderTest.getCreatedAt());
	}

	@Test
	public void testCurrency()
	{
		final String curr = "USD";
		classUnderTest.setCurrency(curr);
		assertEquals(curr, classUnderTest.getCurrency());
	}

	@Test
	public void testDeliveryQty()
	{
		final BigDecimal qty = BigDecimal.ONE;
		classUnderTest.setDeliverdQuantity(qty);
		assertEquals(qty, classUnderTest.getDeliveredQuantity());
	}

	@Test
	public void testDeliveryQtyUnit()
	{
		final String unit = "ST";
		classUnderTest.setDeliverdQuantityUnit(unit);
		assertEquals(unit, classUnderTest.getDeliveredQuantityUnit());
	}

	@Test
	public void testPrices()
	{
		final BigDecimal freight = BigDecimal.ONE;
		classUnderTest.setFreightValue(freight);
		assertEquals(freight, classUnderTest.getFreightValue());
		final BigDecimal netPrice = BigDecimal.TEN;
		classUnderTest.setNetPrice(netPrice);
		assertEquals(netPrice, classUnderTest.getNetPrice());
		final BigDecimal netValue = BigDecimal.TEN;
		classUnderTest.setNetValue(netValue);
		assertEquals(netValue, classUnderTest.getNetValue());

	}

	@Test
	public void testGrossWO()
	{
		final BigDecimal gross = BigDecimal.ONE;
		classUnderTest.setGrossValueWOFreight(gross);
		assertEquals(gross, classUnderTest.getGrossValueWOFreight());
	}

	@Test
	public void testItemCat()
	{
		final String itemCat = "TAC";
		classUnderTest.setItemCategory(itemCat);
		assertEquals(itemCat, classUnderTest.getItemCategory());
	}

	@Test
	public void testInit()
	{
		classUnderTest.init();
		assertNotNull(classUnderTest.getPartnerListData());
	}

	@Test
	public void testAttributes()
	{
		final BigDecimal numericValue = BigDecimal.TEN;
		classUnderTest.setOldQuantity(numericValue);
		assertEquals(numericValue, classUnderTest.getOldQuantity());
		classUnderTest.setTaxValue(numericValue);
		assertEquals(numericValue, classUnderTest.getTaxValue());
		classUnderTest.setQuantityToDeliver(numericValue);
		assertEquals(numericValue, classUnderTest.getQuantityToDeliver());
		classUnderTest.setTotalDiscount(numericValue);
		assertEquals(numericValue, classUnderTest.getTotalDiscount());

	}


}
