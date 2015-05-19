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
package de.hybris.platform.yb2bacceleratorcore.actions;

import de.hybris.platform.b2b.process.approval.actions.SetBookingLineEntries;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;


/**
 * The AcceleratorBookingLineEntries.
 */
public class AcceleratorBookingLineEntries extends SetBookingLineEntries
{
	/** The Constant LOG. */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AcceleratorBookingLineEntries.class);

	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		modelService.refresh(order);

		if (order.getPaymentInfo() != null)
		{
			return Transition.OK;
		}
		else
		{
			return super.executeAction(process);
		}
	}
}
