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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.OrderImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.ProductConfigurationStrategyImplTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ItemBuffer;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;


@SuppressWarnings("javadoc")
public class SalesDocumentERPTest extends SapordermanagmentBolSpringJunitTest
{
	SalesDocumentERP classUnderTest = null;
	SalesDocument salesDocument = null;
	Item item = null;

	@Before
	public void init()
	{
		classUnderTest = (SalesDocumentERP) genericFactory.getBean(SapordermgmtbolConstants.BEAN_ID_BE_CART_ERP);

		salesDocument = new OrderImpl();
		salesDocument.setHeader(new HeaderSalesDocument());
		final ItemListImpl itemList = new ItemListImpl();
		item = new ItemSalesDoc();
		item.setProductConfiguration(ProductConfigurationStrategyImplTest.getConfigModel(null));
		item.setConfigurable(true);
		item.setQuantity(new BigDecimal(2));
		itemList.add(item);
		salesDocument.setItemList(itemList);

	}

	@Test
	public void testConstruting()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void testConfigurationInjection()
	{
		assertEquals("ZSHN", classUnderTest.headerCondTypeFreight);
	}

	@Test
	public void testAddErroneousItems()
	{
		assertEquals(0, classUnderTest.getErroneousItems().size());
		final Item item = new ItemSalesDoc();
		classUnderTest.addErroneousItem(item);
		assertEquals(1, classUnderTest.getErroneousItems().size());
	}

	@Test
	public void testClearErrItems()
	{
		final Item item = new ItemSalesDoc();
		classUnderTest.addErroneousItem(item);
		classUnderTest.clearErroneousItems();
		assertEquals(0, classUnderTest.getErroneousItems().size());
	}

	@Test
	public void testInitializationError()
	{
		salesDocument.setTechKey(TechKey.EMPTY_KEY);
		final boolean checkForInitializationError = classUnderTest.checkForInitializationError(salesDocument);
		//document is not stored in backend, and not being initialized
		assertTrue(checkForInitializationError);
	}

	@Test
	public void testInitializationErrorIsInBackend()
	{
		salesDocument.setTechKey(new TechKey("0000012345"));
		final boolean checkForInitializationError = classUnderTest.checkForInitializationError(salesDocument);
		//document is stored in backend
		assertFalse(checkForInitializationError);
	}

	@Test
	public void testProductConfigStrategy()
	{
		assertNotNull(classUnderTest.getProductConfigurationStrategy());
	}

	@Test
	public void testWriteConfiguration()
	{
		classUnderTest.writeConfiguration(salesDocument);
		//document is not initialized yet, so no call to the back end will take place
		assertEquals(0, salesDocument.getMessageList().size());
	}

	@Test
	public void testValidate() throws BackendException
	{
		//sales document not initialized -> no backend call, no messages
		classUnderTest.validate(salesDocument);
		assertEquals(0, salesDocument.getMessageList().size());
	}

	@Test
	public void testStoreItemsBeforeValidate()
	{
		final ItemList items = classUnderTest.storeItemsBeforeValidate(salesDocument);
		assertNotNull(items);
		assertEquals(1, items.size());
	}

	@Test
	public void testPrepareItemsForFirstValidateStep()
	{
		classUnderTest.prepareItemsForFirstValidateStep(salesDocument);
		final ItemList items = salesDocument.getItemList();
		assertNotNull(items);
		assertEquals(1, items.size());
		assertEquals(BigDecimal.ONE, items.get(0).getQuantity());
	}

	@Test
	public void testPrepareItemsForFirstValidateStepIncreaseQty()
	{
		item.setQuantity(new BigDecimal(0.5));
		classUnderTest.prepareItemsForFirstValidateStep(salesDocument);
		final ItemList items = salesDocument.getItemList();
		assertNotNull(items);
		assertEquals(1, items.size());
		assertEquals(new BigDecimal(1.5), items.get(0).getQuantity());
	}

	@Test
	public void testPrepareForValidation()
	{
		final ItemBuffer itemBuffer = new ItemBufferImpl();
		final Map<String, Item> itemsERPState = new HashMap<String, Item>();
		itemsERPState.put("A", new ItemSalesDoc());
		itemBuffer.setItemsERPState(itemsERPState);
		classUnderTest.setItemBuffer(itemBuffer);
		final ItemList storedItems = classUnderTest.prepareForValidation(salesDocument);
		assertNotNull(storedItems);
		assertEquals(1, storedItems.size());
		assertEquals(0, classUnderTest.getItemBuffer().getItemsERPState().size());
	}

	@Test
	public void testDetermineConfigurableItems()
	{
		final List<String> itemHandleList = classUnderTest.determineConfigurableItems(salesDocument);
		assertNotNull(itemHandleList);
		assertEquals(1, itemHandleList.size());
	}

	@Test(expected = BeanCreationException.class)
	public void testBackendDown()
	{
		classUnderTest.isBackendDown();
	}

	@Test(expected = BeanCreationException.class)
	public void testClose()
	{
		classUnderTest.closeBackendSession();
	}

}
