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
package de.hybris.platform.orderhandler;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.servicelayer.type.TypeService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Test class for Order Status Display dynamic attribute
 * 
 * @author Yannick Robin
 */
public class DynamicAttributesOrderStatusDisplayTest extends ServicelayerTest
{

	private OrderModel orderModel;

	@Resource
	private ModelService modelService;

	@Resource
	private TypeService typeService;

	@Resource
	private DynamicAttributeHandler<String, OrderModel> dynamicAttributesOrderStatusDisplayByMap;

	@Resource
	private DynamicAttributeHandler<String, OrderModel> dynamicAttributesOrderStatusDisplayByEnum;

	@Before
	public void setUp()
	{
		orderModel = modelService.create(OrderModel.class);

		//Add localization for CREATED status (missing for 'junit' tenant)
		final EnumerationValueModel evmCreated = typeService.getEnumerationValue(OrderStatus.CREATED);
		evmCreated.setName("Created");
		//Add localization for COMPLETED status (missing for 'junit' tenant)
		final EnumerationValueModel evmCompleted = typeService.getEnumerationValue(OrderStatus.COMPLETED);
		evmCompleted.setName("Completed");
	}


	@Test
	public void testDynamicAttributeHandlerByMap()
	{
		orderModel.setStatus(OrderStatus.CREATED);
		final String statusCreated = dynamicAttributesOrderStatusDisplayByMap.get(orderModel);
		assertEquals("myCustomCreated status expected!", "myCustomCreated", statusCreated);

		orderModel.setStatus(OrderStatus.COMPLETED);
		final String statusCompleted = dynamicAttributesOrderStatusDisplayByMap.get(orderModel);
		assertEquals("mysCustomCompleted status expected!", "myCustomCompleted", statusCompleted);
	}

	@Test
	public void testDynamicAttributeHandlerByEnum()
	{
		orderModel.setStatus(OrderStatus.CREATED);
		final String statusCreated = dynamicAttributesOrderStatusDisplayByEnum.get(orderModel);
		assertEquals("Created status expected!", "Created", statusCreated);

		orderModel.setStatus(OrderStatus.COMPLETED);
		final String statusCompleted = dynamicAttributesOrderStatusDisplayByEnum.get(orderModel);
		assertEquals("Completed status expected!", "Completed", statusCompleted);
	}
}
