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
package de.hybris.platform.b2b.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTransactionalTest;
import de.hybris.platform.b2b.model.B2BCommentModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class DefaultB2BOrderServiceTest extends B2BIntegrationTransactionalTest
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultB2BOrderServiceTest.class);

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Test
	@Ignore
	public void shouldCalculateOrderForUserAssignedToMulitpleUnits() throws Exception
	{
		final String userId = "GC Sales Boss";
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));

		final B2BCustomerModel user = userService.getUserForUID(userId, B2BCustomerModel.class);
		Assert.assertNotNull(user.getGroups());
		final OrderStatus status = OrderStatus.CREATED;
		final OrderModel order = createOrderWithStatus(userId, status);
		order.setCurrency(commonI18NService.getCurrency("USD"));
		modelService.save(order);
		this.calculationService.recalculate(order);
		Assert.assertEquals(new Double(10d), order.getTotalPrice());

	}


	@Test
	public void testGetOrderByCode() throws Exception
	{
		final OrderModel order = createOrder(1);
		Assert.assertNotNull(b2bOrderService.getOrderForCode(order.getCode()));
	}

	@Test
	public void shouldFindRejectedOrders() throws Exception
	{
		final String userId = "IC CEO";
		final OrderStatus status = OrderStatus.REJECTED;
		final OrderModel order = createOrderWithStatus(userId, status);
		final UserModel user = order.getUser();
		final Collection<OrderModel> orders = b2bOrderService.getRejectedOrders(user);
		Assert.assertFalse(orders.isEmpty());
	}

	@Test
	public void shouldFindApprovedOrders() throws Exception
	{
		final String userId = "IC CEO";
		final OrderStatus status = OrderStatus.APPROVED;
		final OrderModel order = createOrderWithStatus(userId, status);
		final UserModel user = order.getUser();
		final Collection<OrderModel> orders = b2bOrderService.getApprovedOrders(user);
		Assert.assertFalse(orders.isEmpty());
	}

	@Test
	public void shouldFindErroredOrders() throws Exception
	{
		final String userId = "IC CEO";
		final OrderStatus status = OrderStatus.B2B_PROCESSING_ERROR;
		final OrderModel order = createOrderWithStatus(userId, status);
		final UserModel user = order.getUser();
		final Collection<OrderModel> orders = b2bOrderService.getErroredOrders(user);
		Assert.assertFalse(orders.isEmpty());
	}

	@Test
	public void shouldFindPendingApprovalOrders() throws Exception
	{
		final String userId = "IC CEO";
		final OrderStatus status = OrderStatus.PENDING_APPROVAL;
		final OrderModel order = createOrderWithStatus(userId, status);
		final UserModel user = order.getUser();
		final Collection<OrderModel> orders = b2bOrderService.getPendingApprovalOrders(user);
		Assert.assertFalse(orders.isEmpty());
	}


	private OrderModel createOrderWithStatus(final String userId, final OrderStatus status) throws InvalidCartException
	{
		login(userId);
		// Create automatically include B2BUnit
		final CartModel cartModel = b2bCartFactory.createCart();
		b2bCartService.addNewEntry(cartModel, productService.getProductForCode("b2bproduct"), 1, null);
		modelService.save(cartModel);
		final OrderModel orderModel = b2bOrderService.placeOrder(cartModel, null, null, null);
		orderModel.setStatus(status);
		modelService.save(orderModel);
		return orderModel;
	}

	@Test
	public void testOrderComment() throws Exception
	{
		final OrderModel order = createOrder(1);

		final B2BCommentModel b2BCommentModel = modelService.create(B2BCommentModel.class);
		b2BCommentModel.setCode("QuoteRequest");
		b2BCommentModel.setComment("Requesting 5% discount.");
		b2BCommentModel.setModifiedDate(new Date());

		final Date modifiedDate = DateUtils.addDays(b2BCommentModel.getModifiedDate(), 10);
		order.setQuoteExpirationDate(modifiedDate);
		order.setB2bcomments(Collections.singletonList(b2BCommentModel));
		modelService.save(order);

		final OrderModel retrievedOrder = b2bOrderService.getOrderForCode(order.getCode());
		final List<B2BCommentModel> retrievedComments = (List<B2BCommentModel>) retrievedOrder.getB2bcomments();

		Assert.assertNotNull(CollectionUtils.find(retrievedComments, new BeanPropertyValueEqualsPredicate(B2BCommentModel.CODE,
				"QuoteRequest")));

		Assert.assertTrue(order.getQuoteExpirationDate().equals(modifiedDate));
	}
}
