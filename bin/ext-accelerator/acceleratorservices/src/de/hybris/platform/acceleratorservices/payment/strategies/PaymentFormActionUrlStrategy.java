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

/**
 * Strategy for retrieving the correct URL for payment submission.
 */
public interface PaymentFormActionUrlStrategy
{
	/**
	 * Gets the URI of the Hosted Order Page. This method read the HOP URL using the configuration service, as such the
	 * URL has to be defined in a property file on the class path.
	 *
	 * @return a URL to the HOP server.
	 */
	String getHopRequestUrl();

	/**
	 * Gets the URI of the Silent Order Post.  By default this method will utilize a config property to define the SOP
	 * URL.  An extension may implement this method to call an external service (e.g., CIS) to get the correct SOP URL.
	 *
     * @param clientRef unique identifier representing the client in a call to external services.
     *
	 * @return a URL to the SOP server.
	 */
	String getSopRequestUrl(String clientRef);
}
