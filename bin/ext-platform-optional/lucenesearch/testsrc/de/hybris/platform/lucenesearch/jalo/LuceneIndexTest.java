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
package de.hybris.platform.lucenesearch.jalo;

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.lucenesearch.constants.LucenesearchConstants;
import de.hybris.platform.test.TestThreadsHolder;
import de.hybris.platform.test.TestThreadsHolder.RunnerCreator;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@PerformanceTest
public class LuceneIndexTest extends HybrisJUnit4Test
{
	/**
	 * 
	 */
	private static final String LUCENESEARCH_TEST = "LucenesearchTest1";

	private static final Logger LOG = Logger.getLogger(LuceneIndexTest.class.getName());

	private static final int TURNS = 1;
	final int THREADS = 50;
	final int WAIT_SECONDS = 60;


	private LucenesearchManager manager;
	private final List<Product> products = new ArrayList<Product>(THREADS);
	private SessionContext ctxDe, ctxEn;
	private Language langEn, langDe;
	private LuceneIndex index;

	//private org.apache.log4j.Level originalLevel;

	/**
	 * Creates a {@link #THREADS} products and index with a multilanguage configuration based on the product's Code and
	 * Name
	 */
	@Before
	public void prepareIndex() throws ConsistencyCheckException
	{
		manager = (LucenesearchManager) jaloSession.getExtensionManager().getExtension(LucenesearchConstants.EXTENSIONNAME);

		langDe = jaloSession.getC2LManager().createLanguage("test-de");
		langEn = jaloSession.getC2LManager().createLanguage("test-en");
		ctxDe = jaloSession.createSessionContext();
		ctxDe.setLanguage(langDe);
		ctxEn = jaloSession.createSessionContext();
		ctxEn.setLanguage(langEn);
		for (int i = 0; i < THREADS; i++)
		{
			final Product product = jaloSession.getProductManager().createProduct("test product code " + i);
			product.setName(ctxDe, "Der unverselle i905D bietet schnellen und einfachen Fotodirektdruck von Speicherkarten "
					+ "oder zu PictBridge und Bubble Jet Direct kompatiblen Digitalkameras, bzw. Camcordern.");
			product.setName(ctxEn, "The flexible i905D provides fast and easy direct photo printing from memory "
					+ "cards or PictBridge and Bubble Jet Direct compatible digital cameras.");
			products.add(product);
		}
		index = manager.createLuceneIndex(LUCENESEARCH_TEST);
		index.createLanguageConfiguration(langDe);
		index.createLanguageConfiguration(langEn);
		final ComposedType productType = jaloSession.getTypeManager().getComposedType(Product.class);
		index.createIndexConfiguration(productType,
				Arrays.asList(productType.getAttributeDescriptor(Product.CODE), productType.getAttributeDescriptor(Product.NAME)));

		index.rebuildIndex();

		//originalLevel = Logger.getLogger(LuceneIndex.class).getLevel();
		//Logger.getLogger(LuceneIndex.class).setLevel(Level.DEBUG);
		//LOG.info("Set up a DEBUG level for a LuceneIndex ");
	}

	@After
	public void after() throws ConsistencyCheckException
	{
		//		Logger.getLogger(LuceneIndex.class).setLevel(originalLevel);
		//		LOG.info("Reverted a " + originalLevel + " level for a LuceneIndex ");
	}

