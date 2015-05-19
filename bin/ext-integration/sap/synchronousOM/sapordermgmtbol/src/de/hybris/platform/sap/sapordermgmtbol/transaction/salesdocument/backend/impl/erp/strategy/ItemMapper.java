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
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase.ItemUsage;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp.BackendConfigurationException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy.ERPLO_APICustomerExits;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.CustomizingHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.GetAllReadParameters;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


/**
 * Class is responsible to map item (cart entry, oder entry) information between LO-API and the BOL layer. Used for
 * reading and writing
 */
public class ItemMapper extends BaseMapper
{

	/**
	 * ID of LO-API item segment
	 */
	public static final String OBJECT_ID_ITEM = "ITEM";
	/**
	 * Logging instance
	 */
	public static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(ItemMapper.class.getName());

	/**
	 * Empty string
	 */
	protected static String EMPTY_STRING = "";

	/** text control constant */
	public static final String LF = "\n";

	/**
	 * Customer exit instance
	 */
	protected ERPLO_APICustomerExits custExit = null;
	/**
	 * Factory to access SAP session beans
	 */
	protected GenericFactory genericFactory = null;
	/**
	 * Converter BO
	 */
	protected Converter converter;

	/**
	 * @param converter
	 */
	public void setConverter(final Converter converter)
	{
		this.converter = converter;
	}

	/**
	 * @param custExit
	 */
	public void setCustExit(final ERPLO_APICustomerExits custExit)
	{
		this.custExit = custExit;
	}

