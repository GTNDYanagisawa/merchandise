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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ScheduledCartPopulatorTest
{
	private static final String JOB_CODE = "jobCode";


	@Mock
	private Converter<CartModel, CartData> cartConverter;
	@Mock
	private Converter<TriggerModel, TriggerData> triggerConverter;

	private ScheduledCartPopulator scheduledCartPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		scheduledCartPopulator = new ScheduledCartPopulator();
		scheduledCartPopulator.setCartConverter(cartConverter);
		scheduledCartPopulator.setTriggerConverter(triggerConverter);

	}

	@Test
	public void testConvert()
	{
		final CartModel cartModel = mock(CartModel.class);
		final TriggerModel triggerModel = mock(TriggerModel.class);
		final JobModel jobModel = mock(JobModel.class);
		final ArrayList<OrderModel> orders = new ArrayList<OrderModel>();
		final OrderModel first = mock(OrderModel.class);
		final OrderModel second = mock(OrderModel.class);
		final Calendar calendar = Calendar.getInstance();
		final Date firstDate = calendar.getTime();
		given(first.getDate()).willReturn(firstDate);
		calendar.add(Calendar.MONTH, 1);
		given(second.getDate()).willReturn(calendar.getTime());
		orders.add(second);
		orders.add(first);


		given(jobModel.getCode()).willReturn(JOB_CODE);


		final CartToOrderCronJobModel cronjob = new CartToOrderCronJobModel();
		cronjob.setCart(cartModel);
		cronjob.setActive(Boolean.TRUE);
		cronjob.setTriggers(Collections.singletonList(triggerModel));
		cronjob.setJob(jobModel);
		cronjob.setOrders(orders);
		cronjob.setCode(JOB_CODE);


		final ScheduledCartData scheduleCartData = new ScheduledCartData();
		scheduledCartPopulator.populate(cronjob, scheduleCartData);
		Assert.assertEquals(JOB_CODE, scheduleCartData.getJobCode());
		Assert.assertEquals(firstDate, scheduleCartData.getFirstOrderDate());
		verify(cartConverter).convert(cartModel, scheduleCartData);
		verify(triggerConverter).convert(triggerModel);
	}

	@Test
	public void testFirstOrderDate()
	{
		final CartModel cartModel = mock(CartModel.class);
		final TriggerModel triggerModel = mock(TriggerModel.class);
		final JobModel jobModel = mock(JobModel.class);
		final TriggerData triggerData = mock(TriggerData.class);

		final ArrayList<OrderModel> orders = new ArrayList<OrderModel>();
		final OrderModel first = mock(OrderModel.class);
		final Calendar calendar = Calendar.getInstance();
		final Date firstDate = calendar.getTime();
		given(first.getDate()).willReturn(firstDate);
		orders.add(first);

		calendar.add(Calendar.YEAR, 1);
		final Date nextYear = calendar.getTime();

		given(triggerData.getActivationTime()).willReturn(nextYear);

		final CartToOrderCronJobModel cronjob = new CartToOrderCronJobModel();
		cronjob.setCart(cartModel);
		cronjob.setActive(Boolean.TRUE);
		cronjob.setTriggers(Collections.singletonList(triggerModel));
		cronjob.setJob(jobModel);
		cronjob.setCode(JOB_CODE);

		given(triggerConverter.convert(Mockito.any(TriggerModel.class))).willReturn(triggerData);

		final ScheduledCartData scheduleCartData = new ScheduledCartData();
		scheduledCartPopulator.populate(cronjob, scheduleCartData);
		Assert.assertEquals(nextYear, scheduleCartData.getFirstOrderDate());
		cronjob.setOrders(orders);
		scheduledCartPopulator.populate(cronjob, scheduleCartData);
		Assert.assertEquals(firstDate, scheduleCartData.getFirstOrderDate());

	}
}
