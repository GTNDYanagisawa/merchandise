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
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryAdjustAction;
import de.hybris.platform.promotions.jalo.PromotionOrderEntryConsumed;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.result.PromotionEvaluationContext;
import de.hybris.platform.promotions.result.PromotionOrderEntry;
import de.hybris.platform.promotions.result.PromotionOrderView;
import de.hybris.platform.promotions.util.Helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;


/**
 * ProductThresholdPriceDiscountPromotion
 * 
 * This promotion class can apply discounts at product level once a threshold for product cost is reached. Get X$
 * discount for purchasing a product more than Y$.
 * 
 */
public class ProductThresholdPriceDiscountPromotion extends GeneratedProductThresholdPriceDiscountPromotion
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ProductThresholdPriceDiscountPromotion.class.getName());

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

		// Find all valid products in the cart
		final PromotionsManager.RestrictionSetResult restrictionResult = this.findEligibleProductsInBasket(ctx, promoContext);

		if (restrictionResult.isAllowedToContinue() && !restrictionResult.getAllowedProducts().isEmpty())
		{
			final PromotionOrderView view = promoContext.createView(ctx, this, restrictionResult.getAllowedProducts());
			long quantityToDiscount = 0L;

			//Enter this loop only when we have order entries that are yet to be processed
			while (view.getTotalQuantity(ctx) > 0)
			{
				//Create a comparator to sort the order entries by price
				final Comparator comparator = PromotionEvaluationContext.createPriceComparator(ctx);
				//Get the bottom most order entry since it will be the one with highest price after sorting
				final PromotionOrderEntry entry = view.peekFromTail(ctx, comparator);
				//Get the quantity of entries to which discount has to be applied
				quantityToDiscount = entry.getQuantity(ctx);

				final double originalUnitPrice = entry.getBasePrice(ctx).doubleValue();
				//Threshold price above which this promotion has to be fired
				final Double threshold = this.getPriceForOrder(ctx, this.getProductThresholdPrice(ctx), promoContext.getOrder(),
						ProductThresholdPriceDiscountPromotion.PRODUCTTHRESHOLDPRICE);

				final Currency currency = promoContext.getOrder().getCurrency(ctx);
				//Check whether the current order entry price is greater than the threshold value configured above which the promotion will be fired
				if (originalUnitPrice > threshold.doubleValue())
				{
					final long quantityOfOrderEntry = entry.getBaseOrderEntry().getQuantity(ctx).longValue();
					//Start logging that this promotion is being worked
					promoContext.startLoggingConsumed(this);

					//Original order entry price calculated by multiplying the quantityToDiscount and the unit price
					final double originalEntryPrice = quantityToDiscount * originalUnitPrice;
					//Get the discount price 
					final Double discountPriceValue = this.getPriceForOrder(ctx, this.getProductPriceDiscount(ctx),
							promoContext.getOrder(), ProductThresholdPriceDiscountPromotion.PRODUCTPRICEDISCOUNT);

					//Entry price after applying the discount
					final BigDecimal adjustedEntryPrice = Helper.roundCurrencyValue(ctx, currency, originalEntryPrice
							- (quantityToDiscount * discountPriceValue.doubleValue()));

					// Calculate the unit price and round it
					final BigDecimal adjustedUnitPrice = Helper.roundCurrencyValue(
							ctx,
							currency,
							adjustedEntryPrice.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : adjustedEntryPrice.divide(
									BigDecimal.valueOf(quantityToDiscount), RoundingMode.HALF_EVEN));


					//Consume it from tail
					for (final PromotionOrderEntryConsumed poec : view.consumeFromTail(ctx, comparator, quantityToDiscount))
					{
						poec.setAdjustedUnitPrice(ctx, adjustedUnitPrice.doubleValue());
					}

					final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
							promoContext.getOrder(), 1.0F);
					//Finish the logging that work is done otherwise exception will be thrown
					result.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, true));

					final BigDecimal adjustment = Helper.roundCurrencyValue(ctx, currency,
							adjustedEntryPrice.subtract(BigDecimal.valueOf(originalEntryPrice)));

					final PromotionOrderEntryAdjustAction poeac = PromotionsManager.getInstance()
							.createPromotionOrderEntryAdjustAction(ctx, entry.getBaseOrderEntry(), quantityOfOrderEntry,
									adjustment.doubleValue());
					result.addAction(ctx, poeac);
					promotionResults.add(result);
				}
				else
				{
					// Check to see if there are still some qualifying products in the basket
					final long remainingCount = view.getTotalQuantity(ctx);
					if (remainingCount > 0)
					{
						/*
						 * Execute when a stage has reached where the entry prices are less than the threshold price. Create
						 * dummy promotion as PromotionManager expects a promotion result. The dummy promotion can be created
						 * with a certainity factor lesser than 1 and exit the while loop as other entries must be lesser than
						 * threshold value since we have sorted.
						 */
						final float certainity = (float) (originalUnitPrice / threshold.doubleValue());
						promoContext.startLoggingConsumed(this);
						view.consume(ctx, remainingCount);
						final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
								promoContext.getOrder(), (certainity == 1) ? 0.5F : certainity);
						result.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, false));
						promotionResults.add(result);
						break;
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
			final de.hybris.platform.jalo.c2l.Currency currency = order.getCurrency(ctx);
			final Double totalDiscount = Double.valueOf(promotionResult.getTotalDiscount(ctx));
			final Double thresholdPrice = this.getPriceForOrder(ctx, this.getProductThresholdPrice(ctx), order,
					ProductThresholdPriceDiscountPromotion.PRODUCTTHRESHOLDPRICE);

			if (totalDiscount.doubleValue() > 0D)
			{
				if (promotionResult.getFired(ctx))
				{
					final Object[] args =
					{ Helper.formatCurrencyAmount(ctx, locale, currency, totalDiscount.doubleValue()),
							Helper.formatCurrencyAmount(ctx, locale, currency, thresholdPrice.doubleValue()) };
					return formatMessage(this.getMessageFired(ctx), args, locale);
				}
			}
			else if (promotionResult.getCouldFire(ctx))
			{
				return this.getMessageCouldHaveFired();
			}
		}
		return "";
	}

	@Override
	protected void buildDataUniqueKey(final SessionContext ctx, final StringBuilder builder)
	{
		super.buildDataUniqueKey(ctx, builder);
		buildDataUniqueKeyForPriceRows(ctx, builder, getProductPriceDiscount(ctx));
	}
}
