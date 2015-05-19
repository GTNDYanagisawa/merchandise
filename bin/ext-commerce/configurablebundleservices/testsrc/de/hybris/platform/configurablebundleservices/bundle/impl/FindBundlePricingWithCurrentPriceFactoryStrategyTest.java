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
package de.hybris.platform.configurablebundleservices.bundle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionProductModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;


/**
 * JUnit test suite for {@link FindBundlePricingWithCurrentPriceFactoryStrategy}
 */
@UnitTest
public class FindBundlePricingWithCurrentPriceFactoryStrategyTest
{
	private static final Double FIRST_RECURRING_PRICE = Double.valueOf(19.99);
	private static final Double LAST_RECURRING_PRICE = Double.valueOf(39.99);
	private static final Double RULE_LOW_PRICE = Double.valueOf(14.99);
	private static final Double RULE_HIGH_PRICE = Double.valueOf(24.99);

	private static final Double ONE_TIME_CHARGE_PLAN_PRICE = Double.valueOf(19.99);
	private static final Double BASE_PRICE = Double.valueOf(49.99);

	private FindBundlePricingWithCurrentPriceFactoryStrategy bundlePriceFactory;
	private CurrencyModel currency;
	private ChangeProductPriceBundleRuleModel priceRule;
	private RecurringChargeEntryModel recurringChargeEntry1;
	private RecurringChargeEntryModel recurringChargeEntry2;
	private OneTimeChargeEntryModel oneTimeChargeEntry;
	private SubscriptionProductModel subscriptionProduct;
	private BillingFrequencyModel billingFrequency;
	private BillingEventModel oneTimeChargeBillingEvent;
	private BillingEventModel payNowBillingEvent;
	private CartModel childCart;
	private CartEntryModel childEntry;
	private CartEntryModel masterEntry;
	private SubscriptionPricePlanModel pricePlan;

	@Mock
	private BundleRuleService bundleRuleService;

