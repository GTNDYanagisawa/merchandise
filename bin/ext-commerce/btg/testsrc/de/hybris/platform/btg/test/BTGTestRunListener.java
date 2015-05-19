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
package de.hybris.platform.btg.test;

import de.hybris.platform.btg.test.DBLogUtils.DBLogConfig;
import de.hybris.platform.util.Utilities;

import java.io.File;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;


public class BTGTestRunListener extends RunListener
{
	private DBLogConfig logConfig;

	@Override
	public void testStarted(final Description description) throws Exception
	{
		final String className = description.getClassName();
		final String methodName = description.getMethodName();
		final String id = String.valueOf(System.currentTimeMillis());
		final String logDir = Utilities.getPlatformConfig().getSystemConfig().getLogDir().getAbsolutePath() + "s";
		this.logConfig = DBLogUtils.startLogging(new File(logDir, className + "_" + methodName + "_" + id + ".log")
				.getAbsolutePath());
	}

	@Override
	public void testFinished(final Description description) throws Exception
	{
		DBLogUtils.stopLogging(logConfig);
	}

}
