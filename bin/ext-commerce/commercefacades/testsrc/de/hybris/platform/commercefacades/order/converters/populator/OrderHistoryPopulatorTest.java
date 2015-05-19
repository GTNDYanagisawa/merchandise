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
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;

import java.sql.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class OrderHistoryPopulatorTest
{
	@Mock
	private PriceDataFactory priceDataFactory;

	private AbstractPopulatingConverter<OrderModel, OrderHistoryData> orderHistoryConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final OrderHistoryPopulator orderHistoryPopulator = new OrderHistoryPopulator();
		orderHistoryPopulator.setPriceDataFactory(priceDataFactory);
		orderHistoryConverter = new ConverterFactory<OrderModel, OrderHistoryData, OrderHistoryPopulator>().create(
				OrderHistoryData.class, orderHistoryPopulator);
	}

	@Test
	public void testConvert()
	{
		final OrderModel orderModel = mock(OrderModel.class);
		final Date date = mock(Date.class);
		final OrderStatus orderStatus = mock(OrderStatus.class);
		given(orderModel.getCode()).willReturn("code");
		given(orderModel.getDate()).willReturn(date);
		given(orderModel.getStatus()).willReturn(orderStatus);
		given(orderModel.getTotalPrice()).willReturn(Double.valueOf(123.0));
		final OrderHistoryData orderHistoryData = orderHistoryConverter.convert(orderModel);
		Assert.assertEquals("code", orderHistoryData.getCode());
		Assert.assertEquals(date, orderHistoryData.getPlaced());
		Assert.assertEquals(orderStatus, orderHistoryData.getStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertNull()
	{
		orderHistoryConverter.convert(null);
	}
}
