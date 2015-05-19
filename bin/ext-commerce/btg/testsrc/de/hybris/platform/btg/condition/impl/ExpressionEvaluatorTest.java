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

import de.hybris.platform.btg.condition.ExpressionEvaluator;
import de.hybris.platform.btg.condition.ExpressionEvaluatorRegistry;
import de.hybris.platform.btg.condition.operand.types.KeyValuePair;
import de.hybris.platform.btg.integration.BTGIntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ExpressionEvaluator} and {@link ExpressionEvaluatorRegistry}.
 * <p/>
 * Absolutely no hybris dependencies which means each test can be executed without a running platform.<br/>
 * Additionally test execution can be triggered via main(...) as regular java application. This approach brings some
 * additional debug output which may be helpful for bug-hunting.
 * 
 */

public class ExpressionEvaluatorTest extends BTGIntegrationTest
{
	@Resource
	private static final Logger LOG = Logger.getLogger(ExpressionEvaluatorTest.class);

	private static final Boolean FALSE = Boolean.FALSE;
	private static final Boolean TRUE = Boolean.TRUE;

	/**
	 * A custom {@link ExpressionEvaluator} for testing.
	 * <p/>
	 * Adds a new operator "IS_DUMMY" which evaluates expression to true when left and right operand value is "DUMMY"
	 */
	private static class DummyExpressionEvaluator extends AbstractExpressionEvaluator
	{
		public DummyExpressionEvaluator()
		{
			super(String.class);
			super.addSupportedOperator("IS_DUMMY", String.class);
		}

		@Override
		public boolean evaluateTerm(final Object leftOperand, final String operator, final Object rightOperand)
		{
			// dummy operation; returns true when left operand is "DUMMY" and right operand is "DUMMY"
			return "DUMMY".equals(leftOperand) && "DUMMY".equals(rightOperand);
		}
	}


	/**
	 * Tests {@link StringExpressionEvaluator}.
	 */
	@Test
	public void testStringExpressionEvaluator()
	{
		// the evaluator to test
		final ExpressionEvaluator eval = new StringExpressionEvaluator();

		// setup test container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);
		test.expectedRightTypes = (List) Arrays.asList(String.class);

		// Tests for
		// EQUALS operator

		// test "left".equals."right" -> false
		test.execute("left", "equals", "right", FALSE);

		// test "left".equals.null -> Exception (missing right operand)
		test.execute("left", "equals", null, FALSE);

		// test "left".equals."" -> false
		test.execute("left", "equals", "", FALSE);

		// test "left".equals."left" -> true
		test.execute("left", "equals", "left", TRUE);

		// test "left".equals.10 -> Exception (invalid right operand)
		test.execute("left", "equals", Integer.valueOf("10"), FALSE);

		// Tests for
		// IS_EMPTY operator
		test.expectedRightTypes = Collections.EMPTY_LIST;

		// test "left".isEmpty."right" -> Exception (right operand invalid)
		test.execute("left", "isEmpty", "right", FALSE);

		// test "left".isEmpty.null -> false
		test.execute("left", "isEmpty", null, FALSE);

		// test "".isEmpty.null -> true
		test.execute("", "isEmpty", null, TRUE);

		// test null.isEmpty.null -> Exception (missing left operand)
		// ---
		// TODO: this may be a pitfall but it's currently implemented:
		// 'null' as left operand will never be allowed, in that case the
		// ValueProvider must deal with that (e.g. return "" instead of null)
		test.execute(null, "isEmpty", null, AbstractExpressionEvaluator.LEFT_OP_MISSING);
	}

	/**
	 * Tests Integer expression
	 */
	@Test
	public void testNumericExpressionEvaluator()
	{
		// the evaluator to test
		final ExpressionEvaluator eval = new NumericExpressionEvaluator(Integer.class);

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		// set expected value for supported right types
		test.expectedRightTypes = (List) Arrays.asList(Integer.class);

		// Tests for
		// < (LESS_THEN) operator
		final Integer i10 = Integer.valueOf(10);
		final Integer i11 = Integer.valueOf(11);

		// test 10 < 10 -> false
		test.execute(i10, NumericExpressionEvaluator.LESS_THAN, i10, FALSE);

		// test 10 < 11 -> true
		test.execute(i10, NumericExpressionEvaluator.LESS_THAN, i11, TRUE);

		// test 11 < 10 -> false
		test.execute(i11, NumericExpressionEvaluator.LESS_THAN, i10, FALSE);

		// test 10 < "" -> Exception
		test.execute(i10, NumericExpressionEvaluator.LESS_THAN, "10", AbstractExpressionEvaluator.RIGHT_OP_NOT_SUPPORTED);
	}

