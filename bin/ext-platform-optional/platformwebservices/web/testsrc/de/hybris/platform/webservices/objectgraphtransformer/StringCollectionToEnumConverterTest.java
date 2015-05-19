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
package de.hybris.platform.webservices.objectgraphtransformer;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.webservices.BadRequestException;
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;


/**
 *
 */
public class StringCollectionToEnumConverterTest
{
	public enum TestEnum implements HybrisEnumValue
	{
		TEST_CONSTANT1, TEST_CONSTANT2, TEST_CONSTANT3, ;

		@Override
		public String getType()
		{
			return this.getClass().getSimpleName();
		}

		@Override
		public String getCode()
		{
			return this.name();
		}
	}

	private final HybrisEnumValue testEnumValue1 = TestEnum.TEST_CONSTANT1;
	private final HybrisEnumValue testEnumValue2 = TestEnum.TEST_CONSTANT2;
	private final HybrisEnumValue testEnumValue3 = TestEnum.TEST_CONSTANT3;

	@Test
	public void testHybrisEnumCollectionConverterInvalidConstant()
	{
		final StringCollectionToHybrisEnumValuesConverter converter = new StringCollectionToHybrisEnumValuesConverter()
		{

			@Override
			protected Class getEnumType(final PropertyContext propertyCtx)
			{
				return TestEnum.class;
			}
		};

		final Collection<String> source = Arrays.asList("TEST_CONSTANT0", "TEST_CONSTANT2", "TEST_CONSTANT3");


		try
		{
			converter.intercept(null, source);
			Assert.fail();
		}
		catch (final BadRequestException ex)
		{
			Assert.assertTrue("Invalid error message: " + ex.getMessage(), ex.getMessage().contains("TEST_CONSTANT0"));
		}
	}

	@Test
	public void testHybrisEnumCollectionToStringValuesConverter()
	{
		final StringCollectionToHybrisEnumValuesConverter converter = new StringCollectionToHybrisEnumValuesConverter()
		{

			@Override
			protected Class getEnumType(final PropertyContext propertyCtx)
			{
				return TestEnum.class;
			}
		};

		final Collection<String> source = Arrays.asList("TEST_CONSTANT1", "TEST_CONSTANT2", "TEST_CONSTANT3");

		final Collection<HybrisEnumValue> expected = Arrays.asList(testEnumValue1, testEnumValue2, testEnumValue3);
		final Collection<HybrisEnumValue> actual = converter.intercept(null, source);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testHybrisEnumListToStringValuesConverter()
	{
		final StringListToHybrisEnumValuesConverter converter = new StringListToHybrisEnumValuesConverter()
		{

			@Override
			protected Class getEnumType(final PropertyContext propertyCtx)
			{
				return TestEnum.class;
			}
		};

		final List<String> source = Arrays.asList("TEST_CONSTANT1", "TEST_CONSTANT2", "TEST_CONSTANT3");

		final List<HybrisEnumValue> expected = Arrays.asList(testEnumValue1, testEnumValue2, testEnumValue3);
		final List<HybrisEnumValue> actual = converter.intercept(null, source);

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testHybrisEnumSetToStringValuesConverter()
	{
		final StringSetToHybrisEnumValuesConverter converter = new StringSetToHybrisEnumValuesConverter()
		{
			@Override
			protected Class getEnumType(final PropertyContext propertyCtx)
			{
				return TestEnum.class;
			}
		};

		final Set<String> source = new HashSet<String>(Arrays.asList("TEST_CONSTANT1", "TEST_CONSTANT2", "TEST_CONSTANT3"));

		final Set<HybrisEnumValue> expected = new HashSet<HybrisEnumValue>(Arrays.asList(testEnumValue1, testEnumValue2,
				testEnumValue3));
		final Set<HybrisEnumValue> actual = converter.intercept(null, source);

		Assert.assertEquals(expected, actual);
	}
}
