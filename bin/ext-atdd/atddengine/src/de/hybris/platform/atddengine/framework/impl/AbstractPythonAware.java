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
package de.hybris.platform.atddengine.framework.impl;

import org.python.util.PythonInterpreter;


public abstract class AbstractPythonAware implements PythonAware
{
	private final PythonInterpreter pythonInterpreter;

	public AbstractPythonAware(final PythonAware pythonAware)
	{
		this(pythonAware.getPythonInterpreter());
	}

	public AbstractPythonAware(final PythonInterpreter pythonInterpreter)
	{
		this.pythonInterpreter = pythonInterpreter;
	}

	@Override
	public PythonInterpreter getPythonInterpreter()
	{
		return pythonInterpreter;
	}
}