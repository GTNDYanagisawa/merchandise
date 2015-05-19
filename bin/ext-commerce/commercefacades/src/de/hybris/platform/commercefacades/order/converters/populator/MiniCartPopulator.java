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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;

import org.springframework.util.Assert;


public class MiniCartPopulator extends AbstractOrderPopulator<CartModel, CartData>
{

	@Override
	public void populate(final CartModel source, final CartData target)
	{
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source == null)
		{
			target.setTotalPrice(createZeroPrice());
			target.setDeliveryCost(null);
			target.setSubTotal(createZeroPrice());
			target.setTotalItems(Integer.valueOf(0));
			target.setTotalUnitCount(Integer.valueOf(0));
		}
		else
		{
			addCommon(source, target);
			addTotals(source, target);

			target.setTotalUnitCount(calcTotalUnitCount(source));
		}
	}
}
