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
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.promotions.PromotionResultService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 */
public class PromotionResultPopulator implements Populator<PromotionResultModel, PromotionResultData>
{
	private PromotionResultService promotionResultService;
	private Converter<AbstractPromotionModel, PromotionData> promotionsConverter;
	private Converter<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData> promotionOrderEntryConsumedConverter;

	protected PromotionResultService getPromotionResultService()
	{
		return promotionResultService;
	}

	@Required
	public void setPromotionResultService(final PromotionResultService promotionResultService)
	{
		this.promotionResultService = promotionResultService;
	}

	protected Converter<AbstractPromotionModel, PromotionData> getPromotionsConverter()
	{
		return promotionsConverter;
	}

	@Required
	public void setPromotionsConverter(final Converter<AbstractPromotionModel, PromotionData> promotionsConverter)
	{
		this.promotionsConverter = promotionsConverter;
	}

	protected Converter<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData> getPromotionOrderEntryConsumedConverter()
	{
		return promotionOrderEntryConsumedConverter;
	}

	@Required
	public void setPromotionOrderEntryConsumedConverter(
			final Converter<PromotionOrderEntryConsumedModel, PromotionOrderEntryConsumedData> promotionOrderEntryConsumedConverter)
	{
		this.promotionOrderEntryConsumedConverter = promotionOrderEntryConsumedConverter;
	}

	@Override
	public void populate(final PromotionResultModel source, final PromotionResultData target)
	{
		target.setDescription(getPromotionResultService().getDescription(source));
		target.setPromotionData(getPromotionsConverter().convert(source.getPromotion()));
		target.setConsumedEntries(Converters.convertAll(source.getConsumedEntries(), getPromotionOrderEntryConsumedConverter()));
	}
}
