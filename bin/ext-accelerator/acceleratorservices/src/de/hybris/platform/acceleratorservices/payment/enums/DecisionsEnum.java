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
package de.hybris.platform.acceleratorservices.payment.enums;

/**
 * This Enum represents the transaction results that may return from the PSP Service.
 */
public enum DecisionsEnum
{
	ACCEPT, //The request succeeded.

	REVIEW, //Decision Manager was triggered and you should review this order. A subscription or profile will not be created when an order is in a review state. You must manually create each one by using the Business Center.

	ERROR, //A system error occurred.

	REJECT, //One or more of the services was declined.

	CANCEL, //Customer cancelled the order.

	PARTIAL //The authorization was partially approved.
}
