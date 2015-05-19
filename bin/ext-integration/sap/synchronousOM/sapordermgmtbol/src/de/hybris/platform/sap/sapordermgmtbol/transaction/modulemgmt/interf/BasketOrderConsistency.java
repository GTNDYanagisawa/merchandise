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
package de.hybris.platform.sap.sapordermgmtbol.transaction.modulemgmt.interf;

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;


/**
 * Ensures consistency basket<->order <br>
 * 
 * @version 1.0
 */
public interface BasketOrderConsistency
{

	/**
	 * Sets the order BO. Needed for telling whether basket and order are linked, and for updating basket from order <br>
	 * 
	 * @param order
	 *           order the basket is linked to
	 */
	void setOrder(Order order);

	/**
	 * Is basket linked to order? Only true for external baskets<br>
	 * 
	 * @param basket
	 *           the basket to be checked
	 * @return true if basket and order are linked
	 */
	boolean isBasketLinkedToOrder(Basket basket);

	/**
	 * Sets an internal flag to indicate that the synchronisation from the order is needed<br>
	 * 
	 * @param aIsSyncFromOrderNeeded
	 *           to or no to synchronise
	 */
	void setSyncFromOrderNeeded(boolean aIsSyncFromOrderNeeded);

	/**
	 * Sets an internal flag to indicate that an update call is missing.<br>
	 */
	void setBasketUpdateMissing();

	/**
	 * Synchronises basket from order if necessary.
	 * 
	 * @param basket
	 *           the basket to be checked
	 * @return true if the data was not consistent and a basket update is necessary
	 */
	boolean assureConsistentData(Basket basket);

	/**
	 * Checks whether all pending updates in the basket have been sent to the Backend Object.<br>
	 * 
	 * @return <code>true</code>, only if an update is missing
	 */
	boolean isUpdateMissing();

}
