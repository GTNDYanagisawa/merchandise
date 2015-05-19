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
import de.hybris.platform.atddengine.framework.RobotTestSuite;
import de.hybris.platform.atddengine.framework.RobotTestSuiteFactory;

import java.io.File;

import org.python.core.PyException;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;


public class PythonRobotTestSuiteFactory extends AbstractRobotSettingsAwareFactory implements RobotTestSuiteFactory
{
	public PythonRobotTestSuiteFactory(final PythonAware pythonAware)
	{
		super(pythonAware);
	}

	@Override
	public RobotTestSuite parseTestSuite(final File testSuiteFile)
	{
		return parseTestSuite(null, new File[]
		{ testSuiteFile });
	}

	@Override
	public RobotTestSuite parseTestSuite(final File[] testSuiteFiles)
	{
		return parseTestSuite(null, testSuiteFiles);
	}

	@Override
	public RobotTestSuite parseTestSuite(final RobotSettings robotSettings, final File testSuiteFile)
	{
		return parseTestSuite(robotSettings, new File[]
		{ testSuiteFile });
	}

	@Override
	public RobotTestSuite parseTestSuite(final RobotSettings robotSettings, final File[] testSuiteFiles) throws PyException
	{
		final PyList datasources = new PyList();

		for (final File testSuiteFile : testSuiteFiles)
		{
			datasources.add(new PyString(testSuiteFile.getAbsolutePath()));
		}

		final PyObject testsuiteConstructor = getPythonInterpreter().get("TestSuite");

		final PyObject testSuite = testsuiteConstructor.__call__(new PyObject[]
		{ datasources, createSettings(robotSettings) });

		return new PythonRobotTestSuite(this, createSettings(robotSettings), testSuite);
	}
}
