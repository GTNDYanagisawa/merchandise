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
package de.hybris.platform.jalo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.test.TestThreadsHolder;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Sanity check for {@link SessionContext} thread modification safety due to proving PLA-10825 already fixed.
 */
@IntegrationTest
public class SessionContextTest extends HybrisJUnit4Test
{
	private final static Logger LOG = Logger.getLogger(SessionContextTest.class);

	@Test
	public void testCartAccessViaSessionContext()
	{
		assertNoCart(jaloSession);

		final Cart autoCreated1 = jaloSession.getCart();
		assertNotNull(autoCreated1);
		assertCart(jaloSession, autoCreated1);

		jaloSession.removeCart();
		assertNoCart(jaloSession);

		final Cart autoCreated2 = jaloSession.getCart();
		assertNotNull(autoCreated2);
		assertCart(jaloSession, autoCreated2);

		jaloSession.setAttribute(JaloSession.CART, null);
		assertNoCart(jaloSession);

		final Cart autoCreated3 = jaloSession.getCart();
		assertNotNull(autoCreated3);
		assertCart(jaloSession, autoCreated3);
	}

	private void assertNoCart(final JaloSession jaloSession)
	{
		assertFalse(jaloSession.hasCart());
		assertNull(jaloSession.getAttribute(JaloSession.CART));
	}

	private void assertCart(final JaloSession jaloSession, final Cart cart)
	{
		assertTrue(jaloSession.hasCart());
		assertEquals(cart, jaloSession.getAttribute(JaloSession.CART));
	}

	@Test
	public void testConcurrentSessionAccess()
	{
		final int[] rands = new int[1000];
		final Random rand = new Random(System.nanoTime());
		for (int i = 0; i < 1000; i++)
		{
			rands[i] = (Math.abs(rand.nextInt()) % 1000);
		}

		testConcurrentSessionReadAccess(//
				rands, 1000, //attributes
				100, // threads
				1000, // turns
				120 // seconds to wait for completion
		);
	}

	@Ignore("Test relies highly on the system clock - therefore it is not reliable")
	@Test
	public void testSetCurrentTime()
	{
		// Test - offset time adjustment
		final Date pastTimeInstant = new Date(System.currentTimeMillis() - 1);
		final SessionContext sess = jaloSession.getSessionContext();
		sess.setCurrentTime(pastTimeInstant);
		Date adjustedTime = sess.getAdjustedCurrentTime();
		long offset = adjustedTime.getTime() - System.currentTimeMillis();
		assertEquals(-1, offset);
		assertEquals(-1, sess.getTimeOffset());
		assertEquals(Long.valueOf(offset), sess.getAttribute(SessionContext.TIMEOFFSET));

		// Test 0 offset time adjustment
		final Date now = new Date();
		sess.setCurrentTime(now);
		adjustedTime = sess.getAdjustedCurrentTime();
		offset = adjustedTime.getTime() - System.currentTimeMillis();
		assertEquals(0, offset);
		assertEquals(0, sess.getTimeOffset());
		assertEquals(Long.valueOf(offset), sess.getAttribute(SessionContext.TIMEOFFSET));

		// Test + offset time adjustment
		final Date futureTimeInstant = new Date(System.currentTimeMillis() + 1);
		sess.setCurrentTime(futureTimeInstant);
		adjustedTime = sess.getAdjustedCurrentTime();
		offset = adjustedTime.getTime() - System.currentTimeMillis();
		assertEquals(1, offset);
		assertEquals(1, sess.getTimeOffset());
		assertEquals(Long.valueOf(offset), sess.getAttribute(SessionContext.TIMEOFFSET));

	}



	@Test
	public void testSetTimeOffsetAttr()
	{
		// Test - offset time adjustment
		try
		{
			final SessionContext sess = jaloSession.createLocalSessionContext();
			sess.setAttribute(SessionContext.TIMEOFFSET, Long.valueOf(100));

			assertNotSame(SessionContext.ZERO_TIME_OFFSET, Long.valueOf(sess.getAdjustedCurrentTime().getTime()));
		}
		finally
		{
			jaloSession.removeLocalSessionContext();
		}

	}


