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
package de.hybris.platform.btg.segment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.btg.condition.impl.PriceExpressionEvaluator;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGOrderTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.model.BTGSegmentModel;
import de.hybris.platform.btg.rule.BTGRuleEvaluationTest;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;

import java.util.Collections;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


public class SegmentTest extends BTGIntegrationTest
{

	private static final Logger LOG = Logger.getLogger(BTGRuleEvaluationTest.class);

	@Resource
	private SegmentEvaluatorRegistry segmentEvaluatorRegistry;
	@Resource
	private SegmentService segmentService;

	@Before
	public void prepare() throws Exception
	{
		LOG.info("Prepare rule evaluation test");
	}

	@Test
	public void testSegment() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGRuleModel rule = createRule("testRule");
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(5, EUR))));
		userService.setCurrentUser(customerA);
		modelService.save(segment);
		placeTestOrder(AUGISTINER);

		final SegmentEvaluator segmentEvaluator = segmentEvaluatorRegistry.getSegmentEvaluator(BTGEvaluationMethod.OPTIMIZED);
		assertNotNull("Factory return null service", segmentService);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED);
		assertTrue("Segment is not evaluated properly", segmentEvaluator.evaluate(customerA, segment, context));

		assertTrue("Segment do not contain user customerA", btgResultService.isFulfilled(customerA, segment, context));

	}

	@Test
	public void testSegmentNotFulfilled() throws Exception
	{
		final BTGSegmentModel segment = createSegment();
		final BTGRuleModel rule = createRule("testRule");
		final SegmentEvaluator segmentEvaluator = segmentEvaluatorRegistry.getSegmentEvaluator(BTGEvaluationMethod.OPTIMIZED);
		segment.setRules(Collections.singletonList(rule));

		rule.setConditions(Collections.singletonList(createExpression(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.EQUALS),
				createPriceReferenceOperand(6, EUR))));

		modelService.save(segment);

		userService.setCurrentUser(customerA);

		assertNotNull("Factory return null service", segmentService);

		final BTGEvaluationContext context = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED);
		assertFalse("Segment is not evaluated properly", segmentEvaluator.evaluate(customerA, segment, context));

		assertFalse("Segment contain user customerA", btgResultService.isFulfilled(customerA, segment, context));

	}

}
