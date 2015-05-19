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
package de.hybris.platform.acceleratorservices.dataimport.batch.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.util.RegexParser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link RegexParser}
 */
@UnitTest
public class RegexParserTest
{
	private static final String TEST_VALUE_SUB = "-12345.csv";
	private static final String TEST_VALUE = "test" + TEST_VALUE_SUB;
	private static final String TEST_REGEX = "-(\\d+)\\.csv";
	private RegexParser parser;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		parser = new RegexParser();
		parser.setRegex(TEST_REGEX);
		parser.afterPropertiesSet();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegal()
	{
		parser.parse(null, -1);
	}

	@Test
	public void testNull()
	{
		Assert.assertNull(parser.parse(null));
	}

	@Test
	public void testEmpty()
	{
		Assert.assertNull(parser.parse(""));
	}

	@Test
	public void testNotMatched()
	{
		Assert.assertNull(parser.parse("12345.csv"));
	}

	@Test
	public void testMatched()
	{
		Assert.assertEquals(TEST_VALUE_SUB, parser.parse(TEST_VALUE));
		Assert.assertEquals(TEST_VALUE_SUB, parser.parse(TEST_VALUE, 0));
		Assert.assertEquals("12345", parser.parse(TEST_VALUE, 1));
	}

}
