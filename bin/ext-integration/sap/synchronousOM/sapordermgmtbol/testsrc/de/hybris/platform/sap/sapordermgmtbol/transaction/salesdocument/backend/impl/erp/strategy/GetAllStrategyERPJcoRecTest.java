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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp.BasketERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.TextImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.ItemBufferImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ItemBuffer;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.GetAllReadParameters;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


@UnitTest
@SuppressWarnings("javadoc")
public class GetAllStrategyERPJcoRecTest extends JCORecTestBase
{


	GetAllStrategyERP classUnderTest = null;
	private static JCoTable ttHeadDocFlowUnknownDocType;
	private static JCoTable ttHeadDocFlowOrder;
	private static JCoTable itemKeyTable;
	private static JCoTable itItemObjreq;
	private Item item;
	private Map<String, String> itemKey;
	private Map<String, Item> itemMap;
	private String posnr;
	private final String vbeln = "0000012345";
	private final String vbelnNotPersisted = "AABBB07625243BCD";
	private JCoFunction function;
	JCoConnection connection = null;
	private JCoParameterList importParameters;
	private JCoTable itHeadFieldreqComv;
	private JCoTable itHeadFieldreqComr;
	private JCoTable itItemFieldreqComv;
	private JCoTable itItemFieldreqComr;


	@Before
	public void init() throws BackendException
	{
		classUnderTest = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_READ_STRATEGY);
		function = EasyMock.createMock(JCoFunction.class);
		importParameters = EasyMock.createMock(JCoParameterList.class);
		EasyMock.expect(importParameters.getTable("IT_ITEM_OBJREQ")).andReturn(itItemObjreq);
		EasyMock.expect(importParameters.getTable("IT_HEAD_FIELDREQ_COMV")).andReturn(itHeadFieldreqComv);
		EasyMock.expect(importParameters.getTable("IT_HEAD_FIELDREQ_COMR")).andReturn(itHeadFieldreqComr);
		EasyMock.expect(importParameters.getTable("IT_ITEM_FIELDREQ_COMV")).andReturn(itItemFieldreqComv);
		EasyMock.expect(importParameters.getTable("IT_ITEM_FIELDREQ_COMR")).andReturn(itItemFieldreqComr);
		importParameters.setValue("IV_VBELN", vbeln);
		importParameters.setValue("IV_TRTYP", LoadOperation.display);
		importParameters.setValue("IF_INIT_MSGLOG", "X");
		importParameters.setValue("IV_SCENARIO_ID", ConstantsR3Lrd.scenario_LO_API_WEC);
		final JCoStructure structureLogicSwitch = EasyMock.createMock(JCoStructure.class);
		structureLogicSwitch.setValue("FAST_DISPLAY", "X");
		structureLogicSwitch.setValue("NO_CONVERSION", "X");
		EasyMock.expect(importParameters.getStructure("IS_LOGIC_SWITCH")).andReturn(structureLogicSwitch).times(2);

		EasyMock.expect(function.getImportParameterList()).andReturn(importParameters);

		connection = EasyMock.createMock(JCoConnection.class);
		EasyMock.expect(connection.getFunction(ConstantsR3Lrd.FM_LO_API_WEC_ORDER_GET)).andReturn(function);
		EasyMock.replay(function, importParameters, connection, structureLogicSwitch);

		classUnderTest.setGenericFactory(getGenericFactory());

