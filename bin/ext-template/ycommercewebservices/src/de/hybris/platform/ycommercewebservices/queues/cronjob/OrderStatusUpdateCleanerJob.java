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
package de.hybris.platform.ycommercewebservices.queues.cronjob;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.ycommercewebservices.model.expressupdate.cron.OrderStatusUpdateCleanerCronJobModel;
import de.hybris.platform.ycommercewebservices.queues.impl.OrderStatusUpdateQueue;

import java.util.Date;


/**
 * A Cron Job for cleaning up {@link de.hybris.platform.ycommercewebservices.queues.impl.OrderStatusUpdateQueue}.
 */
public class OrderStatusUpdateCleanerJob extends AbstractJobPerformable<OrderStatusUpdateCleanerCronJobModel>
{
	private OrderStatusUpdateQueue orderStatusUpdateQueue;

	@Override
	public PerformResult perform(final OrderStatusUpdateCleanerCronJobModel cronJob)
	{
		final Date timestamp = new Date(System.currentTimeMillis() - (cronJob.getQueueTimeLimit().intValue() * 60 * 1000));
		getOrderStatusUpdateQueue().removeItems(timestamp);
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	private OrderStatusUpdateQueue getOrderStatusUpdateQueue()
	{
		return orderStatusUpdateQueue;
	}

	public void setOrderStatusUpdateQueue(final OrderStatusUpdateQueue orderStatusUpdateQueue)
	{
		this.orderStatusUpdateQueue = orderStatusUpdateQueue;
	}
}
