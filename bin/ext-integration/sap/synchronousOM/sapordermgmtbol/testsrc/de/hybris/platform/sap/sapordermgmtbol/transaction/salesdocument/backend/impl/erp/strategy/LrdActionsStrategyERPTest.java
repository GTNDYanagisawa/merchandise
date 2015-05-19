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
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.util.LocaleUtil;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.AbapBackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.backend.impl.erp.BasketERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl.TransactionConfigurationImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.BackendExceptionECOERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.ProcessTypeConverter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdActionsStrategy;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.JCORecTestBase;

import java.util.Locale;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;



@UnitTest
@SuppressWarnings("javadoc")
public class LrdActionsStrategyERPTest extends JCORecTestBase
{


	private LrdActionsStrategyERP classUnderTest;

	private JCoStructure is_logic_switch = null;
	private JCoTable itAction = null;
	private JCoTable etMessages;
	private JCoStructure esError;
	private JCoStructure isHeadComv;
	private JCoStructure isHeadComx;


	final TechKey itemKey = new TechKey("A");
	final TechKey[] itemsToDelete = new TechKey[]
	{ itemKey };
	private final TransactionConfiguration shop = new TransactionConfigurationImpl();
	private final SalesDocument salesDoc = new BasketImpl();
	private JCoConnection cn;
	private final String objectName = LrdActionsStrategy.ITEMS;
	private JCoFunction function;
	private JCoParameterList importParams;
	private JCoParameterList exportParams;
	private JCoFunction functionLoad;

	private JCoParameterList importParamsLoad;

	private JCoParameterList exportParamsLoad;

	private JCoFunction functionConvert;

	private JCoParameterList importParamsConvert;

	private JCoParameterList exportParamsConvert;

	private JCoStructure esHeadComv;

	private JCoStructure esHeadComr;



	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest#setUp()
	 */
	@Override
	@Before
	public void setUp()
	{
		classUnderTest = (LrdActionsStrategyERP) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ACTIONS_STRATEGY);
		final JCoMockRepository testRepository = getJCORepository("jcoReposActionsStrategy");
		try
		{
			is_logic_switch = testRepository.getStructure("IS_LOGIC_SWITCH");
			itAction = testRepository.getTable("IT_ACTION");
			itAction.deleteAllRows();
			etMessages = testRepository.getTable("ET_MESSAGES");
			esError = testRepository.getStructure("ES_ERROR");
			isHeadComv = testRepository.getStructure("IS_HEAD_COMV");
			isHeadComx = testRepository.getStructure("IS_HEAD_COMX");
			esHeadComr = testRepository.getStructure("ES_HEAD_COMR");
			esHeadComv = testRepository.getStructure("ES_HEAD_COMV");
		}
		catch (final JCoRecException e)
		{
			e.printStackTrace();
		}

