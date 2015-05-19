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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.commerceservices.event.ForgottenPwdEvent;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.springframework.beans.factory.annotation.Required;


/**
 * Event listener for forgotten password functionality.
 */
public class ForgottenPasswordEventListener extends AbstractSiteEventListener<ForgottenPwdEvent>
{
	private BusinessProcessService businessProcessService;
	private ModelService modelService;

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
	protected void onSiteEvent(final ForgottenPwdEvent forgottenPwdEvent)
	{
		final ForgottenPasswordProcessModel forgottenPasswordProcessModel = (ForgottenPasswordProcessModel) getBusinessProcessService()
				.createProcess(
						"b2bForgottenPassword" + "-" + forgottenPwdEvent.getCustomer().getUid() + "-" + System.currentTimeMillis(),
						"b2bForgottenPasswordEmailProcess");
		forgottenPasswordProcessModel.setSite(forgottenPwdEvent.getSite());
		forgottenPasswordProcessModel.setCustomer(forgottenPwdEvent.getCustomer());
		forgottenPasswordProcessModel.setToken(forgottenPwdEvent.getToken());
		forgottenPasswordProcessModel.setLanguage(forgottenPwdEvent.getLanguage());
		forgottenPasswordProcessModel.setCurrency(forgottenPwdEvent.getCurrency());
		forgottenPasswordProcessModel.setStore(forgottenPwdEvent.getBaseStore());
		getModelService().save(forgottenPasswordProcessModel);
		getBusinessProcessService().startProcess(forgottenPasswordProcessModel);
	}

	@Override
	protected boolean shouldHandleEvent(final ForgottenPwdEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return SiteChannel.B2B.equals(site.getChannel());
	}
}
