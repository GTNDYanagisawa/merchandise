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

package de.hybris.platform.subscriptionservices.subscription.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.price.impl.DefaultSubscriptionCommercePriceService;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * JUnit test suite for {@link DefaultSubscriptionCommercePriceService}
 */
public class DefaultSubscriptionCommercePriceServiceTest
{

	private SubscriptionPricePlanModel pricePlan;

	private OneTimeChargeEntryModel oneTimeChargeEntry;

	private BillingEventModel oneTimeChargeBillingEvent;

	private BillingEventModel payNowBillingEvent;

	private DefaultSubscriptionCommercePriceService priceService;

	private static final String BILLING_EVENT_CODE = "onetimeplan";

	private static final String PAY_NOW_EVENT = "paynow";

	@Before
	public void setUp() throws Exception
	{
		pricePlan = mock(SubscriptionPricePlanModel.class);
		oneTimeChargeEntry = mock(OneTimeChargeEntryModel.class);
		oneTimeChargeBillingEvent = mock(BillingEventModel.class);
		payNowBillingEvent = mock(BillingEventModel.class);
		priceService = new DefaultSubscriptionCommercePriceService();
	}


	/**
	 * Test method to verify
	 * {@link SubscriptionCommercePriceService#getOneTimeChargeEntryPlan(SubscriptionPricePlanModel, BillingEventModel)}
	 * which returns the OneTimeChargeEntryModel for the given BillingEventModel is same as in
	 * {@link OneTimeChargeEntryModel#getBillingEvent()}
	 */
	@Test
	public void testGetOneTimeChargeEntryPlan()
	{

		final Collection<OneTimeChargeEntryModel> oneTimeChargeEntryList = new ArrayList<OneTimeChargeEntryModel>();
		oneTimeChargeEntryList.add(oneTimeChargeEntry);

		given(pricePlan.getOneTimeChargeEntries()).willReturn(oneTimeChargeEntryList);
		given(oneTimeChargeEntry.getBillingEvent()).willReturn(oneTimeChargeBillingEvent);
		given(oneTimeChargeBillingEvent.getCode()).willReturn(BILLING_EVENT_CODE);


		oneTimeChargeEntry = priceService.getOneTimeChargeEntryPlan(pricePlan, oneTimeChargeBillingEvent);

		Assert.assertNotNull(oneTimeChargeEntry);

	}

	/**
	 * Test method to verify
	 * {@link SubscriptionCommercePriceService#getOneTimeChargeEntryPlan(SubscriptionPricePlanModel, BillingEventModel)}
	 * which returns null if the given BillingEventModel is not same as in
	 * {@link OneTimeChargeEntryModel#getBillingEvent()}
	 */
	@Test
	public void testGetOneTimeChargeEntryPlanForGivenPayNowEvent()
	{

		final Collection<OneTimeChargeEntryModel> oneTimeChargeEntryList = new ArrayList<OneTimeChargeEntryModel>();
		oneTimeChargeEntryList.add(oneTimeChargeEntry);

		payNowBillingEvent.setCode(PAY_NOW_EVENT);
		oneTimeChargeEntry.setBillingEvent(payNowBillingEvent);

		given(oneTimeChargeEntry.getBillingEvent()).willReturn(payNowBillingEvent);
		given(oneTimeChargeBillingEvent.getCode()).willReturn(BILLING_EVENT_CODE);

		oneTimeChargeEntry = priceService.getOneTimeChargeEntryPlan(pricePlan, oneTimeChargeBillingEvent);

		Assert.assertNull(oneTimeChargeEntry);

	}
}
