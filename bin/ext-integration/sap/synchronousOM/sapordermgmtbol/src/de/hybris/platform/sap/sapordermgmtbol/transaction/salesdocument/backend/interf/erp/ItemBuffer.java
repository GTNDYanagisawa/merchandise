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

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.Map;

/**
 * Helper Interface to Buffer Item data, typically used by ERP backend
 * implementation.<br>
 * 
 */
public interface ItemBuffer {

	/**
	 * The list of items which represents the ERP status. We use it to compile a
	 * delta to the status we get from the BO layer. Aim is to optimise the
	 * LO-API call
	 * 
	 * @return list of items
	 */
	public Map<String, Item> getItemsERPState();

	/**
	 * The list of items which represents the ERP status. We use it to compile a
	 * delta to the status we get from the BO layer. Aim is to optimise the
	 * LO-API call
	 * 
	 * @param itemsERPState
	 *            list of items
	 */
	public void setItemsERPState(Map<String, Item> itemsERPState);

	/**
	 * Removes item from ERP state map, together with sub items (free goods!)
	 * 
	 * @param idAsString
	 *            item ID
	 */
	public void removeItemERPState(String idAsString);

	/**
	 * Clears the buffer for the ERP state of the document.
	 */
	public void clearERPBuffer();

}