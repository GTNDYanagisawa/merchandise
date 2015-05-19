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
 */
package de.hybris.platform.ycommercewebservices.exceptions;

/**
 * Specific exception that is thrown when delivery address is not supported for the current session cart
 * 
 * @author KKW
 * 
 */
public class UnsupportedDeliveryAddressException extends Exception
{

	private final String addressId;

	/**
	 * @param id
	 */
	public UnsupportedDeliveryAddressException(final String id)
	{
		super("Address [" + id + "] is not supported for the current cart");
		this.addressId = id;
	}

	/**
	 * @return the addressId
	 */
	public String getAddressId()
	{
		return addressId;
	}
}
