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
package de.hybris.platform.impex.jalo;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.imp.ImpExWorkerResult;
import de.hybris.platform.impex.jalo.imp.MultiThreadedImpExImportReader;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.testframework.TestUtils;

import org.junit.Test;


/**
 * Tests potential thread pool leak when running multi-threaded ImpEx with errors caught by workers.
 * 
 * <b>See PLA-12491 for the whole story!</b>
 */
@IntegrationTest
public class PLA_12491_Test extends HybrisJUnit4Test
{

	@Test
	public void testErrorInWorker()
	{
		final int THREADS = 50;
		final int LINES = THREADS * 20;
		final int ERROR_LINE_NR = THREADS + 1;
		final int MAX_WAIT_SEC = 30;

		final TestMTIR reader = createTestReader(//
				createTestLines(LINES, ERROR_LINE_NR), //
				THREADS,//
				ERROR_LINE_NR//
		);

		try
		{
			TestUtils.disableFileAnalyzer("PLA-12491 test requires item creation exception to be thrown");
			try
			{
				reader.readAll();
			}
			catch (final Exception e)
			{
				// for now we don't care - important is the fact of all threads having finished
				System.err.println("error from readAll() : " + e.getMessage());
			}

			waitForWorkersToFinish(reader, MAX_WAIT_SEC);

			assertTrue(reader.isReaderFinished());
			assertTrue(reader.isResultProcessorFinished());
			assertTrue(reader.isAllWorkerFinished());
		}
		finally
		{
			TestUtils.enableFileAnalyzer();
		}
	}

	void waitForWorkersToFinish(final TestMTIR reader, final int seconds)
	{
		int tick = 0;
		do
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (final InterruptedException e)
			{
				Thread.currentThread().interrupt();
				break;
			}
		}
		while (++tick < seconds && !allWorkersFinished(reader));
	}

	boolean allWorkersFinished(final TestMTIR reader)
	{
		return reader.isReaderFinished() && reader.isResultProcessorFinished() && reader.isAllWorkerFinished();
	}

	String createTestLines(final int amount, final int errorPos)
	{
		final StringBuilder stringBuilder = new StringBuilder("INSERT Title; code").append('\n');
		for (int i = 0; i < amount; i++)
		{
			if (errorPos == i) // use existing code to cause error
			{
				stringBuilder.append(";TTT").append(i - 1).append('\n');
			}
			else
			{
				stringBuilder.append(";TTT").append(i).append('\n');
			}
		}
		return stringBuilder.toString();
	}

	static class TestMTIR extends MultiThreadedImpExImportReader
	{
		volatile int errorLineNr;
		volatile boolean gotWorkerError = false;

		TestMTIR(final String lines, final int threads, final int errorLineNr)
		{
			super(lines);
			setMaxThreads(threads);
			this.errorLineNr = errorLineNr;
		}

		@Override
		protected boolean processPendingResult(final ImpExWorkerResult result)
		{
			final boolean ret = super.processPendingResult(result);
			if (result.getError() != null)
			{
				gotWorkerError = true;
			}
			return ret;
		}

		@Override
		protected boolean readLineFromWorker() throws ImpExException
		{
			final boolean notDone = super.readLineFromWorker();

			// after reaching the error line we have to slow down  
			// noticeably to avoid reading all lines before any worker
			// could throw a error !
			if (--errorLineNr <= 0 && !gotWorkerError)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (final InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
			return notDone;
		}

		@Override
		public boolean isAllWorkerFinished()
		{
			return super.isAllWorkerFinished();
		}

		@Override
		public boolean isReaderFinished()
		{
			return super.isReaderFinished();
		}

		@Override
		public boolean isResultProcessorFinished()
		{
			return super.isResultProcessorFinished();
		}
	}

	TestMTIR createTestReader(final String lines, final int threads, final int errorLineNr)
	{
		final TestMTIR reader = new TestMTIR(lines, threads, errorLineNr);
		return reader;
	}
}
