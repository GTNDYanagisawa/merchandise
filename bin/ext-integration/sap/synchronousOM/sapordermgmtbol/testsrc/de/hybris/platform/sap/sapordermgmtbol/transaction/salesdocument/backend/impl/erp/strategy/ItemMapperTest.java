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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.sap.conn.jco.JCoTable;


@SuppressWarnings("javadoc")
public class ItemMapperTest extends JCORecTestBase
{

	ItemMapper classUnderTest;

	private static JCoTable itemComVTable;
	private static JCoTable itemComRTable;
	private static JCoTable itemComRTableWith3;
	private static JCoTable itemKeyTable;
	private Item item;
	private Item item2;
	private Map<String, String> itemKey;
	private Map<String, Item> itemMap;
	private String posnr;
	private String posnr2;

	private SalesDocument salesDoc;

	private TransactionConfiguration transactionConfiguration;

	private String key;

	private String key2;

	@Test
	public void testBeanInitialization()
	{
		final ItemMapper cut = (ItemMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER);
		assertNotNull(cut);
	}

	@Test
	public void testBeanDependencyInjection()
	{
		final ItemMapper cut = (ItemMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER);
		assertNotNull(cut.converter);
	}



	@Test
	public void testDetermineRootParentOneLevel()
	{
		itemMap = fillItemMapWithTwo();
		final Item item = itemMap.get("B");
		final Item parent = classUnderTest.determineRootParentItem(itemMap, item);

		assertEquals("A", parent.getHandle());
	}

	@Test
	public void testDetermineRootParentTwoLevels()
	{
		itemMap = fillItemMapWithThree();
		final Item item = itemMap.get("C");
		final Item parent = classUnderTest.determineRootParentItem(itemMap, item);

		assertEquals("A", parent.getHandle());
	}


	@Test
	public void testBuildItemKeyMapNullTable()
	{
		final Map<String, String> itemMap = classUnderTest.buildItemKeyMap(null);
		assertNull("We expect null", itemMap);
	}



	private Map<String, Item> fillItemMapWithThree()
	{
		final Map<String, Item> itemMap = this.fillItemMapWithTwo();
		final Item it3 = new ItemSalesDoc();
		it3.setHandle("C");
		it3.setConfigurable(true);
		it3.setParentHandle("B");

		itemMap.put("C", it3);

		return itemMap;

	}

	private Map<String, Item> fillItemMapWithTwo()
	{
		final Map<String, Item> itemMap = new HashMap<String, Item>();

		final Item it1 = new ItemSalesDoc();
		it1.setHandle("A");
		it1.setConfigurable(true);

		itemMap.put("A", it1);

		final Item it2 = new ItemSalesDoc();
		it2.setHandle("B");
		it2.setConfigurable(true);
		it2.setParentHandle("A");

		itemMap.put("B", it2);

		return itemMap;

	}


	@Test
	public void testCheckParentItemConfigurableOneLevel()
	{
		itemMap = fillItemMapWithTwo();

		classUnderTest.handleItemUsages(itemComRTable, itemMap);
		assertEquals(ItemBase.ItemUsage.CONFIGURATION, itemMap.get("B").getItemUsage());
	}

	@Test
	public void testCheckParentItemConfigurableTwoLevels()
	{
		itemMap = fillItemMapWithThree();

		classUnderTest.handleItemUsages(itemComRTableWith3, itemMap);
		assertEquals(ItemBase.ItemUsage.CONFIGURATION, itemMap.get("B").getItemUsage());
		assertEquals(ItemBase.ItemUsage.CONFIGURATION, itemMap.get("C").getItemUsage());
	}

	@Test
	public void testBuildItemKeyMap()
	{

		final Map<String, String> itemMap = classUnderTest.buildItemKeyMap(itemKeyTable);
		assertEquals("We expect 2 entries in item map", 2, itemMap.size());
		assertNotNull(itemMap.get("10"));
		assertEquals("We expect correct handle in item 10", "A", itemMap.get("10"));
	}

	@Test
	public void testBuildItemKeyMapEmptyTable()
	{
		itemKeyTable.deleteAllRows();
		final Map<String, String> itemMap = classUnderTest.buildItemKeyMap(itemKeyTable);
		assertNull("We expect null", itemMap);
		readTablesFromRepository();
	}

	@Test
	public void testBuildItemMapNoERPItems()
	{
		final SalesDocument businessObjectInterface = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		final ItemList itemList = new ItemListImpl();
		final Map<String, Item> itemsERPState = new HashMap<>();
		final Map<String, Item> itemMap = classUnderTest.buildItemMap(businessObjectInterface, itemList, itemsERPState,
				itemComVTable);
		assertNotNull(itemMap);
		//we expect 2 items, as the corresponding JCO table contains 2 items
		checkItemListSize(itemList, itemMap, 2);
	}

