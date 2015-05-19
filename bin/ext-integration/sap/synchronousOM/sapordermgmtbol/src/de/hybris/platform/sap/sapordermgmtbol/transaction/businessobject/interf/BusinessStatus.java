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

/**
 * Represents the BusinessStatus object. <br>
 * 
 */
public interface BusinessStatus extends Cloneable
{

	/**
	 * Returns status.<br>
	 * 
	 * @return status
	 */
	public EStatus getStatus();

	/**
	 * Returns true if the status is "Not Relevant".<br>
	 * 
	 * @return true if the status is "Not Relevant"
	 */
	public boolean isNotRelevant();

	/**
	 * Returns true if the status is "Not Processed". which is the same as Open.<br>
	 * 
	 * @return true if the status is "Not Processed"
	 */
	public boolean isNotProcessed();

	/**
	 * Returns true if the status is "Partially Processed".<br>
	 * 
	 * @return true if the status is "Partially Processed"
	 */
	public boolean isPartiallyProcessed();

	/**
	 * Returns true if the status is "Processed".<br>
	 * 
	 * @return true if the status is "Processed"
	 */
	public boolean isProcessed();

	/**
	 * Initializes the BusinessStatus object.<br>
	 * 
	 * @param dlvStatus
	 *           Delivery Status
	 * @param rjStatus
	 *           Rejection Status
	 */
	public void init(EStatus dlvStatus, EStatus rjStatus);

	/**
	 * Initializes the BusinessStatus object.<br>
	 * 
	 * @param key
	 *           - status
	 */
	public void init(EStatus key);

	/**
	 * Initializes the BusinessStatus object.<br>
	 */
	public void init();

	/**
	 * Performs a deep-copy of the object rather than a shallow copy.
	 * 
	 * @return returns a deep copy
	 */
	public Object clone();

}