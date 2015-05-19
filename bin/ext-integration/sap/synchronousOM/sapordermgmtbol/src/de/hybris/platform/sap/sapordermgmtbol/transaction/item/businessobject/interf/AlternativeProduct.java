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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf;

import de.hybris.platform.sap.core.common.TechKey;


/**
 * Represents the backend's view of a AlternativProduct.
 * 
 * @version 1.0
 */
public interface AlternativeProduct extends Cloneable
{

	/**
	 * Returns the system product id of the AlternativProduct
	 * 
	 * @return systemProductId of AlternativProduct
	 */
	public String getSystemProductId();

	/**
	 * Set the system product id of the AlternativProduct
	 * 
	 * @param systemProductId
	 *           system product id of AlternativProduct
	 */
	public void setSystemProductId(String systemProductId);

	/**
	 * Returns the description of the AlternativProduct
	 * 
	 * @return String
	 */
	public String getDescription();

	/**
	 * Returns the enteredProductIdType of the AlternativProduct.
	 * 
	 * @return String
	 */
	public String getEnteredProductIdType();

	/**
	 * Returns the substitutionReasonId of the AlternativProduct.
	 * 
	 * @return String
	 */
	public String getSubstitutionReasonId();

	/**
	 * Returns the systemProductGUID of the AlternativProduct.
	 * 
	 * @return TechKey
	 */
	public TechKey getSystemProductGUID();

	/**
	 * Sets the description of the AlternativProduct.
	 * 
	 * @param description
	 *           The description of the AlternativProduct
	 */
	public void setDescription(String description);

	/**
	 * Sets the enteredProductIdType of the AlternativProduct.
	 * 
	 * @param enteredProductIdType
	 *           The enteredProductIdType of the AlternativProduct
	 */
	public void setEnteredProductIdType(String enteredProductIdType);

	/**
	 * Sets the substitutionReasonId of the AlternativProduct.
	 * 
	 * @param substitutionReasonId
	 *           The substitutionReasonId of the AlternativProduct
	 */
	public void setSubstitutionReasonId(String substitutionReasonId);

	/**
	 * Sets the systemProductGUID of the AlternativProduct.
	 * 
	 * @param systemProductGUID
	 *           The systemProductGUID of the AlternativProduct
	 */
	public void setSystemProductGUID(TechKey systemProductGUID);

	/**
	 * Performs a deep copy of this object.
	 * 
	 * @return deep copy of this object
	 */
	public Object clone();
}