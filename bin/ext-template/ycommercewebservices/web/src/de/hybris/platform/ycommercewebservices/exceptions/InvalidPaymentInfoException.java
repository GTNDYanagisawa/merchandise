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
 * Specific exception that is thrown when the given payment info could not be associated with the checkout cart.
 * 
 * @author KKW
 * 
 */
public class InvalidPaymentInfoException extends Exception
{

	private final String paymentInfoId;

	/**
	 * @param id
	 */
	public InvalidPaymentInfoException(final String id)
	{
		super("PaymentInfo [" + id + "] is invalid for the current cart");
		this.paymentInfoId = id;
	}

	/**
	 * @return the paymentInfoId
	 */
	public String getPaymentInfoId()
	{
		return paymentInfoId;
	}

}
