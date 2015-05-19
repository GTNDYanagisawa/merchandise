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
package de.hybris.platform.btg.condition.impl;

import de.hybris.platform.btg.condition.ConditionEvaluator;
import de.hybris.platform.btg.condition.ConditionEvaluatorRegistry;
import de.hybris.platform.btg.condition.operand.factory.OperandValueProviderRegistry;
import de.hybris.platform.btg.condition.operand.types.CountrySet;
import de.hybris.platform.btg.condition.operand.types.JavaEnumSet;
import de.hybris.platform.btg.condition.operand.types.PostalCodeSet;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.enums.BTGEvaluationMethod;
import de.hybris.platform.btg.enums.BTGResultScope;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.btg.model.BTGConditionModel;
import de.hybris.platform.btg.model.BTGOperandModel;
import de.hybris.platform.btg.model.BTGOrderTotalSumOperandModel;
import de.hybris.platform.btg.services.impl.BTGEvaluationContext;
import de.hybris.platform.core.enums.Gender;
import de.hybris.platform.util.Config;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class BtgExpressionEvaluatorTest extends BTGIntegrationTest
{

	private static final Logger LOG = Logger.getLogger(BtgExpressionEvaluatorTest.class);


	@Resource
	private OperandValueProviderRegistry operandValueProviderRegistry;

	@Resource
	private ConditionEvaluatorRegistry conditionEvaluatorRegistry;

	@Before
	public void prepare() throws Exception
	{
		LOG.info("Prepare Order Total sum operand");
	}

	@Test
	public void testEvaluateOrderTotalSumCondition() throws Exception
	{
		final BTGEvaluationContext defaultContext = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		userService.setCurrentUser(customerC);
		//comparing to literal 7
		final BTGConditionModel orderExpression = createExpressionInRuleAndSegment(
				createOrderOperand(BTGOrderTotalSumOperandModel.class, 2, ""), createOperator(PriceExpressionEvaluator.GREATER_THAN),
				createPriceReferenceOperand(7, EUR));

		final ConditionEvaluator evaluator = conditionEvaluatorRegistry.getConditionEvaluator(orderExpression);

		Assert.assertFalse("Order total sum condition should evaluate: false (no orders in history)",
				evaluator.evaluate(orderExpression, customerC, defaultContext));
		//order for 6 EUR
		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 0, TYSKIE);
		Assert.assertFalse("Order total sum condition should evaluate: false (6 EUR so far)",
				evaluator.evaluate(orderExpression, customerC, defaultContext));
		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 1);
		Assert.assertTrue("Order total sum condition should evaluate: true (8 EUR > litaral:7)",
				evaluator.evaluate(orderExpression, customerC, defaultContext));
	}

	@Test
	public void testEvaluateLastOrderForExactDate() throws Exception
	{
		userService.setCurrentUser(customerC);

		final BTGEvaluationContext defaultContext = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);

		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 0, TYSKIE);

		if (Config.isMySQLUsed())
		{
			//MySQL datetime has a resolution of 1 sec!!!
			//we want to assure that creationTime is different for each testorder.
			Thread.sleep(1100);
		}

		final BTGConditionModel orderExpression = createExpressionInRuleAndSegment(createLastOrderDateOperand("LastOrder"),
				createOperator(DateExpressionEvaluator.GREATER_THAN), createReferenceExactDateOperand("ReferenceDate", new Date()));

		modelService.save(orderExpression);

		final ConditionEvaluator evaluator = conditionEvaluatorRegistry.getConditionEvaluator(orderExpression);

		Assert.assertFalse("Last order date is not before the reference date",
				evaluator.evaluate(orderExpression, customerC, defaultContext));

		if (Config.isMySQLUsed())
		{
			//MySQL datetime has a resolution of 1 sec!!!
			//we want to assure that creationTime is different for each testorder.
			Thread.sleep(1100);
		}

		placeTestOrder(NONFULFILLING_PRODUCT_TEMPLATE + 1, TYSKIE);

		Assert.assertTrue("Last order date is before the reference date",
				evaluator.evaluate(orderExpression, customerC, defaultContext));
	}

	@Test
	public void testCustomerAddressConditionEvaluation() throws Exception
	{
		final BTGEvaluationContext defaultContext = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);

		final BTGOperandModel postalCodeOperand = createCustomerPostalCodeOperand("customerPostalCodeOperand");
		final BTGOperandModel countryOperand = createCustomerCountryOperand("countryOperand");

		final PostalCodeSet postalCodeA = (PostalCodeSet) operandValueProviderRegistry.getOperandValueProvider(
				postalCodeOperand.getClass()).getValue(postalCodeOperand, customerA, BTGConditionEvaluationScope.OFFLINE);
		final PostalCodeSet postalCodeB = (PostalCodeSet) operandValueProviderRegistry.getOperandValueProvider(
				postalCodeOperand.getClass()).getValue(postalCodeOperand, customerB, BTGConditionEvaluationScope.OFFLINE);
		final PostalCodeSet postalCodeC = (PostalCodeSet) operandValueProviderRegistry.getOperandValueProvider(
				postalCodeOperand.getClass()).getValue(postalCodeOperand, customerC, BTGConditionEvaluationScope.OFFLINE);

		Assert.assertEquals("Operand service for [customerA: postalCode] returned wrong value", customerA.getAddresses().iterator()
				.next().getPostalcode(), postalCodeA.iterator().next());
		Assert.assertEquals("Operand service for [customerB: postalCode] returned wrong value", customerB.getAddresses().iterator()
				.next().getPostalcode(), postalCodeB.iterator().next());
		Assert.assertEquals("Operand service for [customerC: postalCode] returned wrong value", customerC.getAddresses().iterator()
				.next().getPostalcode(), postalCodeC.iterator().next());

		final CountrySet countryA = (CountrySet) operandValueProviderRegistry.getOperandValueProvider(countryOperand.getClass())
				.getValue(countryOperand, customerA, BTGConditionEvaluationScope.OFFLINE);
		final CountrySet countryB = (CountrySet) operandValueProviderRegistry.getOperandValueProvider(countryOperand.getClass())
				.getValue(countryOperand, customerB, BTGConditionEvaluationScope.OFFLINE);
		final CountrySet countryC = (CountrySet) operandValueProviderRegistry.getOperandValueProvider(countryOperand.getClass())
				.getValue(countryOperand, customerC, BTGConditionEvaluationScope.OFFLINE);

		Assert.assertEquals("Operand service for [customerA: country] returned wrong value", customerA.getAddresses().iterator()
				.next().getCountry(), countryA.iterator().next());
		Assert.assertEquals("Operand service for [customerB: country] returned wrong value", customerB.getAddresses().iterator()
				.next().getCountry(), countryB.iterator().next());
		Assert.assertEquals("Operand service for [customerC: country] returned wrong value", customerC.getAddresses().iterator()
				.next().getCountry(), countryC.iterator().next());

		final BTGConditionModel postalCodeExpression = createExpressionInRuleAndSegment(postalCodeOperand,
				createOperator("startsWith"), createStringLiteralOperand("40-1"));

		final BTGConditionModel countryExpression = createExpressionInRuleAndSegment(countryOperand, createOperator("containsAny"),
				createBTGReferenceCountriesOperandModel("DE"));

		modelService.save(countryExpression);
		modelService.save(postalCodeExpression);

		Assert.assertTrue("Postal code condition should evaluate: TRUE for user C", conditionEvaluatorRegistry
				.getConditionEvaluator(postalCodeExpression).evaluate(postalCodeExpression, customerC, defaultContext));
		Assert.assertFalse("Postal code condition should evaluate: FALSE for user B", conditionEvaluatorRegistry
				.getConditionEvaluator(postalCodeExpression).evaluate(postalCodeExpression, customerB, defaultContext));

		Assert.assertTrue(
				"Country condition should evaluate: TRUE for user B",
				conditionEvaluatorRegistry.getConditionEvaluator(countryExpression).evaluate(countryExpression, customerB,
						defaultContext));
		Assert.assertFalse(
				"Country condition should evaluate: FALSE for user C",
				conditionEvaluatorRegistry.getConditionEvaluator(countryExpression).evaluate(countryExpression, customerC,
						defaultContext));

	}

	@Test
	public void testCustomerGenderConditionEvaluation() throws Exception
	{
		final BTGEvaluationContext defaultContext = new BTGEvaluationContext(BTGConditionEvaluationScope.ONLINE,
				BTGEvaluationMethod.OPTIMIZED, BTGResultScope.PERMANENT);
		final BTGOperandModel genderOperand = createCustomerGenderOperand("genderOperand");

		final JavaEnumSet genderA = (JavaEnumSet) operandValueProviderRegistry.getOperandValueProvider(genderOperand.getClass())
				.getValue(genderOperand, customerA, BTGConditionEvaluationScope.OFFLINE);
		final JavaEnumSet genderB = (JavaEnumSet) operandValueProviderRegistry.getOperandValueProvider(genderOperand.getClass())
				.getValue(genderOperand, customerB, BTGConditionEvaluationScope.OFFLINE);
		final JavaEnumSet genderC = (JavaEnumSet) operandValueProviderRegistry.getOperandValueProvider(genderOperand.getClass())
				.getValue(genderOperand, customerC, BTGConditionEvaluationScope.OFFLINE);

		Assert.assertEquals("Operand service for [customerA: gender] returned wrong value", Gender.MALE, genderA.iterator().next());
		Assert.assertEquals("Operand service for [customerB: gender] returned wrong value", Gender.FEMALE, genderB.iterator()
				.next());
		Assert.assertEquals("Operand service for [customerC: gender] returned wrong value", Gender.FEMALE, genderC.iterator()
				.next());

		//We use collection operator here, because gender is an attribute of an Address and customer has a collection of them.
		final BTGConditionModel genderExpression = createExpressionInRuleAndSegment(genderOperand, createOperator("containsAny"),
				createGenderLiteralOperand(Gender.FEMALE));

		modelService.save(genderExpression);
		Assert.assertFalse(
				"Gender condition should evaluate: FALSE for user A",
				conditionEvaluatorRegistry.getConditionEvaluator(genderExpression).evaluate(genderExpression, customerA,
						defaultContext));
		Assert.assertTrue(
				"Gender condition should evaluate: TRUE for user B",
				conditionEvaluatorRegistry.getConditionEvaluator(genderExpression).evaluate(genderExpression, customerB,
						defaultContext));
		Assert.assertTrue(
				"Gender condition should evaluate: TRUE for user C",
				conditionEvaluatorRegistry.getConditionEvaluator(genderExpression).evaluate(genderExpression, customerC,
						defaultContext));

	}






}