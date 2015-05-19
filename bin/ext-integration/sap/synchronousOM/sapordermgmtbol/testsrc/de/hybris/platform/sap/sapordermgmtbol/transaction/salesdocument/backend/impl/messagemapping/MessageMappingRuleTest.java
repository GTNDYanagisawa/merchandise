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
public class MessageMappingRuleTest extends TestCase
{

	@Test
	public void testEqualsFields()
	{
		assertTrue(MessageMappingRule.equalsField(null, null));
		assertFalse(MessageMappingRule.equalsField("A", null));
		assertFalse(MessageMappingRule.equalsField(null, "A"));
		assertTrue(MessageMappingRule.equalsField("A", "A"));
		assertFalse(MessageMappingRule.equalsField("A", "B"));
		assertFalse(MessageMappingRule.equalsField("B", "A"));
	}

	@Test
	public void testHashCodeFields()
	{
		final String obj = "X";
		assertEquals(1, MessageMappingRule.hashCodeField(null));
		assertEquals("X".hashCode(), MessageMappingRule.hashCodeField(obj));
	}

	@Test
	public void testComposites()
	{

		final MessageMappingRule rule = new MessageMappingRule();

		assertNotNull(rule);
		assertNotNull(rule.getPattern());
		assertNotNull(rule.getResult());
	}

	@Test
	public void testConstructor()
	{

		final MessageMappingRule rule = new MessageMappingRule(new MessageMappingRule.Pattern(
				MessageMappingRulePatternTest.BE_CLASS, MessageMappingRulePatternTest.BE_NUMBER,
				MessageMappingRulePatternTest.BE_SEVERITY), new MessageMappingRule.Result(
				MessageMappingRuleResultTest.SEVERITY_ERROR, MessageMappingRuleResultTest.ANY_KEY));

		assertEquals(MessageMappingRulePatternTest.BE_CLASS, rule.getPattern().getBeClass());
		assertEquals(MessageMappingRulePatternTest.BE_NUMBER, rule.getPattern().getBeNumber());
		assertEquals(MessageMappingRulePatternTest.BE_SEVERITY, rule.getPattern().getBeSeverity());
		assertEquals(MessageMappingRuleResultTest.SEVERITY_ERROR, rule.getResult().getSeverity());
		assertEquals(MessageMappingRuleResultTest.ANY_KEY, rule.getResult().getResourceKey());

	}

	@Test
	public void testConstructorParts()
	{
		final MessageMappingRule.Pattern pattern = new MessageMappingRule.Pattern(MessageMappingRulePatternTest.BE_CLASS,
				MessageMappingRulePatternTest.BE_NUMBER, MessageMappingRulePatternTest.BE_SEVERITY);
		final MessageMappingRule.Result result = new MessageMappingRule.Result(MessageMappingRuleResultTest.SEVERITY_ERROR,
				MessageMappingRuleResultTest.ANY_KEY);
		final MessageMappingRule rule = new MessageMappingRule(pattern, result);

		assertEquals(MessageMappingRulePatternTest.BE_CLASS, rule.getPattern().getBeClass());
		assertEquals(MessageMappingRulePatternTest.BE_NUMBER, rule.getPattern().getBeNumber());
		assertEquals(MessageMappingRulePatternTest.BE_SEVERITY, rule.getPattern().getBeSeverity());
		assertEquals(MessageMappingRuleResultTest.SEVERITY_ERROR, rule.getResult().getSeverity());
		assertEquals(MessageMappingRuleResultTest.ANY_KEY, rule.getResult().getResourceKey());

	}

	@Test
	public void testToString()
	{

		final MessageMappingRule.Pattern pattern = new MessageMappingRule.Pattern(MessageMappingRulePatternTest.BE_CLASS,
				MessageMappingRulePatternTest.BE_NUMBER, MessageMappingRulePatternTest.BE_SEVERITY);
		final MessageMappingRule.Result result = new MessageMappingRule.Result(Boolean.TRUE);
		final MessageMappingRule rule = new MessageMappingRule(pattern, result);

		assertTrue(rule.toString().contains(rule.getPattern().toString()));
		assertTrue(rule.toString().contains(rule.getResult().toString()));
	}

}
