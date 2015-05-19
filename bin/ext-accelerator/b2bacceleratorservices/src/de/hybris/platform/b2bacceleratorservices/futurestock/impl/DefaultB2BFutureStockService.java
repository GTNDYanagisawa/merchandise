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
package de.hybris.platform.b2bacceleratorservices.futurestock.impl;

import de.hybris.platform.acceleratorservices.futurestock.FutureStockService;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default B2B implementation for {@link FutureStockService}. This service will just return empty values. To have a
 * service that actually returns future availability information, please use another implementation.
 */
public class DefaultB2BFutureStockService implements FutureStockService
{

	@Override
	public Map<String, Map<Date, Integer>> getFutureAvailability(final List<ProductModel> products)
	{
		final Map<String, Map<Date, Integer>> productsMap = new HashMap<>();

		for (final ProductModel product : products)
		{
			if (!product.getStockLevels().isEmpty())
			{
				final HashMap<Date, Integer> futureAvailability = new HashMap<>();
				productsMap.put(product.getCode(), futureAvailability);
			}
		}

		return productsMap;
	}

}
