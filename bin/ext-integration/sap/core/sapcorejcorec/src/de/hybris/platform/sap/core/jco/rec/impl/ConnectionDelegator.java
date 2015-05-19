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
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoStateful;
import de.hybris.platform.sap.core.jco.connection.impl.JCoConnectionImpl;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.RepositoryRecording;
import de.hybris.platform.sap.core.jco.rec.version000.impl.JCoRecRepository;

import com.sap.conn.jco.JCoFunction;


/**
 * The JCoRecorder equivalent to the {@link JCoConnectionImpl}.
 */
public class ConnectionDelegator implements JCoConnection, JCoStateful
{

	private JCoConnection delegatedConnection;

	private RepositoryPlayback repositoryPlayback;
	private RepositoryRecording repositoryRecording;

	private final JCoRecMode mode;

	/**
	 * Constructor.
	 * 
	 * @param connection
	 *           the delegated Connection.
	 * @param mode
	 *           the mode of the recorder.
	 * @param repoPlayback
	 *           the RepositoryPlayback instance.
	 * @param repoRecording
	 *           the RepositoryRecording instance.
	 * 
	 */
	public ConnectionDelegator(final JCoConnection connection, final JCoRecMode mode, final RepositoryPlayback repoPlayback,
			final RepositoryRecording repoRecording)
	{
		this.mode = mode;

		switch (mode)
		{
			case PLAYBACK:
				this.repositoryPlayback = repoPlayback;
				break;
			case RECORDING:
				this.delegatedConnection = connection;
				this.repositoryRecording = repoRecording;
				break;
			case OFF:
				// should not be possible here
				break;
			default:
				throw new UnsupportedOperationException("Mode " + mode + " is not supported!");
		}
	}

	@Override
	public JCoFunction getFunction(final String functionName) throws BackendException
	{
		//		final int count = increaseCounter(functionName);
		JCoFunction function = null;

		if (mode == JCoRecMode.PLAYBACK)
		{
			try
			{
				function = repositoryPlayback.getFunction(functionName);
			}
			catch (final JCoRecException e)
			{
				throw new BackendException(e.getMessage(), e);
			}
		}
		else
		{
			// mode == JCoRecMode.RECORDING
			// only most up-to-date RepositoryRecording implementation is used
			function = delegatedConnection.getFunction(functionName);
			final int count = repositoryRecording.putFunction(function);
			function = new JCoRecFunctionDecorator(function, count, mode);
		}

		if (function == null)
		{
			throw new JCoRecRuntimeException("Function " + functionName + " not found in repository!");
		}
		return function;
	}

	@Override
	public void execute(final JCoFunction function) throws BackendException
	{
		// if PLAYBACK is enabled and there is a repositoryVersion in the file
		// then skip execution because all data is already contained in the JCoFunction (-Decorator)
		if (mode == JCoRecMode.PLAYBACK)
		{
			if (!(function instanceof JCoRecFunctionDecorator))
			{
				throw new JCoRecRuntimeException(this.getClass().getSimpleName()
						+ " should get a function of type JCoRecFunctionDecorator in the execute-method!");
			}
			final JCoRecFunctionDecorator decoFunction = (JCoRecFunctionDecorator) function;
			if (repositoryPlayback instanceof JCoRecRepository)
			{
				// execute will do nothing in PLAYBACK mode.
				//	decoFunction.execute(null);
				return;
			}

			// repositories with version number contain executed functions as well as their empty counterpart
			JCoRecFunctionDecorator executedFunction;
			try
			{
				executedFunction = (JCoRecFunctionDecorator) repositoryPlayback.getFunction(decoFunction.getName());
			}
			catch (final JCoRecException e)
			{
				throw new JCoRecRuntimeException(e);
			}
			// execute will do nothing in PLAYBACK mode.
			//	executedFunction.execute(null);

			decoFunction.setDecoratedFunction(executedFunction);
			return;
		}

		// mode == RECORDING
		// only most up-to-date RepositoryRecording implementation is used
		delegatedConnection.execute(function);
		repositoryRecording.putFunction(function);
	}

	@Override
	public boolean isFunctionAvailable(final String functionName) throws BackendException
	{
		if (mode == JCoRecMode.PLAYBACK)
		{
			return true;
		}
		return delegatedConnection.isFunctionAvailable(functionName);
	}

	@Override
	public void setCallerId(final String callerID)
	{
		// nothing to do here
	}

	@Override
	public boolean isBackendOffline() throws BackendException
	{
		if (mode == JCoRecMode.PLAYBACK)
		{
			return false;
		}
		return delegatedConnection.isBackendOffline();
	}

	@Override
	public boolean isBackendAvailable() throws BackendException
	{
		if (mode == JCoRecMode.PLAYBACK)
		{
			return true;
		}
		return delegatedConnection.isBackendOffline();
	}

	@Override
	public void destroy() throws BackendException
	{
		if (delegatedConnection instanceof JCoStateful)
		{
			((JCoStateful) delegatedConnection).destroy();
		}

	}
}
