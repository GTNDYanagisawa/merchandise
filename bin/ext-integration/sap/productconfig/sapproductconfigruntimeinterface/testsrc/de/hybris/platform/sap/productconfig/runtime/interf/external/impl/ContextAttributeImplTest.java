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
package de.hybris.platform.sap.productconfig.runtime.interf.external.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class ContextAttributeImplTest
{
	ContextAttributeImpl classUnderTest = new ContextAttributeImpl();



	@Test
	public void testContextAttributeImplAttributes()
	{
		final String name = "VBAK-MATNR";
		classUnderTest.setName(name);
		assertEquals(name, classUnderTest.getName());
		final String value = "KD990MIX";
		classUnderTest.setValue(value);
		assertEquals(value, classUnderTest.getValue());
	}

}
