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

import de.hybris.platform.sap.core.jco.connection.JCoConnectionEvent;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionEventListener;


/**
 * 
 */

public class JCoListenerSTFC_CONNECTION implements JCoConnectionEventListener
{

	/**
	 * Self-explanatory.
	 */
	protected boolean calledAfterJCOFunctionCall; //NOPMD
	/**
	 * Self-explanatory.
	 */
	protected boolean calledBeforeJCOFunctionCall; //NOPMD
	/**
	 * Self-explanatory.
	 */
	protected boolean sourceAvailableAfterJCOFunctionCall; //NOPMD
	/**
	 * Self-explanatory.
	 */
	protected boolean sourceAvailableBeforeJCOFunctionCall;//NOPMD
	/**
	 * Self-explanatory.
	 */
	protected boolean functionAvailableAfterJCOFunctionCall;//NOPMD
	/**
	 * Self-explanatory.
	 */
	protected boolean functionAvailableBeforeJCOFunctionCall;//NOPMD


	@SuppressWarnings("javadoc")
	public boolean isCalledAfterJCOFunctionCall()
	{
		return calledAfterJCOFunctionCall;
	}


	@SuppressWarnings("javadoc")
	public boolean isCalledBeforeJCOFunctionCall()
	{
		return calledBeforeJCOFunctionCall;
	}

	@SuppressWarnings("javadoc")
	public boolean isSourceAvailableAfterJCOFunctionCall()
	{
		return sourceAvailableAfterJCOFunctionCall;
	}

	@SuppressWarnings("javadoc")
	public boolean isSourceAvailableBeforeJCOFunctionCall()
	{
		return sourceAvailableBeforeJCOFunctionCall;
	}

	@SuppressWarnings("javadoc")
	public boolean isFunctionAvailableAfterJCOFunctionCall()
	{
		return functionAvailableAfterJCOFunctionCall;
	}

	@SuppressWarnings("javadoc")
	public boolean isFunctionAvailableBeforeJCOFunctionCall()
	{
		return functionAvailableBeforeJCOFunctionCall;
	}

	@SuppressWarnings("javadoc")
	public void initialize()
	{
		calledAfterJCOFunctionCall = false;
		calledBeforeJCOFunctionCall = false;
		sourceAvailableAfterJCOFunctionCall = false;
		sourceAvailableBeforeJCOFunctionCall = false;
		functionAvailableAfterJCOFunctionCall = false;
		functionAvailableBeforeJCOFunctionCall = false;

	}


	@Override
	@SuppressWarnings("javadoc")
	public void connectionEvent(final JCoConnectionEvent event)
	{

		if (event.getId() == JCoConnectionEvent.BEFORE_JCO_FUNCTION_CALL)
		{
			calledBeforeJCOFunctionCall = true;
			if (event.getSource() != null)
			{
				sourceAvailableBeforeJCOFunctionCall = true;
			}
			if (event.getFunction() != null)
			{
				functionAvailableBeforeJCOFunctionCall = true;
			}

		}
		if (event.getId() == JCoConnectionEvent.AFTER_JCO_FUNCTION_CALL)
		{
			calledAfterJCOFunctionCall = true;
			if (event.getSource() != null)
			{
				sourceAvailableAfterJCOFunctionCall = true;
			}
			if (event.getFunction() != null)
			{
				functionAvailableAfterJCOFunctionCall = true;
			}


		}
	}
}
