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
package de.hybris.platform.b2bacceleratorservices.order.impl;

import de.hybris.platform.b2bacceleratorservices.model.process.ReplenishmentProcessModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.cronjob.TriggerService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class B2BAcceleratorCartToOrderJob extends AbstractJobPerformable<CartToOrderCronJobModel>
{

	private static final Logger LOG = Logger.getLogger(B2BAcceleratorCartToOrderJob.class);
	private BusinessProcessService businessProcessService;
	private TriggerService triggerService;
	private I18NService i18NService;

	@Override
	public PerformResult perform(final CartToOrderCronJobModel cronJob)
	{
		final String replenishmentOrderProcessCode = "replenishmentOrderProcess" + cronJob.getCode() + System.currentTimeMillis();
		final ReplenishmentProcessModel businessProcessModel = (ReplenishmentProcessModel) getBusinessProcessService()
				.createProcess(replenishmentOrderProcessCode, "replenishmentOrderProcess");
		businessProcessModel.setCartToOrderCronJob(cronJob);
		modelService.save(businessProcessModel);
		getBusinessProcessService().startProcess(businessProcessModel);
		modelService.refresh(businessProcessModel);

		while (businessProcessModel.getProcessState().equals(ProcessState.RUNNING)
				|| businessProcessModel.getProcessState().equals(ProcessState.CREATED))
		{
			modelService.refresh(businessProcessModel);
			if (businessProcessModel.getProcessState().equals(ProcessState.SUCCEEDED))
			{
				return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
			}
			else if (businessProcessModel.getProcessState().equals(ProcessState.ERROR)
					|| businessProcessModel.getProcessState().equals(ProcessState.FAILED))
			{
				return new PerformResult(CronJobResult.ERROR, CronJobStatus.UNKNOWN);
			}

			try
			{
				Thread.sleep(1000);
			}
			catch (final InterruptedException e)
			{
				LOG.warn("Thread interrupted " + e.getMessage());
			}
		}

		return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);

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

	protected TriggerService getTriggerService()
	{
		return triggerService;
	}

	@Required
	public void setTriggerService(final TriggerService triggerService)
	{
		this.triggerService = triggerService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}
}
