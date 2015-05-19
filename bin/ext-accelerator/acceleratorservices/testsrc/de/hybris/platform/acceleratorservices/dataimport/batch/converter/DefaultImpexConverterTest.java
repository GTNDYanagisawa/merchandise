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

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.impl.DefaultImpexConverter;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for {@link DefaultImpexConverter}
 */
@UnitTest
public class DefaultImpexConverterTest
{
	private static final Integer COL_0 = NumberUtils.INTEGER_ZERO;
	private static final String TEST_0 = "test0";
	private static final String TEST_DUMMY = "dummy";
	private static final Long TEST_SEQUENCE = Long.valueOf(12345L);


	private DefaultImpexConverter converter;
	private Map<Integer, String> row;

	@Before
	public void setUp()
	{
		converter = new DefaultImpexConverter();
		row = new HashMap<Integer, String>();
		row.put(COL_0, TEST_0);
	}

	@Test(expected = SystemException.class)
	public void testSyntax()
	{
		converter.setImpexRow("{");
		converter.convert(row, null);
	}

	@Test(expected = SystemException.class)
	public void testSyntax2()
	{
		converter.setImpexRow("{}");
		converter.convert(row, null);
	}

	@Test(expected = SystemException.class)
	public void testSyntax3()
	{
		converter.setImpexRow("{Err}");
		converter.convert(row, null);
	}

	@Test(expected = SystemException.class)
	public void testSyntax4()
	{
		converter.setImpexRow("{+}");
		converter.convert(row, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMandatory()
	{
		converter.setImpexRow("{+1}");
		converter.convert(row, null);
	}

	@Test
	public void testSuccess()
	{
		converter.setImpexRow("{+0}" + TEST_DUMMY);
		assertEquals(TEST_0 + TEST_DUMMY, converter.convert(row, null));
	}

	@Test
	public void testSuccess2()
	{
		converter.setImpexRow(TEST_DUMMY + "{+0}");
		assertEquals(TEST_DUMMY + TEST_0, converter.convert(row, null));
	}

	@Test
	public void testOptional()
	{
		converter.setImpexRow("{0};{1}");
		assertEquals(TEST_0 + ";", converter.convert(row, null));
	}

	@Test
	public void testEmpty()
	{
		converter.setImpexRow("{0}");
		row.clear();
		assertEquals("", converter.convert(row, null));
	}

	@Test
	public void testSequence()
	{
		converter.setImpexRow(TEST_DUMMY + "{S}" + TEST_DUMMY);
		assertEquals(TEST_DUMMY + TEST_SEQUENCE + TEST_DUMMY, converter.convert(row, TEST_SEQUENCE));
	}

	@Test
	public void shouldEscapeQuotesWhenWordWithQuotes()
	{
		converter.setImpexRow(";{0};{1};{2};{3}");

		final String escapedImpex = converter.convert(rows("test1", "\"test2\" aaa", "test3", "test4"), TEST_SEQUENCE);

		assertThat(escapedImpex).isEqualTo(";test1;\"\"\"test2\"\" aaa\";test3;test4");
	}

	@Test
	public void shouldEscapeQuotesForMultiWordColumnWhenOneWordWithQuotes()
	{
		converter.setImpexRow(";{0};{1} {2};{3}");

		final String escapedImpex = converter.convert(rows("test1", "\"test2\" aaa", "test3", "test4"), TEST_SEQUENCE);

		assertThat(escapedImpex).isEqualTo(";test1;\"\"\"test2\"\" aaa test3\";test4");
	}

	@Test
	public void shouldEscapeQuotesForMultiWordColumnWhenAllWordsWithQuotes()
	{
		converter.setImpexRow(";{0};{1} {2};{3}");

		final String escapedImpex = converter.convert(rows("test1", "\"test2\" aaa", "\"test3\"", "test4"), TEST_SEQUENCE);

		assertThat(escapedImpex).isEqualTo(";test1;\"\"\"test2\"\" aaa \"\"test3\"\"\";test4");
	}

	@Test
	public void shouldNotEscapeQuotesWhenNoQuotesInWord()
	{
		converter.setImpexRow(";{0};{1};{2};{3}");

		final String escapedImpex = converter.convert(rows("test1", "test2 aaa", "test3", "test4"), TEST_SEQUENCE);

		assertThat(escapedImpex).isEqualTo(";test1;test2 aaa;test3;test4");
	}

	@Test
	public void shouldNotEscapeQuotesWhenNoQuotesInMultiWordsColumn()
	{
		converter.setImpexRow(";{0};{1} {2};{3}");

		final String escapedImpex = converter.convert(rows("test1", "test2 aaa", "test3", "test4"), TEST_SEQUENCE);

		assertThat(escapedImpex).isEqualTo(";test1;test2 aaa test3;test4");
	}

	@Test
	public void shouldNotAddQuotesWhenNewLineInRow()
	{
		converter.setImpexRow(";1200Wx1200H;/1200Wx1200H/{+1};;;;{+1};$importFolder/1200Wx1200H/{+1} \n"
				+ ";515Wx515H;/515Wx515H/{+1};;;;{+1};$importFolder/515Wx515H/{+1} \n"
				+ ";300Wx300H;/300Wx300H/{+1};;;;{+1};$importFolder/300Wx300H/{+1} \n"
				+ ";96Wx96H;/96Wx96H/{+1};;;;{+1};$importFolder/96Wx96H/{+1} \n"
				+ ";65Wx65H;/65Wx65H/{+1};;;;{+1};$importFolder/65Wx65H/{+1} \n"
				+ ";30Wx30H;/30Wx30H/{+1};;;;{+1};$importFolder/30Wx30H/{+1}");

		final String escapedImpex = converter.convert(
				rows(";1200Wx1200H;/1200Wx1200H/test.jpg;;;;test.jpg;$importFolder/1200Wx1200H/test.jpg",
						";515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg",
						";300Wx300H;/300Wx300H/test.jpg;;;;test.jpg;$importFolder/300Wx300H/test.jpg",
						";96Wx96H;/96Wx96H/test.jpg;;;;test.jpg;$importFolder/96Wx96H/test.jpg",
						";65Wx65H;/65Wx65H/test.jpg;;;;test.jpg;$importFolder/65Wx65H/test.jpg",
						";30Wx30H;/30Wx30H/test.jpg;;;;test.jpg;$importFolder/30Wx30H/test.jpg"), TEST_SEQUENCE);

		assertThat(escapedImpex)
				.isEqualTo(
						";1200Wx1200H;/1200Wx1200H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;;;;;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;$importFolder/1200Wx1200H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg \n"
								+ ";515Wx515H;/515Wx515H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;;;;;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;$importFolder/515Wx515H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg \n"
								+ ";300Wx300H;/300Wx300H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;;;;;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;$importFolder/300Wx300H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg \n"
								+ ";96Wx96H;/96Wx96H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;;;;;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;$importFolder/96Wx96H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg \n"
								+ ";65Wx65H;/65Wx65H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;;;;;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;$importFolder/65Wx65H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg \n"
								+ ";30Wx30H;/30Wx30H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;;;;;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg;$importFolder/30Wx30H/;515Wx515H;/515Wx515H/test.jpg;;;;test.jpg;$importFolder/515Wx515H/test.jpg");
	}

	protected Map<Integer, String> rows(final String... values)
	{
		final HashMap<Integer, String> result = new HashMap<Integer, String>();
		int index = 0;
		for (final String value : values)
		{
			result.put(Integer.valueOf(index), value);
			index++;
		}
		return result;
	}

}
