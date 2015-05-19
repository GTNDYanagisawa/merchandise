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
package de.hybris.platform.commercefacades.user;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.TitleData;

import java.util.List;


/**
 * User facade interface. Deals with methods related to user operations - registering, logging in and other.
 */
public interface UserFacade
{

	/**
	 * Provide all localized titles.
	 * 
	 * @return List of {@link TitleData} objects
	 */
	List<TitleData> getTitles();

	/**
	 * Test if the address book is empty.
	 * 
	 * @return true if the customer has no addresses
	 */
	boolean isAddressBookEmpty();

	/**
	 * Get the list of delivery addresses.
	 * 
	 * @return the delivery addresses
	 */
	List<AddressData> getAddressBook();

	/**
	 * Adds the address for the current user
	 * 
	 * @param addressData
	 *           the address to add
	 */
	void addAddress(AddressData addressData);

	/**
	 * Removes the address for the current user
	 * 
	 * @param addressData
	 *           the address to remove
	 */
	void removeAddress(AddressData addressData);

	/**
	 * Updates the address for the current user
	 * 
	 * @param addressData
	 *           the address to update
	 */
	void editAddress(AddressData addressData);

	/**
	 * Sets the default address
	 * 
	 * @param addressData
	 *           the address to make default
	 */
	void setDefaultAddress(AddressData addressData);

	/**
	 * Returns the default address
	 */
	AddressData getDefaultAddress();

	/**
	 * Returns the address with matching code for the current user
	 */
	AddressData getAddressForCode(String code);

	/**
	 * Test if the address id matches with the default address id
	 *
	 * @return true if address id is the default address id
	 */
	boolean isDefaultAddress(String addressId);

	/**
	 * Returns the current user's Credit Card Payment Infos
	 * 
	 * @param saved
	 *           <code>true</code> to retrieve only saved credit card payment infos
	 * @return list of Credit Card Payment Info Data
	 */
	List<CCPaymentInfoData> getCCPaymentInfos(boolean saved);

	/**
	 * Returns the current user's credit card payment info given it's code
	 * 
	 * @param code
	 *           the code
	 * @return the Credit Card Payment Info Data
	 */
	CCPaymentInfoData getCCPaymentInfoForCode(String code);


	/**
	 * Updates current users' payment info
	 * 
	 * @param paymentInfo
	 *           - new payment info data
	 * 
	 */
	void updateCCPaymentInfo(CCPaymentInfoData paymentInfo);


	/**
	 * Removes credit card payment info by id
	 * 
	 * @param id
	 *           the id
	 */
	void removeCCPaymentInfo(String id);

	/**
	 * Unlink the credit card info from the customer by CC id
	 * 
	 * @param id
	 *           the id
	 */
	void unlinkCCPaymentInfo(String id);


	/**
	 * Sets the default Payment Info
	 * 
	 * @param paymentInfo
	 *           the paymentInfo to make default
	 */
	void setDefaultPaymentInfo(CCPaymentInfoData paymentInfo);

	/**
	 * Sets users preferred language to the current session language
	 */
	void syncSessionLanguage();

	/**
	 * Sets users preferred currency to the current session currency
	 */
	void syncSessionCurrency();

	/**
	 * Test if the current user is anonymous
	 * 
	 * @return true if the current user is anonymous
	 */
	boolean isAnonymousUser();

}
