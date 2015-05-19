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

import de.hybris.platform.btg.condition.impl.PriceExpressionEvaluator;
import de.hybris.platform.btg.dao.BTGDao;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGRuleType;
import de.hybris.platform.btg.invalidation.BTGInvalidationDataContainer;
import de.hybris.platform.btg.model.BTGAbstractCartOperandModel;
import de.hybris.platform.btg.model.BTGAbstractResultModel;
import de.hybris.platform.btg.model.BTGCartTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGConditionModel;
import de.hybris.platform.btg.model.BTGConditionResultModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.model.BTGRuleResultModel;
import de.hybris.platform.btg.model.BTGSegmentResultModel;
import de.hybris.platform.btg.segment.SegmentEvaluationException;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests if the result data is saved properly. The expected behavior is that on each level (segment/rule/condition) a
 * new result entry should be persisted if relates to first evaluation on this level or if the evaluation result has
 * just changed.
 */
public class BTGResultDataInvalidationTest extends BTGIntegrationTest
{


	@Resource
	private BTGDao btgDao;
	protected BTGRuleModel cartRule;

	@Resource
	private BTGInvalidationDataContainer<BTGAbstractResultModel> btgInvalidationDataContainer;


	@Before
	public void prepare() throws Exception
	{
		createSegmentA();
		appendCartRuleToSegmentA();
	}

	@Test
	public void testInvalidateAllEvaluationForUser() throws Exception
	{
		//place orders for customerA. Orders satisfy order rules of segmentA
		placeOrdersForSegmentA(customerA);

		//cart rule not fulfilled
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.FULL);
		addToCart(TYSKIE);