	/**
	 * Bean constructor.
	 * <p>
	 * 
	 * @param genericFactory
	 *           Factory to access SAP session beans
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}


	@Override
	public void init()
	{
		/* nothing to initialize */
	}

	/**
	 * Reading item information from LO-API. The first 4 parameters are JCO tables containing item related information
	 * 
	 * @param ttItemComV
	 * @param ttItemComR
	 * @param ttItemVstatComV
	 * @param ttItemSlineComV
	 * @param esError
	 *           Error structure
	 * @param shop
	 *           Customizing
	 * @param objInstMap
	 * @param itemKey
	 *           Map of item keys
	 * @param itemMap
	 *           Map of BOL items
	 * @param itemsPriceAttribMap
	 * @param backendState
	 * @param readParams
	 * @param salesDocument
	 */
	public void read(final JCoTable ttItemComV, //
			final JCoTable ttItemComR, //
			final JCoTable ttItemVstatComV, //
			final JCoTable ttItemSlineComV, //
			final JCoStructure esError, //
			final TransactionConfiguration shop, //
			final ObjectInstances objInstMap, //
			final Map<String, String> itemKey, //
			final Map<String, Item> itemMap, //
			final Map<String, Map<String, String>> itemsPriceAttribMap, //
			final BackendState backendState, //
			final GetAllReadParameters readParams, //
			final SalesDocument salesDocument //
	)
	{

		final ItemList itemList = salesDocument.getItemList();
		final Header header = salesDocument.getHeader();

		handleTtItemComR(salesDocument, ttItemComR, shop, itemMap);
		handleTtItemComV(itemMap, ttItemComV, readParams.setIpcPriceAttributes, itemsPriceAttribMap, itemKey, backendState);
		handleTtItemSlineComV(ttItemSlineComV, objInstMap, itemMap);

		// reset CFG indicator
		for (final Item item : salesDocument.getItemList())
		{
			if ((!TechKey.isEmpty(item.getParentId())) && item.isConfigurable() && isRootConfigurable(item, itemMap))
			{
				item.setConfigurable(false);
			}
		}

		handleItemUsages(ttItemComR, itemMap);

		handleTtItemVstatComV(ttItemVstatComV, objInstMap, itemMap, backendState.getSavedItemsMap());

		setDeliveryPriorityToHeader(header, ttItemComV, itemMap);

		markInvalidItems(itemList, esError);
	}

	/**
	 * Handle data of TT_ITEM_COMR and fill item data. The HANDLE is the <code>TechKey</code> and is used as key in the
	 * <code>HashMap</code>
	 * 
	 * @param salesDoc
	 * @param ttItemComR
	 * @param shop
	 * @param itemMap
	 * @return Map of items, with their handles as key
	 */
	protected Map<String, Item> handleTtItemComR(final SalesDocument salesDoc, final JCoTable ttItemComR,
			final TransactionConfiguration shop, final Map<String, Item> itemMap)
	{

		if (!ttItemComR.isEmpty())
		{
			ttItemComR.firstRow();
			do
			{
				final String handle = JCoHelper.getString(ttItemComR, ConstantsR3Lrd.FIELD_HANDLE);
				final Item item = itemMap.get(handle);
				if (item != null)
				{
					mapItemComR(salesDoc, ttItemComR, shop, item);
				}
				else
				{
					throw new ApplicationBaseRuntimeException("Item not found, handle: " + handle);
				}

			}
			while (ttItemComR.nextRow());
		}

		return itemMap;
	}

	/**
	 * Maps the data for one item from the TT_ITEM_COMR table.
	 * 
	 * @param salesDoc
	 * 
	 * @param ttItemComR
	 * @param shop
	 * @param item
	 */
	protected void mapItemComR(final SalesDocument salesDoc, final JCoTable ttItemComR, final TransactionConfiguration shop,
			final Item item)
	{

		final BigDecimal quantToDeliver = ttItemComR.getBigDecimal("LSMNG_R");
		final BigDecimal quantDelivered = ttItemComR.getBigDecimal("VSMNG_R");
		final BigDecimal remainingQuantity = new BigDecimal(quantToDeliver.intValue() - quantDelivered.intValue());

		item.setDeliverdQuantity(quantDelivered);
		final String deliveredQuantityUnit = JCoHelper.getString(ttItemComR, "VRKME_R");
		item.setDeliverdQuantityUnit(deliveredQuantityUnit);

		item.setQuantityToDeliver(remainingQuantity);

		// confirmed quantity, so we do not need to read schedlines
		final BigDecimal confirmedQuantity = ttItemComR.getBigDecimal("KBMENG_R");
		item.setConfirmedQuantity(confirmedQuantity);

		// latest deliverydate,so we do not need to read schedlines
		final Date latestDelDate = ttItemComR.getDate("EDATU_LAST");
		item.setLatestDeliveryDate(latestDelDate);

		// currency
		final String currency = JCoHelper.getString(ttItemComR, "WAERK_R");
		item.setCurrency(currency);
		final int numberOfDecimals = CustomizingHelper.getNumberOfDecimals(converter, currency);

		BigDecimal netValue = ttItemComR.getBigDecimal("NETWR_R");
		netValue = ConversionHelper.adjustCurrencyDecimalPoint(netValue, numberOfDecimals);
		item.setNetValue(netValue);

		BigDecimal vat = ttItemComR.getBigDecimal("MWSBP_R");
		vat = ConversionHelper.adjustCurrencyDecimalPoint(vat, numberOfDecimals);
		item.setTaxValue(vat);

		BigDecimal netPrice = ttItemComR.getBigDecimal("NETPR_R");
		netPrice = ConversionHelper.adjustCurrencyDecimalPoint(netPrice, numberOfDecimals);
		item.setNetPrice(netPrice);

		final BigDecimal grossValue = netValue.add(vat);
		item.setGrossValue(grossValue);

		// --------------
		// "Freight" of item is returned in subtotal as specified in the WCB
		// "Source for Freight"
		final String fieldFreight = getFieldNameForFreight(salesDoc);
		if ((fieldFreight != null) && !fieldFreight.isEmpty())
		{
			BigDecimal freight = ttItemComR.getBigDecimal(fieldFreight);
			freight = ConversionHelper.adjustCurrencyDecimalPoint(freight, numberOfDecimals);
			item.setFreightValue(freight);
		}

		// "Source for Net Value without Freight"
		final String fieldNetValueWOFreight = getFieldNameForNetValueWOFreight(salesDoc);
		if ((fieldNetValueWOFreight != null) && !fieldNetValueWOFreight.isEmpty())
		{
			BigDecimal netWOFreight = ttItemComR.getBigDecimal(fieldNetValueWOFreight);
			netWOFreight = ConversionHelper.adjustCurrencyDecimalPoint(netWOFreight, numberOfDecimals);
			item.setNetValueWOFreight(netWOFreight);
		}

		// --------------

		if (sapLogger.isDebugEnabled())
		{
			// now trace pricing information
			final StringBuilder debugOutput = new StringBuilder(LF + "Pricing information on item level: ");
			debugOutput.append(LF + "Currency          " + item.getCurrency());
			debugOutput.append(LF + "NetPrice          " + item.getNetPrice());
			debugOutput.append(LF + "FreightValue      " + item.getFreightValue());
			debugOutput.append(LF + "NetValue          " + item.getNetValue());
			debugOutput.append(LF + "NetValueWoFreight " + item.getNetValueWOFreight());
			debugOutput.append(LF + "TaxValue          " + item.getTaxValue());
			debugOutput.append(LF + "GrossValue        " + item.getGrossValue());
			debugOutput.append(LF + "GrossValueWoFreight " + item.getGrossValueWOFreight());
			sapLogger.debug(debugOutput);
		}

		// set flag for pricing relevant
		if (JCoHelper.getString(ttItemComR, "PRSFD_R").trim().equals(""))
		{
			item.setPriceRelevant(false);
		}
		else
		{
			item.setPriceRelevant(true);
		}

		// set flag for statistical
		if (ttItemComR.getMetaData().hasField("KOWRR_R"))
		{
			if (JCoHelper.getString(ttItemComR, "KOWRR_R").trim().equals(""))
			{
				item.setStatistical(false);
			}
			else
			{
				item.setStatistical(true);
			}
		}

		// internal material/product number
		item.setProductGuid(new TechKey(JCoHelper.getString(ttItemComR, "MATNR_INT_R")));

		// set configurable flag
		item.setConfigurable(checkIsItemConfigurable(ttItemComR) && !checkIsItemVariant(ttItemComR));

		// set isVariant flag and get map
		if (checkIsItemVariant(ttItemComR))
		{
			item.setVariant(true);
			final String variant = JCoHelper.getString(ttItemComR, "STDPD_R");
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("Storing item variant info: " + variant + " for item " + item.getNumberInt() + " - "
						+ item.getProductId());
			}

		}


		mapNullValuesToZero(item);
	}

	/**
	 * Returns the field name for "Freight"
	 * 
	 * @param salesDoc
	 *           Sales document BO, contains the configuration we need to access
	 * 
	 * @return field name for "Freight"
	 */
	protected String getFieldNameForFreight(final SalesDocument salesDoc)
	{
		final String sourceWCB = salesDoc.getTransactionConfiguration().getSourceForFreightItem();
		final String fieldName = generateSubtotalFieldName(sourceWCB);
		return fieldName;
	}

	/**
	 * Returns the field name for "Net Value without Freight"
	 * 
	 * @param salesDoc
	 *           Sales document BO, contains the configuration we need to access
	 * 
	 * @return field name for "Net Value without Freight"
	 */
	protected String getFieldNameForNetValueWOFreight(final SalesDocument salesDoc)
	{
		final String sourceWCB = salesDoc.getTransactionConfiguration().getSourceForNetValueWithoutFreight();
		final String fieldName = generateSubtotalFieldName(sourceWCB);
		return fieldName;
	}

	/**
	 * Generates the subtotal field name
	 * 
	 * @param sourceWCB
	 *           Source for subtotal, read from configuration
	 * 
	 * @return subtotal field name in backend interface
	 */
	protected String generateSubtotalFieldName(final String sourceWCB)
	{

		String source;

		if (sourceWCB == null)
		{
			throw new ApplicationBaseRuntimeException("Null is not allowed");
		}
		else if (sourceWCB.equalsIgnoreCase(TransactionConfiguration.SUBTOTAL1))
		{
			source = "1";
		}
		else if (sourceWCB.equalsIgnoreCase(TransactionConfiguration.SUBTOTAL2))
		{
			source = "2";
		}
		else if (sourceWCB.equalsIgnoreCase(TransactionConfiguration.SUBTOTAL3))
		{
			source = "3";
		}
		else if (sourceWCB.equalsIgnoreCase(TransactionConfiguration.SUBTOTAL4))
		{
			source = "4";
		}
		else if (sourceWCB.equalsIgnoreCase(TransactionConfiguration.SUBTOTAL5))
		{
			source = "5";
		}
		else if (sourceWCB.equalsIgnoreCase(TransactionConfiguration.SUBTOTAL6))
		{
			source = "6";
		}
		else
		{
			throw new ApplicationBaseRuntimeException("Source for " + sourceWCB + " is not defined");
		}

		return "KZWI".concat(source.concat("_R"));
	}

	/**
	 * Check if the item is configurable
	 * 
	 * @param ttItemComR
	 * @return <code>true</code> if item is configurable
	 */
	boolean checkIsItemConfigurable(final JCoTable ttItemComR)
	{
		return !JCoHelper.getStringFromNUMC(ttItemComR, "CUOBJ_R").trim().equals("");
	}

	/**
	 * In case BE returns null for totalDiscount, item gets an initial BigDecimal
	 * 
	 * @param item
	 */
	protected void mapNullValuesToZero(final Item item)
	{
		if (item.getTotalDiscount() == null)
		{
			item.setTotalDiscount(BigDecimal.ZERO);
		}
	}

	/**
	 * Checks if the item is a variant
	 * 
	 * @param ttItemComR
	 * @return <code>true</code> if the item is a variant
	 */
	boolean checkIsItemVariant(final JCoTable ttItemComR)
	{
		final String variant = JCoHelper.getString(ttItemComR, "STDPD_R");
		return variant != null && variant.length() > 0;
	}

	/**
	 * Fills the schedule line entries for all items and sets the requested delivery data (date and quantity) for each
	 * item. The first schedule line, which contains the entered quantity (WNENG is not zero) includes the manual entered
	 * quantity and requested delivery date of the item.
	 * 
	 * @param slineComV
	 * @param objInstMap
	 * @param itemMap
	 *           Map of BOL items where handle is key
	 */
	protected void handleTtItemSlineComV(final JCoTable slineComV, final ObjectInstances objInstMap,
			final Map<String, Item> itemMap)
	{

		// get the schedlin lines and assign them to the items
		final int numSchedLines = slineComV.getNumRows();
		for (int j = 0; j < numSchedLines; j++)
		{

			slineComV.setRow(j);

			// get the item, the schedule line belongs to
			final Item itm = getParentItem(slineComV.getString(ConstantsR3Lrd.FIELD_HANDLE), objInstMap, itemMap);

			final List<Schedline> aList = itm.getScheduleLines();

			/*
			 * The first schedule line, which contains the entered quantity (WNENG is not zero) includes the manual entered
			 * quantity and requested delivery date of the item.
			 */
			if (slineComV.getBigDecimal("WMENG").intValue() != 0)
			{
				itm.setReqDeliveryDate(slineComV.getDate("EDATU"));

			}
			// BMENG contains the confirmed quantity at a certain date
			if (slineComV.getBigDecimal("BMENG").intValue() != 0)
			{
				final Schedline sLine = itm.createScheduleLine();
				sLine.setTechKey(TechKey.generateKey());
				sLine.setCommittedDate(slineComV.getDate("EDATU"));
				sLine.setUnit(itm.getUnit());
				final BigDecimal committedQuantity = new BigDecimal(JCoHelper.getString(slineComV, "BMENG"));
				sLine.setCommittedQuantity(committedQuantity);
				aList.add(sLine);
			}
		}
	}

	/**
	 * Handle data of TT_ITEM_COMV and fill item data. The HANDLE is the <code>TechKey</code> and is used as key in the
	 * <code>HashMap</code>
	 * 
	 * @param itemMap
	 *           Map of BOL items where handle is key
	 * @param ttItemComV
	 * @param setIpcPriceAttributes
	 * @param itemsPriceAttribMap
	 * @param itemKey
	 * @param baseR3Lrd
	 */
	protected void handleTtItemComV(final Map<String, Item> itemMap, final JCoTable ttItemComV,
			final boolean setIpcPriceAttributes, final Map<String, Map<String, String>> itemsPriceAttribMap,
			final Map<String, String> itemKey, final BackendState baseR3Lrd)
	{

		final int numItems = ttItemComV.getNumRows();
		MessageList msgList = null;

		for (int i = 0; i < numItems; i++)
		{

			ttItemComV.setRow(i);

			final String handle = JCoHelper.getString(ttItemComV, ConstantsR3Lrd.FIELD_HANDLE);
			final Item itm = itemMap.get(handle);

			final int itemNumberFromLOAPI = ttItemComV.getInt("POSNR");

			itm.setNumberInt(itemNumberFromLOAPI);

			mapItemComV(itm, ttItemComV, setIpcPriceAttributes, itemsPriceAttribMap, itemKey, itemMap);

			msgList = baseR3Lrd.getMessageList(itm.getTechKey());

			if ((msgList != null) && (msgList.size() > 0))
			{
				for (int j = 0; j < msgList.size(); j++)
				{
					itm.addMessage(msgList.get(j));
				}

			}

		}
	}

	/**
	 * Transfers the item data form <code>ttItemComV</code> to the <code>ItemData</code>.
	 * 
	 * @param item
	 * @param ttItemComV
	 * @param setIpcPriceAttributes
	 * @param itemsPriceAttribMap
	 * @param itemKey
	 * @param itemMap
	 *           Map of BOL items where handle is key
	 */
	protected void mapItemComV(final Item item, final JCoTable ttItemComV, final boolean setIpcPriceAttributes,
			final Map<String, Map<String, String>> itemsPriceAttribMap, final Map<String, String> itemKey,
			final Map<String, Item> itemMap)
	{

		// reset Free Quantity, needs to be re-calculated after by Free Goods
		// backend after each read
		item.setFreeQuantity(BigDecimal.ZERO);

		item.setProductId((JCoHelper.getString(ttItemComV, "MABNR")));
		if (item.getProductGuid() == null)
		{
			// in case of itemComR is missing, initialize product ID
			item.setProductGuid(new TechKey(null));
		}
		final BigDecimal quantity = ttItemComV.getBigDecimal("KWMENG");
		item.setQuantity(quantity);
		item.setOldQuantity(item.getQuantity());
		final String unit = JCoHelper.getString(ttItemComV, "VRKME");


		item.setUnit(unit);
		item.setDescription(JCoHelper.getString(ttItemComV, "ARKTX"));

		if (sapLogger.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder("\nmapItemComV, item created:");
			debugOutput.append("\nNumber:      " + JCoHelper.getStringFromNUMC(ttItemComV, "POSNR"));
			debugOutput.append("\nProduct:     " + JCoHelper.getString(ttItemComV, "MABNR"));
			debugOutput.append("\nQuantity:    " + ConversionHelper.convertBigDecimalToString(quantity));
			debugOutput.append("\nUnit:        " + item.getUnit());
			debugOutput.append("\nDescription: " + JCoHelper.getString(ttItemComV, "ARKTX"));
			sapLogger.debug(debugOutput);
		}

		final String parentItemNumber = JCoHelper.getString(ttItemComV, "UEPOS");
		if ("000000".equals(parentItemNumber))
		{
			item.setParentId(TechKey.EMPTY_KEY);
		}
		else
		{
			final String parentHandle = itemKey.get(parentItemNumber);
			item.setParentId(new TechKey(parentHandle));

			// Set the item usage for subitems
			final Item parentItem = itemMap.get(parentHandle);
			if (parentItem != null)
			{

				final ItemUsage currentUsage = item.getItemUsage();
				if (currentUsage == ItemBase.ItemUsage.NONE)
				{
					if (parentItem.isConfigurable())
					{
						item.setItemUsage(ItemBase.ItemUsage.CONFIGURATION);
					}
					else
					{
						item.setItemUsage(ItemBase.ItemUsage.BOM);
					} // if
				} // if
			} // if
		} // else

		final OverallStatus overallStatus = (OverallStatus) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_OVERALL_STATUS_ORDER);

		overallStatus.init(EStatus.NOT_PROCESSED);
		// Default values
		item.setOverallStatus(overallStatus);
		item.setConfigType("");

		// Cancellation is transferred as rejection code (should not be
		// overwritten
		// in handleTtItemVstatComV().
		final String reasonRejection = JCoHelper.getString(ttItemComV, "ABGRU").trim();
		if (!"".equals(reasonRejection))
		{
			overallStatus.init(EStatus.CANCELLED);
			item.setOverallStatus(overallStatus);
			sapLogger.debug("item rejected");
		}

		// Default value for text.
		final Text text = item.createText();
		text.setText(EMPTY_STRING);
		item.setText(text);

		// Item Category
		item.setItemCategory(JCoHelper.getString(ttItemComV, "PSTYV"));

	}

	private boolean isRootConfigurable(final Item item, final Map<String, Item> itemMap)
	{

		final TechKey parentId = item.getParentId();
		if (parentId.equals(TechKey.EMPTY_KEY))
		{
			return item.isConfigurable();
		}
		else
		{
			final Item parentItem = itemMap.get(parentId.getIdAsString());
			return isRootConfigurable(parentItem, itemMap);
		}

	}

	/**
	 * Handles item usage information
	 * 
	 * @param ttItemComR
	 * @param itemMap
	 *           Map of BOL items where handle is key
	 */
	protected void handleItemUsages(final JCoTable ttItemComR, final Map<String, Item> itemMap)
	{
		final int nrRows = ttItemComR.getNumRows();
		ttItemComR.firstRow();
		for (int i = 0; i < nrRows; i++)
		{
			final Item item = itemMap.get(JCoHelper.getString(ttItemComR, "HANDLE"));
			final String usage = JCoHelper.getString(ttItemComR, "UEPVW_R");
			if ("B".equals(usage))
			{
				item.setItemUsage(ItemBase.ItemUsage.FREE_GOOD_INCL);
			}
			if ("C".equals(usage))
			{
				item.setItemUsage(ItemBase.ItemUsage.FREE_GOOD_EXCL);
			}

			final Item parentItem = determineRootParentItem(itemMap, item);
			if (parentItem != null && parentItem.isConfigurable() && usage.isEmpty())
			{
				item.setItemUsage(ItemBase.ItemUsage.CONFIGURATION);
			}

			ttItemComR.nextRow();
		}
	}

	/**
	 * Determine the root item for a given BOL item
	 * 
	 * @param itemMap
	 *           Map of BOL items where handle is key
	 * @param currItem
	 *           BOL item we want to know the root item for
	 * @return Root item
	 */
	protected Item determineRootParentItem(final Map<String, Item> itemMap, final Item currItem)
	{
		Item parentItem = itemMap.get(currItem.getParentHandle());
		Item rootParent = parentItem;
		while (parentItem != null)
		{
			rootParent = parentItem;
			parentItem = itemMap.get(parentItem.getParentHandle());
		}
		return rootParent;
	}


	/**
	 * Returns the <code>ItemData</code> for a handle in the <code>objInstMap</code> table.
	 * 
	 * @param handle
	 * @param objInstMap
	 * @param itemMap
	 * @return Item for handle
	 */
	protected Item getParentItem(final String handle, final ObjectInstances objInstMap, final Map<String, Item> itemMap)
	{

		final String itemHandle = objInstMap.getParent(handle);
		final Item item = itemMap.get(itemHandle);
		if (item == null)
		{
			throw new ApplicationBaseRuntimeException("error in data references, item not found");
		}

		return item;
	}

	/**
	 * Sets delivery priority into the BOL header from a given item table record
	 * 
	 * @param header
	 * @param ttItemComV
	 * @param itemMap
	 */
	protected void setDeliveryPriorityToHeader(final Header header, final JCoTable ttItemComV, final Map<String, Item> itemMap)
	{
		ttItemComV.firstRow();
		if (ttItemComV.getNumRows() <= 0)
		{
			return;
		}

		if (header.getDeliveryPriority() != null)
		{
			return;
		}

		final String handle = JCoHelper.getString(ttItemComV, ConstantsR3Lrd.FIELD_HANDLE);
		final Item itm = itemMap.get(handle);
		header.setDeliveryPriority(itm.getDeliveryPriority());
	}

	/**
	 * @param itemList
	 * @param esError
	 *           Error structure from LO-API RFC calls
	 */
	protected void markInvalidItems(final ItemList itemList, final JCoStructure esError)
	{

		if (itemList.size() == 0)
		{
			return;
		}

		// reset the invalidity of all items
		final Iterator<Item> it = itemList.iterator();
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
		item = itemList.get(new TechKey(esError.getString(ConstantsR3Lrd.FIELD_HANDLE)));
		if (item != null)
		{
			item.setErroneous(true);
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

	/**
	 * Create a map with all items. The HANDLE is the <code>TechKey</code> and is used as key in the map
	 * 
	 * @param businessObjectInterface
	 *           BO we are dealing with
	 * @param itemList
	 *           list of items which we are going to create
	 * @param itemsERPState
	 *           map of buffered items which we read previously, and which haven't been touched
	 * @param ttItemComV
	 *           JCO Table reflecting the item information
	 * @return map of items, with handle as key
	 */
	public Map<String, Item> buildItemMap(final SalesDocument businessObjectInterface, final ItemList itemList,
			final Map<String, Item> itemsERPState, final JCoTable ttItemComV)
	{

		final int numItems = ttItemComV.getNumRows();

		final StringBuilder debugText = (sapLogger.isDebugEnabled() ? new StringBuilder(LF + "buildItemMap, results: ") : null);
		final Map<String, Item> itemMap = new HashMap<String, Item>(numItems);
		for (int i = 0; i < numItems; i++)
		{

			ttItemComV.setRow(i);

			final Item newItem = businessObjectInterface.createItem();
			itemList.add(newItem);

			newItem.setTechKey(JCoHelper.getTechKey(ttItemComV, ConstantsR3Lrd.FIELD_HANDLE));
			newItem.setHandle(JCoHelper.getString(ttItemComV, ConstantsR3Lrd.FIELD_HANDLE));
			itemMap.put(newItem.getTechKey().getIdAsString(), newItem);
			if (debugText != null)
			{
				debugText.append(LF + newItem.getTechKey() + " added from backend");
			}
		}

		// and now add the remaining items from the buffer
		if (itemsERPState != null)
		{
			final Iterator<Item> bufferedItems = itemsERPState.values().iterator();
			while (bufferedItems.hasNext())
			{
				final Item bufferedItem = bufferedItems.next();
				// check whether we have this item already
				if (!itemMap.containsKey(bufferedItem.getHandle()))
				{
					itemList.add(bufferedItem);
					itemMap.put(bufferedItem.getHandle(), bufferedItem);
					if (debugText != null)
					{
						debugText.append(LF + bufferedItem.getHandle() + " added from buffer");
					}
				}
			}
		}

		if (debugText != null)
		{
			sapLogger.debug(debugText);
		}
		return itemMap;
	}

	/**
	 * Builds a <code>Map</code> for the items to map the POSNR to the HANDLE.
	 * 
	 * @param ttItemKey
	 * @return Map of items
	 */
	public Map<String, String> buildItemKeyMap(final JCoTable ttItemKey)
	{

		if (ttItemKey == null)
		{
			return null;
		}

		final int numLines = ttItemKey.getNumRows();
		if (numLines <= 0)
		{
			return null;
		}

		final HashMap<String, String> itemKey = new HashMap<String, String>(numLines);

		for (int i = 0; i < numLines; i++)
		{

			ttItemKey.setRow(i);

			final String posNr = JCoHelper.getString(ttItemKey, "POSNR");
			final String handle = JCoHelper.getString(ttItemKey, ConstantsR3Lrd.FIELD_HANDLE);

			itemKey.put(posNr, handle);
		}

		return itemKey;
	}

	/**
	 * Helper to fill item table before a LO-API update is done.
	 * 
	 * @param salesDoc
	 *           The sales document
	 * @param itemsToBeChanged
	 * @param salesDocR3Lrd
	 * @param itemComV
	 *           Item data (values)
	 * @param itemComX
	 *           Item data (change flag)
	 * @param objInst
	 * @throws BackendConfigurationException
	 */

	public void write(final SalesDocument salesDoc, final Set<String> itemsToBeChanged, final BackendState salesDocR3Lrd,
			final JCoTable itemComV, final JCoTable itemComX, final JCoTable objInst) throws BackendConfigurationException
	{

		boolean isNewItem = false;

		final Iterator<Item> it = salesDoc.iterator();
		int itemCounter = 0;
		while (it.hasNext())
		{

			final Item item = it.next();

			// Do not send sub items
			if (TechKey.isEmpty(item.getParentId()))
			{

				// Decide whether we need to send this item
				if (itemsToBeChanged.contains(item.getHandle()))
				{

					// handle new items
					if (TechKey.isEmpty(item.getTechKey()))
					{
						if (item.getHandle().isEmpty())
						{
							item.createUniqueHandle();
						}
						item.setTechKey(new TechKey(item.getHandle()));

						isNewItem = true;
						// also update the list of items to be changed and
						// those of items to be re-read, because
						// the item handle has changed
						itemsToBeChanged.remove(item.getHandle());
						itemsToBeChanged.add(item.getTechKey().getIdAsString());

					}

					// Fill itemComV
					itemComV.appendRow();
					itemComX.appendRow();

					JCoHelper.setValue(itemComV, item.getHandle(), ConstantsR3Lrd.FIELD_HANDLE);

					// Set item number to force items to stay in SD session even
					// if a erroneous item has been corrected through
					// initialising its material number
					if (item.getNumberInt() == 0)
					{
						itemCounter = determineItemPosnr(item, itemCounter);
						JCoHelper.setValue(itemComV, itemCounter, ConstantsR3Lrd.FIELD_POSNR);
						JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, ConstantsR3Lrd.FIELD_POSNR);
					}
					else
					{
						itemCounter = item.getNumberInt();
					}

					final String productId = item.getProductId();

					if (productId != null && !productId.isEmpty())
					{
						JCoHelper.setValue(itemComV, item.getProductId(), "MABNR");
					}
					else
					{
						JCoHelper.setValue(itemComV, item.getProductGuid().getIdAsString(), "MABNR");
					}

					salesDocR3Lrd.removeMessageFromMessageList(item.getTechKey(), "b2b.r3lrd.quantityerror");

					// if the item has an error we need to clear the item
					// category
					// because that way the error will be gone
					if (item.isErroneous())
					{
						itemComV.setValue("PSTYV", "");
						itemComX.setValue("PSTYV", ConstantsR3Lrd.ABAP_TRUE);
					}

					if (isNewItem && (item.getQuantity() == null))
					{
						JCoHelper.setValue(itemComV, "1", "KWMENG");
						item.setQuantity(BigDecimal.ONE);
					}

					if (null == item.getQuantity())
					{
						sapLogger.debug("Given quantity was not valid");

						final Message msg = new Message(Message.ERROR, "b2b.r3lrd.quantityerror", null, "");
						salesDocR3Lrd.getOrCreateMessageList(item.getTechKey()).add(msg);
						item.addMessage(msg);
					}

					itemComV.setValue("KWMENG", item.getQuantity());
					itemComV.setValue("VRKME", item.getUnit());


					// The cancellation of items is transferred as rejection
					// code,
					// which
					// is maintained in the shop
					if (item.getOverallStatus().isCancelled())
					{
						final String rejectionCode = item.getRejectionCode();

						JCoHelper.setValue(itemComV, rejectionCode, "ABGRU");
						JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, "ABGRU");
					}

					// Requested Delivery Date
					if (item.getReqDeliveryDate() == null
							&& ((salesDoc.getHeader().getReqDeliveryDate() != null) && (!salesDoc.getHeader().getReqDeliveryDate()
									.equals(ConstantsR3Lrd.DATE_INITIAL))))
					{

						item.setReqDeliveryDate(salesDoc.getHeader().getReqDeliveryDate());
					}
					final Date reqDlvDate = item.getReqDeliveryDate();
					if (reqDlvDate != null)
					{
						itemComV.setValue("EDATU", reqDlvDate);
						itemComX.setValue("EDATU", ConstantsR3Lrd.ABAP_TRUE);
					}

					// Fill itemComX
					JCoHelper.setValue(itemComX, item.getHandle(), ConstantsR3Lrd.FIELD_HANDLE);
					JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, "MABNR");
					JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, "KWMENG");
					JCoHelper.setValue(itemComX, ConstantsR3Lrd.ABAP_TRUE, "VRKME");
					// JCoHelper.setValue(itemComX, "X", "VBELN_REF");
					// JCoHelper.setValue(itemComX, "X", "POSNR_REF");

					if (sapLogger.isDebugEnabled())
					{
						final StringBuffer debugOutput = new StringBuffer("\nItem sent to ERP: ");
						debugOutput.append("\nHandle          : " + itemComV.getString(ConstantsR3Lrd.FIELD_HANDLE));
						debugOutput.append("\nMatId           : " + itemComV.getString("MABNR"));
						debugOutput.append("\nRejection reason: " + itemComV.getString("ABGRU"));
						debugOutput.append("\nQuantity        : " + itemComV.getValue("KWMENG"));
						debugOutput.append("\nUnit            : " + itemComV.getValue("VRKME"));
						debugOutput.append("\nDelivery Date   : " + itemComV.getValue("EDATU"));
						debugOutput.append(" \n");
						sapLogger.debug(debugOutput);
					}

					addToObjInst(objInst, item.getHandle(), "", OBJECT_ID_ITEM);

					if (sapLogger.isDebugEnabled())
					{
						final StringBuffer debugOutput = new StringBuffer("\nOBJINST sent to ERP: ");
						debugOutput.append("\nHandle   : " + item.getHandle());
						debugOutput.append("\nObject Id: " + "ITEM");
						debugOutput.append(" \n");
						sapLogger.debug(debugOutput);
					}
				}
				else
				{
					itemCounter = item.getNumberInt();
					if (sapLogger.isDebugEnabled())
					{
						sapLogger.debug("We don't need to send item: " + item.getHandle());
					}
				}
			}
			else
			{
				if (sapLogger.isDebugEnabled())
				{
					sapLogger.debug("We don't send sub item: " + item.getHandle());
				}
			}

		}

	}

	/**
	 * Handles the item status.
	 * 
	 * @param ttItemVstatComV
	 * @param objInstMap
	 * @param itemMap
	 *           Map of BOL items where handle is key
	 * @param savedItemsMap
	 */
	protected void handleTtItemVstatComV(final JCoTable ttItemVstatComV, final ObjectInstances objInstMap,
			final Map<String, Item> itemMap, final Set<String> savedItemsMap)
	{

		final int numLines = ttItemVstatComV.getNumRows();
		for (int j = 0; j < numLines; j++)
		{

			ttItemVstatComV.setRow(j);
			final Item item = getParentItem(ttItemVstatComV.getString(ConstantsR3Lrd.FIELD_HANDLE), objInstMap, itemMap);

			// Deletable/Cancelable
			if (item.getOverallStatus().isNotProcessed() && (item.getParentId() == TechKey.EMPTY_KEY))
			{
				if (savedItemsMap.contains(item.getTechKey().getIdAsString()))
				{
					item.setCancelable(true);
				}
				else
				{
					item.setDeletable(true);
				}

			}

		}
	}




}
