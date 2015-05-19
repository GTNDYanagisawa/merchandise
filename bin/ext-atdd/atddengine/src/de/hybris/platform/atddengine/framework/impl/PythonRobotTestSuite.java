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

import de.hybris.platform.atddengine.framework.PythonWrapper;
import de.hybris.platform.atddengine.framework.RobotTest;
import de.hybris.platform.atddengine.framework.RobotTestResult;
import de.hybris.platform.atddengine.framework.RobotTestSuite;

import java.util.ArrayList;
import java.util.List;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyStringMap;
import org.python.core.PyTuple;


public class PythonRobotTestSuite extends AbstractPythonAware implements RobotTestSuite, PythonWrapper
{
	private static boolean initializedGlobals;

	private boolean closed = false;

	private PyObject context;

	private PyObject errors;

	private PyObject output;

	private final List<PythonRobotTest> robotTests = new ArrayList<PythonRobotTest>();

	private final PyObject settings;

	private boolean started = false;

	private final PyObject testSuite;

	public PythonRobotTestSuite(final PythonAware pythonAware, final PyObject settings, final PyObject testSuite)
	{
		super(pythonAware);

		this.testSuite = testSuite;

		this.settings = settings;

		initializeRobotTests();
	}

	@Override
	public void close()
	{
		if (started)
		{
			if (closed)
			{
				throw new IllegalStateException("RobotTestSuite has already been closed.");
			}
			else
			{
				testSuite.invoke("_report_status", context, errors);
				testSuite.invoke("_run_teardown", context, errors);
				testSuite.invoke("_end_run", context, errors);

				output.invoke("close", testSuite);

				final PyObject resultWriterConstructor = getPythonInterpreter().get("ResultWriter");

				final PyTuple outputAndSettings = (PyTuple) settings.invoke("get_rebot_datasource_and_settings");

				final PyObject resultWriter = resultWriterConstructor.__call__(outputAndSettings.pyget(0));

				resultWriter.invoke("write_results", outputAndSettings.pyget(1));

				closed = true;
			}
		}
		else
		{
			throw new IllegalStateException("RobotTestSuite has not been started yet.");
		}
	}

	private RobotTestResult createRobotTestResult(final PyObject errors)
	{
		final PyStringMap errorMap = (PyStringMap) errors.getDict();

		for (final PyObject key : errorMap.keys().asIterable())
		{
			if (key.asString().endsWith("_error"))
			{
				final String message = errorMap.get(key).asStringOrNull();

				if (message != null && !message.equals(""))
				{
					return new PythonRobotTestResult(false, message);
				}
			}
		}

		return new PythonRobotTestResult(true, null);
	}

	private PyObject findTest(final RobotTest robotTest)
	{
		for (final PythonRobotTest defaultRobotTest : robotTests)
		{
			if (defaultRobotTest.equals(robotTest))
			{
				return defaultRobotTest.getPyObject();
			}
		}

		return null;
	}

	@Override
	public String getName()
	{
		return testSuite.__getattr__("name").asString();
	}

	@Override
	public PyObject getPyObject()
	{
		return testSuite;
	}

	@Override
	public RobotTest getRobotTest(final String testName)
	{
		for (final RobotTest robotTest : getRobotTests())
		{
			if (robotTest.getName().equals(testName))
			{
				return robotTest;
			}
		}

		return null;
	}

	@Override
	public List<RobotTest> getRobotTests()
	{
		return new ArrayList<RobotTest>(robotTests);
	}

	private void initializeGlobals()
	{
		if (!initializedGlobals)
		{
			final PyObject initFunc = getPythonInterpreter().get("init_global_variables");
			initFunc.__call__(settings);

			initializedGlobals = true;
		}
	}

	private void initializeRobotTests()
	{
		final PyObject[] tests = ((PyList) testSuite.__getattr__("tests")).getArray();

		for (final PyObject test : tests)
		{
			robotTests.add(new PythonRobotTest(this, test));
		}
	}

	@Override
	public boolean isClosed()
	{
		return closed;
	}

	@Override
	public boolean isStarted()
	{
		return started;
	}

	@Override
	public RobotTestResult run(final RobotTest robotTest)
	{
		final PyObject test = findTest(robotTest);

		if (test == null)
		{
			final String message = String.format("RobotTest %s is not part of RobotTestSuite %s", robotTest.getName(), getName());
			throw new IllegalArgumentException(message);
		}
		else
		{
			final PyObject testErrors = test.invoke("_start_run", context, errors);

			if (testErrors.invoke("is_run_allowed").asInt() == 0)
			{
				test.invoke("_not_allowed_to_run", testErrors);
			}
			else
			{
				test.invoke("_run", context, testErrors);
			}

			test.invoke("_end_run", context);

			return createRobotTestResult(testErrors);
		}
	}

	@Override
	public void start()
	{
		if (started)
		{
			if (closed)
			{
				throw new IllegalStateException("RobotTestSuite has already been closed.");
			}
			else
			{
				throw new IllegalStateException("RobotTestSuite is already started.");
			}
		}
		else
		{
			initializeGlobals();

			final PyObject outputConstructor = getPythonInterpreter().get("Output");
			output = outputConstructor.__call__(settings);

			final PyObject errorsConstructor = getPythonInterpreter().get("SuiteRunErrors");
			errors = errorsConstructor.__call__(testSuite.__getattr__("_exit_on_failure_mode"),
					testSuite.__getattr__("_skip_teardowns_on_exit_mode"));

			final PyObject none = getPythonInterpreter().eval("None");

			context = testSuite.invoke("_start_run", new PyObject[]
			{ output, none, errors });
			testSuite.invoke("_run_setup", context, errors);

			started = true;
		}
	}
}
