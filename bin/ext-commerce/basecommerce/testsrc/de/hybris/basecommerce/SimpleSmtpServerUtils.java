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
package de.hybris.basecommerce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.dumbster.smtp.SmtpActionType;
import com.dumbster.smtp.SmtpMessage;
import com.dumbster.smtp.SmtpRequest;
import com.dumbster.smtp.SmtpResponse;
import com.dumbster.smtp.SmtpState;


/**
 *
 */
public class SimpleSmtpServerUtils
{
	private static final Logger LOG = Logger.getLogger(SimpleSmtpServerUtils.class);

	public interface SimpleSmtpServer
	{
		int getPort();

		boolean isStopped();

		void stop();

		int getReceivedEmailSize();

		@Deprecated
		Iterator<SmtpMessage> getReceivedEmail();

		Collection<SmtpMessage> getReceivedEmails();
	}

	public static SimpleSmtpServer startServer()
	{
		return startServer(DummySmtpServer.DEFAULT_SMTP_PORT);
	}

	public static SimpleSmtpServer startServer(final int port)
	{
		return startServer(port, true);
	}

	public static SimpleSmtpServer startServer(final int port, final boolean findOpenPort)
	{
		try
		{
			return DummySmtpServer.start(findOpenPort ? getNextOpenPort(port, 100) : port, 15);
		}
		catch (final TimeoutException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static int getNextOpenPort(final int startingPort)
	{
		return getNextOpenPort(startingPort, 100);
	}

	public static int getNextOpenPort(final int startingPort, final int range)
	{
		final int endRange = startingPort + range;
		for (int port = startingPort; port < endRange; port++)
		{
			if (available(port))
			{
				LOG.info("Got open port:" + port + " (starting port:" + startingPort + ")");
				return port;
			}
		}
		throw new IllegalArgumentException("Could not find any open port in [" + startingPort + "-" + (endRange - 1) + "]");
	}

	private static boolean available(final int port)
	{
		ServerSocket serverSocket = null;
		try
		{
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			return true;
		}
		catch (final IOException e)
		{
			//
		}
		finally
		{
			if (serverSocket != null)
			{
				try
				{
					serverSocket.close();
				}
				catch (final IOException e)
				{
					/* should not be thrown */
				}
			}
		}
		return false;
	}

	// patched version of com.dumbster.smtp.SimpleSmtpServer which was suffering
	// from some serious hanging thread issue when starting up the server !!!
	private static class DummySmtpServer implements Runnable, SimpleSmtpServer
	{
		private final List receivedMail;
		public static final int DEFAULT_SMTP_PORT = 25;
		private volatile boolean stopped = true;
		private ServerSocket serverSocket;
		private volatile boolean socketSuccess = false;
		private int port = DEFAULT_SMTP_PORT;
		private static final int TIMEOUT = 500;

		private final CountDownLatch startupBarrier;

		public DummySmtpServer(final int port)
		{
			this.receivedMail = new ArrayList();
			this.port = port;
			this.startupBarrier = new CountDownLatch(1);
		}

		@Override
		public int getPort()
		{
			return port;
		}

		boolean waitForServerSocketCreated(final int timeoutSeconds) throws InterruptedException, TimeoutException
		{
			if (!startupBarrier.await(timeoutSeconds, TimeUnit.SECONDS))
			{
				throw new TimeoutException("Socket wasn't created within " + timeoutSeconds + " seconds");
			}
			return socketSuccess;
		}

		/**
		 * Main loop of the SMTP server.
		 */
		@Override
		public void run()
		{
			try
			{
				try
				{
					serverSocket = new ServerSocket(port);
					serverSocket.setSoTimeout(TIMEOUT); // Block for maximum of 1.5 seconds
					socketSuccess = true;
					stopped = false;
				}
				finally
				{
					startupBarrier.countDown();
				}

				// Server: loop until stopped
				while (!isStopped())
				{
					// Start server socket and listen for client connections
					Socket socket = null;
					try
					{
						socket = serverSocket.accept();
					}
					catch (final Exception e)
					{
						if (socket != null)
						{
							socket.close();
						}
						continue; // Non-blocking socket timeout occurred: try accept() again
					}

					// Get the input and output streams
					final BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					final PrintWriter out = new PrintWriter(socket.getOutputStream());

					synchronized (this)
					{
						/*
						 * We synchronize over the handle method and the list update because the client call completes inside
						 * the handle method and we have to prevent the client from reading the list until we've updated it.
						 * For higher concurrency, we could just change handle to return void and update the list inside the
						 * method to limit the duration that we hold the lock.
						 */
						final List msgs = handleTransaction(out, input);
						receivedMail.addAll(msgs);
					}
					socket.close();
				}
			}
			catch (final Exception e)
			{
				/** @todo Should throw an appropriate exception here. */
				e.printStackTrace();
			}
			finally
			{
				stopped = true;
				if (serverSocket != null)
				{
					try
					{
						serverSocket.close();
					}
					catch (final IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * Check if the server has been placed in a stopped state. Allows another thread to stop the server safely.
		 * 
		 * @return true if the server has been sent a stop signal, false otherwise
		 */
		@Override
		public synchronized boolean isStopped()
		{
			return stopped;
		}

		/**
		 * Stops the server. Server is shutdown after processing of the current request is complete.
		 */
		@Override
		public synchronized void stop()
		{
			// Mark us closed
			stopped = true;
			try
			{
				// Kick the server accept loop
				serverSocket.close();
			}
			catch (final IOException e)
			{
				// Ignore
			}
		}

		/**
		 * Handle an SMTP transaction, i.e. all activity between initial connect and QUIT command.
		 * 
		 * @param out
		 *           output stream
		 * @param input
		 *           input stream
		 * @return List of SmtpMessage
		 * @throws IOException
		 */
		private List handleTransaction(final PrintWriter out, final BufferedReader input) throws IOException
		{
			// Initialize the state machine
			SmtpState smtpState = SmtpState.CONNECT;
			final SmtpRequest smtpRequest = new SmtpRequest(SmtpActionType.CONNECT, "", smtpState);

			// Execute the connection request
			final SmtpResponse smtpResponse = smtpRequest.execute();

			// Send initial response
			sendResponse(out, smtpResponse);
			smtpState = smtpResponse.getNextState();

			final List msgList = new ArrayList();
			SmtpMessage msg = new SmtpMessage();

			while (smtpState != SmtpState.CONNECT)
			{
				final String line = input.readLine();

				if (line == null)
				{
					break;
				}

				// Create request from client input and current state
				final SmtpRequest request = SmtpRequest.createRequest(line, smtpState);
				// Execute request and create response object
				final SmtpResponse response = request.execute();
				// Move to next internal state
				smtpState = response.getNextState();
				// Send reponse to client
				sendResponse(out, response);

				// Store input in message
				final String params = request.getParams();
				msg.store(response, params);

				// If message reception is complete save it
				if (smtpState == SmtpState.QUIT)
				{
					msgList.add(msg);
					msg = new SmtpMessage();
				}
			}

			return msgList;
		}

		/**
		 * Send response to client.
		 * 
		 * @param out
		 *           socket output stream
		 * @param smtpResponse
		 *           response object
		 */
		private static void sendResponse(final PrintWriter out, final SmtpResponse smtpResponse)
		{
			if (smtpResponse.getCode() > 0)
			{
				final int code = smtpResponse.getCode();
				final String message = smtpResponse.getMessage();
				out.print(code + " " + message + "\r\n");
				out.flush();
			}
		}

		/**
		 * Get email received by this instance since start up.
		 * 
		 * @return List of String
		 */
		@Override
		public synchronized Iterator getReceivedEmail()
		{
			return getReceivedEmails().iterator();
		}

		@Override
		public synchronized Collection<SmtpMessage> getReceivedEmails()
		{
			return new ArrayList<SmtpMessage>(receivedMail);
		}

		/**
		 * Get the number of messages received.
		 * 
		 * @return size of received email list
		 */
		public synchronized int getReceivedEmailSize()
		{
			return receivedMail.size();
		}

		/**
		 * Creates an instance of SimpleSmtpServer and starts it. Will listen on the default port.
		 * 
		 * @return a reference to the SMTP server
		 * @throws TimeoutException
		 */
		static DummySmtpServer start(final int timeoutSeconds) throws TimeoutException
		{
			return start(DEFAULT_SMTP_PORT, timeoutSeconds);
		}

		/**
		 * Creates an instance of SimpleSmtpServer and starts it.
		 * 
		 * @param port
		 *           port number the server should listen to
		 * @return a reference to the SMTP server
		 * @throws TimeoutException
		 */
		static DummySmtpServer start(final int port, final int timeoutSeconds) throws TimeoutException
		{
			final DummySmtpServer tmpServer = new DummySmtpServer(port);
			final Thread thread = new Thread(tmpServer);
			thread.start();

			try
			{
				if (tmpServer.waitForServerSocketCreated(timeoutSeconds))
				{
					return tmpServer;
				}
			}
			catch (final InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			return null;
		}

	}
}
