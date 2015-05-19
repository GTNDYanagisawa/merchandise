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
package de.hybris.platform.integration.oms.order.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.externaltax.TaxCodeStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.integration.commons.services.OndemandTaxCalculationService;
import de.hybris.platform.integration.oms.order.service.ProductAttributeStrategy;
import de.hybris.platform.integration.oms.order.strategies.OrderEntryNoteStrategy;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.oms.domain.order.OrderLine;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OmsOrderLinePopulatorTest
{
	private OmsOrderLinePopulator omsOrderLinePopulator;
	@Mock
	private TaxCodeStrategy taxCodeStrategy;
	@Mock
	private ProductAttributeStrategy productAttributeStrategy;
	@Mock
	private OndemandTaxCalculationService ondemandTaxCalculationService;
	@Mock
	private OrderEntryNoteStrategy orderEntryNoteStrategy;

	@Before
	public void beforeTest()
	{
		MockitoAnnotations.initMocks(this.getClass());
		omsOrderLinePopulator = new OmsOrderLinePopulator();
		omsOrderLinePopulator.setTaxCodeStrategy(taxCodeStrategy);
		omsOrderLinePopulator.setProductAttributeStrategy(productAttributeStrategy);
		omsOrderLinePopulator.setOndemandTaxCalculationService(ondemandTaxCalculationService);
		omsOrderLinePopulator.setOrderEntryNoteStrategy(orderEntryNoteStrategy);
	}

	@Test
	public void testOmsOrderLinePopulator() throws Exception
	{
		final OrderLine omsOrderLine = new OrderLine();
		final OrderEntryModel orderLineEntryModel = Mockito.mock(OrderEntryModel.class);
		final OrderModel orderModel = Mockito.mock(OrderModel.class);
		final UnitModel unitModel = Mockito.mock(UnitModel.class);
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		final TaxValue taxValue = new TaxValue("TEST", 8.75D, true, 10.76D, "USD");
		final CurrencyModel currencyModel = Mockito.mock(CurrencyModel.class);
		final OndemandDiscountedOrderEntry ondemandDiscountedOrderEntry = Mockito.mock(OndemandDiscountedOrderEntry.class);
		//final ProductTaxCodeModel productTaxCodeModel = Mockito.mock(ProductTaxCodeModel.class);

		BDDMockito.when(taxCodeStrategy.getTaxCodeForCodeAndOrder("orderLine1", orderModel)).thenReturn("P0000001");
		BDDMockito.when(unitModel.getCode()).thenReturn("orderLineUnitCode");
		BDDMockito.when(productModel.getCode()).thenReturn("orderLine1");

		BDDMockito.when(orderLineEntryModel.getQuantity()).thenReturn(Long.valueOf(1));
		BDDMockito.when(orderLineEntryModel.getUnit()).thenReturn(unitModel);
		BDDMockito.when(orderLineEntryModel.getEntryNumber()).thenReturn(Integer.valueOf(123));
		BDDMockito.when(orderLineEntryModel.getProduct()).thenReturn(productModel);
		BDDMockito.when(currencyModel.getIsocode()).thenReturn("USD");
		BDDMockito.when(orderLineEntryModel.getOrder()).thenReturn(orderModel);
		BDDMockito.when(orderLineEntryModel.getOrder().getCurrency()).thenReturn(currencyModel);
		BDDMockito.when(orderLineEntryModel.getTotalPrice()).thenReturn(Double.valueOf(123D));
		BDDMockito.when(orderLineEntryModel.getTaxValues()).thenReturn(Collections.singletonList(taxValue));
		BDDMockito.when(orderLineEntryModel.getOrder().getCode()).thenReturn("order1");
		BDDMockito.when(orderLineEntryModel.getOrder().getStatus()).thenReturn(OrderStatus.CANCELLED);
		BDDMockito.when(ondemandDiscountedOrderEntry.getOrderEntry()).thenReturn(orderLineEntryModel);
		BDDMockito.when(ondemandDiscountedOrderEntry.getDiscountedUnitPrice()).thenReturn(BigDecimal.valueOf(123D));
		BDDMockito.when(
				omsOrderLinePopulator.getOndemandTaxCalculationService().calculatePreciseUnitTax(orderLineEntryModel.getTaxValues(),
						1, false)).thenReturn(BigDecimal.valueOf(10.76D));
		omsOrderLinePopulator.populate(ondemandDiscountedOrderEntry, omsOrderLine);

		Assert.assertEquals(omsOrderLine.getQuantity().getValue(), orderLineEntryModel.getQuantity().intValue());
		Assert.assertEquals(omsOrderLine.getOrderLineId(), orderLineEntryModel.getOrder().getCode() + "_"
				+ orderLineEntryModel.getEntryNumber().toString());
		Assert.assertEquals(omsOrderLine.getSkuId(), orderLineEntryModel.getProduct().getCode());
		Assert.assertEquals(omsOrderLine.getUnitPrice().getValue().doubleValue(),
				orderLineEntryModel.getTotalPrice().doubleValue(), 0);
		Assert.assertEquals(omsOrderLine.getUnitTax().getValue().doubleValue(), 10.76D, 0);
		Assert.assertEquals(omsOrderLine.getTaxCategory(), "P0000001");

	}
}
