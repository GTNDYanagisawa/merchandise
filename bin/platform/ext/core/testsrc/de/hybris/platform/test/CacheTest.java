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
package de.hybris.platform.test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import org.apache.log4j.Logger;
import org.junit.Test;



@IntegrationTest
public class CacheTest extends HybrisJUnit4Test
{
	private static final Logger log = Logger.getLogger(CacheTest.class.getName());

	@Test
	public void testGetUIDPerformance()
	{
		final User user = JaloSession.getCurrentSession().getUser();
		user.getUID();
		long startTime = System.nanoTime();
		for (int i = 0; i < 10000000; i++)
		{
			user.getUID();
		}
		startTime = (System.nanoTime() - startTime) / 1000 / 1000;
		log.info("Calling 10.000.000 times getUID(): " + startTime + "ms");
	}
}
