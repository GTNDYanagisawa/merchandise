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
package de.hybris.platform.btg.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.btg.condition.impl.DateExpressionEvaluator;
import de.hybris.platform.btg.condition.impl.NumericExpressionEvaluator;
import de.hybris.platform.btg.condition.impl.PriceExpressionEvaluator;
import de.hybris.platform.btg.enums.BTGRuleType;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGCartTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGEachOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGIntegerLiteralOperandModel;
import de.hybris.platform.btg.model.BTGLastOrderDateOperandModel;
import de.hybris.platform.btg.model.BTGNumberOfOrdersOperandModel;
import de.hybris.platform.btg.model.BTGOperandModel;
import de.hybris.platform.btg.model.BTGOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGProductsInCartOperandModel;
import de.hybris.platform.btg.model.BTGQuantityOfProductInCartOperandModel;
import de.hybris.platform.btg.model.BTGReferenceCategoriesOperandModel;
import de.hybris.platform.btg.model.BTGReferenceDateOperandModel;
import de.hybris.platform.btg.model.BTGReferencePriceOperandModel;
import de.hybris.platform.btg.model.BTGReferenceProductsOperandModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.model.BTGStringLiteralOperandModel;
import de.hybris.platform.btg.model.BTGUserAddressPostalCodeOperandModel;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * @author arkadiusz.balwierz
 * 
 */
public class ExpressionServiceTest extends BTGIntegrationTest
{
	@Resource
	ExpressionService expressionService;

	@Test
	public void getAvalibleOrderLeftOperandsTest()
	{
		final BTGRuleModel rule = createRule("a rule");
		rule.setRuleType(BTGRuleType.ORDER);

		final Collection<Class<? extends BTGOperandModel>> result = expressionService.getAvailableLeftOperandTypes(rule
				.getRuleType());

		assertFalse("resuld sould not be empty", result.isEmpty());

		assertFalse("BTGCartTotalSumOperandModel should not be avalible", result.contains(BTGCartTotalSumOperandModel.class));
		assertTrue("BTGOrderTotalSumOperandModel should be avalible", result.contains(BTGOrderTotalSumOperandModel.class));
		assertFalse("BTGUserAddressPostalCodeOperandModel should not be avalible", result
				.contains(BTGUserAddressPostalCodeOperandModel.class));
	}

	@Test
	public void getAvalibleCartLeftOperandsTest()
	{

		final BTGRuleModel rule = createRule("a rule");
		rule.setRuleType(BTGRuleType.CART);

		Collection<Class<? extends BTGOperandModel>> result = expressionService.getAvailableLeftOperandTypes(rule.getRuleType());

		result = expressionService.getAvailableLeftOperandTypes(rule.getRuleType());
		assertFalse("resuld sould not be empty", result.isEmpty());

		assertTrue("BTGCartTotalSumOperandModel should be avalible", result.contains(BTGCartTotalSumOperandModel.class));
		assertFalse("BTGOrderTotalSumOperandModel should not be avalible", result.contains(BTGOrderTotalSumOperandModel.class));
		assertFalse("BTGUserAddressPostalCodeOperandModel should not be avalible", result
				.contains(BTGUserAddressPostalCodeOperandModel.class));

	}

	@Test
	public void getAvalibleUserLeftOperandsTest()
	{
		final BTGRuleModel rule = createRule("a rule");
		rule.setRuleType(BTGRuleType.USER);

		final Collection<Class<? extends BTGOperandModel>> result = expressionService.getAvailableLeftOperandTypes(rule
				.getRuleType());

		assertFalse("resuld sould not be empty", result.isEmpty());

		assertFalse("BTGCartTotalSumOperandModel should not be avalible", result.contains(BTGCartTotalSumOperandModel.class));
		assertFalse("BTGOrderTotalSumOperandModel should not be avalible", result.contains(BTGOrderTotalSumOperandModel.class));
		assertTrue("BTGUserAddressPostalCodeOperandModel should be avalible", result
				.contains(BTGUserAddressPostalCodeOperandModel.class));
	}



	@Test
	public void getAvailableNumericOperatorsTest() throws Exception
	{
		final Collection<String> operators = expressionService.getAvailableOperators(new BTGCartTotalSumOperandModel());

		assertTrue("== should be in", operators.contains(PriceExpressionEvaluator.EQUALS));
		assertTrue("<= should be in", operators.contains(PriceExpressionEvaluator.LESS_OR_EQUALS));
		assertTrue(">= should be in", operators.contains(PriceExpressionEvaluator.GREATER_OR_EQUALS));
		assertTrue("< should be in", operators.contains(PriceExpressionEvaluator.LESS_THAN));
		assertTrue("> should be in", operators.contains(PriceExpressionEvaluator.GREATER_THAN));
	}


	@Test
	public void getAvailableDateOperatorsTest() throws Exception
	{
		final Collection<String> operators = expressionService.getAvailableOperators(new BTGLastOrderDateOperandModel());

		assertTrue("after should be in", operators.contains(DateExpressionEvaluator.GREATER_THAN));
		assertTrue("not after should be in", operators.contains(DateExpressionEvaluator.LESS_OR_EQUALS));
		assertTrue("equals should be in", operators.contains(DateExpressionEvaluator.EQUALS));
		assertTrue("before should be in", operators.contains(DateExpressionEvaluator.LESS_THAN));
		assertTrue("not before should be in", operators.contains(DateExpressionEvaluator.GREATER_OR_EQUALS));
		assertTrue("different than should be in", operators.contains(DateExpressionEvaluator.NOT_EQUALS));
	}


