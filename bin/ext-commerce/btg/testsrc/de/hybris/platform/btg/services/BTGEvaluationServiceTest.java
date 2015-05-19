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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.btg.condition.impl.PriceExpressionEvaluator;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGConditionModel;
import de.hybris.platform.btg.model.BTGConditionResultModel;
import de.hybris.platform.btg.model.BTGOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.model.BTGRuleResultModel;
import de.hybris.platform.btg.model.BTGSegmentModel;
import de.hybris.platform.btg.model.BTGSegmentResultModel;
import de.hybris.platform.btg.segment.SegmentEvaluationException;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Test;


public class BTGEvaluationServiceTest extends BTGIntegrationTest
{
	/**
	 * 
	 */
	private static final BTGEvaluationContext PERISTENT_RESULT_SCOPE_CONTEXT = new BTGEvaluationContext(BTGResultScope.PERMANENT);

	@Resource
	private BTGEvaluationService btgEvaluationService;

	@Resource
	private ModelService modelService;

	@Resource
	private SessionService sessionService;

	@Test
	public void testEvaluateAllSegmentsTest() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(5, EUR))));
		userService.setCurrentUser(customerA);
		modelService.save(segment);
		placeTestOrder(TYSKIE);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		btgEvaluationService.evaluateAllSegments(customerA, Collections.EMPTY_LIST, context);


		assertTrue("Segment do not contain user customerA", btgResultService.isFulfilled(customerA, segment, context));

		final Collection<BTGRuleModel> rules = segment.getRules();
		for (final BTGRuleModel eachRule : rules)
		{
			assertTrue("Rule not fulfiled " + eachRule.getCode(), btgResultService.isFulfilled(customerA, eachRule, context));
			final Collection<BTGConditionModel> conditions = eachRule.getConditions();
			for (final BTGConditionModel eachCondition : conditions)
			{
				assertTrue("Condition not fulfiled " + eachCondition.getUid(),
						btgResultService.isFulfilled(customerA, eachRule, context));
			}
		}

	}


	@Test
	public void testEvaluateAllSegmentsInSessionResultScopeTest() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		segment.setDefaultResultScope(BTGResultScope.PERMANENT);
		modelService.save(segment);
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(5, EUR))));
		userService.setCurrentUser(customerA);
		modelService.save(segment);
		placeTestOrder(TYSKIE);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.SESSION);
		btgEvaluationService.evaluateAllSegments(customerA, Collections.EMPTY_LIST, context);


		assertSegmentResults(customerA, segment, true, context, BTGResultScope.SESSION);
		assertSegmentResults(customerA, segment, false, new BTGEvaluationContext(BTGResultScope.PERMANENT), null);

		sessionService.createNewSession();

		assertSegmentResults(customerA, segment, false, context, null);


	}


	@Test
	public void testEvaluateAllSegmentsForAnonymous() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);

		evaluateAnonymousSegment(segment, context);

		sessionService.createNewSession();

		assertSegmentResults(anonymous, segment, false, context, null);


	}


	/**
	 * 
	 */
	protected void evaluateAnonymousSegment(final BTGSegmentModel segment, final BTGEvaluationContext context) throws Exception,
			SegmentEvaluationException
	{
		segment.setDefaultResultScope(BTGResultScope.PERMANENT);
		modelService.save(segment);
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(5, EUR))));
		userService.setCurrentUser(anonymous);
		modelService.save(segment);
		placeTestOrder(TYSKIE);

		btgEvaluationService.evaluateAllSegments(anonymous, Collections.EMPTY_LIST, context);


		assertSegmentResults(anonymous, segment, true, context, BTGResultScope.SESSION);
	}

	@Test
	public void testEvaluateAllSegmentsForAnonymousAndMoveResults() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);

		evaluateAnonymousSegment(segment, context);
		btgResultService.moveSessionResults(anonymous, context, customerA, context);

		assertSegmentResults(customerA, segment, true, context, BTGResultScope.PERMANENT);
	}

	@Test
	public void testEvaluateAllSegmentsInDefaultSegmentScope() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		segment.setDefaultResultScope(BTGResultScope.SESSION);
		modelService.save(segment);
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(5, EUR))));
		userService.setCurrentUser(customerA);
		modelService.save(segment);
		placeTestOrder(TYSKIE);

		btgEvaluationService.evaluateAllSegments(customerA, Collections.EMPTY_LIST, new BTGEvaluationContext(
				BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.OPTIMIZED));


		assertSegmentResults(customerA, segment, true, new BTGEvaluationContext(BTGResultScope.SESSION), BTGResultScope.SESSION);
		assertSegmentResults(customerA, segment, false, new BTGEvaluationContext(BTGResultScope.PERMANENT), null);

		sessionService.createNewSession();

		assertSegmentResults(customerA, segment, false, new BTGEvaluationContext(BTGResultScope.SESSION), null);


	}


	private void assertSegmentResults(final UserModel user, final BTGSegmentModel segment, final boolean result,
			final BTGEvaluationContext context, final BTGResultScope expectedResultScope)
	{
		assertEquals("Segment result", Boolean.valueOf(result),
				Boolean.valueOf(btgResultService.isFulfilled(user, segment, context)));
		if (result)
		{
			final BTGSegmentResultModel segmentResult = btgResultService.checkSegmentForUser(user, segment, context);
			assertEquals("Result scope of segment result", expectedResultScope, segmentResult.getResultScope());
		}

		final Collection<BTGRuleModel> rules = segment.getRules();
		for (final BTGRuleModel eachRule : rules)
		{
			assertEquals("Rule fulfiled " + eachRule.getCode(), Boolean.valueOf(result),
					Boolean.valueOf(btgResultService.isFulfilled(user, eachRule, context)));
			if (result)
			{
				final BTGRuleResultModel ruleResult = btgResultService.checkRuleForUser(user, eachRule, context);
				assertEquals("Result scope for rule result", expectedResultScope, ruleResult.getResultScope());
			}
			final Collection<BTGConditionModel> conditions = eachRule.getConditions();
			for (final BTGConditionModel eachCondition : conditions)
			{
				assertEquals("Condition fulfiled " + eachCondition.getUid(), Boolean.valueOf(result),
						Boolean.valueOf(btgResultService.isFulfilled(user, eachRule, context)));
				if (result)
				{
					final BTGConditionResultModel conditionResult = btgResultService.checkConditionForUser(user, eachCondition,
							context);
					assertEquals("Result scope for condition result", expectedResultScope, conditionResult.getResultScope());
				}
			}
		}
	}

	@Test
	public void testEvaluateAllSegmentsNotTest() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(6, EUR))));
		modelService.save(segment);

		userService.setCurrentUser(customerA);

		placeTestOrder(TYSKIE);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		btgEvaluationService.evaluateAllSegments(customerA, Collections.EMPTY_LIST, context);

		assertFalse("Segment do not contain user customerA", btgResultService.isFulfilled(customerA, segment, context));

	}


	@Test
	public void processSegmentTest() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(5, EUR))));
		userService.setCurrentUser(customerA);
		modelService.save(segment);
		placeTestOrder(TYSKIE);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		btgEvaluationService.evaluateSegment(customerA, segment, context);


		assertTrue("Segment do not contain user customerA", btgResultService.isFulfilled(customerA, segment, context));


	}


	@Test
	public void testProcessSegmentNot() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));



		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(6, EUR))));
		userService.setCurrentUser(customerA);
		modelService.save(segment);
		placeTestOrder(TYSKIE);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		btgEvaluationService.evaluateSegment(customerA, segment, context);


		assertFalse("Segment do not contain user customerA", btgResultService.isFulfilled(customerA, segment, context));


	}


	@Test
	public void testCheckSegmentStatus() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));
		final BTGConditionModel condition = createExpression(createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""),
				createOperator(PriceExpressionEvaluator.EQUALS), createPriceReferenceOperand(5, EUR));
		rule.setConditions(Collections.singletonList(condition));

		userService.setCurrentUser(customerC);
		modelService.save(segment);

		assertNull("Result should be null",
				btgResultService.checkSegmentForUser(customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT));
		assertNull("Result should be null", btgResultService.checkRuleForUser(customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT));
		assertNull("Result should be null",
				btgResultService.checkConditionForUser(customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT));

		btgEvaluationService.evaluateSegment(customerC, segment, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT));

		assertFalse("Result should be false",
				btgResultService.checkSegmentForUser(customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());
		assertFalse("Result should be false", btgResultService.checkRuleForUser(customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT)
				.isFulfilled());
		assertFalse("Result should be false",
				btgResultService.checkConditionForUser(customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());


		placeTestOrder(TYSKIE);

		btgEvaluationService.evaluateSegment(customerC, segment, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT));

		assertTrue("Result should be true", btgResultService
				.checkSegmentForUser(customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());
		assertTrue("Result should be true", btgResultService.checkRuleForUser(customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT)
				.isFulfilled());
		assertTrue("Result should be true",
				btgResultService.checkConditionForUser(customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());

		btgResultService.setSegmentResult(false, customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT, false);
		btgResultService.setRuleResult(false, customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT);
		btgResultService.setConditionResult(false, customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT);

		assertFalse("Result should be false",
				btgResultService.checkSegmentForUser(customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());
		assertFalse("Result should be false", btgResultService.checkRuleForUser(customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT)
				.isFulfilled());
		assertFalse("Result should be false",
				btgResultService.checkConditionForUser(customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());


		btgEvaluationService.evaluateSegment(customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT);

		assertTrue("Result should be true", btgResultService
				.checkSegmentForUser(customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());
		assertTrue("Result should be true", btgResultService.checkRuleForUser(customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT)
				.isFulfilled());
		assertTrue("Result should be true",
				btgResultService.checkConditionForUser(customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());


		btgResultService.setSegmentResult(false, customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT, false);
		btgResultService.setRuleResult(false, customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT);
		btgResultService.setConditionResult(false, customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT);

		assertFalse("Result should be false",
				btgResultService.checkSegmentForUser(customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());
		assertFalse("Result should be false", btgResultService.checkRuleForUser(customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT)
				.isFulfilled());
		assertFalse("Result should be false",
				btgResultService.checkConditionForUser(customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());



		btgResultService.setSegmentResult(true, customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT, false);
		btgResultService.setRuleResult(true, customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT);
		btgResultService.setConditionResult(true, customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT);

		assertTrue("Result should be true", btgResultService
				.checkSegmentForUser(customerC, segment, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());
		assertTrue("Result should be true", btgResultService.checkRuleForUser(customerC, rule, PERISTENT_RESULT_SCOPE_CONTEXT)
				.isFulfilled());
		assertTrue("Result should be true",
				btgResultService.checkConditionForUser(customerC, condition, PERISTENT_RESULT_SCOPE_CONTEXT).isFulfilled());

	}

}
