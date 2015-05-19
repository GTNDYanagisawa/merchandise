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
package de.hybris.platform.btg.rule;

import de.hybris.platform.basecommerce.enums.IntervalResolution;
import de.hybris.platform.btg.condition.impl.DateExpressionEvaluator;
import de.hybris.platform.btg.condition.impl.NumericExpressionEvaluator;
import de.hybris.platform.btg.condition.impl.PriceExpressionEvaluator;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGEachOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGOperandModel;
import de.hybris.platform.btg.model.BTGOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;

import java.util.Collections;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;




/**
 *
 */

public class BTGRuleEvaluationTest extends BTGIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(BTGRuleEvaluationTest.class);




	@Before
	public void prepare() throws Exception
	{
		LOG.info("Prepare rule evaluation test");
	}

	@Test
	public void testOrderRuleEvaluation() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");
		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(5, EUR))));
		modelService.save(rule);
		userService.setCurrentUser(customerA);

		Assert.assertFalse("Rule should evaluate false without any order", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(TYSKIE);
		Assert.assertTrue("Rule should evaluate true with an order of total:5", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(AUGISTINER);
		Assert.assertFalse("Rule should evaluate false with an order of total:10", ruleEvaluator
				.evaluate(customerB, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
	}

	@Test
	public void testEachOrderTotalRuleEvaluation() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");

		final BTGOperandModel leftOperand = createOrderOperand(BTGEachOrderTotalSumOperandModel.class, 2, "");
		final BTGOperandModel rightOperand = createPriceReferenceOperand(10, EUR);
		rule.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(PriceExpressionEvaluator.GREATER_THAN), rightOperand)));
		modelService.save(rule);
		userService.setCurrentUser(customerA);

		Assert.assertFalse("Rule should evaluate false without any order", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(TYSKIE, NONFULFILLING_PRODUCT_TEMPLATE + 3); //total 9

		Assert.assertFalse("Rule should evaluate false : only one order with total 9", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(AUGISTINER, NONFULFILLING_PRODUCT_TEMPLATE + 5); //total 11
		Assert.assertFalse("Rule should evaluate false : 2 last orders (9, 11)", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(AUGISTINER, NONFULFILLING_PRODUCT_TEMPLATE + 9, NONFULFILLING_PRODUCT_TEMPLATE + 0); //total 10,5
		Assert.assertTrue("Rule should evaluate true : 2 last orders (11, 10,5)", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

	}

	@Test
	public void testProductInOrderRuleEvaluation() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");
		//in 3 last orders
		final BTGOperandModel leftOperand = createProductsInOrdersOperand(3, "orderTestOperand");
		//these two products must be
		final BTGOperandModel rightOperand = createProductsOperand(TYSKIE, HACKERPSCHORR);
		rule.setConditions(Collections.singletonList(createExpression(leftOperand, createOperator("contains"), rightOperand)));
		modelService.save(rule);

		userService.setCurrentUser(customerB);
		Assert.assertFalse("Rule should evaluate false without any order", ruleEvaluator
				.evaluate(customerB, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(HACKERPSCHORR);
		Assert.assertFalse("Rule should evaluate FALSE with only HACKERPSCHORR ordered", ruleEvaluator
				.evaluate(customerB, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(TYSKIE);
		Assert.assertTrue("Rule should evaluate TRUE with HACKERPSCHORR and ZYWIEC ordered ", ruleEvaluator.evaluate(customerB,
				rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.OPTIMIZED,
						BTGResultScope.PERMANENT)));
		placeTestOrder(AUGISTINER);
		placeTestOrder(AUGISTINER);

		placeTestOrder(HACKERPSCHORR, TYSKIE);
		Assert.assertTrue("Rule should evaluate TRUE with HACKERPSCHORR and ZYWIEC ordered in one order", ruleEvaluator.evaluate(
				customerB, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.OPTIMIZED,
						BTGResultScope.PERMANENT)));

	}

	@Test
	public void testCategoriesInOrderRuleEvaluation() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");
		//in 3 last orders
		final BTGOperandModel leftOperand = createCategoriesinOrderOperand("testOPerand", false, 2);
		//these two products must be
		final BTGOperandModel rightOperand = createBTGReferenceCategoriesOperand(SOFTDRINKS_ONLINE, POLISHBEERS_ONLINE);
		rule.setConditions(Collections.singletonList(createExpression(leftOperand, createOperator("contains"), rightOperand)));
		modelService.save(rule);

		userService.setCurrentUser(customerC);

		Assert.assertFalse("Rule should evaluate false without any order", ruleEvaluator
				.evaluate(customerC, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(HACKERPSCHORR, AUGISTINER);//germanBeers
		placeTestOrder(TYSKIE);//traditionalBeers

		Assert.assertFalse("Rule should evaluate FALSE", ruleEvaluator.evaluate(customerC, rule, new BTGEvaluationContext(
				BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 9);//softdrink;
		Assert.assertTrue("Rule should evaluate TRUE", ruleEvaluator.evaluate(customerC, rule, new BTGEvaluationContext(
				BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

	}

	@Test
	public void testCategoriesWithSupercategoriesInOrderRuleEvaluation() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");
		//in 3 last orders
		final BTGOperandModel leftOperand = createCategoriesinOrderOperand("testOPerand", true, 2);
		//these two products must be
		final BTGOperandModel rightOperand = createBTGReferenceCategoriesOperand(SOFTDRINKS_ONLINE, ALCOHOLS_ONLINE);
		rule.setConditions(Collections.singletonList(createExpression(leftOperand, createOperator("contains"), rightOperand)));
		modelService.save(rule);

		userService.setCurrentUser(customerC);

		Assert.assertFalse("Rule should evaluate false without any order", ruleEvaluator
				.evaluate(customerC, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(HACKERPSCHORR, AUGISTINER);//alcoholes
		Assert.assertFalse("Rule should evaluate FALSE", ruleEvaluator.evaluate(customerC, rule, new BTGEvaluationContext(
				BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 9);//softdrink;
		Assert.assertTrue("Rule should evaluate TRUE", ruleEvaluator.evaluate(customerC, rule, new BTGEvaluationContext(
				BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
	}


	@Test
	public void testLastOrderDateRule() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");
		final BTGOperandModel leftOperand = createLastOrderDateOperand("lastOrderDate");
		final BTGOperandModel rightOperand = createReferenceDateOperand("reference date", IntervalResolution.MINUTE,
				Integer.valueOf(1));
		rule.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(DateExpressionEvaluator.GREATER_THAN), rightOperand)));
		modelService.save(rule);
		userService.setCurrentUser(customerA);
		Assert.assertFalse("rule should evaluate FALSE without any orders", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.OFFLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
		placeTestOrder(TYSKIE);
		Assert.assertTrue("rule should evaluate TRUE with last order placed before", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.OFFLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

	}

	@Test
	public void testOrderNumberRule() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");
		final BTGOperandModel leftOperand = createNumberOfOrdersOperand(null, null, "numberOfOrdersoperand");
		final BTGOperandModel rightOperand = createIntLiteralOperand(Integer.valueOf(2));
		rule.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(NumericExpressionEvaluator.EQUALS), rightOperand)));
		modelService.save(rule);

		userService.setCurrentUser(customerB);
		Assert.assertFalse("rule should evaluate FALSE without any orders", ruleEvaluator
				.evaluate(customerB, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.OFFLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

		placeTestOrder(AUGISTINER);
		Assert.assertFalse("rule should evaluate FALSE with only 1 order in history", ruleEvaluator
				.evaluate(customerB, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.OFFLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 5);
		Assert.assertTrue("rule should evaluate TRUE with only 2 order in history", ruleEvaluator
				.evaluate(customerB, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.OFFLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

	}

}