	@Test
	public void getAvailableProductOperatorsTest() throws Exception
	{
		final Collection<String> operators = expressionService.getAvailableOperators(new BTGProductsInCartOperandModel());

		assertTrue("contains should be in", operators.contains("contains"));
		assertTrue("containsAny should be in", operators.contains("containsAny"));
		assertTrue("size should be in", operators.contains("size"));
		assertTrue("notContains should be in", operators.contains("notContains"));

	}



	@Test
	public void getAvailableStringOperatorsTest() throws Exception
	{
		final BTGUserAddressPostalCodeOperandModel operand = new BTGUserAddressPostalCodeOperandModel();

		final Collection<String> operators = expressionService.getAvailableOperators(operand);

		assertFalse("isEmpty should not be in", operators.contains("isEmpty"));
		assertFalse("equals should not be in", operators.contains("equals"));
		assertFalse("== should not be in", operators.contains("=="));
		assertFalse("contains should not be in", operators.contains("contains"));

		assertTrue("'containsAny' should be in", operators.contains("containsAny"));
		assertTrue("'notContains' should be in", operators.contains("notContains"));
		assertTrue("'startsWith' should be in", operators.contains("startsWith"));
		assertTrue("'startsNotWith' should be in", operators.contains("startsNotWith"));
	}

	@Test
	public void getAvailableNumericRightOperandsTest() throws Exception
	{
		final Collection<Class<? extends BTGOperandModel>> operands = expressionService.getAvailableRightOperands(
				new BTGNumberOfOrdersOperandModel(), "==");

		assertTrue("BTGDoubleLiteralOperandModel should be in", operands.contains(BTGIntegerLiteralOperandModel.class));
	}

	@Test
	public void getAvailablePriceRightOperandsTest() throws Exception
	{
		final Collection<Class<? extends BTGOperandModel>> operands = expressionService.getAvailableRightOperands(
				new BTGCartTotalSumOperandModel(), PriceExpressionEvaluator.EQUALS);

		assertTrue("BTGDoubleLiteralOperandModel should be in", operands.contains(BTGReferencePriceOperandModel.class));
	}

	@Test
	public void getAvailableProductQuantityRightOperandsTest() throws Exception
	{
		final Collection<Class<? extends BTGOperandModel>> operands = expressionService.getAvailableRightOperands(
				new BTGQuantityOfProductInCartOperandModel(), NumericExpressionEvaluator.EQUALS);

		assertTrue("BTGIntegerLiteralOperandModel should be in", operands.contains(BTGIntegerLiteralOperandModel.class));
	}

	@Test
	public void getAvailableEachPriceRightOperandsTest() throws Exception
	{
		final Collection<Class<? extends BTGOperandModel>> operands = expressionService.getAvailableRightOperands(
				new BTGEachOrderTotalSumOperandModel(), PriceExpressionEvaluator.EQUALS);

		assertTrue("BTGReferencePriceOperandModel should be in", operands.contains(BTGReferencePriceOperandModel.class));
	}

	@Test
	public void getAvailableStringOperandsTest() throws Exception
	{
		final BTGUserAddressPostalCodeOperandModel operand = new BTGUserAddressPostalCodeOperandModel();
		final Collection<Class<? extends BTGOperandModel>> operands = expressionService.getAvailableRightOperands(operand,
				"startsWith");

		assertTrue("BTGStringLiteralOperandModel should be in", operands.contains(BTGStringLiteralOperandModel.class));
	}

	@Test
	public void getAvailableProductRightOperandsTest() throws Exception
	{
		Collection<Class<? extends BTGOperandModel>> operands = expressionService.getAvailableRightOperands(
				new BTGProductsInCartOperandModel(), "contains");

		assertFalse("BTGReferenceCategoriesOperandModel should not be in", operands
				.contains(BTGReferenceCategoriesOperandModel.class));
		assertTrue("BTGReferenceProductsOperandModel should be in", operands.contains(BTGReferenceProductsOperandModel.class));

		operands = expressionService.getAvailableRightOperands(new BTGProductsInCartOperandModel(), "size");
		assertTrue("BTGIntegerLiteralOperandModel should be in", operands.contains(BTGIntegerLiteralOperandModel.class));

		operands = expressionService.getAvailableRightOperands(new BTGProductsInCartOperandModel(), "isEmpty");
		assertTrue("No operands for isEmpty", operands.isEmpty());
	}

	@Test
	public void getAvailableDateRightOperandsTest() throws Exception
	{
		final Collection<Class<? extends BTGOperandModel>> operands = expressionService.getAvailableRightOperands(
				new BTGLastOrderDateOperandModel(), DateExpressionEvaluator.GREATER_THAN);

		assertTrue("BTGReferenceDateOperandModel should be in", operands.contains(BTGReferenceDateOperandModel.class));
	}
}
