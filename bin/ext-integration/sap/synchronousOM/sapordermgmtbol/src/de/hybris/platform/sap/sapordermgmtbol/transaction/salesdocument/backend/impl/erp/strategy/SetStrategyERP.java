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


import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LrdFieldExtension;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.SetStrategy;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult.Result;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * Strategy for function module <br>
 * ERP_LORD_SET</br> in the ERP. This class consists only of static method. Each of theses methods wraps the function
 * module. The purpose of this class is to maintain only one implementation of the logic necessary to call this function
 * module via jco using data provided by Java objects. <br>
 * 
 * @version 1.0
 */
public class SetStrategyERP extends BaseStrategyERP implements SetStrategy, ConstantsR3Lrd
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(SetStrategyERP.class.getName());

	/**
	 * Strategy for ERP_LORD_SET, all values found in the provided sales document are written to the backend system. If
	 * you want a field to be ignored set the corresponding value to <code>null</code>.
	 * 
	 * @param salesDocR3Lrd
	 *           The SalesDocumentR3Lrd object instance
	 * @param salesDoc
	 *           The sales document
	 * @param shop
	 *           the shop object
	 * @param cn
	 *           Connection to use
	 * @param itemNewShipTos
	 *           The list of item tech keys, for which a new ship to should be created
	 * @param onlyUpdateHeader
	 *           boolean, which indicates, if only header of the object should be updated
	 * @return call result.
	 */
	@Override
	public BackendCallResult execute(final BackendState salesDocR3Lrd, final SalesDocument salesDoc,
			final Map<String, Item> itemsERPStatus, final TransactionConfiguration shop, final JCoConnection cn,
			final ArrayList<String> itemNewShipTos, final boolean onlyUpdateHeader) throws BackendException
	{

		BackendCallResult retVal;

		final String METHOD_NAME = "execute()";
		final boolean paytypeCOD = false;

		final HeaderMapper headMapper = (HeaderMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_MAPPER);
		final ItemMapper itemMapper = (ItemMapper) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_MAPPER);
		final PartnerMapper partnerMapper = (PartnerMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_PARTNER_MAPPER);
		final ItemTextMapper itemTextMapper = (ItemTextMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ITEM_TEXT_MAPPER);
		itemTextMapper.setConfigTextId(shop.getItemTextID());
		itemTextMapper.setConfigLangIso(shop.getLanguageIso());
		final HeadTextMapper headerTextMapper = (HeadTextMapper) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_HEADER_TEXT_MAPPER);
		headerTextMapper.setConfigTextId(shop.getHeaderTextID());
		headerTextMapper.setConfigLangIso(shop.getLanguageIso());


		sapLogger.entering(METHOD_NAME);

		try
		{
			final JCoFunction function = cn.getFunction(ConstantsR3Lrd.FM_LO_API_SET);
			// getting import parameter
			final JCoParameterList importParams = function.getImportParameterList();
			// get the import structure for the header
			final JCoStructure headComv = importParams.getStructure("IS_HEAD_COMV");
			final JCoStructure headComx = importParams.getStructure("IS_HEAD_COMX");
			final JCoTable ObjInst = importParams.getTable("IT_OBJINST");
			// fill header
			headMapper.write(salesDoc.getHeader(), salesDocR3Lrd, headComv, headComx, shop);

			// Header extension data
			final JCoTable ttObjectContentComV = importParams.getTable("IT_OBJECT_CONTENT_COMV");
			fillHeaderFieldExtensions(salesDoc.getHeader(), ttObjectContentComV);
			Set<String> itemsToBeChanged = null;

			// setting the import table basket_item
			if (itemNewShipTos == null)
			{
				// fill items
				if (!onlyUpdateHeader)
				{

					// first check which items we need to change
					itemsToBeChanged = findItemsToChange(itemsERPStatus, salesDoc, shop);
					final JCoTable ItemComV = importParams.getTable("IT_ITEM_COMV");
					final JCoTable ItemComX = importParams.getTable("IT_ITEM_COMX");
					itemMapper.write(salesDoc, itemsToBeChanged, salesDocR3Lrd, ItemComV, ItemComX, ObjInst);

					// fill item extension fields
					fillItemFieldExtensions(salesDoc, itemsToBeChanged, ttObjectContentComV);

				}

				// Fill Text
				final JCoTable textComV = importParams.getTable("IT_TEXT_COMV");
				final JCoTable textComX = importParams.getTable("IT_TEXT_COMX");
				headerTextMapper.write(salesDoc.getHeader(), textComV, textComX, ObjInst);
				itemTextMapper.write(salesDoc, itemsToBeChanged, textComV, textComX, ObjInst);
			}
			// set partner info
			final JCoTable PartnerComV = importParams.getTable("IT_PARTY_COMV");
			final JCoTable PartnerComX = importParams.getTable("IT_PARTY_COMX");
			if (!onlyUpdateHeader)
			{
				partnerMapper.write(salesDoc, PartnerComV, PartnerComX, shop, paytypeCOD, ObjInst);
			}
			final ERPLO_APICustomerExits custExit = getCustExit();
			if (custExit != null)
			{
				custExit.customerExitBeforeSet(salesDoc, function, cn, sapLogger);
				// call the function
			}

			cn.execute(function);

			if (custExit != null)
			{
				custExit.customerExitAfterSet(salesDoc, function, cn, sapLogger);
			}

			// get export parameter list
			final JCoParameterList exportParams = function.getExportParameterList();

			if (sapLogger.isDebugEnabled())
			{
				logCall(ConstantsR3Lrd.FM_LO_API_SET, importParams, null);
			}

			final JCoStructure esError = exportParams.getStructure("ES_ERROR");

			// do we need to handle error situations on header level?
			retVal = new BackendCallResult();
			if (isRecoverableHeaderError(esError))
			{
				retVal = new BackendCallResult(Result.failure);
			}

			salesDocR3Lrd.setErroneous(esError.getString("ERRKZ").equals("X"));
			markInvalidItems(salesDoc, esError);

			salesDoc.clearMessages();
			salesDoc.getHeader().clearMessages();

			dispatchMessages(salesDoc, exportParams.getTable("ET_MESSAGES"), exportParams.getStructure("ES_ERROR"));

			salesDocR3Lrd.setDocumentInitial(false);

			return retVal;
		}
		finally
		{
			sapLogger.exiting();
		}
	}

	/**
	 * Determine items which need to be changed. Compares actual sales document data with the buffered ERP status.
	 * 
	 * @param itemsERPStatus
	 *           List of items which reflect the ERP status (when the document has been read previously
	 * @param salesDoc
	 *           Current sales transaction. Contains items which we need to compare with the ERP status
	 * @param transConf
	 *           Holds the sales customizing in general
	 * @return Set of item handles which represent the items to be changed.
	 */
	protected Set<String> findItemsToChange(final Map<String, //
			Item> itemsERPStatus, //
			final SalesDocument salesDoc, //
			final TransactionConfiguration transConf)
	{

		// loop all items and compare data
		final Set<String> itemsToBeChanged = new HashSet<String>();
		final boolean tracing = sapLogger.isDebugEnabled();
		StringBuilder output = null;
		String traceString = null;
		if (tracing)
		{
			output = new StringBuilder(LF + "findItemsToChange, Results:");
		}

		final int size = salesDoc.getItemList().size();
		for (int i = 0; i < size; i++)
		{
			final Item currentItem = salesDoc.getItemList().get(i);
			// try to find corresponding item in existing buffer. If no such
			// item exists,
			// we need to mark the item as to be changed
			Item existingItem = null;
			if (itemsERPStatus != null)
			{
				existingItem = itemsERPStatus.get(currentItem.getHandle());
			}
			if ((existingItem != null))
			{
				// first compare attributes on item level

				final boolean doNotAddToChangedItems = isItemToBeSent(currentItem, existingItem);
				if (tracing)
				{
					traceString = LF + currentItem.getHandle() + SEPARATOR + currentItem.getProductId() + SEPARATOR;
					output.append(traceString + "item attributes equal: " + doNotAddToChangedItems);
				}
				if (doNotAddToChangedItems)
				{
					// now take configuration into account

					if (currentItem.isConfigurable())
					{
						// we always need to sent configurable items
						itemsToBeChanged.add(currentItem.getHandle());
						if (tracing)
						{
							output.append(traceString + "is configurable");
						}
					}
					else
					{
						// The item does not need to be changed to ERP
						// Call an additional customer exit to allow
						// customers to
						final ERPLO_APICustomerExits custExit = getCustExit();
						if (!custExit.customerExitIsItemChanged(currentItem, existingItem))
						{
							if (tracing)
							{
								output.append(traceString + "does not need to be changed in ERP");
							}
						}
						else
						{
							itemsToBeChanged.add(currentItem.getHandle());
							if (tracing)
							{
								output.append(traceString + "extensions changed / cust exit");
							}
						}
					}
				}
				else
				{
					itemsToBeChanged.add(currentItem.getHandle());
					if (tracing)
					{
						output.append(traceString + "changed");
					}
				}
			}
			else
			{
				itemsToBeChanged.add(currentItem.getHandle());
				if (tracing)
				{
					output.append(traceString + "new or not known yet or customizing tells to send all items");
				}
			}
		}
		if (tracing)
		{
			sapLogger.debug(output);
		}

		return itemsToBeChanged;
	}

	protected boolean isItemToBeSent(final Item currentItem, final Item existingItem)
	{
		boolean isEqual = true;
		final Date currentItemReqDelDate = currentItem.getReqDeliveryDate();
		final Date existingItemReqDelDate = existingItem.getReqDeliveryDate();
		final boolean equalDeliverDate = (currentItemReqDelDate == null && existingItemReqDelDate == null)
				|| (currentItemReqDelDate != null && currentItemReqDelDate.equals(existingItemReqDelDate));

		final boolean isProductIdIdentical = currentItem.getProductId().equals(existingItem.getProductId());
		final String currentUnit = currentItem.getUnit();
		final String existingUnit = existingItem.getUnit();

		final boolean isUnitIdentical = (currentUnit == null && existingUnit == null)
				|| (currentUnit != null && currentUnit.equals(existingUnit));

		final boolean isQuantityIdentical = currentItem.getQuantity().compareTo(existingItem.getQuantity()) == 0;
		final boolean isCancelStatusIdentical = currentItem.getOverallStatus().isCancelled() == existingItem.getOverallStatus()
				.isCancelled();
		isEqual = isProductIdIdentical && (isQuantityIdentical && currentItem.getFreeQuantity().compareTo(BigDecimal.ZERO) == 0)
				&& equalDeliverDate && isEqualTexts(currentItem, existingItem) && isUnitIdentical && isCancelStatusIdentical;
		return isEqual;
	}

	/**
	 * Compare 2 item texts
	 */
	protected boolean isEqualTexts(final Item item1, final Item item2)
	{
		if ((item1.getText() == null) && (item2.getText() == null))
		{
			return true;
		}
		if ((item1.getText() == null) || (item2.getText() == null))
		{
			return false;
		}
		return (item1.getText().getText().equals(item2.getText().getText()));
	}

	/**
	 * @param salesDoc
	 * @param esError
	 * @throws BackendException
	 */
	protected void markInvalidItems(final SalesDocument salesDoc, final JCoStructure esError) throws BackendException
	{
		final ItemList items = salesDoc.getItemList();
		if (items.size() == 0)
		{
			return;
		}

		// reset the invalidity of all items
		final Iterator<Item> it = items.iterator();
		Item item = null;

		while (it.hasNext())
		{
			item = it.next();
			item.setErroneous(false);
		}

		// Return if the error is not related to an item
		if (!esError.getString("OBJECT").equals("ITEM"))
		{
			return;
		}

		// set item invalid
		item = items.get(new TechKey(esError.getString(FIELD_HANDLE)));
		if (item != null)
		{
			item.setErroneous(true);
			dispatchMessages(item, null, esError);
		}
		else
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Item to be marked erroneouos not found");
			}
		}
	}

	/**
	 * @param header
	 * @param objectContentComV
	 */
	protected void fillHeaderFieldExtensions(final Header header, //
			final JCoTable objectContentComV)
	{
		final ERPLO_APICustomerExits custExit = getCustExit();

		if (custExit == null)
		{
			return;
		}

		final Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields = custExit.customerExitGetExtensionFields();

		if (extensionFields.isEmpty())
		{
			return;
		}

		final LrdFieldExtension lrdFieldExtension = extensionFields.get(LrdFieldExtension.FieldType.HeadComV);
		if (lrdFieldExtension == null)
		{
			return;
		}

		final List<String> headerFields = lrdFieldExtension.getFieldnames();

		if (headerFields.isEmpty())
		{
			return;
		}

		objectContentComV.appendRow();
		objectContentComV.setValue(FIELD_HANDLE, header.getHandle());
		objectContentComV.setValue(FIELD_OBJECT_ID, LrdFieldExtension.objectHead);

		final JCoTable fieldContent = objectContentComV.getTable("FIELD_CONTENT");

		for (final String field : headerFields)
		{
			fieldContent.appendRow();
			fieldContent.setValue("FIELDNAME", field);
			fieldContent.setValue("COMC_VALUE", ABAP_TRUE);
			fieldContent.setValue("COMV_VALUE", header.getExtensionData(field));
		}

	}

	/**
	 * Passes item extensions to LO-API.
	 * 
	 * @param salesDoc
	 *           Sales transaction which contains all items
	 * @param itemsToBeChanged
	 *           Handles of items which we want to send to ERP
	 * @param objectContentComV
	 *           JCO table of extensions
	 */
	protected void fillItemFieldExtensions(final SalesDocument salesDoc, final Set<String> itemsToBeChanged,
			final JCoTable objectContentComV)
	{

		final ERPLO_APICustomerExits custExit = getCustExit();

		if (custExit == null)
		{
			return;
		}

		final Map<LrdFieldExtension.FieldType, LrdFieldExtension> extensionFields = custExit.customerExitGetExtensionFields();

		if ((extensionFields == null) || (extensionFields.isEmpty()))
		{
			return;
		}

		final LrdFieldExtension lrdFieldExtension = extensionFields.get(LrdFieldExtension.FieldType.ItemComV);
		if (lrdFieldExtension == null)
		{
			return;
		}

		List<String> itemFields = null;
		itemFields = lrdFieldExtension.getFieldnames();

		if (itemFields.isEmpty())
		{
			return;
		}

		final ItemList itemList = salesDoc.getItemList();
		int index = 0;
		Item item = itemList.get(index);

		while (index < itemList.size())
		{
			item = itemList.get(index);

			// Check if we want to send this item
			if (itemsToBeChanged.contains(item.getHandle()))
			{
				objectContentComV.appendRow();
				// objectContentComV.setValue(FIELD_HANDLE, item.getHandle());
				objectContentComV.setValue(FIELD_HANDLE, item.getTechKey().getIdAsString());
				objectContentComV.setValue(FIELD_OBJECT_ID, "ITEM");

				final JCoTable fieldContent = objectContentComV.getTable("FIELD_CONTENT");

				for (final String field : itemFields)
				{
					fieldContent.appendRow();
					fieldContent.setValue("FIELDNAME", field);
					fieldContent.setValue("COMC_VALUE", ABAP_TRUE);
					fieldContent.setValue("COMV_VALUE", item.getExtensionData(field));
				}
			}

			index++;

		}

	}


	/**
	 * Determines a POSNR for items which have not been exchanged with LO-API so far. The method is called only once per
	 * item (if numberInt is initial) <br>
	 * As default 10 is added to the last known number, as this corresponds to the standard SD customizing.
	 * 
	 * @param item
	 *           Sales document item (has not been exchanged with SD yet)
	 * @param lastNumber
	 *           number of previous item
	 * @return POSNR for new SD item
	 */
	protected int determineItemPosnr(final Item item, final int lastNumber)
	{
		return lastNumber + 10;
	}

}