	/**
	 * Tests Collection specific expression. Tests only collection specific properties. Does nothing with collection
	 * elements.
	 */
	@Test
	public void testPlainCollectionExpressionEvaluator()
	{

		// the evaluator to test
		final ExpressionEvaluator eval = new PlainCollectionExpressionEvaluator();

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		// we use this list as left operand for all test cases
		final List<String> leftOp = Arrays.asList("one", "two", "three");

		// Test-Cases for
		// SIZE
		final Integer integer3 = Integer.valueOf(3);
		test.expectedRightTypes = (List) Arrays.asList(Integer.class);

		// test ["one","two",three"].size.3 -> true
		test.execute(leftOp, "size", integer3, TRUE);

		// test ["one","two",three"].size."3" -> exception: wrong right operand
		test.execute(leftOp, "size", "3", AbstractExpressionEvaluator.RIGHT_OP_NOT_SUPPORTED);

		// Test-Cases for
		// CONTAINS
		test.expectedRightTypes = (List) Arrays.asList(Collection.class, Object.class);

		// test ["one","two",three"].contains."one" -> true
		test.execute(leftOp, "contains", "one", TRUE);
		// test ["one","two",three"].contains."four" -> false
		test.execute(leftOp, "contains", "four", FALSE);
		// test ["one","two",three"].contains.1 -> false
		// (Integer is valid because supported right type is Object)
		test.execute(leftOp, "contains", Integer.valueOf(1), FALSE);
		// test ["one","two",three"].contains.null -> exception: missing right operand
		test.execute(leftOp, "contains", null, AbstractExpressionEvaluator.RIGHT_OP_MISSING);

	}

	/**
	 * Tests Collection specific expression as well as expressions for collection elements. ExpressionnEvaluator must be
	 * told, in which mode it shall operate. Whenever the mode is changed, the state for various properties (like
	 * supported types) gets updated automatically too.
	 * <p/>
	 * For a GUI it means, that a user has to decide, whether he wants to set expression on the collection itself or on
	 * collection elements.
	 */
	@Test
	public void testCollectionExpressionEvaluator()
	{
		// the evaluator to test
		final CollectionExpressionEvaluator eval = new CollectionExpressionEvaluator();
		eval.setCollectionExpEval(new PlainCollectionExpressionEvaluator());
		eval.setElementExpEval(new StringExpressionEvaluator());

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		// we use this list as left operand for all test cases
		final List<String> leftOp = Arrays.asList("", "", "dd");

		test.expectedRightTypes = (List) Arrays.asList(Boolean.class);

		// TEST
		// test isEmpty on collection itself
		eval.setCollectionMode(true);
		test.execute(leftOp, "isEmpty", Boolean.TRUE, FALSE);

		eval.setElementMode(true);
		test.expectedRightTypes = Collections.emptyList();
		// test isEmpty on collection elements
		// and all elements must match
		test.execute(leftOp, "isEmpty", null, FALSE);
	}

	@Test
	public void testRegExpCollectionExpressionEvaluator()
	{
		final RegExpCollectionExpressionEvaluator eval = new RegExpCollectionExpressionEvaluator();

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		// we use this list as left operand for all test cases
		final List<String> leftOp = Arrays.asList("www.google.de", "www.hybris.de", "www.hybris.com");
		final List<String> rightOp = Arrays.asList(".*\\.com", "www.*");

		test.expectedRightTypes = (List) Arrays.asList(Collection.class);

		test.execute(leftOp, "contains", rightOp, Boolean.TRUE);

		test.execute(leftOp.subList(0, 2), "contains", rightOp, Boolean.FALSE);

		test.execute(leftOp.subList(0, 2), "containsAny", rightOp, Boolean.TRUE);

		test.execute(leftOp.subList(0, 2), "notContains", rightOp.subList(0, 1), Boolean.TRUE);
	}