		LocaleUtil.setLocale(Locale.US);
	}







	@Test
	public void testHandleTtHeadDocFlowUnknownDocType()
	{
		final Header head = (Header) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER);
		classUnderTest.handleTtHeadDocFlow(ttHeadDocFlowUnknownDocType, head);
		assertEquals(0, head.getSuccessorList().size());
	}

	@Test
	public void testHandleTtHeadDocFlowOrder()
	{
		final Header head = (Header) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER);
		classUnderTest.handleTtHeadDocFlow(ttHeadDocFlowOrder, head);
		assertEquals(2, head.getSuccessorList().size());
		final ConnectedDocument succOrder = head.getSuccessorList().get(0);
		assertEquals(DocumentType.ORDER, succOrder.getDocType());
		assertEquals(ConnectedDocument.ORDER, succOrder.getAppTyp());
	}

	@Test
	public void testHandleTtHeadDocFlowOrderDelivery()
	{
		final Header head = (Header) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER);
		classUnderTest.handleTtHeadDocFlow(ttHeadDocFlowOrder, head);
		assertEquals(2, head.getSuccessorList().size());
		final ConnectedDocument delivery = head.getSuccessorList().get(1);
		assertEquals(DocumentType.DELIVERY, delivery.getDocType());
		assertEquals(ConnectedDocument.DLVY, delivery.getAppTyp());
	}



	@Test
	public void testHandleTtHeadDocFlowOrderInvoice()
	{
		final Header head = (Header) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER);
		classUnderTest.handleTtHeadDocFlow(ttHeadDocFlowOrder, head);
		assertEquals(1, head.getPredecessorList().size());
		final ConnectedDocument invoice = head.getPredecessorList().get(0);
		assertEquals(DocumentType.INVOICE, invoice.getDocType());
		assertEquals(ConnectedDocument.BILL, invoice.getAppTyp());
	}

	@Test
	public void testBuildDefaultItemObjectRequestParameters()
	{
		itItemObjreq.clear();
		classUnderTest.buildDefaultItemObjectRequestParameters(function);

		//We expect 4 entries for item request objects		
		assertEquals(itItemObjreq.getNumRows(), 4);
	}

	@Test
	public void testBuildDefaultItemObjectRequestParametersSchedLine()
	{
		classUnderTest.buildDefaultItemObjectRequestParameters(function);

		assertTrue(findRequestObject("SLINE"));
	}

	@Test
	public void testBuildExtensibilityFieldsRequestParametersNoCustExit()
	{
		classUnderTest.setCustExit(null);
		//execution should be possible w/o exception
		classUnderTest.buildExtensibilityFieldsRequestParameters(importParameters);
	}

	@Test
	public void testBuildExtensibilityFieldsRequestParameters()
	{
		classUnderTest.setCustExit(new ERPLO_APICustomerExitsImpl());
		classUnderTest.buildExtensibilityFieldsRequestParameters(importParameters);
	}

	@Test
	public void testBuildSavedItemList()
	{
		final Set<String> savedItemsMap = new HashSet<>();
		final Map<String, Item> itemsERPState = new HashMap<>();
		classUnderTest.buildSavedItemsList(savedItemsMap, itemsERPState, itemKeyTable);
		assertEquals(2, savedItemsMap.size());
		assertTrue(savedItemsMap.contains("A"));
	}

	@Test
	public void testBuildSavedItemListWithExistingItems()
	{
		final Set<String> savedItemsMap = new HashSet<>();
		final Map<String, Item> itemsERPState = new HashMap<>();
		final Item existingItem = new ItemSalesDoc();
		final String handleExistingItem = "C";
		itemsERPState.put(handleExistingItem, existingItem);
		existingItem.setHandle(handleExistingItem);
		classUnderTest.buildSavedItemsList(savedItemsMap, itemsERPState, itemKeyTable);
		assertEquals(3, savedItemsMap.size());
		assertTrue(savedItemsMap.contains(handleExistingItem));
	}

	@Test
	public void testBuildSavedItemListNoKeyTable()
	{
		final Set<String> savedItemsMap = new HashSet<>();
		final Map<String, Item> itemsERPState = new HashMap<>();
		//no exception then
		classUnderTest.buildSavedItemsList(savedItemsMap, itemsERPState, null);
	}

	@Test
	public void testCreateItemCopy()
	{
		final Item existingItem = new ItemSalesDoc();
		final Text existingText = new TextImpl();
		final String textMessage = "Hello";
		existingText.setText(textMessage);
		existingItem.setText(existingText);
		final Item itemCopy = classUnderTest.createItemCopy(existingItem);
		existingText.setText("Change");
		assertFalse(existingItem.equals(itemCopy));
		assertEquals(itemCopy.getText().getText(), textMessage);
	}


	public void test() throws BackendException
	{
		final BackendState backendState = new BasketERP();
		final SalesDocument salesDocument = new BasketImpl();
		final Header header = new HeaderSalesDocument();
		salesDocument.setHeader(header);
		salesDocument.setTechKey(new TechKey("A"));
		final ItemBuffer itemBuffer = new ItemBufferImpl();
		final GetAllReadParameters readParams = new GetAllReadParameters();

		classUnderTest.execute(backendState, salesDocument, itemBuffer, readParams, connection);
	}

	@Test
	public void testFillTextMapperAttributes()
	{
		final TransactionConfiguration shop = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);
		final String id = "0001";
		shop.setItemTextID(id);
		shop.setHeaderTextID(id);
		final ItemTextMapper itemTextMapper = new ItemTextMapper();
		final HeadTextMapper headerTextMapper = new HeadTextMapper();
		classUnderTest.fillTextMapperAttributes(shop, itemTextMapper, headerTextMapper);
		assertEquals(id, itemTextMapper.configTextId);
		assertEquals(id, headerTextMapper.configTextId);
	}

	@Test
	public void testInitializeMapper()
	{
		classUnderTest.initializeMapper();
		assertNotNull(classUnderTest.shop);
		assertNotNull(classUnderTest.headerTextMapper);
		assertNotNull(classUnderTest.headMapper);
		assertNotNull(classUnderTest.itemMapper);
		assertNotNull(classUnderTest.itemTextMapper);
		assertNotNull(classUnderTest.partnerMapper);
	}

	@Test
	public void testFillImportParameters()
	{
		final BackendState backendState = new BasketERP();
		final ItemBuffer itemBuffer = new ItemBufferImpl();
		prepareItemBuffer(itemBuffer);
		//in this case we expect a read, item buffer must be cleared
		classUnderTest.fillImportParameters(backendState, itemBuffer, importParameters, vbeln);
		assertTrue(itemBuffer.getItemsERPState().size() == 0);
	}



	@Test
	public void testFillImportParametersNotPersisted()
	{
		final BackendState backendState = new BasketERP();
		final ItemBuffer itemBuffer = new ItemBufferImpl();
		prepareItemBuffer(itemBuffer);
		//in this case we expect a read, item buffer must be kept
		classUnderTest.fillImportParameters(backendState, itemBuffer, importParameters, vbelnNotPersisted);
		assertTrue(itemBuffer.getItemsERPState().size() == 1);
	}

	@Test
	public void testIncompletionLogMapper()
	{
		assertNotNull(classUnderTest.getIncompletionMapper());
	}




	void prepareItemBuffer(final ItemBuffer itemBuffer)
	{
		final Map<String, Item> itemsERPState = new HashMap<>();
		itemsERPState.put("key", new ItemSalesDoc());
		itemBuffer.setItemsERPState(itemsERPState);
	}

	/**
	 * @param string
	 */
	private boolean findRequestObject(final String string)
	{
		itItemObjreq.firstRow();
		while (itItemObjreq.nextRow())
		{
			final String itemObject = itItemObjreq.getString("OBJECT");
			System.out.println("Current item request object: " + itemObject);
			if (itemObject.equals(string))
			{
				return true;
			}
		}
		return false;

	}


	@Override
	public void setUp()
	{
		if (itemKeyTable == null)
		{
			readTablesFromRepository();
		}
		final String key = "A";
		posnr = "000010";
		item = new ItemSalesDoc();
		item.setTechKey(new TechKey(key));
		item.setNumberInt(10);

		itemKey = new HashMap<String, String>();
		itemKey.put(posnr, key);
		itemMap = new HashMap<String, Item>();
		itemMap.put(key, item);
	}



	private void readTablesFromRepository()
	{

		try
		{
			final JCoMockRepository testRepository = getJCORepository("jcoReposGetAllStrategy");

			itemKeyTable = testRepository.getTable("TT_ITEM_KEY");
			testRepository.getTable("TT_ITEM_COMR");
			testRepository.getTable("TT_ITEM_COMV");
			ttHeadDocFlowUnknownDocType = testRepository.getTable("TDT_RFC_HDFLOW");
			ttHeadDocFlowOrder = testRepository.getTable("TDT_RFC_HDFLOW_ORDER");
			testRepository.getTable("TT_IDFLOW");
			testRepository.getTable("TT_ITEM_COMR3");
			itItemObjreq = testRepository.getTable("IT_ITEM_REQ");

		}
		catch (final JCoRecException e)
		{
			throw new ApplicationBaseRuntimeException("Problem with reading data from XML repository", e);
		}

	}

}
