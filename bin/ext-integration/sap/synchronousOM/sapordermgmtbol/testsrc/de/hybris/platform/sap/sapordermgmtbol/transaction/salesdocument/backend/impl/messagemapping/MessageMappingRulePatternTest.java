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
public class MessageMappingRulePatternTest extends TestCase
{

	static public String //
			BE_CLASS = "TST_BeClass", //
			BE_NUMBER = "123", //
			BE_SEVERITY = "W", //
			BE_V1 = "a1", //
			BE_V2 = "a2", //
			BE_V3 = "a3", //
			BE_V4 = "a4", //
			BE_STAR = "*";

	public static MessageMappingRule.Pattern clonePattern(MessageMappingRule.Pattern obj)
	{
		return new MessageMappingRule.Pattern(ObjectTestUtil.cloneString(obj.getBeClass()), ObjectTestUtil.cloneString(obj
				.getBeNumber()), ObjectTestUtil.cloneString(obj.getBeSeverity()), ObjectTestUtil.cloneString(obj.getBeV1()),
				ObjectTestUtil.cloneString(obj.getBeV2()), ObjectTestUtil.cloneString(obj.getBeV3()), ObjectTestUtil.cloneString(obj
						.getBeV4()));
	}

	@Test
	public void testFieldsAdjustment()
	{
		assertEquals(null, MessageMappingRule.Pattern.agjustField(null));
		assertEquals(null, MessageMappingRule.Pattern.agjustField("*"));
		assertEquals(null, MessageMappingRule.Pattern.agjustField(" * "));
		assertSame(BE_CLASS, MessageMappingRule.Pattern.agjustField(BE_CLASS));
	}

	@Test
	public void testConstructorDefault()
	{

		MessageMappingRule.Pattern pattern = new MessageMappingRule.Pattern();

		assertNull(pattern.getBeClass());
		assertNull(pattern.getBeNumber());
		assertNull(pattern.getBeSeverity());
		assertNull(pattern.getBeV1());
		assertNull(pattern.getBeV2());
		assertNull(pattern.getBeV3());
		assertNull(pattern.getBeV4());
	}

	@Test
	public void testConstructorWithValues()
	{

		MessageMappingRule.Pattern pattern = new MessageMappingRule.Pattern(BE_CLASS, BE_NUMBER, BE_SEVERITY);

		assertEquals(BE_CLASS, pattern.getBeClass());
		assertEquals(BE_NUMBER, pattern.getBeNumber());
		assertEquals(BE_SEVERITY, pattern.getBeSeverity());
		assertNull(pattern.getBeV1());
		assertNull(pattern.getBeV2());
		assertNull(pattern.getBeV3());
		assertNull(pattern.getBeV4());
	}

	@Test
	public void testConstructorWithValuesAndArgs()
	{

		MessageMappingRule.Pattern pattern = new MessageMappingRule.Pattern(BE_CLASS, BE_NUMBER, BE_SEVERITY, BE_V1, BE_V2, BE_V3,
				BE_V4);

		assertEquals(BE_CLASS, pattern.getBeClass());
		assertEquals(BE_NUMBER, pattern.getBeNumber());
		assertEquals(BE_SEVERITY, pattern.getBeSeverity());
		assertEquals(BE_V1, pattern.getBeV1());
		assertEquals(BE_V2, pattern.getBeV2());
		assertEquals(BE_V3, pattern.getBeV3());
		assertEquals(BE_V4, pattern.getBeV4());
	}

	@Test
	public void testConstructorWithAllStars()
	{

		MessageMappingRule.Pattern pattern = new MessageMappingRule.Pattern(BE_STAR, BE_STAR, BE_STAR, BE_STAR, BE_STAR, BE_STAR,
				BE_STAR);

		assertNull(pattern.getBeClass());
		assertNull(pattern.getBeNumber());
		assertNull(pattern.getBeSeverity());
		assertNull(pattern.getBeV1());
		assertNull(pattern.getBeV2());
		assertNull(pattern.getBeV3());
		assertNull(pattern.getBeV4());
	}