	@Test
	public void testConcurrentUpdate()
	{

		final TestThreadsHolder<LuceneIndexUpdateRunner> randomAccessHolder = new TestThreadsHolder<LuceneIndexUpdateRunner>(//
				THREADS, //
				new RunnerCreator<LuceneIndexUpdateRunner>()
				{
					int idx = 0;

					@Override
					public LuceneIndexUpdateRunner newRunner(final int threadNumber)
					{

						return new LuceneIndexUpdateRunner(products.get(idx++ % THREADS).getPK(), LUCENESEARCH_TEST);
					}
				});

		randomAccessHolder.startAll();
		int alive = randomAccessHolder.getAlive();
		int changedWhileInWait = alive;
		do
		{
			try
			{
				Thread.sleep(WAIT_SECONDS * 1000);
			}
			catch (final InterruptedException e)
			{
				//ignoring!!!!
			}
			changedWhileInWait = alive - randomAccessHolder.getAlive();
			LOG.info("Waited " + WAIT_SECONDS + " seconds for finishing  (" + changedWhileInWait + " of " + alive + ") threads ");
			alive = randomAccessHolder.getAlive();
		}
		while (changedWhileInWait > 0 && randomAccessHolder.getAlive() > 0);

		org.junit.Assert.assertTrue("not all test threads shut down orderly", randomAccessHolder.stopAndDestroy(15));
		org.junit.Assert.assertEquals("found worker errors", Collections.EMPTY_MAP, randomAccessHolder.getErrors());
		for (final LuceneIndexUpdateRunner runner : randomAccessHolder.getRunners())
		{
			org.junit.Assert.assertEquals("runner " + runner + " had error turns", Collections.EMPTY_LIST, runner.errorTurns);
		}

	}

	@Test
	public void testConcurrentRemove()
	{

		final TestThreadsHolder<LuceneIndexUpdateRunner> randomAccessHolder = new TestThreadsHolder<LuceneIndexUpdateRunner>(//
				THREADS, //
				new RunnerCreator<LuceneIndexUpdateRunner>()
				{
					int idx = 0;

					@Override
					public LuceneIndexUpdateRunner newRunner(final int threadNumber)
					{
						if (idx % 2 == 0)
						{
							return new LuceneIndexRemoveRunner(products.get(idx++ % THREADS).getPK(), LUCENESEARCH_TEST);
						}
						else
						{
							return new LuceneIndexUpdateRunner(products.get(idx++ % THREADS).getPK(), LUCENESEARCH_TEST);
						}
					}
				});

		randomAccessHolder.startAll();
		int alive = randomAccessHolder.getAlive();
		int changedWhileInWait = alive;
		do
		{
			try
			{
				Thread.sleep(WAIT_SECONDS * 1000);
			}
			catch (final InterruptedException e)
			{
				//ignoring!!!!
			}
			changedWhileInWait = alive - randomAccessHolder.getAlive();
			LOG.info("Waited " + WAIT_SECONDS + " seconds for finishing  (" + changedWhileInWait + " of " + alive + ") threads ");
			alive = randomAccessHolder.getAlive();

		}
		while (changedWhileInWait > 0 && randomAccessHolder.getAlive() > 0);

		org.junit.Assert.assertTrue("not all test threads shut down orderly", randomAccessHolder.stopAndDestroy(15));
		org.junit.Assert.assertEquals("found worker errors", Collections.EMPTY_MAP, randomAccessHolder.getErrors());
		for (final LuceneIndexUpdateRunner runner : randomAccessHolder.getRunners())
		{
			org.junit.Assert.assertEquals("runner " + runner + " had error turns", Collections.EMPTY_LIST, runner.errorTurns);
		}

	}

