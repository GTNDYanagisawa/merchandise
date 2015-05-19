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
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test for {@link SequenceIdTranslator}
 */
@UnitTest
public class SequenceIdTranslatorTest
{
	private static final Long TEST_SEQUENCE_ID = Long.valueOf(1000L);

	private SequenceIdTranslator translator;
	@Mock
	private Item item;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		translator = new SequenceIdTranslator();
	}

	@Test
	public void testNull()
	{
		Assert.assertNull(translator.importValue(null, item));
		BDDMockito.verify(item, BDDMockito.never()).getPK();
	}

	@Test
	public void testEmpty()
	{
		Assert.assertNull(translator.importValue("", item));
		BDDMockito.verify(item, BDDMockito.never()).getPK();
	}

	@Test(expected = JaloInvalidParameterException.class)
	public void testNumberFormat()
	{
		translator.importValue("abc", item);
	}

	@Test
	public void testSequenceEmpty() throws JaloInvalidParameterException, JaloSecurityException
	{
		BDDMockito.given(item.getAttribute("sequenceId")).willReturn(null);
		Assert.assertEquals(TEST_SEQUENCE_ID, translator.importValue(TEST_SEQUENCE_ID.toString(), item));
	}

	@Test
	public void testSequenceSmaller() throws JaloInvalidParameterException, JaloSecurityException
	{
		BDDMockito.given(item.getAttribute("sequenceId")).willReturn(Long.valueOf(TEST_SEQUENCE_ID.longValue() - 1));
		Assert.assertEquals(TEST_SEQUENCE_ID, translator.importValue(TEST_SEQUENCE_ID.toString(), item));
	}

	@Test
	public void testSequenceEqual() throws JaloInvalidParameterException, JaloSecurityException
	{
		BDDMockito.given(item.getAttribute("sequenceId")).willReturn(TEST_SEQUENCE_ID);
		translator.importValue(TEST_SEQUENCE_ID.toString(), item);
		Assert.assertTrue(translator.wasUnresolved());
	}

	@Test
	public void testSequenceGreater() throws JaloInvalidParameterException, JaloSecurityException
	{
		BDDMockito.given(item.getAttribute("sequenceId")).willReturn(Long.valueOf(TEST_SEQUENCE_ID.longValue() + 1));
		translator.importValue(TEST_SEQUENCE_ID.toString(), item);
		Assert.assertTrue(translator.wasUnresolved());
	}

}
