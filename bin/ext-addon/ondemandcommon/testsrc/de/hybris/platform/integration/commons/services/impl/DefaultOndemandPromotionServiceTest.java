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
 */
package de.hybris.platform.integration.commons.services.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.integration.commons.services.OndemandTaxCalculationService;
import de.hybris.platform.integration.commons.strategies.OndemandDiscountableEntryStrategy;
import de.hybris.platform.integration.commons.strategies.impl.DefaultOndemandDiscountableEntryStrategy;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultOndemandPromotionServiceTest
{
	@Mock
	private OndemandDiscountableEntryStrategy discountableEntryStrategy;

	@Mock
	private OndemandTaxCalculationService taxCalculationService;

	private DefaultOndemandPromotionService defaultOndemandPromotionService;

	public AbstractOrderModel abstractOrder;

	public AbstractOrderEntryModel abstractEntry;

	public List<TaxValue> taxValuesGross;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		discountableEntryStrategy = mock(DefaultOndemandDiscountableEntryStrategy.class);
		defaultOndemandPromotionService = new DefaultOndemandPromotionService();
		defaultOndemandPromotionService.setDiscountableEntryStrategy(discountableEntryStrategy);
		defaultOndemandPromotionService.setTaxCalculationService(taxCalculationService);
		stubTaxValues();
		stubOrder();
	}

	private void stubOrder()
	{
		abstractEntry = Mockito.mock(AbstractOrderEntryModel.class);
		given(abstractEntry.getTaxValues()).willReturn(taxValuesGross);

		abstractOrder = Mockito.mock(AbstractOrderModel.class);
		given(abstractEntry.getTotalPrice()).willReturn(Double.valueOf(14.00));
		given(abstractEntry.getQuantity()).willReturn(Long.valueOf(2));
		given(abstractOrder.getEntries()).willReturn(Collections.singletonList(abstractEntry));
		given(abstractOrder.getNet()).willReturn(Boolean.TRUE);
		given(abstractEntry.getOrder()).willReturn(abstractOrder);
	}

	private void stubTaxValues()
	{
		taxValuesGross = new ArrayList<TaxValue>();
		final TaxValue taxValueGross1 = Mockito.mock(TaxValue.class);
		final TaxValue taxValueGross2 = Mockito.mock(TaxValue.class);

		given(Boolean.valueOf(taxValueGross1.isAbsolute())).willReturn(Boolean.FALSE);
		given(Double.valueOf(taxValueGross1.getAppliedValue())).willReturn(Double.valueOf(0.95));

		given(Boolean.valueOf(taxValueGross2.isAbsolute())).willReturn(Boolean.FALSE);
		given(Double.valueOf(taxValueGross2.getAppliedValue())).willReturn(Double.valueOf(1.45));

		taxValuesGross.add(taxValueGross1);
		taxValuesGross.add(taxValueGross2);
	}

	@Test
	public void shouldCalculateWithoutPromotions()
	{
		final List<OndemandDiscountedOrderEntry> results = defaultOndemandPromotionService
				.calculateProportionalDiscountForEntries(abstractOrder);

		Assert.assertNotNull(results);
		Assert.assertNotNull(results.get(0));
		Assert.assertTrue(BigDecimal.valueOf(14).compareTo(results.get(0).getDiscountedLinePrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(7).compareTo(results.get(0).getDiscountedUnitPrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(0.0).compareTo(results.get(0).getTotalDiscount()) == 0);

	}

	@Test
	public void shouldCalculateWithAnOrderPromotion()
	{
		final DiscountValue discountValue = Mockito.mock(DiscountValue.class);
		given(Double.valueOf(discountValue.getAppliedValue())).willReturn(Double.valueOf(5));

		given(abstractOrder.getGlobalDiscountValues()).willReturn(Collections.singletonList(discountValue));
		given(
				defaultOndemandPromotionService.getDiscountableEntryStrategy().getDiscountableEntries(
						Collections.singletonList(abstractEntry))).willReturn(Collections.singletonList(abstractEntry));
		given(defaultOndemandPromotionService.getDiscountableEntryStrategy().getDiscountableEntries(abstractOrder.getEntries()))
				.willReturn(Collections.singletonList(abstractEntry));
		given(abstractEntry.getOrder()).willReturn(abstractOrder);

		final List<OndemandDiscountedOrderEntry> results = defaultOndemandPromotionService
				.calculateProportionalDiscountForEntries(abstractOrder);

		Assert.assertNotNull(results);
		Assert.assertNotNull(results.get(0));
		Assert.assertTrue(BigDecimal.valueOf(9.0).compareTo(results.get(0).getDiscountedLinePrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(4.5).compareTo(results.get(0).getDiscountedUnitPrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(5).compareTo(results.get(0).getTotalDiscount()) == 0);
	}

	@Test
	public void shouldLoseCentOnPromotions()
	{
		final AbstractOrderEntryModel entryModel = Mockito.mock(AbstractOrderEntryModel.class);
		final AbstractOrderModel orderModel = Mockito.mock(AbstractOrderModel.class);
		final DiscountValue discountValue = Mockito.mock(DiscountValue.class);
		given(Double.valueOf(discountValue.getAppliedValue())).willReturn(Double.valueOf(12.5));

		given(entryModel.getTotalPrice()).willReturn(Double.valueOf(22.50));
		given(entryModel.getQuantity()).willReturn(Long.valueOf(3));
		given(orderModel.getEntries()).willReturn(Collections.singletonList(entryModel));
		given(orderModel.getGlobalDiscountValues()).willReturn(Collections.singletonList(discountValue));
		given(
				defaultOndemandPromotionService.getDiscountableEntryStrategy().getDiscountableEntries(
						Collections.singletonList(entryModel))).willReturn(Collections.singletonList(entryModel));
		given(defaultOndemandPromotionService.getDiscountableEntryStrategy().getDiscountableEntries(orderModel.getEntries()))
				.willReturn(Collections.singletonList(entryModel));
		given(orderModel.getNet()).willReturn(Boolean.TRUE);
		given(entryModel.getOrder()).willReturn(orderModel);

		final List<OndemandDiscountedOrderEntry> results = defaultOndemandPromotionService
				.calculateProportionalDiscountForEntries(orderModel);


		Assert.assertNotNull(results);
		Assert.assertNotNull(results.get(0));
		Assert.assertTrue(BigDecimal.valueOf(9.99).compareTo(results.get(0).getDiscountedLinePrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(3.33).compareTo(results.get(0).getDiscountedUnitPrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(12.5).compareTo(results.get(0).getTotalDiscount()) == 0);
	}

	@Test
	public void shouldCalculateWithoutPromotionGross()
	{
		given(abstractOrder.getNet()).willReturn(Boolean.FALSE);
		given(
				defaultOndemandPromotionService.getTaxCalculationService().calculatePreciseUnitTax(abstractEntry.getTaxValues(),
						abstractEntry.getQuantity().doubleValue(), abstractOrder.getNet().booleanValue())).willReturn(
				BigDecimal.valueOf(1.2));
		final List<OndemandDiscountedOrderEntry> results = defaultOndemandPromotionService
				.calculateProportionalDiscountForEntries(abstractOrder);

		Assert.assertNotNull(results);
		Assert.assertNotNull(results.get(0));
		Assert.assertTrue(BigDecimal.valueOf(11.6).compareTo(results.get(0).getDiscountedLinePrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(5.8).compareTo(results.get(0).getDiscountedUnitPrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(0.0).compareTo(results.get(0).getTotalDiscount()) == 0);
	}

	@Test
	public void shouldCalculateWithAnOrderPromotionGross()
	{
		final DiscountValue discountValue = Mockito.mock(DiscountValue.class);
		given(Double.valueOf(discountValue.getAppliedValue())).willReturn(Double.valueOf(5));

		given(abstractOrder.getNet()).willReturn(Boolean.FALSE);
		given(abstractOrder.getGlobalDiscountValues()).willReturn(Collections.singletonList(discountValue));
		given(
				defaultOndemandPromotionService.getDiscountableEntryStrategy().getDiscountableEntries(
						Collections.singletonList(abstractEntry))).willReturn(Collections.singletonList(abstractEntry));
		given(defaultOndemandPromotionService.getDiscountableEntryStrategy().getDiscountableEntries(abstractOrder.getEntries()))
				.willReturn(Collections.singletonList(abstractEntry));
		given(abstractEntry.getOrder()).willReturn(abstractOrder);
		given(
				defaultOndemandPromotionService.getTaxCalculationService().calculatePreciseUnitTax(abstractEntry.getTaxValues(),
						abstractEntry.getQuantity().doubleValue(), abstractOrder.getNet().booleanValue())).willReturn(
				BigDecimal.valueOf(1.2));

		final List<OndemandDiscountedOrderEntry> results = defaultOndemandPromotionService
				.calculateProportionalDiscountForEntries(abstractOrder);

		Assert.assertNotNull(results);
		Assert.assertNotNull(results.get(0));
		Assert.assertTrue(BigDecimal.valueOf(6.6).compareTo(results.get(0).getDiscountedLinePrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(3.3).compareTo(results.get(0).getDiscountedUnitPrice()) == 0);
		Assert.assertTrue(BigDecimal.valueOf(5).compareTo(results.get(0).getTotalDiscount()) == 0);
	}

}
