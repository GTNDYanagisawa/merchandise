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

import de.hybris.platform.atddengine.framework.RobotSettings;

import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyString;


public class AbstractRobotSettingsAwareFactory extends AbstractPythonAware
{
	public AbstractRobotSettingsAwareFactory(final PythonAware pythonAware)
	{
		super(pythonAware);
	}

	protected PyObject createSettings(final RobotSettings robotSettings)
	{
		final PyDictionary options = new PyDictionary();

		if (robotSettings != null)
		{
			setOptionsValue(options, robotSettings.getOutputDir(), "outputdir", robotSettings.getOutputDir().getAbsolutePath());
			setOptionsValue(options, robotSettings.getLogName(), "log", robotSettings.getLogName());
			setOptionsValue(options, robotSettings.getOutputName(), "output", robotSettings.getOutputName());
			setOptionsValue(options, robotSettings.getReportName(), "report", robotSettings.getReportName());
		}

		final PyObject robotSettingsConstructor = getPythonInterpreter().get("RobotSettings");
		return robotSettingsConstructor.__call__(options);
	}

	protected static void setOptionsValue(final PyDictionary options, final Object conditionalValue, final String name,
			final String value)
	{
		if (conditionalValue != null)
		{
			options.put(new PyString(name), new PyString(value));
		}
	}
}
