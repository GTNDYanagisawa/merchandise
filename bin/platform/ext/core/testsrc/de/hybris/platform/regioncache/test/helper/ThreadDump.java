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
package de.hybris.platform.regioncache.test.helper;

import java.io.PrintStream;
import java.util.Map;


public class ThreadDump
{

	public static void dumpThreads(final PrintStream writer)
	{
		DeadlockDetector.printDeadlocks(writer);
		final Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
		for (final Thread thread : traces.keySet())
		{
			writer.println(String.format("\nThread %s@%d: (state = %s)", thread.getName(), Long.valueOf(thread.getId()),
					thread.getState()));
			for (final StackTraceElement stackTraceElement : traces.get(thread))
			{
				writer.println(" - " + stackTraceElement);
			}
		}
	}

}
