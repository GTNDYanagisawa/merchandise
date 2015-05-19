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
import de.hybris.platform.btg.model.BTGSegmentResultModel;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class EvaluationModeTest extends BTGIntegrationTest
{

	@Before
	public void prepare() throws Exception
	{
		createSegmentA();

		userService.setCurrentUser(customerA);
		//15 orders for user
		//5 random orders
		placeRandomOrdersNotFulfillingConditions(5, customerA);
		//10 of total sum > 1000 and each of them > 50
		placeTestOrder(EXPENSIVE_150, NONFULFILLING_PRODUCT_TEMPLATE + 4);

		//this order should brake successful segmentA evaluation due to A1C2L condition (each order  > 50)
		placeTestOrder(EXPENSIVE_40);

		placeTestOrder(EXPENSIVE_150, TYSKIE);
		placeTestOrder(EXPENSIVE_40, EXPENSIVE_60);
		placeTestOrder(EXPENSIVE_150);
		//Last 5 order contain a product with code = 'augustinerhelles'
		placeTestOrder(EXPENSIVE_150, AUGISTINER);
		placeTestOrder(EXPENSIVE_100);
		placeTestOrder(EXPENSIVE_150);
		placeTestOrder(EXPENSIVE_60, HACKERPSCHORR);
		placeTestOrder(EXPENSIVE_150);
	}


	/**
	 * Given userA And 15 orders for userA And Sum of last 10 order values >= 1000 (Order Rule: cond1) And not each order
	 * value of last 10 orders >= 50(Order Rule: cond2) And Last 5 order contain a product with code =
	 * 'augustinerhelles'(Order Rule: cond3) And Cart for this user contains product with code = 'augustinerhelles'(Cart
	 * Rule: cond1) Then If segmentA is evaluated in the FULL mode in scope 'online' for the WebSiteA and the online
	 * catalog the Segment A is not fulfilled And Order rule is not fulfilled And Cart rule is not evaluated And (Order
	 * Rule: cond1) is fulfilled and (Order Rule: cond2) is not fulfilled. (The rest is not evaluated) And The user
	 * doesn't belong to the group "Good Taste"
	 * 
	 */
	@Test
	public void testOptimizedEvaluationMode() throws Exception
	{
		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		userService.setCurrentUser(customerA);
		addToCart(AUGISTINER);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED));
		modelService.refresh(customerA);

		Assert.assertEquals("Only one segment should be evaluated", 1, customerA.getSegmentResults().size());
		final BTGSegmentResultModel segmentResultEntry = customerA.getSegmentResults().iterator().next();
		Assert.assertFalse("SegmentA should have evaluate FALSE", segmentResultEntry.isFulfilled());
		Assert.assertTrue("Segment evaluation entry should correspond to segmentA", segmentResultEntry.getSegment()
				.equals(segmentA));

	}

	/**
	 * Given userA And 15 orders for userA And Sum of last 10 order values >= 1000 (Order Rule: cond1) And not each order
	 * value of last 10 orders >= 50(Order Rule: cond2) And Last 5 order contain a product with code =
	 * 'augustinerhelles'(Order Rule: cond3) And Cart for this user contains product with code = 'augustinerhelles'(Cart
	 * Rule: cond1) Then If segmentA is evaluated in the FULL mode in scope 'online' for the WebSiteA and the online
	 * catalog the Segment A is not fulfilled And Order rule is not fulfilled And Cart rule is fulfilled And All
	 * conditions are fulfilled except for Order Rule: cond2 And The user doesn't belong to the group "Good Taste"
	 */
	@Test
	public void testFullEvaluationMode() throws Exception
	{
		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		addToCart(AUGISTINER);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.FULL));
		modelService.refresh(customerA);


		Assert.assertEquals("Only one segment should be evaluated", 1, customerA.getSegmentResults().size());
		final BTGSegmentResultModel segmentResultEntry = customerA.getSegmentResults().iterator().next();
		Assert.assertFalse("SegmentA should have evaluate FALSE", segmentResultEntry.isFulfilled());
		Assert.assertTrue("Segment evaluation entry should correspond to segmentA", segmentResultEntry.getSegment()
				.equals(segmentA));
	}
}
