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
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.SalesDocumentBackend;


/**
 * Base for OrderBackend and OrderHistoryBackend
 * 
 */
public interface OrderBaseBackend extends SalesDocumentBackend
{

	/**
	 * Saves a basket in the back end as an order object.<br>
	 * 
	 * @param ordr
	 *           Order BO to be saved
	 * @param commit
	 *           if <code>true</code> commit in the back end will be triggered.
	 * @return <code>true</code> if the save in the back end was successful.
	 * @throws BackendException
	 *            in case of a back-end error
	 */
	boolean saveInBackend(Order ordr, boolean commit) throws BackendException;

	/**
	 * Checks if the order is cancelable, different logic is implemented for the different back ends.
	 * 
	 * @param order
	 *           Order which is checked
	 * @return true if order is cancelable
	 */
	boolean isCancelable(Order order);

}