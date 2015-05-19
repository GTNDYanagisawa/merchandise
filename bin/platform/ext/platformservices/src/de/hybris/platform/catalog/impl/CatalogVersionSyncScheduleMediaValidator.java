/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.catalog.impl;

import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncScheduleMedia;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncScheduleMediaModel;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * {@link ValidateInterceptor} for the {@link CatalogVersionSyncScheduleMediaModel}. The media must contain a sync
 * cronjob.
 */
public class CatalogVersionSyncScheduleMediaValidator implements ValidateInterceptor
{
	private ModelService modelService;

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof CatalogVersionSyncScheduleMediaModel)
		{
			final CatalogVersionSyncScheduleMediaModel modelImpl = (CatalogVersionSyncScheduleMediaModel) model;
			final CatalogVersionSyncScheduleMedia cvssmJalo = modelService.getSource(modelImpl);
			try
			{
				if (cvssmJalo.getAttribute(CatalogVersionSyncScheduleMedia.CRONJOB) == null)
				{
					throw new InterceptorException("missing sync cronjob for creating a new " + cvssmJalo.getCode());
				}
			}
			catch (final JaloInvalidParameterException e)
			{
				throw new InterceptorException(e.getMessage(), e);
			}
			catch (final JaloSecurityException e)
			{
				throw new InterceptorException(e.getMessage(), e);
			}
		}
	}
}