	@Test
	public void testConcurrentRebuild()
	{

		final TestThreadsHolder<LuceneIndexUpdateRunner> randomAccessHolder = new TestThreadsHolder<LuceneIndexUpdateRunner>(//
				THREADS, //
				new RunnerCreator<LuceneIndexUpdateRunner>()
				{
					int idx = 0;

					@Override
					public LuceneIndexUpdateRunner newRunner(final int threadNumber)
					{
						if (idx % 3 == 0)
						{
							return new LuceneIndexRemoveRunner(products.get(idx++ % THREADS).getPK(), LUCENESEARCH_TEST);
						}
						else if (idx % 3 == 1)
						{
							return new LuceneIndexUpdateRunner(products.get(idx++ % THREADS).getPK(), LUCENESEARCH_TEST);
						}
						else
						{
							return new LuceneIndexRebuildRunner(LUCENESEARCH_TEST);
						}
					}
				});

		randomAccessHolder.startAll();
		int alive = randomAccessHolder.getAlive();
		int changedWhileInWait = alive;
		do
		{
			try
			{
				Thread.sleep(WAIT_SECONDS * 1000);
			}
			catch (final InterruptedException e)
			{
				//ignoring!!!!
			}
			changedWhileInWait = alive - randomAccessHolder.getAlive();
			LOG.info("Waited " + WAIT_SECONDS + " seconds for finishing  (" + changedWhileInWait + " of " + alive + ") threads ");
			alive = randomAccessHolder.getAlive();

		}
		while (changedWhileInWait > 0 && randomAccessHolder.getAlive() > 0);

		org.junit.Assert.assertTrue("not all test threads shut down orderly", randomAccessHolder.stopAndDestroy(15));
		org.junit.Assert.assertEquals("found worker errors", Collections.EMPTY_MAP, randomAccessHolder.getErrors());
		for (final LuceneIndexUpdateRunner runner : randomAccessHolder.getRunners())
		{
			org.junit.Assert.assertEquals("runner " + runner + " had error turns", Collections.EMPTY_LIST, runner.errorTurns);
		}

	}



	static private class LuceneIndexUpdateRunner implements Runnable
	{
		protected final String indexCode;

		protected final PK itemPK;

		private final Tenant tenant;

		private volatile List<Exception> errorTurns;

		LuceneIndexUpdateRunner(final PK itemPK, final String indexCode)
		{
			this.indexCode = indexCode;
			this.itemPK = itemPK;
			this.tenant = Registry.getCurrentTenantNoFallback();
		}

		@Override
		public void run()
		{
			try
			{
				Registry.setCurrentTenant(tenant);
				final List<Exception> recordedErrorTurns = new LinkedList<Exception>();

				for (int i = 0; i < TURNS && !Thread.currentThread().isInterrupted(); i++)
				{
					modifyIndex(recordedErrorTurns);
				}
				this.errorTurns = recordedErrorTurns; // volatile write
			}
			finally
			{
				Registry.unsetCurrentTenant();
			}

		}

		/**
		 * Updates an index for one of the product's
		 */
		protected void modifyIndex(final List<Exception> recordedErrorTurns)
		{
			try
			{
				final LuceneIndex index = LucenesearchManager.getInstance().getLuceneIndex(indexCode);
				index.updateIndexForItem(JaloSession.getCurrentSession().getItem(itemPK));

			}
			catch (final Exception e)
			{
				e.printStackTrace();
				LOG.error(e);
				recordedErrorTurns.add(e);
				Thread.currentThread().interrupt();
			}
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + " idx " + indexCode + " product " + itemPK;
		}
	}

	static private class LuceneIndexRemoveRunner extends LuceneIndexUpdateRunner
	{

		LuceneIndexRemoveRunner(final PK itemPK, final String indexCode)
		{
			super(itemPK, indexCode);
		}

		@Override
		protected void modifyIndex(final List<Exception> recordedErrorTurns)
		{
			try
			{
				final LuceneIndex index = LucenesearchManager.getInstance().getLuceneIndex(indexCode);
				index.removeItemFromIndex(JaloSession.getCurrentSession().getItem(itemPK));

			}
			catch (final Exception e)
			{
				e.printStackTrace();
				LOG.error(e);
				recordedErrorTurns.add(e);
				Thread.currentThread().interrupt();
			}
		}
	}

	static private class LuceneIndexRebuildRunner extends LuceneIndexUpdateRunner
	{

		LuceneIndexRebuildRunner(final String indexCode)
		{
			super(null, indexCode);
		}

		@Override
		protected void modifyIndex(final List<Exception> recordedErrorTurns)
		{
			try
			{
				final LuceneIndex index = LucenesearchManager.getInstance().getLuceneIndex(indexCode);
				index.rebuildIndex();

			}
			catch (final Exception e)
			{
				e.printStackTrace();
				LOG.error(e);
				recordedErrorTurns.add(e);
				Thread.currentThread().interrupt();
			}
		}
	}

}
