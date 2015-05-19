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
package de.hybris.platform.b2b.mock;

import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.Date;
import java.util.Map;

import org.junit.Ignore;


@Ignore
public class MockBusinessProcessService implements BusinessProcessService
{
	@Override
	public BusinessProcessModel startProcess(final String string, final String string1)
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}


	@Override
	public <T extends BusinessProcessModel> T startProcess(final String processCode, final String processDefinitionName,
			final Map<String, Object> contextParameters)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void startProcess(final BusinessProcessModel businessProcessModel)
	{
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public BusinessProcessModel getProcess(final String string)
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}

	@Override
	public void triggerEvent(final BusinessProcessModel businessProcessModel, final String string)
	{
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public void triggerEvent(final String string)
	{
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	@Override
	public void triggerEvent(final String string, final Date date)
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T extends BusinessProcessModel> T createProcess(final String processCode, final String processDefinitionName,
			final Map<String, Object> contextParameters)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}


	@Override
	public BusinessProcessModel createProcess(final String string, final String string1)
	{
		return null; // To change body of implemented methods use File |
						 // Settings | File Templates.
	}

	@Override
	public void restartProcess(final BusinessProcessModel businessProcessModel, final String string)
	{
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}
}
