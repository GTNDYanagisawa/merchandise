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
package de.hybris.platform.acceleratorservices.email.dao;

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;


/**
 * Data Access for looking up email addresses.
 */
public interface EmailAddressDao extends Dao
{
	/**
	 * Retrieves EmailAddress given email address and display name.
	 * 
	 * @param email
	 *           the email address
	 * @param displayName
	 *           the display name
	 * @return The EmailAddress or null if not found
	 */
	EmailAddressModel findEmailAddressByEmailAndDisplayName(String email, String displayName);
}
