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
import de.hybris.platform.btg.jalo.BTGSegment;
import de.hybris.platform.btg.model.BTGConditionResultModel;
import de.hybris.platform.btg.model.BTGRuleResultModel;
import de.hybris.platform.btg.model.BTGSegmentModel;
import de.hybris.platform.btg.model.BTGSegmentResultModel;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class SegmentEvaluationTest extends BTGIntegrationTest
{

	@Before
	public void prepareOrders() throws Exception
	{
		createSegmentA();
		createSegmentB();
		createSegmentC();
		placeOrdersForSegmentA(customerA);
	}


	/**
	 * Given user A And 15 orders for user A And Sum of last 10 order values >= 1000 And Each order value of last 10
	 * orders >= 50 And Last 5 order contain a product with code = 'augustinerhelles' And Cart for this user contains
	 * product with code = 'augustinerhelles' Then If all segments are evaluated in scope 'online' for the WebSiteA and
	 * the online catalog the Segment A is fulfilled And All of the segment's rules are fulfilled And All of the rules's
	 * conditions are fulfilled And The user belongs to the group "Good Taste"
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSuccessfulSegmentEvaluation() throws Exception
	{
		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		addToCart(AUGISTINER);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);
		Assert.assertTrue("CustomerA should belong to segmentA", btgResultService.isFulfilled(customerA, segmentA, context));
		Assert.assertTrue("customerA fulfilled all the rules from SegmentA and should be assigned to 'Good taste' group", customerA
				.getGroups().contains(goodTaste));

		assertUserSegmentEvaluationResultData(customerA, segmentA, segmentB, Arrays.asList(segmentA, segmentB));

		placeOrdersForSegmentB(customerB);

		btgEvaluationService.evaluateAllSegments(customerB, websiteB, context);
		modelService.refresh(customerB);
		modelService.refresh(segmentB);
		for (final BTGSegmentResultModel result : customerB.getSegmentResults())
		{
			if (result.getSegment().equals(segmentB))
			{
				Assert.assertFalse("result should be evaluated not forced", result.isForced());
			}
		}
		final BTGSegment segmentBItem = JaloSession.getCurrentSession().getItem(segmentB.getPk());
		Assert.assertNotNull("Jalo item for segmentB", segmentBItem);
		final User customerBItem = JaloSession.getCurrentSession().getItem(customerB.getPk());
		Assert.assertNotNull("Jalo item for customerA", customerBItem);
		Assert.assertTrue("CustomerB should belong to segmentB", btgResultService.isFulfilled(customerB, segmentB, context));
		Assert.assertTrue("customerB fulfilled all the rules from SegmentB and should be assigned to 'Bad Taste' group", customerB
				.getGroups().contains(badTaste));

		assertUserSegmentEvaluationResultData(customerB, segmentB, segmentA, Arrays.asList(segmentA, segmentB));

		catalogService.setSessionCatalogVersion(DRINKS, STAGED);
		placeOrdersForUserC();
		userService.setCurrentUser(customerC);
		addToCart(AUGISTINER);
		btgEvaluationService.evaluateAllSegments(customerC, websiteA, context);
		modelService.refresh(customerC);
		Assert.assertTrue("CustomerC should belong to segmentC", btgResultService.isFulfilled(customerC, segmentC, context));
		Assert.assertTrue("customerC fulfilled all the rules from SegmentC and should be assigned to 'GoodTaste' group", customerC
				.getGroups().contains(goodTaste));

		assertUserSegmentEvaluationResultData(customerC, segmentC, segmentA, Arrays.asList(segmentC));
	}


	/**
	 * Step 1:
	 * 
	 * Given userA And 15 orders for userA And Sum of last 10 order values >= 1000 And Each order value of last 10 orders
	 * >= 50 And Last 5 order contain a product with code = 'augustinerhelles' Then If all segments are evaluated in
	 * scope 'online' for the WebSiteA and the online catalog the Segment A is not fulfilled And All of the segment's
	 * rules are fulfilled except Rule A2 And All of the rules' conditions are fulfilled except the condition for And The
	 * user does not belong to the group "Good Taste"
	 * 
	 * Step 2:
	 * 
	 * Given the above scenario And Cart for this user now contains product with code = 'augustinerhelles' Then If all
	 * segments are evaluated in scope 'online' for the WebSiteA and the online catalog the Segment A is fulfilled And
	 * All of the segment's rules are fulfilled And All of the rules's conditions are fulfilled And The user belongs to
	 * the group "Good Taste"
	 */
	@Test
	public void testStepByStepSuccessfulSegmentEvaluation() throws Exception
	{
		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		//step1
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);
		Assert.assertFalse("CustomerA should not belong to segmentA: cart condition not satisfied ",
				btgResultService.isFulfilled(customerA, segmentA, context));

		Assert.assertFalse("customerA didn't fulill cart rule from SegmentA and shouldn't be assigned to 'Good taste' group",
				customerA.getGroups().contains(goodTaste));
		//step2
		addToCart(AUGISTINER);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);
		Assert.assertTrue("With cart rule satisfied, customer should belong to segmentA",
				btgResultService.isFulfilled(customerA, segmentA, context));
		Assert.assertTrue("With cart rule satisfied, customer should be assigned to 'Good taste' group", customerA.getGroups()
				.contains(goodTaste));
	}

	/**
	 * Given userA And 15 orders for userA And Sum of last 10 order values >= 1000 And Each order value of last 10 orders
	 * >= 50 And Last 5 order contain a product with code = 'augustinerhelles' And Cart for this user contains product
	 * with code = 'augustinerhelles' Then If all segments are evaluated in scope 'online' for the WebSiteB and the
	 * online catalog the Segment A is not fulfilled And None of the segment's rules are fulfilled And None of the
	 * rules's conditions are fulfilled And The user doesn't belong to the group "Good Taste"
	 */
	@Test
	public void testEvaluationForDifferentWebSite() throws Exception
	{
		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		addToCart(AUGISTINER);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED);
		btgEvaluationService.evaluateAllSegments(customerA, websiteB, context);
		modelService.refresh(customerA);
		Assert.assertFalse(
				"CustomerA should not belong to segmentA - it was evaluated for WebSiteB: cart condition not satisfied ",
				btgResultService.isFulfilled(customerA, segmentA, context));
		Assert.assertFalse("SegmentA  was evaluated for WebSiteB. CustomerA shouldn't be assigned to 'Good taste' group", customerA
				.getGroups().contains(goodTaste));
	}

	/**
	 * Given userA And 15 orders for userA And Sum of last 10 order values >= 1000 And Each order value of last 10 orders
	 * >= 50 And Last 5 order contain a product with code = 'augustinerhelles' And Cart for this user contains product
	 * with code = 'augustinerhelles' Then If all segments are evaluated in scope 'online' for the WebSiteA and the
	 * offline catalog the Segment A is not fulfilled And None of the segment's rules are fulfilled And None of the
	 * rules's conditions are fulfilled And The user doesn't belong to the group "Good Taste"
	 * 
	 */
	@Test
	public void testEvaluationForDifferentCatalog() throws Exception
	{
		//segmentA is assigned to Online catalog
		catalogService.setSessionCatalogVersion(DRINKS, STAGED);
		addToCart(AUGISTINER);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);
		Assert.assertFalse("CustomerA should not belong to segmentA because it should not be evaluated in the staged catalog",
				btgResultService.isFulfilled(customerA, segmentA, context));
		Assert.assertFalse("CustomerA should not be assigned to 'Good taste' group", customerA.getGroups().contains(goodTaste));
	}

	/**
	 * Given successful execution of testSuccessfulSegmentEvaluation And Cart for this user still contains product with
	 * code = 'augustinerhelles' And We remove all of the orders of the users Then If all segments are evaluated in scope
	 * 'online' for the WebSiteA and the online catalog the Segment A is still fulfilled And All of the segment's rules
	 * are still fulfilled And All of the rules's conditions are still fulfilled And The user still belongs to the group
	 * "Good Taste"
	 * 
	 * 
	 */
	@Test
	public void testReevaluationOfFulfilledSegment() throws Exception
	{
		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		addToCart(AUGISTINER);
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);
		Assert.assertTrue("CustomerA should belong to segmentA", btgResultService.isFulfilled(customerA, segmentA, context));
		Assert.assertTrue("customerA fulfilled all the rules from SegmentA and should be assigned to 'Good taste' group", customerA
				.getGroups().contains(goodTaste));
		removeUsersOrders(customerA);
		modelService.refresh(customerA);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);
		Assert.assertTrue("CustomerA should still belong to segmentA once it was qualified earlier",
				btgResultService.isFulfilled(customerA, segmentA, context));
		Assert.assertTrue(
				"customerA fulfilled earlier all the rules from SegmentA and should be still assigned to 'Good taste' group",
				customerA.getGroups().contains(goodTaste));


	}

	private void assertUserSegmentEvaluationResultData(final CustomerModel customer,
			final BTGSegmentModel expectedFulfilledSegment, final BTGSegmentModel expectedNonFulfilledSegment,
			final Collection<BTGSegmentModel> evaluatedSegments)
	{
		for (final BTGSegmentResultModel segmentResult : customer.getSegmentResults())
		{
			if (expectedFulfilledSegment.equals(segmentResult.getSegment()))
			{
				Assert.assertTrue("Segment evaluation result entry must be true", segmentResult.isFulfilled());
			}
			if (expectedNonFulfilledSegment.equals(segmentResult.getSegment()))
			{
				Assert.assertFalse("Segment evaluation result entry must be false", segmentResult.isFulfilled());
			}
			Assert.assertTrue("Unexpected segment evaluation: [" + segmentResult.getSegment().getName() + "]",
					evaluatedSegments.contains(segmentResult.getSegment()));
		}
		for (final BTGRuleResultModel ruleResult : customer.getRuleResults())
		{
			if (ruleResult.getRule().getSegment().equals(expectedFulfilledSegment))
			{
				Assert.assertTrue("Rule [" + ruleResult.getRule().getCode() + "] evaluation result entry for "
						+ expectedFulfilledSegment.getName() + " must be true", ruleResult.isFulfilled());
			}
		}
		for (final BTGConditionResultModel conditionResult : customer.getConditionResults())
		{
			if (conditionResult.getCondition().getRule().getSegment().equals(expectedFulfilledSegment))
			{
				Assert.assertTrue(conditionResult.isFulfilled());
			}
		}
	}


}