	@Mock
	private SubscriptionCommercePriceService commercePriceService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		bundlePriceFactory = new FindBundlePricingWithCurrentPriceFactoryStrategy();
		bundlePriceFactory.setBundleRuleService(bundleRuleService);
		bundlePriceFactory.setCommercePriceService(commercePriceService);
		bundlePriceFactory.setSubscriptionCommercePriceService(commercePriceService);
		currency = mock(CurrencyModel.class);
		priceRule = mock(ChangeProductPriceBundleRuleModel.class);
		recurringChargeEntry1 = mock(RecurringChargeEntryModel.class);
		recurringChargeEntry2 = mock(RecurringChargeEntryModel.class);
		billingFrequency = mock(BillingFrequencyModel.class);
		oneTimeChargeBillingEvent = mock(BillingEventModel.class);
		payNowBillingEvent = mock(BillingEventModel.class);
		oneTimeChargeEntry = mock(OneTimeChargeEntryModel.class);
		childCart = mock(CartModel.class);
		childEntry = mock(CartEntryModel.class);
		masterEntry = mock(CartEntryModel.class);
		subscriptionProduct = mock(SubscriptionProductModel.class);
		pricePlan = mock(SubscriptionPricePlanModel.class);
		createBasicBasicSubscriptionProduct();
		createBasicBundleCartEntry();
		createBasicPricePlan();
	}

	@Test
	public void testFindDiscountValuesNoPrice() throws CalculationException
	{
		childEntry.setBasePrice(Double.valueOf(0.00));
		final List<DiscountValue> discounts = bundlePriceFactory.findDiscountValues(childEntry);
		assertTrue("", CollectionUtils.isEmpty(discounts));
	}

	@Test
	public void testFindDiscountValuesNoPricePlan() throws CalculationException
	{
		given(commercePriceService.getSubscriptionPricePlanForEntry(childEntry)).willReturn(null);
		final List<DiscountValue> discounts = bundlePriceFactory.findDiscountValues(childEntry);
		assertTrue("", CollectionUtils.isEmpty(discounts));
	}

	@Test
	public void testFindDiscountValuesPriceWrongCartBillingFrequency() throws CalculationException
	{
		final BillingFrequencyModel billingFrequencyOrder = mock(BillingFrequencyModel.class);
		childEntry.getOrder().setBillingTime(new BillingFrequencyModel());
		given(childCart.getBillingTime()).willReturn(billingFrequencyOrder);
		given(commercePriceService.getSubscriptionPricePlanForEntry(childEntry)).willReturn(pricePlan);
		final List<DiscountValue> discounts = bundlePriceFactory.findDiscountValues(childEntry);
		assertTrue("", CollectionUtils.isEmpty(discounts));
	}

	@Test
	public void testFindDiscountValuesPlanWinsNoRule() throws CalculationException
	{
		given(commercePriceService.getSubscriptionPricePlanForEntry(childEntry)).willReturn(pricePlan);
		given(commercePriceService.getFirstRecurringPriceFromPlan(pricePlan)).willReturn(recurringChargeEntry1);
		final List<DiscountValue> discounts = bundlePriceFactory.findDiscountValues(childEntry);
		assertEquals("", 1, discounts.size());
		final DiscountValue discount = discounts.iterator().next();
		assertEquals("", "recurringChargeEntry1", discount.getCode());
		assertEquals(LAST_RECURRING_PRICE.doubleValue() - FIRST_RECURRING_PRICE.doubleValue(), discount.getValue(), 0.005);
	}

	@Test
	public void testFindDiscountValuesRuleWinsAgainstPlan() throws CalculationException
	{
		given(bundleRuleService.getChangePriceBundleRuleForOrderEntry(masterEntry)).willReturn(priceRule);
		given(priceRule.getPrice()).willReturn(BigDecimal.valueOf(RULE_LOW_PRICE.doubleValue()));
		given(priceRule.getId()).willReturn("priceRule");
		given(commercePriceService.getSubscriptionPricePlanForEntry(childEntry)).willReturn(pricePlan);
		given(commercePriceService.getFirstRecurringPriceFromPlan(pricePlan)).willReturn(recurringChargeEntry1);
		final List<DiscountValue> discounts = bundlePriceFactory.findDiscountValues(childEntry);
		assertEquals("", 1, discounts.size());
		final DiscountValue discount = discounts.iterator().next();
		assertEquals("", "priceRule", discount.getCode());
		assertEquals(LAST_RECURRING_PRICE.doubleValue() - RULE_LOW_PRICE.doubleValue(), discount.getValue(), 0.005);
	}

	@Test
	public void testFindDiscountValuesPlanWinsAgainstRule() throws CalculationException
	{
		given(bundleRuleService.getChangePriceBundleRuleForOrderEntry(masterEntry)).willReturn(priceRule);
		given(priceRule.getPrice()).willReturn(BigDecimal.valueOf(RULE_HIGH_PRICE.doubleValue()));
		given(priceRule.getId()).willReturn("priceRule");
		given(commercePriceService.getSubscriptionPricePlanForEntry(childEntry)).willReturn(pricePlan);
		given(commercePriceService.getFirstRecurringPriceFromPlan(pricePlan)).willReturn(recurringChargeEntry1);
		final List<DiscountValue> discounts = bundlePriceFactory.findDiscountValues(childEntry);
		assertEquals("", 1, discounts.size());
		final DiscountValue discount = discounts.iterator().next();
		assertEquals("", "recurringChargeEntry1", discount.getCode());
		assertEquals(LAST_RECURRING_PRICE.doubleValue() - FIRST_RECURRING_PRICE.doubleValue(), discount.getValue(), 0.005);
	}

	@Test
	public void testOneTimeChargePriceRuleWinsAgainstPlan()
	{
		given(childCart.getBillingTime()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getBillingEvent()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getPrice()).willReturn(BigDecimal.valueOf(RULE_LOW_PRICE.doubleValue()));
		given(commercePriceService.getOneTimeChargeEntryPlan(pricePlan, oneTimeChargeBillingEvent)).willReturn(oneTimeChargeEntry);
		given(oneTimeChargeEntry.getPrice()).willReturn(ONE_TIME_CHARGE_PLAN_PRICE);
		given(childEntry.getBasePrice()).willReturn(BASE_PRICE);
		given(oneTimeChargeEntry.getId()).willReturn("oneTimeChargeEntry");
		given(priceRule.getId()).willReturn("priceRule");

		final List<DiscountValue> discountValues = Lists.newArrayList();
		bundlePriceFactory.reduceOneTimePrice(pricePlan, priceRule, discountValues, currency, childEntry);

		assertEquals("", 1, discountValues.size());
		final DiscountValue discount = discountValues.iterator().next();
		assertEquals("", "priceRule", discount.getCode());
		assertEquals(BASE_PRICE.doubleValue() - RULE_LOW_PRICE.doubleValue(), discount.getValue(), 0.005);
	}

	@Test
	public void testOneTimeChargePricePlanWinsAgainstRule()
	{
		given(childCart.getBillingTime()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getBillingEvent()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getPrice()).willReturn(BigDecimal.valueOf(RULE_HIGH_PRICE.doubleValue()));
		given(commercePriceService.getOneTimeChargeEntryPlan(pricePlan, oneTimeChargeBillingEvent)).willReturn(oneTimeChargeEntry);
		given(oneTimeChargeEntry.getPrice()).willReturn(ONE_TIME_CHARGE_PLAN_PRICE);
		given(childEntry.getBasePrice()).willReturn(BASE_PRICE);
		given(oneTimeChargeEntry.getId()).willReturn("oneTimeChargeEntry");
		given(priceRule.getId()).willReturn("priceRule");

		final List<DiscountValue> discountValues = Lists.newArrayList();
		bundlePriceFactory.reduceOneTimePrice(pricePlan, priceRule, discountValues, currency, childEntry);

		assertEquals("", 1, discountValues.size());
		final DiscountValue discount = discountValues.iterator().next();
		assertEquals("", "oneTimeChargeEntry", discount.getCode());
		assertEquals(BASE_PRICE.doubleValue() - ONE_TIME_CHARGE_PLAN_PRICE.doubleValue(), discount.getValue(), 0.005);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOneTimeChargePriceNoRule()
	{
		given(childCart.getBillingTime()).willReturn(oneTimeChargeBillingEvent);
		given(commercePriceService.getOneTimeChargeEntryPlan(pricePlan, oneTimeChargeBillingEvent)).willReturn(oneTimeChargeEntry);
		given(oneTimeChargeEntry.getPrice()).willReturn(ONE_TIME_CHARGE_PLAN_PRICE);
		given(childEntry.getBasePrice()).willReturn(BASE_PRICE);
		given(oneTimeChargeEntry.getId()).willReturn("oneTimeChargeEntry");

		final List<DiscountValue> discountValues = Lists.newArrayList();
		bundlePriceFactory.reduceOneTimePrice(pricePlan, null, discountValues, currency, childEntry);

		assertTrue(discountValues.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOneTimeChargePriceNoPlan()
	{
		given(childCart.getBillingTime()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getBillingEvent()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getPrice()).willReturn(BigDecimal.valueOf(RULE_HIGH_PRICE.doubleValue()));
		given(oneTimeChargeEntry.getPrice()).willReturn(ONE_TIME_CHARGE_PLAN_PRICE);
		given(childEntry.getBasePrice()).willReturn(BASE_PRICE);
		given(oneTimeChargeEntry.getId()).willReturn("oneTimeChargeEntry");
		given(priceRule.getId()).willReturn("priceRule");

		final List<DiscountValue> discountValues = Lists.newArrayList();
		bundlePriceFactory.reduceOneTimePrice(null, priceRule, discountValues, currency, childEntry);

		assertTrue(discountValues.isEmpty());
	}

	@Test
	public void testOneTimeChargePriceNoOneTimeChargeEntry()
	{
		given(childCart.getBillingTime()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getBillingEvent()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getPrice()).willReturn(BigDecimal.valueOf(RULE_HIGH_PRICE.doubleValue()));
		given(commercePriceService.getOneTimeChargeEntryPlan(pricePlan, oneTimeChargeBillingEvent)).willReturn(null);
		given(oneTimeChargeEntry.getPrice()).willReturn(ONE_TIME_CHARGE_PLAN_PRICE);
		given(childEntry.getBasePrice()).willReturn(BASE_PRICE);
		given(oneTimeChargeEntry.getId()).willReturn("oneTimeChargeEntry");
		given(priceRule.getId()).willReturn("priceRule");

		final List<DiscountValue> discountValues = Lists.newArrayList();
		bundlePriceFactory.reduceOneTimePrice(pricePlan, priceRule, discountValues, currency, childEntry);

		assertTrue(discountValues.isEmpty());
	}

	@Test
	public void testOneTimeChargePriceDifferentBillingEvent()
	{
		given(childCart.getBillingTime()).willReturn(oneTimeChargeBillingEvent);
		given(priceRule.getBillingEvent()).willReturn(payNowBillingEvent);

		final List<DiscountValue> discountValues = Lists.newArrayList();
		bundlePriceFactory.reduceOneTimePrice(pricePlan, priceRule, discountValues, currency, childEntry);

		assertTrue(discountValues.isEmpty());
	}

	protected void createBasicBundleCartEntry()
	{
		given(masterEntry.getBundleNo()).willReturn(Integer.valueOf(1));
		given(childEntry.getMasterEntry()).willReturn(masterEntry);
		given(childEntry.getBasePrice()).willReturn(LAST_RECURRING_PRICE);
		given(childEntry.getProduct()).willReturn(subscriptionProduct);
		given(childCart.getCurrency()).willReturn(currency);
		given(childCart.getBillingTime()).willReturn(billingFrequency);
		given(childEntry.getOrder()).willReturn(childCart);
	}

	protected void createBasicPricePlan()
	{
		final List<RecurringChargeEntryModel> recurringChargeEntries = new ArrayList<RecurringChargeEntryModel>();
		recurringChargeEntries.add(recurringChargeEntry1);
		given(recurringChargeEntry1.getPrice()).willReturn(FIRST_RECURRING_PRICE);
		given(recurringChargeEntry1.getId()).willReturn("recurringChargeEntry1");
		recurringChargeEntries.add(recurringChargeEntry2);
		given(recurringChargeEntry2.getPrice()).willReturn(LAST_RECURRING_PRICE);
		given(recurringChargeEntry2.getId()).willReturn("recurringChargeEntry2");
		given(pricePlan.getRecurringChargeEntries()).willReturn(recurringChargeEntries);
	}

	protected void createBasicBasicSubscriptionProduct()
	{
		final SubscriptionTermModel subscriptionTerm = mock(SubscriptionTermModel.class);
		final BillingPlanModel billingPlan = mock(BillingPlanModel.class);
		given(billingPlan.getBillingFrequency()).willReturn(billingFrequency);
		given(subscriptionTerm.getBillingPlan()).willReturn(billingPlan);
		given(subscriptionProduct.getSubscriptionTerm()).willReturn(subscriptionTerm);
	}

}
