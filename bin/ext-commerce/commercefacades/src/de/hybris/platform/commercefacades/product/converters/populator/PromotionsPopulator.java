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
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.AbstractPromotion;
import de.hybris.platform.promotions.jalo.ProductPromotion;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.promotions.model.AbstractPromotionModel} as source and {@link de.hybris.platform.commercefacades.product.data.PromotionData} as target type.
 */
public class PromotionsPopulator implements Populator<AbstractPromotionModel, PromotionData>
{
	private CartService cartService;
	private ModelService modelService;
	private PromotionsService promotionService;

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setPromotionService(final PromotionsService promotionService)
	{
		this.promotionService = promotionService;
	}

	protected PromotionsService getPromotionService()
	{
		return promotionService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Override
	public void populate(final AbstractPromotionModel source, final PromotionData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setEndDate(source.getEndDate());
		target.setDescription(source.getDescription());
		target.setPromotionType(source.getPromotionType());
		processPromotionMessages(source, target);
	}

	protected void processPromotionMessages(final AbstractPromotionModel source, final PromotionData prototype)
	{
		if (getCartService().hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			if (cartModel != null)
			{
				final AbstractPromotion promotion = getModelService().getSource(source);

				final PromotionOrderResults promoOrderResults = getPromotionService().getPromotionResults(cartModel);
				if (promoOrderResults != null)
				{
					prototype.setCouldFireMessages(getCouldFirePromotionsMessages(promoOrderResults, promotion));
					prototype.setFiredMessages(getFiredPromotionsMessages(promoOrderResults, promotion));
				}
			}
		}
	}

	protected List<String> getCouldFirePromotionsMessages(final PromotionOrderResults promoOrderResults,
			final AbstractPromotion promotion)
	{
		final List<String> descriptions = new LinkedList<String>();

		if (promotion instanceof ProductPromotion)
		{
			addDescriptions(descriptions, filter(promoOrderResults.getPotentialProductPromotions(), promotion));
		}
		else
		{
			addDescriptions(descriptions, filter(promoOrderResults.getPotentialOrderPromotions(), promotion));

		}
		return descriptions;
	}

	protected List<String> getFiredPromotionsMessages(final PromotionOrderResults promoOrderResults,
			final AbstractPromotion promotion)
	{
		final List<String> descriptions = new LinkedList<String>();

		if (promotion instanceof ProductPromotion)
		{
			addDescriptions(descriptions, filter(promoOrderResults.getFiredProductPromotions(), promotion));
		}
		else
		{
			addDescriptions(descriptions, filter(promoOrderResults.getFiredOrderPromotions(), promotion));
		}

		return descriptions;
	}

	protected void addDescriptions(final List<String> descriptions, final List<PromotionResult> promotionResults)
	{
		if (promotionResults != null)
		{
			for (final PromotionResult promoResult : promotionResults)
			{
				descriptions.add(promoResult.getDescription());
			}
		}
	}

	protected List<PromotionResult> filter(final List<PromotionResult> results, final AbstractPromotion promotion)
	{
		final List<PromotionResult> filteredResults = new LinkedList<PromotionResult>();
		for (final PromotionResult result : results)
		{
			if (result.getPromotion().equals(promotion))
			{
				filteredResults.add(result);
			}
		}
		return filteredResults;
	}
}
