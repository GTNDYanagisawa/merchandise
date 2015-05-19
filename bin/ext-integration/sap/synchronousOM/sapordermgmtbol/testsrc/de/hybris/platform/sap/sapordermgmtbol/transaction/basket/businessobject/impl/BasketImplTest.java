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
package de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.interf.BasketBackend;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl.TransactionConfigurationImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class BasketImplTest extends SapordermanagmentBolSpringJunitTest
{

	private static final String BASKET_TYPE = "BasketType";

	@Resource(name = SapordermgmtbolConstants.ALIAS_BO_CART)
	private BasketImpl classUnderTest;
	private BasketBackend mockedBasketBackend;
	private TransactionConfiguration transConf;


	@Override
	@Before
	public void setUp()
	{
		transConf = new TransactionConfigurationImpl();
		mockedBasketBackend = EasyMock.createMock(BasketBackend.class);

		classUnderTest.setTechKey(TechKey.generateKey());

		classUnderTest.setBackendService(mockedBasketBackend);
		classUnderTest.getHeader().setProcessType(BASKET_TYPE);

		classUnderTest.setTransactionConfiguration(transConf);


	}

	private void replay()
	{
		EasyMock.replay(mockedBasketBackend);
	}



	@Test
	public void testPrepareItemsWithChangedProducts_ChangedProduct() throws CommunicationException
	{
		final String oldProduct = "oldProduct";
		final String newProduct = "newProduct";

		try
		{
			mockedBasketBackend.updateInBackend(classUnderTest, transConf, new ArrayList<TechKey>());
			mockedBasketBackend.updateInBackend(classUnderTest, transConf, null);
		}
		catch (final BackendException e)
		{

			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);

		}
		replay();

		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);

		item.setProductGuid(new TechKey("productId"));
		item.setProductId(oldProduct);
		item.setUnit("unit");
		item.setParentId(new TechKey(null));


		item.setProductId(newProduct);

		classUnderTest.addItem(item);

		classUnderTest.update();

		assertEquals("Wrong product was found", newProduct, item.getProductId());
		assertEquals("New product was found, but unit was not set to null", null, item.getUnit());
		assertEquals("New product was found, but TechKey was not set to Empty", TechKey.EMPTY_KEY, item.getProductGuid());
	}

	@Test
	public void testPrepareItemsWithChangedProducts_UnChangedProduct() throws CommunicationException
	{
		final String oldProduct = "oldProduct";
		final String newProduct = "oldProduct";

		try
		{
			mockedBasketBackend.updateInBackend(classUnderTest, transConf, new ArrayList<TechKey>());
			mockedBasketBackend.updateInBackend(classUnderTest, transConf, null);
		}
		catch (final BackendException e)
		{

			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);

		}
		replay();

		final Item item = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);

		item.setProductGuid(new TechKey("productId"));
		item.setProductId(oldProduct);
		item.setUnit("unit");
		item.setParentId(new TechKey(null));

		item.setProductId(newProduct);

		classUnderTest.addItem(item);

		classUnderTest.update();

		assertEquals("Wrong product was found", newProduct, item.getProductId());
		assertNotSame("No new product was found, but unit was set to null", null, item.getUnit());
		assertNotSame("No new product was found, but TechKey was not set to Empty", TechKey.EMPTY_KEY, item.getProductGuid());
	}

	@Test
	public void testSaveAndCommit_false() throws CommunicationException, BackendException
	{
		EasyMock.expect(mockedBasketBackend.saveInBackend(classUnderTest, true)).andReturn(false);
		classUnderTest.setDirty(false);
		classUnderTest.getHeader().setDirty(false);
		replay();

		assertFalse(classUnderTest.saveAndCommit());
		assertTrue(classUnderTest.isDirty());
		assertTrue(classUnderTest.getHeader().isDirty());
	}

	@Test
	public void testSaveAndCommit_true() throws CommunicationException, BackendException
	{
		EasyMock.expect(mockedBasketBackend.saveInBackend(classUnderTest, true)).andReturn(true);
		classUnderTest.setDirty(false);
		classUnderTest.getHeader().setDirty(false);
		replay();

		assertTrue(classUnderTest.saveAndCommit());
		assertTrue(classUnderTest.isDirty());
		assertTrue(classUnderTest.getHeader().isDirty());
	}

	@Test
	public void testRead() throws CommunicationException, BackendException
	{
		EasyMock.replay(mockedBasketBackend);
		classUnderTest.setBackendService(mockedBasketBackend);
		classUnderTest.setDirty(false);

		classUnderTest.read();

		EasyMock.verify(mockedBasketBackend);
	}

	@Test
	public void testReadForUpdate() throws CommunicationException, BackendException
	{
		EasyMock.replay(mockedBasketBackend);
		classUnderTest.setBackendService(mockedBasketBackend);
		classUnderTest.setDirty(false);

		classUnderTest.readForUpdate();

		EasyMock.verify(mockedBasketBackend);
	}

	@Test
	public void testRead_dirty() throws CommunicationException, BackendException
	{
		expectOneRead();
		EasyMock.replay(mockedBasketBackend);
		classUnderTest.setBackendService(mockedBasketBackend);
		classUnderTest.setDirty(true);

		classUnderTest.read();

		EasyMock.verify(mockedBasketBackend);
	}

	@Test
	public void testReadForUpdate_dirty() throws CommunicationException, BackendException
	{
		expectOneReadForUpdate();
		EasyMock.replay(mockedBasketBackend);
		classUnderTest.setBackendService(mockedBasketBackend);
		classUnderTest.setDirty(true);

		classUnderTest.readForUpdate();

		EasyMock.verify(mockedBasketBackend);
	}


	private void expectOneReadForUpdate() throws BackendException
	{
		mockedBasketBackend.readForUpdateFromBackend(classUnderTest);
		EasyMock.expectLastCall().times(1);

	}

	private void expectOneRead() throws BackendException
	{
		mockedBasketBackend.readFromBackend(classUnderTest, transConf, false);
		EasyMock.expectLastCall().times(1);
	}

	public void testRead_notConsistent() throws CommunicationException, BackendException
	{
		expectOneUpdate();
		expectOneRead();
		EasyMock.replay(mockedBasketBackend);
		classUnderTest.setBackendService(mockedBasketBackend);
		classUnderTest.setDirty(false);

		classUnderTest.read();

		EasyMock.verify(mockedBasketBackend);
	}

	@Test
	public void testUpdate() throws CommunicationException, BackendException
	{
		expectOneUpdate();
		EasyMock.replay(mockedBasketBackend);
		classUnderTest.setBackendService(mockedBasketBackend);

		classUnderTest.update();

		assertEquals("Process type must not be copied", BASKET_TYPE, classUnderTest.getHeader().getProcessType());
		EasyMock.verify(mockedBasketBackend);
	}

	private void expectOneUpdate() throws BackendException
	{
		final List<TechKey> emptyList = new ArrayList<TechKey>();
		mockedBasketBackend.updateInBackend(classUnderTest, transConf, emptyList);
		EasyMock.expectLastCall().times(1);
	}

	@Test
	public void testRemoveEmptyItemsFromItemList()
	{

		addItem(new TechKey("A"), "a product");
		addItem(null, null);
		addItem(null, "");

		assertEquals(3, classUnderTest.getItemList().size());

		classUnderTest.removeEmptyItemsFromItemList();
		assertEquals("Empty items must be removed", 1, classUnderTest.getItemList().size());

	}

	@Test
	public void testRemoveEmptyItemsFromItemList_removeExistingItem()
	{

		addItem(new TechKey("A"), "");

		assertEquals(1, classUnderTest.getItemList().size());

		classUnderTest.removeEmptyItemsFromItemList();
		assertEquals("Item must not be removed", 1, classUnderTest.getItemList().size());

	}

	private Item addItem(final TechKey key, final String product)
	{
		final Item item = new ItemSalesDoc();
		item.setProductId(product);
		item.setTechKey(key);
		item.setQuantity(new BigDecimal(1));
		classUnderTest.addItem(item);
		return item;
	}

	@Test
	public void test_getTotalQuantity_empty() throws Exception
	{

		final BigDecimal total = classUnderTest.calculateTotalQuantity();
		assertEquals(BigDecimal.ZERO, total);

	}

	@Test
	public void test_getTotalQuantity_normalItems() throws Exception
	{
		addItem(TechKey.generateKey(), "A");
		addItem(TechKey.generateKey(), "B");

		final BigDecimal total = classUnderTest.calculateTotalQuantity();
		assertEquals(new BigDecimal("2"), total);

	}

	@Test
	public void test_getTotalQuantity_normalItemsWithEmptyItem() throws Exception
	{
		addItem(TechKey.generateKey(), "A");
		addItem(TechKey.generateKey(), "B");
		addItem(TechKey.generateKey(), "");

		final BigDecimal total = classUnderTest.calculateTotalQuantity();
		assertEquals(new BigDecimal("2"), total);

	}

	@Test
	public void testReleaseBackendService() throws BackendException, CommunicationException
	{
		classUnderTest.release();
		final SalesDocumentBackend backendService = classUnderTest.getBackendService();
		assertNotNull(backendService);
	}

	@Test
	public void testReleaseHeader() throws CommunicationException
	{
		final String poNr = "A";
		classUnderTest.getHeader().setPurchaseOrderExt(poNr);
		classUnderTest.release();
		assertNull(classUnderTest.getHeader().getPurchaseOrderExt());
	}

	@Test
	public void testReleaseItems() throws CommunicationException
	{
		final Item item = new ItemSalesDoc();
		classUnderTest.addItem(item);
		classUnderTest.release();
		assertEquals(0, classUnderTest.getItemList().size());
	}

	@Test
	public void testReleaseMessages() throws CommunicationException
	{
		final Message message = new Message(Message.ERROR);
		classUnderTest.addMessage(message);
		classUnderTest.release();
		assertEquals(0, classUnderTest.getMessageList().size());
	}

	@Test
	public void testBackendDown()
	{
		assertFalse(classUnderTest.isBackendDown());
	}


}
