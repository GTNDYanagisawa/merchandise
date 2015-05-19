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
package de.hybris.platform.sap.core.test.property;

import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;


/**
 * Utility class for PropertyAccessTest.
 */
public class PropertyAccessTestUtil
{

	/**
	 * 
	 * @return canonical path of extension directory sapcoretest
	 * @throws IOException
	 *            IOException
	 */
	public static String getCanonicalPathOfExtensionSapCoreTest() throws IOException
	{
		return Utilities.getExtensionInfo("sapcoretest").getExtensionDirectory().getCanonicalPath() + File.separator;
	}
}
