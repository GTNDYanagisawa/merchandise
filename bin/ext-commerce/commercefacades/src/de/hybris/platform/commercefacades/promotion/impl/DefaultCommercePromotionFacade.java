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
package de.hybris.platform.commercefacades.promotion.impl;

import de.hybris.platform.commercefacades.converter.ConfigurablePopulator;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionFacade;
import de.hybris.platform.commercefacades.promotion.PromotionOption;
import de.hybris.platform.commerceservices.promotion.CommercePromotionService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.OrderPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultCommercePromotionFacade implements CommercePromotionFacade
{
	private CommercePromotionService commercePromotionService;
	private Converter<AbstractPromotionModel, PromotionData> promotionsConverter;
	private ConfigurablePopulator<AbstractPromotionModel, PromotionData, PromotionOption> promotionConfiguredPopulator;
	private PromotionsService promotionsService;

	@Override
	public List<PromotionData> getProductPromotions()
	{
		final List<ProductPromotionModel> promotionList = getCommercePromotionService().getProductPromotions();
		return Converters.convertAll(promotionList, getPromotionsConverter());
	}

	@Override
	public List<PromotionData> getOrderPromotions()
	{
		final List<OrderPromotionModel> promotionList = getCommercePromotionService().getOrderPromotions();
		return Converters.convertAll(promotionList, getPromotionsConverter());
	}

	@Override
	public List<PromotionData> getProductPromotions(final String promotionGroup)
	{
		return getProductPromotions(Collections.singleton(promotionGroup));
	}

	@Override
	public List<PromotionData> getProductPromotions(final Collection<String> promotionGroups)
	{
		validatePromotionGroupsParameter(promotionGroups);

		final List<ProductPromotionModel> promotionList = getCommercePromotionService().getProductPromotions(
				getPromotionGroups(promotionGroups));
		return Converters.convertAll(promotionList, getPromotionsConverter());
	}

	protected void validatePromotionGroupsParameter(final Collection<String> promotionGroups)
	{
		if (promotionGroups == null)
		{
			throw new IllegalArgumentException("Parameter promotionGroups must not be null");
		}
		if (promotionGroups.isEmpty())
		{
			throw new IllegalArgumentException("Parameter promotionGroups must not be empty");
		}
	}

	protected Collection<PromotionGroupModel> getPromotionGroups(final Collection<String> promotionGroupCodes)
	{
		final Collection<PromotionGroupModel> promotionGroups = new ArrayList<PromotionGroupModel>();
		PromotionGroupModel groupModel;
		for (final String groupCode : promotionGroupCodes)
		{
			groupModel = getPromotionsService().getPromotionGroup(groupCode);
			if (groupModel != null)
			{
				promotionGroups.add(groupModel);
			}
			else
			{
				throw new IllegalArgumentException("Promotion group doesn't exist : " + groupCode);
			}
		}
		return promotionGroups;
	}

	@Override
	public List<PromotionData> getOrderPromotions(final String promotionGroup)
	{
		return getOrderPromotions(Collections.singleton(promotionGroup));
	}

	@Override
	public List<PromotionData> getOrderPromotions(final Collection<String> promotionGroups)
	{
		validatePromotionGroupsParameter(promotionGroups);

		final List<OrderPromotionModel> promotionList = getCommercePromotionService().getOrderPromotions(
				getPromotionGroups(promotionGroups));
		return Converters.convertAll(promotionList, getPromotionsConverter());
	}

	@Override
	public PromotionData getPromotion(final String code)
	{
		if (code == null)
		{
			throw new IllegalArgumentException("Parameter code must not be null");
		}

		final AbstractPromotionModel promotion = getCommercePromotionService().getPromotion(code);
		return getPromotionsConverter().convert(promotion);
	}

	@Override
	public PromotionData getPromotion(final String code, final Collection<PromotionOption> options)
	{
		if (code == null)
		{
			throw new IllegalArgumentException("Parameter code must not be null");
		}

		final AbstractPromotionModel promotionModel = getCommercePromotionService().getPromotion(code);
		final PromotionData promotionData = getPromotionsConverter().convert(promotionModel);

		if (options != null)
		{
			getPromotionConfiguredPopulator().populate(promotionModel, promotionData, options);
		}

		return promotionData;
	}

	public CommercePromotionService getCommercePromotionService()
	{
		return commercePromotionService;
	}

	@Required
	public void setCommercePromotionService(final CommercePromotionService commercePromotionService)
	{
		this.commercePromotionService = commercePromotionService;
	}

	public PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	@Required
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

	public Converter<AbstractPromotionModel, PromotionData> getPromotionsConverter()
	{
		return promotionsConverter;
	}

	@Required
	public void setPromotionsConverter(final Converter<AbstractPromotionModel, PromotionData> promotionsConverter)
	{
		this.promotionsConverter = promotionsConverter;
	}

	protected ConfigurablePopulator<AbstractPromotionModel, PromotionData, PromotionOption> getPromotionConfiguredPopulator()
	{
		return promotionConfiguredPopulator;
	}

	@Required
	public void setPromotionConfiguredPopulator(
			final ConfigurablePopulator<AbstractPromotionModel, PromotionData, PromotionOption> promotionConfiguredPopulator)
	{
		this.promotionConfiguredPopulator = promotionConfiguredPopulator;
	}
}
