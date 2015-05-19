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
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;


/**
 * Default implementation for language attribute handler. This changes the store language if language is included as one
 * of the encoding attribute.
 */
public class DefaultLanguageAttributeManager extends AbstractUrlEncodingAttributeManager
{
	@Override
	public Collection<String> getAllAvailableValues()
	{
		return CollectionUtils.collect(getCommerceCommonI18NService().getAllLanguages(), new Transformer()
		{
			@Override
			public Object transform(final Object object)
			{
				return ((LanguageModel) object).getIsocode();
			}
		});
	}

	@Override
	public void updateAndSyncForAttrChange(final String value)
	{
		if (isValid(value))
		{
			getStoreSessionService().setCurrentLanguage(value);
		}
	}

	@Override
	public String getDefaultValue()
	{
		return getCommerceCommonI18NService().getDefaultLanguage().getIsocode();
	}

	@Override
	public String getCurrentValue()
	{
		return getCommerceCommonI18NService().getCurrentLanguage().getIsocode();
	}


	@Override
	public String getAttributeValueForEmail(final BusinessProcessModel businessProcessModel)
	{
		if(businessProcessModel instanceof StoreFrontCustomerProcessModel)
		{
			return ((StoreFrontCustomerProcessModel) businessProcessModel).getLanguage().getIsocode();
		}
		else if(businessProcessModel instanceof OrderProcessModel)
		{
			return ((OrderProcessModel)businessProcessModel).getOrder().getLanguage().getIsocode();
		}
		else if(businessProcessModel instanceof ConsignmentProcessModel)
		{
			if (((ConsignmentProcessModel)businessProcessModel).getConsignment().getOrder() instanceof OrderModel)
			{
				return ((OrderModel)((ConsignmentProcessModel)businessProcessModel).getConsignment().getOrder()).getLanguage().getIsocode();
			}
		}
		return getDefaultValue();
	}

}
