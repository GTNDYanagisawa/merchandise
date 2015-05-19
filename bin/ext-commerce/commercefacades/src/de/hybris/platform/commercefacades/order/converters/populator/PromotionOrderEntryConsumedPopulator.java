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

import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;

import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.core.model.order.OrderModel} as source and
 * {@link de.hybris.platform.commercefacades.order.data.OrderHistoryData} as target type.
 * 
 */
public class PromotionOrderEntryConsumedPopulator implements
		Populator<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData>
{

	@Override
	public void populate(final PromotionOrderEntryConsumedModel source, final PromotionOrderEntryConsumedData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setAdjustedUnitPrice(source.getAdjustedUnitPrice());
		if (source.getOrderEntry() != null)
		{
			target.setOrderEntryNumber(source.getOrderEntry().getEntryNumber());
		}
		target.setQuantity(source.getQuantity());
	}
}