		classUnderTest.setCustExit((ERPLO_APICustomerExits) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BE_CUST_EXIT));

		final Header header = new HeaderSalesDocument();
		header.setHandle("A");
		salesDoc.setHeader(header);
	}


	void mockJCOCall(final boolean shouldThrowWhenErrorException, final boolean shouldThrowWhenDisplayException)
	{
		cn = EasyMock.createMock(JCoConnection.class);
		function = EasyMock.createMock(JCoFunction.class);
		importParams = EasyMock.createMock(JCoParameterList.class);
		exportParams = EasyMock.createMock(JCoParameterList.class);
		functionLoad = EasyMock.createMock(JCoFunction.class);
		importParamsLoad = EasyMock.createMock(JCoParameterList.class);
		exportParamsLoad = EasyMock.createMock(JCoParameterList.class);
		functionConvert = EasyMock.createMock(JCoFunction.class);
		importParamsConvert = EasyMock.createMock(JCoParameterList.class);
		exportParamsConvert = EasyMock.createMock(JCoParameterList.class);
		try
		{
			EasyMock.expect(cn.getFunction(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS)).andReturn(function);
			EasyMock.expect(cn.getFunction(ConstantsR3Lrd.FM_LO_API_LOAD)).andReturn(functionLoad);
			EasyMock.expect(cn.getFunction(ProcessTypeConverter.FM_FOR_CONVERSION)).andReturn(functionConvert);
			cn.execute(functionLoad);
			cn.execute(functionConvert);
			cn.execute(function);
			if (shouldThrowWhenErrorException)
			{
				final Throwable beEx = new BackendException("Ex", new AbapBackendException(
						LrdActionsStrategyERP.EXC_NO_ACTION_WHEN_ERROR));
				EasyMock.expectLastCall().andThrow(beEx);
			}
			if (shouldThrowWhenDisplayException)
			{
				final Throwable beEx = new BackendException("Ex", new AbapBackendException(
						LrdActionsStrategyERP.EXC_NO_ACTION_WHEN_DISPLAY));
				EasyMock.expectLastCall().andThrow(beEx);
			}
			EasyMock.expect(function.getImportParameterList()).andReturn(importParams);
			EasyMock.expect(importParams.getTable("IT_ACTION")).andReturn(itAction);
			EasyMock.expect(exportParams.getTable("ET_MESSAGES")).andReturn(etMessages);
			EasyMock.expect(exportParams.getStructure("ES_ERROR")).andReturn(esError);
			EasyMock.expect(function.getExportParameterList()).andReturn(exportParams);
			EasyMock.expect(functionLoad.getImportParameterList()).andReturn(importParamsLoad);
			EasyMock.expect(functionLoad.getExportParameterList()).andReturn(exportParamsLoad);
			EasyMock.expect(importParamsLoad.getStructure("IS_HEAD_COMV")).andReturn(isHeadComv);
			EasyMock.expect(importParamsLoad.getStructure("IS_HEAD_COMX")).andReturn(isHeadComx);
			EasyMock.expect(importParamsLoad.getStructure("IS_LOGIC_SWITCH")).andReturn(is_logic_switch);
			importParamsLoad.setValue("IV_SCENARIO_ID", ConstantsR3Lrd.scenario_LO_API_WEC);
			importParamsLoad.setValue("IV_TRTYP", "A");
			importParamsLoad.setValue("IV_VBELN", "");
			EasyMock.expect(functionConvert.getImportParameterList()).andReturn(importParamsConvert);
			EasyMock.expect(functionConvert.getExportParameterList()).andReturn(exportParamsConvert);
			importParamsConvert.setValue(ProcessTypeConverter.INPUT_PARAMETER_PROCESSTYPE, "TA");
			importParamsConvert.setValue(ProcessTypeConverter.INPUT_PARAMETER_LANGUAGE, "E");
			EasyMock.expect(exportParamsConvert.getString(ProcessTypeConverter.EXPORT_PARAMETER_PROCESSTYPE)).andReturn("TA");
			EasyMock.expect(exportParamsLoad.getTable("ET_MESSAGES")).andReturn(etMessages);
			EasyMock.expect(exportParamsLoad.getStructure("ES_ERROR")).andReturn(esError);
			EasyMock.expect(exportParamsLoad.getStructure("ES_HEAD_COMV")).andReturn(esHeadComv).anyTimes();
			EasyMock.expect(exportParamsLoad.getStructure("ES_HEAD_COMR")).andReturn(esHeadComr);

		}
		catch (final BackendException e)
		{

			e.printStackTrace();
		}
		EasyMock.replay(cn, function, importParams, exportParams, functionLoad, importParamsLoad, exportParamsLoad,
				functionConvert, importParamsConvert, exportParamsConvert);
		LocaleUtil.setLocale(Locale.US);
	}

	@Test
	public void testBeanAvailable()
	{
		assertNotNull(classUnderTest);
	}



	@Test
	public void testFillControlAttributes()
	{
		classUnderTest.fillControlAttributes(is_logic_switch);
		assertEquals("X", is_logic_switch.getString(LrdActionsStrategyERP.FIELD_NO_MESSAGES_DOC));
		assertEquals("X", is_logic_switch.getString(LrdActionsStrategyERP.FIELD_NO_CONVERSION));
	}

	@Test
	public void checkAttributesLrdLoadOk() throws BackendExceptionECOERP
	{
		classUnderTest = (LrdActionsStrategyERP) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ACTIONS_STRATEGY);
		final SalesDocument salesDoc = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BO_CART);
		final PartnerList partnerList = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST);
		final PartnerListEntry partnerListEntry = genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_LIST_ENTRY);
		partnerListEntry.setPartnerId("4711");
		partnerListEntry.setPartnerTechKey(new TechKey("4711"));
		partnerList.setSoldToData(partnerListEntry);

		salesDoc.getHeader().setPartnerList(partnerList);
		classUnderTest.checkAttributesLrdLoad(salesDoc);
	}

	@Test
	public void testDeleteItems()
	{
		LrdActionsStrategyERP.deleteItems(itAction, itemsToDelete);
		assertEquals(1, itAction.getNumRows());
	}

	@Test
	public void testDeleteItemsNull()
	{
		LrdActionsStrategyERP.deleteItems(itAction, null);
		assertEquals(0, itAction.getNumRows());
	}

	@Test
	public void testDelete() throws BackendException
	{
		mockJCOCall(false, false);
		classUnderTest.executeLrdDoActionsDelete(shop, salesDoc, cn, objectName, itemsToDelete);
		//We received an error from (mock) backend
		assertEquals(1, salesDoc.getMessageList().size());
	}

	@Test
	public void testDeleteBackendEx() throws BackendException
	{
		mockJCOCall(true, false);
		classUnderTest.executeLrdDoActionsDelete(shop, salesDoc, cn, objectName, itemsToDelete);
		//We received an exception from backend which was known, so it was translated into a message
		assertEquals(1, salesDoc.getMessageList().size());
	}

	@Test
	public void testDeleteBackendExDisplay() throws BackendException
	{
		mockJCOCall(false, true);
		classUnderTest.executeLrdDoActionsDelete(shop, salesDoc, cn, objectName, itemsToDelete);
		//We received an exception from backend which was known, so it was translated into a message
		assertEquals(1, salesDoc.getMessageList().size());
	}

	@Test
	public void testPricing() throws BackendException
	{
		mockJCOCall(false, false);
		classUnderTest.executeLrdDoActionsDocumentPricing(salesDoc, cn, shop);
		//We received an error from (mock) backend
		assertEquals(1, salesDoc.getMessageList().size());
	}

	@Test
	public void testLoad() throws BackendException
	{
		mockJCOCall(false, false);
		final BackendState erpDocument = new BasketERP();
		final LoadOperation loadState = new LoadOperation();
		loadState.setLoadOperation(LoadOperation.display);
		classUnderTest.executeLrdLoad(salesDoc, erpDocument, cn, loadState);
		//We received an error from (mock) backend
		assertEquals(1, salesDoc.getMessageList().size());
	}


}
