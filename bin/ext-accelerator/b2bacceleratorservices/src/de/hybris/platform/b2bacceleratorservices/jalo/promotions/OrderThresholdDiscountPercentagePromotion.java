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
package de.hybris.platform.b2bacceleratorservices.jalo.promotions;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.util.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;


public class OrderThresholdDiscountPercentagePromotion extends GeneratedOrderThresholdDiscountPercentagePromotion
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(OrderThresholdDiscountPercentagePromotion.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.promotions.jalo.AbstractPromotion#evaluate(de.hybris.platform.jalo.SessionContext,
	 * de.hybris.platform.promotions.result.PromotionEvaluationContext)
	 */
	@Override
	public List<PromotionResult> evaluate(final SessionContext ctx, final PromotionEvaluationContext promoContext)
	{
		final List<PromotionResult> promotionResults = new ArrayList<PromotionResult>();

		if (checkRestrictions(ctx, promoContext))
		{
			final Double threshold = this.getPriceForOrder(ctx, this.getThresholdTotals(ctx), promoContext.getOrder(),
					OrderThresholdDiscountPercentagePromotion.THRESHOLDTOTALS);
			if (threshold != null)
			{
				// Get the discount price
				final Double discountPriceValue = Double.valueOf(this.getPercentageDiscount(ctx).doubleValue() / 100D);
				if (discountPriceValue != null)
				{
					final AbstractOrder order = promoContext.getOrder();
					final double orderSubtotalAfterDiscounts = getOrderSubtotalAfterDiscounts(ctx, order);

					// If we pass the threshold then fire, and add an action to put a discount in the basket
					if (orderSubtotalAfterDiscounts >= threshold.doubleValue())
					{
						if (log.isDebugEnabled())
						{
							log.debug("(" + getPK() + ") evaluate: Subtotal " + orderSubtotalAfterDiscounts + ">" + threshold
									+ ".  Creating a discount action for value:" + discountPriceValue + ".");
						}
						final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
								promoContext.getOrder(), 1.0F);

						result.addAction(
								ctx,
								PromotionsManager.getInstance().createPromotionOrderAdjustTotalAction(ctx,
										-(orderSubtotalAfterDiscounts * discountPriceValue.doubleValue())));

						promotionResults.add(result);
					}
					// Otherwise calculate the certainty by seeing how close the order is to meeting the threshold
					else
					{
						if (log.isDebugEnabled())
						{
							log.debug("(" + getPK() + ") evaluate: Subtotal " + orderSubtotalAfterDiscounts + "<" + threshold
									+ ".  Skipping discount action.");
						}
						final float certainty = (float) (orderSubtotalAfterDiscounts / threshold.doubleValue());
						final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
								promoContext.getOrder(), certainty);
						promotionResults.add(result);
					}
				}
			}
		}

		return promotionResults;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.promotions.jalo.AbstractPromotion#getResultDescription(de.hybris.platform.jalo.SessionContext,
	 * de.hybris.platform.promotions.jalo.PromotionResult, java.util.Locale)
	 */
	@Override
	public String getResultDescription(final SessionContext ctx, final PromotionResult promotionResult, final Locale locale)
	{
		final AbstractOrder order = promotionResult.getOrder(ctx);
		if (order != null)
		{
			final de.hybris.platform.jalo.c2l.Currency orderCurrency = order.getCurrency(ctx);

			final Double threshold = this.getPriceForOrder(ctx, this.getThresholdTotals(ctx), order,
					OrderThresholdDiscountPercentagePromotion.THRESHOLDTOTALS);
			final double orderSubtotalAfterDiscounts = getOrderSubtotalAfterDiscounts(ctx, order);

			if (threshold != null)
			{
				//	final double discountPercentValue = this.getPercentageDiscount(ctx).doubleValue() / 100D;

				// Get the discount price
				final Double discountPriceValue = Double.valueOf(promotionResult.getTotalDiscount(ctx));


				if (discountPriceValue != null)
				{
					if (promotionResult.getFired(ctx))
					{
						// "You saved {3} for spending over {1}"
						final Object[] args =
						{ threshold, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, threshold.doubleValue()),
								discountPriceValue,
								Helper.formatCurrencyAmount(ctx, locale, orderCurrency, discountPriceValue.doubleValue()) };
						return formatMessage(this.getMessageFired(ctx), args, locale);
					}
					else if (promotionResult.getCouldFire(ctx))
					{
						final double amountRequired = threshold.doubleValue() - orderSubtotalAfterDiscounts;

						// "Spend {1} to get a discount of {3} - Spend another {5} to qualify"
						final Object[] args =
						{ threshold, Helper.formatCurrencyAmount(ctx, locale, orderCurrency, threshold.doubleValue()),
								discountPriceValue,
								Helper.formatCurrencyAmount(ctx, locale, orderCurrency, discountPriceValue.doubleValue()),
								Double.valueOf(amountRequired), Helper.formatCurrencyAmount(ctx, locale, orderCurrency, amountRequired) };
						return formatMessage(this.getMessageCouldHaveFired(ctx), args, locale);
					}
				}

			}
		}
		return "";
	}

	@Override
	protected void buildDataUniqueKey(final SessionContext ctx, final StringBuilder builder)
	{
		super.buildDataUniqueKey(ctx, builder);
		buildDataUniqueKeyForPriceRows(ctx, builder, getThresholdTotals(ctx));
		builder.append(getPercentageDiscount(ctx)).append('|');
	}
}
