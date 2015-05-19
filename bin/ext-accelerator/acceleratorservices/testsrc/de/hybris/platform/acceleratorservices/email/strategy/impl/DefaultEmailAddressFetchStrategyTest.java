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
package de.hybris.platform.acceleratorservices.email.strategy.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.email.dao.EmailAddressDao;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.test.TestThreadsHolder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.fest.assertions.Assertions;
import org.junit.Test;


@IntegrationTest
public class DefaultEmailAddressFetchStrategyTest extends ServicelayerBaseTest
{
	@Resource
	private DefaultEmailAddressFetchStrategy emailAddressFetchStrategy;

	@Resource
	private EmailAddressDao emailAddressDao;

	@Resource
	private ModelService modelService;

	private interface RunnableWithResult<T> extends Runnable
	{
		T getResult();
	}

	@Test
	public void testFetch()
	{
		final EmailAddressModel fetch = emailAddressFetchStrategy.fetch("test-1@hybris.com", "yTestEmail-1");
		final EmailAddressModel fetch2 = emailAddressFetchStrategy.fetch("test-1@hybris.com", "yTestEmail-1");
		Assertions.assertThat(fetch).isEqualTo(fetch2);
	}

	@Test
	public void testFetchMultiThreadedTwoPhase() throws InterruptedException, ExecutionException
	{

		final Semaphore semaphoreA = new Semaphore(1);
		final Semaphore semaphoreB = new Semaphore(1);

		final TestThreadsHolder<RunnableWithResult<PK>> workerThreads = new TestThreadsHolder<RunnableWithResult<PK>>(10, true)
		{
			@Override
			public Runnable newRunner(final int threadNumber)
			{
				return new RunnableWithResult<PK>()
				{
					private volatile PK result;

					@Override
					public void run()
					{
						result = new DefaultEmailAddressFetchStrategy()
						{
							@Override
							protected EmailAddressModel loadEmailAddressFromDatabase(final String emailAddress, final String displayName)
							{
								try
								{
									(threadNumber % 2 == 0 ? semaphoreA : semaphoreB).acquire();
									final EmailAddressModel result = super.loadEmailAddressFromDatabase(emailAddress, displayName);
									return result;
								}
								catch (final InterruptedException e)
								{
									Assert.fail(e.getMessage());
								}
								finally
								{
									(threadNumber % 2 == 0 ? semaphoreA : semaphoreB).release();
								}
								return null;
							}

							@Override
							public EmailAddressDao getEmailAddressDao()
							{
								return emailAddressDao;
							}

							@Override
							public ModelService getModelService()
							{
								return modelService;
							}
						}.fetch("test-2@hybris.com", "yTestEmail-2").getPk();
					}

					@Override
					public PK getResult()
					{
						return result;
					}
				};
			}
		};
		workerThreads.startAll();
		verifySamePKs(workerThreads);
	}

	@Test
	public void testFetchMultiThreaded()
	{
		final TestThreadsHolder<RunnableWithResult<PK>> workerThreads = new TestThreadsHolder<RunnableWithResult<PK>>(50, true)
		{
			@Override
			public Runnable newRunner(final int threadNumber)
			{
				return new RunnableWithResult<PK>()
				{

					private volatile PK result;

					@Override
					public void run()
					{
						result = emailAddressFetchStrategy.fetch("test-3@hybris.com", "yTestEmail-3").getPk();
					}

					@Override
					public PK getResult()
					{
						return result;
					}
				};
			}
		};
		workerThreads.startAll();
		verifySamePKs(workerThreads);
	}

	private void verifySamePKs(final TestThreadsHolder<RunnableWithResult<PK>> workerThreads)
	{
		Assertions.assertThat(workerThreads.waitForAll(60, TimeUnit.SECONDS)).isTrue();
		Assertions.assertThat(workerThreads.getErrors()).isEmpty();

		PK previous = null;
		for (final RunnableWithResult<PK> runnable : workerThreads.getRunners())
		{
			final PK result = runnable.getResult();
			Assertions.assertThat(result).isNotNull();
			if (previous != null)
			{
				Assertions.assertThat(result).isEqualTo(previous);
			}
			previous = result;
		}
	}
}
