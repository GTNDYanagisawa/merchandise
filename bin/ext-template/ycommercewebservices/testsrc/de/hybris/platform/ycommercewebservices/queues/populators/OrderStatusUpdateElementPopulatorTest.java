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
 */
package de.hybris.platform.ycommercewebservices.queues.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ycommercewebservices.queues.data.OrderStatusUpdateElementData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class OrderStatusUpdateElementPopulatorTest
{
	private static final String ORDER_CODE = "orderCode";
	private static final OrderStatus ORDER_STATUS = OrderStatus.CREATED;
	private OrderStatusUpdateElementPopulator orderStatusUpdateElementPopulator;
	private Converter<OrderModel, OrderStatusUpdateElementData> orderStatusUpdateElementConverter;
	@Mock
	private OrderModel order;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		orderStatusUpdateElementPopulator = new OrderStatusUpdateElementPopulator();
		orderStatusUpdateElementConverter = new ConverterFactory<OrderModel, OrderStatusUpdateElementData, OrderStatusUpdateElementPopulator>()
				.create(OrderStatusUpdateElementData.class, orderStatusUpdateElementPopulator);

		given(order.getCode()).willReturn(ORDER_CODE);
		given(order.getStatus()).willReturn(ORDER_STATUS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenSourceIsNull()
	{
		orderStatusUpdateElementConverter.convert(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenTargetIsNull()
	{
		orderStatusUpdateElementConverter.convert(mock(OrderModel.class), null);
	}

	@Test
	public void testConvert()
	{
		final OrderStatusUpdateElementData result = orderStatusUpdateElementConverter.convert(order);

		Assert.assertEquals(ORDER_CODE, result.getCode());
		Assert.assertEquals(ORDER_STATUS.getCode(), result.getStatus());
	}

	@Test
	public void testConvertWithResultCreated()
	{
		final OrderStatusUpdateElementData result = new OrderStatusUpdateElementData();
		orderStatusUpdateElementConverter.convert(order, result);

		Assert.assertEquals(ORDER_CODE, result.getCode());
		Assert.assertEquals(ORDER_STATUS.getCode(), result.getStatus());
	}

}
