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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.TextImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.BackendMessageMapperImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.BackendMessageMapper;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import java.math.BigDecimal;

import org.easymock.EasyMock;
import org.junit.Test;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


@SuppressWarnings("javadoc")
public class SetStrategyERPJcoRecTest extends JCORecTestBase
{

	SetStrategyERP cut;

	private static final String SHIP_COND = "VD02";
	private static final String HANDLE = "123";
	private static final String PURCHASE_ORDER_EXT = "This is a unit test";
	private static final TechKey TECH_KEY_ERRORNEOUS = new TechKey("A");
	private static final TechKey TECH_KEY_OK = new TechKey("B");

	private static JCoStructure esError = null;
	private static JCoStructure esError_noItemError = null;
	private static JCoStructure esError_noErrorAtAll = null;
	private SalesDocument salesDoc;

	@Test
	public void testMarkInvalidItems() throws BackendException
	{
		// disable message mapping 
		final BackendMessageMapper messageMapperMock = EasyMock.createNiceMock(BackendMessageMapperImpl.class);
		messageMapperMock.map((BusinessObject) EasyMock.anyObject(), (JCoRecord) EasyMock.anyObject(),
				(JCoTable) EasyMock.anyObject());
		EasyMock.expectLastCall();
		EasyMock.replay(messageMapperMock);

		cut.setMessageMapper(messageMapperMock);
		cut.markInvalidItems(salesDoc, esError);

		Item item = salesDoc.getItemList().get(TECH_KEY_ERRORNEOUS);
		assertNotNull("Item not present", item);
		assertTrue("Item must be errorneous", item.isErroneous());
		item = salesDoc.getItemList().get(TECH_KEY_OK);
		assertNotNull("Item not present", item);
		assertFalse("Item must be ok", item.isErroneous());
	}

	@Test
	public void testMarkInvalidItems_NoItemError() throws BackendException
	{
		cut.markInvalidItems(salesDoc, esError_noItemError);
		checkAllItemsOk();
	}

	@Test
	public void testMarkInvalidItems_NoErrorAtAll() throws BackendException
	{
		cut.markInvalidItems(salesDoc, esError_noErrorAtAll);
		checkAllItemsOk();
	}


	@Test
	public void testIsEqualTexts()
	{
		final Item item1 = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final Item item2 = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final Text text1 = new TextImpl("001", "A");
		final Text text2 = new TextImpl("002", "A");
		item1.setText(text1);
		item2.setText(text2);

		assertTrue(cut.isEqualTexts(item1, item2));
	}

	@Test
	public void testIsEqualTextsDifferentTexts()
	{
		final Item item1 = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final Item item2 = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final Text text1 = new TextImpl("001", "A");
		final Text text2 = new TextImpl("002", "B");
		item1.setText(text1);
		item2.setText(text2);

		assertFalse(cut.isEqualTexts(item1, item2));
	}

	@Test
	public void testIsEqualTextsNoTexts()
	{
		final Item item1 = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final Item item2 = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);

		assertTrue(cut.isEqualTexts(item1, item2));
	}

	protected void checkAllItemsOk()
	{
		Item item = salesDoc.getItemList().get(TECH_KEY_ERRORNEOUS);
		assertNotNull("Item not present", item);
		assertFalse("Item must be ok", item.isErroneous());
		item = salesDoc.getItemList().get(TECH_KEY_OK);
		assertNotNull("Item not present", item);
		assertFalse("Item must be ok", item.isErroneous());
	}

	@Override
	public void setUp()
	{
		super.setUp();

		cut = (SetStrategyERP) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_WRITE_STRATEGY);

		if (esError == null)
		{
			readTablesFromRepository();
		}
		readHeaderTables();
		salesDoc = (SalesDocument) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		fillSalesDocHeader(salesDoc);
		final Item item = salesDoc.createItem();
		item.setTechKey(TECH_KEY_ERRORNEOUS);
		salesDoc.addItem(item);
		final Item itemOk = salesDoc.createItem();
		itemOk.setTechKey(TECH_KEY_OK);
		salesDoc.addItem(itemOk);

		// tc.getCommonConfiguration().setCurrency("USD");

	}

	private void fillSalesDocHeader(final SalesDocument salesDoc)
	{

		salesDoc.getHeader().setHandle(HANDLE);
		salesDoc.getHeader().setPurchaseOrderExt(PURCHASE_ORDER_EXT);
		salesDoc.getHeader().setShipCond(SHIP_COND);
	}

	private void readTablesFromRepository()
	{
		final JCoMockRepository testRepository = getJCORepository("jcoReposSetStrategy");

		try
		{
			esError = (JCoStructure) testRepository.getRecord("TDS_ERROR");
			esError_noItemError = (JCoStructure) testRepository.getRecord("TDS_ERROR_NO_ITEM_ERROR");
			esError_noErrorAtAll = (JCoStructure) testRepository.getRecord("TDS_ERROR_NO_ERROR");

		}
		catch (final JCoRecException e)
		{
			throw new ApplicationBaseRuntimeException("Problem with reading data from XML repository", e);
		}

	}

	private void readHeaderTables()
	{
		//


	}

	@Test
	public void testIsItemSent_FreeQuantity()
	{
		final Item cItem = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final Item eItem = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		cItem.setProductId("HT-1010");
		eItem.setProductId("HT-1010");
		cItem.setQuantity(BigDecimal.TEN);
		eItem.setQuantity(new BigDecimal("9"));
		cItem.setFreeQuantity(BigDecimal.ONE);
		cItem.setUnit("ST");
		eItem.setUnit("ST");

		assertFalse(cut.isItemToBeSent(cItem, eItem));
	}

	@Test
	public void testIsItemSent_QuantityChange()
	{
		final Item cItem = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final Item eItem = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		cItem.setProductId("HT-1010");
		eItem.setProductId("HT-1010");
		cItem.setQuantity(BigDecimal.TEN);
		eItem.setQuantity(BigDecimal.ONE);
		cItem.setFreeQuantity(BigDecimal.ZERO);
		cItem.setUnit("ST");
		eItem.setUnit("ST");

		assertFalse(cut.isItemToBeSent(cItem, eItem));
	}



	@Test
	public void testIsNotSent_QuantityIsEqual()
	{
		final Item cItem = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		final Item eItem = (Item) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM);
		cItem.setProductId("HT-1010");
		eItem.setProductId("HT-1010");
		cItem.setQuantity(BigDecimal.TEN);
		final BigDecimal ten = BigDecimal.TEN;
		ten.setScale(4);
		eItem.setQuantity(ten);
		cItem.setFreeQuantity(BigDecimal.ZERO);
		cItem.setUnit("ST");
		eItem.setUnit("ST");

		assertTrue(cut.isItemToBeSent(cItem, eItem));
	}
}
