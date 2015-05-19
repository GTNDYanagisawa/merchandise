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
package de.hybris.platform.acceleratorservices.dataimport.batch.decorator;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link CustomerNameDecorator}
 */
@UnitTest
public class CustomerNameDecoratorTest
{
	private static final String EMAIL = "testCustomer@test.com";
	private static final String EXPECTED_NAME = "testCustomer";
	private static final String EMPTY_NAME = "    ";
	private static final int NAME_POSITION_INT = 1;
	private static final Integer NAME_POSITION = Integer.valueOf(NAME_POSITION_INT);
	private static final Integer EMAIL_POSITION = Integer.valueOf(NAME_POSITION_INT - 1);

	private final CustomerNameDecorator decorator = new CustomerNameDecorator();

	@Test
	public void decorateTest()
	{
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(EMAIL_POSITION, EMAIL);
		srcLine.put(NAME_POSITION, null);
		String decoratedName = decorator.decorate(NAME_POSITION_INT, srcLine);
		Assert.assertEquals(EXPECTED_NAME, decoratedName);

		srcLine.put(NAME_POSITION, "");
		decoratedName = decorator.decorate(NAME_POSITION_INT, srcLine);
		Assert.assertEquals(EXPECTED_NAME, decoratedName);

		srcLine.put(NAME_POSITION, "    ");
		decoratedName = decorator.decorate(NAME_POSITION_INT, srcLine);
		Assert.assertEquals(EXPECTED_NAME, decoratedName);

	}

	@Test
	public void lackOfEmailValueTest()
	{
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(NAME_POSITION, null);
		String decoratedName = decorator.decorate(NAME_POSITION_INT, srcLine);
		Assert.assertNull(decoratedName);

		srcLine.put(NAME_POSITION, EMPTY_NAME);
		decoratedName = decorator.decorate(NAME_POSITION_INT, srcLine);
		Assert.assertEquals(EMPTY_NAME, decoratedName);
	}

	@Test
	public void notEmptyNameTest()
	{
		final String customerName = "customer";
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(EMAIL_POSITION, EMAIL);
		srcLine.put(NAME_POSITION, customerName);

		final String decoratedName = decorator.decorate(NAME_POSITION_INT, srcLine);
		Assert.assertEquals(customerName, decoratedName);
	}
}
