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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.events.AfterInitializationEndEvent;
import de.hybris.platform.servicelayer.event.events.AfterInitializationStartEvent;
import de.hybris.platform.servicelayer.event.events.AfterSessionCreationEvent;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;
import de.hybris.platform.servicelayer.event.events.BeforeSessionCloseEvent;
import de.hybris.platform.tx.Transaction;

import org.junit.Test;


@IntegrationTest
public class AsynchronousEventServiceTest extends AbstractAsynchronousEventServiceTest
{
	@Test
	public void testEvents() throws Exception
	{
		setExpectedEventClass(CustomEvent.class);
		final CustomEvent event = new CustomEvent();
		eventService.publishEvent(event);

		assertSame("Received event is not expected one", event, pollEvent());
	}

	@Test
	public void testTxEvents() throws Exception
	{
		setExpectedEventClass(CustomEvent.class);
		try
		{
			boolean success;

			final CustomEvent event = new CustomEvent();

			//Tx1
			success = false;
			final Transaction tx1 = Transaction.current();
			tx1.begin();
			try
			{
				eventService.publishEvent(event);
				assertSame("Not received expected event", event, pollEvent());
				success = true;
			}
			finally
			{
				if (success)
				{
					tx1.commit();
				}
				else
				{
					tx1.rollback();
				}
			}

			//Tx2
			success = false;
			final Transaction tx2 = Transaction.current();
			tx2.begin();
			try
			{
				eventService.publishEvent(event);
				assertSame("Received event is not expected one", event, pollEvent());
				success = true;
			}
			finally
			{
				if (success)
				{
					tx2.commit();
				}
				else
				{
					tx2.rollback();
				}
			}
		}
		finally
		{
			//eventService.unregisterEventListener(listener);
		}
	}

	@Test
	public void testTxAwareEvents() throws Exception
	{
		setExpectedEventClass(CustomTxEvent.class);
		boolean success;

		final CustomTxEvent event = new CustomTxEvent(true, "1");

		//test commit
		success = false;
		final Transaction tx1 = Transaction.current();
		tx1.begin();
		try
		{
			eventService.publishEvent(event);
			success = true;
		}
		finally
		{
			if (success)
			{
				tx1.commit();
			}
			else
			{
				tx1.rollback();
			}
		}
		assertSame("Received event is not expected one", event, pollEvent());

		//test rollback
		success = false;
		final Transaction tx2 = Transaction.current();
		tx2.begin();
		try
		{
			eventService.publishEvent(event);
		}
		finally
		{
			tx2.rollback();
		}

		//test outside tx
		eventService.publishEvent(event);

		assertSame("Received event is not expected one", event, pollEvent());
	}

	@Test
	public void testInitializationStartEvent() throws Exception
	{
		setExpectedEventClass(AfterInitializationStartEvent.class);
		final AfterInitializationStartEvent initializationStartEvent = new AfterInitializationStartEvent();
		eventService.publishEvent(initializationStartEvent);

		assertEquals("Received event is not expected one", initializationStartEvent, pollEvent());
	}



	@Test
	public void testInitializationEndEvent() throws Exception
	{
		setExpectedEventClass(AfterInitializationEndEvent.class);
		final AfterInitializationEndEvent initializationEndEvent = new AfterInitializationEndEvent();
		eventService.publishEvent(initializationEndEvent);

		assertSame("Received event is not expected one", initializationEndEvent, pollEvent());
	}

	/*
	 * If this test will fail often we have to set a specific user to session and filter for events having this session
	 * user.
	 */
	@Test
	public void testSessionEvents() throws Exception
	{
		setExpectedEventClass(AfterSessionCreationEvent.class);
		final JaloSession anon = JaloConnection.getInstance().createAnonymousCustomerSession();
		assertEquals("Received event is not of expected class", AfterSessionCreationEvent.class, pollEvent().getClass());

		setExpectedEventClass(AfterSessionUserChangeEvent.class);
		anon.setUser(UserManager.getInstance().getUserByLogin("admin"));
		assertEquals("Received event is not of expected class", AfterSessionUserChangeEvent.class, pollEvent().getClass());

		setExpectedEventClass(BeforeSessionCloseEvent.class);
		anon.close();
		assertEquals("Received event is not of expected class", BeforeSessionCloseEvent.class, pollEvent().getClass());
	}


	private static class CustomEvent extends AbstractEvent
	{
		//
	}

	@SuppressWarnings("unused")
	private static class CustomAlwaysClusterEvent extends AbstractEvent implements ClusterAwareEvent
	{
		public final String id;

		public CustomAlwaysClusterEvent(final String id)
		{
			super();
			this.id = id;
		}

		@Override
		public boolean publish(final int sourceNodeId, final int targetNodeId)
		{
			return targetNodeId != 10;
		}

	}

	private static class CustomTxEvent extends AbstractEvent implements TransactionAwareEvent
	{
		private final String id;
		private final boolean onCommit;

		public CustomTxEvent(final boolean publishOnCommit, final String id)
		{
			super();
			this.id = id;
			this.onCommit = publishOnCommit;
		}

		@Override
		public Object getId()
		{
			return id;
		}


		@Override
		public boolean publishOnCommitOnly()
		{
			return onCommit;
		}
	}


}
