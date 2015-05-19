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

import de.hybris.platform.sap.core.jco.rec.RepositoryRecording;
import de.hybris.platform.sap.core.jco.rec.RepositoryRecordingFactory;
import de.hybris.platform.sap.core.jco.rec.version100.RepositoryRecording100;

import java.io.File;


/**
 * Implementation of {@link RepositoryRecordingFactory} interface.
 */
public class RepositoryRecordingFactoryImpl implements RepositoryRecordingFactory
{

	private final File repoFile;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *           this file is the location the recorded data will be saved to.
	 */
	public RepositoryRecordingFactoryImpl(final File file)
	{
		this.repoFile = file;
	}

	@Override
	public RepositoryRecording createRepositoryRecording()
	{
		return new RepositoryRecording100(repoFile);
	}



}
