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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

import de.hybris.platform.sap.core.bol.backend.jco.JCoHelper;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionTools;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocumentItem;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp.OrderERP;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LrdFieldExtension;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ItemBuffer;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.GetAllStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdRequestObjectsHead;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.LrdRequestObjectsItem;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.GetAllReadParameters;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

/**
 * Strategy for function module ERP_LORD_GET_ALL.
 */
public abstract class GetAllStrategyERP extends BaseStrategyERP implements
		GetAllStrategy, ConstantsR3Lrd {

	TransactionConfiguration shop;
	HeaderMapper headMapper;
	ItemMapper itemMapper;
	PartnerMapper partnerMapper;
	ItemTextMapper itemTextMapper;
	HeadTextMapper headerTextMapper;
	IncompletionMapperImpl incompletionMapper;

	/**
	 * reference to SAP logging API
	 */
	public static Log4JWrapper sapLogger = Log4JWrapper
			.getInstance(GetAllStrategyERP.class.getName());

	/**
	 * Empty string
	 */
	protected static final String EMPTY_STRING = "";

	/**
	 * Attribute for LO-API: No conversion intended (conversion exits not called
	 * in SAP back end)
	 */
	protected static final String FIELD_NO_CONVERSION = "NO_CONVERSION";

	/**
	 * Fills the import parameter <code>IT_ITEM_OBJREQ</code> with default
	 * values for items.
	 * 
	 * @param function
	 *            JCO function
	 */
	protected void buildDefaultItemObjectRequestParameters(
			final JCoFunction function) {

		final JCoTable objReq = function.getImportParameterList().getTable(
				"IT_ITEM_OBJREQ");

		for (final LrdRequestObjectsItem itemRequestObject : LrdRequestObjectsItem
				.values()) {
			objReq.appendRow();
			objReq.setValue("OBJECT", itemRequestObject.name());
			objReq.setValue("COMV_REQUEST", itemRequestObject.getComv_request());
			objReq.setValue("COMR_REQUEST", itemRequestObject.getComr_request());
			objReq.setValue("DEF_REQUEST", itemRequestObject.getDef_request());
		}

	}

	/**
	 * @param importParameters
	 */
	protected void buildExtensibilityFieldsRequestParameters(
			final JCoParameterList importParameters) {

		final ERPLO_APICustomerExits custExit = getCustExit();

		if (custExit == null) {
			return;
		}

		final Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields = custExit
				.customerExitGetExtensionFields();

		// Fill Head ComV
		final JCoTable itHeadFieldReqComV = importParameters
				.getTable("IT_HEAD_FIELDREQ_COMV");
		fillExtensibilityTable(extensionFields, itHeadFieldReqComV,
				LrdFieldExtension.FieldType.HeadComV);

		// Fill Item ComR
		final JCoTable itHeadFieldReqComR = importParameters
				.getTable("IT_HEAD_FIELDREQ_COMR");
		fillExtensibilityTable(extensionFields, itHeadFieldReqComR,
				LrdFieldExtension.FieldType.HeadComR);

		// Fill Item ComV
		final JCoTable itItemFieldReqComV = importParameters
				.getTable("IT_ITEM_FIELDREQ_COMV");
		fillExtensibilityTable(extensionFields, itItemFieldReqComV,
				LrdFieldExtension.FieldType.ItemComV);

		// Fill Item ComR
		final JCoTable itItemFieldReqComR = importParameters
				.getTable("IT_ITEM_FIELDREQ_COMR");
		fillExtensibilityTable(extensionFields, itItemFieldReqComR,
				LrdFieldExtension.FieldType.ItemComR);

	}

	/**
	 * Build list of items that are already saved in the backend (order change).
	 * This method should be called when the order items are retrieved the
	 * document is read the first time, because it is not possible to
	 * distinguish between saved and new items.
	 * 
	 * @param savedItemsMap
	 *            list of saved items
	 * @param itemsERPState
	 *            ERP status of items which we buffered previously
	 * @param ttItemKey
	 *            list of item keys
	 */
	protected void buildSavedItemsList(final Set<String> savedItemsMap,
			final Map<String, Item> itemsERPState, final JCoTable ttItemKey) {

		if (ttItemKey == null) {
			return;
		}

		final int numLines = ttItemKey.getNumRows();

		// first deal with all items retrieved
		for (int i = 0; i < numLines; i++) {
			ttItemKey.setRow(i);
			final String handle = JCoHelper.getString(ttItemKey, FIELD_HANDLE);
			savedItemsMap.add(handle);
		}
		// and put also already buffered items into the result
		final Iterator<Item> existingItems = itemsERPState.values().iterator();
		while (existingItems.hasNext()) {
			savedItemsMap.add(existingItems.next().getHandle());
		}

	}

	/**
	 * Creates a deep copy of an item
	 * 
	 * @param currentItem
	 *            Source item
	 * @return Deep copy
	 */
	protected Item createItemCopy(final Item currentItem) {
		final Item item = (Item) currentItem.clone();
		return item;
	}

	/**
	 * Determine status from data read via LO-API
	 * 
	 * @param head
	 *            Cart or order header
	 * @param esHvStatComV
	 *            Header status info
	 * @param ttItemVstatComV
	 *            Item status info
	 * @param objInstMap
	 *            Map of LO-API objects (items)
	 * @param itemMap
	 *            Map of BOL items
	 */
	protected abstract void determineStatus(Header head,
			final JCoStructure esHvStatComV, final JCoTable ttItemVstatComV,
			final ObjectInstances objInstMap, Map<String, Item> itemMap);

	/**
	 * Strategy for ERP_LORD_GET_ALL. Reads all relevant data for header and
	 * item.
	 * 
	 * @param cn
	 *            JCO connection to use
	 * @return Object containing messages of call and (if present) the return
	 *         code generated by the function module.
	 */

	@Override
	public BackendCallResult execute(final BackendState backendState,
			final SalesDocument salesDocument, final ItemBuffer itemBuffer,
			final GetAllReadParameters readParams, final JCoConnection cn)
			throws BackendException {

		final String METHOD_NAME = "execute()";
		sapLogger.entering(METHOD_NAME);

		initializeMapper();

		fillTextMapperAttributes(shop, itemTextMapper, headerTextMapper);

		final BackendCallResult retVal = new BackendCallResult();

		final JCoFunction function = cn
				.getFunction(ConstantsR3Lrd.FM_LO_API_WEC_ORDER_GET);
		// Fill import parameters
		final JCoParameterList importParameters = function
				.getImportParameterList();

		final Header header = salesDocument.getHeader();
		final ItemList itemList = salesDocument.getItemList();

		final String vbeln = header.getTechKey().getIdAsString();
		if (sapLogger.isDebugEnabled()) {
			final StringBuilder debugOutput = new StringBuilder(
					"Calling getAll with attributes: ");
			debugOutput.append("\n VBELN    : " + vbeln);
			debugOutput.append("\n is changeable: "
					+ backendState.getLoadState().isChangeable());
			sapLogger.debug(debugOutput.toString());
		}

		fillImportParameters(backendState, itemBuffer, importParameters, vbeln);

		insertDefaultHeadObjectRequestParameters(function);
		buildDefaultItemObjectRequestParameters(function);

		// Extensibility fields
		buildExtensibilityFieldsRequestParameters(importParameters);

		// Call the RFC function module
		final ERPLO_APICustomerExits custExit = getCustExit();
		if (custExit != null) {
			custExit.customerExitBeforeGetAll(salesDocument, function, cn,
					sapLogger);
		}
		executeRfc(cn, function);
		if (custExit != null) {
			custExit.customerExitAfterGetAllJCOCall(salesDocument, function,
					cn, sapLogger);
		}
		// First error check
		final JCoTable etMessages = function.getExportParameterList().getTable(
				"ET_MESSAGES");
		final JCoStructure esError = function.getExportParameterList()
				.getStructure("ES_ERROR");

		if ("".equals(JCoHelper.getString(function.getExportParameterList()
				.getStructure("ES_HEAD_COMV"), FIELD_HANDLE))) {
			// Dispatch assigned messages before throwing exception.
			dispatchMessages(salesDocument, etMessages, esError);
			// No HANDLE -> fatal error -> throw backend exception
			logErrorMessageAndThrowException(esError);
		}

		// First check document still exits (might have been deleted
		// meanwhile)
		if (hasMessage("E", "SLS_LORD", "064",
				ConstantsR3Lrd.MESSAGE_IGNORE_VARS, "", "", "", etMessages,
				esError)) {
			return new BackendCallResult(BackendCallResult.Result.failure);
		}

		// Check whether we had an issue with wrong units
		final String wrongUnit = getWrongUnit(esError);
		if (wrongUnit != null) {
			throw new BackendException("Wrong unit passed to ERP: " + wrongUnit
					+ ". Check master data setup. ");
		}

		// Handle result
		final JCoStructure esHeadComV = function.getExportParameterList()
				.getStructure("ES_HEAD_COMV");
		final JCoStructure esHeadComR = function.getExportParameterList()
				.getStructure("ES_HEAD_COMR");

		// Header extensions
		final JCoTable etHeadFieldComV = function.getExportParameterList()
				.getTable("ET_HEAD_FIELD_COMV");
		final JCoTable etHeadFieldComR = function.getExportParameterList()
				.getTable("ET_HEAD_FIELD_COMR");

		final JCoStructure esHvStatComV = function.getExportParameterList()
				.getStructure("ES_HEAD_VSTAT_COMV");
		final JCoTable etHeadTextV = function.getExportParameterList()
				.getTable("ET_HEAD_TEXT_COMV");
		final JCoTable ttHeadCondV = function.getTableParameterList().getTable(
				"TT_HEAD_COND_COMV");

		// partner table at header level:
		final JCoTable ttHeadPartyComV = function.getTableParameterList()
				.getTable("TT_HEAD_PARTY_COMV");
		// final JCoTable ttHeadPartyComI =
		// function.getTableParameterList().getTable("TT_HEAD_PARTY_COMI");
		final JCoTable ttHeadPartyComR = function.getTableParameterList()
				.getTable("TT_HEAD_PARTY_COMR");

		// item tables
		if (sapLogger.isDebugEnabled()) {
			sapLogger
					.debug("Tracing requested items (IT_ITEM_HANDLE) and returned items (TT_ITEM_COMV):\n");

			JCoHelper.logCall(function.getName(), function
					.getImportParameterList().getTable("IT_ITEM_HANDLE"),
					function.getTableParameterList().getTable("TT_ITEM_COMV"),
					sapLogger);
		}

		final JCoTable ttItemKey = function.getTableParameterList().getTable(
				"TT_ITEM_KEY");
		final JCoTable ttItemComV = function.getTableParameterList().getTable(
				"TT_ITEM_COMV");
		final JCoTable ttItemComR = function.getTableParameterList().getTable(
				"TT_ITEM_COMR");
		final JCoTable ttItemVstatComV = function.getTableParameterList()
				.getTable("TT_ITEM_VSTAT_COMV");

		final JCoTable ttItemSlineComV = function.getTableParameterList()
				.getTable("TT_ITEM_SLINE_COMV");

		// Item extensions
		final JCoTable etItemFieldComV = function.getExportParameterList()
				.getTable("ET_ITEM_FIELD_COMV");
		final JCoTable etItemFieldComR = function.getExportParameterList()
				.getTable("ET_ITEM_FIELD_COMR");
		// JCoTable ttItemSlineComI =
		// function.getTableParameterList().getTable("TT_ITEM_SLINE_COMI");

		// document flow
		JCoTable ttHeadDocFlow;
		JCoTable ttIDFlow;
		if (!backendState.isDocflowRead()) {
			backendState.setHeaderDocFlow(function.getTableParameterList()
					.getTable("TT_HDFLOW"));
			backendState.setItemDocFlow(function.getTableParameterList()
					.getTable("TT_IDFLOW"));
			backendState.setDocflowRead(true);
		}
		ttHeadDocFlow = backendState.getHeaderDocFlow();
		ttIDFlow = backendState.getItemDocFlow();

		final JCoTable etItemTextComV = function.getExportParameterList()
				.getTable("ET_ITEM_TEXT_COMV");
		final JCoTable ttObjInst = function.getTableParameterList().getTable(
				"TT_OBJINST");

		// Create map for references of object instances
		final ObjectInstances objInstMap = new ObjectInstances(ttObjInst);

		// Create savedItemsList
		if ((backendState instanceof OrderERP)
				&& backendState.getSavedItemsMap().isEmpty()) {
			buildSavedItemsList(backendState.getSavedItemsMap(),
					itemBuffer.getItemsERPState(), ttItemKey);
		}

		Map<String, Map<String, String>> itemsPriceAttribMap = null;
		Map<String, String> headerPriceAttribs = null;
		Map<String, String> itemsPropMap = null;
		Map<String, String> headerPropMap = null;

		if (readParams.setIpcPriceAttributes) {
			itemsPriceAttribMap = backendState.getItemsPriceAttribMap();
			headerPriceAttribs = backendState.getHeaderPriceAttribs();
			itemsPropMap = backendState.getItemsPropMap();
			headerPropMap = backendState.getHeaderPropMap();

			itemsPriceAttribMap.clear();
			headerPriceAttribs.clear();
			itemsPropMap.clear();
			headerPropMap.clear();
		}

		// header tables
		headMapper.read(esHeadComV, esHeadComR, ttHeadCondV, backendState,
				shop, readParams, headerPriceAttribs, itemsPropMap,
				headerPropMap, salesDocument, header);

		// header extension tables
		handleEtHeadFields(etHeadFieldComV, header);
		handleEtHeadFields(etHeadFieldComR, header);

		// Create map from posno to handle
		final Map<String, String> itemKey = itemMapper
				.buildItemKeyMap(ttItemKey);

		// Build the item map and handle the data from ttItemComR
		// item tables
		final Map<String, Item> itemMap = itemMapper.buildItemMap(
				salesDocument, itemList, itemBuffer.getItemsERPState(),
				ttItemComV);

		itemMapper.read(ttItemComV, ttItemComR, ttItemVstatComV,
				ttItemSlineComV, esError, shop, objInstMap, itemKey, itemMap,
				itemsPriceAttribMap, backendState, readParams, salesDocument);

		// item extension tables
		handleEtItemFields(etItemFieldComV, itemMap);
		handleEtItemFields(etItemFieldComR, itemMap);

		// item doc flow
		if (backendState.isDocflowRead()) {
			handleTtIDFlow(itemKey, itemMap, header.getTechKey()
					.getIdAsString(), header.getDocumentType(),
					backendState.getItemDocFlow());
		} else {
			handleTtIDFlow(itemKey, itemMap, header.getTechKey()
					.getIdAsString(), header.getDocumentType(), ttIDFlow);
		}

		partnerMapper.read(ttHeadPartyComV, ttHeadPartyComR, salesDocument,
				backendState, header);

		if (backendState.isDocflowRead()) {
			handleTtHeadDocFlow(backendState.getHeaderDocFlow(), header);
		} else {
			handleTtHeadDocFlow(ttHeadDocFlow, header);
			backendState.setDocflowRead(true);
		}

		headerTextMapper.read(etHeadTextV, header);
		itemTextMapper.read(etItemTextComV, objInstMap, itemList);

		determineStatus(header, esHvStatComV, ttItemVstatComV, objInstMap,
				itemMap);

		incompletionMapper.mapLogToMessage(function.getTableParameterList()
				.getTable("TT_INCLIST"), etMessages, itemList);

		dispatchMessages(salesDocument, etMessages, esError);

		// now re-create buffer
		itemBuffer.getItemsERPState().clear();
		final Iterator<Item> currentItemState = itemList.iterator();
		while (currentItemState.hasNext()) {
			final Item currentItem = currentItemState.next();
			final boolean isSubItem = !TechKey.isEmpty(currentItem
					.getParentId());
			if (!isSubItem) {
				itemBuffer.getItemsERPState().put(currentItem.getHandle(),
						createItemCopy(currentItem));
			}
		}
		if (custExit != null) {
			custExit.customerExitAfterGetAll(salesDocument, function, cn,
					sapLogger);
		}
		sapLogger.exiting();
		return retVal;
	}

	void fillImportParameters(final BackendState backendState,
			final ItemBuffer itemBuffer,
			final JCoParameterList importParameters, final String vbeln) {
		if (!backendState.getLoadState().isChangeable()
				&& (vbeln.length() <= 10)) {
			importParameters.setValue("IV_VBELN", vbeln);

			importParameters.setValue("IV_TRTYP", LoadOperation.display);
			// In display mode the display field checks can be disabled for
			// performance purposes
			importParameters.getStructure("IS_LOGIC_SWITCH").setValue(
					"FAST_DISPLAY", "X");
			importParameters.getStructure("IS_LOGIC_SWITCH").setValue(
					FIELD_NO_CONVERSION, "X");
			itemBuffer.clearERPBuffer();
		}
		// Instruct LRD to initialize the message log (at the end of FM
		// ...GET_ALL).
		importParameters.setValue("IF_INIT_MSGLOG", "X");

		// scenario ID
		importParameters.setValue("IV_SCENARIO_ID", scenario_LO_API_WEC);
	}

	void initializeMapper() {
		shop = genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BO_TRANSACTION_CONFIGURATION);
		headMapper = (HeaderMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_MAPPER);
		itemMapper = (ItemMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER);
		partnerMapper = (PartnerMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_MAPPER);
		itemTextMapper = (ItemTextMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_TEXT_MAPPER);
		headerTextMapper = (HeadTextMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_TEXT_MAPPER);
	}

	void fillTextMapperAttributes(final TransactionConfiguration shop,
			final ItemTextMapper itemTextMapper,
			final HeadTextMapper headerTextMapper) {
		itemTextMapper.setConfigTextId(shop.getItemTextID());
		itemTextMapper.setConfigLangIso(shop.getLanguageIso());

		headerTextMapper.setConfigTextId(shop.getHeaderTextID());
		headerTextMapper.setConfigLangIso(shop.getLanguageIso());
	}

	/**
	 * Wrapper for the remote function call. This can be used for performance
	 * measurement instrumentation, additional logging a.o.
	 */
	@Override
	public void executeRfc(final JCoConnection theConnection,
			final JCoFunction theFunction) throws BackendException {

		sapLogger.entering("executeRfc");
		try {
			theConnection.execute(theFunction);
		} finally {
			sapLogger.exiting();
		}
	}

	/**
	 * Fills table of extensions for the LO-API call
	 * 
	 * @param extensionFields
	 *            Available extensions
	 * @param itFieldReq
	 * @param fieldType
	 */
	protected void fillExtensibilityTable(
			final Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields,
			final JCoTable itFieldReq,
			final LrdFieldExtension.FieldType fieldType) {

		if (extensionFields.get(fieldType) == null) {
			return;
		}

		final LrdFieldExtension lrdFieldExtension = extensionFields
				.get(fieldType);
		final List<String> fields = lrdFieldExtension.getFieldnames();

		if (fields != null) {
			for (final String field : fields) {
				itFieldReq.appendRow();
				itFieldReq.setValue("OBJECT", lrdFieldExtension.getFieldType()
						.getObjectType());
				itFieldReq.setValue("FIELD", field);
			}
		}

	}

	/**
	 * Returns the <code>ItemData</code> for a handle in the
	 * <code>objInstMap</code> table.
	 * 
	 * @param handle
	 *            Item handle
	 * @param objInstMap
	 *            Map of LO-API objects (items)
	 * @param itemMap
	 *            Map of BOL items
	 * @return Parent item
	 */
	protected Item getParentItem(final String handle,
			final ObjectInstances objInstMap, final Map<String, Item> itemMap) {

		final String itemHandle = objInstMap.getParent(handle);
		final Item item = itemMap.get(itemHandle);
		if (item == null) {
			throw new ApplicationBaseRuntimeException(
					"error in data references, item not found");
		}

		return item;
	}

	/**
	 * Evaluates the response and checks for wrong unit. Later on we need to
	 * handle those units when re-reading items.
	 * 
	 * @param message
	 *            LO-API call results
	 * @return Wrong unit ID
	 */
	protected String getWrongUnit(final JCoStructure message) {
		String wrongUnit = null;
		final String msgId = message.getString("MSGID");
		final String msgNo = message.getString("MSGNO");
		if ("BM".equals(msgId) && "305".equals(msgNo)) {
			wrongUnit = message.getString("MSGV1");
		} else if ("V1".equals(msgId) && "384".equals(msgNo)) {
			wrongUnit = message.getString("MSGV1");
		}
		return wrongUnit;
	}

	/**
	 * Transfers header fields from the LO-API response to the BOL header
	 * 
	 * @param etHeadFields
	 *            Table of header fields
	 * @param header
	 *            BOL representation of header
	 */
	protected void handleEtHeadFields(final JCoTable etHeadFields,
			final Header header) {

		if (!(etHeadFields.getNumRows() > 0)) {
			return;
		}

		int cntRow = 0;

		etHeadFields.firstRow();

		while (cntRow < etHeadFields.getNumRows()) {
			final JCoTable fieldContent = etHeadFields
					.getTable("FIELD_CONTENT");

			fieldContent.firstRow();
			int i = 0;
			while (i < fieldContent.getNumRows()) {

				header.addExtensionData(
						(String) fieldContent.getValue("FIELDNAME"),
						fieldContent.getValue("COMV_VALUE"));
				i++;
				fieldContent.nextRow();
			}
			cntRow++;
			etHeadFields.nextRow();
		}
	}

	/**
	 * Transfers item fields from LO-API response to the BOL item
	 * 
	 * @param etItemFields
	 *            Item fields from ERP
	 * @param itemMap
	 *            Map of BOL items
	 */
	protected void handleEtItemFields(final JCoTable etItemFields,
			final Map<String, Item> itemMap) {

		if (!(etItemFields.getNumRows() > 0)) {
			return;
		}

		int cntRow = 0;

		etItemFields.firstRow();

		while (cntRow < etItemFields.getNumRows()) {

			final String handle = JCoHelper.getString(etItemFields,
					FIELD_HANDLE);
			final Item item = itemMap.get(handle);

			if (item != null) {

				final JCoTable fieldContent = etItemFields
						.getTable("FIELD_CONTENT");

				fieldContent.firstRow();
				int i = 0;
				while (i < fieldContent.getNumRows()) {

					item.addExtensionData(
							(String) fieldContent.getValue("FIELDNAME"),
							fieldContent.getValue("COMV_VALUE"));
					i++;
					fieldContent.nextRow();
				}
			}

			cntRow++;
			etItemFields.nextRow();
		}

	}

	/**
	 * Handles docflow
	 * 
	 * @param ttHeadDocFlow
	 * @param head
	 *            Cart or order header
	 */
	protected void handleTtHeadDocFlow(final JCoTable ttHeadDocFlow,
			final Header head) {

		if (!(ttHeadDocFlow.getNumRows() > 0)) {
			return;
		}

		int cntRow = 0;

		ttHeadDocFlow.firstRow();
		final int numRows = ttHeadDocFlow.getNumRows();

		while (cntRow < numRows) {
			final ConnectedDocument conDoc = head.createConnectedDocument();

			conDoc.setTechKey(JCoHelper.getTechKey(ttHeadDocFlow, "VBELN_N"));
			conDoc.setDocNumber(ConversionTools.cutOffZeros(JCoHelper
					.getString(ttHeadDocFlow, "VBELN_N")));
			final DocumentType docType = DocumentTypeMapping
					.getDocumentTypeByProcess(JCoHelper.getString(
							ttHeadDocFlow, "VBTYP_N"));

			conDoc.setDocType(docType);

			/** Only process known document type * */

			if (docType != null) {

				// Determine displayability and application type
				if (DocumentType.ORDER.equals(docType)
						|| DocumentType.QUOTATION.equals(docType)
						|| DocumentType.RFQ.equals(docType)) {
					conDoc.setAppTyp(ConnectedDocument.ORDER);
					conDoc.setDisplayable(true);
				} else if (DocumentType.INVOICE.equals(docType)
						|| DocumentType.INVOICE_CNC.equals(docType)
						|| DocumentType.CREDIT_MEMO.equals(docType)
						|| DocumentType.CREDIT_MEMO_CNC.equals(docType)) {
					conDoc.setAppTyp(ConnectedDocument.BILL);
					conDoc.setDisplayable(true);
				} else if (DocumentType.DELIVERY.equals(docType)) {
					conDoc.setAppTyp(ConnectedDocument.DLVY);
					conDoc.setDisplayable(false);
				}

				// Predecessor Documents
				if (ttHeadDocFlow.getInt("HLEVEL") < 0) {
					head.addPredecessor(conDoc);
				}

				// Sucessor Documents
				if (ttHeadDocFlow.getInt("HLEVEL") > 0) {
					head.addSuccessor(conDoc);
				}
			}

			cntRow++;
			ttHeadDocFlow.nextRow();
		}

	}

	/**
	 * Handle data of the table <code>ttIDFlow</code> and fill
	 * <code>item data</code>. (Only direct references, either successor or
	 * predecessor ones, of type contract, delivery or invoice are determined
	 * according to ECO ERP due to performance reason)
	 * 
	 * @param itemKey
	 *            Map of item posnr
	 * @param itemMap
	 *            Map of items
	 * @param thisDocNumber
	 *            Number of the document for which the doc flow is read (WITHOUT
	 *            leading zeros)
	 * @param documentType
	 *            Document type
	 * @param ttIDFlow
	 *            Table of item document flow
	 */
	protected void handleTtIDFlow(final Map<String, String> itemKey,
			final Map<String, Item> itemMap, String thisDocNumber,
			final DocumentType documentType, final JCoTable ttIDFlow) {

		if (!(ttIDFlow.getNumRows() > 0)) {
			return;
		}
		thisDocNumber = ConversionTools.cutOffZeros(thisDocNumber);
		final int numItems = ttIDFlow.getNumRows();
		for (int i = 0; i < numItems; i++) {
			ttIDFlow.setRow(i);

			DocumentType docType = DocumentTypeMapping
					.getDocumentTypeByProcess(JCoHelper.getString(ttIDFlow,
							"VBTYP_N"));
			String docNum = ConversionTools.cutOffZeros(JCoHelper.getString(
					ttIDFlow, "VBELN_N"));
			String posNum = JCoHelper.getString(ttIDFlow, "POSNR_N");
			DocumentType flowDocType = DocumentType.UNKNOWN;

			String fieldSuffix = ""; // Can either be "_V" or "_N"
			// First check if line is a direct predecessor or sucessor of the
			// docNumber (
			if (documentType.equals(docType) && thisDocNumber.equals(docNum)) {
				// Successor - Predecessor relation (e.g. contract -> order)
				flowDocType = DocumentTypeMapping
						.getDocumentTypeByProcess(JCoHelper.getString(ttIDFlow,
								"VBTYP_V"));
				JCoHelper.getString(ttIDFlow, "VBELN_V");
				fieldSuffix = "_V";
			} else {
				// Not then try Predecessor - Successor relation (e.g. order ->
				// delivery or invoice)
				docType = DocumentTypeMapping
						.getDocumentTypeByProcess(JCoHelper.getString(ttIDFlow,
								"VBTYP_V"));
				docNum = ConversionTools.cutOffZeros(JCoHelper.getString(
						ttIDFlow, "VBELN_V"));
				posNum = JCoHelper.getString(ttIDFlow, "POSNR_V");
				if (documentType.equals(docType)
						&& thisDocNumber.equals(docNum)) {
					// Predecessor - Successor relation (e.g. order -> delivery
					// or invoice)
					flowDocType = DocumentTypeMapping
							.getDocumentTypeByProcess(JCoHelper.getString(
									ttIDFlow, "VBTYP_N"));
					JCoHelper.getString(ttIDFlow, "VBELN_N");
					fieldSuffix = "_N";
				}
			}

			// Only Contracts and Deliveries will be processed
			if (DocumentType.DELIVERY.equals(flowDocType)) {

				// get the item, the DocFlow belongs to
				String handle = null;
				if (itemKey != null) {
					handle = itemKey.get(posNum);
				}

				Item itm = null;
				if ((handle != null) && (handle.length() > 0)) {
					itm = itemMap.get(handle);
				}

				/** Delivery, attached via connected documents * */
				if (itm != null) {
					final ConnectedDocumentItem conDoc = itm
							.createConnectedDocumentItemData();

					conDoc.setTechKey(JCoHelper.getTechKey(ttIDFlow,
							("VBELN" + fieldSuffix)));
					conDoc.setDocNumber(ConversionTools.cutOffZeros(JCoHelper
							.getString(ttIDFlow, ("VBELN" + fieldSuffix))));
					conDoc.setPosNumber(ConversionTools.cutOffZeros(JCoHelper
							.getString(ttIDFlow, ("POSNR" + fieldSuffix))));
					conDoc.setDocType(flowDocType);
					conDoc.setQuantity(ttIDFlow.getBigDecimal("RFMNG"));
					// this is SAP key, no conversion necessary
					conDoc.setUnit(JCoHelper.getString(ttIDFlow, "MEINS"));
					// In ERP scenario only the creation date is available, not
					// the delivery date!
					conDoc.setDate(ttIDFlow.getDate("ERDAT"));
					conDoc.setTrackingURL(""); // Needs to be initialized
					conDoc.setAppTyp("DLVY");
					conDoc.setDisplayable(false);
					itm.addSuccessor(conDoc);
				}
			} // if
		} // for
	}

	/**
	 * Sets up the required LO-API segments on header level (like HEAD, COND..)
	 * 
	 * @param function
	 *            JCO function
	 */
	protected void insertDefaultHeadObjectRequestParameters(
			final JCoFunction function) {
		final JCoTable objReq = function.getImportParameterList().getTable(
				"IT_HEAD_OBJREQ");

		for (final LrdRequestObjectsHead headRequestObject : LrdRequestObjectsHead
				.values()) {
			objReq.appendRow();
			objReq.setValue("OBJECT", headRequestObject.name());
			objReq.setValue("COMV_REQUEST", headRequestObject.getComv_request());
			objReq.setValue("COMR_REQUEST", headRequestObject.getComr_request());
			objReq.setValue("DEF_REQUEST", headRequestObject.getDef_request());
		}

	}

	/**
	 * @return the incompletionMapper
	 */
	public IncompletionMapperImpl getIncompletionMapper() {
		return incompletionMapper;
	}

	/**
	 * @param incompletionMapper
	 *            the incompletionMapper to set
	 */
	public void setIncompletionMapper(
			final IncompletionMapperImpl incompletionMapper) {
		this.incompletionMapper = incompletionMapper;
	}

}
