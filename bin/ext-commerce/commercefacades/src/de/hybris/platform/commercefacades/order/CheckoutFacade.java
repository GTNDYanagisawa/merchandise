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

import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.strategies.CartValidator;

import java.util.List;


/**
 */
public interface CheckoutFacade
{
	/**
	 * Check if checkout cart exist.
	 * 
	 * @return true if cart exists
	 */
	boolean hasCheckoutCart();

	/**
	 * Method gets checkout cart, if any exist.
	 * 
	 * @return cart data if cart exists
	 */
	CartData getCheckoutCart();

	/**
	 * Get the list of supported delivery addresses.
	 * 
	 * @param visibleAddressesOnly
	 *           include only the visible addresses
	 * @return the supported delivery addresses
	 */
	List<? extends AddressData> getSupportedDeliveryAddresses(boolean visibleAddressesOnly);

	/**
	 * Get the delivery address with the specified code
	 * 
	 * @param code
	 *           the code
	 * @return the delivery address
	 */
	AddressData getDeliveryAddressForCode(String code);

	/**
	 * Set the delivery address on the cart.
	 * 
	 * @param address
	 *           the address, If null the delivery address is removed from the session cart.
	 * @return true if operation succeeded
	 */
	boolean setDeliveryAddress(AddressData address);

	/**
	 * Removes delivery address from session cart.
	 * 
	 * @return true if delivery address was removed from cart
	 */
	boolean removeDeliveryAddress();

	/**
	 * Get the supported delivery modes for the cart.
	 * 
	 * @return the collection of supported delivery modes
	 */
	List<? extends DeliveryModeData> getSupportedDeliveryModes();

	/**
	 * Set delivery address if customer has a default delivery address and is valid for the cart
	 * 
	 * @return true if successful
	 */
	boolean setDeliveryAddressIfAvailable();

	/**
	 * Set cheapest delivery mode if the cart has one or more supported delivery modes
	 * 
	 * @return true if successful
	 */
	boolean setDeliveryModeIfAvailable();

	/**
	 * Set payment details if the customer has a default payment details
	 * 
	 * @return true if successful
	 */
	boolean setPaymentInfoIfAvailable();

	/**
	 * Set the delivery mode on the cart Checks if the deliveryMode code is supported. If the code is not supported it
	 * does not get set and a false is returned.
	 * 
	 * @param deliveryModeCode
	 *           the delivery mode
	 * @return true if successful
	 */
	boolean setDeliveryMode(String deliveryModeCode);


	/**
	 * Removes the delivery mode on the cart
	 * 
	 * @return true if successfully removed.
	 */
	boolean removeDeliveryMode();

	/**
	 * Get the supported delivery countries. The list is sorted alphabetically.
	 * 
	 * @return list of supported delivery countries.
	 */
	List<CountryData> getDeliveryCountries();

	/**
	 * Get the supported billing countries. The list is sorted alphabetically.
	 * 
	 * @return list of supported billing countries.
	 */
	List<CountryData> getBillingCountries();

	/**
	 * Get the CountryData Object for the country represented by the given ISO Code.
	 * 
	 * @use {@link I18NFacade#getCountryForIsocode(String)}
	 * @param countryIso
	 *           the ISO code for the country to lookup
	 * @return the CountryData Object
	 */
	@Deprecated
	CountryData getCountryForIsocode(String countryIso);

	/**
	 * Set Payment Details on the cart
	 * 
	 * @param paymentInfoId
	 *           the ID of the payment info to set as the default payment
	 * @return true if operation succeeded
	 */
	boolean setPaymentDetails(String paymentInfoId);

	/**
	 * Get supported payment card types
	 * 
	 * @return list of supported card types
	 */
	List<CardTypeData> getSupportedCardTypes();

	/**
	 * Create new payment subscription. Pass in a CCPaymentInfoData containing the customer's card details. A new payment
	 * subscription will be created, and the sorted card details will be returned in a new CCPaymentInfoData.
	 * 
	 * @param paymentInfoData
	 *           the data instance containing the customers cart details
	 * @return the newly created payment info data
	 */
	CCPaymentInfoData createPaymentSubscription(CCPaymentInfoData paymentInfoData);

	/**
	 * Authorize payment for the order. The order must have a subscription payment details set on it before the payment
	 * can be authorized.
	 * 
	 * @param securityCode
	 *           the 3 or 4 number CV2 or CVV security code
	 * @return true if successful
	 */
	boolean authorizePayment(String securityCode);

	/**
	 * Place order
	 * 
	 * @return orderData representing the order
	 * @throws InvalidCartException
	 *            is thrown by underlying {@link CartValidator}
	 */
	OrderData placeOrder() throws InvalidCartException;

	/**
	 * Checks that cart or any of cart entries has TaxValues
	 * 
	 * @return true if the cart or its entries has TaxValues
	 */
	boolean containsTaxValues();

	/**
	 * Looks up addresses in the customer address book
	 * 
	 * @param addressId
	 *           The PK of an address
	 * @param visibleAddressesOnly
	 *           If true checks visible addresses in the address book
	 * @return A address from the address book with a matching id
	 */
	AddressData getAddressDataForId(String addressId, boolean visibleAddressesOnly);

	/**
	 * Prepares cart for checkout
	 */
	void prepareCartForCheckout();

	/**
	 * Sets the defaultPayment info on the cart if the current user has a default payment info
	 * 
	 * @return true if successful
	 */
	boolean setDefaultPaymentInfoForCheckout();

	/**
	 * Sets the defaultAddress info on the cart with if current user has a default address info
	 * 
	 * @return true if successful
	 */
	boolean setDefaultDeliveryAddressForCheckout();

	/**
	 * Sets the cheapest delivery mode in the cart when called
	 * 
	 * @return boolean if successful
	 */
	boolean setCheapestDeliveryModeForCheckout();

	/**
	 * Checks if at least one entry in the cart is for shipping
	 * 
	 * @return true if at least one shipping entry is found in the cart
	 */
	boolean hasShippingItems();

	/**
	 * Checks if at least one entry in the cart is a pickup entry
	 * 
	 * @return true if even one pickup entry is found in the cart
	 */
	boolean hasPickUpItems();
}
