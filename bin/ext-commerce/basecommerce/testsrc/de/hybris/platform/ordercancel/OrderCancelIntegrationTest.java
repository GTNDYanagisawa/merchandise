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
package de.hybris.platform.ordercancel;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * The test class tests the cancel service behavior basing on the <a
 * href="https://wiki.hybris.com/display/release4/OrderCancel+-+Default+Mapping+Strategy">default implementation</a> of
 * state mapping and internal business strategies.<br>
 * 
 * The test presumes the following configuration of {@link OrderCancelConfigModel}:<br>
 * <ul>
 * <li>{@link OrderCancelConfigModel#ORDERCANCELALLOWED} = TRUE</li>
 * <li>{@link OrderCancelConfigModel#CANCELAFTERWAREHOUSEALLOWED} = TRUE</li>
 * <li>{@link OrderCancelConfigModel#COMPLETECANCELAFTERSHIPPINGSTARTEDALLOWED} = TRUE</li>
 * <li>{@link OrderCancelConfigModel#PARTIALCANCELALLOWED} = TRUE</li>
 * <li>{@link OrderCancelConfigModel#PARTIALORDERENTRYCANCELALLOWED} = TRUE</li>
 * </ul>
 */
public class OrderCancelIntegrationTest extends ServicelayerTransactionalTest
{

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private OrderCancelService orderCancelService;

	@Resource
	private UserService userService;

	@Before
	public void setUp() throws Exception
	{

		importCsv("/testsrc/cancelOrderTestData.csv", "UTF-8");
	}

	/**
	 * trying to reproduce BCOM-126
	 */
	@Test
	public void testCancellableOrders() throws Exception
	{
		//--cancelable orders
		final OrderModel orderTemplate = new OrderModel();
		orderTemplate.setCode("O-K2010-C0000-001");
		final OrderModel cancellableOrder1 = flexibleSearchService.getModelByExample(orderTemplate);
		CancelDecision decision = orderCancelService.isCancelPossible(cancellableOrder1, userService.getAdminUser(), false, false);
		Assert.assertTrue("Sample order [" + cancellableOrder1.getCode() + "] should be fully cancellable", decision.isAllowed());

		orderTemplate.setCode("O-K2010-C0001-001");
		final OrderModel cancellableOrder2 = flexibleSearchService.getModelByExample(orderTemplate);
		decision = orderCancelService.isCancelPossible(cancellableOrder2, userService.getAdminUser(), false, false);
		Assert.assertTrue("Sample order [" + cancellableOrder2.getCode() + "] should be fully cancellable", decision.isAllowed());

		//-- partially cancelable
		orderTemplate.setCode("O-K2010-C0005-001");

		final OrderModel partiallyCancellableOrder = flexibleSearchService.getModelByExample(orderTemplate);

		Assert.assertEquals("unexpected order code", "O-K2010-C0005-001", partiallyCancellableOrder.getCode());
		Assert.assertNull("unexpected order version ID", partiallyCancellableOrder.getVersionID());
		Assert.assertEquals("unexpected order status", OrderStatus.CREATED, partiallyCancellableOrder.getStatus());

		CancelDecision fullCancelDecision = orderCancelService.isCancelPossible(partiallyCancellableOrder, userService
				.getAdminUser(), false, false);
		CancelDecision partiallyCancelDecision = orderCancelService.isCancelPossible(partiallyCancellableOrder, userService
				.getAdminUser(), true, false);
		Assert.assertFalse("Sample order [" + partiallyCancellableOrder.getCode() + "] should not fully cancellable",
				fullCancelDecision.isAllowed());
		Assert.assertTrue("Sample order [" + partiallyCancellableOrder.getCode() + "] should be partially cancellable",
				partiallyCancelDecision.isAllowed());

		//--non cancelable
		orderTemplate.setCode("O-K2010-C0002-001");
		final OrderModel nonCancellableOrder = flexibleSearchService.getModelByExample(orderTemplate);
		fullCancelDecision = orderCancelService.isCancelPossible(nonCancellableOrder, userService.getAdminUser(), false, false);
		partiallyCancelDecision = orderCancelService.isCancelPossible(nonCancellableOrder, userService.getAdminUser(), true, false);
		Assert.assertFalse("Sample order [" + nonCancellableOrder.getCode() + "] should not fully cancellable", fullCancelDecision
				.isAllowed());
		Assert.assertFalse("Sample order [" + nonCancellableOrder.getCode() + "] should not be partially cancellable",
				partiallyCancelDecision.isAllowed());


	}
}
