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


import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.PartnerBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.BackendExceptionECOERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LrdFieldExtension;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.ProcessTypeConverter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdActionsStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sap.hybris.integration.models.constants.SapmodelConstants;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * ERP implementation of LrdActionsStrategy and ConstantsR3Lrd.
 * 
 * @see LrdActionsStrategy
 * @see ConstantsR3Lrd
 * @version 1.0
 */
public class LrdActionsStrategyERP extends BaseStrategyERP implements LrdActionsStrategy, ConstantsR3Lrd
{

	private static final String ACTION_PARAM_NEW_FREIGHT_DETERMINATION = "H";

	private static final String ACTION_ID_PRICING = "PRICING";
	/**
	 * In case setting to 'X': Deactivating of switch for check of open documents (contracts, quotations): no error
	 * message should be returned when a open contract or quotation exists for the entered product of the order
	 */
	protected static String FIELD_NO_MESSAGES_DOC = "NO_MESSAGES_DOC";
	/**
	 * LO-API should not trigger ERP conversion exits
	 */
	protected static final String FIELD_NO_CONVERSION = "NO_CONVERSION";

	/**
	 * constant naming the key referenced in factory-config.xml where the strategy class is specified
	 */
	public static final String STRATEGY_FACTORY_ERP = "STStrategyFactoryERP";

	private long rfctime = 0;

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(LrdActionsStrategyERP.class.getName());

	/**
	 * Field list which fields will be checked in create or change mode
	 */
	protected final ArrayList<SetActiveFieldsListEntry> activeFieldsListCreateChange = new ArrayList<SetActiveFieldsListEntry>();

	static final String EXC_NO_ACTION_WHEN_ERROR = "EXC_NO_ACTION_WHEN_ERROR";

	static final String EXC_NO_ACTION_WHEN_DISPLAY = "EXC_NO_ACTION_WHEN_DISPLAY";


	/**
	 * The condition type which should be used to determine the freight value
	 */
	protected String headerCondTypeFreight = "";

	/**
	 * The subtotal for the item freight value
	 */
	protected String subTotalItemFreight = "";

	/**
	 * Allows access to configuration settings
	 */
	protected ModuleConfigurationAccess moduleConfigurationAccess;

	/**
	 * @param moduleConfigurationAccess
	 *           Allows access to configuration settings
	 */
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	/**
	 * Standard constructor. <br>
	 */
	public LrdActionsStrategyERP()
	{
		super();
		// Fill the active Fields list only once
		setActiveFieldsListCreateChange(activeFieldsListCreateChange);
	}

	@Override
	public ReturnValue executeLrdDoActionsDelete(final TransactionConfiguration shop, final SalesDocument salesDoc,
			final JCoConnection cn, final String objectName, final TechKey[] itemsToDelete) throws BackendException
	{

		final String METHOD_NAME = "executeLrdDoActionsDelete()";
		sapLogger.entering(METHOD_NAME);

		ReturnValue retVal = new ReturnValue(ConstantsR3Lrd.BAPI_RETURN_ERROR);

		try
		{
			final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS);

			// getting import parameter
			final JCoParameterList importParams = function.getImportParameterList();

			// getting export parameters
			final JCoParameterList exportParams = function.getExportParameterList();
			final JCoTable itAction = importParams.getTable("IT_ACTION");

			// check, if items should be deleted:
			if (objectName.equals(LrdActionsStrategy.ITEMS))
			{
				deleteItems(itAction, itemsToDelete);
			}

			// if there is something to delete
			if (itAction.getNumRows() > 0)
			{
				final ERPLO_APICustomerExits custExit = getCustExit();
				if (custExit != null)
				{
					custExit.customerExitBeforeLoad(salesDoc, function, cn, sapLogger);
				}

				cn.execute(function);

				if (custExit != null)
				{
					custExit.customerExitAfterLoad(salesDoc, function, cn, sapLogger);
				}
			}

			if (sapLogger.isDebugEnabled())
			{
				logCall(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS, importParams, null);
			}

			// error handling
			final JCoTable etMessages = exportParams.getTable("ET_MESSAGES");
			final JCoStructure esError = exportParams.getStructure("ES_ERROR");
			dispatchMessages(salesDoc, etMessages, esError);

			if (!(esError != null && esError.getString("ERRKZ").equals("X")))
			{
				retVal = new ReturnValue(ConstantsR3Lrd.BAPI_RETURN_INFO);
			}

		}
		catch (final BackendException ex)
		{
			final String abapExc = ex.getCause().getMessage();
			String resourceKey = "";
			if (abapExc.equals(EXC_NO_ACTION_WHEN_ERROR))
			{
				resourceKey = "sapsalestransactions.bo.sales.erp.actions.error";
			}
			else if (abapExc.equals(EXC_NO_ACTION_WHEN_DISPLAY))
			{
				resourceKey = "sapsalestransactions.bo.sales.erp.actions.display";
			}
			else
			{
				throw ex;
			}
			final Message message = new Message(Message.ERROR, resourceKey);
			salesDoc.addMessage(message);
		}
		finally
		{
			sapLogger.exiting();
		}

