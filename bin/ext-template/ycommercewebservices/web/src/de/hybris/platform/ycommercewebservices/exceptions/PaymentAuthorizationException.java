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
 * Specific exception that is thrown when the payment authorization was not accepted
 * 
 * @author KKW
 * 
 */
public class PaymentAuthorizationException extends Exception
{


	public PaymentAuthorizationException()
	{
		super("Payment authorication was not successful");
	}

}