		//now the cart rule should be fulfilled and this changes evaluation result of the whole segment (but not the ORDER RULE, which was already fulfilled earlier)
		addToCart(AUGISTINER);

		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);

		Assert.assertTrue(btgResultService.isFulfilled(customerA, segmentA, context));

		btgResultService.invalidateEvaluationResults(websiteA, customerA, context, null);

		//segment results should be invalidated!
		final BTGSegmentResultModel segmentResult = btgDao.getLastResult(customerA, segmentA,
				context.getResultScope(segmentA, customerA), context.getJaloSessionId());

		Assert.assertTrue(segmentResult != null && btgInvalidationDataContainer.isInvalidated(segmentResult));

		//first rule results should be invalidated!
		BTGRuleResultModel ruleResult = btgDao.getLastResult(customerA, segmentAOrderRule,
				context.getResultScope(segmentA, customerA), context.getJaloSessionId());
		Assert.assertTrue(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : segmentAOrderRule.getConditions())
		{
			//all conditions should be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertTrue(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}
		//second rule results should be invalidated!
		ruleResult = btgDao.getLastResult(customerA, segmentACartRule, context.getResultScope(segmentA, customerA),
				context.getJaloSessionId());
		Assert.assertTrue(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : segmentACartRule.getConditions())
		{
			//all conditions should be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertTrue(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}

		//third rule results should be invalidated!
		ruleResult = btgDao.getLastResult(customerA, cartRule, context.getResultScope(segmentA, customerA),
				context.getJaloSessionId());
		Assert.assertTrue(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : cartRule.getConditions())
		{
			//all conditions should be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertTrue(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}

		evaluateSegmentAAgain(context);

	}


	@Test
	public void testInvalidateOrderRulesEvaluationForUser() throws Exception
	{
		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		//place orders for customerA. Orders satisfy order rules of segmentA
		placeOrdersForSegmentA(customerA);

		//cart rule not fulfilled
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.FULL);
		addToCart(TYSKIE);

		//now the cart rule should be fulfilled and this changes evaluation result of the whole segment (but not the ORDER RULE, which was already fulfilled earlier)
		addToCart(AUGISTINER);

		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);

		Assert.assertTrue(btgResultService.isFulfilled(customerA, segmentA, context));

		btgResultService.invalidateEvaluationResults(websiteA, customerA, context, BTGRuleType.ORDER);

		final BTGSegmentResultModel segmentResult = btgDao.getLastResult(customerA, segmentA,
				context.getResultScope(segmentA, customerA), context.getJaloSessionId());
		//segment results should be invalidated!
		Assert.assertTrue(segmentResult != null && btgInvalidationDataContainer.isInvalidated(segmentResult));

		//first rule results should be invalidated!
		BTGRuleResultModel ruleResult = btgDao.getLastResult(customerA, segmentAOrderRule,
				context.getResultScope(segmentA, customerA), context.getJaloSessionId());
		Assert.assertTrue(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : segmentAOrderRule.getConditions())
		{
			//all conditions should be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertTrue(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}

		//second rule results should be invalidated!
		ruleResult = btgDao.getLastResult(customerA, segmentACartRule, context.getResultScope(segmentA, customerA),
				context.getJaloSessionId());
		Assert.assertTrue(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : segmentACartRule.getConditions())
		{
			//all conditions should be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertTrue(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}

		//second rule results SHOULDN'T be invalidated!
		ruleResult = btgDao.getLastResult(customerA, cartRule, context.getResultScope(segmentA, customerA),
				context.getJaloSessionId());
		Assert.assertFalse(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : cartRule.getConditions())
		{
			//all conditions SHOULDN'T be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertFalse(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}

		evaluateSegmentAAgain(context);
	}

	@Test
	public void testInvalidateCartRulesEvaluationForUser() throws Exception
	{
		catalogService.setSessionCatalogVersion(DRINKS, ONLINE);
		//place orders for customerA. Orders satisfy order rules of segmentA
		placeOrdersForSegmentA(customerA);

		//cart rule not fulfilled
		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE, BTGEvaluationMethod.FULL);
		addToCart(TYSKIE);

		//now the cart rule should be fulfilled and this changes evaluation result of the whole segment (but not the ORDER RULE, which was already fulfilled earlier)
		addToCart(AUGISTINER);

		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		modelService.refresh(customerA);

		Assert.assertTrue(btgResultService.isFulfilled(customerA, segmentA, context));

		btgResultService.invalidateEvaluationResults(websiteA, customerA, context, BTGRuleType.CART);


		//segment results should be invalidated!
		final BTGSegmentResultModel segmentResult = btgDao.getLastResult(customerA, segmentA,
				context.getResultScope(segmentA, customerA), context.getJaloSessionId());
		Assert.assertTrue(segmentResult != null && btgInvalidationDataContainer.isInvalidated(segmentResult));

		//first rule results SHOULDN'T be invalidated!
		BTGRuleResultModel ruleResult = btgDao.getLastResult(customerA, segmentAOrderRule,
				context.getResultScope(segmentA, customerA), context.getJaloSessionId());
		Assert.assertFalse(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : segmentAOrderRule.getConditions())
		{
			//all conditions SHOULDN'T be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertFalse(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}

		//second rule results SHOULDN'T be invalidated!
		ruleResult = btgDao.getLastResult(customerA, segmentACartRule, context.getResultScope(segmentA, customerA),
				context.getJaloSessionId());
		Assert.assertFalse(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : segmentACartRule.getConditions())
		{
			//all conditions SHOULDN'T be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertFalse(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}

		//third rule results should be invalidated!
		ruleResult = btgDao.getLastResult(customerA, cartRule, context.getResultScope(segmentA, customerA),
				context.getJaloSessionId());
		Assert.assertTrue(ruleResult != null && btgInvalidationDataContainer.isInvalidated(ruleResult));

		for (final BTGConditionModel conditionModel : cartRule.getConditions())
		{
			//all conditions SHOULDN'T be invalidated!
			final BTGConditionResultModel conditionResult = btgDao.getLastResult(customerA, conditionModel,
					context.getResultScope(segmentA, customerA), context.getJaloSessionId());
			Assert.assertTrue(conditionResult != null && btgInvalidationDataContainer.isInvalidated(conditionResult));
		}
		evaluateSegmentAAgain(context);
	}

	protected <T extends BTGAbstractCartOperandModel> T createCartOperand(final Class<T> clazz, final String name)
	{
		final T operand = (T) modelService.create(clazz);
		operand.setCode(name);
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	private void appendCartRuleToSegmentA()
	{
		final Collection<BTGConditionModel> conditionsA1 = new ArrayList<BTGConditionModel>();
		conditionsA1.add(createExpression(createCartOperand(BTGCartTotalSumOperandModel.class, "segmentACartCondition1"),
				createOperator(PriceExpressionEvaluator.GREATER_OR_EQUALS), createPriceReferenceOperand(9, EUR)));
		cartRule = createRule(online, conditionsA1, "segmentACartRule", BTGRuleType.CART);
		final List<BTGRuleModel> rules = new ArrayList<BTGRuleModel>(segmentA.getRules());
		rules.add(cartRule);
		segmentA.setRules(rules);
		modelService.save(segmentA);
	}

	private void evaluateSegmentAAgain(final BTGEvaluationContext context) throws SegmentEvaluationException
	{
		//evaluate segment AGAIN
		btgEvaluationService.evaluateAllSegments(customerA, websiteA, context);
		//check whether is fulfilled
		Assert.assertTrue(btgResultService.isFulfilled(customerA, segmentA, context));
		//check whether evaluation results are invalidated -SHOULDN'T BE!
		final BTGSegmentResultModel segmentResult = btgDao.getLastResult(customerA, segmentA,
				context.getResultScope(segmentA, customerA), context.getJaloSessionId());
		Assert.assertFalse(segmentResult != null && btgInvalidationDataContainer.isInvalidated(segmentResult));
	}


}
