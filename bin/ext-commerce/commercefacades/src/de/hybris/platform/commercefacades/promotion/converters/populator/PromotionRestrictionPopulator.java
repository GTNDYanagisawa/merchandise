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
package de.hybris.platform.commercefacades.promotion.converters.populator;

import de.hybris.platform.commercefacades.promotion.data.PromotionRestrictionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


/**
 * Populator implementation for {@link de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel} as source
 * and {@link de.hybris.platform.commercefacades.promotion.data.PromotionRestrictionData} as target type.
 */
public class PromotionRestrictionPopulator implements Populator<AbstractPromotionRestrictionModel, PromotionRestrictionData>
{
	@Override
	public void populate(final AbstractPromotionRestrictionModel source, final PromotionRestrictionData target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setRestrictionType(source.getRestrictionType());
		target.setDescription(source.getRenderedDescription());
	}
}
