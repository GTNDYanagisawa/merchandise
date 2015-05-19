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
package de.hybris.platform.yb2bacceleratorcore.event;

import de.hybris.platform.b2b.event.QuoteApprovedEvent;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;


/**
 * A listener of {@link de.hybris.platform.b2b.event.QuoteApprovedEvent} which is fired when a quote is approved by a
 * manager
 */
public class QuoteApprovedEventListener extends QuoteEventListener<QuoteApprovedEvent>
{
	@Override
	protected void onEvent(final QuoteApprovedEvent event)
	{
		final OrderModel quoteOrder = event.getOrder();
		final OrderProcessModel orderProcess = (OrderProcessModel) getBusinessProcessService().createProcess(
				"quoteOrderApproveEmailProcess" + "-" + quoteOrder.getCode() + "-" + System.currentTimeMillis(),
				"quoteOrderApproveEmailProcess");
		orderProcess.setOrder(quoteOrder);
		getModelService().save(orderProcess);
		getBusinessProcessService().startProcess(orderProcess);
		final UserModel manager = (UserModel) event.getManager();
		final B2BCommentModel comment = getLatestCommentByUser(quoteOrder, manager);
		createSnapshot(quoteOrder, comment, manager, OrderStatus.APPROVED_QUOTE);
	}
}
