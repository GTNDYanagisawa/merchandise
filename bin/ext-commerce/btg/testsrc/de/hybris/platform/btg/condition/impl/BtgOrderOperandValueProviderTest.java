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
package de.hybris.platform.btg.condition.impl;

import de.hybris.platform.basecommerce.enums.IntervalResolution;
import de.hybris.platform.btg.condition.operand.factory.OperandValueProviderRegistry;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGCategoriesInOrdersOperandModel;
import de.hybris.platform.btg.model.BTGEachOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGNumberOfOrdersOperandModel;
import de.hybris.platform.btg.model.BTGNumberOfOrdersRelativeDateOperandModel;
import de.hybris.platform.btg.model.BTGOrderTotalSumOperandModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.util.PriceValue;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class BtgOrderOperandValueProviderTest extends BTGIntegrationTest
{
	@Resource
	private OperandValueProviderRegistry operandValueProviderRegistry;



	@Test
	public void testNumberOfOrdersEvaluation() throws Exception
	{
		userService.setCurrentUser(customerA);
		final BTGNumberOfOrdersOperandModel numberOfOrdersOperand = createNumberOfOrdersOperand(
				BTGNumberOfOrdersOperandModel.class, "Test", DateUtils.addDays(new Date(), -1), DateUtils.addDays(new Date(), +1));
		Assert.assertEquals(
				"Test",
				Integer.valueOf(0),
				operandValueProviderRegistry.getOperandValueProvider(numberOfOrdersOperand.getClass()).getValue(
						numberOfOrdersOperand, customerA, null));

		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Test2",
				Integer.valueOf(1),
				operandValueProviderRegistry.getOperandValueProvider(numberOfOrdersOperand.getClass()).getValue(
						numberOfOrdersOperand, customerA, null));

		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Test3",
				Integer.valueOf(2),
				operandValueProviderRegistry.getOperandValueProvider(numberOfOrdersOperand.getClass()).getValue(
						numberOfOrdersOperand, customerA, null));

	}

	@Test
	public void testNumberOfOrdersRelativeDateEvaluation() throws Exception
	{
		userService.setCurrentUser(customerA);
		final BTGNumberOfOrdersRelativeDateOperandModel numberOfOrdersOperand = createNumberOfOrdersRelativeDateOperand(
				BTGNumberOfOrdersRelativeDateOperandModel.class, "Test", IntervalResolution.MINUTE, 1);
		Assert.assertEquals(
				"Test",
				Integer.valueOf(0),
				operandValueProviderRegistry.getOperandValueProvider(numberOfOrdersOperand.getClass()).getValue(
						numberOfOrdersOperand, customerA, null));

		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Test2",
				Integer.valueOf(1),
				operandValueProviderRegistry.getOperandValueProvider(numberOfOrdersOperand.getClass()).getValue(
						numberOfOrdersOperand, customerA, null));

		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Test3",
				Integer.valueOf(2),
				operandValueProviderRegistry.getOperandValueProvider(numberOfOrdersOperand.getClass()).getValue(
						numberOfOrdersOperand, customerA, null));

	}

	@Test
	public void testOrderTotalSumEvaluationFoxXOrders() throws Exception
	{
		userService.setCurrentUser(customerA);
		final BTGOrderTotalSumOperandModel orderSumOperand = createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, "");
		Assert.assertEquals(
				"Order total sum operand should evaluate to 0 without any orders in history",
				0,
				((PriceValue) operandValueProviderRegistry.getOperandValueProvider(orderSumOperand.getClass()).getValue(
						orderSumOperand, customerA, null)).getValue(), 00000.1);
		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Order total sum operand should evaluate to 5 after first",
				5,
				((PriceValue) operandValueProviderRegistry.getOperandValueProvider(orderSumOperand.getClass()).getValue(
						orderSumOperand, customerA, null)).getValue(), 00000.1);
		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Order total sum operand should evaluate to 10 after second",
				10,
				((PriceValue) operandValueProviderRegistry.getOperandValueProvider(orderSumOperand.getClass()).getValue(
						orderSumOperand, customerA, null)).getValue(), 00000.1);
		// only the last 2 orders are considered
		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Order total sum operand should evaluate to 10 after second",
				10,
				((PriceValue) operandValueProviderRegistry.getOperandValueProvider(orderSumOperand.getClass()).getValue(
						orderSumOperand, customerA, null)).getValue(), 00000.1);
	}

	@Test
	public void testOrderTotalSumEvaluationForAllOrders() throws Exception
	{
		userService.setCurrentUser(customerA);
		final BTGOrderTotalSumOperandModel orderSumOperand = createOrderOperand(BTGOrderTotalSumOperandModel.class, "");
		Assert.assertEquals(
				"Order total sum operand should evaluate to 0 without any orders in history",
				0,
				((PriceValue) operandValueProviderRegistry.getOperandValueProvider(orderSumOperand.getClass()).getValue(
						orderSumOperand, customerA, null)).getValue(), 00000.1);
		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Order total sum operand should evaluate to 5 after first",
				5,
				((PriceValue) operandValueProviderRegistry.getOperandValueProvider(orderSumOperand.getClass()).getValue(
						orderSumOperand, customerA, null)).getValue(), 00000.1);
		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Order total sum operand should evaluate to 10 after second",
				10,
				((PriceValue) operandValueProviderRegistry.getOperandValueProvider(orderSumOperand.getClass()).getValue(
						orderSumOperand, customerA, null)).getValue(), 00000.1);
		placeTestOrder(AUGISTINER);
		Assert.assertEquals(
				"Order total sum operand should evaluate to 10 after second",
				15,
				((PriceValue) operandValueProviderRegistry.getOperandValueProvider(orderSumOperand.getClass()).getValue(
						orderSumOperand, customerA, null)).getValue(), 00000.1);
	}

	@Test
	public void testEachOrderTotalSumEvaluation() throws Exception
	{
		userService.setCurrentUser(customerB);
		final BTGEachOrderTotalSumOperandModel eachorderSumOperand = createOrderOperand(BTGEachOrderTotalSumOperandModel.class, 2,
				"");
		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 0);
		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 0, NONFULFILLING_PRODUCT_TEMPLATE + 1);
		final Collection<PriceValue> result = (Collection<PriceValue>) operandValueProviderRegistry.getOperandValueProvider(
				eachorderSumOperand.getClass()).getValue(eachorderSumOperand, customerB, null);
		final Iterator<PriceValue> iter = result.iterator();
		final PriceValue price1 = iter.next();
		final PriceValue price2 = iter.next();
		Assert.assertNotNull("resulting value cannot be null", result);
		Assert.assertEquals("resulting value should have two Order Total values after two orders", 2, result.size());
		Assert.assertEquals("resulting values are not as expected", 4, price1.getValue() + price2.getValue(), 0.001);
	}


	@Test
	public void testCategoriesFromOrderEvaluation() throws Exception
	{
		userService.setCurrentUser(customerA);
		BTGCategoriesInOrdersOperandModel catOperand = createCategoriesinOrderOperand("catOPer1", true, 2);
		placeTestOrder(TYSKIE, HACKERPSCHORR);
		Collection<CategoryModel> result = (Collection<CategoryModel>) operandValueProviderRegistry.getOperandValueProvider(
				catOperand.getClass()).getValue(catOperand, customerA, null);

		de.hybris.platform.testframework.Assert.assertCollectionElements(result, categoryService.getCategory("PolishBeersOnline"),
				categoryService.getCategory("BeersOnline"), categoryService.getCategory("AlcoholsOnline"),
				categoryService.getCategory("GermanBeersOnline"));

		catOperand = createCategoriesinOrderOperand("catOPer2", false, 2);
		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 6);
		result = (Collection<CategoryModel>) operandValueProviderRegistry.getOperandValueProvider(catOperand.getClass()).getValue(
				catOperand, customerA, null);

		de.hybris.platform.testframework.Assert.assertCollectionElements(result, categoryService.getCategory("PolishBeersOnline"),
				categoryService.getCategory("GermanBeersOnline"), categoryService.getCategory("SoftDrinksOnline"));
	}


	private <T extends BTGNumberOfOrdersOperandModel> T createNumberOfOrdersOperand(final Class<T> clazz, final String name,
			final Date fromDate, final Date toDate)
	{
		final T operand = (T) modelService.create(clazz);
		operand.setCode(name);
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());

		operand.setFrom(fromDate);
		operand.setTo(toDate);

		modelService.save(operand);
		return operand;
	}


	private <T extends BTGNumberOfOrdersRelativeDateOperandModel> T createNumberOfOrdersRelativeDateOperand(final Class<T> clazz,
			final String name, final IntervalResolution interval, final int value)
	{
		final T operand = (T) modelService.create(clazz);
		operand.setCode(name);
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());

		operand.setUnit(interval);
		operand.setValue(Integer.valueOf(value));

		modelService.save(operand);
		return operand;
	}
}