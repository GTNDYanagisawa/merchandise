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

import de.hybris.platform.btg.condition.impl.BooleanExpressionEvaluator;
import de.hybris.platform.btg.condition.impl.NumericExpressionEvaluator;
import de.hybris.platform.btg.condition.impl.PriceExpressionEvaluator;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGCartIsEmptyOperandModel;
import de.hybris.platform.btg.model.BTGCartTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGCategoriesInCartOperandModel;
import de.hybris.platform.btg.model.BTGOperandModel;
import de.hybris.platform.btg.model.BTGProductsInCartOperandModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;

import java.util.Collections;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class BTGCartRuleEvaluationTest extends BTGIntegrationTest
{
	@Resource
	private RuleEvaluator ruleEvaluator;

	@Test
	public void testCartTotalValueRuleUSD() throws Exception
	{
		i18nService.setCurrentCurrency(usd);

		userService.setCurrentUser(customerA);

		final BTGRuleModel rule = createRule("testRule");
		final BTGOperandModel leftOperand = createOperandModel("carttotaloperand", BTGCartTotalSumOperandModel.class);
		final BTGOperandModel rightOperand = createPriceReferenceOperand(10, EUR);
		rule.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(PriceExpressionEvaluator.GREATER_THAN), rightOperand)));
		modelService.save(rule);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		Assert.assertFalse("Rule should evaluate False without any item in cart", ruleEvaluator.evaluate(customerA, rule, context));
		addToCart(TYSKIE);
		Assert.assertFalse("Rule should evaluate False with a cart of total 5", ruleEvaluator.evaluate(customerA, rule, context));

		addToCart(NONFULFILLING_PRODUCT_TEMPLATE + 5);
		Assert.assertTrue("Rule should evaluate TRUE with a cart of total 11", ruleEvaluator.evaluate(customerA, rule, context));
	}

	@Test
	public void testCartTotalValueRuleEUR() throws Exception
	{
		i18nService.setCurrentCurrency(eur);

		userService.setCurrentUser(customerA);

		final BTGRuleModel rule = createRule("testRule");
		final BTGOperandModel leftOperand = createOperandModel("carttotaloperand", BTGCartTotalSumOperandModel.class);
		final BTGOperandModel rightOperand = createPriceReferenceOperand(13.8, USD);
		rule.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(PriceExpressionEvaluator.GREATER_THAN), rightOperand)));
		modelService.save(rule);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		Assert.assertFalse("Rule should evaluate False without any item in cart", ruleEvaluator.evaluate(customerA, rule, context));
		addToCart(TYSKIE);
		Assert.assertFalse("Rule should evaluate False with a cart of total 5", ruleEvaluator.evaluate(customerA, rule, context));

		addToCart(NONFULFILLING_PRODUCT_TEMPLATE + 5);
		Assert.assertTrue("Rule should evaluate TRUE with a cart of total 11", ruleEvaluator.evaluate(customerA, rule, context));
	}

	@Test
	public void testCartProducts() throws Exception
	{
		final BTGRuleModel ruleContainsAll = createRule("testRuleContainsAll");
		final BTGRuleModel ruleContainsAny = createRule("testRuleContainsAny");

		final BTGOperandModel leftOperand = createOperandModel("cart products", BTGProductsInCartOperandModel.class);
		final BTGOperandModel rightOperand = createProductsOperand(AUGISTINER, TYSKIE);
		ruleContainsAll.setConditions(Collections.singletonList(createExpression(leftOperand, createOperator("contains"),
				rightOperand)));
		ruleContainsAny.setConditions(Collections.singletonList(createExpression(leftOperand, createOperator("containsAny"),
				rightOperand)));
		modelService.save(ruleContainsAll);
		modelService.save(ruleContainsAny);
		userService.setCurrentUser(customerC);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		Assert.assertFalse("Rule should evaluate FALSE: cart empty", ruleEvaluator.evaluate(customerC, ruleContainsAll, context));
		Assert.assertFalse("Rule should evaluate FALSE: cart empty", ruleEvaluator.evaluate(customerC, ruleContainsAny, context));

		addToCart(AUGISTINER, NONFULFILLING_PRODUCT_TEMPLATE + 2);
		Assert.assertFalse("Rule should evaluate FALSE: AUGISTINER is in the cart, but no TYSKIE",
				ruleEvaluator.evaluate(customerB, ruleContainsAll, context));
		Assert.assertTrue("Rule should evaluate TRUE: AUGISTINER is in the cart",
				ruleEvaluator.evaluate(customerB, ruleContainsAny, context));

		addToCart(TYSKIE);
		Assert.assertTrue("Rule should evaluate FALSE: AUGISTINER and TYSKIE are in the cart",
				ruleEvaluator.evaluate(customerB, ruleContainsAll, context));


	}

	@Test
	public void testCartCategories() throws Exception
	{
		final BTGOperandModel leftOperand = createOperandModel("cart categories", BTGCategoriesInCartOperandModel.class);
		final BTGOperandModel rightOperand = createBTGReferenceCategoriesOperand(POLISHBEERS_ONLINE);

		final BTGRuleModel ruleContains = createRule("testRuleContains");
		final BTGRuleModel ruleNotContains = createRule("testRuleNotContains");

		ruleContains.setConditions(Collections
				.singletonList(createExpression(leftOperand, createOperator("contains"), rightOperand)));
		ruleNotContains.setConditions(Collections.singletonList(createExpression(leftOperand, createOperator("notContains"),
				rightOperand)));
		modelService.save(ruleContains);
		modelService.save(ruleNotContains);
		userService.setCurrentUser(customerB);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		Assert.assertFalse("Rule should evaluate FALSE: cart empty", ruleEvaluator.evaluate(customerB, ruleContains, context));
		Assert.assertTrue("Rule should evaluate TRUE :empty cart", ruleEvaluator.evaluate(customerB, ruleNotContains, context));
		addToCart(TYSKIE);
		Assert.assertTrue("Rule should evaluate TRUE: POLISH BEER is in the cart",
				ruleEvaluator.evaluate(customerC, ruleContains, context));
		Assert.assertFalse("Rule should evaluate FALSE: POLISH BEER is in the cart",
				ruleEvaluator.evaluate(customerC, ruleNotContains, context));
	}

	@Test
	public void testCartCategoriesWithSuperCategories() throws Exception
	{
		final BTGOperandModel leftOperandWithSup = createCategoriesInCartOperandModel("cart categories", true);
		final BTGOperandModel rightOperand = createBTGReferenceCategoriesOperand(ALCOHOLS_ONLINE);

		final BTGRuleModel ruleContainsWithSup = createRule("ruleContainsWithSup");

		ruleContainsWithSup.setConditions(Collections.singletonList(createExpression(leftOperandWithSup,
				createOperator("contains"), rightOperand)));
		modelService.save(ruleContainsWithSup);

		userService.setCurrentUser(customerB);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		Assert.assertFalse("Rule should evaluate FALSE: cart empty",
				ruleEvaluator.evaluate(customerB, ruleContainsWithSup, context));
		addToCart(TYSKIE);
		Assert.assertTrue("Rule should evaluate TRUE: Super category matches.",
				ruleEvaluator.evaluate(customerC, ruleContainsWithSup, context));
	}

	@Test
	public void testCartCategoriesWithoutSuperCategories() throws Exception
	{
		final BTGOperandModel leftOperandWithoutSup = createCategoriesInCartOperandModel("cart categories", false);
		final BTGOperandModel rightOperand = createBTGReferenceCategoriesOperand(ALCOHOLS_ONLINE);

		final BTGRuleModel ruleContainsWithoutSup = createRule("ruleContainsWithoutSup");

		ruleContainsWithoutSup.setConditions(Collections.singletonList(createExpression(leftOperandWithoutSup,
				createOperator("contains"), rightOperand)));
		modelService.save(ruleContainsWithoutSup);

		userService.setCurrentUser(customerB);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		Assert.assertFalse("Rule should evaluate FALSE :empty cart",
				ruleEvaluator.evaluate(customerB, ruleContainsWithoutSup, context));
		addToCart(TYSKIE);
		Assert.assertFalse("Rule should evaluate FALSE: No super category matches.",
				ruleEvaluator.evaluate(customerC, ruleContainsWithoutSup, context));
	}

	@Test
	public void testCartProductQuantity() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");
		final BTGOperandModel leftOperand = createQuantityOfProductInCartOperand("cart product quantity", HACKERPSCHORR);
		final BTGOperandModel rightOperand = createIntLiteralOperand(Integer.valueOf(2));
		userService.setCurrentUser(customerA);
		rule.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(NumericExpressionEvaluator.EQUALS), rightOperand)));
		modelService.save(rule);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		Assert.assertFalse("Rule should evaluate FALSE: cart empty", ruleEvaluator.evaluate(customerA, rule, context));
		addToCart(TYSKIE);
		Assert.assertFalse("Rule should evaluate FALSE: no HACKERPSCHORR in the cart",
				ruleEvaluator.evaluate(customerA, rule, context));
		addToCart(HACKERPSCHORR);

		Assert.assertFalse("Rule should evaluate FALSE: only one HACKERPSCHORR in the cart",
				ruleEvaluator.evaluate(customerA, rule, context));
		addToCart(HACKERPSCHORR);
		Assert.assertTrue("Rule should evaluate TRUE: with 2 HACKERPSCHORR in the cart",
				ruleEvaluator.evaluate(customerA, rule, context));
		addToCart(HACKERPSCHORR);
		Assert.assertTrue("Rule should evaluate TRUE: it evaluated TRUE for the user before",
				ruleEvaluator.evaluate(customerA, rule, context));
	}

	@Test
	public void testCartProductQuantityWithStaged() throws Exception
	{
		final BTGRuleModel rule = createRule("testRule");
		final BTGOperandModel leftOperand = createQuantityOfProductInCartOperand("cart product quantity", STAGED, KROSTITZER);
		final BTGOperandModel rightOperand = createIntLiteralOperand(Integer.valueOf(2));
		userService.setCurrentUser(customerA);
		rule.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(NumericExpressionEvaluator.EQUALS), rightOperand)));
		modelService.save(rule);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		Assert.assertFalse("Rule should evaluate FALSE: cart empty", ruleEvaluator.evaluate(customerA, rule, context));

		addToCartWithCatalog(ONLINE, TYSKIE);
		Assert.assertFalse(ruleEvaluator.evaluate(customerA, rule, context));

		addToCartWithCatalog(ONLINE, KROSTITZER);
		Assert.assertFalse(ruleEvaluator.evaluate(customerA, rule, context));

		addToCartWithCatalog(ONLINE, KROSTITZER);
		Assert.assertTrue(ruleEvaluator.evaluate(customerA, rule, context));
	}

	@Test
	public void testCartIsEmpty() throws Exception
	{
		userService.setCurrentUser(customerA);

		final BTGOperandModel leftOperand = createCartIsEmptyOperand("cart is empty");
		final BTGOperandModel rightOperand = createBooleanLiteralOperand(true);

		final BTGRuleModel rule1 = createRule("testRule");
		rule1.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(BooleanExpressionEvaluator.EQUALS), rightOperand)));

		modelService.save(rule1);

		Assert.assertTrue("Rule should evaluate TRUE: cart empty", ruleEvaluator
				.evaluate(customerA, rule1, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

		addToCart(TYSKIE);

		// positive results of rules are cached
		final BTGRuleModel rule2 = createRule("testRule");
		rule2.setConditions(Collections.singletonList(createExpression(leftOperand,
				createOperator(BooleanExpressionEvaluator.EQUALS), rightOperand)));

		modelService.save(rule2);

		Assert.assertFalse("Rule should evaluate FALSE: cart is not empty", ruleEvaluator
				.evaluate(customerA, rule2, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
	}

	protected BTGCategoriesInCartOperandModel createCategoriesInCartOperandModel(final String code,
			final boolean withSuperCategories)
	{
		final BTGCategoriesInCartOperandModel operand = modelService.create(BTGCategoriesInCartOperandModel.class);
		operand.setCode(code);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		operand.setScope(BTGConditionEvaluationScope.ONLINE);
		operand.setWithSupercategories(Boolean.valueOf(withSuperCategories));
		modelService.save(operand);
		return operand;
	}

	protected BTGCartIsEmptyOperandModel createCartIsEmptyOperand(final String code)
	{
		final BTGCartIsEmptyOperandModel operand = modelService.create(BTGCartIsEmptyOperandModel.class);
		operand.setCode(code);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		operand.setScope(BTGConditionEvaluationScope.ONLINE);
		modelService.save(operand);
		return operand;
	}
}
