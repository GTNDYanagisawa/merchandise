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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;

import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoTable;


/**
 * Represents the state of a sales document
 * 
 */
public interface BackendState
{

	/**
	 * @return Current Load state of the document
	 */
	public LoadOperation getLoadState();

	/**
	 * For performance reasons DocFlow should only be read when necessary. Typically + DocFlow will not change when one
	 * changes or displays an order within one session. So it is sufficient to read it once.
	 * 
	 * @return true, if docflow has been read
	 */
	public boolean isDocflowRead();

	/**
	 * In case an order has just been created, or just been saved, we do not expect certain data to be present already.
	 * For Example DocFlow data. So for performance reasons it makes sense not to request those data.
	 * 
	 * @return true, if the salesdocument is just created
	 */
	public boolean isCreateProcess();

	/**
	 * Sets the header docflow in the table buffer
	 * 
	 * @param table
	 *           header docflow table
	 */
	public void setHeaderDocFlow(JCoTable table);

	/**
	 * Sets the items docflow in the table buffer
	 * 
	 * @param table
	 *           JCoTable item docflow
	 */
	public void setItemDocFlow(JCoTable table);

	/**
	 * Sets the flag dowflowRead
	 * 
	 * @param b
	 *           docflowread
	 */
	public void setDocflowRead(boolean b);

	/**
	 * Returns the docflow of the header
	 * 
	 * @return JCoTable docflow of the header
	 */
	public JCoTable getHeaderDocFlow();

	/**
	 * Returns the docflow of the items
	 * 
	 * @return HashMap doc flow of the items
	 */
	public JCoTable getItemDocFlow();

	/**
	 * Returns the item price attribute map If not yet present, creates the map
	 * 
	 * @return HashMap, map for the item price attributes
	 */
	public Map<String, Map<String, String>> getItemsPriceAttribMap();

	/**
	 * Returns the Header price attribute map If not yet present, creates the map
	 * 
	 * @return HashMap, map for the header price attributes
	 */
	public Map<String, String> getHeaderPriceAttribs();

	/**
	 * Returns the item property map If not yet present, creates the map
	 * 
	 * @return HashMap, map for the item properties
	 */
	public Map<String, String> getItemsPropMap();

	/**
	 * Returns the header property map If not yet present, creates the map
	 * 
	 * @return HashMap, map for the header properties
	 */
	public Map<String, String> getHeaderPropMap();

	/**
	 * Returns the MessageList for the given key, if one exits, null else
	 * 
	 * @param techKey
	 *           Technical key identifying head or item object
	 * @return the MessageList for the given TechKey or a default MessageList, if the key is null or initial, if existing
	 *         else null
	 */
	public MessageList getMessageList(TechKey techKey);

	/**
	 * Returns the map, to store item variant information
	 * 
	 * @return HashMap, map of item variants
	 */
	public Map<String, String> getItemVariantMap();

	/**
	 * Returns the map, which stores the header resp. item vs. shipToKey relation
	 * 
	 * @return Map
	 */
	public Map<String, ShipTo> getShipToMap();

	/**
	 * @return List of saved (already existing) items of the order
	 */
	public Set<String> getSavedItemsMap();

	/**
	 * Removes the given message from the MessageList in the MessageBufferMap for the given key, if present
	 * 
	 * @param techKey
	 *           technical of the business object the message belongs to
	 * @param resourceKey
	 *           resource key of the message
	 */
	public void removeMessageFromMessageList(TechKey techKey, String resourceKey);

	/**
	 * Returns the MessageList for the given key, if one exits, if not create one
	 * 
	 * @param key
	 *           technical key identifying head or item object
	 * @return the MessageList for the given TechKey or a default MessageList, if the key is null or initial
	 */
	public MessageList getOrCreateMessageList(TechKey key);

	/**
	 * Set the error state read from the backend
	 * 
	 * @param b
	 *           true if ERRKZ is 'X', otherwise false
	 */
	public void setErroneous(boolean b);

	/**
	 * Sets handle of soldTo partner
	 * 
	 * @param soldTo
	 *           handle of the soldto partner function
	 */
	public void setSoldToHandle(String soldTo);

	/**
	 * Sets handle of payer
	 * 
	 * @param payer
	 *           handle of the payer partner function
	 */
	public void setPayerHandle(String payer);

	/**
	 * @return handle of the soldTo partner
	 */
	public String getSoldToHandle();

	/**
	 * @return handle of the payer
	 */
	public String getPayerHandle();

	/**
	 * Sales document is initial. The first update might need to do specific shipto handling
	 * 
	 * @param isInitial
	 *           true if document is initial
	 */
	public void setDocumentInitial(boolean isInitial);

	/**
	 * @return Sales document is initial
	 */
	public boolean isDocumentInitial();

	/**
	 * Returns stored shipping condition. We need this to decide whether a pricing update is necessary.<br>
	 * 
	 * @return Shipping condition
	 */
	public String getShippingCondition();

	/**
	 * Sets stored shipping condition. We need this to decide whether a pricing update is necessary.<br>
	 * 
	 * @param currentShippingCondition
	 *           Shipping condition
	 */
	public void setShippingCondition(String currentShippingCondition);

	/**
	 * Stores an erroneous item to be later able to restore its attrtibutes<br>
	 * 
	 * @param item
	 *           The item (including message list, productId which we need to restore
	 */
	public void addErroneousItem(Item item);

	/**
	 * Returns the previously stored information about erroneous items. Even if the sales document items are technically
	 * ok and can be sent via LO-API, this list holds the information which causes the trouble, and which needs to be put
	 * to the items after read to persist the user entry <br>
	 * 
	 * @return Map for all erroneous items
	 */
	public Map<TechKey, Item> getErroneousItems();

	/**
	 * Returns the previously stored information about the erroneous header.<br>
	 * 
	 * @return Sales document header
	 */
	public Header getErroneousHeader();

	/**
	 * Sets erroneous header.<br>
	 * 
	 * @param header
	 *           Sales document header
	 */
	public void setErroneousHeader(Header header);

	/**
	 * Clear list of erroneous items<br>
	 */
	public void clearErroneousItems();

	/**
	 * Remove entries from list of error restore information .<br>
	 * 
	 * @param itemsToDelete
	 */
	public void removeErroneousItems(TechKey[] itemsToDelete);

}
