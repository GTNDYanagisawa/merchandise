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
package de.hybris.platform.chinaaccelerator.facades.customer;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;


/**
 * ExtendedCustomerFacade
 */
public interface ExtendedCustomerFacade extends CustomerFacade
{
	/**
	 * Update current customer's mobile number with given parameters. The current password is required in order to
	 * validate that the current visitor is actually the customer.
	 *
	 * @param customerData
	 *           the updated customer data
	 * @param currentPassword
	 *           current user password to validate user
	 * @throws PasswordMismatchException
	 *            thrown if the password is invalid
	 * @throws DuplicateUidException
	 *            thrown if mobileNumber is duplicated
	 */
	public void updateMobileNumber(final CustomerData customerData, final String currentPassword)
			throws PasswordMismatchException, DuplicateUidException;
}
