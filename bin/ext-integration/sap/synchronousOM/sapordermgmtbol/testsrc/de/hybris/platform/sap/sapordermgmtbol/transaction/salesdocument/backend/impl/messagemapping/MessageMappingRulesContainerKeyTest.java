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
public class MessageMappingRulesContainerKeyTest extends TestCase
{

	public static String cloneString(String str)
	{
		return new String(str.getBytes().clone());
	}

	static final public String //
			ID = "TST_BeClass", //
			NUM = "123", //
			SEV = "W";
	public static MessageMappingRulesContainerImpl.Key //
			empty = new MessageMappingRulesContainerImpl.Key(null, null, null), //
			emptyIdentical = new MessageMappingRulesContainerImpl.Key(null, null, null), //
			sample = new MessageMappingRulesContainerImpl.Key(ID, NUM, SEV), //
			sampleEqual = new MessageMappingRulesContainerImpl.Key(cloneString(sample.id), cloneString(sample.number),
					cloneString(sample.severity)), //
			sampleEqual2 = new MessageMappingRulesContainerImpl.Key(cloneString(sampleEqual.id), cloneString(sampleEqual.number),
					cloneString(sampleEqual.severity)), //
			oDiffCl = new MessageMappingRulesContainerImpl.Key("Z" + ID, NUM, SEV), //
			oDiffNum = new MessageMappingRulesContainerImpl.Key(ID, "999", SEV), //
			oDiffSever = new MessageMappingRulesContainerImpl.Key(ID, NUM, "I"), //
			sample2 = new MessageMappingRulesContainerImpl.Key("Z" + ID, "888", "A");

	public static MessageMappingRulesContainerImpl.Key //
			equalSet[] = new MessageMappingRulesContainerImpl.Key[]
			{ sample, sampleEqual, sampleEqual2 },
			emptySet[] = new MessageMappingRulesContainerImpl.Key[]
			{ empty, emptyIdentical }, differntSet[] = new MessageMappingRulesContainerImpl.Key[]
			{ oDiffCl, oDiffNum, oDiffSever };

	@Test
	public void testConstructor()
	{

		MessageMappingRulesContainerImpl.Key k = new MessageMappingRulesContainerImpl.Key(ID, NUM, SEV);

		assertEquals(ID, k.id);
		assertEquals(NUM, k.number);
		assertEquals(SEV, k.severity);
	}

	@Test
	public void testToString()
	{

		assertTrue(sample.toString().contains(ID));
		assertTrue(sample.toString().contains(NUM));
		assertTrue(sample.toString().contains(SEV));
	}

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

}
