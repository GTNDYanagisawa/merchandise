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
package de.hybris.platform.acceleratorservices.dataimport.batch.task;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.util.BatchDirectoryUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Cleanup for the impex import process. Deletes the transformed file and moves the processed file to the archive
 * directory.
 */
public class CleanupHelper
{
	private static final String DATE_SEPARATOR = "_";
	private static final Logger LOG = Logger.getLogger(CleanupHelper.class);
	private String timeStampFormat;

	/**
	 * Performs the cleanup of given header
	 * 
	 * @param header
	 *           to clean up
	 * @param error
	 *           flag indicating if there was an error
	 */
	public void cleanup(final BatchHeader header, final boolean error)
	{
		if (header != null)
		{
			cleanupTransformedFiles(header);
			cleanupSourceFile(header, error);
		}
	}

	/**
	 * Deletes a file
	 * 
	 * @param file
	 */
	public void cleanupFile(final File file)
	{
		if (!file.delete())
		{
			LOG.warn("Could not delete " + file);
		}
	}

	/**
	 * Removes the transformed file
	 * 
	 * @param header
	 * @param error
	 */
	protected void cleanupSourceFile(final BatchHeader header, final boolean error)
	{
		if (header.getFile() != null)
		{
			final File movedFile = getDestFile(header.getFile(), error);
			if (!header.getFile().renameTo(movedFile))
			{
				LOG.warn("Could not move " + header.getFile() + " to " + movedFile);

			}
		}
	}

	/**
	 * Removes the transformed file
	 * 
	 * @param header
	 */
	protected void cleanupTransformedFiles(final BatchHeader header)
	{
		if (header.getTransformedFiles() != null)
		{
			for (final File file : header.getTransformedFiles())
			{
				cleanupFile(file);
			}
		}
	}

	/**
	 * Returns the destination location of the file
	 * 
	 * @param file
	 * @param error
	 *           flag indicating if there was an error
	 * @return the destination file
	 */
	protected File getDestFile(final File file, final boolean error)
	{
		final StringBuilder builder = new StringBuilder(file.getName());
		if (!StringUtils.isBlank(timeStampFormat))
		{
			final SimpleDateFormat sdf = new SimpleDateFormat(timeStampFormat, Locale.getDefault());
			builder.append(DATE_SEPARATOR);
			builder.append(sdf.format(new Date()));
		}
		return new File(error ? BatchDirectoryUtils.getRelativeErrorDirectory(file)
				: BatchDirectoryUtils.getRelativeArchiveDirectory(file), builder.toString());
	}

	/**
	 * @param timeStampFormat
	 *           the timeStampFormat to set
	 */
	public void setTimeStampFormat(final String timeStampFormat)
	{
		this.timeStampFormat = timeStampFormat;
		// just verify format; SimpleDateFormat is not thread safe
		new SimpleDateFormat(timeStampFormat, Locale.getDefault());
	}

	/**
	 * 
	 * @return timeStampFormat
	 */
	protected String getTimeStampFormat()
	{
		return timeStampFormat;
	}

}
