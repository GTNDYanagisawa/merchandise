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

import de.hybris.platform.btg.condition.impl.BtgExpressionEvaluatorTest;
import de.hybris.platform.btg.condition.impl.BtgOrderOperandValueProviderTest;
import de.hybris.platform.btg.condition.impl.ExpressionEvaluatorTest;
import de.hybris.platform.btg.condition.impl.ScriptOperandServiceTest;
import de.hybris.platform.btg.integration.BTGResultDataInvalidationTest;
import de.hybris.platform.btg.integration.BTGResultDataTest;
import de.hybris.platform.btg.integration.EvaluationModeTest;
import de.hybris.platform.btg.integration.ManualCorrectionTest;
import de.hybris.platform.btg.integration.SegmentEvaluationTest;
import de.hybris.platform.btg.jalo.BtgTest;
import de.hybris.platform.btg.rule.BTGCartRuleEvaluationTest;
import de.hybris.platform.btg.rule.BTGRuleEvaluationTest;
import de.hybris.platform.btg.rule.BTGWCMSRuleEvaluationTest;
import de.hybris.platform.btg.rule.StorageElementsContainerTest;
import de.hybris.platform.btg.segment.SegmentTest;
import de.hybris.platform.btg.servicelayer.services.evaluator.impl.BTGEvaluationContextProviderTest;
import de.hybris.platform.btg.servicelayer.services.evaluator.impl.BtgSegmentRestrictionEvaluatorTest;
import de.hybris.platform.btg.services.BTGEvaluationServiceTest;
import de.hybris.platform.btg.services.ExpressionServiceTest;
import de.hybris.platform.btg.validation.ExpressionValidatorTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses(
{ BtgExpressionEvaluatorTest.class,//
		BtgOrderOperandValueProviderTest.class,//
		ExpressionEvaluatorTest.class,//
		ScriptOperandServiceTest.class,//
		BTGResultDataInvalidationTest.class,//
		BTGResultDataTest.class,//
		EvaluationModeTest.class,//
		ManualCorrectionTest.class,//
		SegmentEvaluationTest.class,//
		BtgTest.class,//
		BTGCartRuleEvaluationTest.class,//
		BTGRuleEvaluationTest.class,//
		BTGWCMSRuleEvaluationTest.class,//
		StorageElementsContainerTest.class,//
		SegmentTest.class,//
		BTGEvaluationContextProviderTest.class,//
		BtgSegmentRestrictionEvaluatorTest.class,//
		BTGEvaluationServiceTest.class,//
		ExpressionServiceTest.class,//
		ExpressionValidatorTest.class //
})
public class BTGTestSuite
{
	//
}
