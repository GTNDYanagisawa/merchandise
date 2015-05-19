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

import de.hybris.platform.b2b.process.approval.actions.AbstractProceduralB2BOrderApproveAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class StartFulfilmentProcessAction extends AbstractProceduralB2BOrderApproveAction
{
	private static final Logger LOG = Logger.getLogger(StartFulfilmentProcessAction.class);

	private BusinessProcessService businessProcessService;

	@Override
	public void executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		final BaseStoreModel store = order.getStore();
		final String fulfilmentProcessDefinitionName = store.getSubmitOrderProcessCode();

		if (fulfilmentProcessDefinitionName == null || fulfilmentProcessDefinitionName.isEmpty())
		{
			LOG.error("Unable to start fulfilment process for order [" + order.getCode() + "]. Store [" + store.getUid()
					+ "] has missing SubmitOrderProcessCode");
		}
		else
		{
			final String processCode = fulfilmentProcessDefinitionName + order.getCode() + System.currentTimeMillis();
			final OrderProcessModel businessProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
					processCode, fulfilmentProcessDefinitionName);

			businessProcessModel.setOrder(order);
			getModelService().save(businessProcessModel);
			getBusinessProcessService().startProcess(businessProcessModel);
		}
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}


}
