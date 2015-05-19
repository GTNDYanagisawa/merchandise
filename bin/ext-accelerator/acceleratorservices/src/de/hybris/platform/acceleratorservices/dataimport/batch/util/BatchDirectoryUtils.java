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
package de.hybris.platform.acceleratorservices.dataimport.batch.util;

import java.io.File;

import org.springframework.util.Assert;


/**
 * Utility methods for folders accessed during batch import.
 */
public final class BatchDirectoryUtils
{
	private static final String ARCHIVE_DIRECTORY = "archive";
	private static final String ERROR_DIRECTORY = "error";

	private BatchDirectoryUtils()
	{
		//empty
	}

	/**
	 * Retrieves the relative error directory from the currently processed file
	 * 
	 * @param processedFile
	 * @return error directory path
	 */
	public static String getRelativeErrorDirectory(final File processedFile)
	{
		return BatchDirectoryUtils.verify(BatchDirectoryUtils.getRelativeBaseDirectory(processedFile) + File.separator
				+ ERROR_DIRECTORY);
	}

	/**
	 * Retrieves the relative archive directory from the currently processed file
	 * 
	 * @param processedFile
	 * @return error archive path
	 */
	public static String getRelativeArchiveDirectory(final File processedFile)
	{
		return BatchDirectoryUtils.verify(BatchDirectoryUtils.getRelativeBaseDirectory(processedFile) + File.separator
				+ ARCHIVE_DIRECTORY);
	}

	/**
	 * Retrieves the relative base directory from the currently processed file
	 * 
	 * @param processedFile
	 * @return base directory path
	 */
	public static String getRelativeBaseDirectory(final File processedFile)
	{
		return processedFile.getParentFile().getParentFile().getAbsolutePath();
	}

	/**
	 * Verifies, if the given directory path exists and if not, creates and verifies the directories
	 * 
	 * @param directory
	 *           to check
	 * @return verified directory (same reference)
	 */
	public static String verify(final String directory)
	{
		Assert.hasText(directory);
		final File dir = new File(directory);
		if (!dir.exists())
		{
			dir.mkdirs();
		}
		Assert.isTrue(dir.exists());
		return directory;
	}
}
