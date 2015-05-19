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

import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.promotion.data.PromotionRestrictionData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.AbstractPromotionRestrictionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator implementation for {@link de.hybris.platform.promotions.model.AbstractPromotionModel} as source and
 * {@link de.hybris.platform.commercefacades.product.data.PromotionData} as target type.
 */
public class PromotionExtendedPopulator implements Populator<AbstractPromotionModel, PromotionData>
{
	private Converter<AbstractPromotionRestrictionModel, PromotionRestrictionData> promotionRestrictionConverter;

	@Override
	public void populate(final AbstractPromotionModel source, final PromotionData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setTitle(source.getTitle());
		target.setStartDate(source.getStartDate());
		target.setEnabled(source.getEnabled());
		target.setPriority(source.getPriority());
		target.setPromotionGroup(source.getPromotionGroup().getIdentifier());
		target.setRestrictions(convertRestrictions(source.getRestrictions()));
	}

	protected Collection<PromotionRestrictionData> convertRestrictions(
			final Collection<AbstractPromotionRestrictionModel> restrictions)
	{
		return Converters.convertAll(restrictions, promotionRestrictionConverter);
	}

	protected Converter<AbstractPromotionRestrictionModel, PromotionRestrictionData> getPromotionRestrictionConverter()
	{
		return promotionRestrictionConverter;
	}

	@Required
	public void setPromotionRestrictionConverter(
			final Converter<AbstractPromotionRestrictionModel, PromotionRestrictionData> promotionRestrictionConverter)
	{
		this.promotionRestrictionConverter = promotionRestrictionConverter;
	}
}
