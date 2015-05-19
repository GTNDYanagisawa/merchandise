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
import de.hybris.platform.jalo.order.AbstractOrderEntry;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.Unit;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Test to ensure that actions can be written in customer package.
 */
public class TestAction extends GeneratedPromotionOrderAddFreeGiftAction
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(PromotionOrderAddFreeGiftAction.class.getName());
	private final long quantity = 1;


	@Override
	public boolean apply(final SessionContext ctx)
	{
		final AbstractOrder order = getPromotionResult(ctx).getOrder(ctx);

		// Create a new order entry
		final Product product = this.getFreeProduct(ctx);
		final Unit unit = product.getUnit(ctx);

		if (log.isDebugEnabled())
		{
			log.debug("(" + getPK() + ") apply: Adding " + quantity + " free gift to Cart with " + order.getAllEntries().size()
					+ " order entries.");
		}
		final AbstractOrderEntry orderEntry = order.addNewEntry(product, 1, unit, false);
		if (log.isDebugEnabled())
		{
			log.debug("(" + getPK() + ") apply: Adding " + quantity + " free gift.  There are now " + order.getAllEntries().size()
					+ " order entries.");
		}

		orderEntry.setGiveAway(ctx, true);

		if (log.isDebugEnabled())
		{
			log.debug("(" + getPK() + ") apply: Created a free gift order entry with " + orderEntry.getDiscountValues(ctx).size()
					+ " discount values");
		}

		// Now show that the new order entry has been consumed by the Promotion
		final PromotionResult pr = this.getPromotionResult(ctx);

		// Create a new promotion order entry to hold the gift, adjusted unit price is 0
		final PromotionOrderEntryConsumed consumed = PromotionsManager.getInstance().createPromotionOrderEntryConsumed(ctx,
				this.getGuid(ctx), orderEntry, 1);
		consumed.setAdjustedUnitPrice(ctx, 0.0D);

		pr.addConsumedEntry(ctx, consumed);

		setMarkedApplied(ctx, true);

		// Return true, this action has added an order entry and therefore needs to calculate totals
		return true;
	}

	@Override
	public boolean undo(final SessionContext ctx)
	{
		final AbstractOrder order = getPromotionResult(ctx).getOrder(ctx);

		if (log.isDebugEnabled())
		{
			log.debug("(" + getPK() + ") undo: Undoing add free gift from order with " + order.getAllEntries().size()
					+ " order entries");
		}

		for (final AbstractOrderEntry aoe : (List<AbstractOrderEntry>) order.getAllEntries())
		{
			if (aoe.isGiveAway(ctx).booleanValue() && aoe.getProduct(ctx).equals(this.getFreeProduct(ctx))
					&& aoe.getQuantity(ctx).longValue() >= quantity)
			{
				final long remainingQuantityAfterUndo = aoe.getQuantity(ctx).longValue() - quantity;
				if (remainingQuantityAfterUndo < 1)
				{
					if (log.isDebugEnabled())
					{
						log.debug("(" + getPK()
								+ ") undo: Line item has the same or less quantity than the offer.  Removing whole order entry.");
					}
					order.removeEntry(aoe);
				}
				else
				{
					if (log.isDebugEnabled())
					{
						log.debug("("
								+ getPK()
								+ ") undo: Line item has a greater quantity than the offer.  Removing the offer quantity and resetting giveaway flag.");
					}
					aoe.setQuantity(ctx, remainingQuantityAfterUndo);
					aoe.setGiveAway(ctx, false);
				}

				// Remove promotion order entry consumed
				final PromotionResult pr = this.getPromotionResult(ctx);
				for (final PromotionOrderEntryConsumed poec : (Collection<PromotionOrderEntryConsumed>) pr.getConsumedEntries(ctx))
				{
					if (poec.getCode(ctx).equals(this.getGuid(ctx)))
					{
						pr.removeConsumedEntry(ctx, poec);
					}
				}

				break;
			}
		}

		setMarkedApplied(ctx, false);

		if (log.isDebugEnabled())
		{
			log.debug("(" + getPK() + ") undo: Free gift removed from order which now has " + order.getAllEntries().size()
					+ " order entries");
		}
		return true;
	}

	@Override
	public boolean isAppliedToOrder(final SessionContext ctx)
	{
		final AbstractOrder order = getPromotionResult(ctx).getOrder(ctx);

		for (final AbstractOrderEntry aoe : (List<AbstractOrderEntry>) order.getAllEntries())
		{
			if (aoe.isGiveAway(ctx).booleanValue() && aoe.getProduct(ctx).equals(this.getFreeProduct(ctx))
					&& aoe.getQuantity(ctx).longValue() >= quantity)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public double getValue(final SessionContext ctx)
	{
		return 0.0D;
	}

	/**
	 * Called to deep clone attributes of this instance
	 * <p/>
	 * The values map contains all the attributes defined on this instance. The map will be used to initialize a new
	 * instance of the Action that is a clone of this instance. This method can remove, replace or add to the Map of
	 * attributes.
	 * 
	 * @param ctx
	 *           The hybris context
	 * @param values
	 *           The map to write into
	 */
	@Override
	protected void deepCloneAttributes(final SessionContext ctx, final Map values)
	{
		super.deepCloneAttributes(ctx, values);

		// leave all attributes in map
	}

}
