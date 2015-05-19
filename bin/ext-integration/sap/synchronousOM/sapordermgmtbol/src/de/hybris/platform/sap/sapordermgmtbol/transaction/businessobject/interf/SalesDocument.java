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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;

import java.util.List;


/**
 * Common interface representing sales documents like Basket or Order
 * 
 */
public interface SalesDocument extends SalesDocumentBase<ItemList, Item, Header>
{

	/**
	 * Clears the list of the shipTo.
	 */
	public void clearShipTos();

	/**
	 * Creates a new billTo object
	 * 
	 * @return Newly created billTo
	 */
	public BillTo createBillTo();

	/**
	 * Creates a new ship object
	 * 
	 * @return Newly created shipTo
	 */
	public ShipTo createShipTo();

	/**
	 * Deletes items from the sales document.
	 * 
	 * @param techKeys
	 *           Technical keys of the items to be canceld
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void removeItems(TechKey[] techKeys) throws CommunicationException;

	/**
	 * Destroy the data of the document (items and header) in the backend representation. After a call to this method,
	 * the object is in an undefined state. You have to call init() before you can use it again.<br>
	 * This method is normally called before removing the BO-representation of the object using the BOM.
	 * 
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void destroyContent() throws CommunicationException;

	/**
	 * Returns the type of the sales document (e.g. Order, Basket)
	 * 
	 * @return type of sales document
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public SalesDocumentType getDocumentType() throws CommunicationException;



	/**
	 * Returns the transaction configuration. The transaction configuration provides access to sales-specific settings
	 * (e.g. sales organisation)
	 * 
	 * @return TransactionConfiguration to access the sales-related configuration
	 */
	public TransactionConfiguration getTransactionConfiguration();

	/**
	 * Initializes the sales document with the given arguments and creates and instance in back-end application memory.
	 * 
	 * @param partnerList
	 *           any partners maintained will be added to the sales document in the same partner function
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void init(PartnerList partnerList) throws CommunicationException;

	/**
	 * Initializes an empty instance of this object with the data coming from the provided document. The former state of
	 * this document is dropped and lost, the fields of the other document are copied into the fields of this document.
	 * To do this the clone() method of the fields is used to get an shallow copy and minimise interference between the
	 * new and the old object. The creation date of the old document is not copied to the new one but the original date
	 * is left as it is. A back-end representation of the new object is created and filled with the data retrieved by the
	 * copy operation.
	 * 
	 * @param posd
	 *           The SalesDocument to retrieve data from
	 * @param processType
	 *           process type for the new SalesDocument (optional)
	 * @throws BusinessObjectException
	 *            in case back-end error
	 */
	public void init(SalesDocument posd, String processType) throws BusinessObjectException;

	/**
	 * Checks if the document exists in the backend storage
	 * 
	 * @return does the document exist in backend?
	 */
	public boolean isExistingInBackend();

	/**
	 * Indicates whether or not the order has to be maintained as an external document to this one. (e.g. java basket is
	 * external to crm backend order)
	 * 
	 * @return true indicates that the order is maintained external to this document, false indicates that the order lies
	 *         on the same system.
	 */
	public boolean isExternalToOrder();

	/**
	 * Indicates whether or not the sales document can provide the Gross value.
	 * 
	 * @return true indicates that the sales document can provide the gross value. false indicates that the sales
	 *         document can not deliver the gross value.
	 */
	public boolean isGrossValueAvailable();

	/**
	 * Returns if multiple addresses are supported
	 * 
	 * @return multiple addresses supported
	 * @throws BusinessObjectException
	 *            in case back-end error
	 */
	public boolean isMultipleAddressesSupported() throws BusinessObjectException;

	/**
	 * Indicates whether or not the sales document can provide a net value.
	 * 
	 * @return true indicates that the sales document can provide the net value false indicates that the sales document
	 *         can not deliver the net value.
	 */
	public boolean isNetValueAvailable();

	/**
	 * Reads the sales document. The backend call happens only if necessary.
	 * 
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void read() throws CommunicationException;

	/**
	 * Reads the sales document. The backend call happens if considered as necessary or if forced.
	 * 
	 * @param force
	 *           If true, then read even if not considered as necessary
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void read(boolean force) throws CommunicationException;

	/**
	 * Reads the sales document for update (tried to get a lock). The backend call happens only if necessary.
	 * 
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void readForUpdate() throws CommunicationException;



	/**
	 * Reads the sales document for update (tried to get a lock). The backend call happens if considered as necessary or
	 * if forced.
	 * 
	 * @param force
	 *           If true, then read even if not considered as necessary
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void readForUpdate(boolean force) throws CommunicationException;

	/**
	 * Removes the item.
	 * 
	 * @param item
	 *           to remove
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void removeItem(Item item) throws CommunicationException;


	/**
	 * Save the sales document and commit it in the backend. If successful, locks in backend are released
	 * 
	 * @throws BusinessObjectException
	 *            in case back-end error
	 * @return true if the save and commit was successful
	 */
	public boolean saveAndCommit() throws BusinessObjectException;

