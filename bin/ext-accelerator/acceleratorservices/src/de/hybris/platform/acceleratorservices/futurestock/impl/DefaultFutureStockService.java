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
package de.hybris.platform.acceleratorservices.futurestock.impl;

import de.hybris.platform.acceleratorservices.futurestock.FutureStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Default implementation for {@link FutureStockService}, just to avoid problems with Spring initialization. This
 * service will just return dummy values. To have a service that actually returns future availability information,
 * please use another implementation.
 */
public class DefaultFutureStockService implements FutureStockService
{
	private static final Logger LOGGER = Logger.getLogger(DefaultFutureStockService.class);

	private static final int DEFAULT_FUTURE_STOCK_LEVEL_QUANTITY = 10;

	@Override
	public Map<String, Map<Date, Integer>> getFutureAvailability(final List<ProductModel> products)
	{
		LOGGER.warn("Using default implementation of FutureStockService, that return dummy values. Use another implementation to get real future availability.");

		final Map<String, Map<Date, Integer>> productsMap = new HashMap<>();

		for (final ProductModel product : products)
		{
			if (!product.getStockLevels().isEmpty())
			{
				final HashMap<Date, Integer> futureAvailability = new HashMap<>();

				final StockLevelModel stockLevel = product.getStockLevels().iterator().next();
				// assuming maxPreOrder amount gets re-stocked
				int quantity = stockLevel.getMaxPreOrder();

				if (quantity <= 0)
				{
					quantity = DEFAULT_FUTURE_STOCK_LEVEL_QUANTITY;
				}

				final Calendar calendar = Calendar.getInstance();
				// now in a week + 33 years (to avoid thinking it return real values)
				calendar.add(Calendar.WEEK_OF_YEAR, 1);
				calendar.add(Calendar.YEAR, 33);

				futureAvailability.put(new Date(calendar.getTimeInMillis()), Integer.valueOf(quantity));
				productsMap.put(product.getCode(), futureAvailability);
			}
		}

		return productsMap;
	}

}
