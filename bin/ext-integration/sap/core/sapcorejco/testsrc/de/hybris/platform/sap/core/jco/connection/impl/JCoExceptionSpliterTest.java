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

package de.hybris.platform.sap.core.jco.connection.impl;

import org.junit.Assert;
import org.junit.Test;

import com.sap.conn.jco.JCoException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendRuntimeException;
import de.hybris.platform.sap.core.jco.exceptions.DestinationChangedRuntimeException;
import de.hybris.platform.sap.core.jco.exceptions.JCoExceptionSpliter;


/**
 * Test class for JCoExceptionSpliter.
 */
@UnitTest
public class JCoExceptionSpliterTest
{

	@Test
	@SuppressWarnings("javadoc")
	public void testSplitExceptionErrorCodeUnknown()
	{

		final JCoException jcoEx = new JCoException(50712, "This is just a test");

		try
		{
			JCoExceptionSpliter.splitAndThrowException(jcoEx);
			Assert.fail("In case of an unknown ErrorCode an exception should be thrown");
		}
		catch (final BackendException e)
		{
			Assert.fail("BackendRuntimeException expected");
		}
		catch (final BackendRuntimeException e)
		{
			//OK
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void testSplitExceptionErrorCodeKnown()
	{

		final JCoException jcoEx = new JCoException(JCoException.JCO_ERROR_DESTINATION_DATA_INVALID, "This is just a test");

		try
		{
			JCoExceptionSpliter.splitAndThrowException(jcoEx);
			Assert.fail("In case of an known ErrorCode an exception should be thrown");
		}
		catch (final DestinationChangedRuntimeException e)
		{
			// OK
		}
		catch (final BackendException e)
		{
			Assert.fail("BackendRuntimeException expected");
		}

	}

}
