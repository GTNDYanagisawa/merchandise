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
package de.hybris.platform.sap.sapordermgmtservices.cart;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.List;



/**
 *
 */
public interface CartService
{

	/**
	 * Retrieves the session cart held in the SAP back end
	 *
	 * @return Current session cart
	 */
	public abstract CartData getSessionCart();

	/**
	 * Checks if a session cart exists held in the SAP back end
	 *
	 * @return Does the session cart exist?
	 */
	public abstract boolean hasSessionCart();

	/**
	 * Adds an entry to the cart. The result contains error messages if those have occurred.
	 *
	 * @param code
	 *           Product ID
	 * @param quantity
	 *           Quantity to be added
	 * @return Cart modification data
	 */
	public abstract CartModificationData addToCart(String code, long quantity);

	/**
	 * Returns session cart, sorted in inverted order if required
	 *
	 * @param recentlyAddedFirst
	 *           If true, recently added items will be returned first (Standard sorting will be inverted)
	 * @return Session Cart
	 */
	CartData getSessionCart(boolean recentlyAddedFirst);

	/**
	 * Updates a cart entry
	 *
	 * @param entryNumber
	 *           Item number
	 * @param quantity
	 *           New quantity of item
	 * @return Cart modifications
	 */
	public abstract CartModificationData updateCartEntry(long entryNumber, long quantity);

	/**
	 * Validates cart
	 *
	 * @return Modification status as result of the validation
	 */
	List<CartModificationData> validateCartData();

	/**
	 * Removes the existing session cart and releases the underlying LO-API session in SD. Afterwards, the cart is
	 * initial
	 */
	void removeSessionCart();

	/**
	 * Adds a new configuration to the cart. A new item will be created, based on the root instance of the config model
	 *
	 * @param configModel
	 * @return Key of new item
	 */
	String addConfigurationToCart(ConfigModel configModel);

	/**
	 * Updates the configuration attached to an item
	 *
	 * @param key
	 *           Key of existing item
	 * @param configModel
	 *           Configuration
	 */
	void updateConfigurationInCart(String key, ConfigModel configModel);



	/**
	 * Does an item exist with a given key?
	 *
	 * @param itemKey
	 * @return Does item exist for the item key?
	 */
	boolean isItemAvailable(String itemKey);

	/**
	 * @param items
	 */
	void addItemsToCart(List<Item> items);



}