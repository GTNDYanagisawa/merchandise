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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ProcessingStatusImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.OrderImpl;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class OrderERPTest extends SapordermanagmentBolSpringJunitTest
{

	private OrderERP classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.BEAN_ID_BE_ORDER_ERP);

	}


	@Test
	public void testBeanInstantiation()
	{
		assertNotNull(classUnderTest.getAdditionalPricing());
	}


	@Test
	public void testIsCancelable_open()
	{
		final Order order = createOrderBo();

		final ItemList itemList = new ItemListImpl();
		itemList.add(createItem("A", EStatus.OPEN));
		order.setItemList(itemList);

		Assert.assertTrue("Order should be cancelable", classUnderTest.isCancelable(order));
	}


	private Order createOrderBo()
	{
		final Order order = new OrderImpl();
		order.setHeader(new HeaderSalesDocument());
		return order;
	}

	@Test
	public void testIsCancelable_itemProcessed()
	{
		final Order order = createOrderBo();

		final ItemList itemList = new ItemListImpl();
		itemList.add(createItem("A", EStatus.OPEN));
		itemList.add(createItem("B", EStatus.PROCESSED));
		order.setItemList(itemList);

		Assert.assertFalse("Order can not be canceled", classUnderTest.isCancelable(order));
	}

	@Test
	public void testIsCancelable_itemPartialyProcessed()
	{
		final Order order = createOrderBo();

		final ItemList itemList = new ItemListImpl();
		itemList.add(createItem("A", EStatus.OPEN));
		itemList.add(createItem("B", EStatus.PARTIALLY_PROCESSED));
		order.setItemList(itemList);

		Assert.assertTrue("Order should be cancelable", classUnderTest.isCancelable(order));
	}

	private Item createItem(final String techKey, final EStatus status)
	{
		final TechKey parentKey = new TechKey(techKey);
		final Item itemA = new ItemSalesDoc();
		itemA.setTechKey(parentKey);
		itemA.setParentId(TechKey.EMPTY_KEY);
		itemA.setQuantity(BigDecimal.ONE);
		final ProcessingStatus procStatus = new ProcessingStatusImpl(status);
		itemA.setProcessingStatus(procStatus);
		return itemA;
	}
}
