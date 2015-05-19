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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class DefaultOndemandTaxCalculationServiceTest
{
	public DefaultOndemandTaxCalculationService ondemandTaxCalculationService;

	public List<TaxValue> taxValuesNet;

	public List<TaxValue> taxValuesGross;

	public AbstractOrderModel abstractOrderNet;

	public AbstractOrderModel abstractOrderGross;

	@Before
	public void setUp()
	{
		ondemandTaxCalculationService = new DefaultOndemandTaxCalculationService();
		stubTaxValues();
		stubOrder();
	}

	public void stubTaxValues()
	{
		taxValuesNet = new ArrayList<TaxValue>();
		final TaxValue taxValue1 = Mockito.mock(TaxValue.class);
		final TaxValue taxValue2 = Mockito.mock(TaxValue.class);

		given(Boolean.valueOf(taxValue1.isAbsolute())).willReturn(Boolean.TRUE);
		given(Double.valueOf(taxValue1.getValue())).willReturn(Double.valueOf(.89));

		given(Boolean.valueOf(taxValue2.isAbsolute())).willReturn(Boolean.TRUE);
		given(Double.valueOf(taxValue2.getValue())).willReturn(Double.valueOf(.90));

		taxValuesNet.add(taxValue1);
		taxValuesNet.add(taxValue2);

		taxValuesGross = new ArrayList<TaxValue>();
		final TaxValue taxValueGross1 = Mockito.mock(TaxValue.class);
		final TaxValue taxValueGross2 = Mockito.mock(TaxValue.class);

		given(Boolean.valueOf(taxValueGross1.isAbsolute())).willReturn(Boolean.FALSE);
		given(Double.valueOf(taxValueGross1.getAppliedValue())).willReturn(Double.valueOf(1.90));

		given(Boolean.valueOf(taxValueGross2.isAbsolute())).willReturn(Boolean.FALSE);
		given(Double.valueOf(taxValueGross2.getAppliedValue())).willReturn(Double.valueOf(3.90));

		taxValuesGross.add(taxValueGross1);
		taxValuesGross.add(taxValueGross2);
	}

	public void stubOrder()
	{
		abstractOrderNet = Mockito.mock(AbstractOrderModel.class);
		final AbstractOrderEntryModel abstractEntryNet = Mockito.mock(AbstractOrderEntryModel.class);

		given(abstractEntryNet.getTaxValues()).willReturn(taxValuesNet);

		given(abstractOrderNet.getEntries()).willReturn(Collections.singletonList(abstractEntryNet));
		given(abstractOrderNet.getTotalTaxValues()).willReturn(taxValuesNet);
		given(abstractOrderNet.getNet()).willReturn(Boolean.TRUE);
		given(abstractOrderNet.getDeliveryCost()).willReturn(Double.valueOf("9.99"));

		abstractOrderGross = Mockito.mock(AbstractOrderModel.class);
		final AbstractOrderEntryModel abstractEntryGross = Mockito.mock(AbstractOrderEntryModel.class);

		given(abstractEntryGross.getTaxValues()).willReturn(taxValuesGross);

		given(abstractOrderGross.getEntries()).willReturn(Collections.singletonList(abstractEntryGross));
		given(abstractOrderGross.getEntries().get(0).getQuantity()).willReturn(Long.valueOf(1));
		given(abstractOrderGross.getTotalTaxValues()).willReturn(taxValuesGross);
		given(abstractOrderGross.getNet()).willReturn(Boolean.FALSE);
		given(abstractOrderGross.getDeliveryCost()).willReturn(Double.valueOf("9.99"));
	}

	@Test
	public void shouldCalculatePreciseUnitTaxSingle()
	{
		final BigDecimal unitTax = ondemandTaxCalculationService.calculatePreciseUnitTax(taxValuesNet, 1, true);

		Assert.assertNotNull(unitTax);
		Assert.assertTrue(unitTax.compareTo(new BigDecimal("1.79")) == 0);
	}

	@Test
	public void shouldCalculatePreciseUnitTaxMultipleLoseCent()
	{
		final BigDecimal unitTax = ondemandTaxCalculationService.calculatePreciseUnitTax(taxValuesNet, 2, true);

		Assert.assertNotNull(unitTax);
		Assert.assertTrue(unitTax.compareTo(new BigDecimal(".89")) == 0);
	}

	@Test
	public void shouldCalculatePreciseTotalTax()
	{
		given(abstractOrderNet.getEntries().get(0).getQuantity()).willReturn(Long.valueOf(1));
		final BigDecimal totalTax = ondemandTaxCalculationService.calculateTotalTax(abstractOrderNet);

		Assert.assertNotNull(totalTax);
		Assert.assertTrue(totalTax.compareTo(new BigDecimal("3.58")) == 0);
	}

	@Test
	public void shouldCalculatePreciseTotalTaxLoseCents()
	{
		given(abstractOrderNet.getEntries().get(0).getQuantity()).willReturn(Long.valueOf(10));
		final BigDecimal totalTax = ondemandTaxCalculationService.calculateTotalTax(abstractOrderNet);

		Assert.assertNotNull(totalTax);
		Assert.assertTrue(totalTax.compareTo(new BigDecimal("3.49")) == 0);
	}

	@Test
	public void shouldCalculateShippingTaxNet()
	{
		final BigDecimal shippingTax = ondemandTaxCalculationService.calculateShippingTax(abstractOrderNet);

		Assert.assertNotNull(shippingTax);
		Assert.assertTrue(shippingTax.compareTo(new BigDecimal("1.79")) == 0);
	}

	@Test
	public void shouldCalculateShippingCostNet()
	{
		final Double shippingCost = ondemandTaxCalculationService.calculateShippingCost(abstractOrderNet);

		Assert.assertNotNull(abstractOrderNet);
		Assert.assertTrue(shippingCost.compareTo(new Double("9.99")) == 0);
	}

	@Test
	public void shouldCalculatePreciseUnitTaxGross()
	{
		final BigDecimal unitTax = ondemandTaxCalculationService.calculatePreciseUnitTax(taxValuesGross, 1, false);

		Assert.assertNotNull(unitTax);
		Assert.assertTrue(unitTax.compareTo(new BigDecimal("5.80")) == 0);
	}

	@Test
	public void shouldCalculatePreciseTotalTaxGross()
	{
		given(abstractOrderGross.getTotalTax()).willReturn(Double.valueOf("8.35"));
		final BigDecimal totalTax = ondemandTaxCalculationService.calculateTotalTax(abstractOrderGross);

		Assert.assertNotNull(totalTax);
		Assert.assertTrue(totalTax.compareTo(new BigDecimal("8.35")) == 0);
	}

	@Test
	public void shouldCalculateShippingTaxGross()
	{
		given(abstractOrderGross.getTotalTax()).willReturn(Double.valueOf("8.35"));
		final BigDecimal shippingTax = ondemandTaxCalculationService.calculateShippingTax(abstractOrderGross);

		Assert.assertNotNull(shippingTax);
		Assert.assertTrue(shippingTax.compareTo(new BigDecimal("2.55")) == 0);
	}

	@Test
	public void shouldCalculateShippingCostGross()
	{
		given(abstractOrderGross.getTotalTax()).willReturn(Double.valueOf("7.20"));

		final Double shippingCost = ondemandTaxCalculationService.calculateShippingCost(abstractOrderGross);

		Assert.assertNotNull(shippingCost);
		Assert.assertTrue(shippingCost.equals(Double.valueOf("8.59")));
	}

	@Test
	public void shouldLoseCentOnTaxesNotOverall()
	{
		taxValuesGross = new ArrayList<TaxValue>();
		final TaxValue taxValueGross1 = Mockito.mock(TaxValue.class);

		given(Boolean.valueOf(taxValueGross1.isAbsolute())).willReturn(Boolean.FALSE);
		given(Double.valueOf(taxValueGross1.getAppliedValue())).willReturn(Double.valueOf(20.23));

		taxValuesGross.add(taxValueGross1);
		given(abstractOrderGross.getEntries().get(0).getTaxValues()).willReturn(taxValuesGross);
		given(abstractOrderGross.getEntries().get(0).getQuantity()).willReturn(Long.valueOf(3));
		given(abstractOrderGross.getEntries().get(0).getTotalPrice()).willReturn(Double.valueOf(121.38));
		given(abstractOrderGross.getTotalTax()).willReturn(Double.valueOf(22.23));
		given(abstractOrderGross.getDeliveryCost()).willReturn(Double.valueOf(11.99));

		final Double shippingCost = ondemandTaxCalculationService.calculateShippingCost(abstractOrderGross);
		final BigDecimal shippingTax = ondemandTaxCalculationService.calculateShippingTax(abstractOrderGross);
		final BigDecimal unitTax = ondemandTaxCalculationService.calculatePreciseUnitTax(taxValuesGross, 3, false);

		Assert.assertNotNull(shippingTax);
		Assert.assertNotNull(shippingCost);
		Assert.assertNotNull(unitTax);

		Assert.assertTrue(shippingTax.compareTo(BigDecimal.valueOf(2.0)) == 0);
		Assert.assertTrue(shippingCost.equals(Double.valueOf(9.99)));
		Assert.assertTrue(unitTax.compareTo(BigDecimal.valueOf(6.74)) == 0);

		final BigDecimal lineEntryTotalTax = unitTax.multiply(BigDecimal.valueOf(3));
		Assert.assertTrue(shippingTax.add(lineEntryTotalTax).compareTo(BigDecimal.valueOf(22.23)) != 0);

	}
}