	@Test
	public void testToString()
	{

		assertTrue(sample.toString().contains(BE_CLASS));
		assertTrue(sample.toString().contains(BE_NUMBER));
		assertTrue(sample.toString().contains(BE_SEVERITY));
		assertTrue(sample.toString().contains(BE_V1));
		assertTrue(sample.toString().contains(BE_V2));
		assertTrue(sample.toString().contains(BE_V3));
		assertTrue(sample.toString().contains(BE_V4));
	}

	public static MessageMappingRule.Pattern //
			empty = new MessageMappingRule.Pattern(null, null, null), //
			emptyIdentical = new MessageMappingRule.Pattern(null, null, null), //
			sample = new MessageMappingRule.Pattern(BE_CLASS, BE_NUMBER, BE_SEVERITY, BE_V1, BE_V2, BE_V3, BE_V4), //
			sampleEqual = clonePattern(sample), //
			sampleEqual2 = clonePattern(sampleEqual), //
			oDiffCl = new MessageMappingRule.Pattern("Z" + BE_CLASS, BE_NUMBER, BE_SEVERITY), //
			oDiffNum = new MessageMappingRule.Pattern(BE_CLASS, "999", BE_SEVERITY), //
			oDiffSever = new MessageMappingRule.Pattern(BE_CLASS, BE_NUMBER, "I"), //
			oDiffV1 = new MessageMappingRule.Pattern(BE_CLASS, BE_NUMBER, BE_SEVERITY, "Z" + BE_V1, BE_V2, BE_V3, BE_V4), //
			oDiffV2 = new MessageMappingRule.Pattern(BE_CLASS, BE_NUMBER, BE_SEVERITY, BE_V1, "Z" + BE_V2, BE_V3, BE_V4), //
			oDiffV3 = new MessageMappingRule.Pattern(BE_CLASS, BE_NUMBER, BE_SEVERITY, BE_V1, BE_V2, "Z" + BE_V3, BE_V4), //
			oDiffV4 = new MessageMappingRule.Pattern(BE_CLASS, BE_NUMBER, BE_SEVERITY, BE_V1, BE_V2, BE_V3, "Z" + BE_V4), //
			sample2 = new MessageMappingRule.Pattern("Z" + BE_CLASS, "888", "A");

	public static MessageMappingRule.Pattern[] //
			equalSet =
			{ sample, sampleEqual, sampleEqual2 }, //
			emptySet =
			{ empty, emptyIdentical }, //
			differntSet =
			{ oDiffCl, oDiffNum, oDiffSever, oDiffV1, oDiffV2, oDiffV3, oDiffV4, sample2 };

	@Test
	public void testHashCodeContract()
	{
		ObjectTestUtil.assertHashCodeContract(equalSet, emptySet, differntSet);
	}

	@Test
	public void testEqualsContract()
	{
		ObjectTestUtil.assertEqualsContract(equalSet, emptySet, differntSet);
	}

