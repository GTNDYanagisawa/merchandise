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
package de.hybris.platform.core.initialization.testbeans;

import de.hybris.platform.core.initialization.SystemSetup;


@SystemSetup(extension = SystemSetupEmptyTestBean.NOTHIN_TEST_EXTENSION)
public class SystemSetupEmptyTestBean
{
	public static final String NOTHIN_TEST_EXTENSION = "nothin_extension";

	public void doNothing()
	{
		//nope
	}
}
