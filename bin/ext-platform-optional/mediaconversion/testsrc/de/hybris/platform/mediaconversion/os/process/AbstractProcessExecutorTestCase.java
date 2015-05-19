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
package de.hybris.platform.mediaconversion.os.process;

import de.hybris.platform.mediaconversion.os.ProcessContext;
import de.hybris.platform.mediaconversion.os.ProcessExecutor;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author pohl
 */
@Ignore
public abstract class AbstractProcessExecutorTestCase
{

	private static final Logger LOG = Logger.getLogger(AbstractProcessExecutorTestCase.class);
	private ProcessExecutor executor;


	@Before
	public void setupExecutor() throws Exception
	{
		this.executor = this.createExecutor();
	}

	protected abstract ProcessExecutor createExecutor() throws Exception;

	protected abstract int amountOfThreads();

	@After
	public void cleanUp() throws IOException
	{
		if (this.executor != null)
		{
			LOG.debug("Quitting executor.");
			this.executor.quit();
		}
	}

	@Test
	public void testEcho() throws IOException
	{
		final String message = "nemeses";
		final StringDrain out = new StringDrain(false);
		final StringDrain err = new StringDrain(false);
		final int extVal = this.executor
				.execute(new ProcessContext(EchoCommandFactory.buildCommand(message), null, null, out, err));
		Assert.assertEquals("Normal process termination.", 0, extVal);
		Assert.assertEquals("No error out.", "", err.toString());
		Assert.assertEquals("Stdout speaks..", message, out.toString());
	}

	@Test
	public void testMultithreadedEcho() throws IOException, InterruptedException
	{
		this.execMultithreaded(EchoCommandFactory.buildCommand("Hallo welt!"));
	}

	private void execMultithreaded(final String... command) throws IOException, InterruptedException
	{
		AbstractProcessExecutorTestCase.execMultithreaded(this.executor, this.amountOfThreads(), command);
	}

	static void execMultithreaded(final ProcessExecutor executor, final int amountOfThreads, final String... command)
			throws IOException, InterruptedException
	{

		final Handle<Throwable> handle = new Handle<Throwable>();
		final Semaphore lock = new Semaphore(1 - amountOfThreads);
		for (int i = 0; i < amountOfThreads; i++)
		{
			new Thread("test_" + i)
			{
				@Override
				public void run()
				{
					try
					{
						final StringDrain out = new StringDrain();
						final StringDrain err = new StringDrain();
						final int extVal = executor.execute(new ProcessContext(command, null, null, out, err));
						Assert.assertEquals("Normal process termination.", 0, extVal);
						Assert.assertEquals("No error out.", "", err.toString());
						//                        Assert.assertEquals("Stdout speaks..", message, out.toString());
					}
					catch (final Throwable thr)
					{
						LOG.error("Failed to execute.", thr);
						handle.set(thr);
					}
					finally
					{
						lock.release();
					}
				}
			}.start();
		}
		lock.acquire();
		Assert.assertNull(handle.get());
	}
}
