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
package de.hybris.platform.acceleratorservices.payment.strategies;

import java.util.Map;


/**
 * A strategy to handle callbacks about fraud from a payment service provider
 * 
 */
public interface FraudCallbackStrategy
{
	/**
	 * Handle fraudulent transactions
	 * 
	 * @param parameters
	 *           A map of key value pairs about fraudulent payment transactions.
	 */
	void handleFraudCallback(final Map<String, String> parameters);
}
