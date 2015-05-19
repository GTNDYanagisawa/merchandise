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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import junit.framework.TestCase;

import org.junit.Test;


@SuppressWarnings("javadoc")
public class MessageMappingRuleResultTest extends TestCase
{

	static final public String ANY_KEY = "any_key";
	static final public Character SEVERITY_ERROR = Character.valueOf('E');

	/*
	 * final protected static int hashCodeField(Object obj ) { return (obj != null ? obj.hashCode() : 1); }
	 */

	@Test
	public void testCoonstructorDefault()
	{
		final MessageMappingRule.Result o = new MessageMappingRule.Result();

		assertEquals(false, o.isHide());
		assertNull(o.getSeverity());
		assertNull(o.getResourceKey());

	}

	@Test
	public void testCoonstructorMap()
	{
		final MessageMappingRule.Result o = new MessageMappingRule.Result(SEVERITY_ERROR, ANY_KEY, ANY_KEY, ANY_KEY);
		assertEquals(false, o.isHide());
		assertEquals(SEVERITY_ERROR, o.getSeverity());
		assertEquals(ANY_KEY, o.getResourceKey());
	}

	@Test
	public void testCoonstructorHide()
	{
		final MessageMappingRule.Result o = new MessageMappingRule.Result(Boolean.TRUE);

		assertEquals(true, o.isHide());
		assertNull(o.getSeverity());
		assertNull(o.getResourceKey());
	}

	public static MessageMappingRule.Result //
			sample = new MessageMappingRule.Result(SEVERITY_ERROR, ANY_KEY), //
			sampleEqual = new MessageMappingRule.Result(new Character(sample.getSeverity().charValue()),
					ObjectTestUtil.cloneString(sample.getResourceKey())), //
			sampleEqual2 = new MessageMappingRule.Result(new Character(sample.getSeverity().charValue()),
					ObjectTestUtil.cloneString(sample.getResourceKey())), //
			sampleDiff = new MessageMappingRule.Result(new Character('A'), "Z" + ANY_KEY), //
			sampleH = new MessageMappingRule.Result(Boolean.TRUE), //
			sampleH1 = new MessageMappingRule.Result(Boolean.TRUE), //
			sampleH2 = new MessageMappingRule.Result(Boolean.TRUE), //
			sampleHDiff = new MessageMappingRule.Result(Boolean.FALSE) //
			;

	public static MessageMappingRule.Result[] //
			equalSetM =
			{ sample, sampleEqual, sampleEqual2 }, //
			differntSetM =
			{ sampleDiff }, //
			equalSetH =
			{ sampleH, sampleH1, sampleH2 }, //
			differntSetH =
			{ sampleHDiff };

	@Test
	public void testEqualsContract()
	{
		ObjectTestUtil.assertEqualsContract(equalSetM, null, differntSetM);
		ObjectTestUtil.assertEqualsContract(equalSetH, null, differntSetH);
	}

	@Test
	public void testHashCodeContract()
	{
		ObjectTestUtil.assertHashCodeContract(equalSetM, null, differntSetM);
		ObjectTestUtil.assertHashCodeContract(equalSetH, null, differntSetH);
	}

	@Test
	public void testToString_map()
	{

		final MessageMappingRule.Result sample = new MessageMappingRule.Result(SEVERITY_ERROR, ANY_KEY);
		assertTrue(sample.toString().contains(SEVERITY_ERROR.toString()));
		assertTrue(sample.toString().contains(ANY_KEY));
	}

	@Test
	public void testToString_hide()
	{

		final MessageMappingRule.Result sample = new MessageMappingRule.Result(Boolean.TRUE);
		assertTrue(sample.toString().contains("hide"));
	}

}
