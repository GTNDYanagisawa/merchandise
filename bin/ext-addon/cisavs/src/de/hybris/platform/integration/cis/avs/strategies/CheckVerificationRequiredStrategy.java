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
package de.hybris.platform.integration.cis.avs.strategies;

import de.hybris.platform.core.model.user.AddressModel;


/**
 * Strategy that defines if we should call the address verification external service.
 */
public interface CheckVerificationRequiredStrategy
{
	/**
	 * Check if the address should be verified.
	 * 
	 * @param addressToVerify
	 *           A hybris address model
	 * @return true if the address should be verified, false otherwise
	 */
	boolean isVerificationRequired(AddressModel addressToVerify);
}