	public static MessageMappingRule.Pattern //
			sampleArgxxxx = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity), //
			sampleArgxxx1 = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, null, null, null,
					BE_V4), //
			sampleArgxx1x = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, null, null, BE_V3,
					null), //
			sampleArgx1xx = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, null, BE_V2, null,
					null), //
			sampleArg1xxx = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, BE_V1, null, null,
					null), //
			sampleArgxx11 = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, null, null, BE_V3,
					BE_V4), //
			sampleArgx1x1 = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, null, BE_V2, null,
					BE_V4), //
			sampleArg1xx1 = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, BE_V1, null, null,
					BE_V4), //
			sampleArg11xx = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, BE_V1, BE_V2, null,
					null), //
			sampleArg1111 = new MessageMappingRule.Pattern(sample.beClass, sample.beNumber, sample.beSeverity, BE_V1, BE_V2, BE_V3,
					BE_V4);

	@Test
	public void testAttrDegree()
	{
		// class, number and severity is ignored
		assertEquals(0x00, sampleArgxxxx.attrDergee());
		assertEquals(0x11, sampleArgxxx1.attrDergee());
		assertEquals(0x12, sampleArgxx1x.attrDergee());
		assertEquals(0x14, sampleArgx1xx.attrDergee());
		assertEquals(0x18, sampleArg1xxx.attrDergee());
		assertEquals(0x23, sampleArgxx11.attrDergee());
		assertEquals(0x25, sampleArgx1x1.attrDergee());
		assertEquals(0x29, sampleArg1xx1.attrDergee());
		assertEquals(0x2C, sampleArg11xx.attrDergee());
		assertEquals(0x4F, sampleArg1111.attrDergee());
	}

	@Test
	public void testMatchCNS_Stars()
	{
		// null case too
		assertTrue(new MessageMappingRule.Pattern(null, "001", "W").matchCNS("arbitrary", "001", "W"));

		// empty case too
		assertTrue(new MessageMappingRule.Pattern("class", "", "W").matchCNS("class", "001", "W"));

		// '*' case too
		assertTrue(new MessageMappingRule.Pattern("class", "001", "*").matchCNS("class", "001", "W"));
	}

	@Test
	public void testPatternMatchCNS_StarsRule()
	{
		MessageMappingRule.Pattern o = new MessageMappingRule.Pattern("*", "*", "*");
		assertTrue(o.matchCNS("class", "001", "W"));
	}

	@Test
	public void testPatternMatchCNS_NullRule()
	{
		MessageMappingRule.Pattern o = new MessageMappingRule.Pattern(null, null, null);
		assertTrue(o.matchCNS("class", "001", "W"));
	}

	@Test
	public void testPatternMatchCNS_3Concrete()
	{
		MessageMappingRule.Pattern o = new MessageMappingRule.Pattern("class", "001", "W");
		assertTrue(o.matchCNS("class", "001", "W"));
	}

	@Test
	public void testPatternUnMatchCNS_3Concrete()
	{
		MessageMappingRule.Pattern o = new MessageMappingRule.Pattern("class", "001", "W");
		assertFalse(o.matchCNS("class1", "001", "W"));
		assertFalse(o.matchCNS("class", "002", "W"));
		assertFalse(o.matchCNS("class", "001", "E"));
	}

	@Test
	public void testPatternMatchVARS_Stars()
	{
		assertTrue(sampleArgxxxx.matchVARS("anything1", "anything2", "anything3", "anything4"));
	}

	@Test
	public void testPatternMatchVARS_VarPositions()
	{
		assertTrue(sampleArg1xxx.matchVARS(BE_V1, "anything2", "anything3", "anything4"));
		assertTrue(sampleArgx1xx.matchVARS("anything2", BE_V2, "anything3", "anything4"));
		assertTrue(sampleArgxx1x.matchVARS("anything2", "anything3", BE_V3, "anything4"));
		assertTrue(sampleArgxxx1.matchVARS("anything2", "anything3", "anything4", BE_V4));
	}

	@Test
	public void testPatternMatchVARS_All()
	{
		assertTrue(sampleArg1111.matchVARS(BE_V1, BE_V2, BE_V3, BE_V4));
		assertFalse(sampleArg1111.matchVARS("err", BE_V2, BE_V3, BE_V4));
		assertFalse(sampleArg1111.matchVARS(BE_V1, "err", BE_V3, BE_V4));
		assertFalse(sampleArg1111.matchVARS(BE_V1, BE_V2, "err", BE_V4));
		assertFalse(sampleArg1111.matchVARS(BE_V1, BE_V2, BE_V3, "err"));
	}

	@Test
	public void testPatternMatch()
	{
		assertTrue(sample.match(BE_CLASS, BE_NUMBER, BE_SEVERITY, BE_V1, BE_V2, BE_V3, BE_V4));
	}

}
