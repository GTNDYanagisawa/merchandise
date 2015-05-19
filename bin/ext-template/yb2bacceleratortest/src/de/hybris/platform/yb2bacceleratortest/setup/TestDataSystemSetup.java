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
package de.hybris.platform.yb2bacceleratortest.setup;


import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.yb2bacceleratortest.constants.YB2BAcceleratorTestConstants;
import de.hybris.platform.yb2bacceleratortest.orders.B2BAcceleratorTestOrderData;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This class provides hooks into the system's initialization and update processes.
 * 
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = YB2BAcceleratorTestConstants.EXTENSIONNAME)
public class TestDataSystemSetup extends AbstractSystemSetup
{
	private static final Logger LOG = Logger.getLogger(TestDataSystemSetup.class);

	private static final String CREATE_TEST_DATA = "createTestData";

	private B2BAcceleratorTestOrderData b2bAcceleratorTestOrderData;

	protected B2BAcceleratorTestOrderData getB2BAcceleratorTestOrderData()
	{
		return b2bAcceleratorTestOrderData;
	}

	@Required
	public void setB2BAcceleratorTestOrderData(final B2BAcceleratorTestOrderData b2bAcceleratorTestOrderData)
	{
		this.b2bAcceleratorTestOrderData = b2bAcceleratorTestOrderData;
	}

	/**
	 * Generates the Dropdown and Multi-select boxes for the projectdata import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(CREATE_TEST_DATA, "Create B2B Test Data", true));

		return params;
	}

	/**
	 * Implement this method to create initial objects. This method will be called by system creator during
	 * initialization and system update. Be sure that this method can be called repeatedly.
	 * 
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		// no essential data required
	}

	/**
	 * Implement this method to create data that is used in your project. This method will be called during the system
	 * initialization.
	 * 
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		if (getBooleanSystemSetupParameter(context, CREATE_TEST_DATA))
		{
			importImpexFile(context, "/yb2bacceleratortest/import/qa-organization.impex");

			LOG.info("Creating Test B2B Payment Subscriptions...");
			try
			{
				getB2BAcceleratorTestOrderData().createPaymentInfos();
			}
			catch (final Exception e)
			{
				LOG.error(e);
			}

			LOG.info("Creating Test B2B Orders now...");
			try
			{
				getB2BAcceleratorTestOrderData().createSampleOrders();
			}
			catch (final Exception e)
			{
				LOG.error(e);
			}
			LOG.info("Finished Creating B2B Orders");
		}
	}
}
