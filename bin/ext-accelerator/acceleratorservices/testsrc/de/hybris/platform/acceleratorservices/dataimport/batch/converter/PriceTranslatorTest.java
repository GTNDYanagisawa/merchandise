/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.Item;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link PriceTranslator}
 */
@UnitTest
public class PriceTranslatorTest
{
	private PriceTranslator translator;
	@Mock
	private Item item;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		translator = new PriceTranslator();
	}

	@Test
	public void testNull()
	{
		Assert.assertNull(translator.importValue(null, item));
	}

	@Test
	public void testEmpty()
	{
		Assert.assertNull(translator.importValue("", item));
	}

	@Test
	public void testNumberFormat()
	{
		Assert.assertNull(translator.importValue("abc", item));
		Assert.assertTrue(translator.wasUnresolved());
	}

	@Test
	public void testNegativeValue()
	{
		Assert.assertEquals(translator.importValue("-10", item), Double.valueOf(-10));
		Assert.assertTrue(translator.wasUnresolved());
	}

	@Test
	public void testPositiveValue()
	{
		Assert.assertEquals(translator.importValue("10", item), Double.valueOf(10));
		Assert.assertFalse(translator.wasUnresolved());
	}

	@Test
	public void testZero()
	{
		Assert.assertEquals(translator.importValue("0", item), Double.valueOf(0));
		Assert.assertFalse(translator.wasUnresolved());
	}

}