	@Test
	public void testBuildItemMapWithERPItems()
	{
		final SalesDocument businessObjectInterface = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		final ItemList itemList = new ItemListImpl();
		final Map<String, Item> itemsERPState = new HashMap<>();
		itemsERPState.put("C", new ItemSalesDoc());
		final Map<String, Item> itemMap = classUnderTest.buildItemMap(businessObjectInterface, itemList, itemsERPState,
				itemComVTable);
		assertNotNull(itemMap);
		//we expect 3 items, as the corresponding JCO table contains 2 items, and we had an additional one from ERP
		final int itemNo = 3;
		checkItemListSize(itemList, itemMap, itemNo);
	}

	@Test
	public void testCheckItemConfigurable()
	{
		assertFalse(classUnderTest.checkIsItemConfigurable(itemComRTable));
	}

	@Test
	public void testCheckIsItemVariant()
	{
		assertTrue(classUnderTest.checkIsItemVariant(itemComRTable));

	}

	@Test
	public void testDetermineItemPosnr()
	{
		final int lastNumber = 10;
		final int determineItemPosnr = classUnderTest.determineItemPosnr(item, lastNumber);
		assertEquals(20, determineItemPosnr);
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testGenerateSubtotalFieldNameExeption()
	{
		classUnderTest.generateSubtotalFieldName("A");
	}

	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testGenerateSubtotalFieldNameExeptionNull()
	{
		classUnderTest.generateSubtotalFieldName(null);
	}

	@Test
	public void testGenerateSubtotalFieldName()
	{
		assertEquals("KZWI1_R", classUnderTest.generateSubtotalFieldName("SUBTOTAL1"));
		assertEquals("KZWI2_R", classUnderTest.generateSubtotalFieldName("SUBTOTAL2"));
		assertEquals("KZWI3_R", classUnderTest.generateSubtotalFieldName("SUBTOTAL3"));
		assertEquals("KZWI4_R", classUnderTest.generateSubtotalFieldName("SUBTOTAL4"));
		assertEquals("KZWI5_R", classUnderTest.generateSubtotalFieldName("SUBTOTAL5"));
		assertEquals("KZWI6_R", classUnderTest.generateSubtotalFieldName("SUBTOTAL6"));
	}

	@Test
	public void testGetFieldNameForFreight()
	{

		final String fieldNameForFreight = classUnderTest.getFieldNameForFreight(salesDoc);
		assertEquals("KZWI3_R", fieldNameForFreight);
	}

	@Test
	public void testGetFieldNameForNetValueWOFreight()
	{

		final String fieldNameForFreight = classUnderTest.getFieldNameForNetValueWOFreight(salesDoc);
		assertEquals("KZWI6_R", fieldNameForFreight);
	}



	@Test(expected = ApplicationBaseRuntimeException.class)
	public void testHandleTtItemComRMapDoesNotContainItem()
	{
		classUnderTest.handleTtItemComR(salesDoc, itemComRTableWith3, transactionConfiguration, itemMap);
	}

	@Test
	public void testHandleTtItemComR()
	{
		classUnderTest.setConverter(null);
		classUnderTest.handleTtItemComR(salesDoc, itemComRTable, transactionConfiguration, itemMap);
		assertEquals(2, itemMap.size());
		final Item item = itemMap.get(key);
		assertEquals(new BigDecimal("99.00"), item.getNetValueWOFreight());
		assertEquals(new BigDecimal("10.00"), item.getFreightValue());
		assertEquals("USD", item.getCurrency());
	}

	void checkItemListSize(final ItemList itemList, final Map<String, Item> itemMap, final int itemNo)
	{
		assertEquals(itemNo, itemMap.size());
		assertEquals(itemNo, itemList.size());
	}


	@Override
	public void setUp()
	{
		super.setUp();

		classUnderTest = (ItemMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER);
		//		classUnderTest.setCustExit(new ERPLO_APICustomerExitsImpl());

		if (itemKeyTable == null)
		{
			readTablesFromRepository();
		}
		key = "A";
		key2 = "B";
		posnr = "000010";
		item = new ItemSalesDoc();
		item.setTechKey(new TechKey(key));
		item.setNumberInt(10);

		item2 = new ItemSalesDoc();
		item2.setTechKey(new TechKey(key2));
		item2.setNumberInt(20);

		itemKey = new HashMap<String, String>();
		itemKey.put(posnr, key);
		itemKey.put(posnr2, key2);
		itemMap = new HashMap<String, Item>();
		itemMap.put(key, item);
		itemMap.put(key2, item2);

		salesDoc = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		transactionConfiguration = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);
	}

	private void readTablesFromRepository()
	{

		try
		{
			final JCoMockRepository testRepository = getJCORepository("jcoReposGetAllStrategy");

			itemKeyTable = testRepository.getTable("TT_ITEM_KEY");
			itemComRTable = testRepository.getTable("TT_ITEM_COMR");
			itemComVTable = testRepository.getTable("TT_ITEM_COMV");
			itemComRTableWith3 = testRepository.getTable("TT_ITEM_COMR3");

		}
		catch (final JCoRecException e)
		{
			throw new ApplicationBaseRuntimeException("Problem with reading data from XML repository", e);
		}

	}




}
