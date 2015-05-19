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
package de.hybris.platform.integration.oms.order.service.impl;

import com.hybris.commons.client.RestCallException;
import com.hybris.commons.client.RestResponse;
import com.hybris.oms.domain.order.Order;
import com.hybris.oms.rest.client.order.OrderRestClient;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.integration.commons.hystrix.HystrixExecutable;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandConfiguration;
import de.hybris.platform.integration.commons.hystrix.OndemandHystrixCommandFactory;
import de.hybris.platform.integration.oms.order.data.OrderPlacementResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketBusinessService;
import java.util.Date;
import javax.ws.rs.core.Response.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class OmsOrderServiceTest
{
	private final DefaultOmsOrderService defaultOmsOrderService = new DefaultOmsOrderService();

	@Mock
	private ModelService modelService;
	@Mock
	private TicketBusinessService ticketBusinessService;
	@Mock
	private OrderModel order;
	@Mock
	private CsTicketModel ticket;
	@Mock
	private RestResponse restResponse;
	@Mock
	private AbstractConverter<OrderModel, Order> orderConverter;
	@Mock
	private OrderRestClient omsOrdersRestClient;
	@Mock
	private Order convertedOrder;
	@Mock
	private OndemandHystrixCommandConfiguration hystrixConfiguration;
	@Mock
	private OndemandHystrixCommandFactory ondemandHystrixCommandFactory;

	@Before
	public void setupMocks()
	{
		MockitoAnnotations.initMocks(this);
		defaultOmsOrderService.setModelService(modelService);
		defaultOmsOrderService.setTicketBusinessService(ticketBusinessService);
		defaultOmsOrderService.setOrderConverter(orderConverter);
		defaultOmsOrderService.setOrderRestClient(omsOrdersRestClient);
		defaultOmsOrderService.setHystrixCommandConfig(hystrixConfiguration);
		defaultOmsOrderService.setOndemandHystrixCommandFactory(ondemandHystrixCommandFactory);

	}

	@Test
	public void shouldCreateOmsOrder()
	{
		final OrderPlacementResult result = new OrderPlacementResult(OrderPlacementResult.Status.SUCCESS);
		final OndemandHystrixCommandFactory.OndemandHystrixCommand command = Mockito
				.mock(OndemandHystrixCommandFactory.OndemandHystrixCommand.class);
		Mockito.when(
				ondemandHystrixCommandFactory.newCommand(Mockito.any(OndemandHystrixCommandConfiguration.class),
						Mockito.any(HystrixExecutable.class))).thenReturn(command);
		Mockito.when(command.execute()).thenReturn(result);
		defaultOmsOrderService.createOmsOrder(order);
		Mockito.verify(modelService, Mockito.atLeast(2)).save(order);
		Mockito.verify(modelService, Mockito.never()).create(CsTicketModel.class);
		Mockito.verify(order).setOrderExportTime(Mockito.any(Date.class));
	}

	@Test
	public void shouldNotCreateOmsOrder()
	{
		Mockito.doThrow(new RestCallException(restResponse)).when(ondemandHystrixCommandFactory).newCommand(Mockito.any(OndemandHystrixCommandConfiguration.class),
								Mockito.any(HystrixExecutable.class));
		Mockito.when(restResponse.getStatus()).thenReturn(Status.SERVICE_UNAVAILABLE);
		Mockito.when(Integer.valueOf(restResponse.getStatusCode())).thenReturn(
				Integer.valueOf(Status.SERVICE_UNAVAILABLE.getStatusCode()));
		try
		{
			defaultOmsOrderService.createOmsOrder(order);
		}
		catch (final RestCallException e)
		{
			// do nothing, this is expected
		}
		Mockito.verify(modelService, Mockito.times(1)).save(order);
		Mockito.verify(modelService, Mockito.never()).create(CsTicketModel.class);
		Mockito.verify(order, Mockito.never()).setOrderExportTime(Mockito.any(Date.class));
	}


}
