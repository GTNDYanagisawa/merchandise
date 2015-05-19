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
package de.hybris.platform.servicelayer.event;

import static org.junit.Assert.fail;

import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.event.impl.PlatformClusterEventSender;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;


/**
 * Extracted base logic to use for a integration tests of event system using {@link TestEventQueueBasedListener}.
 */
@Ignore
abstract public class AbstractAsynchronousEventServiceTest extends ServicelayerBaseTest
{
	private static final Logger LOG = Logger.getLogger(AbstractAsynchronousEventServiceTest.class);

	@Resource
	protected EventService eventService;

	@Resource
	private PlatformClusterEventSender platformClusterEventSender;

	private TestEventQueueBasedListener listener;
	private Executor oldExecutor;

	@Before
	public void setUp()
	{
		oldExecutor = platformClusterEventSender.getExecutor();
		platformClusterEventSender.setExecutor(Executors.newCachedThreadPool());
		listener = new TestEventQueueBasedListener(10); // wait at most 10 seconds at event queue
		eventService.registerEventListener(listener);
		LOG.info("registered test event listener " + listener);
	}

	@After
	public void tearDown()
	{
		listener.setExpectedEventClass(null); // make listener not handling events any more
		eventService.unregisterEventListener(listener); // remove listener
		platformClusterEventSender.setExecutor(oldExecutor);
		LOG.info("unregistered test event listener " + listener);
	}

	protected void setExpectedEventClass(final Class<? extends AbstractEvent> eventClass)
	{
		listener.setExpectedEventClass(eventClass);
	}

	protected AbstractEvent pollEvent()
	{
		return listener.pollEvent();
	}


	protected static class TestEventQueueBasedListener extends AbstractEventListener<AbstractEvent>
	{
		private volatile Class<? extends AbstractEvent> expectedClass = null;
		private final EventQueue eventQueue;

		public TestEventQueueBasedListener(final int timeoutSeconds)
		{
			super();
			eventQueue = new EventQueue<AbstractEvent>(timeoutSeconds);
		}

		@Override
		protected void onEvent(final AbstractEvent event)
		{
			try
			{
				if (expectedClass != null && expectedClass.isAssignableFrom(event.getClass()))
				{
					eventQueue.push(event);
				}
			}
			catch (final InterruptedException e)
			{
				Thread.currentThread().interrupt(); // preserve interrupted status
				LOG.error("Interrupted while pushing event to queue", e);
			}
			if (LOG.isDebugEnabled())
			{
				LOG.debug(event);
			}
		}

		public AbstractEvent pollEvent()
		{
			AbstractEvent event = null;
			try
			{
				do
				{
					event = eventQueue.poll();
				}
				while (event != null && expectedClass != null && (!expectedClass.isAssignableFrom(event.getClass())));
				return event;
			}
			catch (final InterruptedException e)
			{
				fail(e.getMessage());
				return event;
			}
		}

		public void setExpectedEventClass(final Class<? extends AbstractEvent> clazz)
		{
			this.expectedClass = clazz;
		}
	}

	protected static class EventQueue<T extends AbstractEvent>
	{
		private final int timeoutSeconds;
		private final BlockingQueue<T> queue = new SynchronousQueue<T>();

		EventQueue(final int timeoutSeconds)
		{
			this.timeoutSeconds = timeoutSeconds;
		}

		public void push(final T event) throws InterruptedException
		{
			queue.offer(event, timeoutSeconds, TimeUnit.SECONDS);
		}

		public T poll() throws InterruptedException
		{
			return queue.poll(timeoutSeconds, TimeUnit.SECONDS);
		}
	}
}
