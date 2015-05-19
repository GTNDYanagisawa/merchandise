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
 *
 *
 */
package de.hybris.platform.subscriptionservices.interceptor.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link AbstractOrderLoadInterceptor}
 */
@UnitTest
public class AbstractOrderLoadInterceptorTest
{
	private AbstractOrderLoadInterceptor orderLoadInterceptor;

	private CartModel masterCart;
	private CartModel childCartMonthly;
	private CartModel childCartQuarterly;
	private CartModel childCartYearly;

	@Before
	public void setUp() throws Exception
	{
		orderLoadInterceptor = new AbstractOrderLoadInterceptor();
		setupCarts();
	}

	@Test
	public void testOnLoadChildCart() throws InterceptorException
	{
		orderLoadInterceptor.onLoad(childCartMonthly, null);
		assertTrue("", CollectionUtils.isEmpty(childCartMonthly.getChildren()));
	}

	@Test
	public void testOnLoadMasterCartWithoutChildCarts() throws InterceptorException
	{
		orderLoadInterceptor.onLoad(masterCart, null);
		assertTrue("", CollectionUtils.isEmpty(masterCart.getChildren()));
	}

	@Test
	public void testOnLoadMasterCartWithSortedChildCarts() throws InterceptorException
	{
		final List<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>();
		childCarts.add(childCartMonthly);
		childCarts.add(childCartQuarterly);
		childCarts.add(childCartYearly);
		masterCart.setChildren(childCarts);

		orderLoadInterceptor.onLoad(masterCart, null);
		verifyChildCartSequence(masterCart);
	}

	@Test
	public void testOnLoadMasterCartWithUnsortedChildCarts1() throws InterceptorException
	{
		final List<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>();
		childCarts.add(childCartYearly);
		childCarts.add(childCartQuarterly);
		childCarts.add(childCartMonthly);
		masterCart.setChildren(childCarts);

		orderLoadInterceptor.onLoad(masterCart, null);
		verifyChildCartSequence(masterCart);
	}

	@Test
	public void testOnLoadMasterCartWithUnsortedChildCarts2() throws InterceptorException
	{
		final List<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>();
		childCarts.add(childCartQuarterly);
		childCarts.add(childCartYearly);
		childCarts.add(childCartMonthly);
		masterCart.setChildren(childCarts);

		orderLoadInterceptor.onLoad(masterCart, null);
		verifyChildCartSequence(masterCart);
	}

	private void verifyChildCartSequence(final CartModel masterCart)
	{
		assertTrue("", CollectionUtils.isNotEmpty(masterCart.getChildren()));
		assertEquals("", 3, masterCart.getChildren().size());

		final ArrayList<AbstractOrderModel> childCarts = new ArrayList<AbstractOrderModel>(masterCart.getChildren());
		assertEquals("", 0, childCarts.indexOf(childCartMonthly));
		assertEquals("", 1, childCarts.indexOf(childCartQuarterly));
		assertEquals("", 2, childCarts.indexOf(childCartYearly));
	}

	private void setupCarts()
	{
		masterCart = new CartModel();
		final BillingTimeModel billingTimePayNow = new BillingTimeModel();
		billingTimePayNow.setCode("paynow");
		billingTimePayNow.setOrder(Integer.valueOf(0));
		masterCart.setBillingTime(billingTimePayNow);

		childCartMonthly = new CartModel();
		final BillingTimeModel billingTimeMonthly = new BillingTimeModel();
		billingTimeMonthly.setCode("monthly");
		billingTimeMonthly.setOrder(Integer.valueOf(1));
		childCartMonthly.setBillingTime(billingTimeMonthly);
		childCartMonthly.setParent(masterCart);
		childCartMonthly.setChildren(Collections.EMPTY_LIST);

		childCartQuarterly = new CartModel();
		final BillingTimeModel billingTimeQuarterly = new BillingTimeModel();
		billingTimeQuarterly.setCode("quarterly");
		billingTimeQuarterly.setOrder(Integer.valueOf(2));
		childCartQuarterly.setBillingTime(billingTimeQuarterly);
		childCartQuarterly.setParent(masterCart);
		childCartQuarterly.setChildren(Collections.EMPTY_LIST);

		childCartYearly = new CartModel();
		final BillingTimeModel billingTimeYearly = new BillingTimeModel();
		billingTimeYearly.setCode("yearly");
		billingTimeYearly.setOrder(Integer.valueOf(3));
		childCartYearly.setBillingTime(billingTimeYearly);
		childCartYearly.setParent(masterCart);
		childCartYearly.setChildren(Collections.EMPTY_LIST);
	}
}
