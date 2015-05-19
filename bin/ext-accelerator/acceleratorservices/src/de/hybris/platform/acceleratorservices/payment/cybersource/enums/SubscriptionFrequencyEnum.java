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
package de.hybris.platform.acceleratorservices.payment.cybersource.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * This Enum represents the different types of subscriptions that are allowed to be created by the CyberSource Hosted Order Page Service.
 */
public enum SubscriptionFrequencyEnum
{
	ON_DEMAND("on-demand"),
	WEEKLY("weekly"),
	BI_WEEKLY("bi-weekly"),
	SEMI_MONTHLY("semi-monthly"),
	MONTHLY("monthly"),
	QUARTERLY("quarterly"),
	QUAD_WEEKLY("quad-weekly"),
	SEMI_ANNUALLY("semi-annually"),
	ANNUALLY("annually");

	private final String stringValue;

	// Reverse-lookup map for getting a day from an abbreviation
	private static final Map<String, SubscriptionFrequencyEnum> LOOKUP = new HashMap<String, SubscriptionFrequencyEnum>();

	static
	{
		for (final SubscriptionFrequencyEnum subscriptionFrequency : SubscriptionFrequencyEnum.values())
		{
			LOOKUP.put(subscriptionFrequency.getStringValue(), subscriptionFrequency);
		}
	}

	private SubscriptionFrequencyEnum(final String stringValue)
	{
		this.stringValue = stringValue.intern();
	}

	/**
	 * gets a String value of the current enum.
	 * @return - a Sting object.
	 */
	public String getStringValue()
	{
		return this.stringValue;
	}

	/**
	 * Gets a SubscriptionFrequencyEnum for the given String representation.
	 * @param stringValue - a String representing one of the SubscriptionFrequencyEnum.
	 * @return - the SubscriptionFrequencyEnum that is represented by the String value given.
	 */
	public static SubscriptionFrequencyEnum get(final String stringValue)
	{
		return LOOKUP.get(stringValue);
	}
}
