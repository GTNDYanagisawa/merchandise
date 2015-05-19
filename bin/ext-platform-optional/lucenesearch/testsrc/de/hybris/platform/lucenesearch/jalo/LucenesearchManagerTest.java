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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.JspContext;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * An isolated presenting how does the 'check.index.existence' flag is affecting
 * {@link LucenesearchManager#createEssentialData(Map, JspContext)} logic.
 * 
 */
@IntegrationTest
public class LucenesearchManagerTest extends HybrisJUnit4Test
{

	@Mock
	private JspContext jspContext;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
	}

	private class TestLucenesearchManager extends LucenesearchManager
	{
		@Override
		public void createHMCIndex(final java.util.Map values, final de.hybris.platform.util.JspContext jspc) throws Exception
		{
			throw new CreateHMCException();
		}

		@Override
		protected void createRebuildCronjob()
		{
			//nop
		}

		@Override
		protected void createUpdateCronjob()
		{
			//nop
		}
	}


	@Test
	public void testHMCIndexCreationCalledWhenInit() throws Exception
	{
		final LucenesearchManager manager = new TestLucenesearchManager()
		{
			@Override
			boolean isInitForMaster(final java.util.Map values)
			{
				return true;
			}

		};

		final Map nomatterWhatMap = new HashMap();
		try
		{
			manager.createEssentialData(nomatterWhatMap, jspContext);
			Assert.fail("Whenever there is init,  recreation is performed regardless other attributtes");
		}
		catch (final CreateHMCException e)
		{
			//ok
		}
	}


	@Test
	public void testHMCIndexCreationCalledWhenUpdateIndexConfigurationSetToTrue() throws Exception
	{
		final LucenesearchManager manager = new TestLucenesearchManager()
		{
			@Override
			boolean isInitForMaster(final java.util.Map values)
			{
				return false;
			}

		};

		final Map updatemap = new HashMap();
		updatemap.put("initmethod", "update");
		updatemap.put("update.index.configuration", "true");


		try
		{
			manager.createEssentialData(updatemap, jspContext);
			Assert.fail("IndexExistenceCheck was specified to false (not true) - create hmc index should be called");
		}
		catch (final CreateHMCException e)
		{
			//ok
		}

	}

	@Test
	public void testHMCIndexCreationCalledWhenUpdateIndexConfigurationSetToFalse() throws Exception
	{
		final LucenesearchManager manager = new TestLucenesearchManager()
		{
			@Override
			boolean isInitForMaster(final java.util.Map values)
			{
				return false;
			}
		};

		final Map updatemap = new HashMap();
		updatemap.put("initmethod", "update");
		updatemap.put("update.index.configuration", "false");


		manager.createEssentialData(updatemap, jspContext);

	}



	class CreateHMCException extends RuntimeException
	{
		//
	}
}