	@Test
	public void testKeyValuePairCollectionExpressionEvaluator()
	{
		final KeyValuePairCollectionExpressionEvaluator eval = new KeyValuePairCollectionExpressionEvaluator();

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		// we use this list as left operand for all test cases
		final List<KeyValuePair> leftOp = Arrays.asList(new KeyValuePair("user", "test"), new KeyValuePair("user", "admin"),
				new KeyValuePair("group", "users"));

		test.expectedRightTypes = (List) Arrays.asList(Collection.class);

		test.execute(leftOp, "containsAny", Arrays.asList(new KeyValuePair("user", "admin"), new KeyValuePair("user", "guest")),
				Boolean.TRUE);

		test.execute(leftOp, "contains", Arrays.asList(new KeyValuePair("user", "admin"), new KeyValuePair("group", null)),
				Boolean.TRUE);

		test.execute(leftOp, "contains", Arrays.asList(new KeyValuePair("user", "admin"), new KeyValuePair("group", "admins")),
				Boolean.FALSE);

		test.execute(leftOp, "notContains", Arrays.asList(new KeyValuePair("user", "guest"), new KeyValuePair("group", "admins")),
				Boolean.TRUE);
	}

	@Test
	public void testProductCollectionExpressionEvaluator()
	{
		List<ProductModel> leftProducts;
		List<ProductModel> rightProducts;

		final ProductCollectionExpressionEvaluator eval = new ProductCollectionExpressionEvaluator();
		eval.setFlexibleSearchService(flexibleSearchService);

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		test.expectedRightTypes = (List) Arrays.asList(Collection.class);

		// one match
		leftProducts = Arrays.asList(getProduct(ONLINE, "krostitzer"));
		rightProducts = Arrays.asList(getProduct(STAGED, "krostitzer"), getProduct(ONLINE, TYSKIE));

		test.execute(leftProducts, "containsAny", rightProducts, Boolean.TRUE);

		// two matches
		leftProducts = Arrays
				.asList(getProduct(ONLINE, "krostitzer"), getProduct(ONLINE, TYSKIE), getProduct(STAGED, "krostitzer"));
		rightProducts = Arrays.asList(getProduct(STAGED, "krostitzer"), getProduct(ONLINE, TYSKIE));

		test.execute(leftProducts, "containsAny", rightProducts, Boolean.TRUE);
		test.execute(leftProducts, "contains", rightProducts, Boolean.TRUE);

		// no matches
		leftProducts = Arrays.asList(getProduct(ONLINE, AUGISTINER), getProduct(ONLINE, HACKERPSCHORR));
		rightProducts = Arrays.asList(getProduct(STAGED, "krostitzer"), getProduct(ONLINE, TYSKIE));

		test.execute(leftProducts, "containsAny", rightProducts, Boolean.FALSE);
		test.execute(leftProducts, "notContains", rightProducts, Boolean.TRUE);
	}

	@Test
	public void testProductCollectionExpressionEvaluatorSize()
	{
		List<ProductModel> leftProducts;

		final ProductCollectionExpressionEvaluator eval = new ProductCollectionExpressionEvaluator();
		eval.setFlexibleSearchService(flexibleSearchService);

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		test.expectedRightTypes = (List) Arrays.asList(Integer.class);

		// empty
		test.execute(Collections.EMPTY_LIST, "size", Integer.valueOf(0), Boolean.TRUE);

		// one real
		leftProducts = Arrays.asList(getProduct(ONLINE, "krostitzer"));

		test.execute(leftProducts, "size", Integer.valueOf(1), Boolean.TRUE);

		// three products, two real
		leftProducts = Arrays
				.asList(getProduct(ONLINE, "krostitzer"), getProduct(ONLINE, TYSKIE), getProduct(STAGED, "krostitzer"));

		test.execute(leftProducts, "size", Integer.valueOf(2), Boolean.TRUE);

		// two products, two real
		leftProducts = Arrays.asList(getProduct(ONLINE, AUGISTINER), getProduct(ONLINE, HACKERPSCHORR));

		test.execute(leftProducts, "size", Integer.valueOf(2), Boolean.TRUE);
	}

