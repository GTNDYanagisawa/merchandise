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
package de.hybris.platform.orderprocessing.events;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;


/**
 * Event representing failure to authorise a payment.
 */
public class AuthorizationFailedEvent extends OrderProcessingEvent
{
	private static final long serialVersionUID = 8181864059445399549L;

	public AuthorizationFailedEvent(final OrderProcessModel process)
	{
		super(process);
	}
}
