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

package de.hybris.platform.sap.orderexchange.mocks;

import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.Date;
import java.util.Map;



/**
 * Mock to be used for spring tests
 */
public class MockBusinessProcessService implements de.hybris.platform.processengine.BusinessProcessService
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#createProcess(java.lang.String, java.lang.String)
	 */
	public <T extends BusinessProcessModel> T createProcess(final String arg0, final String arg1)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#createProcess(java.lang.String, java.lang.String,
	 * java.util.Map)
	 */
	public <T extends BusinessProcessModel> T createProcess(final String arg0, final String arg1, final Map<String, Object> arg2)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#getProcess(java.lang.String)
	 */
	public <T extends BusinessProcessModel> T getProcess(final String arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.processengine.BusinessProcessService#restartProcess(de.hybris.platform.processengine.model.
	 * BusinessProcessModel, java.lang.String)
	 */
	public void restartProcess(final BusinessProcessModel arg0, final String arg1)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#startProcess(de.hybris.platform.processengine.model.
	 * BusinessProcessModel)
	 */
	public void startProcess(final BusinessProcessModel arg0)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#startProcess(java.lang.String, java.lang.String)
	 */
	public <T extends BusinessProcessModel> T startProcess(final String arg0, final String arg1)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#startProcess(java.lang.String, java.lang.String,
	 * java.util.Map)
	 */
	public <T extends BusinessProcessModel> T startProcess(final String arg0, final String arg1, final Map<String, Object> arg2)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#triggerEvent(java.lang.String)
	 */
	public void triggerEvent(final String arg0)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#triggerEvent(de.hybris.platform.processengine.model.
	 * BusinessProcessModel, java.lang.String)
	 */
	public void triggerEvent(final BusinessProcessModel arg0, final String arg1)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.processengine.BusinessProcessService#triggerEvent(java.lang.String, java.util.Date)
	 */
	public void triggerEvent(final String arg0, final Date arg1)
	{

	}

}
