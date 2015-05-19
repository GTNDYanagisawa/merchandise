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

import de.hybris.platform.b2bacceleratorservices.event.ReplenishmentOrderPlacedEvent;
import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;


public class ReplenishmentOrderPlacedEventListener extends AbstractEventListener<ReplenishmentOrderPlacedEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	protected void onEvent(final ReplenishmentOrderPlacedEvent event)
	{
		final CartToOrderCronJobModel cartToOrderCronJob = event.getCartToOrderCronJob();
		final ReplenishmentProcessModel replenishmentProcess = (ReplenishmentProcessModel) getBusinessProcessService()
				.createProcess(
						"replenishmentOrderPlacedEmailProcess" + "-" + cartToOrderCronJob.getCode() + "-" + System.currentTimeMillis(),
						"replenishmentOrderPlacedEmailProcess");
		replenishmentProcess.setCartToOrderCronJob(cartToOrderCronJob);
		replenishmentProcess.setCustomer(event.getCustomer());
		replenishmentProcess.setCurrency(event.getCurrency());
		replenishmentProcess.setLanguage(event.getLanguage());
		replenishmentProcess.setStore(event.getBaseStore());
		getModelService().save(replenishmentProcess);
		getBusinessProcessService().startProcess(replenishmentProcess);
	}
}
