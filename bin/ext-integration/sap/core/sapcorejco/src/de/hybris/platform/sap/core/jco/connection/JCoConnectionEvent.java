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
package de.hybris.platform.sap.core.jco.connection;

import de.hybris.platform.sap.core.jco.connection.impl.ConnectionEvent;

import com.sap.conn.jco.JCoFunction;


/**
 * This object contains information about a JCo Connection event.
 */
@SuppressWarnings("serial")
public class JCoConnectionEvent extends ConnectionEvent
{

	/**
	 * Id which indicates that the event has been fired just before a JCo Function module has been called.
	 */
	public static final int BEFORE_JCO_FUNCTION_CALL = 1;

	/**
	 * Id which indicates that the event has been fired after a JCo Function module has been called.
	 */
	public static final int AFTER_JCO_FUNCTION_CALL = 2;

	private int id = 0;
	private final JCoFunction function;

	/**
	 * Creates a event object.
	 * 
	 * @param source
	 *           the source of the event
	 * @param function
	 *           a JCoFunction
	 * @param id
	 *           id indicating what kind of event it is
	 */
	public JCoConnectionEvent(final Object source, final JCoFunction function, final int id)
	{
		super(source);
		this.function = function;
		this.id = id;
	}

	/**
	 * Returns a JCo function if this event is related with a call of a JCo function.
	 * 
	 * @return a JCo function if this event is related with a call of a JCo function
	 */
	public JCoFunction getFunction()
	{
		return function;
	}

	/**
	 * Returns an id which indicates what kind of event it is.
	 * 
	 * @return id indicating what kind of event it is
	 */
	public int getId()
	{
		return id;
	}

}
