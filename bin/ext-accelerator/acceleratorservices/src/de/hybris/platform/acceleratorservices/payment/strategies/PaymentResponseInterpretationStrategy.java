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


import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;

import java.util.Map;


/**
 * Strategy for interpreting the response to a payment profile creation request.
 */
public interface PaymentResponseInterpretationStrategy
{
	/**
	 * This method defines how to interpret a {@link CreateSubscriptionResult} that represents the set of parameters
	 * returned from of a profile creation request with an external payment service.
	 *
	 * @param responseParams
	 * 				- The response params necessary to complete payment profile creation with an external service.
	 * @param clientRef
	 * 				- Unique identifier representing the client in a call to external services.
	 * @param errors
	 * 				- Map of payment details errors populated by external service.
	 */
	CreateSubscriptionResult interpretResponse(Map<String, String> responseParams, String clientRef, Map<String, PaymentErrorField> errors);
}
