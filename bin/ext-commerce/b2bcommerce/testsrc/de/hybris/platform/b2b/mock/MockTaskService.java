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
package de.hybris.platform.b2b.mock;

import de.hybris.platform.task.TaskEngine;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.Date;

import org.junit.Ignore;


@Ignore
public class MockTaskService implements TaskService
{
	@Override
	public void triggerEvent(final String event)
	{
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public void triggerEvent(final String string, final Date date)
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void scheduleTask(final TaskModel task)
	{
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public TaskEngine getEngine()
	{
		return new TaskEngine()
		{
			@Override
			public void triggerRepoll(final Integer nodeID)
			{
				// To change body of implemented methods use File | Settings |
				// File Templates.
			}

			@Override
			public void repoll()
			{
				// To change body of implemented methods use File | Settings |
				// File Templates.
			}

			@Override
			public void start()
			{
				// To change body of implemented methods use File | Settings |
				// File Templates.
			}

			@Override
			public void stop()
			{
				// To change body of implemented methods use File | Settings |
				// File Templates.
			}

			@Override
			public boolean isRunning()
			{
				return false; // To change body of implemented methods use File
								  // | Settings | File Templates.
			}
		}; // To change body of implemented methods use File | Settings | File
			// Templates.
	}
}
