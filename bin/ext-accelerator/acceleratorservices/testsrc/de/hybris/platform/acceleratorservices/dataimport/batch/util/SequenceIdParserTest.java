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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.util.RegexParser;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link SequenceIdParser}
 */
@UnitTest
public class SequenceIdParserTest
{
	private SequenceIdParser parser;
	@Mock
	private File file;
	@Mock
	private RegexParser regexParser;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		parser = new SequenceIdParser();
		parser.setParser(regexParser);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testSyntax()
	{
		given(file.getName()).willReturn("err.csv");
		given(regexParser.parse("err.csv")).willReturn(null);
		parser.getSequenceId(file);
	}

	@Test
	public void testSuccess()
	{
		given(file.getName()).willReturn("test-1234.csv");
		given(regexParser.parse("test-1234.csv", 1)).willReturn("1234");
		Assert.assertEquals(Long.valueOf(1234L), parser.getSequenceId(file));
	}


}
