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

import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordercancel.exceptions.OrderCancelRecordsHandlerException;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelService;
import de.hybris.platform.ordercancel.impl.denialstrategies.AbstractCancelDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.DefaultRequestOriginDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.OrderStateDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.PartialCancelRulesViolationStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.SentToWarehouseDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.ShippingDenialStrategy;
import de.hybris.platform.ordercancel.impl.denialstrategies.SingleCancelRequestDenialStrategy;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests if cancel is possible by using
 * {@link OrderCancelService#isCancelPossible(OrderModel, de.hybris.platform.core.model.security.PrincipalModel, boolean, boolean)}
 * method in different scenarios.
 */
public class OrderCancelPossibilityTest extends AbstractOrderCancelTest
{
	private static final Logger LOG = Logger.getLogger(OrderCancelPossibilityTest.class);

	@Resource
	private OrderCancelRecordsHandler orderCancelRecordsHandler;

	@Resource
	private DefaultOrderCancelService orderCancelService;

	private OrderModel order;

	private PrincipalModel currentPrincipal;

	private Map<String, OrderCancelDenialReason> originalReasons;

	@Before
	public void customSetUp() throws InvalidCartException
	{
		LOG.info("Setting up OrderCancelDaoTest");
		order = placeTestOrder();
		currentPrincipal = REQUEST_ORIGIN_STRATEGY_CUSTOMER.getCallingPrincipal();

		originalReasons = getOriginalReasons(orderCancelService);

		injectDenialReasons(orderCancelService);

		configuration = getOrCreateOrderCancelConfig(3600);
		configuration.setPartialCancelAllowed(true);
		configuration.setPartialOrderEntryCancelAllowed(true);
		configuration.setOrderCancelAllowed(true);
		configuration.setCancelAfterWarehouseAllowed(true);
		configuration.setCompleteCancelAfterShippingStartedAllowed(true);

		final OrderCancelDaoMock ocdMock = new OrderCancelDaoMock(configuration);
		this.orderCancelService.setOrderCancelDao(ocdMock);
	}



	/**
	 * Tests state-based conditions, under which no Cancel Request is possible
	 */
	@Test
	public void testRequestImpossibleOnClosedOrders() throws OrderCancelException
	{
		final OrderCancelRequest completeCancelRequest = prepareCompleteCancelRequest(order);
		final OrderCancelRequest partialCancelRequest = preparePartialCancelRequest(order, 1);

		//		final OrderCancelConfigModel configuration = createOrderCancelConfig(3600);
		configuration.setPartialOrderEntryCancelAllowed(true);
		configuration.setOrderCancelAllowed(true);
		configuration.setCancelAfterWarehouseAllowed(true);
		configuration.setCompleteCancelAfterShippingStartedAllowed(true);
		configuration.setPartialCancelAllowed(true);

		for (final OrderCancelStateMappingStrategy stateStrategy : new OrderCancelStateMappingStrategy[]
		{ STATE_STRATEGY_IMPOSSIBLE })
		{
			injectStateMappingStrategy(stateStrategy);

			Assert.assertFalse("Order Cancel Request should be impossible in state: "
					+ stateStrategy.getOrderCancelState(partialCancelRequest.getOrder()), isCancelPossible(partialCancelRequest,
					configuration, orderStateDenialStrategyReason));

			Assert.assertFalse("Order Cancel Request should be impossible in state: "
					+ stateStrategy.getOrderCancelState(completeCancelRequest.getOrder()), isCancelPossible(completeCancelRequest,
					configuration, orderStateDenialStrategyReason));
		}
	}

	/**
	 * Tests state-based conditions, under which no Cancel Request is possible
	 * 
	 * @throws OrderCancelException
	 */
	@Test
	public void testRequestImpossibleOnPendingCancelOrders() throws OrderCancelException
	{
		final OrderCancelRequest pendingCancelRequest = prepareCompleteCancelRequest(order);

		final OrderCancelRequest completeCancelRequest = prepareCompleteCancelRequest(order);
		final OrderCancelRequest partialCancelRequest = preparePartialCancelRequest(order, 1);

		injectStateMappingStrategy(STATE_STRATEGY_QUEUE_WAITING);

		//final OrderCancelConfigModel configuration = createOrderCancelConfig(3600);
		configuration.setPartialOrderEntryCancelAllowed(true);
		configuration.setOrderCancelAllowed(true);
		configuration.setCancelAfterWarehouseAllowed(true);
		configuration.setCompleteCancelAfterShippingStartedAllowed(true);
		configuration.setPartialCancelAllowed(true);

		Assert.assertTrue("Order Cancel Partial Request should be possible", isCancelPossible(partialCancelRequest, configuration));
		Assert.assertTrue("Order Cancel Complete Request should be possible",
				isCancelPossible(completeCancelRequest, configuration));

		try
		{
			preparePendingCancelRequest(pendingCancelRequest);

			Assert.assertFalse("Order Cancel Partial Request should be impossible because of already pending cancel request",
					isCancelPossible(partialCancelRequest, configuration, singleCancelRequestDenialStrategyReason));
			Assert.assertFalse("Order Cancel Complete Request should be impossible  because of already pending cancel request",
					isCancelPossible(completeCancelRequest, configuration, singleCancelRequestDenialStrategyReason));

		}
		finally
		{
			modelService.remove(this.orderCancelRecordsHandler.getCancelRecord(pendingCancelRequest.getOrder()));
		}

		Assert.assertTrue("Order Cancel Partial Request should be possible", isCancelPossible(partialCancelRequest, configuration));
		Assert.assertTrue("Order Cancel Complete Request should be possible",
				isCancelPossible(completeCancelRequest, configuration));

	}

	/**
	 * Tests how configuration affects processing Complete Cancel Requests with different origins (Customer/CSA)
	 */
	@Test
	public void testCompleteCancelOrigin() throws OrderCancelException
	{
		final OrderCancelRequest cancelRequest = prepareCompleteCancelRequest(order);
		//final OrderCancelConfigModel configuration = createOrderCancelConfig(3600);

		injectStateMappingStrategy(STATE_STRATEGY_QUEUE_WAITING);

		currentPrincipal = REQUEST_ORIGIN_STRATEGY_CUSTOMER.getCallingPrincipal();

		configuration.setOrderCancelAllowed(true);
		Assert.assertTrue("Order Cancel Complete should be allowed for orders in holding area", isCancelPossible(cancelRequest,
				configuration));

		configuration.setOrderCancelAllowed(false);
		Assert
				.assertFalse(
						"Order Cancel Complete should be disallowed for customer-created orders in holding area when configuration disallows it",
						isCancelPossible(cancelRequest, configuration, defaultRequestOriginDenialStrategyReason));

		//orderCancelService.setRequestOriginStrategy(REQUEST_ORIGIN_STRATEGY_CSA);
		currentPrincipal = REQUEST_ORIGIN_STRATEGY_CSA.getCallingPrincipal();

		Assert
				.assertTrue(
						"Order Cancel Complete should be allowed for csa-created orders in holding area even when configuration dissallows it for customer-created orders",
						isCancelPossible(cancelRequest, configuration));
	}


	/**
	 * Tests how configuration affects processing of Partial Cancel Requests
	 */
	@Test
	public void testPartialCancelAcrossStates() throws OrderCancelException
	{
		final OrderCancelRequest partialCancelRequest = preparePartialCancelRequest(order, 1);
		//final OrderCancelConfigModel configuration = createOrderCancelConfig(3600);
		configuration.setCancelAfterWarehouseAllowed(true);
		configuration.setPartialOrderEntryCancelAllowed(true);
		for (final RequestOriginStrategyHelper originStrategy : new RequestOriginStrategyHelper[]
		{ REQUEST_ORIGIN_STRATEGY_CUSTOMER, REQUEST_ORIGIN_STRATEGY_CSA })
		{
			//orderCancelService.setRequestOriginStrategy(originStrategy);
			currentPrincipal = originStrategy.getCallingPrincipal();

			for (final OrderCancelStateMappingStrategy stateStrategy : new OrderCancelStateMappingStrategy[]
			{ STATE_STRATEGY_QUEUE_WAITING, STATE_STRATEGY_SENT_TO_WAREHOUSE, STATE_STRATEGY_SHIPPING,
					STATE_STRATEGY_PARTIALLY_SHIPPED })
			{
				orderCancelService.setStateMappingStrategy(stateStrategy);
				configuration.setPartialCancelAllowed(true);
				Assert.assertTrue("Order Cancel Partial should be allowed when configuration allows it", isCancelPossible(
						partialCancelRequest, configuration));

				configuration.setPartialCancelAllowed(false);
				Assert.assertFalse("Order Cancel Partial should be disallowed when configuration disallows it", isCancelPossible(
						partialCancelRequest, configuration, partialCancelRulesViolationStrategyReason));
			}
		}
	}


	/**
	 * Tests how configuration settings affect Complete Cancel Requests for orders sent to Warehouse
	 */
	@Test
	public void testCompleteCancelPossibleInWHS() throws OrderCancelException
	{
		injectStateMappingStrategy(STATE_STRATEGY_SENT_TO_WAREHOUSE);
		final OrderCancelRequest cancelRequest = prepareCompleteCancelRequest(order);
		//final OrderCancelConfigModel configuration = createOrderCancelConfig(3600);
		configuration.setCancelAfterWarehouseAllowed(true);
		Assert.assertTrue("Order Cancel Complete should be allowed for orders sent to Warehouse when configuration allows it",
				isCancelPossible(cancelRequest, configuration));

		configuration.setCancelAfterWarehouseAllowed(false);
		Assert.assertFalse(
				"Order Cancel Complete should be disallowed for orders sent to Warehouse when configuration disallows it",
				isCancelPossible(cancelRequest, configuration, sentToWarehouseDenialStrategyReason));
	}


	/**
	 * Tests how configuration settings affect Complete Cancel Requests for orders that are being shipped or are shipped
	 */
	@Test
	public void testCompleteCancelPossibleInShipping() throws OrderCancelException
	{
		final OrderCancelRequest cancelRequest = prepareCompleteCancelRequest(order);
		//final OrderCancelConfigModel configuration = createOrderCancelConfig(3600);
		configuration.setCancelAfterWarehouseAllowed(true);


		injectStateMappingStrategy(STATE_STRATEGY_SHIPPING);
		configuration.setCompleteCancelAfterShippingStartedAllowed(true);
		Assert.assertTrue("Order Cancel Complete should be allowed for orders after shipping started when configuration allows it",
				isCancelPossible(cancelRequest, configuration));

		configuration.setCompleteCancelAfterShippingStartedAllowed(false);
		Assert.assertFalse(
				"Order Cancel Complete should be disallowed for orders after shipping started when configuration disallows it",
				isCancelPossible(cancelRequest, configuration, shippingDenialStrategyReason));

		injectStateMappingStrategy(STATE_STRATEGY_PARTIALLY_SHIPPED);

		configuration.setCompleteCancelAfterShippingStartedAllowed(true);
		Assert.assertFalse("Order Cancel Complete should be disallowed for partially shipped orders ", isCancelPossible(
				cancelRequest, configuration, orderStateDenialStrategyReason));

		configuration.setCompleteCancelAfterShippingStartedAllowed(false);
		Assert.assertFalse("Order Cancel Complete should be disallowed for partially shipped orders ", isCancelPossible(
				cancelRequest, configuration, orderStateDenialStrategyReason, shippingDenialStrategyReason));


	}



	private interface RequestOriginStrategyHelper
	{
		PrincipalModel getCallingPrincipal();
	}

	private final static RequestOriginStrategyHelper REQUEST_ORIGIN_STRATEGY_CSA = new RequestOriginStrategyHelper()
	{

		@Override
		public PrincipalModel getCallingPrincipal()
		{
			return new EmployeeModel();
		}
	};

	private final static RequestOriginStrategyHelper REQUEST_ORIGIN_STRATEGY_CUSTOMER = new RequestOriginStrategyHelper()
	{
		@Override
		public PrincipalModel getCallingPrincipal()
		{
			return new CustomerModel();
		}
	};

	private static abstract class AbstractMockOrderCancelStateManagementStrategy implements OrderCancelStateMappingStrategy
	{
		//
	}

	private final static OrderCancelStateMappingStrategy STATE_STRATEGY_QUEUE_WAITING = new AbstractMockOrderCancelStateManagementStrategy()
	{

		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.PENDINGORHOLDINGAREA;
		}
	};

	private final static OrderCancelStateMappingStrategy STATE_STRATEGY_SENT_TO_WAREHOUSE = new AbstractMockOrderCancelStateManagementStrategy()
	{

		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.SENTTOWAREHOUSE;
		}
	};

	private final static OrderCancelStateMappingStrategy STATE_STRATEGY_SHIPPING = new AbstractMockOrderCancelStateManagementStrategy()
	{

		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.SHIPPING;
		}
	};

	private final static OrderCancelStateMappingStrategy STATE_STRATEGY_PARTIALLY_SHIPPED = new AbstractMockOrderCancelStateManagementStrategy()
	{

		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.PARTIALLYSHIPPED;
		}
	};

	private final static OrderCancelStateMappingStrategy STATE_STRATEGY_IMPOSSIBLE = new AbstractMockOrderCancelStateManagementStrategy()
	{

		@Override
		public OrderCancelState getOrderCancelState(final OrderModel order)
		{
			return OrderCancelState.CANCELIMPOSSIBLE;
		}
	};

	private final static DefaultOrderCancelDenialReason singleCancelRequestDenialStrategyReason = new DefaultOrderCancelDenialReason(
			1, SingleCancelRequestDenialStrategy.class.getName());
	private final static DefaultOrderCancelDenialReason defaultRequestOriginDenialStrategyReason = new DefaultOrderCancelDenialReason(
			1, DefaultRequestOriginDenialStrategy.class.getName());
	private final static DefaultOrderCancelDenialReason partialCancelRulesViolationStrategyReason = new DefaultOrderCancelDenialReason(
			1, PartialCancelRulesViolationStrategy.class.getName());
	private final static DefaultOrderCancelDenialReason orderStateDenialStrategyReason = new DefaultOrderCancelDenialReason(1,
			OrderStateDenialStrategy.class.getName());
	private final static DefaultOrderCancelDenialReason sentToWarehouseDenialStrategyReason = new DefaultOrderCancelDenialReason(
			1, SentToWarehouseDenialStrategy.class.getName());
	private final static DefaultOrderCancelDenialReason shippingDenialStrategyReason = new DefaultOrderCancelDenialReason(1,
			ShippingDenialStrategy.class.getName());


	/**
	 * Injects custom denial reasons into denial strategies. It is done to be independent on any Spring-based
	 * configuration, so the test would run fine even after Spring configuration is changed.
	 */
	private static void injectDenialReasons(final DefaultOrderCancelService orderCancelService)
	{
		//Prepare data structures
		final DefaultOrderCancelDenialReason[] reasonsArray = new DefaultOrderCancelDenialReason[]
		{ singleCancelRequestDenialStrategyReason, defaultRequestOriginDenialStrategyReason,
				partialCancelRulesViolationStrategyReason, orderStateDenialStrategyReason, sentToWarehouseDenialStrategyReason,
				shippingDenialStrategyReason };

		final Map<String, DefaultOrderCancelDenialReason> reasonsMap = new HashMap<String, DefaultOrderCancelDenialReason>();

		for (final DefaultOrderCancelDenialReason reason : reasonsArray)
		{
			reasonsMap.put(reason.getDescription(), reason);
		}

		//Inject
		for (final OrderCancelDenialStrategy ocds : orderCancelService.getCancelDenialStrategies())
		{
			if (ocds instanceof AbstractCancelDenialStrategy)
			{
				final DefaultOrderCancelDenialReason reason = reasonsMap.get(ocds.getClass().getName());
				if (reason == null)
				{
					throw new IllegalStateException("No reason found in reasonsMap");
				}
				((AbstractCancelDenialStrategy) ocds).setReason(reason);
			}
		}
	}

	private Map<String, OrderCancelDenialReason> getOriginalReasons(final DefaultOrderCancelService orderCancelService)
	{
		final Map<String, OrderCancelDenialReason> result = new HashMap<String, OrderCancelDenialReason>();
		for (final OrderCancelDenialStrategy ocds : orderCancelService.getCancelDenialStrategies())
		{
			result.put(ocds.getClass().getName(), ((AbstractCancelDenialStrategy) ocds).getReason());
		}
		return result;
	}



	/**
	 * Helper method to verify whether cancel is possible
	 * 
	 * @param request
	 * @param configuration
	 * @param denialReasons
	 * @return true if cancel is allowed, false otherwise
	 */
	private boolean isCancelPossible(final OrderCancelRequest request, final OrderCancelConfigModel configuration,
			final OrderCancelDenialReason... denialReasons)
	{
		//		final OrderCancelDaoMock ocdMock = new OrderCancelDaoMock(configuration);
		//		this.orderCancelService.setOrderCancelDao(ocdMock);

		final Set<OrderCancelDenialReason> expectedDenialReasonsSet = new HashSet<OrderCancelDenialReason>();
		if (denialReasons != null && denialReasons.length > 0)
		{
			for (final OrderCancelDenialReason ocdr : denialReasons)
			{
				expectedDenialReasonsSet.add(ocdr);
			}
		}

		final CancelDecision cancelDecision = orderCancelService.isCancelPossible(request.getOrder(), this.currentPrincipal,
				request.isPartialCancel(), request.isPartialEntryCancel());

		final Set<OrderCancelDenialReason> actualDenialReasonsSet = new HashSet<OrderCancelDenialReason>(cancelDecision
				.getDenialReasons());
		Assert.assertEquals("incorrect denial reasons in response", expectedDenialReasonsSet, actualDenialReasonsSet);

		return cancelDecision.isAllowed();
	}

	private OrderCancelRecordEntryModel preparePendingCancelRequest(final OrderCancelRequest request)
			throws OrderCancelRecordsHandlerException
	{
		return this.orderCancelRecordsHandler.createRecordEntry(request);
	}

	private OrderCancelRequest prepareCompleteCancelRequest(final OrderModel order)
	{
		return new OrderCancelRequest(order);
	}

	private OrderCancelRequest preparePartialCancelRequest(final OrderModel order, final int entryIndex)
	{
		final List<OrderCancelEntry> entries = new ArrayList<OrderCancelEntry>();
		entries.add(new OrderCancelEntry(order.getEntries().get(entryIndex), order.getEntries().get(entryIndex).getQuantity()
				.longValue() - 1));
		return new OrderCancelRequest(order, entries);
	}

	@Override
	@After
	public void tearDown()
	{

		for (final OrderCancelDenialStrategy ocds : orderCancelService.getCancelDenialStrategies())
		{
			if (ocds instanceof AbstractCancelDenialStrategy)
			{
				final OrderCancelDenialReason reason = originalReasons.get(ocds.getClass().getName());
				((AbstractCancelDenialStrategy) ocds).setReason(reason);

			}
		}
		super.tearDown();
	}

}