	private void testConcurrentSessionReadAccess(final int[] rands, final int MAX_ATTRIBUTES, final int THREADS, final int TURNS,
			final int WAIT_SECONDS)
	{
		try
		{
			final JaloSession session = jaloSession;
			final Map<String, Object> dummySessionAttributes = new HashMap<String, Object>();
			for (int i = 0; i < MAX_ATTRIBUTES; i++)
			{
				dummySessionAttributes.put("key_" + i, "value_" + i);
			}
			session.setAttributes(dummySessionAttributes);

			//random access test
			final TestThreadsHolder<AbstractSessionRandomAccessRunner> randomAccessHolder = new TestThreadsHolder<AbstractSessionRandomAccessRunner>(//
					THREADS, //
					new de.hybris.platform.test.RunnerCreator<AbstractSessionRandomAccessRunner>()
					{
						int idx = 0;

						@Override
						public AbstractSessionRandomAccessRunner newRunner(final int threadNumber)
						{
							if ((++idx % 2) == 0)
							{
								return new SourceSessionRandomAccessRunner(rands, TURNS, session);
							}
							else
							{
								return new TargetSessionRandomAccessRunner(rands, TURNS, session);
							}
						}
					});

			randomAccessHolder.startAll();
			org.junit.Assert.assertTrue("not all test threads shut down orderly", randomAccessHolder.waitAndDestroy(WAIT_SECONDS));
			org.junit.Assert.assertEquals("found worker errors", Collections.EMPTY_MAP, randomAccessHolder.getErrors());
			for (final AbstractSessionRandomAccessRunner runner : randomAccessHolder.getRunners())
			{
				org.junit.Assert.assertEquals("runner " + runner + " had error turns [" + runner.errorTurns.size() + "]",
						Collections.EMPTY_LIST, runner.errorTurns);
			}
		}
		finally
		{
			JaloSession.deactivate();
		}
	}

	/**
	 * Base runner for modifying session context in some way
	 */
	static private abstract class AbstractSessionRandomAccessRunner implements Runnable
	{

		private final int turns;
		protected final JaloSession localJaloSession;
		protected final int[] rands;

		protected volatile List<Exception> errorTurns = new ArrayList<Exception>(10);

		protected AbstractSessionRandomAccessRunner(final int[] rands, final int turns, final JaloSession session)
		{
			this.rands = rands;
			this.turns = turns;
			this.localJaloSession = session;
		}

		@Override
		public void run()
		{
			de.hybris.platform.core.Registry.setCurrentTenant(localJaloSession.getTenant());

			final int max = turns;
			final List<Exception> recordedErrorTurns = new LinkedList<Exception>();

			try
			{
				for (int i = 0; i < max && !Thread.currentThread().isInterrupted(); i++)
				{
					try
					{
						modifySessionContext(i);
					}
					catch (final Exception e)
					{
						LOG.error(e.getMessage(), e);
						recordedErrorTurns.add(e);
						break;
					}
				}
				this.errorTurns = recordedErrorTurns; // volatile write
			}
			finally
			{
				Registry.unsetCurrentTenant();
				//
			}
		}

		abstract protected void modifySessionContext(final int i);

	}


	/**
	 * Session attribute remove runner
	 */
	static private class SourceSessionRandomAccessRunner extends AbstractSessionRandomAccessRunner
	{
		SourceSessionRandomAccessRunner(final int[] rands, final int turns, final JaloSession session)
		{
			super(rands, turns, session);
		}

		@Override
		protected void modifySessionContext(final int i)
		{

			localJaloSession.getSessionContext().removeAttribute("key_" + rands[i]);
		}
	}

	/**
	 * Session attributes putAll and remove afterwards runner
	 */
	static private class TargetSessionRandomAccessRunner extends AbstractSessionRandomAccessRunner
	{
		TargetSessionRandomAccessRunner(final int[] rands, final int turns, final JaloSession session)
		{
			super(rands, turns, session);
		}

		@Override
		protected void modifySessionContext(final int i)
		{

			try
			{
				final SessionContext sessionContext = localJaloSession.createLocalSessionContext();
				sessionContext.removeAttribute("key_" + rands[i]);
			}
			finally
			{
				JaloSession.getCurrentSession().removeLocalSessionContext();
			}
		}
	}


}
