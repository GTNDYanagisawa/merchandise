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
package de.hybris.platform.acceleratorservices.search.comparators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class SizeAttributeComparatorTest
{
	private SizeAttributeComparator comparator;
	private List<List<String>> sizeSystems;

	/**
	 * Creates list of size systems and comparator to put under the test
	 */
	@Before
	public void before()
	{
		/* the order of these systems in list is expected in following test - handle with care :) */
		sizeSystems = new ArrayList<List<String>>();

		final List<String> sys0 = new ArrayList<String>();
		sys0.add("A");
		sys0.add("B");
		sys0.add("C");
		sizeSystems.add(sys0);

		final List<String> sys1 = new ArrayList<String>();
		sys1.add("a1");
		sys1.add("a2");
		sys1.add("a3");
		sizeSystems.add(sys1);

		final List<String> sys2 = new ArrayList<String>();
		sys2.add("S");
		sys2.add("M");
		sys2.add("L");
		sizeSystems.add(sys2);

		final List<String> sys3 = new ArrayList<String>();
		sys3.add("A");
		sys3.add("AA");
		sys3.add("AAA");
		sizeSystems.add(sys3);

		comparator = new SizeAttributeComparator();
		comparator.setSizeSystems(sizeSystems);
		comparator.setPattern("[0-9]+(\\.[0-9])*");//it's a numeric pattern which find: "(digits).(digits)" e.g. 12.34 or 1 or 3.14159265
	}

	/**
	 * The most (interesting) important test method. Verifies if comparator respects size systems when doing comparison.
	 */
	@Test
	public void testCompare()
	{
		assertEquals("null's should rather be considered equal, don't ya think?", 0, comparator.compare(null, null));
		assertTrue("Since when 1 is bigger than 2?", comparator.compare("1", "2") < 0);
		assertTrue("B is bigger than 1!", comparator.compare("1", "B") < 0);
		assertTrue("B is bigger than 1!", comparator.compare("B", "1") > 0);
		assertTrue("B is bigger than A!", comparator.compare("A", "B") < 0);
		assertTrue("L is bigger than S!", comparator.compare("S", "L") < 0);
		assertTrue("AAA is bigger than A!", comparator.compare("A", "AAA") < 0);
		assertTrue("Any value from size sytems like 'A' is considered bigger than any number like '123'",
				comparator.compare("123", "A") < 0);
		assertTrue("'values' not from size systems should be considered bigger than anything from size system or numeric",
				comparator.compare("whatever", "2.71828183") > 0);
		assertTrue("'values' not from size systems should be considered bigger than anything from size system or numeric",
				comparator.compare("3.14159", "anything") < 0);
		assertTrue("'values' not from size systems should be considered bigger than anything from size system or numeric",
				comparator.compare("A", "anything") < 0);
		assertTrue("'values' not from size systems should be considered bigger than anything from size system or numeric",
				comparator.compare("anything", "A") > 0);
		assertEquals("both 'values' from outside size systems should considered equal", 0,
				comparator.compare("something", "anything"));
		/* comparisons among size systems */
		final int _3rdTo1st = comparator.compare("S", "B");
		assertTrue("'S' is from 3rd size system so it should be considered 'bigger' then 'B' which cames froms first size system",
				_3rdTo1st > 0);
		final int _4thTo3rd = comparator.compare("AA", "B");
		assertTrue("'AA' is from 4th size system so it should be considered 'bigger' then 'B' which cames froms first size system",
				_4thTo3rd > 0);
		assertTrue(
				"Comparing 'AA' (from 4th sys) to 'B' (1st sys) should return bigger number than comparison of 'S' and 'B' so that values from different systems are not mixed up after sorting (values from each system are hold 'together')).",
				_3rdTo1st < _4thTo3rd);
	}

	/**
	 * Test method for {@link SizeAttributeComparator#isNumber(java.lang.String)}.
	 */
	@Test
	public void testIsNumber()
	{
		final boolean number = comparator.isNumber("123.456");
		assertTrue("123.456 was not found", number);
		final boolean notNumber = comparator.isNumber("notANumber");
		assertFalse("'notANumber' string is recognized as number", notNumber);
	}

	/**
	 * Test method for {@link SizeAttributeComparator#getSizeSystemIndex(java.lang.String)} .
	 */
	@Test
	public void testGetSizeSystemIndex()
	{
		/* just find existing 'L' in one of systems */
		final int lIndex = comparator.getSizeSystemIndex("L");
		assertEquals("'L' was found in system: " + lIndex, 2, lIndex);//2 is index of sys2

		/* two systems contains 'A', first one should be returned */
		final int aIndex = comparator.getSizeSystemIndex("A");
		assertEquals("'A' was found in system: " + aIndex + " but should be in 0", 0, aIndex);//0 is index of sys0

		/* 'Z' doesn't exist in either system should return -1 */
		final int zIndex = comparator.getSizeSystemIndex("Z");
		assertEquals("'Z' doesn't exist in either system should return -1", -1, zIndex);
	}

	/**
	 * Test method for {@link SizeAttributeComparator#numericCompare(java.lang.String, java.lang.String)} .
	 */
	@Test
	public void testNumericCompare()
	{
		assertTrue("Since when 1 is bigger than 2?", comparator.numericCompare("1", "2") < 0);
		assertTrue("Not-numbers should be always treated as bigger than numbers.", comparator.numericCompare("whatever", "2") > 0);
		assertTrue("Not-numbers should be always treated as bigger than numbers.", comparator.numericCompare("42", "whatever") < 0);
	}

	/**
	 * It's actually testing regexp but anyway:
	 */
	@Test
	public void testGetNumber()
	{
		final double one = comparator.getNumber("a1");
		assertEquals(1, one, 0);
		final double oneHalf = comparator.getNumber("a1.5b");
		assertEquals(1.5, oneHalf, 0.01);
	}

	/**
	 * Test method for {@link SizeAttributeComparator#getPattern()}.
	 */
	@Test
	public void testGetPattern()
	{
		final String pattern = "whatever";
		comparator.setPattern(pattern);
		assertEquals("Comparator shouldn't modify pattern!", pattern, comparator.getPattern());
	}

	/**
	 * Test method for {@link SizeAttributeComparator#getSizeSystems()}.
	 */
	@Test
	public void testGetSizeSystems()
	{
		comparator.setSizeSystems(sizeSystems);
		assertEquals("Size systems malformed", sizeSystems, comparator.getSizeSystems());
	}
}
