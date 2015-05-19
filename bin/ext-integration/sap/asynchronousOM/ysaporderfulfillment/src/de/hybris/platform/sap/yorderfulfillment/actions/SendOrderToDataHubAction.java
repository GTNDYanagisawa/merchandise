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

package de.hybris.platform.sap.yorderfulfillment.actions;

import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.task.RetryLaterException;

import org.springframework.beans.factory.annotation.Required;



/**
 * triggers sending of the order to the SAP backend as an IDoc. Retry logic is applied in case the sending did not
 * succeed. Order export status is set to EXPORTED/NOTEXPORTED accordingly.
 */
public class SendOrderToDataHubAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	public static final String ERROR_END_MESSAGE = "Sending to ERP went wrong.";
	static final int DEFAULT_MAX_RETRIES = 10;
	static final int DEFAULT_RETRY_DELAY = 60 * 1000; // value in ms

	private int maxRetries = DEFAULT_MAX_RETRIES;
	private int retryDelay = DEFAULT_RETRY_DELAY;

	private SendToDataHubHelper<OrderModel> sendOrderToDataHubHelper;


	@SuppressWarnings("javadoc")
	public SendToDataHubHelper<OrderModel> getSendOrderToDataHubHelper()
	{
		return sendOrderToDataHubHelper;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setSendOrderToDataHubHelper(final SendToDataHubHelper<OrderModel> sendOrderAsCSVHelper)
	{
		this.sendOrderToDataHubHelper = sendOrderAsCSVHelper;
	}

	@SuppressWarnings("javadoc")
	public int getMaxRetries()
	{
		return maxRetries;
	}

	@SuppressWarnings("javadoc")
	public void setMaxRetries(final int maxRetries)
	{
		this.maxRetries = maxRetries;
	}

	@SuppressWarnings("javadoc")
	public int getRetryDelay()
	{
		return retryDelay;
	}

	@SuppressWarnings("javadoc")
	public void setRetryDelay(final int retryDelay)
	{
		this.retryDelay = retryDelay;
	}

	@Override
	public Transition executeAction(final OrderProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		final SendToDataHubResult result = sendOrderToDataHubHelper.createAndSendRawItem(order);
		if (result.isSuccess())
		{
			setOrderStatus(order, ExportStatus.EXPORTED);
			resetEndMessage(process);
			return Transition.OK;
		}
		else
		{
			setOrderStatus(order, ExportStatus.NOTEXPORTED);
			handleRetry(process);
			return Transition.NOK;
		}

	}

	protected void handleRetry(final OrderProcessModel process) throws RetryLaterException
	{
		if (process.getSendOrderRetryCount() < getMaxRetries())
		{
			final OrderModel order = process.getOrder();
			process.setSendOrderRetryCount(process.getSendOrderRetryCount() + 1);
			modelService.save(process);
			final RetryLaterException ex = new RetryLaterException("Sending to backend failed for order " + order.getCode());
			ex.setDelay(getRetryDelay());
			ex.setRollBack(false);
			throw ex;
		}
	}

	protected void setOrderStatus(final OrderModel order, final ExportStatus exportStatus)
	{
		order.setExportStatus(exportStatus);
		save(order);
	}

	protected void resetEndMessage(final OrderProcessModel process)
	{
		final String endMessage = process.getEndMessage();
		if (ERROR_END_MESSAGE.equals(endMessage))
		{
			process.setEndMessage("");
			modelService.save(process);
		}
	}

	protected void tryAgainNextTime(final OrderProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		process.setSendOrderRetryCount(process.getSendOrderRetryCount() + 1);
		modelService.save(process);
		final RetryLaterException ex = new RetryLaterException("Sending to backend failed for order " + order.getCode());
		ex.setDelay(getRetryDelay());
		ex.setRollBack(false);
		throw ex;
	}

}
