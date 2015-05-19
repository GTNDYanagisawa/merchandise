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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.mediaconversion.os.ProcessContext;
import de.hybris.platform.mediaconversion.os.ProcessExecutor;
import de.hybris.platform.testframework.HybrisJUnit4Test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * @author pohl
 */
@IntegrationTest
public class ProcessExecutorTest extends HybrisJUnit4Test
{

	private static final Logger LOG = Logger.getLogger(ProcessExecutorTest.class);

	private ProcessExecutor processExecutor;

	@Override
	@Before
	public void init()
	{
		super.init();
		this.processExecutor = Registry.getApplicationContext().getBean(ProcessExecutor.class);
		LOG.debug("Using ProcessExecutor: " + this.processExecutor);
	}

	@Test
	public void testEcho() throws IOException
	{
		final String msg = "on_windows_boxes_cmd_/C_echo_puts_the_whole_string_in_double_quotes_"
				+ "if_there_are_spaces_in_it...so_only_one_word_here";
		final StringDrain out = new StringDrain(false);
		final StringDrain err = new StringDrain(false);
		final int extVal = this.processExecutor.execute(new ProcessContext(EchoCommandFactory.buildCommand(msg), null, null, out,
				err));
		Assert.assertEquals("Normal process termination.", 0, extVal);
		Assert.assertEquals("No error out.", "", err.toString());
		Assert.assertEquals("Stdout speaks..", msg, out.toString());
	}

	@Test
	public void testMultithreadedEcho() throws IOException, InterruptedException
	{
		AbstractProcessExecutorTestCase.execMultithreaded(this.processExecutor, 500,
				EchoCommandFactory.buildCommand("ProcessExecutorTest", "...a lot!"));
	}
}
