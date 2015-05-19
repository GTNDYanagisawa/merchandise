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
package de.hybris.platform.sap.core.common.util;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * Test class for LogDebug.
 */
@UnitTest
public class LogDebugTest
{

	@Test
	@SuppressWarnings("javadoc")
	public void testLogDebug()
	{

		final TestLogger testLog = new TestLogger("");


		final String msg = "The quick brown {0} jumps over the lazy {1} {2} times";
		LogDebug.debug(testLog, msg, "fox", "dogs", 6);

		Assert.assertNotNull(testLog.message);
		Assert.assertEquals("The quick brown fox jumps over the lazy dogs 6 times", testLog.message);
	}


	/**
	 * Test logger.
	 */
	public static class TestLogger extends Logger
	{
		/**
		 * Flag indicating if debug mode is set.
		 */
		public boolean isDebugEnabled = true; //NOPMD
		/**
		 * Receives a message.
		 */
		public String message = null;//NOPMD

		/**
		 * Constructor.
		 * 
		 * @param name
		 *           name
		 */
		public TestLogger(final String name)
		{
			super(name);
		}

		@Override
		public boolean isDebugEnabled()
		{
			return isDebugEnabled;
		}

		@Override
		public void debug(final Object message)
		{
			this.message = message.toString();
		}


	}
}
