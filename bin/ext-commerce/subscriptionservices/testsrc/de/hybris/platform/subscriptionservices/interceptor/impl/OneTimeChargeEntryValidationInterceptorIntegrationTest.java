/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.subscriptionservices.interceptor.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;
import de.hybris.platform.util.Config;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class OneTimeChargeEntryValidationInterceptorIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(OneTimeChargeEntryValidationInterceptorIntegrationTest.class);
	private static final String PRODUCT_CODE = "Y_STARTER_100_1Y";
	private static final String BILLING_EVENT_CODE = "onfirstbill";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Resource
	private ModelService modelService;

	@Resource
	private ProductService productService;

	@Resource
	private BillingTimeService billingTimeService;

	private BillingEventModel onFirstBill;
	private SubscriptionPricePlanModel pricePlan;


	@Before
	public void setUp() throws ImpExException
	{
		final long startTime = System.currentTimeMillis();
		LOG.info("Creating data for OneTimeChargeEntryValidationInterceptorIntegrationTest ..");
		final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
		LOG.info("Existing value for " + ImpExConstants.Params.LEGACY_MODE_KEY + " :" + legacyModeBackup);
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
		importCsv("/commerceservices/test/testCommerceCart.csv", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "false");
		importCsv("/subscriptionservices/test/testSubscriptionCommerceCartService.impex", "utf-8");
		Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);

		LOG.info("Finished data for OneTimeChargeEntryValidationInterceptorIntegrationTest "
				+ (System.currentTimeMillis() - startTime) + "ms");

		final SubscriptionProductModel product = (SubscriptionProductModel) productService.getProductForCode(PRODUCT_CODE);
		onFirstBill = (BillingEventModel) billingTimeService.getBillingTimeForCode(BILLING_EVENT_CODE);
		pricePlan = (SubscriptionPricePlanModel) product.getEurope1Prices().iterator().next();
	}

	@Test
	public void testCreateOneTimeChargeEntry()
	{
		final OneTimeChargeEntryModel oneTimeChargeEntry = createOneTimeChargeEntry(pricePlan, onFirstBill, 10.0d);
		modelService.save(oneTimeChargeEntry);
	}

	@Test
	public void testCreateMultipleOneTimeChargeEntriesWithSameBillingEvent()
	{
		thrown.expect(ModelSavingException.class);
		thrown.expectMessage("please modify the existing one-time charge instead of assigning a second one");

		final OneTimeChargeEntryModel entryOne = createOneTimeChargeEntry(pricePlan, onFirstBill, 10.0d);
		modelService.save(entryOne);

		modelService.refresh(pricePlan); // manual refresh necessary in junit tenant

		final OneTimeChargeEntryModel entryTwo = createOneTimeChargeEntry(pricePlan, onFirstBill, 5.0d);
		modelService.save(entryTwo);
	}

	/**
	 * Utility method to create a {@link OneTimeChargeEntryModel} with the given parameters
	 * 
	 * @param plan
	 *           the subscription price plan to add the one time charge entry to
	 * @param event
	 *           the billing event to set
	 * @param price
	 *           the price to set
	 * @return the newly created {@link OneTimeChargeEntryModel} instance
	 */
	private OneTimeChargeEntryModel createOneTimeChargeEntry(final SubscriptionPricePlanModel plan, final BillingEventModel event,
			final double price)
	{
		final OneTimeChargeEntryModel oneTimeChargeEntry = modelService.create(OneTimeChargeEntryModel.class);
		oneTimeChargeEntry.setBillingEvent(event);
		oneTimeChargeEntry.setPrice(Double.valueOf(price));
		oneTimeChargeEntry.setSubscriptionPricePlanOneTime(plan);
		return oneTimeChargeEntry;
	}
}
