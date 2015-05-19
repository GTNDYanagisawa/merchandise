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

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Ignore;


@Ignore
public abstract class AbstractPromotionTest extends ServicelayerTransactionalTest
{

	private static final Logger LOG = Logger.getLogger(AbstractPromotionTest.class);

	/** Reference to current session. */


	protected void createPromotionData() throws ImpExException
	{
		importCsv("/test/promotionTestData.csv", "windows-1252");
	}



	PromotionsManager getPromotionManager()
	{
		return PromotionsManager.getInstance();
	}


	/**
	 * Method calculates promotions for order by call apply for each promotionResult. It should be executed if promotion
	 * prices weren't adjusted after use of PromotionManager.promotionUpdate.
	 */

	boolean calculateWithPromotions(final AbstractOrder order) throws JaloPriceFactoryException
	{
		// final Date date = order instanceof Cart ? new Date() : order.getDate();

		// getPromotionManager().updatePromotions(promotionGroups, order);
		final PromotionOrderResults promotionOrderResults = getPromotionManager().getPromotionResults(order);

		boolean needsCalculate = false;
		boolean resultApplied = false;

		// auto apply promotions that need to be auto applied
		final List<PromotionResult> unappliedOrderPromotions = promotionOrderResults.getFiredOrderPromotions();
		for (final PromotionResult result : unappliedOrderPromotions)
		{
			needsCalculate = true;
			if (!result.isApplied())
			{
				resultApplied |= result.apply();
			}
		}

		final List<PromotionResult> unappliedProductPromotions = promotionOrderResults.getFiredProductPromotions();
		for (final PromotionResult result : unappliedProductPromotions)
		{
			final AbstractPromotion promotion = result.getPromotion();
			needsCalculate = true;

			LOG.info("Order or cart [" + order + "] Promotion [" + promotion.getCode() + "] fired [" + result.getFired()
					+ "] applied [" + result.isApplied() + "]");
			if (!result.isApplied())
			{
				LOG.info("Auto applying promotion [" + promotion.getCode() + "]");
				resultApplied |= result.apply();
			}
		}
		if (resultApplied)
		{
			order.calculateTotals(true);
		}
		return needsCalculate;
	}


}
