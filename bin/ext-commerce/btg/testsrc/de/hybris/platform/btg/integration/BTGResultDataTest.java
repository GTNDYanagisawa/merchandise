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
import de.hybris.platform.btg.model.BTGAbstractResultModel;
import de.hybris.platform.btg.model.BTGSegmentModel;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests if the result data is saved properly. The expected behavior is that on each level (segment/rule/condition) a
 * new result entry should be persisted if relates to first evaluation on this level or if the evaluation result has
 * just changed.
 */
public class BTGResultDataTest extends BTGIntegrationTest
{

	@Resource
	private SessionService sessionService;

	@Before
	public void prepare() throws Exception
	{
		createSegmentA();
	}

	@Test
	public void testResultDataInFullMode() throws Exception
	{

		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		//place orders for customerA. Orders satisfy order rules of segmentA
		placeOrdersForSegmentA(customerA);

		//cart rule not fulfilled
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.FULL);
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);

		Assert.assertFalse(btgResultService.isFulfilled(customerA, segmentA, context));

		assertResultScope(customerA.getSegmentResults(), context, segmentA, customerA);
		Assert.assertEquals("One segment result Entry expected", 1, customerA.getSegmentResults().size());
		Assert.assertEquals("Each rule evaluation should produce one result entry during first evaluation", segmentA.getRules()
				.size(), customerA.getRuleResults().size());

		//segment A has two rules:
		final int condNumnber = segmentAOrderRule.getConditions().size() + segmentACartRule.getConditions().size();
		assertResultScope(customerA.getConditionResults(), context, segmentA, customerA);
		Assert.assertEquals("Each condition evaluation should produce one result entry during first evaluation", condNumnber,
				customerA.getConditionResults().size());
		//in the context of BTG - nothing should change - no additional result entries should be produced
		addToCart(TYSKIE);

		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);

		Assert.assertFalse(btgResultService.isFulfilled(customerA, segmentA, context));

		Assert.assertEquals("No additional segment result entries expected", 1, customerA.getSegmentResults().size());
		assertResultScope(customerA.getRuleResults(), context, segmentA, customerA);
		Assert.assertEquals("No additional rules result entries expected", segmentA.getRules().size(), customerA.getRuleResults()
				.size());
		Assert.assertEquals("No additional conditions result entries expected", condNumnber, customerA.getConditionResults().size());

		//now the cart rule should be fulfilled and this changes evaluation result of the whole segment (but not the ORDER RULE, which was already fulfilled earlier)
		addToCart(AUGISTINER);

		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);

		Assert.assertTrue(btgResultService.isFulfilled(customerA, segmentA, context));

		Assert.assertEquals("Aditional segment result entry expected", 2, customerA.getSegmentResults().size());
		Assert.assertEquals("Additional rule result entries expected - cart rule changed evaluation result", segmentA.getRules()
				.size() + 1, customerA.getRuleResults().size());
		Assert.assertEquals("Additional condition result entry expected", condNumnber + 1, customerA.getConditionResults().size());

	}

	private void assertResultScope(final Collection<? extends BTGAbstractResultModel> results, final BTGEvaluationContext context,
			final BTGSegmentModel segment, final UserModel user)
	{
		for (final BTGAbstractResultModel result : results)
		{
			BTGResultScope resultScope = context.getResultScope(segment, user);
			if (resultScope == null)
			{
				resultScope = BTGResultScope.PERMANENT;
			}
			Assert.assertEquals("Result scope", resultScope, result.getResultScope());
			Assert.assertEquals("Session ID", sessionService.getCurrentSession().getSessionId(), result.getSessionId());
		}
	}
}
