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
package de.hybris.platform.task;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.runner.LatchTaskRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * Test as proof of jalo session being closed 'explicitly' inside TaskService's Poll
 */
@IntegrationTest
public class TaskEngineSessionTest extends AbstractTaskTest
{
	private static final Logger LOG = Logger.getLogger(TaskEngineSessionTest.class);
	public static final int AMOUNT_OF_TASKS = 150;
	private final CountDownLatch latch = new CountDownLatch(AMOUNT_OF_TASKS);
	private final LatchTaskRunner runner = new LatchTaskRunner();

	private TaskService getTaskService()
	{
		return Registry.getApplicationContext().getBean(TaskService.BEAN_ID, TaskService.class);
	}

	@Override
	protected Map<String, Object> createCustomSingletons()
	{
		final Map<String, Object> ret = new HashMap<String, Object>();
		runner.setLatch(latch);
		ret.put("latchTestTaskRunner", runner);
		return ret;
	}

	@Test
	public void testTaskSessionLeakage() throws InterruptedException
	{
		final int initialCount = this.countSessions();
		LOG.info("Initial session count: " + initialCount);
		this.runTasks();

		// Evaluation/check the sessions...
		final int countAfterTasks = this.countSessions();
		final int sessionsCreatedDuringTest = countAfterTasks - initialCount;

		LOG.info("Count after " + TaskEngineSessionTest.AMOUNT_OF_TASKS + " tasks: " + countAfterTasks);
		LOG.info("There were " + sessionsCreatedDuringTest + " sessions created " + "(of which "
				+ JaloConnection.getInstance().getExpiredSessions().size() + " are expired).");
		Assert.assertTrue("Should there be less sessions created (" + sessionsCreatedDuringTest + ") " + "than tasks run ("
				+ TaskEngineSessionTest.AMOUNT_OF_TASKS + ").", sessionsCreatedDuringTest < TaskEngineSessionTest.AMOUNT_OF_TASKS);
	}

	private void runTasks() throws InterruptedException
	{
		final String latchName = "testlatch_" + Math.random();

		for (int i = 0; i < TaskEngineSessionTest.AMOUNT_OF_TASKS; i++)
		{
			final TaskModel task = modelService.create(TaskModel.class);
			task.setContext(latchName);
			task.setRunnerBean("latchTestTaskRunner");
			getTaskService().scheduleTask(task);
		}
		LOG.info("Waiting for tasks to complete.");
		latch.await();
	}

	private int countSessions()
	{
		return JaloConnection.getInstance().getAllSessions().size();
	}

}
