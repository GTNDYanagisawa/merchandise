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
package de.hybris.platform.btg.rule;

import de.hybris.platform.btg.condition.operand.valueproviders.UrlParameterOperandValueProvider;
import de.hybris.platform.btg.condition.operand.valueproviders.ViewedProductsOperandValueProvider;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGGenericOperandModel;
import de.hybris.platform.btg.model.BTGReferenceKeyValuePairListOperandModel;
import de.hybris.platform.btg.model.BTGRuleModel;
import de.hybris.platform.btg.model.BTGUrlParameterOperandModel;
import de.hybris.platform.btg.model.BTGViewedProductsOperandModel;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;




/**
 * Tests for the expressions of the <code>WCMS</code> rule.
 */
public class BTGWCMSRuleEvaluationTest extends BTGIntegrationTest
{
	private ProductModel getProduct(final String productName)
	{
		return productService.getProduct(productName);
	}

	protected <T extends BTGGenericOperandModel> BTGGenericOperandModel createOperand(final Class<T> clazz, final String code)
	{
		final BTGGenericOperandModel operand = modelService.create(clazz);
		operand.setCode(code);
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		modelService.save(operand);
		return operand;
	}

	protected BTGReferenceKeyValuePairListOperandModel createKeyValuePairListOperand(final String code,
			final String... keyValuePairs)
	{
		final BTGReferenceKeyValuePairListOperandModel operand = modelService
				.create(BTGReferenceKeyValuePairListOperandModel.class);
		operand.setCode(code);
		operand.setScope(BTGConditionEvaluationScope.OFFLINE);
		operand.setUid(UUID.randomUUID().toString());
		operand.setCatalogVersion(getDrinksOnlineCatalogVersion());
		operand.setKeyValuePairs(Arrays.asList(keyValuePairs));
		modelService.save(operand);
		return operand;
	}

	@Test
	public void testViewedProducts() throws Exception
	{
		BTGRuleModel rule;

		// without viewed products		
		rule = createRule("testRule1");
		rule.setConditions(Collections.singletonList(createExpression(createOperand(BTGViewedProductsOperandModel.class, "test"),
				createOperator("contains"), createProductsOperand(AUGISTINER))));
		modelService.save(rule);

		Assert.assertFalse("Rule should evaluate false without any products", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

		// with one viewed product
		getRuleDataContainerForProducts().add(getProduct(AUGISTINER).getPk().toString());

		rule = createRule("testRule2");
		rule.setConditions(Collections.singletonList(createExpression(createOperand(BTGViewedProductsOperandModel.class, "test"),
				createOperator("contains"), createProductsOperand(AUGISTINER))));
		modelService.save(rule);

		Assert.assertTrue("Rule should evaluate true with a product", ruleEvaluator
				.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
						BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

	}

	@Test
	public void testUrlParameters() throws Exception
	{
		final RuleDataContainer<HashMap<String, String[]>> ruleDataContainer = getRuleDataContainerForUrlParameters();

		BTGRuleModel rule;

		// without viewed products		
		rule = createRule("testRule1");
		rule.setConditions(Collections.singletonList(createExpression(createOperand(BTGUrlParameterOperandModel.class, "test"),
				createOperator("contains"), createKeyValuePairListOperand("keyValuePairs", "param1", "param2=test"))));
		modelService.save(rule);

		Assert.assertFalse(ruleEvaluator.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

		// with one parameter
		ruleDataContainer.clear();
		HashMap<String, String[]> paramMap = new ParamMapBuilder().add("param1").toMap();
		getRuleDataContainerForUrlParameters().add(paramMap);

		rule = createRule("testRule2");
		rule.setConditions(Collections.singletonList(createExpression(createOperand(BTGUrlParameterOperandModel.class, "test"),
				createOperator("containsAny"), createKeyValuePairListOperand("keyValuePairs", "param1", "param2=test"))));
		modelService.save(rule);

		Assert.assertTrue(ruleEvaluator.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

		// with two parameters
		ruleDataContainer.clear();
		paramMap = new ParamMapBuilder().add("param1", "").add("param2", "test").toMap();
		getRuleDataContainerForUrlParameters().add(paramMap);

		rule = createRule("testRule3");
		rule.setConditions(Collections.singletonList(createExpression(createOperand(BTGUrlParameterOperandModel.class, "test"),
				createOperator("contains"), createKeyValuePairListOperand("keyValuePairs", "param1", "param2=test"))));
		modelService.save(rule);

		Assert.assertTrue(ruleEvaluator.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

		// with two parameters with multiple values
		ruleDataContainer.clear();
		paramMap = new ParamMapBuilder().add("param1", "testVal").add("param2", "testVal", "test").toMap();
		getRuleDataContainerForUrlParameters().add(paramMap);

		rule = createRule("testRule3");
		rule.setConditions(Collections.singletonList(createExpression(createOperand(BTGUrlParameterOperandModel.class, "test"),
				createOperator("contains"), createKeyValuePairListOperand("keyValuePairs", "param1", "param2=test"))));
		modelService.save(rule);

		Assert.assertTrue(ruleEvaluator.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));

		// not contains
		ruleDataContainer.clear();
		paramMap = new ParamMapBuilder().add("param1", "testVal").add("param2", "testVal", "test").toMap();
		getRuleDataContainerForUrlParameters().add(paramMap);

		rule = createRule("testRule3");
		rule.setConditions(Collections.singletonList(createExpression(createOperand(BTGUrlParameterOperandModel.class, "test"),
				createOperator("notContains"), createKeyValuePairListOperand("keyValuePairs", "param3", "param4=test"))));
		modelService.save(rule);

		Assert.assertTrue(ruleEvaluator.evaluate(customerA, rule, new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT)));
	}

	private RuleDataContainer<String> getRuleDataContainerForProducts()
	{
		final RuleDataContainer<String> ruleDataContainer = new MockRuleDataContainer<String>();

		//Setting up ViewedProductsOperandValueProvider with our mock RuleDataContainer (ugly, but works for now)
		final ViewedProductsOperandValueProvider vpovp = this.getApplicationContext().getBean(
				ViewedProductsOperandValueProvider.class);
		vpovp.setRuleDataContainer(ruleDataContainer);

		return ruleDataContainer;
	}

	private RuleDataContainer<HashMap<String, String[]>> getRuleDataContainerForUrlParameters()
	{
		final RuleDataContainer<HashMap<String, String[]>> ruleDataContainer = new MockRuleDataContainer<HashMap<String, String[]>>();

		final UrlParameterOperandValueProvider operandValueProvider = this.getApplicationContext().getBean(
				UrlParameterOperandValueProvider.class);
		operandValueProvider.setRuleDataContainer(ruleDataContainer);

		return ruleDataContainer;
	}

	@SuppressWarnings("PMD")
	private class ParamMapBuilder
	{
		private final HashMap<String, String[]> paramMap;

		public ParamMapBuilder()
		{
			// instantiate only with factory method
			paramMap = new HashMap<String, String[]>();
		}

		public ParamMapBuilder add(final String key, final String... values)
		{
			paramMap.put(key, values);
			return this;
		}

		public HashMap<String, String[]> toMap()
		{
			return paramMap;
		}
	}
}
