package de.hybris.platform.testframework;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

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
public class SampleTestCornerCaseBeforeAssume
{
	private static final Logger LOG = Logger.getLogger(SampleTestCornerCaseBeforeAssume.class);

	@Before
	public void prepare()
	{
		Assume.assumeTrue(false);
	}

	@Test
	public void test()
	{
		Assert.assertTrue(true);
	}

}
