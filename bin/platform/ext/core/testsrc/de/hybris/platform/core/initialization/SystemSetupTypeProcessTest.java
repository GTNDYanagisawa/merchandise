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
package de.hybris.platform.core.initialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.testbeans.SystemSetupTypeProcessTestBean;

import org.junit.Test;


@UnitTest
public class SystemSetupTypeProcessTest extends AbstractSystemSetupTest
{

	@Test
	public void testEssentialInit()
	{
		final SystemSetupContext systemSetupContext = new SystemSetupContext(null, Type.ESSENTIAL, Process.INIT,
				SystemSetupTypeProcessTestBean.SYSTEM_SETUP_TYPE_PROCESS_TEST_EXTENSION);

		boolean exceptionWasThrown = false;
		try
		{
			exceptionWasThrown = true;
			systemSetupCollector.executeMethods(systemSetupContext);
		}
		catch (final Exception e)
		{
			assertEquals(e.getCause().getMessage(), SystemSetupTypeProcessTestBean.ESSENTIAL_INIT);
		}

		assertTrue(exceptionWasThrown);
	}

	@Test
	public void testProjectInit()
	{
		final SystemSetupContext systemSetupContext = new SystemSetupContext(null, Type.PROJECT, Process.INIT,
				SystemSetupTypeProcessTestBean.SYSTEM_SETUP_TYPE_PROCESS_TEST_EXTENSION);

		boolean exceptionWasThrown = false;
		try
		{
			exceptionWasThrown = true;
			systemSetupCollector.executeMethods(systemSetupContext);
		}
		catch (final Exception e)
		{
			assertEquals(e.getCause().getMessage(), SystemSetupTypeProcessTestBean.PROJECT_INIT);
		}
		assertTrue(exceptionWasThrown);
	}

	@Test
	public void testEssentialUpdate()
	{
		final SystemSetupContext systemSetupContext = new SystemSetupContext(null, Type.ESSENTIAL, Process.UPDATE,
				SystemSetupTypeProcessTestBean.SYSTEM_SETUP_TYPE_PROCESS_TEST_EXTENSION);

		boolean exceptionWasThrown = false;
		try
		{
			exceptionWasThrown = true;
			systemSetupCollector.executeMethods(systemSetupContext);
		}
		catch (final Exception e)
		{
			assertEquals(e.getCause().getMessage(), SystemSetupTypeProcessTestBean.ESSENTIAL_UPDATE);
		}
		assertTrue(exceptionWasThrown);
	}

	@Test
	public void testProjectUpdate()
	{
		final SystemSetupContext systemSetupContext = new SystemSetupContext(null, Type.PROJECT, Process.UPDATE,
				SystemSetupTypeProcessTestBean.SYSTEM_SETUP_TYPE_PROCESS_TEST_EXTENSION);

		boolean exceptionWasThrown = false;
		try
		{
			exceptionWasThrown = true;
			systemSetupCollector.executeMethods(systemSetupContext);
		}
		catch (final Exception e)
		{
			assertEquals(e.getCause().getMessage(), SystemSetupTypeProcessTestBean.PROJECT_UPDATE);
		}
		assertTrue(exceptionWasThrown);
	}
}
