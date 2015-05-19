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

package de.hybris.platform.sap.orderexchange.taskrunners;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class is to send an asynchronous order cancellation. Maximum number of retries and the delay for data resending
 * can be configured.
 */
public class SendOrderCancelRequestAsCSVTaskRunner implements TaskRunner<TaskModel>
{
	static final int DEFAULT_MAX_RETRIES = 10;
	static final int DEFAULT_RETRY_DELAY = 60 * 1000; // value in milliseconds	

	private ModelService modelService;
	private int maxRetries = DEFAULT_MAX_RETRIES;
	private int retryDelay = DEFAULT_RETRY_DELAY;
	private SendToDataHubHelper<OrderCancelRecordEntryModel> orderCancelRequestCSVHelper;

	@Override
	public void run(final TaskService paramTaskService, final TaskModel taskModel) throws RetryLaterException
	{
		final OrderCancelRecordEntryModel orderCancelRecordEntry = (OrderCancelRecordEntryModel) taskModel.getContext();
		final SendToDataHubResult sendingResult = getOrderCancelRequestCSVHelper().createAndSendRawItem(orderCancelRecordEntry);

		if (!sendingResult.isSuccess())
		{
			final OrderModel order = orderCancelRecordEntry.getModificationRecord().getOrder();
			order.setStatus(OrderStatus.CANCELLING);
			getModelService().save(order);
			if (taskModel.getRetry().intValue() <= getMaxRetries())
			{
				final RetryLaterException ex = new RetryLaterException(
						"Sending of cancellation information to backend failed for order " + order.getCode());
				ex.setDelay(getRetryDelay());
				ex.setRollBack(false);
				throw ex;
			}
		}
	}

	@Override
	public void handleError(final TaskService paramTaskService, final TaskModel taskModel, final Throwable paramThrowable)
	{
		final OrderCancelRecordEntryModel orderCancel = (OrderCancelRecordEntryModel) taskModel.getContext();
		final OrderModel order = orderCancel.getModificationRecord().getOrder();
		order.setStatus(OrderStatus.CANCELLING);
		getModelService().save(order);
	}

	protected SendToDataHubHelper<OrderCancelRecordEntryModel> getOrderCancelRequestCSVHelper()
	{
		return orderCancelRequestCSVHelper;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setOrderCancelRequestCSVHelper(final SendToDataHubHelper<OrderCancelRecordEntryModel> orderCancelRequestCSVHelper)
	{
		this.orderCancelRequestCSVHelper = orderCancelRequestCSVHelper;
	}

	protected int getMaxRetries()
	{
		return maxRetries;
	}

	@SuppressWarnings("javadoc")
	public void setMaxRetries(final int maxRetries)
	{
		this.maxRetries = maxRetries;
	}

	protected int getRetryDelay()
	{
		return retryDelay;
	}

	@SuppressWarnings("javadoc")
	public void setRetryDelay(final int retryDelay)
	{
		this.retryDelay = retryDelay;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@SuppressWarnings("javadoc")
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
