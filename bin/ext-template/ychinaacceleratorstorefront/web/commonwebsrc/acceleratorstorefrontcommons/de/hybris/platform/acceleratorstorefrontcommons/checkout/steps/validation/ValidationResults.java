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
package de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation;


public enum ValidationResults
{
	SUCCESS("SUCCESS"), FAILED("FAILED"), REDIRECT_TO_CART("REDIRECT_TO_CART"), REDIRECT_TO_PICKUP_LOCATION(
			"REDIRECT_TO_PICKUP_LOCATION"), REDIRECT_TO_DELIVERY_ADDRESS("REDIRECT_TO_DELIVERY_ADDRESS"), REDIRECT_TO_DELIVERY_METHOD(
			"REDIRECT_TO_DELIVERY_METHOD"), REDIRECT_TO_PAYMENT_METHOD("REDIRECT_TO_PAYMENT_METHOD"), REDIRECT_TO_DUMMY_STEP("REDIRECT_TO_DUMMY_STEP");

	private String result;

	private ValidationResults(final String name)
	{
		this.result = name;
	}
}
