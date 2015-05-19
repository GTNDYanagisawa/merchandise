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
package de.hybris.platform.acceleratorservices.urlencoder.attributes.impl;


import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;


/**
 * Default implementation for storefront attribute handler.
 * 
 */
public class DefaultStoreFrontAttributeManager extends AbstractUrlEncodingAttributeManager
{
	@Override
	public Collection<String> getAllAvailableValues()
	{
		return CollectionUtils.collect(getCmsSiteService().getCurrentSite().getStores(), new Transformer()
		{
			@Override
			public Object transform(final Object object)
			{
				return ((BaseStoreModel) object).getUid();
			}
		});
	}

	@Override
	public void updateAndSyncForAttrChange(final String value)
	{
		//Do nothing for Storefront attribute
	}

	@Override
	public String getDefaultValue()
	{
		return getCmsSiteService().getCurrentSite().getUid();
	}

	@Override
	public String getCurrentValue()
	{
		return getCmsSiteService().getCurrentSite().getUid();
	}

	@Override
	public String getAttributeValueForEmail(final BusinessProcessModel businessProcessModel)
	{
		if(businessProcessModel instanceof StoreFrontCustomerProcessModel)
		{
			return ((StoreFrontCustomerProcessModel) businessProcessModel).getStore().getUid();
		}
		else if(businessProcessModel instanceof OrderProcessModel)
		{
			return ((OrderProcessModel)businessProcessModel).getOrder().getStore().getUid();
		}
		return getDefaultValue();
	}
}
