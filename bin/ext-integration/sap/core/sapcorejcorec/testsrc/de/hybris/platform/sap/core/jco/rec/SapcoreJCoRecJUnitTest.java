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
package de.hybris.platform.sap.core.jco.rec;

import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.platform.sap.core.jco.rec.impl.JCoRecManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.test.SapcoreJCoJUnitTest;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.test.context.ContextConfiguration;


/**
 * Base class for testing with the help of the JCoRecorder.
 */
@ContextConfiguration(locations =
{ "jcorec-test-spring.xml" })
public class SapcoreJCoRecJUnitTest extends SapcoreJCoJUnitTest
{

	/** This is the name of the current test-method. It is used for the key of the repository. */
	@Rule
	public TestName name = new TestName();

	/**
	 * Rule notifying about failed and succeeded tests. In case of a failed test the recording will not be saved in order
	 * saving only successful tests and leaving existing files untouched.
	 */
	@Rule
	public final JCoRecJUnitTestWatcher watcher = new JCoRecJUnitTestWatcher(this);

	/**
	 * JCo Recording Factory.
	 */
	@Resource(name = "sapCoreJCoManagedConnectionFactory")
	private JCoRecManagedConnectionFactory factory;

	/**
	 * Default relative recording directory in extensions.
	 */
	public static final String DEFAULT_RELATIVE_RECORDING_DIRECTORY = "testdata" + File.separator + "jcorecorder" + File.separator;

	@Override
	public void setUp()
	{
		super.setUp();
		final String directoryPath = getDirectoryPathForTestdata();
		factory.startRecorder(this.getRecordingMode(), directoryPath, this.getClass().getName() + "." + getCurrentTestMethodName());
	}

	/**
	 * Returns the name of the current test method.
	 * 
	 * @return name of the current test method
	 */
	private String getCurrentTestMethodName()
	{
		if (name == null || name.getMethodName() == null)
		{
			throw new JCoRecRuntimeException("Cannot get current test method name.");
		}
		return name.getMethodName();
	}

	/**
	 * Returns directory path based on extension name.
	 * 
	 * @return directory path for storing and reading test data.
	 */
	private String getDirectoryPathForTestdata()
	{
		final JCoRecMode mode = this.getRecordingMode();
		if (mode == JCoRecMode.OFF)
		{
			return null;
		}

		final String recordingExtensionName = this.getRecordingExtensionName();
		if (recordingExtensionName == null || recordingExtensionName.isEmpty())
		{
			throw new JCoRecRuntimeException("If recordingMode is set to " + JCoRecMode.RECORDING.toString() + " or "
					+ JCoRecMode.PLAYBACK.toString()
					+ " a valid extension name is required for recording. Please use setter for recording extension name.");
		}

		final ExtensionInfo extensionInfo = Utilities.getExtensionInfo(recordingExtensionName);

		if (extensionInfo == null)
		{
			throw new JCoRecRuntimeException("Could not find extension with extension name " + recordingExtensionName
					+ " for recording.");
		}

		try
		{
			final String path = extensionInfo.getExtensionDirectory().getCanonicalPath();
			return path + File.separator + getRelativeRecordingDirectory();
		}
		catch (final IOException e)
		{
			throw new JCoRecRuntimeException("Could not determine extension directory due to IOException", e);
		}
	}


	/**
	 * Returns recording mode.
	 * 
	 * @return the recordingMode
	 */
	public JCoRecMode getRecordingMode()
	{
		final JCoRecording recordingAnnotation = getRecordingAnnotation();
		if (recordingAnnotation == null)
		{
			return JCoRecMode.OFF;
		}
		return recordingAnnotation.mode();
	}

	/**
	 * @return the recordingExtensionName
	 */
	public String getRecordingExtensionName()
	{
		final JCoRecording recordingAnnotation = getRecordingAnnotation();
		if (recordingAnnotation == null)
		{
			return null;
		}
		return recordingAnnotation.recordingExtensionName();
	}


	/**
	 * Executed if the test fails. Recording is not saved.
	 */
	public void testFailed()
	{
		final JCoRecMode mode = this.getRecordingMode();
		if (mode == JCoRecMode.OFF)
		{
			return;
		}

		factory.testFailed();
	}

	/**
	 * Executed if the test succeeded. Recording is saved.
	 */
	public void testSucceeded()
	{
		final JCoRecMode mode = this.getRecordingMode();
		if (mode == JCoRecMode.OFF)
		{
			return;
		}

		factory.stopRecorder();
	}

	/**
	 * Getter for relative recording directory.
	 * 
	 * @return the relativeRecordingDirectory
	 */
	public String getRelativeRecordingDirectory()
	{
		final JCoRecording recordingAnnotation = getRecordingAnnotation();
		if (recordingAnnotation == null || recordingAnnotation.relativeRecordingDirectory().isEmpty())
		{
			return DEFAULT_RELATIVE_RECORDING_DIRECTORY;
		}
		return recordingAnnotation.relativeRecordingDirectory();
	}

	/**
	 * Returns JCoRecording of the unit test.
	 * 
	 * @return Annotation if set, null otherwise
	 */
	protected JCoRecording getRecordingAnnotation()
	{
		return this.getClass().getAnnotation(JCoRecording.class);
	}
}
