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
package de.hybris.platform.order.interceptors;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;


/**
 * Before an abstract order entry gets removed, the interceptor marks the owning order as not calculated.
 */
public class DefaultAbstractOrderEntryRemoveInterceptor implements RemoveInterceptor
{


	@Override
	public void onRemove(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof AbstractOrderEntryModel)
		{
			final AbstractOrderEntryModel entryModel = (AbstractOrderEntryModel) model;

			final AbstractOrderModel owningOrder = entryModel.getOrder();
			if (owningOrder == null)
			{
				throw new InterceptorException("Owning order cannot be null");
			}
			if (Boolean.TRUE.equals(owningOrder.getCalculated()))
			{
				owningOrder.setCalculated(Boolean.FALSE);
			}
		}

	}
}