		sapLogger.exiting();
		return retVal;
	}

	/**
	 * Registers the items for deletion. This is done by generating entries in the <code>itAction</code> table.
	 * 
	 * @param itAction
	 *           the JCoTable, that is filled with the items to be deleted.
	 * @param itemsToDelete
	 *           the <code>TechKey</code>s of the items to be deleted.
	 */
	protected static void deleteItems(final JCoTable itAction, final TechKey[] itemsToDelete)
	{

		if (itemsToDelete == null)
		{
			return;
		}

		for (int i = 0; i < itemsToDelete.length; i++)
		{

			itAction.appendRow();
			itAction.setValue("HANDLE", itemsToDelete[i].getIdAsString());
			itAction.setValue("ACTION", "DELETE");

		} // for

	} // deleteItems

	@Override
	public BackendCallResult executeLrdSave(final SalesDocument posd, final boolean commit, final JCoConnection cn)
			throws BackendException
	{
		final String METHOD_NAME = "executeLrdSave()";
		sapLogger.entering(METHOD_NAME);

		BackendCallResult retVal = new BackendCallResult();

		final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_SAVE);

		// getting import parameter
		final JCoParameterList importParams = function.getImportParameterList();

		// getting export parameters
		final JCoParameterList exportParams = function.getExportParameterList();

		if (!commit)
		{
			JCoHelper.setValue(importParams, "X", "IF_NO_COMMIT");
		}
		else
		{
			// Makes only sence after a commit
			JCoHelper.setValue(importParams, "X", "IF_SYNCHRON");
		}
		final ERPLO_APICustomerExits custExit = getCustExit();
		if (custExit != null)
		{
			custExit.customerExitBeforeSave(commit, function, cn, sapLogger);
		}
		cn.execute(function);
		if (custExit != null)
		{
			custExit.customerExitAfterSave(commit, function, cn, sapLogger);
		}
		if (sapLogger.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder(60);
			debugOutput.append("Result of ERP_LORD_SAVE: ");
			debugOutput.append("\n EF_SAVED      : " + exportParams.getString("EF_SAVED"));
			debugOutput.append("\n EV_VBELN_SAVED: " + exportParams.getString("EV_VBELN_SAVED"));
			sapLogger.debug(debugOutput);
		}

		if ((!(exportParams.getString("EF_SAVED").isEmpty())) && (!exportParams.getString("EV_VBELN_SAVED").trim().isEmpty()))
		{
			// Only set Techkey in case it is really new - when creating a
			// document,
			// otherwise several issues will occur:
			// - document is read multiple time afterwards
			// -exceptions can occur, see int.msg. 4340020 /2009
			if (posd.getHeader().getSalesDocNumber() == null || posd.getHeader().getSalesDocNumber().equals(""))
			{
				posd.setTechKey(JCoHelper.getTechKey(exportParams, "EV_VBELN_SAVED"));
				posd.getHeader().setTechKey(JCoHelper.getTechKey(exportParams, "EV_VBELN_SAVED"));
				posd.getHeader().setSalesDocNumber(ConversionTools.cutOffZeros(JCoHelper.getString(exportParams, "EV_VBELN_SAVED")));
			}
			sapLogger.debug("Transaction with ID" + exportParams.getString("EV_VBELN_SAVED") + "was saved successfully");

			// Now we need to tell the BO layer and UI that the order
			// is not in editing anymore!
			posd.getHeader().setChangeable(false);

		}
		// No data was changed
		else if (hasMessage("W", "V1", "041", ConstantsR3Lrd.MESSAGE_IGNORE_VARS, "", "", "", exportParams.getTable("ET_MESSAGES"),
				null))
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("No data changed");
			}
			final TechKey techKey = new TechKey(posd.getHeader().getSalesDocNumber());
			posd.setTechKey(techKey);
			posd.getHeader().setTechKey(techKey);
		}
		else
		{
			retVal = new BackendCallResult(BackendCallResult.Result.failure);
			sapLogger.debug("Transaction could not be saved.");
		}

		if (sapLogger.isDebugEnabled())
		{
			logCall(ConstantsR3Lrd.FM_LO_API_SAVE, importParams, null);
		}

		dispatchMessages(posd, exportParams.getTable("ET_MESSAGES"), exportParams.getStructure("ES_ERROR"));

		sapLogger.exiting();
		return retVal;
	}

	/**
	 * Checks if following attributes have been provided: process type, soldTo, sales organisation, distribution channel,
	 * division (Mandatory fields for LOAD-call).
	 * 
	 * @param posd
	 * @throws BackendExceptionECOERP
	 */
	protected void checkAttributesLrdLoad(final SalesDocument posd) throws BackendExceptionECOERP
	{

		checkAttributeEmpty(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE),
				"Transaction Type");
		checkAttributeEmpty(posd.getHeader().getPartnerKey(PartnerFunctionData.SOLDTO), "SoldTo");
		checkAttributeEmpty(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG), "SalesOrg");
		checkAttributeEmpty(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL),
				"DistrChan");
		checkAttributeEmpty(moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION), "Division");

	}

	@Override
	public BackendCallResult executeLrdLoad(final SalesDocument posd, final BackendState erpDocument, final JCoConnection cn,
			final LoadOperation loadState) throws BackendException
	{

		final String METHOD_NAME = "executeLrdLoad()";

		sapLogger.entering(METHOD_NAME);

		final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_LOAD);

		// getting import parameter
		final JCoParameterList importParams = function.getImportParameterList();

		// header structure
		final JCoStructure headComv = importParams.getStructure("IS_HEAD_COMV");
		final JCoStructure headComx = importParams.getStructure("IS_HEAD_COMX");

		// setting the import table basket_item
		// JCoTable ItemRef = importParams.getTable("IT_ITEM_REF");

		// getting export parameters
		final JCoParameterList exportParams = function.getExportParameterList();

		// Structure for switches
		final JCoStructure logicSwitch = importParams.getStructure("IS_LOGIC_SWITCH");
		fillControlAttributes(logicSwitch);

		//Determine process type
		String processType = posd.getHeader().getProcessType();
		if (processType == null || processType.length() == 0)
		{
			processType = (String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_TRANSACTION_TYPE);
		}
		final ProcessTypeConverter ptc = new ProcessTypeConverter();
		final String convProcessType = ptc.convertProcessTypeToLanguageDependent(processType, cn);



		// Scenario
		importParams.setValue("IV_SCENARIO_ID", scenario_LO_API_WEC);

		if (loadState.getLoadOperation().equals(LoadOperation.display))
		{

			JCoHelper.setValue(importParams, LoadOperation.display, "IV_TRTYP");
			JCoHelper.setValue(importParams, posd.getHeader().getTechKey().toString(), "IV_VBELN");
		}
		if (loadState.getLoadOperation().equals(LoadOperation.create))
		{

			// we need to know that first update might need to do specific
			// shipto handling
			erpDocument.setDocumentInitial(true);

			// First check on essential attributes
			checkAttributesLrdLoad(posd);

			JCoHelper.setValue(importParams, LoadOperation.create, "IV_TRTYP");
			JCoHelper.setValue(importParams, convProcessType, "IV_AUART");
			JCoHelper.setValue(importParams, posd.getHeader().getPartnerKey(PartnerFunctionData.SOLDTO), "IV_KUNAG");

			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG), "IV_VKORG");
			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL),
					"IV_VTWEG");
			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION), "IV_SPART");


			headComv.setValue("BSTKD", posd.getHeader().getPurchaseOrderExt());
			headComx.setValue("BSTKD", "X");

			final Date reqDelvDate = posd.getHeader().getReqDeliveryDate();
			if (reqDelvDate != null)
			{
				// String reqDelDate =
				// Conversion.dateToISADateString(reqDelvDate);
				headComv.setValue("VDATU", reqDelvDate);
				headComx.setValue("VDATU", "X");
			}

		}
		if (loadState.getLoadOperation().equals(LoadOperation.edit))
		{

			JCoHelper.setValue(importParams, LoadOperation.edit, "IV_TRTYP");
			JCoHelper.setValue(importParams, posd.getHeader().getTechKey().toString(), "IV_VBELN");
			JCoHelper.setValue(importParams, posd.getHeader().getPartnerKey(PartnerFunctionData.SOLDTO), "IV_KUNAG");

			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_SALES_ORG), "IV_VKORG");

			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DISTRIBUTION_CHANNEL),
					"IV_VTWEG");

			JCoHelper.setValue(importParams,
					(String) moduleConfigurationAccess.getProperty(SapmodelConstants.CONFIGURATION_PROPERTY_DIVISION), "IV_SPART");

		}
		final ERPLO_APICustomerExits custExit = getCustExit();
		if (custExit != null)
		{
			custExit.customerExitBeforeLoad(posd, function, cn, sapLogger);
		}

		executeRfc(cn, function);

		if (custExit != null)
		{
			custExit.customerExitAfterLoad(posd, function, cn, sapLogger);
		}

		final JCoTable etMessages = exportParams.getTable("ET_MESSAGES");
		final JCoStructure esError = exportParams.getStructure("ES_ERROR");

		if (loadState.getLoadOperation().equals(LoadOperation.create))
		{
			final JCoStructure headerV = exportParams.getStructure("ES_HEAD_COMV");
			posd.setTechKey(JCoHelper.getTechKey(headerV, "HANDLE"));
		}

		final JCoStructure headerComV = exportParams.getStructure("ES_HEAD_COMV");
		posd.getHeader().setHandle(JCoHelper.getString(headerComV, "HANDLE"));

		if (sapLogger.isDebugEnabled())
		{
			logCall(ConstantsR3Lrd.FM_LO_API_LOAD, importParams, null);
			logCall(ConstantsR3Lrd.FM_LO_API_LOAD, null, exportParams.getStructure("ES_HEAD_COMV"));
			logCall(ConstantsR3Lrd.FM_LO_API_LOAD, null, exportParams.getStructure("ES_HEAD_COMR"));
		}

		// error during load cannot be corrected
		// this we need to raise as an exception
		final String messageType = esError.getString("MSGTY");

		if ("E".equals(messageType) || "A".equals(messageType))
		{

			if (loadState.getLoadOperation().equals(LoadOperation.create))
			{
				if (!isRecoverableHeaderError(esError))
				{
					logErrorMessageAndThrowException(esError);

					posd.setInitialized(false);
				}
			}
			else if (loadState.getLoadOperation().equals(LoadOperation.edit))
			{
				loadState.setLoadOperation(LoadOperation.display);
			}
		}

		dispatchMessages(posd, etMessages, esError);

		if (posd.isInitialized())
		{
			readAlternativePartners(posd, function.getTableParameterList().getTable("ET_ALTERNATIVE_PARTNERS"));
		}

		if ("".equals(JCoHelper.getString(exportParams.getStructure("ES_HEAD_COMV"), "HANDLE")))
		{
			sapLogger.debug("No handle");
		}

		BackendCallResult result = null;
		if (!esError.getString("ERRKZ").isEmpty())
		{
			result = new BackendCallResult(BackendCallResult.Result.failure);
			sapLogger.debug("Error in " + METHOD_NAME + ": " + esError);
		}
		else
		{
			result = new BackendCallResult();
		}

		sapLogger.exiting();
		return result;
	}



	private void readAlternativePartners(final SalesDocument salesDocument, final JCoTable alternativePartnerTable)
	{

		final List<ShipTo> shipToList = salesDocument.getAlternativeShipTos();
		final List<BillTo> billToList = salesDocument.getAlternativeBillTos();
		shipToList.clear();
		billToList.clear();

		final int numRows = alternativePartnerTable.getNumRows();

		for (int i = 0; i < numRows; i++)
		{
			alternativePartnerTable.setRow(i);
			PartnerBase partner = null;
			if (alternativePartnerTable.getString("PARVW").equals("WE"))
			{
				partner = salesDocument.createShipTo();
				shipToList.add((ShipTo) partner);
			}
			if (alternativePartnerTable.getString("PARVW").equals("RE"))
			{
				partner = salesDocument.createBillTo();
				billToList.add((BillTo) partner);
			}

			partner.setId(alternativePartnerTable.getString("KUNNR"));

			final Address address = (Address) genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_ADDRESS);

			address.setCompanyName(alternativePartnerTable.getString("NAME"));
			address.setName1(alternativePartnerTable.getString("NAME"));
			address.setName2(alternativePartnerTable.getString("NAME_2"));
			address.setCity(alternativePartnerTable.getString("CITY"));
			address.setStreet(alternativePartnerTable.getString("STREET"));
			address.setPostlCod1(alternativePartnerTable.getString("POSTL_COD1"));
			address.setHouseNo(alternativePartnerTable.getString("HOUSE_NO"));
			address.setCountry(alternativePartnerTable.getString("COUNTRY"));
			address.setRegion(alternativePartnerTable.getString("REGION"));
			address.setAddressString_C(alternativePartnerTable.getString("ADDRESS_SHORT_FORM_S"));
			address.setAddressPartner(alternativePartnerTable.getString("KUNNR"));
			address.setTel1Numbr(alternativePartnerTable.getString("TEL1_NUMBR"));
			address.setTel1Ext(alternativePartnerTable.getString("TEL1_EXT"));
			address.setFaxNumber(alternativePartnerTable.getString("FAX_NUMBER"));
			address.setFaxExtens(alternativePartnerTable.getString("FAX_EXTENS"));
			address.setAddrnum(alternativePartnerTable.getString("KUNNR")); // As
			// we
			// don't
			// have
			// a
			// native
			// address
			// number

			partner.setShortAddress(alternativePartnerTable.getString("ADDRESS_SHORT_FORM_S"));
			address.clear_X();
			partner.setAddress(address);

		}

	}

	@Override
	public ReturnValue executeSetActiveFields(final JCoConnection cn) throws BackendException
	{

		final String METHOD_NAME = "executeSetActiveFields()";
		sapLogger.entering(METHOD_NAME);

		ReturnValue retVal = null;

		final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_SET_ACTIVE_FIELDS);

		// getting import parameters
		final JCoParameterList importParams = function.getImportParameterList();
		final JCoTable itActiveFields = importParams.getTable("IT_ACTIVE_FIELD");

		final Iterator<SetActiveFieldsListEntry> aflIT = activeFieldsListCreateChange.iterator();
		while (aflIT.hasNext())
		{
			final SetActiveFieldsListEntry aflEntry = aflIT.next();

			// New row
			itActiveFields.appendRow();
			// Set values
			itActiveFields.setValue("OBJECT", aflEntry.getObjectName());
			itActiveFields.setValue("FIELD", aflEntry.getFieldName());
		}

		final ERPLO_APICustomerExits custExit = getCustExit();
		if (custExit != null)
		{
			custExit.customerExitBeforeSetActiveFields(function, cn, sapLogger);
		}
		cn.execute(function);
		if (custExit != null)
		{
			custExit.customerExitAfterSetActiveFields(function, cn, sapLogger);
		}

		retVal = new ReturnValue("000");

		sapLogger.exiting();
		return retVal;
	}

	/**
	 * Fill active fields list
	 * 
	 * @param activeFieldsListCreateChange
	 *           List of active fields which we setup with this call
	 */
	protected void setActiveFieldsListCreateChange(final ArrayList<SetActiveFieldsListEntry> activeFieldsListCreateChange)
	{
		// HEAD
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "BSTKD"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "VSBED"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "VDATU"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "INCO1"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "INCO2"));
		// activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD",
		// "KDGRP"));
		// activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD",
		// "KONDA"));
		// activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD",
		// "PLTYP"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "VKORG"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "VTWEG"));
		// activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD",
		// "ZTERM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "KUNAG"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "BSARK"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "LIFSK"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "WAERK"));

		// in case of no guestUserMode those fields are excluded at
		// executeSetActiveFields
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_STREET"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_HNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_PCODE"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_CITY"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_COUNTRY"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("HEAD", "CPD_LANGU_EXT"));

		final ERPLO_APICustomerExits custExit = getCustExit();

		// Header Extension Data
		if (custExit != null)
		{

			final Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields = custExit.customerExitGetExtensionFields();

			final LrdFieldExtension lrdFieldExtension = extensionFields.get(LrdFieldExtension.FieldType.HeadComV);

			if (lrdFieldExtension != null)
			{

				final List<String> fields = lrdFieldExtension.getFieldnames();

				if (fields != null)
				{
					for (final String field : fields)
					{
						activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(LrdFieldExtension.objectHead, field));
					}
				}
			}

		}

		// ITEM
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM", "MABNR"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM", "POSNR"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM", "KWMENG"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM", "ABGRU"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM", "EDATU"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM", "PSTYV"));

		// activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM",
		// "LPRIO"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("ITEM", "VRKME"));

		// Partner
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "KUNNR"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "PARVW"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "PCODE"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "CITY"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "CITY2"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "STREET"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "HNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "COUNTRY"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "REGION"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "NAME"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "NAME2"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "TELNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "TELEXT"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "FAXNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "FAXEXT"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "MOBNUM"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "EMAIL"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "TAXJURCODE"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "TITLE"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "LANGU_EXT"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "PBOX"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("PARTY", "PBOX_PCODE"));

		// Item Extension Data
		if (custExit != null)
		{

			final Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields = custExit.customerExitGetExtensionFields();

			final LrdFieldExtension lrdFieldExtension = extensionFields.get(LrdFieldExtension.FieldType.ItemComV);

			if (lrdFieldExtension != null)
			{
				final List<String> fields = lrdFieldExtension.getFieldnames();

				if (fields != null)
				{
					for (final String field : fields)
					{
						activeFieldsListCreateChange.add(new SetActiveFieldsListEntry(LrdFieldExtension.objectItem, field));
					}
				}
			}

		}

		// Text
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("TEXT", "TEXT_STRING"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("TEXT", "ID"));
		activeFieldsListCreateChange.add(new SetActiveFieldsListEntry("TEXT", "SPRAS_ISO"));

	}

	/**
	 * Wrapper for the remote function call. This can be used for performance measurement instrumentation, additional
	 * logging a.o. as well as for unit tests to get independent from the ERP backend
	 * 
	 * @param theConnection
	 *           JCO connection
	 * @param theFunction
	 *           JCO Function
	 * @throws BackendException
	 */
	protected void executeRfc(final JCoConnection theConnection, final JCoFunction theFunction) throws BackendException
	{

		sapLogger.entering("executeRfc");
		try
		{

			final long start = System.currentTimeMillis();
			theConnection.execute(theFunction);
			final long end = System.currentTimeMillis();

			final long millis = end - start;
			rfctime = rfctime + millis;
		}
		finally
		{
			sapLogger.exiting();
		}
	}

	@Override
	public void executeLrdDoActionsDocumentPricing(final SalesDocument salesDocument, final JCoConnection cn,
			final TransactionConfiguration transConf) throws BackendException
	{
		executeLrdDoActionsDocumentPricing(salesDocument, ACTION_PARAM_NEW_FREIGHT_DETERMINATION, cn, transConf);
	}

	@Override
	public void executeLrdDoActionsDocumentPricing(final SalesDocument salesDocument, final String pricingType,
			final JCoConnection cn, final TransactionConfiguration transConf) throws BackendException
	{

		final String METHOD_NAME = "executeLrdDoActionsDocumentPricing()";
		sapLogger.entering(METHOD_NAME);

		final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS);

		// getting import parameter
		final JCoParameterList importParams = function.getImportParameterList();

		// getting export parameters
		final JCoParameterList exportParams = function.getExportParameterList();
		final JCoTable itAction = importParams.getTable("IT_ACTION");
		itAction.appendRow();
		itAction.setValue("HANDLE", salesDocument.getHeader().getHandle());
		itAction.setValue("ACTION", ACTION_ID_PRICING);
		itAction.setValue("PARAM", pricingType);
		cn.execute(function);

		if (sapLogger.isDebugEnabled())
		{
			logCall(ConstantsR3Lrd.FM_LO_API_DO_ACTIONS, importParams, null);
		}

		// error handling
		final JCoTable etMessages = exportParams.getTable("ET_MESSAGES");
		final JCoStructure esError = exportParams.getStructure("ES_ERROR");
		dispatchMessages(salesDocument, etMessages, esError);

		sapLogger.exiting();
	}

	/**
	 * @param isLogicSwitch
	 */
	protected void fillControlAttributes(final JCoStructure isLogicSwitch)
	{
		// deactivating of switch for check of open documents
		// (contracts, quotations): no error message
		// should be returned when a open contract or quotation exists
		// for the entered product of the order
		isLogicSwitch.setValue(FIELD_NO_MESSAGES_DOC, "X");
		isLogicSwitch.setValue(FIELD_NO_CONVERSION, "X");

	}

}
