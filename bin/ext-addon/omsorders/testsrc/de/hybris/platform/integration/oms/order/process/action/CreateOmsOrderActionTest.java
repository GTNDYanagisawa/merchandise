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
package de.hybris.platform.integration.oms.order.process.action;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.impersonation.impl.DefaultImpersonationService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.integration.oms.order.service.OmsOrderService;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.testframework.TestUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CreateOmsOrderActionTest
{
	private final CreateOmsOrderAction action = new CreateOmsOrderAction();

	@Mock
	private OmsOrderService omsOrderService;

	@Mock
	private OrderProcessModel orderProcessModel;

	@Mock
	private OrderModel order;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private DefaultImpersonationService impersonationService;


	@Before
	public void init() throws Throwable
	{
		MockitoAnnotations.initMocks(this);
		action.setOmsOrderService(omsOrderService);
		action.setImpersonationService(impersonationService);
		action.setCatalogVersionService(catalogVersionService);
		action.setMaxRetryCount(5);
		action.setRetryDelay(1000);
		Mockito.when(order.getCode()).thenReturn("order1");
		Mockito.when(orderProcessModel.getOrder()).thenReturn(order);
	}

	@Test
	public void shouldCreateOmsOrder() throws Throwable
	{
		Mockito.when(
				impersonationService.executeInContext(Mockito.any(ImpersonationContext.class),
						Mockito.any(ImpersonationService.Executor.class))).thenReturn(Transition.OK);
		final Transition transition = action.executeAction(orderProcessModel);
		Assert.assertEquals(Transition.OK, transition);
	}

	@Test(expected = RetryLaterException.class)
	public void shouldSetTheOrderToRetry() throws Throwable
	{
		TestUtils.disableFileAnalyzer("Here an exception is expectred");
		try
		{
			Mockito.doThrow(new RetryLaterException()).when(impersonationService)
					.executeInContext(Mockito.any(ImpersonationContext.class), Mockito.any(ImpersonationService.Executor.class));
			action.executeAction(orderProcessModel);
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test
	public void shouldNotCreateOmsOrder() throws Throwable
	{
		Mockito.when(
				impersonationService.executeInContext(Mockito.any(ImpersonationContext.class),
						Mockito.any(ImpersonationService.Executor.class))).thenReturn(Transition.NOK);
		final Transition transition = action.executeAction(orderProcessModel);
		Assert.assertEquals(Transition.NOK, transition);
	}

}
