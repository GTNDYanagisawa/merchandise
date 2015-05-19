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

package de.hybris.platform.sap.core.test;

import de.hybris.platform.util.Utilities;

import java.io.File;


/**
 * Utility for test files.
 */
public class TestFileUtility
{

	/**
	 * Reads the file within the requested extension using the file path.
	 * 
	 * @param extensionsName
	 *           extension name
	 * @param extensionFilePath
	 *           file name
	 * @return file
	 */
	public static File getFile(final String extensionsName, final String extensionFilePath)
	{
		final String extensionPath = Utilities.getPlatformConfig().getExtensionInfo("sapcorejco").getExtensionDirectory()
				.getAbsolutePath();
		return new File(extensionPath + File.separator + extensionFilePath);
	}

}
