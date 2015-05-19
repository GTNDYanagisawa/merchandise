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
package de.hybris.platform.btg;

import de.hybris.platform.btg.condition.impl.DefaultBTGExpressionEvaluator;
import de.hybris.platform.btg.condition.impl.PriceExpressionEvaluator;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGExpressionModel;
import de.hybris.platform.btg.model.BTGIntegerLiteralOperandModel;
import de.hybris.platform.btg.model.BTGOperandModel;
import de.hybris.platform.btg.model.BTGOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.model.BTGSegmentModel;
import de.hybris.platform.btg.rule.impl.DefaultRuleEvaluator;
import de.hybris.platform.btg.segment.impl.DefaultSegmentEvaluator;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;

import java.util.Collections;
import java.util.UUID;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class StoreFirstNegativeResultTest extends BTGIntegrationTest
{

	@Resource
	private DefaultSegmentEvaluator defaultSegmentEvaluator;
	@Resource
	private DefaultRuleEvaluator defaultRuleEvaluator;
	@Resource
	private DefaultBTGExpressionEvaluator btgExpressionEvaluator;

	private BTGSegmentModel segment;
	private BTGRuleModel rule;
	private BTGExpressionModel expression;

	private boolean defaultSegmentEvaluatorDefVal;
	private boolean defaultRuleEvaluatorDefVal;
	private boolean btgExpressionEvaluatorDefVal;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		defaultSegmentEvaluatorDefVal = defaultSegmentEvaluator.isStoreFirstNegativeResult();
		defaultRuleEvaluatorDefVal = defaultRuleEvaluator.isStoreFirstNegativeResult();
		btgExpressionEvaluatorDefVal = btgExpressionEvaluator.isStoreFirstNegativeResult();
		setCustomersAndUserGroups();
		setCatalogVersions();
		segment = createSegment();
		rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(5, EUR))));
		userService.setCurrentUser(customerA);

		expression = createExpression(createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createLiteral());
		expression.setRule(rule);
		modelService.save(expression);
		modelService.save(segment);
	}

	@After
	public void cleanup()
	{
		defaultSegmentEvaluator.setStoreFirstNegativeResult(defaultSegmentEvaluatorDefVal);
		defaultRuleEvaluator.setStoreFirstNegativeResult(defaultRuleEvaluatorDefVal);
		btgExpressionEvaluator.setStoreFirstNegativeResult(btgExpressionEvaluatorDefVal);
	}

	@Test
	public void testSegmentEvaluatorStore()
	{
		defaultRuleEvaluator.setStoreFirstNegativeResult(true);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGResultScope.PERMANENT);
		defaultSegmentEvaluator.setSegmentResult(false, customerA, segment, false, context, context.getJaloSessionId(), false);
		Assertions.assertThat(defaultSegmentEvaluator.checkSegmentForUser(customerA, segment, context)).isNotNull();
	}

	@Test
	public void testSegmentEvaluatorNotStore()
	{
		defaultSegmentEvaluator.setStoreFirstNegativeResult(false);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGResultScope.PERMANENT);
		defaultSegmentEvaluator.setSegmentResult(false, customerA, segment, false, context, context.getJaloSessionId(), false);
		Assertions.assertThat(defaultSegmentEvaluator.checkSegmentForUser(customerA, segment, context)).isNull();

		defaultSegmentEvaluator.setSegmentResult(true, customerA, segment, false, context, context.getJaloSessionId(), false);
		Assertions.assertThat(defaultSegmentEvaluator.checkSegmentForUser(customerA, segment, context)).isNotNull();
	}

	@Test
	public void testRuleEvaluatorStore()
	{
		defaultRuleEvaluator.setStoreFirstNegativeResult(true);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGResultScope.PERMANENT);
		defaultRuleEvaluator.setRuleResult(customerA, rule, BTGResultScope.PERMANENT, false, context.getJaloSessionId(), false);
		Assertions.assertThat(defaultRuleEvaluator.checkRuleForUser(customerA, rule, context)).isNotNull();
	}

	@Test
	public void testRuleEvaluatorNotStore()
	{
		defaultRuleEvaluator.setStoreFirstNegativeResult(false);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGResultScope.PERMANENT);
		defaultRuleEvaluator.setRuleResult(customerA, rule, BTGResultScope.PERMANENT, false, context.getJaloSessionId(), false);
		Assertions.assertThat(defaultRuleEvaluator.checkRuleForUser(customerA, rule, context)).isNull();
		defaultRuleEvaluator.setRuleResult(customerA, rule, BTGResultScope.PERMANENT, true, context.getJaloSessionId(), false);
		Assertions.assertThat(defaultRuleEvaluator.checkRuleForUser(customerA, rule, context)).isNotNull();
	}


	@Test
	public void testExpresionEvaluatorStore()
	{
		btgExpressionEvaluator.setStoreFirstNegativeResult(true);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGResultScope.PERMANENT);
		btgExpressionEvaluator.setConditionResult(customerA, expression, BTGResultScope.PERMANENT, false,
				context.getJaloSessionId(), false);
		Assertions.assertThat(btgExpressionEvaluator.checkConditionForUser(customerA, expression, context)).isNotNull();
	}

	@Test
	public void testExpresionEvaluatorNotStore()
	{
		btgExpressionEvaluator.setStoreFirstNegativeResult(false);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGResultScope.PERMANENT);
		btgExpressionEvaluator.setConditionResult(customerA, expression, BTGResultScope.PERMANENT, false,
				context.getJaloSessionId(), false);
		Assertions.assertThat(btgExpressionEvaluator.checkConditionForUser(customerA, expression, context)).isNull();
		btgExpressionEvaluator.setConditionResult(customerA, expression, BTGResultScope.PERMANENT, true,
				context.getJaloSessionId(), false);
		Assertions.assertThat(btgExpressionEvaluator.checkConditionForUser(customerA, expression, context)).isNotNull();
	}


	private BTGExpressionModel createExpression(final BTGOperandModel left, final BTGOperandModel right)
	{
		final BTGExpressionModel expression = new BTGExpressionModel();
		expression.setLeftOperand(left);
		expression.setRightOperand(right);
		expression.setCatalogVersion(online);
		expression.setUid(UUID.randomUUID().toString());
		return expression;
	}

	private BTGIntegerLiteralOperandModel createLiteral()
	{
		final BTGIntegerLiteralOperandModel literal = new BTGIntegerLiteralOperandModel();
		literal.setCatalogVersion(online);
		literal.setLiteral(Integer.valueOf(1));
		literal.setUid(UUID.randomUUID().toString());
		modelService.save(literal);
		return literal;
	}

}
