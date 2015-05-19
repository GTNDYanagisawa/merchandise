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

package de.hybris.vjdbc;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.Assert;


@UnitTest
public class VjdbcConnectionStringParserTest
{
	private static final Logger LOG = Logger.getLogger(VjdbcConnectionStringParserTest.class);

	@Test
	public void testParseFlex()
	{

		VjdbcConnectionStringParser parser = new VjdbcConnectionStringParser("foo");

		Assert.assertEquals("llllll?flexMode=true", parser.decorateRootServletSql("llllll"));

	}

	@Test
	public void testParseFlexEndsWithAnd()
	{

		VjdbcConnectionStringParser parser = new VjdbcConnectionStringParser("foo");

		Assert.assertEquals("llllll&?flexMode=true", parser.decorateRootServletSql("llllll&"));

	}

	@Test
	public void testParseFlexEndsWithQuestionMark()
	{

		VjdbcConnectionStringParser parser = new VjdbcConnectionStringParser("foo");

		Assert.assertEquals("llllll?&flexMode=true", parser.decorateRootServletSql("llllll?"));

	}

	@Test
	public void testParseFlexEndsWithQuestionMarkWithAndInMiddle()
	{

		VjdbcConnectionStringParser parser = new VjdbcConnectionStringParser("foo");

		Assert.assertEquals("lll&lll?flexMode=true", parser.decorateRootServletSql("lll&lll?"));

	}

	@Test
	public void testParseFlexEndsWithQuestionMarkWithAndAtEnd()
	{

		VjdbcConnectionStringParser parser = new VjdbcConnectionStringParser("foo");

		Assert.assertEquals("llllll?&flexMode=true", parser.decorateRootServletSql("llllll?&"));

	}

	@Test
	public void testParseFlexEndsWithOneParam()
	{

		VjdbcConnectionStringParser parser = new VjdbcConnectionStringParser("foo");

		Assert.assertEquals("llllll?foo=bar&flexMode=true", parser.decorateRootServletSql("llllll?foo=bar"));

	}

	@Test
	public void testParseFlexEndsWithFewParams()
	{

		VjdbcConnectionStringParser parser = new VjdbcConnectionStringParser("foo");

		Assert.assertEquals("llllll?foo=bar&baz=boo&flexMode=true", parser.decorateRootServletSql("llllll?foo=bar&baz=boo"));

	}


}