	/**
	 * Sets whether or not the order has to be maintained as an external document to this one. (e.g. a java basket is an
	 * external document to crm backend order)
	 * 
	 * @param isExternalToOrder
	 *           true indicates that the order is maintained external to this document, false indicates that the order
	 *           lies on the same system.
	 */
	public void setExternalToOrder(boolean isExternalToOrder);

	/**
	 * Sets the property grossValueAvailable
	 * 
	 * @param grossValueAvailable
	 *           true indicates that the sales document can provide the gross value <code>false</code> indicates that the
	 *           sales document can not provide the gross value.
	 */
	public void setGrossValueAvailable(boolean grossValueAvailable);

	/**
	 * Sets the property netValueAvailable
	 * 
	 * @param netValueAvailable
	 *           true indicates that the sales document can deliver the net value false indicates that the sales document
	 *           can not deliver the net value.
	 */
	public void setNetValueAvailable(boolean netValueAvailable);

	/**
	 * Sets shipTo list
	 * 
	 * @param shipToList
	 *           list of ShipTo's
	 */
	public void setShipToList(List<ShipTo> shipToList);

	/**
	 * Sets BillTo list
	 * 
	 * @param billToList
	 *           list of BillTo's
	 */
	public void setBillToList(List<BillTo> billToList);

	/**
	 * The transaction configuration provides access to sales-specific settings (e.g. sales organisation)
	 * 
	 * @param transConf
	 *           API to access the configuration and documents
	 */
	public void setTransactionConfiguration(TransactionConfiguration transConf);

	/**
	 * Sets whether update before save is required. Has to be set to true for initial objects to ensure that update is
	 * called at least once before save.
	 * 
	 * @param updateMissing
	 *           set to <code>true</code> to force an update before next save
	 */
	public void setUpdateMissing(boolean updateMissing);

	/**
	 * Check whether its required to Update the object<br>
	 * 
	 * @return <code>true</code>, only if the object has to be updated
	 */
	public boolean isUpdateMissing();

	/**
	 * Update the sales document in the backend. The method also checks for changes in the businesspartner list
	 * 
	 * @throws CommunicationException
	 *            in case back-end error
	 */
	public void update() throws CommunicationException;

	/**
	 * Gets a new Item object<br>
	 * 
	 * @return new item instance
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocumentBase#createItem()
	 */
	@Override
	public Item createItem();




	/**
	 * @param techKeySoldTo
	 *           the guid of the soldto
	 * @param soldToId
	 *           the partner id of the soldto
	 */
	public void setSoldToGuid(TechKey techKeySoldTo, String soldToId);

	/**
	 * Removes all buffered item information
	 */
	public void clearItemBuffer();

	/**
	 * Scans list of predecessors for given document type.<br>
	 * 
	 * @param docType
	 *           to be searched for in predecessors
	 * @return true if predecessor list contains given doc type
	 */
	public boolean hasPredecessorOfSpecificType(DocumentType docType);

	/**
	 * Indicates whether a sales document could be initialized
	 * 
	 * @param b
	 *           true if initialization was not possible
	 */
	public void setInitialized(boolean b);

	/**
	 * Does the document carry an initialization error?
	 * 
	 * @return true if initialization was not possible
	 */
	public boolean isInitialized();

	/**
	 * Does the document require that the items are checked against the catalog?
	 * 
	 * @return true if check is required
	 */
	boolean isCheckCatalogNecessary();

	/**
	 * Indicates whether a check of the items against the catalog is required
	 * 
	 * @param checkCatalogNecessary
	 *           if check is required
	 */
	void setCheckCatalogNecessary(boolean checkCatalogNecessary);


	/**
	 * Checks if availability is compiled only based on item information. <br>
	 * 
	 * @return Availability is compiled only based item on information?
	 */
	public boolean isItemBasedAvailability();

	/**
	 * @param converter
	 *           access object for unit related conversions
	 */
	public void setConverter(Converter converter);

	/**
	 * Validates as sales document. Afterwards, all messages are available as part of the message list
	 * 
	 * @throws CommunicationException
	 */
	void validate() throws CommunicationException;


	/**
	 * Stores config ID per item in session
	 * 
	 * @param key
	 *           Item key
	 * @param configId
	 *           Configuration ID
	 */
	void setConfigId(TechKey key, String configId);

	/**
	 * Releases all configuration sessions attached to this sales document
	 */
	void releaseConfigurationSessions();

	/**
	 * @return Is back end down for a planned downtime?
	 */
	boolean isBackendDown();

}
