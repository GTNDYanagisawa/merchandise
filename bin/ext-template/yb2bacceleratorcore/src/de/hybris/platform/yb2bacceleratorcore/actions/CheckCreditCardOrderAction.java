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

import de.hybris.platform.b2b.process.approval.actions.AbstractSimpleB2BApproveOrderDecisionAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;


public class CheckCreditCardOrderAction extends AbstractSimpleB2BApproveOrderDecisionAction
{

	protected static final Logger LOG = Logger.getLogger(CheckOrderEntryCostCentersActive.class);

	/*
	 * Returns Transition.NOK if the order has any entries with inactive cost centers otherwise returns Transition.OK
	 */
	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		OrderModel order = null;
		Transition transition = Transition.NOK;
		try
		{
			order = process.getOrder();
			final PaymentInfoModel paymentInfo = order.getPaymentInfo();

			if (CheckoutPaymentType.CARD.equals(order.getPaymentType()) && paymentInfo instanceof CreditCardPaymentInfoModel)
			{
				// this is a credit card payment, approval is not required
				transition = Transition.OK;
			}
		}
		catch (final Exception e)
		{
			this.handleError(order, e);
		}
		return transition;
	}

	protected void handleError(final OrderModel order, final Exception exception)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(exception.getMessage(), exception);
	}


}
