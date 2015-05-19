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
package de.hybris.platform.yb2bacceleratorstorefront.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class MetaSanitizerUtilTest
{

	@Test
	public void canSanitizeKeywords()
	{
		final String html = "<p>one <a href='http://example.com/two'><b>three</b></a> link.</p> <p>two</p> <p>'three'</p> <p>\"three\"</p>";
		final String expected = "one,three,link.,two,'three'";
		final String actual = MetaSanitizerUtil.sanitizeKeywords(html);

		assertEquals(expected, actual);
	}

}
