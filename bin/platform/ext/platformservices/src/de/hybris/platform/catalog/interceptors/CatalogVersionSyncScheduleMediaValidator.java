/*
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.catalog.interceptors;

import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncScheduleMediaModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class CatalogVersionSyncScheduleMediaValidator implements ValidateInterceptor
{
	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof CatalogVersionSyncScheduleMediaModel)
		{
			if (((CatalogVersionSyncScheduleMediaModel) model).getCronjob() == null)
			{
				throw new InterceptorException("mssing sync cronjob for creating a new " + CatalogVersionSyncScheduleMediaModel
						  ._TYPECODE);
			}
		}
	}
}
