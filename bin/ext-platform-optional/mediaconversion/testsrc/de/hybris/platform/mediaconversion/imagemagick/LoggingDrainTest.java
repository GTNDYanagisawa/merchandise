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
package de.hybris.platform.mediaconversion.imagemagick;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author pohl
 */
@UnitTest
public class LoggingDrainTest
{
	@Test
	public void testNullTolerance()
	{
		final LoggingDrain drain = new LoggingDrain(this.getClass(), Level.DEBUG);
		drain.drain("Test output only...");
		drain.drain(null);
		Assert.assertNotNull(drain);
	}
}
