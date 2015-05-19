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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.python.util.PythonInterpreter;


public class DefaultPythonProvider extends AbstractPythonAware
{
	public DefaultPythonProvider() throws IOException
	{
		super(new PythonInterpreter());
		loadRobotModules();
	}

	public DefaultPythonProvider(final String keywordLibrariesDirectoryName) throws IOException
	{
		this();
		locateKeywordLibraries(keywordLibrariesDirectoryName);
	}

	private void loadRobotModules()
	{
		getPythonInterpreter().exec("import robot");
		getPythonInterpreter().exec("from robot.conf import RobotSettings");
		getPythonInterpreter().exec("from robot.variables import init_global_variables");
		getPythonInterpreter().exec("from robot.running.runerrors import SuiteRunErrors");
		getPythonInterpreter().exec("from robot.running import TestSuite");
		getPythonInterpreter().exec("from robot.output import Output");
		getPythonInterpreter().exec("from robot.reporting import ResultWriter");
	}

	private void locateKeywordLibraries(final String keywordLibrariesDirectoryName) throws IOException
	{
		getPythonInterpreter().exec("import sys");
		getPythonInterpreter().exec("from os.path import abspath");

		final Enumeration<URL> robotKeywordLocations = getClass().getClassLoader().getResources(keywordLibrariesDirectoryName);

		while (robotKeywordLocations.hasMoreElements())
		{
			final File robotKeywordDirectory = new File(robotKeywordLocations.nextElement().getFile());
			if (robotKeywordDirectory.exists() && robotKeywordDirectory.isDirectory())
			{
				final String robotKeywordPath = robotKeywordDirectory.getAbsolutePath().replaceAll("\\\\", "/");
				getPythonInterpreter().exec("sys.path.append(abspath('" + robotKeywordPath + "'))");
			}
		}
	}
}