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

package de.hybris.platform.sap.yorderfulfillment.jobs;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.sap.orderexchange.cancellation.SapOrderCancelService;
import de.hybris.platform.sap.orderexchange.outbound.OrderExchangeRepair;
import de.hybris.platform.sap.yorderfulfillment.constants.ActionIds;
import de.hybris.platform.sap.yorderfulfillment.constants.YsaporderfulfillmentConstants;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * resets status of the order in case a cancel order request was send to ERP but no response was received. The business
 * process is set status succeeded.
 */
public class OrderCancelRepairJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = Logger.getLogger(OrderCancelRepairJob.class);
	private static final String YSAPORDERFULFILLMENT_REPAIR_JOB_CANCEL_MINUTES = "ysaporderfulfillment.repair.job.cancel.minutes";

	private OrderExchangeRepair orderExchangeRepair;
	private BusinessProcessService businessProcessService;
	private SapOrderCancelService sapOrderCancelService;

	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		List<OrderProcessModel> waitProcesses;
		List<OrderModel> orderList;

		waitProcesses = orderExchangeRepair.findProcessesByActionIds(YsaporderfulfillmentConstants.ORDER_PROCESS_NAME, new String[]
		{ ActionIds.WAIT_FOR_ERP_CONFIRMATION, ActionIds.WAIT_FOR_CONSIGNMENT_CREATION, ActionIds.WAIT_FOR_GOODS_ISSUE });

		cancelProcessesForCancelledOrders(waitProcesses);

		orderList = orderExchangeRepair.findAllOrdersInStatus(OrderStatus.CANCELLING);
		resetOrders(orderList);

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}


	protected void resetOrders(final List<OrderModel> orderList)
	{

		final long configDiff = Long.parseLong(Config.getParameter(YSAPORDERFULFILLMENT_REPAIR_JOB_CANCEL_MINUTES));
		for (final OrderModel order : orderList)
		{
			final Date orderDate = order.getDate();
			final long diffResult = determineDiffMinutes(orderDate);

			if (diffResult > configDiff)
			{
				try
				{
					sapOrderCancelService.restoreAfterCancelFailed(order);
				}
				catch (final OrderCancelException e)
				{
					LOG.error("Error while restoring cancelled Order " + order.getCode(), e);
				}
			}
		}
	}

	private long determineDiffMinutes(final Date d1)
	{
		final Date today = new Date();
		final long diff = today.getTime() - d1.getTime();
		return TimeUnit.MILLISECONDS.toMinutes(diff);
	}


	protected void cancelProcessesForCancelledOrders(final List<OrderProcessModel> waitProcesses)
	{
		for (final OrderProcessModel orderProcessModel : waitProcesses)
		{
			final OrderModel orderModel = orderProcessModel.getOrder();
			if (orderModel != null && OrderStatus.CANCELLED.equals(orderModel.getStatus()))
			{
				// Remove "waiting" task
				final Collection<ProcessTaskModel> currentTasks = new ArrayList<>();
				orderProcessModel.setCurrentTasks(currentTasks);
				modelService.save(orderProcessModel);
				businessProcessService.restartProcess(orderProcessModel, ActionIds.SET_CANCEL_STATUS);
			}
		}
	}

	/**
	 * @return the orderExchangeRepair
	 */
	public OrderExchangeRepair getOrderExchangeRepair()
	{
		return orderExchangeRepair;
	}


	/**
	 * @param orderExchangeRepair
	 *           the orderExchangeRepair to set
	 */
	@Required
	public void setOrderExchangeRepair(final OrderExchangeRepair orderExchangeRepair)
	{
		this.orderExchangeRepair = orderExchangeRepair;
	}


	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}


	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}


	/**
	 * @return sapOrderCancelService
	 */
	public SapOrderCancelService getSapOrderCancelService()
	{
		return sapOrderCancelService;
	}

	/**
	 * @param sapOrderCancelService
	 */
	@Required
	public void setSapOrderCancelService(final SapOrderCancelService sapOrderCancelService)
	{
		this.sapOrderCancelService = sapOrderCancelService;
	}


}
