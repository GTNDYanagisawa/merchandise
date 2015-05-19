package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import org.apache.log4j.Logger;


public class SSCTimer
{
	private static final Logger LOG = Logger.getLogger(SSCTimer.class);


	private long startTime;
	private long endTime;


	private String step;

	public SSCTimer()
	{
		super();
		start("");
	}

	public void stop()
	{
		endTime = System.nanoTime();
		logTime();
	}

	private void logTime()
	{
		if (!LOG.isDebugEnabled())
		{
			return;
		}
		final long timeInNanos = endTime - startTime;
		final long timeInMs = timeInNanos / (1000 * 1000);

		LOG.debug("Call to SSC (" + step + ") took " + timeInMs + "ms");
	}

	public void start(final String step)
	{
		this.step = step;
		startTime = System.nanoTime();
	}

}
