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
 * This Enum represents the credit card types used by the CyberSource Hosted Order Page Service.
 */
public enum CardTypeEnum
{
	visa("001"),
	master("002"),
	amex("003"),
	discover("004"),
	diners("005"),
	jcb("007"),
	maestro("024"),
	SWITCH("switch");

	private final String stringValue;

	// Reverse-lookup map for getting a day from an abbreviation
	private static final Map<String, CardTypeEnum> LOOKUP = new HashMap<String, CardTypeEnum>();

	static
	{
		for (final CardTypeEnum cardType : CardTypeEnum.values())
		{
			LOOKUP.put(cardType.getStringValue(), cardType);
		}
	}

	private CardTypeEnum(final String stringValue)
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
	 * Gets a CardTypeEnum for the given String representation.
	 * @param stringValue - a String representing one of the CardTypeEnum.
	 * @return - the CardTypeEnum that is represented by the String value given.
	 */
	public static CardTypeEnum get(final String stringValue)
	{
		return LOOKUP.get(stringValue);
	}
}
