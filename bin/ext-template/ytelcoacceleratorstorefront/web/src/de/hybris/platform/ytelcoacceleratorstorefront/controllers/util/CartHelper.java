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
package de.hybris.platform.ytelcoacceleratorstorefront.controllers.util;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Helper that contains cart data related utility methods
 */
public class CartHelper
{
	public static List<OrderEntryData> removeEmptyEntries(final List<OrderEntryData> allEntries)
	{
		if (CollectionUtils.isEmpty(allEntries))
		{
			return Collections.EMPTY_LIST;
		}

		final List<OrderEntryData> realEntries = new ArrayList<OrderEntryData>();
		for (final OrderEntryData entry : allEntries)
		{
			if (entry.getProduct() != null)
			{
				realEntries.add(entry);
			}
		}

		if (CollectionUtils.isEmpty(realEntries))
		{
			return Collections.EMPTY_LIST;
		}
		else
		{
			return realEntries;
		}
	}
}
