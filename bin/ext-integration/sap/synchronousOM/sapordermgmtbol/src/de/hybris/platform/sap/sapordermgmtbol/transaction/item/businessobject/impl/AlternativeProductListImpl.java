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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.AlternativeProduct;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.AlternativeProductList;

/**
 * List of alternative products
 * 
 * @version 1.0
 */
public class AlternativeProductListImpl implements AlternativeProductList {

	/**
	 * List of alternative products
	 */
	protected ArrayList<AlternativeProduct> alternativProductList;

	/**
	 * Factory to access SAP session beans
	 */
	protected GenericFactory genericFactory;

	/**
	 * @param genericFactory
	 *            Factory to access SAP session beans
	 */
	public void setGenericFactory(final GenericFactory genericFactory) {
		this.genericFactory = genericFactory;
	}

	/**
	 * Creates a new <code>AlternativProductList</code> object.
	 */
	public AlternativeProductListImpl() {
		alternativProductList = new ArrayList<AlternativeProduct>();
	}

	/**
	 * Creates an empty <code>AlternativProductData</code>.
	 * 
	 * @returns AlternativProduct which can added to the AlternativProductList
	 */
	@Override
	public AlternativeProduct createAlternativProduct() {
		return (AlternativeProduct) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ALTERNATIVE_PRODUCT);
	}

	/**
	 * Creates a initialised <code>AlternativProductData</code> for the basket.
	 * 
	 * @param systemProductId
	 *            id of the system product
	 * @param systemProductGUID
	 *            techkey of the system product
	 * @param description
	 *            description of the system product
	 * @param enteredProductIdType
	 *            if the system product was found through determination, this
	 *            specifies, as what the entred product id was interpreted
	 * @param substitutionReasonId
	 *            if the system product is a substitute product, this is the id
	 *            for the substitution reason
	 * @returns AlternativProduct which can added to the AlternativProductList
	 */
	@Override
	public AlternativeProduct createAlternativProduct(
			final String systemProductId, final TechKey systemProductGUID,
			final String description, final String enteredProductIdType,
			final String substitutionReasonId) {

		final AlternativeProduct alternProduct = (AlternativeProduct) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ALTERNATIVE_PRODUCT);

		alternProduct.setSystemProductGUID(systemProductGUID);
		alternProduct.setDescription(description);
		alternProduct.setEnteredProductIdType(enteredProductIdType);
		alternProduct.setSubstitutionReasonId(substitutionReasonId);

		return alternProduct;

	}

	/**
	 * clear the alternativ product list
	 */
	@Override
	public void clear() {
		alternativProductList.clear();
	}

	/**
	 * get the size of the list of alternativ products
	 */
	@Override
	public int size() {
		return alternativProductList.size();
	}

	/**
	 * returns true if the alternativ product list is empty
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isEmpty() {
		return alternativProductList.isEmpty();
	}

	/**
	 * Returns a string representation of the object.
	 * 
	 * @return object as string
	 */
	@Override
	public String toString() {
		return alternativProductList.toString();
	}

	/**
	 * get the alternative product list
	 * 
	 * @return List list of alternative products
	 */
	public List<AlternativeProduct> getList() {
		return alternativProductList;
	}

	/**
	 * gets the alternative product for the given index
	 * 
	 * @param i
	 *            index of the product
	 * @return AlternativProduct the alternative product for the given index, or
	 *         null if index is out of bounds
	 */
	public AlternativeProductImpl getAlternativProduct(final int i) {
		return (AlternativeProductImpl) alternativProductList.get(i);
	}

	/**
	 * sets the alternative product for the given index
	 * 
	 * @param i
	 *            index to set the product
	 * @param altProd
	 *            the alternative product for the given index, or null if index
	 *            is out of bounds
	 */
	@Override
	public void addAlternativProduct(final int i,
			final AlternativeProduct altProd) {
		alternativProductList.add(i, altProd);
	}

	/**
	 * sets the alternative product for the given index
	 * 
	 * @param altProd
	 *            the alternative product for the given index, or null if index
	 *            is out of bounds
	 */
	@Override
	public void addAlternativProduct(final AlternativeProduct altProd) {
		alternativProductList.add(altProd);
	}

	/**
	 * Creates and adds an alternative Product to the ProductAliasList
	 * 
	 * @param systemProductId
	 *            id of the system product
	 * @param systemProductGUID
	 *            techkey of the system product
	 * @param description
	 *            description of the system product
	 * @param enteredProductIdType
	 *            if the system product was found through determination, this
	 *            specifies, as what the entered product id was interpreted
	 * @param substitutionReasonId
	 *            if the system product is a substitute product, this is the id
	 *            for the substitution reason
	 */
	@Override
	public void addAlternativProduct(final String systemProductId,
			final TechKey systemProductGUID, final String description,
			final String enteredProductIdType, final String substitutionReasonId) {
		final AlternativeProduct alternProduct = (AlternativeProduct) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_ALTERNATIVE_PRODUCT);

		alternProduct.setSystemProductGUID(systemProductGUID);
		alternProduct.setDescription(description);
		alternProduct.setEnteredProductIdType(enteredProductIdType);
		alternProduct.setSubstitutionReasonId(substitutionReasonId);
		alternProduct.setSystemProductId(systemProductId);

		addAlternativProduct(alternProduct);
	}

	/**
	 * set the alternative product list
	 * 
	 * @param alternativProductListData
	 *            new list of alternative products
	 */
	@Override
	public void setList(final List<AlternativeProduct> alternativProductListData) {
		alternativProductList.clear();
		alternativProductList.addAll(0, alternativProductList);
	}

	/**
	 * Performs a deep copy of this object. Hence the List of alternative
	 * products will be duplicated.
	 * 
	 * @return deep copy of this object
	 */
	@Override
	public Object clone() {
		AlternativeProductListImpl myClone;
		try {
			// will copy all primitives, references
			myClone = (AlternativeProductListImpl) super.clone();
		} catch (final CloneNotSupportedException ex) {
			// should not happen, because we are cloneable
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented",
					ex);

		}
		// duplicate list of alternative products, clone method of the ArrayList
		// is not suitable for this task, because it creates only a shallow
		// (not-deep) copy.
		myClone.alternativProductList = new ArrayList<AlternativeProduct>(
				alternativProductList.size());
		for (final AlternativeProduct altProd : alternativProductList) {
			myClone.alternativProductList.add((AlternativeProduct) altProd
					.clone());
		}

		return myClone;

	}

	/**
	 * Returns en iterator over the Entrys of the alternativ product list in
	 * form of Map.
	 * 
	 * @return iterator over alternativ products
	 */
	@Override
	public Iterator<AlternativeProduct> iterator() {

		return alternativProductList.iterator();

	}

	/**
	 * returns true, if the list contains substitute products product
	 * 
	 * @return boolean true if the list contains substitute products
	 */
	public boolean isSubstituteProductList() {
		return (alternativProductList != null
				&& !alternativProductList.isEmpty() && ((AlternativeProductImpl) alternativProductList
					.get(0)).isSubstituteProduct());
	}

	/**
	 * returns true, if the list contains determination products
	 * 
	 * @return boolean true if the list contains determination products
	 */
	public boolean isDeterminationProductList() {
		return (alternativProductList != null
				&& !alternativProductList.isEmpty() && ((AlternativeProductImpl) alternativProductList
					.get(0)).isDeterminationProduct());
	}

}
