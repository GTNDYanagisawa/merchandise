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
package de.hybris.platform.sap.core.bol.businessobject;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.exceptions.BackendCommunicationException;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.exceptions.BackendLogonException;
import de.hybris.platform.sap.core.jco.exceptions.BackendServerStartupException;
import de.hybris.platform.sap.core.jco.exceptions.BackendSystemFailureException;


/**
 * Tests for the BusinessObjectHelper and its exception split method.
 */
@UnitTest
public class BusinessObjectHelperTest
{

	/**
	 * Provokes LogonException.
	 */
	@Test
	public void testLogonException()
	{
		final BackendException ex = new BackendLogonException("Test Exception");
		try
		{
			BusinessObjectHelper.splitException(ex);
			Assert.fail("Should throw LogonException!");
		}
		catch (final LogonException e)
		{
			//ok
		}
		catch (CommunicationException | BORuntimeException e)
		{
			Assert.fail("Should not throw a " + getExceptionName(e) + "!");
		}
	}

	/**
	 * Provokes SystemFailureException.
	 */
	@Test
	public void testSystemFailureException()
	{
		final BackendException ex = new BackendSystemFailureException("Test Exception");
		try
		{
			BusinessObjectHelper.splitException(ex);
			Assert.fail("Should throw SystemFailureException!");
		}
		catch (final SystemFailureException e)
		{
			//ok
		}
		catch (CommunicationException | BORuntimeException e)
		{
			Assert.fail("Should not throw a " + getExceptionName(e) + "!");
		}
	}

	/**
	 * Provokes ServerStartupException.
	 */
	@Test
	public void testServerStartupException()
	{
		final BackendException ex = new BackendServerStartupException("Test Exception");
		try
		{
			BusinessObjectHelper.splitException(ex);
			Assert.fail("Should throw ServerStartupException!");
		}
		catch (final ServerStartupException e)
		{
			//ok
		}
		catch (CommunicationException | BORuntimeException e)
		{
			Assert.fail("Should not throw a " + getExceptionName(e) + "!");
		}
	}

	/**
	 * Provokes CommunicationException.
	 */
	@Test
	public void testCommunicationException()
	{
		final BackendException ex = new BackendCommunicationException("Test Exception");
		try
		{
			BusinessObjectHelper.splitException(ex);
			Assert.fail("Should throw CommunicationException!");
		}
		catch (final CommunicationException e)
		{
			//ok
		}
		catch (final BORuntimeException e)
		{
			Assert.fail("Should not throw a " + getExceptionName(e) + "!");
		}
	}

	/**
	 * Provokes BORuntimeException.
	 */
	@Test
	public void testBORuntimeException()
	{
		final BackendException ex = new BackendException("Test Exception");
		try
		{
			BusinessObjectHelper.splitException(ex);
			Assert.fail("Should throw BORuntimeException!");
		}
		catch (final BORuntimeException e)
		{
			//ok
		}
		catch (final CommunicationException e)
		{
			Assert.fail("Should not throw a " + getExceptionName(e) + "!");
		}
	}

	/**
	 * Cuts the exception name (without the package information) out of the class name.
	 * 
	 * @param e
	 *           the name of this exception will be returned.
	 * @return Returns the class name without the package name of the exception {@code e}.
	 */
	private String getExceptionName(final Exception e)
	{
		final String s = e.getClass().getName();
		if (s.contains("."))
		{
			return s.substring(s.lastIndexOf(".") + 1, s.length());
		}
		else
		{
			return s;
		}
	}
}
