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
package de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SimpleHeaderImplTest extends SapordermanagmentBolSpringJunitTest
{

	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_HEADER)
	private SimpleHeaderImpl classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		//
	}

	@Test
	public void test_constructor()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void test_getTypedExtensionMap()
	{
		final Map<String, Object> typedExtensionMap = classUnderTest.getTypedExtensionMap();
		assertNotNull(typedExtensionMap);
		typedExtensionMap.put("test_key", "test_value");
		assertEquals("test_value", typedExtensionMap.get("test_key"));
	}
}
