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
package de.hybris.platform.btg.validation;

import de.hybris.platform.btg.model.BTGCartTotalSumOperandModel;
import de.hybris.platform.btg.model.BTGExpressionModel;
import de.hybris.platform.btg.model.BTGIntegerLiteralOperandModel;
import de.hybris.platform.btg.model.BTGNumberOfOrdersOperandModel;
import de.hybris.platform.btg.model.BTGNumberOfOrdersRelativeDateOperandModel;
import de.hybris.platform.btg.model.BTGOperandModel;
import de.hybris.platform.btg.model.BTGProductsInCartOperandModel;
import de.hybris.platform.btg.model.BTGProductsInOrdersOperandModel;
import de.hybris.platform.btg.model.BTGQuantityOfProductInCartOperandModel;
import de.hybris.platform.btg.model.BTGReferenceCategoriesOperandModel;
import de.hybris.platform.btg.model.BTGReferencePriceOperandModel;

import java.util.Arrays;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeContextBuilder;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the different expression validators.
 */
public class ExpressionValidatorTest
{
	@Test
	public void testCartTotalSum()
	{
		// no CartTotalSum expression
		BTGExpressionModel expression = createExpression(new BTGCartTotalSumOperandModel(), new BTGIntegerLiteralOperandModel());

		assertExpression(true, new CartTotalSumValidator(), expression);

		// negative value
		final BTGReferencePriceOperandModel rightOperandModel = new BTGReferencePriceOperandModel();
		rightOperandModel.setValue(Double.valueOf(-1));

		expression = createExpression(new BTGCartTotalSumOperandModel(), rightOperandModel);

		assertExpression(false, new CartTotalSumValidator(), expression);

		// zero
		rightOperandModel.setValue(Double.valueOf(0));

		assertExpression(true, new CartTotalSumValidator(), expression);

		// positive value
		rightOperandModel.setValue(Double.valueOf(2));

		assertExpression(true, new CartTotalSumValidator(), expression);
	}

	@Test
	public void testNumberOFOrders()
	{
		// no NumberOfOrders expression
		BTGExpressionModel expression = createExpression(new BTGCartTotalSumOperandModel(), new BTGIntegerLiteralOperandModel());

		assertExpression(true, new NumberOfOrdersValidator(), expression);

		BTGIntegerLiteralOperandModel rightOperandModel;

		// negative value
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(-1));

		expression = createExpression(new BTGNumberOfOrdersOperandModel(), rightOperandModel);

		assertExpression(false, new NumberOfOrdersValidator(), expression);

