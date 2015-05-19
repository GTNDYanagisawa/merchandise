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
package de.hybris.platform.integration.cis.tax.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.cis.tax.strategies.CisShippingAddressStrategy;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.integration.commons.services.OndemandPromotionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisLineItem;
import com.hybris.cis.api.model.CisOrder;


/**
 * 
 *
 */
@UnitTest
@Ignore
public class CisOrderPopulatorTest
{
	private static final String ORDER_CODE = "test-order";
	private static final String CURRENCY_CODE = "USD";

	private CisOrderPopulator cisOrderPopulator;
	@Mock
	private CisShippingAddressStrategy cisShippingAddressStrategy;
	@Mock
	private OndemandPromotionService ondemandPromotionService;
	@Mock
	private Converter<OndemandDiscountedOrderEntry, CisLineItem> cisLineItemConverter;
	@Mock
	private Converter<AbstractOrderModel, CisLineItem> deliveryCisLineItemConverter;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		cisOrderPopulator = new CisOrderPopulator();
		cisOrderPopulator.setCisLineItemConverter(cisLineItemConverter);
		cisOrderPopulator.setCisShippingAddressStrategy(cisShippingAddressStrategy);
		cisOrderPopulator.setOndemandPromotionService(ondemandPromotionService);
		cisOrderPopulator.setDeliveryCisLineItemConverter(deliveryCisLineItemConverter);
	}

	@Test
	public void shouldPopulate()
	{
		final CisOrder cisOrder = new CisOrder();
		final AbstractOrderModel abstractOrder = Mockito.mock(AbstractOrderModel.class);
		final OndemandDiscountedOrderEntry ondemandDiscountedOrderEntry = Mockito.mock(OndemandDiscountedOrderEntry.class);
		final AbstractOrderEntryModel abstractOrderEntry = Mockito.mock(AbstractOrderEntryModel.class);
		final CisLineItem cisLineItem = Mockito.mock(CisLineItem.class);
		final List<CisAddress> addresses = Mockito.mock(ArrayList.class);
		final List<CisLineItem> lineItems = new ArrayList();
		lineItems.add(cisLineItem);
		final CurrencyModel currencyModel = Mockito.mock(CurrencyModel.class);
		final Date orderDate = Mockito.mock(Date.class);
		given(currencyModel.getIsocode()).willReturn(CURRENCY_CODE);
		given(ondemandDiscountedOrderEntry.getOrderEntry()).willReturn(abstractOrderEntry);
		given(ondemandDiscountedOrderEntry.getDiscountedUnitPrice()).willReturn(BigDecimal.valueOf(123));
		given(ondemandPromotionService.calculateProportionalDiscountForEntries(abstractOrder)).willReturn(
				Collections.singletonList(ondemandDiscountedOrderEntry));
		given(cisShippingAddressStrategy.getAddresses(abstractOrder)).willReturn(addresses);
		given(cisLineItemConverter.convert(ondemandDiscountedOrderEntry)).willReturn(cisLineItem);
		given(deliveryCisLineItemConverter.convert(abstractOrder)).willReturn(cisLineItem);
		given(abstractOrder.getCode()).willReturn(ORDER_CODE);
		given(abstractOrder.getCurrency()).willReturn(currencyModel);
		given(abstractOrder.getDate()).willReturn(orderDate);

		cisOrderPopulator.populate(abstractOrder, cisOrder);

		Assert.assertEquals(addresses, cisOrder.getAddresses());
		Assert.assertEquals(lineItems, cisOrder.getLineItems());
		Assert.assertEquals(ORDER_CODE, cisOrder.getId());
		Assert.assertEquals(CURRENCY_CODE, cisOrder.getCurrency());
		Assert.assertEquals(orderDate, cisOrder.getDate());

	}
}
