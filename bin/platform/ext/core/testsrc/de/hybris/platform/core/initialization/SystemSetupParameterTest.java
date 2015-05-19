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
import de.hybris.platform.core.initialization.testbeans.SystemSetupParameterTestBean;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


@UnitTest
public class SystemSetupParameterTest extends AbstractSystemSetupTest
{
	@Test
	public void testParameterMap()
	{
		final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		parameterMap.put(SystemSetupParameterTestBean.TEST_PARAMETER_KEY, new String[]
		{ SystemSetupParameterTestBean.TEST_PARAMETER_VALUE3 });
		final SystemSetupContext systemSetupContext = new SystemSetupContext(parameterMap, Type.ALL, Process.ALL,
				SystemSetupParameterTestBean.SYSTEM_SETUP_PARAMETER_TEST_EXTENSION);

		boolean exceptionWasThrown = false;
		try
		{
			systemSetupCollector.executeMethods(systemSetupContext);
		}
		catch (final Exception e)
		{
			exceptionWasThrown = true;
			assertEquals(SystemSetupParameterTestBean.TEST_PARAMETER_VALUE3, e.getCause().getMessage());
		}

		assertTrue(exceptionWasThrown);
	}

	@Test
	public void testParameterMapWithDefaults()
	{
		final SystemSetupContext systemSetupContext = new SystemSetupContext(null, Type.ALL, Process.ALL,
				SystemSetupParameterTestBean.SYSTEM_SETUP_PARAMETER_TEST_EXTENSION);

		boolean exceptionWasThrown = false;
		try
		{
			exceptionWasThrown = true;
			systemSetupCollector.executeMethods(systemSetupContext);
		}
		catch (final Exception e)
		{
			assertEquals(SystemSetupParameterTestBean.TEST_PARAMETER_VALUE2, e.getCause().getMessage());
		}
		assertTrue(exceptionWasThrown);
	}
}
