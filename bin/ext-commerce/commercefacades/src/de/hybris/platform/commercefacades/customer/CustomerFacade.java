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
package de.hybris.platform.commercefacades.customer;

import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


public interface CustomerFacade
{
	/**
	 * Register a user with given parameters
	 * 
	 * @param registerData
	 *           the user data the user will be registered with
	 * @throws IllegalArgumentException
	 *            if required data is missing
	 * @throws UnknownIdentifierException
	 *            if the title code is invalid
	 * @throws DuplicateUidException
	 *            if the login is not unique
	 */
	void register(RegisterData registerData) throws DuplicateUidException, UnknownIdentifierException, IllegalArgumentException;


	/**
	 * Sends a forgotten password request for the customer specified.
	 * 
	 * @param uid
	 *           the uid of the customer to send the forgotten password mail for
	 * @throws UnknownIdentifierException
	 *            if the customer cannot be found for the uid specified
	 */
	void forgottenPassword(String uid);

	/**
	 * Update the password for the user by decrypting and validating the token.
	 * 
	 * @param token
	 *           the token to identify the the customer to reset the password for.
	 * @param newPassword
	 *           the new password to set
	 * @throws IllegalArgumentException
	 *            If the new password is empty or the token is invalid or expired
	 * @throws TokenInvalidatedException
	 *            if the token was already used or there is a newer token
	 */
	void updatePassword(String token, String newPassword) throws TokenInvalidatedException;

	// Methods that operate on the current session user

	/**
	 * Returns the current session user.
	 * 
	 * @return current session user data
	 * @throws ConversionException
	 *            the conversion exception when exception occurred when converting user
	 */
	CustomerData getCurrentCustomer() throws ConversionException;

	/**
	 * Change the current customer's UID. The current password is required for 2 reasons, firstly to validate that the
	 * current visitor is actually the customer, secondly the password hash may be salted with the UID and therefore if
	 * the UID is changed then the password needs to be re-hashed.
	 * 
	 * @param newUid
	 *           the new UID for the current customer
	 * @param currentPassword
	 *           current user password to validate user
	 * @throws PasswordMismatchException
	 *            thrown if the password is invalid
	 * @throws DuplicateUidException
	 *            thrown if the newUid is already in use
	 */
	void changeUid(String newUid, String currentPassword) throws DuplicateUidException, PasswordMismatchException;

	/**
	 * Changes current user password. If current session user is anonymous nothing happens.
	 * 
	 * @param oldPassword
	 *           old password to confirm
	 * @param newPassword
	 *           new password to set
	 * @throws de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException
	 *            if the given old password does not match the one stored in the system
	 */
	void changePassword(String oldPassword, String newPassword) throws PasswordMismatchException;

	/**
	 * Updates current customer's profile with given parameters
	 * 
	 * @param customerData
	 *           the updated customer data
	 * @throws DuplicateUidException
	 *            if the login is not unique
	 */
	void updateProfile(CustomerData customerData) throws DuplicateUidException;

	/**
	 * Updates current customer's profile with given parameters
	 * 
	 * @param customerData
	 *           the updated customer data
	 * @throws DuplicateUidException
	 *            if the login is not unique
	 */
	void updateFullProfile(CustomerData customerData) throws DuplicateUidException;

	/**
	 * updates the session currency and language to the user settings, assigns the cart to the current user and
	 * calculates the cart
	 */
	void loginSuccess();

	/**
	 * Create a regular customer from a guest customer who has just completed the guest checkout.
	 *
	 *
	 * @param pwd
	 * 			  the new password entered by the user
	 * @param orderCode
	 * @throws DuplicateUidException
	 *            if the login is not unique
	 */
	void changeGuestToCustomer(String pwd, String orderCode) throws DuplicateUidException;

	/**
	 * Generate random guid
	 *
	 * @return
	 *             Unique Random number/Id
	 */
	String generateGUID();

	/**
	 * Creates a new  guest customer for anonymousCheckout and sets the email and name.
	 *
	 * @param email
	 * @param name
	 * @throws DuplicateUidException
	 */
	void createGuestUserForAnonymousCheckout(String email, String name) throws DuplicateUidException;

	/**
	 * Guest customer which is created for anonymous checkout will be the cart user.
	 * The session user will remain anonymous.
	 *
	 * @param guestCustomerData
	 */
	void updateCartWithGuestForAnonymousCheckout(CustomerData guestCustomerData);

	/**
	 * This method will be used by rememberMeServices when there is encoding attributes for language
	 * and currency.
	 *
	 * @param languageEncoding
	 * @param currencyEncoding
	 */
	void rememberMeLoginSuccessWithUrlEncoding(boolean languageEncoding, boolean currencyEncoding);
}
