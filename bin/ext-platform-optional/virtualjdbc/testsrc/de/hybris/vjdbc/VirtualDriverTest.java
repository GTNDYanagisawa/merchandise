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

package de.hybris.vjdbc;

import java.sql.SQLException;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.google.common.collect.ImmutableMap;

import de.hybris.bootstrap.annotations.UnitTest;
import de.simplicit.vjdbc.command.CommandSink;


@UnitTest
public class VirtualDriverTest
{

	private static final Logger LOG = Logger.getLogger(VirtualDriverTest.class);
	private static final ImmutableMap<String, String> OF_EMPTY_MAP = ImmutableMap.<String, String> of();

	@Mock
	private CommandSinkProvider commandSinkProvider;
	@Mock
	private CommandSink commandSink;

	@Spy
	private VirtualConnectionBuilder virtualConnectionBuilder = new VirtualConnectionBuilder();

	@Spy
	private VirtualDriver driver = new VirtualDriver();

	@Before
	public void prepare() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(driver.getVirtualConnectionBuilder()).thenReturn(virtualConnectionBuilder);
		Mockito.when(virtualConnectionBuilder.getCommandSinkProvider()).thenReturn(commandSinkProvider);

	}

	@Test
	public void testConnectWithInvalidUrl() throws Exception
	{
		try
		{
			driver.connect("foobar", null);
			Assert.fail("should have failed");
		}
		catch (SQLException e)
		{
			assertFirstLineOfSQLException(
					"Unsupported url format. Expected 'jdbc:hybris:[flexiblesearch:|sql:]<some url>' but got :foobar. Refer to documentation for details",
					e);
		}

		Mockito.verifyZeroInteractions(virtualConnectionBuilder);
	}

	@Test
	public void testConnectWithAcceptedIncorrectUrl() throws Exception
	{
		try
		{
			driver.connect("jdbc:hybris:foobar//baz", null);
			Assert.fail("should have failed");
		}
		catch (SQLException e)
		{
			assertFirstLineOfSQLException("java.sql.SQLException: Unknown protocol identifier foobar//baz", e);
		}

		Mockito.verify(virtualConnectionBuilder).setProperties(null);
		Mockito.verify(virtualConnectionBuilder, Mockito.times(0)).setDataSourceString(Mockito.anyString());
		Mockito.verify(virtualConnectionBuilder, Mockito.times(0)).setUrl("");
	}

	@Test
	public void testConnectWithAcceptedSQLUrl() throws Exception
	{
		final Properties props = new Properties();

		Mockito.when(commandSinkProvider.create(Mockito.eq("//baz"), Mockito.eq(OF_EMPTY_MAP))).thenThrow(new ExpectedException());

		try
		{
			driver.connect("jdbc:hybris:sql://baz", props);
			Assert.fail("should have failed");
		}
		catch (SQLException e)
		{
			assertFirstLineOfSQLException("de.hybris.vjdbc.VirtualDriverTest$ExpectedException", e);
		}

		//Mockito.verify(commandSinkProvider).get(Mockito.eq("//baz"), Mockito.eq(ImmutableMap.<String, String>of()));

		Mockito.verify(virtualConnectionBuilder).setProperties(props);
		Mockito.verify(virtualConnectionBuilder, Mockito.times(0)).setUrl("");
		Mockito.verify(virtualConnectionBuilder).setDataSourceString(
				Mockito.eq(VjdbcConnectionStringParser.VJDBC_DEFAULT_DATASOURCE));
	}

	@Test
	public void testConnectWithAcceptedSQLWithArgsUrl() throws Exception
	{
		final Properties props = new Properties();

		Mockito.when(commandSinkProvider.create(Mockito.eq("//baz"), Mockito.eq(OF_EMPTY_MAP))).thenThrow(new ExpectedException());

		try
		{
			driver.connect("jdbc:hybris:sql://baz;someparam=X", props);
			Assert.fail("should have failed");
		}
		catch (SQLException e)
		{
			assertFirstLineOfSQLException("de.hybris.vjdbc.VirtualDriverTest$ExpectedException", e);
		}



		Mockito.verify(virtualConnectionBuilder).setProperties(props);
		Mockito.verify(virtualConnectionBuilder, Mockito.times(0)).setUrl("");
		Mockito.verify(virtualConnectionBuilder).setDataSourceString(Mockito.eq("someparam=X"));

	}

	@Test
	public void testConnectWithAcceptedFlexUrl() throws Exception
	{
		final Properties props = new Properties();

		Mockito.when(commandSinkProvider.create(Mockito.eq("//baz?flexMode=true"), Mockito.eq(OF_EMPTY_MAP))).thenThrow(
				new ExpectedException());

		try
		{
			driver.connect("jdbc:hybris:flexiblesearch://baz", props);
			Assert.fail("should have failed");
		}
		catch (SQLException e)
		{
			assertFirstLineOfSQLException("de.hybris.vjdbc.VirtualDriverTest$ExpectedException", e);
		}



		Mockito.verify(virtualConnectionBuilder).setProperties(props);
		Mockito.verify(virtualConnectionBuilder, Mockito.times(0)).setUrl("");
		Mockito.verify(virtualConnectionBuilder).setDataSourceString(
				Mockito.eq(VjdbcConnectionStringParser.VJDBC_DEFAULT_DATASOURCE));
	}


	@Test
	public void testConnectWithAcceptedFlexUrlWithCustomDataSource() throws Exception
	{
		final Properties props = new Properties();

		Mockito.when(
				commandSinkProvider.create(Mockito.eq("http://localhost:9001/vjdbc/vjdbcServlet?tenant=my&flexMode=true"),
						Mockito.eq(OF_EMPTY_MAP))).thenThrow(new ExpectedException());

		try
		{
			driver.connect("jdbc:hybris:flexiblesearch:http://localhost:9001/vjdbc/vjdbcServlet?tenant=my,booSystem", props);
			Assert.fail("should have failed");
		}
		catch (SQLException e)
		{
			assertFirstLineOfSQLException("de.hybris.vjdbc.VirtualDriverTest$ExpectedException", e);
		}



		Mockito.verify(virtualConnectionBuilder).setProperties(props);
		Mockito.verify(virtualConnectionBuilder, Mockito.times(0)).setUrl("");
		Mockito.verify(virtualConnectionBuilder).setDataSourceString(Mockito.eq("booSystem"));
	}


	@Test
	public void testConnectWithAcceptedFlexUrlWithSomeParams() throws Exception
	{
		final Properties props = new Properties();

		Mockito.when(
				commandSinkProvider.create(Mockito.eq("http://localhost:9001/vjdbc/vjdbcServlet?tenant=my&param=X&flexMode=true"),
						Mockito.eq(OF_EMPTY_MAP))).thenThrow(new ExpectedException());

		try
		{
			driver.connect("jdbc:hybris:flexiblesearch:http://localhost:9001/vjdbc/vjdbcServlet?tenant=my&param=X,booSystem", props);
			Assert.fail("should have failed");
		}
		catch (SQLException e)
		{
			assertFirstLineOfSQLException("de.hybris.vjdbc.VirtualDriverTest$ExpectedException", e);
		}

		Mockito.verify(virtualConnectionBuilder).setProperties(props);
		Mockito.verify(virtualConnectionBuilder, Mockito.times(0)).setUrl("");
		Mockito.verify(virtualConnectionBuilder).setDataSourceString(Mockito.eq("booSystem"));
	}


	private void assertFirstLineOfSQLException(final String message, final SQLException exception)
	{
		Assert.assertEquals("Expected to have message starting with [" + message + "] but got " + exception.getMessage(), message,
				StringUtils.split(exception.getMessage(), System.lineSeparator())[0]);
	}

	class ExpectedException extends RuntimeException
	{
		//
	}


}