	@Test
	public void testCategoryCollectionExpressionEvaluator()
	{
		List<CategoryModel> leftCategories;
		List<CategoryModel> rightCategories;

		final CategoryCollectionExpressionEvaluator eval = new CategoryCollectionExpressionEvaluator();
		eval.setFlexibleSearchService(flexibleSearchService);

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		test.expectedRightTypes = (List) Arrays.asList(Collection.class);

		// one match
		leftCategories = Arrays.asList(getCategory(ONLINE, "Wine"));
		rightCategories = Arrays.asList(getCategory(STAGED, "Wine"), getCategory(STAGED, BEERS_STAGED));

		test.execute(leftCategories, "containsAny", rightCategories, Boolean.TRUE);

		// two matches
		leftCategories = Arrays.asList(getCategory(ONLINE, "Wine"), getCategory(ONLINE, GERMANBEERS_ONLINE),
				getCategory(STAGED, "Wine"));
		rightCategories = Arrays.asList(getCategory(STAGED, "Wine"), getCategory(ONLINE, GERMANBEERS_ONLINE));

		test.execute(leftCategories, "containsAny", rightCategories, Boolean.TRUE);
		test.execute(leftCategories, "contains", rightCategories, Boolean.TRUE);

		// no matches
		leftCategories = Arrays.asList(getCategory(ONLINE, GERMANBEERS_ONLINE), getCategory(ONLINE, "Wine"));
		rightCategories = Arrays.asList(getCategory(STAGED, POLISHBEERS_STAGED), getCategory(ONLINE, BEERS_ONLINE));

		test.execute(leftCategories, "containsAny", rightCategories, Boolean.FALSE);
		test.execute(leftCategories, "notContains", rightCategories, Boolean.TRUE);
	}

	@Test
	public void testCategoryCollectionExpressionEvaluatorSize()
	{
		List<CategoryModel> leftCategories;

		final CategoryCollectionExpressionEvaluator eval = new CategoryCollectionExpressionEvaluator();
		eval.setFlexibleSearchService(flexibleSearchService);

		// setup test-container
		final ExpressionEvaluatorTestCase test = new ExpressionEvaluatorTestCase(eval);

		test.expectedRightTypes = (List) Arrays.asList(Integer.class);

		// empty
		test.execute(Collections.EMPTY_LIST, "size", Integer.valueOf(0), Boolean.TRUE);

		// one product, one real
		leftCategories = Arrays.asList(getCategory(ONLINE, "Wine"));

		test.execute(leftCategories, "size", Integer.valueOf(1), Boolean.TRUE);

		// three products, two real
		leftCategories = Arrays.asList(getCategory(ONLINE, "Wine"), getCategory(ONLINE, GERMANBEERS_ONLINE),
				getCategory(STAGED, "Wine"));

		test.execute(leftCategories, "size", Integer.valueOf(2), Boolean.TRUE);

		// two products, two real - test negative result
		leftCategories = Arrays.asList(getCategory(ONLINE, GERMANBEERS_ONLINE), getCategory(ONLINE, "Wine"));

		test.execute(leftCategories, "size", Integer.valueOf(1), Boolean.FALSE);
	}

	private ProductModel getProduct(final String catalogVersionName, final String productCode)
	{
		final CatalogVersionModel catalogVersion = catalogService.getCatalogVersion("drinksCatalog", catalogVersionName);
		return productService.getProduct(catalogVersion, productCode);
	}

	private CategoryModel getCategory(final String catalogVersionName, final String categoryCode)
	{
		final CatalogVersionModel catalogVersion = catalogService.getCatalogVersion("drinksCatalog", catalogVersionName);
		return categoryService.getCategory(catalogVersion, categoryCode);
	}

	/**
	 * Internal helper.
	 * <p/>
	 * Creates a generic test setup.
	 */
	private class ExpressionEvaluatorTestCase
	{
		private final ExpressionEvaluator eval;

		// null when an exception is expected
		private List<Class> expectedRightTypes;

		private ExpressionEvaluatorTestCase(final ExpressionEvaluator eval)
		{
			this.eval = eval;
		}

