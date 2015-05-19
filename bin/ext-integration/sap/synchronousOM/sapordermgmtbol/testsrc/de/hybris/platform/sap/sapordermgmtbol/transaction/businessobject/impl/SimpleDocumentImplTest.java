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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.LogonException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SimpleDocumentImplTest extends SapordermanagmentBolSpringJunitTest
{

	private static final String TEST_APPLICATION_ID = "Test Application ID";

	@Resource(name = SapordermgmtbolConstants.ALIAS_BO_CART)
	private SimpleDocumentImpl<ItemList, Item, Header> classUnderTest;

	@Resource(name = "sapCoreGenericFactory")
	private GenericFactory genericFactory;

	@Override
	@Before
	public void setUp()
	{
		//
	}



	private Item createNewItem()
	{
		final Item newItem = new ItemSalesDoc();
		newItem.setProductId("New Product");
		final TechKey newProductKey = new TechKey("New Product TechKey");
		newItem.setProductGuid(newProductKey);
		return newItem;
	}





	private Item createChangedItem()
	{
		final Item changedItem2 = new ItemSalesDoc();
		changedItem2.setProductGuid(new TechKey("Changed Product TechKey"));
		changedItem2.setProductId("New Product");
		return changedItem2;
	}






	private Item createItemInCatalogWithImg()
	{
		final Item oldItemWithImage = new ItemSalesDoc();
		oldItemWithImage.setProductId("Old Product");
		oldItemWithImage.setProductGuid(new TechKey("Old Product TechKey"));

		return oldItemWithImage;
	}

	@Test
	public void testGetApplicationId()
	{
		classUnderTest.setApplicationId(TEST_APPLICATION_ID);
		assertEquals("Application ID not found correctly", TEST_APPLICATION_ID, classUnderTest.getApplicationId());
	}

	@Test
	public void test_getTypedExtensionMap()
	{
		final Map<String, Object> typedExtensionMap = classUnderTest.getTypedExtensionMap();
		assertNotNull(typedExtensionMap);
		typedExtensionMap.put("test_key", "test_value");
		assertEquals("test_value", typedExtensionMap.get("test_key"));
	}

	@Test
	public void testImageAndDescription() throws LogonException, BusinessObjectException, BackendException
	{

		// prepare TestData
		final String productId1 = "product 1";
		final String productId2 = "product 2";
		final String productId3 = "product 3";
		final String itemHandle1 = "item 1";
		final String itemHandle2 = "item 2";
		final String itemHandle3 = "item 3";
		final TechKey productKey1 = new TechKey("key1");
		final TechKey productKey2 = new TechKey("key2");
		final TechKey productKey3 = new TechKey("key3");

		final Item item1 = createItemInCatalogWithImg();
		item1.setHandle(itemHandle1);
		item1.setProductId(productId1);
		item1.setProductGuid(productKey1);

		final Item item2 = createChangedItem();
		item2.setHandle(itemHandle2);
		item2.setProductId(productId2);
		item2.setProductGuid(productKey2);

		final Item item3 = createNewItem();
		item3.setHandle(itemHandle3);
		item3.setProductId(productId3);
		item3.setProductGuid(productKey3);

		classUnderTest.addItem(item1);
		classUnderTest.addItem(item2);
		classUnderTest.addItem(item3);

		// start test
		final ItemList itemList = classUnderTest.getItemList();
		assertEquals(3, itemList.size());

	}

}
