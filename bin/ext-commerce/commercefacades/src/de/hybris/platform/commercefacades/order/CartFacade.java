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
package de.hybris.platform.commercefacades.order;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;


/**
 * Cart facade interface. Service is responsible for getting all necessary information for cart, including delivery
 * details.
 */
public interface CartFacade
{
	/**
	 * Method gets cart from session, if any exist.
	 * 
	 * @return the cart data
	 */
	CartData getSessionCart();

	/**
	 * Method gets cart sorted by recently added entries from session, if param is set to true
	 * 
	 * @param recentlyAddedFirst
	 *           boolean value to determine whether to return sorted cart data
	 * @return the cart data sorted by recently added entries
	 */
	CartData getSessionCartWithEntryOrdering(boolean recentlyAddedFirst);

	/**
	 * Check if session has a cart.
	 * 
	 * @return <tt>true</tt> if the session has a cart
	 */
	boolean hasSessionCart();

	/**
	 * Check if session cart has entries, avoiding full conversion of the cart to CartData. For Performance.
	 * 
	 * @return <tt>true</tt> if the cart has any entries.
	 */
	boolean hasEntries();

	/**
	 * Method gets smaller version of cart, with data necessary for showing mini cart.
	 * 
	 * @return the mini card data
	 */
	CartData getMiniCart();

	/**
	 * Method for adding a product to cart.
	 * 
	 * @param code
	 *           code of product to add
	 * @param quantity
	 *           the quantity of the product
	 * @return the cart modification data that includes a statusCode and the actual quantity added to the cart
	 * @throws CommerceCartModificationException
	 *            if the cart cannot be modified
	 */
	CartModificationData addToCart(String code, long quantity) throws CommerceCartModificationException;

	/**
	 * Method for adding a product to cart.
	 * 
	 * @param code
	 *           code of product to add
	 * @param storeId
	 *           The identifier for the store. If null {@see CartFacade#addToCart(String, long)} is used.
	 * @param quantity
	 *           the quantity of the product
	 * @return the cart modification data that includes a statusCode and the actual quantity added to the cart
	 * @throws CommerceCartModificationException
	 *            if the cart cannot be modified
	 */
	CartModificationData addToCart(String code, long quantity, String storeId) throws CommerceCartModificationException;

	/**
	 * Method for validating every entry in the cart
	 * 
	 * @return a list of cart modifications that includes a statusCode and the actual quantity that the entry was updated
	 *         to
	 * @throws CommerceCartModificationException
	 *            if the cart cannot be modified
	 */
	List<CartModificationData> validateCartData() throws CommerceCartModificationException;

	/**
	 * Method for updating the number of products.
	 * 
	 * @param entryNumber
	 *           the entry number
	 * @param quantity
	 *           new value of quantity for product
	 * @return the cart modification data that includes a statusCode and the actual quantity that the entry was updated
	 *         to
	 * @throws CommerceCartModificationException
	 *            if the cart cannot be modified
	 */
	CartModificationData updateCartEntry(long entryNumber, long quantity) throws CommerceCartModificationException;

	/**
	 * Method for updating the number of products.
	 * 
	 * @param entryNumber
	 *           The entry number
	 * @param storeId
	 *           The identifier for the store. Can be null.
	 * @return the cart modification data that includes a statusCode and the actual quantity that the entry was updated
	 *         to
	 * @throws CommerceCartModificationException
	 *            if the cart cannot be modified
	 */
	CartModificationData updateCartEntry(long entryNumber, String storeId) throws CommerceCartModificationException;

	/**
	 * Restores the user's saved cart to the session. For non-anonymous users, the cart restored will be for the current
	 * user and site.
	 * 
	 * @param code
	 *           the cart code to restore for Anonymous user.
	 * @return the cart restoration data that includes details of any items that could not be restored in part or in
	 *         full.
	 */
	CartRestorationData restoreSavedCart(String code) throws CommerceCartRestorationException;

	/**
	 * Get the supported delivery countries. The list is sorted alphabetically.
	 * 
	 * @return list of supported delivery countries.
	 */
	List<CountryData> getDeliveryCountries();

	/**
	 * Estimate taxes on the cart.
	 * 
	 * @param deliveryZipCode
	 *           delivery zip code used for calculating external taxes
	 * @param countryIsoCode
	 *           delivery country used for calculating taxes
	 * @return cart data that represents the current cart with taxes estimations added
	 */
	CartData estimateExternalTaxes(String deliveryZipCode, String countryIsoCode);


	/**
	 * 
	 * Remove the stale carts of the current user
	 * 
	 */
	void removeStaleCarts();

	/**
	 * Restores the anonymous user's cart to the session and sets its user to current user.
	 * 
	 * @param guid
	 *           GUID used to find anonymous user's cart
	 * @return the cart restoration data that includes details of any items that could not be restored in part or in
	 *         full.
	 * @throws CommerceCartRestorationException
	 */
	CartRestorationData restoreAnonymousCartAndTakeOwnership(String guid) throws CommerceCartRestorationException;

	/**
	 * If existing the current session cart is being detached from this session and removed. Afterwards it's no longer
	 * available.
	 */
	void removeSessionCart();

	/**
	 * Returns all carts for current site and user
	 * 
	 * @return list of carts
	 */
	List<CartData> getCartsForCurrentUser();

	/**
	 * Merge two carts and add modifications
	 * 
	 * @param fromCart
	 *           - Cart from merging is done
	 * @param toCart
	 *           - Cart to merge to
	 * @param modifications
	 *           - List of modifications
	 * @throws CommerceCartMergingException
	 */
	@Deprecated
	void mergeCarts(CartModel fromCart, CartModel toCart, List<CommerceCartModification> modifications)
			throws CommerceCartMergingException;

	/**
	 * Merges carts from anonymous to given user's cart, restores user's cart to the session, sets its user to current
	 * user.
	 * 
	 * @param fromAnonumousCartGuid
	 *           - Anonymous user's cart from merging is done
	 * @param toUserCartGuid
	 *           - User's cart to merge to
	 * @return
	 * @throws CommerceCartMergingException
	 * @throws CommerceCartRestorationException
	 */
	CartRestorationData restoreAnonymousCartAndMerge(String fromAnonumousCartGuid, String toUserCartGuid)
			throws CommerceCartMergingException, CommerceCartRestorationException;

	/**
	 * Merges user's carts, restores merged cart to the session, sets its user to current user.
	 * 
	 * @param fromUserCartGuid
	 *           - User's cart from merging is done
	 * @param toUserCartGuid
	 *           - User's cart to merge to
	 * @return
	 * @throws CommerceCartRestorationException
	 * @throws CommerceCartMergingException
	 */
	CartRestorationData restoreCartAndMerge(String fromUserCartGuid, String toUserCartGuid)
			throws CommerceCartRestorationException, CommerceCartMergingException;
}