		// zero
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(0));

		expression = createExpression(new BTGNumberOfOrdersRelativeDateOperandModel(), rightOperandModel);

		assertExpression(false, new NumberOfOrdersValidator(), expression);

		// positive value
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(1));

		expression = createExpression(new BTGNumberOfOrdersOperandModel(), rightOperandModel);

		assertExpression(true, new NumberOfOrdersValidator(), expression);
	}

	@Test
	public void testProductsInCart()
	{
		// no NumberOfOrders expression
		BTGExpressionModel expression = createExpression(new BTGProductsInCartOperandModel(),
				new BTGReferenceCategoriesOperandModel());

		assertExpression(true, new ProductsInCartValidator(), expression);

		BTGIntegerLiteralOperandModel rightOperandModel;

		// negative value
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(-1));

		expression = createExpression(new BTGProductsInCartOperandModel(), rightOperandModel);

		assertExpression(false, new ProductsInCartValidator(), expression);

		// zero
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(0));

		expression = createExpression(new BTGProductsInCartOperandModel(), rightOperandModel);

		assertExpression(true, new ProductsInCartValidator(), expression);

		// positive value
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(1));

		expression = createExpression(new BTGProductsInCartOperandModel(), rightOperandModel);

		assertExpression(true, new ProductsInCartValidator(), expression);
	}

	@Test
	public void testProductsInLastOrders()
	{
		// no NumberOfOrders expression
		BTGExpressionModel expression = createExpression(new BTGProductsInOrdersOperandModel(),
				new BTGReferenceCategoriesOperandModel());

		assertExpression(true, new ProductsInLastOrdersValidator(), expression);

		BTGIntegerLiteralOperandModel rightOperandModel;

		// negative value
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(-11));

		expression = createExpression(new BTGProductsInOrdersOperandModel(), rightOperandModel);

		assertExpression(false, new ProductsInLastOrdersValidator(), expression);

		// zero
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(0));

		expression = createExpression(new BTGProductsInOrdersOperandModel(), rightOperandModel);

		assertExpression(true, new ProductsInLastOrdersValidator(), expression);

		// positive value
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(13));

		expression = createExpression(new BTGProductsInOrdersOperandModel(), rightOperandModel);

		assertExpression(true, new ProductsInLastOrdersValidator(), expression);
	}

	@Test
	public void testQuantityOfProductInCart()
	{
		// no NumberOfOrders expression
		BTGExpressionModel expression = createExpression(new BTGProductsInOrdersOperandModel(),
				new BTGReferenceCategoriesOperandModel());

		assertExpression(true, new QuantityOfProductInCartValidator(), expression);

		BTGIntegerLiteralOperandModel rightOperandModel;

		// negative value
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(-1002));

		expression = createExpression(new BTGQuantityOfProductInCartOperandModel(), rightOperandModel);

		assertExpression(false, new QuantityOfProductInCartValidator(), expression);

		// zero
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(0));

		expression = createExpression(new BTGQuantityOfProductInCartOperandModel(), rightOperandModel);

		assertExpression(true, new QuantityOfProductInCartValidator(), expression);

		// positive value
		rightOperandModel = new BTGIntegerLiteralOperandModel();
		rightOperandModel.setLiteral(Integer.valueOf(5));

		expression = createExpression(new BTGQuantityOfProductInCartOperandModel(), rightOperandModel);

		assertExpression(true, new QuantityOfProductInCartValidator(), expression);
	}

	@Test
	public void testNumberOfOrdersDateRange()
	{
		BTGNumberOfOrdersOperandModel operand;

		// both dates are null
		operand = new BTGNumberOfOrdersOperandModel();
		Assert.assertTrue(new NumberOfOrdersDateRangeValidator().isValid(operand, null));

		// 'to' date is null
		operand = new BTGNumberOfOrdersOperandModel();
		operand.setFrom(new Date());

		Assert.assertTrue(new NumberOfOrdersDateRangeValidator().isValid(operand, null));

		// 'from' date is null
		operand = new BTGNumberOfOrdersOperandModel();
		operand.setTo(new Date());

		Assert.assertTrue(new NumberOfOrdersDateRangeValidator().isValid(operand, null));

		// date are equal
		operand = new BTGNumberOfOrdersOperandModel();
		operand.setFrom(new Date());
		operand.setTo(operand.getFrom());

		Assert.assertTrue(new NumberOfOrdersDateRangeValidator().isValid(operand, null));

		// 'from' < 'to'
		operand = new BTGNumberOfOrdersOperandModel();
		operand.setFrom(DateUtils.addDays(new Date(), -1));
		operand.setTo(DateUtils.addDays(new Date(), +1));

		Assert.assertTrue(new NumberOfOrdersDateRangeValidator().isValid(operand, null));

		// 'from' > 'to'
		operand = new BTGNumberOfOrdersOperandModel();
		operand.setFrom(DateUtils.addDays(new Date(), +1));
		operand.setTo(DateUtils.addDays(new Date(), -1));

		Assert.assertFalse(new NumberOfOrdersDateRangeValidator().isValid(operand, null));
	}

	@Test
	public void testRegExp()
	{
		// collection null
		Assert.assertTrue(new RegExpValidator().isValid(null, createConstraintValidatorContext()));

		// element null
		Assert.assertFalse(new RegExpValidator().isValid(Arrays.asList((String) null), createConstraintValidatorContext()));

		// element empty
		Assert.assertFalse(new RegExpValidator().isValid(Arrays.asList(""), createConstraintValidatorContext()));

		// wrong
		Assert.assertFalse(new RegExpValidator().isValid(Arrays.asList("\\yTest"), createConstraintValidatorContext()));

		// correct one
		Assert.assertTrue(new RegExpValidator().isValid(Arrays.asList("www"), createConstraintValidatorContext()));

		// one is wrong
		Assert.assertFalse(new RegExpValidator().isValid(Arrays.asList("http[s]*", "\\pTest"), createConstraintValidatorContext()));

		// all is right
		Assert.assertTrue(new RegExpValidator().isValid(Arrays.asList(".*www\\.google\\.de", "[0-9]+[a-b]*"),
				createConstraintValidatorContext()));
	}

	@Test
	public void testKeyValuePair()
	{
		// collection null
		Assert.assertTrue(new KeyValuePairValidator().isValid(null, createConstraintValidatorContext()));

		// element null
		Assert.assertFalse(new KeyValuePairValidator().isValid(Arrays.asList((String) null), createConstraintValidatorContext()));

		// wrong
		Assert.assertFalse(new KeyValuePairValidator().isValid(Arrays.asList(""), createConstraintValidatorContext()));

		// correct one
		Assert.assertTrue(new KeyValuePairValidator().isValid(Arrays.asList("key=value"), createConstraintValidatorContext()));

		// one is wrong
		Assert.assertFalse(new KeyValuePairValidator().isValid(Arrays.asList("key", ""), createConstraintValidatorContext()));

		// all is right
		Assert.assertTrue(new KeyValuePairValidator().isValid(Arrays.asList("param1=val1", "param2=val2"),
				createConstraintValidatorContext()));
	}

	private ConstraintValidatorContext createConstraintValidatorContext()
	{
		final ConstraintValidatorContext context = EasyMock.createNiceMock(ConstraintValidatorContext.class);
		final ConstraintViolationBuilder violationBuilder = EasyMock.createNiceMock(ConstraintViolationBuilder.class);
		final NodeBuilderDefinedContext nodeBuilderDefinedContext = EasyMock.createNiceMock(NodeBuilderDefinedContext.class);
		final NodeBuilderCustomizableContext nodeBuilderCustomContext = EasyMock
				.createNiceMock(NodeBuilderCustomizableContext.class);
		final NodeContextBuilder nodeContextBuilder = EasyMock.createNiceMock(NodeContextBuilder.class);

		// ConstraintValidatorContext
		EasyMock.expect(context.buildConstraintViolationWithTemplate((String) EasyMock.anyObject())).andReturn(
				violationBuilder);
		// TODO further methods

		// ConstraintViolationBuilder
		EasyMock.expect(violationBuilder.addNode((String) EasyMock.anyObject())).andReturn(nodeBuilderDefinedContext);
		EasyMock.expect(violationBuilder.addConstraintViolation()).andReturn(context);

		// NodeBuilderDefinedContext
		EasyMock.expect(nodeBuilderDefinedContext.addNode((String) EasyMock.anyObject())).andReturn(nodeBuilderCustomContext);
		EasyMock.expect(nodeBuilderDefinedContext.addConstraintViolation()).andReturn(context);

		// NodeBuilderCustomizableContext
		EasyMock.expect(nodeBuilderCustomContext.inIterable()).andReturn(nodeContextBuilder);
		EasyMock.expect(nodeBuilderCustomContext.addNode((String) EasyMock.anyObject())).andReturn(
				nodeBuilderCustomContext);
		EasyMock.expect(nodeBuilderCustomContext.addConstraintViolation()).andReturn(context);

		// NodeContextBuilder
		EasyMock.expect(nodeContextBuilder.atKey(EasyMock.anyObject())).andReturn(nodeBuilderDefinedContext);
		EasyMock.expect(nodeContextBuilder.atIndex((Integer) EasyMock.anyObject())).andReturn(nodeBuilderDefinedContext);
		EasyMock.expect(nodeContextBuilder.addNode((String) EasyMock.anyObject())).andReturn(nodeBuilderCustomContext);
		EasyMock.expect(nodeContextBuilder.addConstraintViolation()).andReturn(context);

		EasyMock.replay(context, violationBuilder, nodeBuilderDefinedContext, nodeBuilderCustomContext,
				nodeContextBuilder);

		return context;
	}

	private void assertExpression(final boolean expected, final ConstraintValidator validator, final BTGExpressionModel expression)
	{
		final boolean actual = validator.isValid(expression, null);
		if (expected)
		{
			Assert.assertTrue("Expression must be true!", actual);
		}
		else
		{
			Assert.assertFalse("Expression must be false!", actual);
		}
	}

	private BTGExpressionModel createExpression(final BTGOperandModel left, final BTGOperandModel right)
	{
		final BTGExpressionModel expression = new BTGExpressionModel();
		expression.setLeftOperand(left);
		expression.setRightOperand(right);

		return expression;
	}
}
