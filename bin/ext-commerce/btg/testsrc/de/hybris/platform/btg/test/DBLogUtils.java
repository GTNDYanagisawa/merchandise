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

import de.hybris.platform.util.Config;


public class DBLogUtils
{
	public static DBLogConfig startLogging(final String logFile)
	{
		final DBLogConfig config = new DBLogConfig(Config.getString("db.log.file.path", null), Config.getBoolean("db.log.active",
				false), Config.getBoolean("db.log.appendStackTrace", false));
		Config.setParameter("db.log.file.path", logFile);
		Config.setParameter("db.log.active", "true");
		Config.setParameter("db.log.appendStackTrace", "true");
		return config;
	}

	public static void stopLogging(final DBLogConfig config)
	{
		Config.setParameter("db.log.file.path", config.logFile);
		Config.setParameter("db.log.active", "" + config.dbLogActive);//NOPMD
		Config.setParameter("db.log.appendStackTrace", "" + config.dbAppendStackTrace);//NOPMD
	}

	public static class DBLogConfig
	{
		String logFile;
		boolean dbLogActive;
		boolean dbAppendStackTrace;


		public DBLogConfig(final String logFile, final boolean dbLogActive, final boolean dbAppendStackTrace)
		{
			this.logFile = logFile;
			this.dbLogActive = dbLogActive;
			this.dbAppendStackTrace = dbAppendStackTrace;
		}
	}

}