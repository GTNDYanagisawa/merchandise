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
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import com.sap.conn.jco.JCoStructure;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp.BasketERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.SalesDocumentERP;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

@SuppressWarnings("javadoc")
public class HeaderMapperJCoRecTest extends JCORecTestBase {

	private static final String DEL_BLOCK = "DL";
	private static final String PURCH_ORDER_TYPE = "OR";
	private static final String SHIP_COND = "VD02";
	private static final String HANDLE = "123";
	private static final String PURCHASE_ORDER_EXT = "This is a unit test";
	private static final TechKey TECH_KEY_ERRORNEOUS = new TechKey("A");
	private static final TechKey TECH_KEY_OK = new TechKey("B");
	private JCoStructure esError = null;

	private JCoStructure headComV;
	private JCoStructure headComX;
	private TransactionConfiguration tc = null;
	private SalesDocument salesDoc;

	public HeaderMapper cut;

	protected void readHeaderTables() {
		final JCoMockRepository testRepository = getJCORepository("jcoReposSetStrategy");

		try {
			headComV = (JCoStructure) testRepository.getRecord("TDS_HEAD_COMV");
			headComX = (JCoStructure) testRepository.getRecord("TDS_HEAD_COMX");

		} catch (final JCoRecException e) {
			throw new ApplicationBaseRuntimeException(
					"Problem with reading data from XML repository", e);
		}

	}

	protected void readTablesFromRepository() {
		final JCoMockRepository testRepository = getJCORepository("jcoReposSetStrategy");

		try {
			esError = (JCoStructure) testRepository.getRecord("TDS_ERROR");

		} catch (final JCoRecException e) {
			throw new ApplicationBaseRuntimeException(
					"Problem with reading data from XML repository", e);
		}

	}

	protected void fillSalesDocHeader(final SalesDocument salesDoc) {

		salesDoc.getHeader().setHandle(HANDLE);
		salesDoc.getHeader().setPurchaseOrderExt(PURCHASE_ORDER_EXT);
		salesDoc.getHeader().setShipCond(SHIP_COND);
	}

	@Override
	public void setUp() {
		super.setUp();
		cut = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_MAPPER);

		if (esError == null) {
			readTablesFromRepository();
		}
		tc = (TransactionConfiguration) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);

		readHeaderTables();
		salesDoc = (SalesDocument) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		fillSalesDocHeader(salesDoc);
		final Item item = salesDoc.createItem();
		item.setTechKey(TECH_KEY_ERRORNEOUS);
		salesDoc.addItem(item);
		final Item itemOk = salesDoc.createItem();
		itemOk.setTechKey(TECH_KEY_OK);
		salesDoc.addItem(itemOk);
	}

	@Test
	public void testFillDeliveryBlockWithCreateMode() {
		final SalesDocumentERP salesDocR3Lrd = new BasketERP();
		salesDocR3Lrd.setLoadStateCreate();
		salesDocR3Lrd.getLoadState().setLoadOperation(LoadOperation.create);

		tc.setDeliveryBlock(DEL_BLOCK);
		tc.setCustomerPurchOrderType(PURCH_ORDER_TYPE);

		cut.write(salesDoc.getHeader(), salesDocR3Lrd, headComV, headComX, tc);

		assertEquals(HANDLE, JCoHelper.getString(headComV, "HANDLE"));
		assertEquals(DEL_BLOCK, JCoHelper.getString(headComV, "LIFSK"));
		assertEquals(PURCH_ORDER_TYPE, JCoHelper.getString(headComV, "BSARK"));

		assertEquals(HANDLE, JCoHelper.getString(headComX, "HANDLE"));
		assertEquals("X", JCoHelper.getString(headComX, "LIFSK"));
		assertEquals("X", JCoHelper.getString(headComX, "BSARK"));

	}

	@Test
	public void testFillDeliveryBlockWithEditMode() {
		final SalesDocumentERP salesDocR3Lrd = new BasketERP();
		salesDocR3Lrd.setLoadStateCreate();
		salesDocR3Lrd.getLoadState().setLoadOperation(LoadOperation.edit);

		tc.setDeliveryBlock(DEL_BLOCK);
		tc.setCustomerPurchOrderType(PURCH_ORDER_TYPE);

		cut.write(salesDoc.getHeader(), salesDocR3Lrd, headComV, headComX, tc);

		assertEquals(HANDLE, JCoHelper.getString(headComV, "HANDLE"));
		assertEquals(DEL_BLOCK, JCoHelper.getString(headComV, "LIFSK"));
		assertNotSame(PURCH_ORDER_TYPE, JCoHelper.getString(headComV, "BSARK"));

		assertEquals(HANDLE, JCoHelper.getString(headComX, "HANDLE"));
		assertEquals("X", JCoHelper.getString(headComX, "LIFSK"));
		assertEquals("", JCoHelper.getString(headComX, "BSARK"));

	}

	@Test
	public void testFillPurchaseOrderNr() {
		final SalesDocumentERP salesDocR3Lrd = new BasketERP();
		salesDocR3Lrd.setLoadStateCreate();
		salesDocR3Lrd.getLoadState().setLoadOperation(LoadOperation.create);

		cut.write(salesDoc.getHeader(), salesDocR3Lrd, headComV, headComX, tc);

		assertEquals(HANDLE, JCoHelper.getString(headComV, "HANDLE"));
		assertEquals(PURCHASE_ORDER_EXT, JCoHelper.getString(headComV, "BSTKD"));

		assertEquals(HANDLE, JCoHelper.getString(headComX, "HANDLE"));
		assertEquals("X", JCoHelper.getString(headComX, "BSTKD"));
	}

	@Test
	public void testFillShipCond() {
		final SalesDocumentERP salesDocR3Lrd = new BasketERP();
		salesDocR3Lrd.setLoadStateCreate();
		salesDocR3Lrd.getLoadState().setLoadOperation(LoadOperation.create);

		cut.write(salesDoc.getHeader(), salesDocR3Lrd, headComV, headComX, tc);

		assertEquals(HANDLE, JCoHelper.getString(headComV, "HANDLE"));
		assertEquals(SHIP_COND, JCoHelper.getString(headComV, "VSBED"));

		assertEquals(HANDLE, JCoHelper.getString(headComX, "HANDLE"));
		assertEquals("X", JCoHelper.getString(headComX, "VSBED"));
	}

}
