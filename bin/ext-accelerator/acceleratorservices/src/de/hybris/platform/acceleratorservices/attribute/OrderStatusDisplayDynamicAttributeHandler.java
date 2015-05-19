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
package de.hybris.platform.acceleratorservices.attribute;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhandler.DynamicAttributesOrderStatusDisplayByMap;
import de.hybris.platform.util.localization.Localization;


/**
 * Prevent exceptions being thrown by superclass.
 */
// TODO Remove class as has been fixed by https://jira.hybris.com/browse/BCOM-205
public class OrderStatusDisplayDynamicAttributeHandler extends DynamicAttributesOrderStatusDisplayByMap
{
	private String defaultStatus;

	@Override
	public String getDefaultStatus()
	{
		return defaultStatus;
	}

	@Override
	public void setDefaultStatus(final String defaultStatus)
	{
		this.defaultStatus = defaultStatus;
	}

	@Override
	public String get(final OrderModel order)
	{
		String statusLocalisationKey = getDefaultStatus();

		if (order != null && order.getStatus() != null)
		{
			final String statusCode = order.getStatus().getCode();
			final String statusDisplayEntry = getStatusDisplayMap().get(statusCode);
			if (statusDisplayEntry != null)
			{
				statusLocalisationKey = statusDisplayEntry;
			}
		}

		if (statusLocalisationKey == null || statusLocalisationKey.isEmpty())
		{
			return "";
		}
		return Localization.getLocalizedString(statusLocalisationKey);
	}
}
