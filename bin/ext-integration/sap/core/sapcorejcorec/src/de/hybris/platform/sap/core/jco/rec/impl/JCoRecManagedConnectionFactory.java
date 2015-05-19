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
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionFactory;
import de.hybris.platform.sap.core.jco.rec.JCoRecException;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.RepositoryRecording;

import java.io.File;

import org.apache.log4j.Logger;


/**
 * If this Class is registered as ManagedConnectionFactory in the -spring.xml then JCoRec is enabled.
 * <p>
 * Make sure to provide parameters record (OFF, RECORDING or PLAYBACK) and path (the path where to save/load) in the
 * correspondent Test Files.
 * </p>
 */
public class JCoRecManagedConnectionFactory implements JCoManagedConnectionFactory
{

	/**
	 * Environment variable for forcing recording.
	 */
	public static final String ENVIRONMENT_VARIABLE_FORCE_RECORDING = "sap.hybris.JCoRecorder.forceRecording";


	/**
	 * Environment variable for disabling recorder.
	 */
	public static final String ENVIRONMENT_VARIABLE_DISABLE_RECORDING = "sap.hybris.JCoRecorder.disableRecording";


	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoRecManagedConnectionFactory.class.getName());

	/** Current mode of the recorder. */
	private JCoRecMode recordingMode;

	private String directoryPath = "";
	/** The key for the repository as well as the name for the saved repository file. */
	private String key;
	/** The actual file for the repository. */
	private File repositoryFile;

	private RepositoryPlayback repoPlayback;
	private RepositoryRecording repoRecording;

	/**
	 * Connection factory.
	 */
	private final JCoManagedConnectionFactory wrappedFactory;

	/**
	 * Forces recording mode if set to true for all tests which run in playback mode. <br>
	 * If a complete test suite should be recorded this flag can be set to true. It overrules all tests which are running
	 * in playback mode. It is also possible to set the flag in jcorec-test-spring.xml and without touching source code
	 * it is possible to set environment variable sap.hybris.JCoRecorder.forceRecording to true. Setting the environment
	 * variable overrules all programmatic settings. Tests with recording mode off stays untouched. <br>
	 * Default value is false.
	 */
	private boolean forceRecording = false;


	/**
	 * Disables recorder if set to true for all tests. <br>
	 * If this global flag is set the recording is disabled.
	 */
	private boolean disableRecorder = false;


	/**
	 * Constructor for the spring definition. Called as soon as the test class SapcoreJCoRecJUnitTest is initialized.
	 * 
	 * @param factory
	 *           the JCoManagedConnectionFactory to wrap.
	 */
	public JCoRecManagedConnectionFactory(final JCoManagedConnectionFactory factory)
	{
		this.wrappedFactory = factory;
	}


	@Override
	public JCoConnection getManagedConnection(final String connectionName, final String callerId)
	{
		return getManagedConnection(connectionName, callerId, null);
	}

	@Override
	public JCoConnection getManagedConnection(final String connectionName, final String callerId, final String destinationName)
	{
		switch (recordingMode)
		{
			case RECORDING:
				return new ConnectionDelegator(wrappedFactory.getManagedConnection(connectionName, this.getClass().getName(),
						destinationName), recordingMode, repoPlayback, repoRecording);
			case PLAYBACK:
				return new ConnectionDelegator(null, recordingMode, repoPlayback, repoRecording);
			case OFF:
				return wrappedFactory.getManagedConnection(connectionName, this.getClass().getName(), destinationName);
			default:
				throw new UnsupportedOperationException("Mode not supported!");
		}
	}


	/**
	 * Creates a file from the local attributes {@link relativeDir} and {@link key}.
	 * 
	 * @return Returns the repository file.
	 */
	private File getRepositoryFile()
	{
		final String filePath = this.directoryPath + this.key + ".xml";
		return new File(filePath);
	}

	/**
	 * Getter for the local attribute {@link #recordingMode}.
	 * 
	 * @return the current value of {@code mode}.
	 */
	protected JCoRecMode getRecordingMode()
	{
		return recordingMode;
	}

	/**
	 * Starts recorder.
	 * 
	 * @param recordingMode
	 *           recording mode
	 * @param directoryPath
	 *           directory path
	 * @param key
	 *           unique key for creating the repository file name
	 */
	public void startRecorder(final JCoRecMode recordingMode, final String directoryPath, final String key)
	{

		this.recordingMode = recordingMode;
		this.directoryPath = directoryPath;
		this.key = key;


		if (isDisableRecorder())
		{
			log.debug("JCoRecorder diabled for " + key);
			this.recordingMode = JCoRecMode.OFF;
			return;
		}

		if (this.recordingMode == JCoRecMode.OFF)
		{
			log.debug("JCoRecorder started in JCoRecMode.OFF mode for " + key);
			return;
		}

		if (isForceRecording() && this.recordingMode == JCoRecMode.PLAYBACK)
		{
			log.debug("JCoRecorder forced JCoRecMode.RECORDING mode for " + key);
			this.recordingMode = JCoRecMode.RECORDING;
		}

		repositoryFile = getRepositoryFile();

		if (this.recordingMode == JCoRecMode.RECORDING)
		{
			log.debug("JCoRecorder started in JCoRecMode.RECORDING mode for " + key);
			repoRecording = new RepositoryRecordingFactoryImpl(repositoryFile).createRepositoryRecording();
			return;
		}

		if (this.recordingMode == JCoRecMode.PLAYBACK)
		{
			log.debug("JCoRecorder started in JCoRecMode.PLAYBACK mode for " + key);
			repoPlayback = new RepositoryPlaybackFactoryImpl(repositoryFile).createRepositoryPlayback();
			return;
		}
	}


	/**
	 * Stops recorder. In case of recorder mode recording the repository is saved.
	 */
	public void stopRecorder()
	{
		if ((recordingMode == JCoRecMode.OFF) || (recordingMode == JCoRecMode.PLAYBACK))
		{
			repoPlayback = null;
			return;
		}

		if (recordingMode == JCoRecMode.RECORDING)
		{
			//			recordingRepository.saveRepositoryFile(repositoryFile);
			try
			{

				//				JCoRecRepository.getInstance(repositoryFile, recordingMode).writeRepositoryToFile(repositoryFile);
				repoRecording.writeRepositoryToFile();
			}
			catch (final JCoRecException e)
			{
				throw new JCoRecRuntimeException(e);
			}
		}
		repoRecording = null;
	}

	/**
	 * Notify recorder that the test has failed.
	 */
	public void testFailed()
	{
		//for further use.
	}


	/**
	 * Returns if force recording is set. <br>
	 * Considers environment variable which overrules settings in coding.
	 * 
	 * @return the forceRecording
	 */
	public boolean isForceRecording()
	{
		final String envForceRecording = System.getenv(ENVIRONMENT_VARIABLE_FORCE_RECORDING);
		if (envForceRecording != null)
		{
			log.debug("Environment variable for forcing recording is set. Value is " + envForceRecording);
			final boolean envForceRecordingBoolean = Boolean.parseBoolean(envForceRecording);
			log.info("Environment variable for forcing recording is set to " + envForceRecordingBoolean);
			return envForceRecordingBoolean;
		}
		return forceRecording;
	}


	/**
	 * Setter for force recording.
	 * 
	 * @param forceRecording
	 *           the forceRecording to set
	 */
	public void setForceRecording(final boolean forceRecording)
	{
		this.forceRecording = forceRecording;
	}

	/**
	 * Returns if recorder is disabled. <br>
	 * Considers environment variable which overrules settings in coding.
	 * 
	 * @return the disableRecorder
	 */
	public boolean isDisableRecorder()
	{
		final String envDisableRecorder = System.getenv(ENVIRONMENT_VARIABLE_DISABLE_RECORDING);
		if (envDisableRecorder != null)
		{
			log.debug("Environment variable for disable recorder is set. Value is " + envDisableRecorder);
			final boolean envDisableRecorderBoolean = Boolean.parseBoolean(envDisableRecorder);
			log.info("Environment variable for for disable recorder is set to " + envDisableRecorderBoolean);
			return envDisableRecorderBoolean;
		}
		return disableRecorder;
	}


	/**
	 * Setter for disable recorder.
	 * 
	 * @param disableRecorder
	 *           the disableRecorder to set
	 */
	public void setDisableRecorder(final boolean disableRecorder)
	{
		this.disableRecorder = disableRecorder;
	}


}
