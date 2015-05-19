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
package de.hybris.platform.acceleratorservices.email.strategy;

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;


/**
 * Interface representing strategy for fetching EmailAddressModel.
 */
public interface EmailAddressFetchStrategy
{

	/**
	 * @param emailAddress
	 *           email for which the email address object should be fetched
	 * @param displayName
	 *           display name for which the email address object should be fetched
	 * @return email address instance
	 */
	EmailAddressModel fetch(String emailAddress, String displayName);

}
