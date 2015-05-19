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
package de.hybris.platform.btg.integration;

import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.jalo.BTGSegment;
import de.hybris.platform.btg.model.BTGConditionModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ManualCorrectionTest extends BTGIntegrationTest
{

	@Before
	public void prepare() throws Exception
	{
		createSegmentA();
		createSegmentB();
	}

	@Test
	/**
	 * Given userA not fulfilling segmentB (not from Munich, and without any orders in history)
	 * Then userA doesn't fulfill segmentB
	 * And If user was manually added to segment B, userA belongs to the segment, but not to the 'Bad Taste' group.
	 * And If user was manually added to segment B with 'performOutputAction' option, userA belongs to the segment, and to the 'Bad Taste' group.
	 */
	public void testSegmentManualCorrection() throws Exception
	{
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);
		Assert.assertFalse("CustomerA should not belong to segmentB", btgResultService.isFulfilled(customerA, segmentB, context));
		Assert.assertFalse("CustomerA didn't fulfill any rule from segmentB and should not be assigned to 'Bad taste' group",
				customerA.getGroups().contains(badTaste));

		btgResultService.setSegmentResult(true, customerA, segmentB, new BTGEvaluationContext(BTGResultScope.PERMANENT), false);
		modelService.refresh(customerA);
		Assert.assertTrue("CustomerA should be muanually added to segmentB",
				btgResultService.isFulfilled(customerA, segmentB, context));
		Assert.assertFalse("CustomerA should not be assigned to 'Bad taste' group", customerA.getGroups().contains(badTaste));


		btgResultService
				.setSegmentResult(true, customerA, segmentB, new BTGEvaluationContext(BTGResultScope.PERMANENT), true, true);
		modelService.refresh(customerA);
		Assert.assertTrue("CustomerA should be muanually added to segmentB",
				btgResultService.isFulfilled(customerA, segmentB, context));
		Assert.assertTrue("CustomerA should be assigned to 'Bad taste' group", customerA.getGroups().contains(badTaste));

	}

	/**
	 * Step1: Given userA not fulfilling segmentB (not from Munich) And Last 5 order contain a product with code =
	 * 'HACKERPSCHORR' Then user A doesn't fulfill rule1 ('fromMunichRule') Then if segmentB is evaluated, user A doesn't
	 * fulfill segmentB Step2: Given userA was manually marked as fulfilling 'fromMunichRule' Then If segmantB is
	 * evaluated userA belongs to segmentB and to 'Bad Taste' group.
	 */
	@Test
	public void testRuleManualCorrection() throws Exception
	{
		final BTGRuleModel customerFromMunichRule = segmentB.getRules().iterator().next();
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.FULL,
				BTGResultScope.PERMANENT);
		Assert.assertFalse("User A is not from Munich, should not fulfill the rule",
				ruleEvaluator.evaluate(customerA, customerFromMunichRule, context));

		btgResultService.setRuleResult(true, customerA, customerFromMunichRule, context);
		Assert.assertTrue("User A is not from Munich but the rule was manually enabled for the user", ruleEvaluator.evaluate(
				customerA, customerFromMunichRule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.FULL)));
		placeOrdersForSegmentB(customerA);
		btgEvaluationService.evaluateAllSegments(customerA, websiteB, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT));
		modelService.refresh(customerA);
		modelService.refresh(segmentB);

		final BTGSegment segmentBItem = JaloSession.getCurrentSession().getItem(segmentB.getPk());
		Assert.assertNotNull("Jalo item for segmentB", segmentBItem);
		final User customerAItem = JaloSession.getCurrentSession().getItem(customerA.getPk());
		Assert.assertNotNull("Jalo item for customerA", customerAItem);
		Assert.assertTrue("customerA should belong to segmentB", btgResultService.isFulfilled(customerA, segmentB, context));
		Assert.assertTrue(
				"customerA didnt fulfill one rule from segmentB, but the rule evaluation was manualy adjusted and he should be assigned to 'Bad Taste' group",
				customerA.getGroups().contains(badTaste));
	}

	@Test
	/**
	 * Step1:
	 * Given userB not fulfilling segmentB (if from Munich but has no orders)
	 * Then if segmentB is evaluated, userB doesn't belong to segmentB.
	 * Step2:
	 * Given userB was manually marked as fulfilling order condition from order rule of segmentB
	 * Then If segmentB is evaluated userB belongs to segmentB and to 'Bad Taste' group.
	 */
	public void testConditionManualCorrection() throws Exception
	{
		final BTGRuleModel orderRule = ((List<BTGRuleModel>) segmentB.getRules()).get(1);
		final BTGConditionModel orderCondition = orderRule.getConditions().iterator().next();
		//customerB is from Munich, but he has no orders.

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		btgEvaluationService.evaluateAllSegments(customerB, websiteB, context);
		modelService.refresh(customerB);
		Assert.assertFalse("customerB should not belong to segmentB", btgResultService.isFulfilled(customerB, segmentB, context));

		Assert.assertFalse(
				"CustomerB has no orders - should not fulfill order condition",
				conditionEvaluatorRegistry.getConditionEvaluator(orderCondition)
						.evaluate(
								orderCondition,
								customerB,
								new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.FULL,
										BTGResultScope.PERMANENT)));
		//after manual correction..
		btgResultService.setConditionResult(true, customerB, orderCondition, new BTGEvaluationContext(BTGResultScope.PERMANENT));
		//..user should fulfill the condition
		Assert.assertTrue(
				"CustomerB has no orders - but fulfills order condition after manual correction",
				conditionEvaluatorRegistry.getConditionEvaluator(orderCondition).evaluate(orderCondition, customerB,
						new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.FULL)));
		//.. and the whole segment
		btgEvaluationService.evaluateAllSegments(customerB, websiteB, context);
		modelService.refresh(customerB);
		Assert.assertTrue("customerB should belong to segmentB", btgResultService.isFulfilled(customerB, segmentB, context));
		Assert.assertTrue(
				"customerB didnt fulfill one rule from segmentB, but the condition evaluation was manualy adjusted and he should be assigned to 'Bad Taste' group",
				customerB.getGroups().contains(badTaste));


	}
}
