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
package de.hybris.platform.commercefacades.address;

import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.user.data.AddressData;

/**
 * Facade for interacting with the AddressVerificationService.
 */
public interface AddressVerificationFacade
{
	/**
	 * Calls the configurable AddressVerificationService to verify the incoming address.  If the address is invalid
	 * the service will provide a list of possible addresses for the customer to choose from.
	 *
	 * @param addressData the address data to be verified.
	 * @return a POJO containing the set of suggested addresses as well as any field errors.
	 */
	AddressVerificationResult verifyAddressData(AddressData addressData);

	/**
	 * Checks the AddressVerificationService to see if the customer can ignore the suggestions that were provided.
	 *
	 * @return true if the customer can ignore suggestions, false if not.
	 */
	boolean isCustomerAllowedToIgnoreAddressSuggestions();
}
