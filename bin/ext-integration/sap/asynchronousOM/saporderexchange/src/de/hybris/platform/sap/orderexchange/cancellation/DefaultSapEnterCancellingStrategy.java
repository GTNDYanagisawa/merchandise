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
package de.hybris.platform.sap.orderexchange.cancellation;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderStatusChangeStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskConditionModel;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;



/**
 * This class is to overwrite the hybris cancel strategy and to send order cancel information to ERP asynchronously
 */
public class DefaultSapEnterCancellingStrategy implements OrderStatusChangeStrategy
{

	private ModelService modelService;
	private TaskService taskService;

	public void changeOrderStatusAfterCancelOperation(final OrderCancelRecordEntryModel orderCancelRecordEntry,
			final boolean saveOrderModel)
	{
		final TaskConditionModel cond = modelService.create(TaskConditionModel.class);
		cond.setUniqueID("sendCancelToERPEvent");

		final TaskModel task = modelService.create(TaskModel.class);
		task.setRunnerBean("sapSendOrderCancelRequestAsCSVTaskRunner");
		task.setConditions(Collections.singleton(cond));
		task.setContext(orderCancelRecordEntry);

		taskService.scheduleTask(task);
		taskService.triggerEvent("sendCancelToERPEvent");

		final OrderModel order = orderCancelRecordEntry.getModificationRecord().getOrder();
		order.setStatus(OrderStatus.CANCELLING);
		this.modelService.save(order);
	}


	@SuppressWarnings("javadoc")
	public ModelService getModelService()
	{
		return this.modelService;
	}


	@SuppressWarnings("javadoc")
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	@SuppressWarnings("javadoc")
	public TaskService getTaskService()
	{
		return taskService;
	}


	@SuppressWarnings("javadoc")
	@Required
	public void setTaskService(final TaskService taskService)
	{
		this.taskService = taskService;
	}

}
