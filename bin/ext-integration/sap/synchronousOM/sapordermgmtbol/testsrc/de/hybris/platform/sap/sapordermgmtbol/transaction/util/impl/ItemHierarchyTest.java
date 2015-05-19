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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class ItemHierarchyTest extends TestCase
{

	private ItemHierarchy classUnderTest;
	private final ItemList itemList = new ItemListImpl();

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	private void init()
	{
		classUnderTest = new ItemHierarchy(itemList);
	}

	public void testGetNode()
	{
		addItemToList("A");
		addItemToList("B");
		init();

		assertNotNull(classUnderTest.getNode(new TechKey("A")));
		assertNull(classUnderTest.getNode(new TechKey("C")));
	}

	private void addItemToList(final String techKey)
	{
		final Item item = new ItemSalesDoc();
		item.setTechKey(new TechKey(techKey));
		itemList.add(item);
	}
}
