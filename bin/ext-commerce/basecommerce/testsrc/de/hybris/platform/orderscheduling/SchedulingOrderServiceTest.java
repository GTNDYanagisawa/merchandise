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
package de.hybris.platform.orderscheduling;

import static org.junit.Assert.assertNotNull;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.orderscheduling.impl.DefaultScheduleOrderServiceImpl;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.orderscheduling.model.OrderScheduleCronJobModel;
import de.hybris.platform.orderscheduling.model.OrderTemplateToOrderCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
public class SchedulingOrderServiceTest
{

	private DefaultScheduleOrderServiceImpl scheduleOrderService;

	private AddressModel deliveryAddress;
	private DebitPaymentInfoModel paymentInfo;
	private CartModel cart;
	private OrderModel order;
	private List<TriggerModel> triggerList;

	@Mock
	private CronJobService cronJobService;

	@Mock
	private ModelService modelService;

	@Before
	public void initTest() throws Exception //NOPMD
	{
		MockitoAnnotations.initMocks(this);

		scheduleOrderService = new DefaultScheduleOrderServiceImpl();

		scheduleOrderService.setCronJobService(cronJobService);
		scheduleOrderService.setModelService(modelService);

		cart = new CartModel();

		deliveryAddress = new AddressModel();
		deliveryAddress.setFirstname("Krzysztof");
		deliveryAddress.setLastname("Kwiatosz");
		deliveryAddress.setTown("Katowice");
		deliveryAddress.setOwner(new UserModel());



		paymentInfo = new DebitPaymentInfoModel();
		paymentInfo.setOwner(cart);
		paymentInfo.setBank("Bank");
		paymentInfo.setUser(new UserModel());
		paymentInfo.setAccountNumber("34434");
		paymentInfo.setBankIDNumber("1111112");
		paymentInfo.setBaOwner("I");

		final CartEntryModel cem1 = new CartEntryModel();
		cem1.setProduct(new ProductModel());
		final CartEntryModel cem2 = new CartEntryModel();
		cem1.setProduct(new ProductModel());

		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(cem1);
		entries.add(cem2);
		cart.setEntries(entries);


		order = new OrderModel();
		final OrderEntryModel oem1 = new OrderEntryModel();
		oem1.setProduct(new ProductModel());
		final OrderEntryModel oem2 = new OrderEntryModel();
		oem1.setProduct(new ProductModel());

		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<AbstractOrderEntryModel>();
		orderEntries.add(oem1);
		orderEntries.add(oem2);

		order.setEntries(orderEntries);

		triggerList = new ArrayList<TriggerModel>();
		final TriggerModel trigger = new TriggerModel();

		trigger.setActivationTime(new Date());
		triggerList.add(trigger);
	}

	@Test
	public void testCreateOrderFromCartCronJob()
	{
		Mockito.when(cronJobService.getCronJob("cartToOrderJob")).thenReturn(new CronJobModel());

		Mockito.when(modelService.create(CartToOrderCronJobModel.class)).thenReturn(new CartToOrderCronJobModel());

		assertNotNull("CronJob should be created",
				scheduleOrderService.createOrderFromCartCronJob(cart, deliveryAddress, deliveryAddress, paymentInfo, triggerList));



	}

	@Test
	public void testCreateOrderFromOrderTemplateCronJob()
	{
		Mockito.when(cronJobService.getCronJob("orderTemplateToOrderJob")).thenReturn(new CronJobModel());

		Mockito.when(modelService.create(OrderTemplateToOrderCronJobModel.class))
				.thenReturn(new OrderTemplateToOrderCronJobModel());

		assertNotNull("CronJob should be created", scheduleOrderService.createOrderFromOrderTemplateCronJob(order, triggerList));
	}

	@Test
	public void testCreateScheduledOrderCronJob()
	{
		Mockito.when(cronJobService.getCronJob("orderScheduleJob")).thenReturn(new CronJobModel());
		Mockito.when(modelService.create(OrderScheduleCronJobModel.class)).thenReturn(new OrderScheduleCronJobModel());


		assertNotNull("CronJob should be created", scheduleOrderService.createScheduledOrderCronJob(order, triggerList));
	}

}
