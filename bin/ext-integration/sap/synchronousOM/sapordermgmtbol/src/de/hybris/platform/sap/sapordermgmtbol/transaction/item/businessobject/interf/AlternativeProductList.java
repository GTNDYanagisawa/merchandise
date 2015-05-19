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

import java.util.List;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapcommonbol.transaction.businessobject.transfer.interf.SimpleListAccess;


/**
 * Represents the backend's view of List of AlternativProducts.<br>
 * In some cases the back end may return a list of alternatives for a entered product, for example if the product was
 * not unique, or if several substitutions are possible.
 * 
 * @version 1.0
 */
public interface AlternativeProductList extends SimpleListAccess<AlternativeProduct>, Cloneable
{

	/**
	 * Creates an empty <code>AlternativProductData</code> for the basket.
	 * 
	 * @return AlternativProduct which can added to the alternativProductList
	 */
	public AlternativeProduct createAlternativProduct();

	/**
	 * Creates a initialised <code>AlternativProductData</code> for the basket.
	 * 
	 * @param systemProductId
	 *           id of the system product
	 * @param systemProductGUID
	 *           techkey of the system product
	 * @param description
	 *           description of the system product
	 * @param enteredProductIdType
	 *           if the system product was found through determination, this specifies, as what the entred product id was
	 *           interpreted
	 * @param substitutionReasonId
	 *           if the system product is a substitute product, this is the id for the substitution reason
	 * @return AlternativProduct which can added to the AlternativProductList
	 */
	public AlternativeProduct createAlternativProduct(String systemProductId, TechKey systemProductGUID, String description,
			String enteredProductIdType, String substitutionReasonId);

	/**
	 * clear the alternativ product list
	 */
	public void clear();

	/**
	 * get the size of the list of alternative products
	 */
	@Override
	public int size();

	/**
	 * returns true if the alternative product list is empty
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isEmpty();

	/**
	 * sets the alternative product for the given index
	 * 
	 * @param i
	 *           index to set the product
	 * @param altProd
	 *           the alternative product for the given index, or null if index is out of bounds
	 */
	public void addAlternativProduct(int i, AlternativeProduct altProd);

	/**
	 * sets the alternative product for the given index
	 * 
	 * @param altProd
	 *           the alternative product for the given index, or null if index is out of bounds
	 */
	public void addAlternativProduct(AlternativeProduct altProd);

	/**
	 * Creates and adds an alternative Product to the ProductAliasList
	 * 
	 * @param systemProductId
	 *           id of the system product
	 * @param systemProductGUID
	 *           techkey of the system product
	 * @param description
	 *           description of the system product
	 * @param enteredProductIdType
	 *           if the system product was found through determination, this specifies, as what the entered product id
	 *           was interpreted
	 * @param substitutionReasonId
	 *           if the system product is a substitute product, this is the id for the substitution reason
	 */
	public void addAlternativProduct(String systemProductId, TechKey systemProductGUID, String description,
			String enteredProductIdType, String substitutionReasonId);

	/**
	 * sets the alternative product list
	 * 
	 * @param altProdList
	 *           List new list of alternative products
	 */
	public void setList(List<AlternativeProduct> altProdList);

	/**
	 * Performs a deep copy of this object. Hence the List of alternative products will be duplicated.
	 * 
	 * @return deep copy of this object
	 */
	public Object clone();
}