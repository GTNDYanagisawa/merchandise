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
package de.hybris.platform.sap.core.bol.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test Class for {@link GenericCacheKey}.
 */
@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "/test/SAPCacheTest-context.xml" })
public class GenericCacheKeyTest
{

	private final Object[] key = new String[]
	{ "structureName", "fieldName" };
	private final Object[] key2 = new String[]
	{ "abc", "123" };
	private final String typeCode = new String("carrierTypeCode");
	private final GenericCacheKey genericCacheKey1 = new GenericCacheKey(key, typeCode);
	private final GenericCacheKey genericCacheKey2 = new GenericCacheKey(key, typeCode);
	private final GenericCacheKey genericCacheKey3 = new GenericCacheKey(key2, typeCode);

	/**
	 * Equals test.
	 */
	@Test
	public void testEquals()
	{
		assertTrue(genericCacheKey1.equals(genericCacheKey2));
		assertFalse(genericCacheKey1.equals(genericCacheKey3));
	}

	/**
	 * type code test.
	 */
	@Test
	public void testTypeCode()
	{
		assertEquals(typeCode, genericCacheKey1.getTypeCode());
	}

}
