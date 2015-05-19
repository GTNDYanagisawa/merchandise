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
import de.hybris.platform.webservices.util.objectgraphtransformer.PropertyContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 */
public class EnumCollectionToStringConverterTest
{
	private HybrisEnumValue enumConstant1;
	private HybrisEnumValue enumConstant2;
	private HybrisEnumValue enumConstant3;

	@Before
	public void setUp()
	{
		enumConstant1 = Mockito.mock(HybrisEnumValue.class);
		Mockito.when(enumConstant1.getCode()).thenReturn("enumConstant1");

		enumConstant2 = Mockito.mock(HybrisEnumValue.class);
		Mockito.when(enumConstant2.getCode()).thenReturn("enumConstant2");

		enumConstant3 = Mockito.mock(HybrisEnumValue.class);
		Mockito.when(enumConstant3.getCode()).thenReturn("enumConstant3");
	}

	@Test
	public void testHybrisEnumCollectionToStringValuesConverter()
	{
		final HybrisEnumCollectionToStringValuesConverter converter = new HybrisEnumCollectionToStringValuesConverter();

		final Collection<HybrisEnumValue> source = Arrays.asList(enumConstant1, enumConstant2, enumConstant3);
		final Collection<String> actual = converter.intercept(Mockito.mock(PropertyContext.class), source);

		final Collection<String> expected = Arrays.asList("enumConstant1", "enumConstant2", "enumConstant3");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testHybrisEnumListToStringValuesConverter()
	{
		final HybrisEnumListToStringValuesConverter converter = new HybrisEnumListToStringValuesConverter();

		final List<HybrisEnumValue> source = Arrays.asList(enumConstant1, enumConstant2, enumConstant3);
		final List<String> actual = converter.intercept(Mockito.mock(PropertyContext.class), source);

		final List<String> expected = Arrays.asList("enumConstant1", "enumConstant2", "enumConstant3");

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testHybrisEnumSetToStringValuesConverter()
	{
		final HybrisEnumSetToStringValuesConverter converter = new HybrisEnumSetToStringValuesConverter();

		final Set<HybrisEnumValue> source = new HashSet(Arrays.asList(enumConstant1, enumConstant2, enumConstant3));
		final Set<String> actual = converter.intercept(Mockito.mock(PropertyContext.class), source);

		final Set<String> expected = new HashSet(Arrays.asList("enumConstant1", "enumConstant2", "enumConstant3"));

		Assert.assertEquals(expected, actual);
	}
}
