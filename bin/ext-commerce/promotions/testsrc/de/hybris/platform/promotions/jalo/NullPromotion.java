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
package de.hybris.platform.promotions.jalo;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Test to ensure that promotions can be written in customer package.
 */
public class NullPromotion extends OrderPromotion
{
	@Override
	public List<PromotionResult> evaluate(final SessionContext ctx, final PromotionEvaluationContext promoContext)
	{
		final List<PromotionResult> promotionResults = new ArrayList<PromotionResult>();

		if (checkRestrictions(ctx, promoContext))
		{
			final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this, promoContext.getOrder(),
					1.0F);
			result.addAction(ctx, PromotionsManager.getInstance().createPromotionOrderAdjustTotalAction(ctx, 0));
			promotionResults.add(result);
		}

		return promotionResults;
	}

	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult result, final Locale locale)
	{
		final AbstractOrder order = result.getOrder(ctx);
		if (order != null)
		{
			if (result.getFired(ctx))
			{
				final Object[] args = {};
				return formatMessage("Fired", args, locale);
			}
			else if (result.getCouldFire(ctx))
			{
				final Object[] args = {};
				return formatMessage("Not Fired", args, locale);
			}
		}

		return "";
	}
}
