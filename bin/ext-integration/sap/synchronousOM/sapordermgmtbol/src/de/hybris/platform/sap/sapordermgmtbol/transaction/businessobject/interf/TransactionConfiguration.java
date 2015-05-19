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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;

import java.util.Map;


/**
 * The TransactionConfiguration interface provides sales-relevant settings. (e.g. sales organisation)
 * 
 */
public interface TransactionConfiguration extends SalesDocumentConfiguration
{

	/**
	 * Constant to identify the ID field in the Result Set
	 */
	public final static String ID = "ID";

	/**
	 * Constant to identify the Description field in the Result Set
	 */
	public final static String DESCRIPTION = "DESCRIPTION";


	/**
	 * Constants for Pricing Subtotals - Subtotal1
	 */
	public final static String SUBTOTAL1 = "SUBTOTAL1";

	/**
	 * Constants for Pricing Subtotals - Subtotal2
	 */
	public final static String SUBTOTAL2 = "SUBTOTAL2";

	/**
	 * Constants for Pricing Subtotals - Subtotal3
	 */
	public final static String SUBTOTAL3 = "SUBTOTAL3";

	/**
	 * Constants for Pricing Subtotals - Subtotal4
	 */
	public final static String SUBTOTAL4 = "SUBTOTAL4";

	/**
	 * Constants for Pricing Subtotals - Subtotal5
	 */
	public final static String SUBTOTAL5 = "SUBTOTAL5";

	/**
	 * Constants for Pricing Subtotals - Subtotal6
	 */
	public final static String SUBTOTAL6 = "SUBTOTAL6";


	/**
	 * Delivery Types
	 * <ul>
	 * <li>CRM table crmc_ship_cond, valuehelp crm_ship_cond</li>
	 * <li>ERP table tvsb, valuehelp h_tvsb</li>
	 * </ul>
	 * 
	 * @param consideringWECCustomizing
	 *           <ul>
	 *           <li>false = return all values from the corresponding backend customizing table
	 *           <li>true = restrict the values from the backend, to those which are explicitly allowed in the Webchannel
	 *           Customizing</li>
	 *           </ul>
	 * @return a new Map with the allowed DeliverTypes and description
	 * @throws CommunicationException
	 *            in case backend-error
	 */
	public Map<String, String> getAllowedDeliveryTypes(boolean consideringWECCustomizing) throws CommunicationException;

	/**
	 * Gets language dependent short ID of customer purchase order type. Also see
	 * {@link TransactionConfiguration#setCustomerPurchOrderType(String)}
	 * 
	 * @return ID of purchase order type
	 */
	public String getCustomerPurchOrderType();

	/**
	 * Sets language dependent short ID of customer purchase order type. This is sent to ERP when creating an order. See
	 * header->Purchase Order Data <br>
	 * Currently not used in CRM
	 * 
	 * @param customerPurchOrderType
	 *           customerPurchOrderType short ID
	 */
	public void setCustomerPurchOrderType(String customerPurchOrderType);

	/**
	 * Gets delivery block ID. Also see {@link TransactionConfiguration#setDeliveryBlock(String)}
	 * 
	 * @return delivery block ID
	 */
	public String getDeliveryBlock();

	/**
	 * Sets delivery block ID which is sent to ERP when creating an order. Currently not used in CRM. <br>
	 * If a non-initial delivery block is maintained, orders need to be released in ERP before follow-up processes can
	 * start.
	 * 
	 * @param deliveryBlock
	 *           delivery block ID
	 */
	public void setDeliveryBlock(String deliveryBlock);

	/**
	 * Returns the ISO 639 language Code (2 chars) in lower case.
	 * 
	 * @return isoLanguage
	 */
	public String getLanguageIso();

	/**
	 * @return the text ID under which header texts are stored in the backend
	 */
	public String getHeaderTextID();

	/**
	 * @return the text ID under which item texts are stored in the backend
	 */
	public String getItemTextID();

	/**
	 * @param headerTextId
	 *           the text ID under which header texts are stored in the backend
	 */
	public void setHeaderTextID(String headerTextId);

	/**
	 * @param itemTextId
	 *           the text ID under which item texts are stored in the backend
	 */
	public void setItemTextID(String itemTextId);

	/**
	 * Returns the property mergeIdenticalProducts, which is a WCB setting. True if identical products (e.g. same
	 * product-id, same GUID, same unit) shall be merged. This only applies to the Basket, but not to the Order.
	 * 
	 * @return mergeIdenticalProducts
	 */
	public boolean isMergeIdenticalProducts();

	/**
	 * Sets the mergeIdenticalProducts, which is a WCB setting. True if identical products (e.g. same product-id, same
	 * GUID, same unit) shall be merged
	 * 
	 * @param mergeIdenticalProducts
	 *           if <code>true</code> products will be merged in basket
	 */
	public void setMergeIdenticalProducts(boolean mergeIdenticalProducts);


	/**
	 * Provides information which source is used to determine the net value without freight (e.g. any subtotal)
	 * 
	 * @return sourceForNetValueWithoutFreight source for net value without freight
	 */
	public String getSourceForNetValueWithoutFreight();

	/**
	 * Provides information which source is used to determine the freight value (e.g. any subtotal)
	 * 
	 * @return sourceForFreightItem
	 */
	public String getSourceForFreightItem();








}
