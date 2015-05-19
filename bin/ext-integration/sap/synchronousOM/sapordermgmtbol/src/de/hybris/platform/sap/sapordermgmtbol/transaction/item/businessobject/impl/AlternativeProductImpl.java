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
package de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.AlternativeProduct;

/**
 * Class to define a alternative product, that might be an alternative product
 * found by product determination or substitution
 * 
 * @version 1.0
 */
public class AlternativeProductImpl implements AlternativeProduct {

	/**
	 * Product ID
	 */
	protected String systemProductId;
	/**
	 * Product GUID (in ERP case identical to ID)
	 */
	protected TechKey systemProductGUID;
	/**
	 * Entered Product ID
	 */
	protected String enteredProductIdType;
	/**
	 * Product description
	 */
	protected String description;
	/**
	 * Reason for substitution
	 */
	protected String substitutionReasonId;

	/**
	 * Returns the system product id of the AlternativProduct
	 * 
	 * @return systemProductId of AlternativProduct
	 */
	@Override
	public String getSystemProductId() {
		return systemProductId;
	}

	/**
	 * Set the system product id of the AlternativProduct
	 * 
	 * @param systemProductId
	 *            system product id of AlternativProduct
	 */
	@Override
	public void setSystemProductId(final String systemProductId) {
		this.systemProductId = systemProductId;
	}

	/**
	 * Returns the description of the AlternativProduct
	 * 
	 * @return String
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the enteredProductIdType of the AlternativProduct.
	 * 
	 * @return String
	 */
	@Override
	public String getEnteredProductIdType() {
		return enteredProductIdType;
	}

	/**
	 * Returns the substitutionReasonId of the AlternativProduct.
	 * 
	 * @return String
	 */
	@Override
	public String getSubstitutionReasonId() {
		return substitutionReasonId;
	}

	/**
	 * Returns the systemProductGUID of the AlternativProduct.
	 * 
	 * @return TechKey
	 */
	@Override
	public TechKey getSystemProductGUID() {
		return systemProductGUID;
	}

	/**
	 * Sets the description of the AlternativProduct.
	 * 
	 * @param description
	 *            The description of the AlternativProduct
	 */
	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Sets the enteredProductIdType of the AlternativProduct.
	 * 
	 * @param enteredProductIdType
	 *            The enteredProductIdType of the AlternativProduct
	 */
	@Override
	public void setEnteredProductIdType(final String enteredProductIdType) {
		this.enteredProductIdType = enteredProductIdType;
	}

	/**
	 * Sets the substitutionReasonId of the AlternativProduct.
	 * 
	 * @param substitutionReasonId
	 *            The substitutionReasonId of the AlternativProduct
	 */
	@Override
	public void setSubstitutionReasonId(final String substitutionReasonId) {
		this.substitutionReasonId = substitutionReasonId;
	}

	/**
	 * Sets the systemProductGUID of the AlternativProduct.
	 * 
	 * @param systemProductGUID
	 *            The systemProductGUID of the AlternativProduct
	 */
	@Override
	public void setSystemProductGUID(final TechKey systemProductGUID) {
		this.systemProductGUID = systemProductGUID;
	}

	/**
	 * Performs a deep copy of this object. Because of the fact that all fields
	 * of this object consist of immutable objects like <code>String</code> and
	 * <code>TechKey</code> or primitive types the shallow copy is identical
	 * with a deep copy.
	 * 
	 * @return deep copy of this object
	 */
	@Override
	public Object clone() {
		AlternativeProductImpl myClone;
		try {
			// will copy all primitives, references
			myClone = (AlternativeProductImpl) super.clone();
			// TechKey is immutable, so no need to copy
		} catch (final CloneNotSupportedException ex) {
			// should not happen, because we are cloneable
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented",
					ex);

		}
		return myClone;

	}

	/**
	 * returns true, if the alternativ product is a substitute product
	 * 
	 * @return boolean true if the product alias is a substitute product
	 */
	public boolean isSubstituteProduct() {
		return (substitutionReasonId != null
				&& substitutionReasonId.length() > 0 && !isDeterminationProduct());
	}

	/**
	 * returns true, if the alternativ product is a determination product
	 * 
	 * @return boolean true if the product alias is a determination product
	 */
	public boolean isDeterminationProduct() {
		return (enteredProductIdType != null && enteredProductIdType.length() > 0);
	}

}
