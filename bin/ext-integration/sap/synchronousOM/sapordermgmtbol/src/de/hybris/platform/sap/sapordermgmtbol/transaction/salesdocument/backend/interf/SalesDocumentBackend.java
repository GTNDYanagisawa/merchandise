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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.SalesDocumentType;

import java.util.List;


/**
 * Common interface for the back-end implementation of all sales documents (i.e. <code>Order</code> and
 * <code>Basket</code>). This grouping may not be suitable for all possible back-end/front-end scenarios but saves a lot
 * of implementation effort for the default scenario, where the only back end system is the CRM.<br>
 * <br>
 * <b>Note -</b> There was a design decision to give all back-end interfaces and their implementors a
 * <em>stateless-like</em> behaviour. This means that the back-end objects are not truly stateless because the
 * connection management of the actual used back-end systems cannot support this at all, but the method signatures are
 * similar to stateless objects. This feature forces you to provide a reference to the object the back-end method should
 * work on as a method parameter. This may seem a little bit complicated, but it allows us to migrate to an EJB-Solution
 * with real stateless session beans very easy.
 * 
 */
public interface SalesDocumentBackend extends BackendBusinessObject
{



	/**
	 * Creates a back-end representation of this object in the back end.
	 * 
	 * @param transactionConfiguration
	 *           Transaction Configuration object that holds the configuration settings
	 * @param salesDocument
	 *           The sales document object contains data to be used for creating sales document in the back-end
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void createInBackend(TransactionConfiguration transactionConfiguration, SalesDocument salesDocument) throws BackendException;

	/**
	 * Deletes one item of the document in the underlying storage. The document in the business object layer is not
	 * changed at all.
	 * 
	 * @param salesDocument
	 *           document to delete item from
	 * @param itemToDelete
	 *           item technical key that is going to be deleted
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void deleteItemInBackend(SalesDocument salesDocument, TechKey itemToDelete) throws BackendException;

	/**
	 * Deletes list of items from the underlying storage. The document in the business object layer is not changed at
	 * all.
	 * 
	 * @param salesDocument
	 *           Document to delete item from
	 * @param itemsToDelete
	 *           Array of item keys that are going to be deleted
	 * @param transConf
	 *           TransactionConfiguration to delete items dependent on shop settings
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void deleteItemsInBackend(SalesDocument salesDocument, TechKey[] itemsToDelete, TransactionConfiguration transConf)
			throws BackendException;

	/**
	 * Empties the representation of the provided object in the underlying storage. This means that all items and the
	 * header information are cleared. The provided document itself is not changed, so you are responsible for clearing
	 * the data representation on the business object layer on your own.
	 * 
	 * @param salesDocument
	 *           the document to remove the representation in the storage
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void emptyInBackend(SalesDocument salesDocument) throws BackendException;



	/**
	 * Each implementing class has to define the used sales document type. This is e.g. used for finding the right
	 * strategies.
	 * 
	 * @return sales document type
	 */
	SalesDocumentType getSalesDocumentType();

	/**
	 * Checks in the back end whether the multiple addresses are supported.<br>
	 * 
	 * @return <code>true</code> when the back end supports the multiple addresses
	 */
	boolean isMultipleAddressesSupported();

	/**
	 * Reads the sales document from the back end and lock this document, so that it can be modified.
	 * 
	 * @param salesDocument
	 *           Sales Document
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void readForUpdateFromBackend(SalesDocument salesDocument) throws BackendException;

	/**
	 * Reads Data from the back end. Every document consists of two parts: the header and the items. This method
	 * retrieves the header and the item information. If a soldTo is set in the partner list, ship to information should
	 * be read
	 * 
	 * @param salesDocument
	 *           Sales Document to be read
	 * @param transactionConfiguration
	 *           Transaction Configuration object that holds the configuration settings
	 * @param directRead
	 *           In case other sessions/channels update the sales document in parallel, it is only guaranteed that the
	 *           most recent data is returned if direct read is set to <code>true</code>, otherwise the data might be
	 *           returned from a buffer.
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void readFromBackend(SalesDocument salesDocument, TransactionConfiguration transactionConfiguration, boolean directRead)
			throws BackendException;



	/**
	 * Recovers the whole document for the given TechKey from the back end. Before calling this method, it should be
	 * checked if the back end is capable of recovering a document. This can be done by calling checkRecoveryInBackend.
	 * So far the recovery feature is only available for the java basket (see SalesDocumentDB), it is used during startup
	 * to recover a basket via a cookie (see BasketCookieHandler).
	 * 
	 * @param salesDoc
	 *           The document to load the data in.
	 * @param basketGuid
	 *           The TechKey of the basket to read
	 * @return success <code>true</code> when the recovering is successful
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	boolean recoverFromBackend(SalesDocument salesDoc, TechKey basketGuid) throws BackendException;

	/**
	 * Sets the global data in the back end. Use this action, if you don't know the soldTo of the sales document.
	 * 
	 * @param salesDoc
	 *           The salesDoc to set the data to
	 * @param transactionConfiguration
	 *           Transaction configuration
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void setGData(SalesDocument salesDoc, TransactionConfiguration transactionConfiguration) throws BackendException;

	/**
	 * Copies back end data from basket to the order.<br>
	 */
	void setLoadStateCreate();

	/**
	 * Updates object in the back end without supplying extra information about the transactionConfiguration. This method
	 * is normally necessary only for the B2C scenario.<br>
	 * <i>Note: For a correct support of the business event AddToDocument the techkey of the items should be filled from
	 * the back end. </i>
	 * 
	 * @param salesDocument
	 *           the object to update
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void updateInBackend(SalesDocument salesDocument) throws BackendException;

	/**
	 * Updates object in the back end by putting the data into the underlying storage. <br>
	 * <i>Note: For a correct support of the business event AddToDocument the techkey of the items should be filled from
	 * the back end. </i>
	 * 
	 * @param salesDocument
	 *           the document to update
	 * @param transactionConfiguration
	 *           Transaction Configuration object that holds the configuration settings
	 * @throws BackendException
	 *            This exception is thrown if an error occurs in the back end
	 */
	void updateInBackend(SalesDocument salesDocument, TransactionConfiguration transactionConfiguration) throws BackendException;

	/**
	 * Updates object in the back end by putting the data into the underlying storage. <br>
	 * <i>Note: For a correct support of the business event AddToDocument the techkey of the items should be filled from
	 * the back end. </i>
	 * 
	 * @param salesDocument
	 *           the document to update
	 * @param transactionConfiguration
	 *           Transaction Configuration object that holds the configuration settings
	 * @param itemsToDelete
	 *           List of techkeys of the items to be deleted in the back end
	 * @throws BackendException
	 *            This exception is thrown if an error occurs in the back end
	 */

	void updateInBackend(SalesDocument salesDocument, TransactionConfiguration transactionConfiguration,
			List<TechKey> itemsToDelete) throws BackendException;

	/**
	 * Removes all buffered item information
	 */
	void clearItemBuffer();




	/**
	 * Checks if availability is compiled only based on item information. <br>
	 * 
	 * @return Availability is compiled only based on item information?
	 */
	boolean isItemBasedAvailability();

	/**
	 * Validates a sales document. Validation messages should be attached to salesDocument after validation
	 * 
	 * @param salesDocument
	 * @throws BackendException
	 */
	void validate(SalesDocument salesDocument) throws BackendException;

	/**
	 * @return Is back end in planned downtime?
	 */
	boolean isBackendDown();

	/**
	 * Closes sales document in backend
	 */
	void closeBackendSession();

}