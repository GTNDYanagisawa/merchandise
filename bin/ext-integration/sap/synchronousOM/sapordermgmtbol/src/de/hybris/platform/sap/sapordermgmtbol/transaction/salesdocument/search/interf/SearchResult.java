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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf;

import de.hybris.platform.sap.core.common.TechKey;

import java.util.Date;


/**
 * BOL representation of an order search result
 */
public interface SearchResult
{

	/**
	 * Overall status open: A
	 */
	String OVERALL_STATUS_OPEN = "A";
	/**
	 * Shipping status not shipped: A
	 */
	String SHIPPING_NOT_SHIPPED = "A";
	/**
	 * Shipping status partially shipped: B
	 */
	String SHIPPING_PART_SHIPPED = "B";
	/**
	 * Shipping status shipping complete: C
	 */
	String SHIPPING_COMPLETE = "C";

	/**
	 * Sets key of sales document
	 * 
	 * @param key
	 *           Key of search result item
	 */
	void setKey(TechKey key);

	//

	/**
	 * Returns key of sales document
	 * 
	 * @return Key of search result item
	 */
	TechKey getKey();

	/**
	 * Sets purchase order number of sales document
	 * 
	 * @param poNr
	 */
	void setPurchaseOrderNumber(String poNr);

	/**
	 * Returns purchase order number of sales document result item
	 * 
	 * @return Purchase Order number from SD
	 */
	String getPurchaseOrderNumber();

	/**
	 * Return overall status
	 * 
	 * @return Overall status of sales document
	 */
	String getOverallStatus();

	/**
	 * Sets overall status of salesdocument search item
	 * 
	 * @param overallStatus
	 */
	void setOverallStatus(String overallStatus);

	/**
	 * Sets shipping status
	 * 
	 * @param shippingStatus
	 */
	void setShippingStatus(String shippingStatus);

	/**
	 * Returns shipping status of sales document search result item
	 * 
	 * @return Shipping status
	 */
	String getShippingStatus();

	/**
	 * Returns creation date
	 * 
	 * @return Creation date of sales document
	 */
	Date getCreationDate();

	/**
	 * Sets creation date
	 * 
	 * @param date
	 *           Date of creation of sales document
	 */
	void setCreationDate(Date date);
}
