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


import de.hybris.platform.b2bacceleratorservices.jalo.B2BAcceleratorServicesManager;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
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
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * ProductPriceDiscountPromotionByPaymentType This promotion is mainly applicable for scenarios where we fire promotions
 * only for certain users based on the payment type. For example: Get X% of discount when you checkout with Account
 * details.
 * 
 */
public class ProductPriceDiscountPromotionByPaymentType extends GeneratedProductPriceDiscountPromotionByPaymentType
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ProductPriceDiscountPromotionByPaymentType.class.getName());

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
			final AbstractOrder order = promoContext.getOrder();

			for (final PromotionOrderEntry entry : view.getAllEntries(ctx))
			{
				// Get the next order entry
				final long quantityToDiscount = entry.getQuantity(ctx);
				if (quantityToDiscount > 0)
				{
					final long quantityOfOrderEntry = entry.getBaseOrderEntry().getQuantity(ctx).longValue();

					// The adjustment to the order entry
					final double originalUnitPrice = entry.getBasePrice(ctx).doubleValue();
					final double originalEntryPrice = quantityToDiscount * originalUnitPrice;

					final Currency currency = promoContext.getOrder().getCurrency(ctx);
					Double discountPriceValue;

					final EnumerationValue paymentType = B2BAcceleratorServicesManager.getInstance().getPaymentType(ctx, order);

					if (paymentType != null && StringUtils.equalsIgnoreCase(paymentType.getCode(), getPaymentType().getCode()))
					{
						promoContext.startLoggingConsumed(this);
						discountPriceValue = this.getPriceForOrder(ctx, this.getProductDiscountPrice(ctx), promoContext.getOrder(),
								ProductPriceDiscountPromotionByPaymentType.PRODUCTDISCOUNTPRICE);

						final BigDecimal adjustedEntryPrice = Helper.roundCurrencyValue(ctx, currency, originalEntryPrice
								- (quantityToDiscount * discountPriceValue.doubleValue()));
						// Calculate the unit price and round it
						final BigDecimal adjustedUnitPrice = Helper.roundCurrencyValue(
								ctx,
								currency,
								adjustedEntryPrice.equals(BigDecimal.ZERO) ? BigDecimal.ZERO : adjustedEntryPrice.divide(
										BigDecimal.valueOf(quantityToDiscount), RoundingMode.HALF_EVEN));


						for (final PromotionOrderEntryConsumed poec : view.consume(ctx, quantityToDiscount))
						{
							poec.setAdjustedUnitPrice(ctx, adjustedUnitPrice.doubleValue());
						}

						final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
								promoContext.getOrder(), 1.0F);
						result.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, true));
						final BigDecimal adjustment = Helper.roundCurrencyValue(ctx, currency,
								adjustedEntryPrice.subtract(BigDecimal.valueOf(originalEntryPrice)));
						final PromotionOrderEntryAdjustAction poeac = PromotionsManager.getInstance()
								.createPromotionOrderEntryAdjustAction(ctx, entry.getBaseOrderEntry(), quantityOfOrderEntry,
										adjustment.doubleValue());
						result.addAction(ctx, poeac);
						promotionResults.add(result);
					}
				}
			}
			final long remainingCount = view.getTotalQuantity(ctx);
			if (remainingCount > 0)
			{
				promoContext.startLoggingConsumed(this);
				view.consume(ctx, remainingCount);
				final PromotionResult result = PromotionsManager.getInstance().createPromotionResult(ctx, this,
						promoContext.getOrder(), 0.5F);
				result.setConsumedEntries(ctx, promoContext.finishLoggingAndGetConsumed(this, false));
				promotionResults.add(result);
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

			if (totalDiscount.doubleValue() > 0D)
			{
				if (promotionResult.getFired(ctx))
				{
					final Object[] args =
					{ Helper.formatCurrencyAmount(ctx, locale, currency, totalDiscount.doubleValue()),
							B2BAcceleratorServicesManager.getInstance().getPaymentType(ctx, order) };
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
		buildDataUniqueKeyForPriceRows(ctx, builder, getProductDiscountPrice(ctx));
	}

}