		private void execute(final Object leftOp, final String operator, final Object rightOp, final Object expectedResult)
		{

			// some debug output, useful when executing these tests outside from
			// junit as pure java app
			if (LOG.isDebugEnabled())
			{
				final String left = leftOp != null ? leftOp.getClass().getSimpleName() + "(" + leftOp.toString() + ")" : "null";
				final String right = rightOp != null ? rightOp.getClass().getSimpleName() + "(" + rightOp.toString() + ")" : "null";
				final String operatorString = operator != null ? operator : "null";
				LOG.debug("Executing " + left + " " + operatorString + " " + right);
			}

			// assert supported right types
			// but skip that test when no operator is given
			if (operator != null)
			{
				final Collection<Class> actualRightTypes = eval.getSupportedRightTypes(operator);

				if (expectedRightTypes != null)
				{
					Assert.assertEquals(expectedRightTypes.size(), actualRightTypes.size());
					Assert.assertTrue(expectedRightTypes.containsAll(actualRightTypes));
				}
				else
				{
					Assert.assertNull(actualRightTypes);
				}
			}

			// execute evaluation
			boolean actualResult;
			try
			{
				actualResult = eval.evaluate(leftOp, operator, rightOp);
				LOG.debug("> " + actualResult);
				Assert.assertEquals(expectedResult, Boolean.valueOf(actualResult));
			}
			catch (final ExpressionEvaluationException e)
			{
				LOG.debug("> " + e.getMessage());
				if (!e.getMessage().startsWith(expectedResult.toString()))
				{
					Assert.fail(e.toString());
				}
			}
		}
	}

	@Test
	public void testExpressionEvaluatorRegistry()
	{
		final ExpressionEvaluatorRegistry reg = new DefaultExpressionEvaluatorRegistry();

		ExpressionEvaluator actual = reg.getExpressionEvaluator(String.class);
		Assert.assertNull(actual);

		// initially add a StringExpressionEvaluator
		reg.addExpressionEvaluator(new StringExpressionEvaluator());
		actual = reg.getExpressionEvaluator(String.class);
		Assert.assertNotNull(actual);
		Assert.assertEquals(StringExpressionEvaluator.class, actual.getClass());

		// add same evaluator again
		// (Registry should LOG a warning)
		reg.addExpressionEvaluator(new StringExpressionEvaluator());
		actual = reg.getExpressionEvaluator(String.class);
		Assert.assertNotNull(actual);
		// ... still we only get a StringExpressionEvaluator
		Assert.assertEquals(StringExpressionEvaluator.class, actual.getClass());

		// add another evaluator for same left operand type (String.class)
		reg.addExpressionEvaluator(new DummyExpressionEvaluator());
		actual = reg.getExpressionEvaluator(String.class);
		Assert.assertNotNull(actual);
		// ... we now should have a composite
		Assert.assertEquals(CompositeExpressionEvaluator.class, actual.getClass());

		// add again (now affects not the registry anymore but the Composite)
		// (CompositeExpressionEvaluator should LOG a warning)
		reg.addExpressionEvaluator(new StringExpressionEvaluator());
		actual = reg.getExpressionEvaluator(String.class);
		Assert.assertNotNull(actual);
		Assert.assertEquals(CompositeExpressionEvaluator.class, actual.getClass());

		// composite contains two objects
		Assert.assertEquals(2, ((CompositeExpressionEvaluator) actual).getExpressionEvalutorTypes().size());
	}

	@Test
	public void testExpressionEvaluatorRegistry2()
	{
		final ExpressionEvaluatorRegistry registry = new DefaultExpressionEvaluatorRegistry();
		registry.addExpressionEvaluator(new NumericExpressionEvaluator(Integer.class));
		registry.addExpressionEvaluator(new PlainCollectionExpressionEvaluator());

		final ExpressionEvaluator expVal = registry.getExpressionEvaluator(Integer.class);
		expVal.evaluate(Integer.valueOf(10), NumericExpressionEvaluator.LESS_THAN, Integer.valueOf(20));

	}


	public static void main(final String... argc)
	{

		Logger.getRootLogger().addAppender(new ConsoleAppender(new PatternLayout("%-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.DEBUG);

		final ExpressionEvaluatorTest test = new ExpressionEvaluatorTest();

		test.testStringExpressionEvaluator();
		test.testNumericExpressionEvaluator();
		test.testPlainCollectionExpressionEvaluator();
		test.testCollectionExpressionEvaluator();
	}

}
