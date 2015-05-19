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
package de.hybris.platform.virtualjdbc.servlet;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.ssi.ByteArrayServletOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.virtualjdbc.constants.VjdbcConstants;
import de.hybris.platform.virtualjdbc.misc.VjdbcHttpRequestResolver;
import de.simplicit.vjdbc.command.Command;
import de.simplicit.vjdbc.serial.CallingContext;
import de.simplicit.vjdbc.serial.UIDEx;
import de.simplicit.vjdbc.server.command.CommandProcessor;


@UnitTest
public class VjdbcPlatformServletTest
{

	private VjdbcPlatformServlet testedServlet = new VjdbcPlatformServlet();

	private ByteArrayServletOutputStream outputStream;

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private VjdbcHttpRequestResolver requestAttributeResolver;
	@Mock
	private CommandProcessor commandProcessor;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		RequestDispatcher requestDispatcher = new MethodBasedRequestDispatcher()
		{
			void assureServiceStarted() throws SQLException
			{
				// Overrided with empty implementation to get rid of booting all the platform in a unit test
			};

			@Override
			CommandProcessor getCommandProcessor()
			{
				return commandProcessor;
			}
		};

		RequestProcessor requestProcessor = new DefaultRequestProcessor(requestDispatcher);

		testedServlet.setRequestProcessor(requestProcessor);

		when(request.getAttribute(VjdbcConstants.Http.REQUEST_ATTRIBUTE_RESOLVER)).thenReturn(requestAttributeResolver);

		outputStream = new ByteArrayServletOutputStream();
	}

	@Test
	public void shouldSendRedirectWhenNoVjdbcMethodProvided() throws Exception
	{
		// when
		testedServlet.doGet(request, response);

		// then
		verify(response).sendRedirect("index.jsp");
	}

	@Test
	public void responseShouldContainUidForConnectionRequest() throws Exception
	{
		// given
		UIDEx uid = new UIDEx(123);

		givenTheResponseOutputStreamIs(outputStream);
		givenTheRequestIsAConnectRequest();
		when(requestAttributeResolver.getUIDEx()).thenReturn(uid);

		// when
		testedServlet.doGet(request, response);

		// then
		verifyResponseWasNotRedirected();

		ObjectInputStream ois = toObjectInputStream(outputStream);
		UIDEx retrievedUid = (UIDEx) ois.readObject();

		assertThat(retrievedUid).isEqualTo(uid);
	}

	@Test
	public void responseShouldContainExceptionWhenExceptionOccurs() throws Exception
	{
		// given
		givenTheResponseOutputStreamIs(outputStream);
		givenTheRequestIsAConnectRequest();

		String exceptionMessage = "!!! exception from test !!!";
		when(requestAttributeResolver.getUIDEx()).thenThrow(new RuntimeException(exceptionMessage));

		// when
		testedServlet.doGet(request, response);

		// then
		ObjectInputStream ois = toObjectInputStream(outputStream);
		Exception retrievedException = (Exception) ois.readObject();

		assertThat(retrievedException.getMessage()).contains(exceptionMessage);
	}

	@Test
	public void responseInProcessPhaseShouldContainObjectReturnedByCommandProcessor() throws Exception
	{
		// given
		String commandExecutionResult = "commandExecutionResult";

		givenTheResponseOutputStreamIs(outputStream);
		givenTheRequestIsAProcessCommandRequest();
		givenTheCommandProcessorWillReturn(commandExecutionResult);

		// when
		testedServlet.doGet(request, response);

		// then
		ObjectInputStream ois = toObjectInputStream(outputStream);
		String result = (String) ois.readObject();

		assertThat(result).isEqualTo(commandExecutionResult);
	}

	private void givenTheResponseOutputStreamIs(ByteArrayServletOutputStream outputStream) throws IOException
	{
		when(response.getOutputStream()).thenReturn(outputStream);
	}

	private void givenTheRequestIsAProcessCommandRequest()
	{
		when(request.getHeader("vjdbc-method")).thenReturn(VjdbcConstants.Http.PROCESS_ACTION);
	}

	private void givenTheRequestIsAConnectRequest()
	{
		when(request.getHeader("vjdbc-method")).thenReturn(VjdbcConstants.Http.CONNECT_ACTION);
	}

	private void givenTheCommandProcessorWillReturn(String commandExecutionResult) throws SQLException
	{
		Long connUid = 567l;
		Long uid = 123l;
		Command command = mock(Command.class);
		CallingContext callingCtx = mock(CallingContext.class);
		when(requestAttributeResolver.getConnectionUid()).thenReturn(connUid);
		when(requestAttributeResolver.getClientUid()).thenReturn(uid);
		when(requestAttributeResolver.getCommand()).thenReturn(command);
		when(requestAttributeResolver.getCallingContext()).thenReturn(callingCtx);

		when(commandProcessor.process(connUid, uid, command, callingCtx)).thenReturn(commandExecutionResult);
	}

	private void verifyResponseWasNotRedirected() throws IOException
	{
		verify(response, never()).sendRedirect("index.jsp");
	}

	private ObjectInputStream toObjectInputStream(ByteArrayServletOutputStream outputStream) throws IOException
	{
		return new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
	}
}
