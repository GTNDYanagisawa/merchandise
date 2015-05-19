/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.util;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class DiscountValueTest
{

	@Test
	public void testEquals()
	{
		DiscountValue dv1 = new DiscountValue("test", 9, true, 18, "USD");
		DiscountValue dv2 = new DiscountValue("test", 9, true, 27, "USD");
		assertFalse("DiscountValues are equal but shouldn't be", dv1.equals(dv2));
		dv1 = new DiscountValue("test", 9, true, 18, "USD");
		dv2 = new DiscountValue("test", 9, true, 18, "USD");
		assertTrue("DiscountValues aren't equal but should be", dv1.equals(dv2));
	}
}
