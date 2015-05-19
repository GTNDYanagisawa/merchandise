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
package de.hybris.platform.mediaconversion.os.process.factory;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaconversion.mock.MockConfigurationService;
import de.hybris.platform.mediaconversion.os.ProcessExecutor;
import de.hybris.platform.mediaconversion.os.config.DefaultOsConfigurationService;
import de.hybris.platform.mediaconversion.os.process.AbstractProcessExecutorTestCase;
import de.hybris.platform.mediaconversion.os.process.impl.DefaultProcessContextRegistry;
import de.hybris.platform.mediaconversion.os.process.impl.DefaultRMIRegistryService;
import de.hybris.platform.mediaconversion.os.process.impl.DefaultRemoteDrain;
import de.hybris.platform.mediaconversion.os.process.impl.ProcessExecutorClientTest;

import java.io.IOException;


/**
 * @author pohl
 */
@UnitTest
public class ProcessExecutorFactoryTest extends AbstractProcessExecutorTestCase
{

	private ProcessExecutorFactory factory;
	private DefaultRemoteDrain drainService;

	@Override
	public void setupExecutor() throws Exception
	{
		final DefaultRMIRegistryService regService = ProcessExecutorClientTest.startRegistry();
		this.factory = new ProcessExecutorFactory()
		{
			@Override
			protected String getTenantId()
			{
				return "junit";
			}

			@Override
			public void destroy()
			{
				super.destroy();
				regService.destroy();
			}
		};
		this.factory.setConfigurationService(new MockConfigurationService());
		this.factory.setLimit(10);
		this.factory.setRmiRegistryService(regService);
		this.factory.setProcessContextRegistry(new DefaultProcessContextRegistry());
		final DefaultOsConfigurationService osConfigurationService = new DefaultOsConfigurationService();
		osConfigurationService.setConfigurationService(this.factory.getConfigurationService());
		this.factory.setOsConfigurationService(osConfigurationService);

		this.drainService = new DefaultRemoteDrain();
		this.drainService.setRmiRegistryService(this.factory.getRmiRegistryService());
		this.drainService.setProcessContextRegistry(this.factory.getProcessContextRegistry());
		this.drainService.init();

		super.setupExecutor();
	}

	@Override
	public void cleanUp() throws IOException
	{
		this.drainService.destroy();
		this.factory.destroy();
	}

	@Override
	protected int amountOfThreads()
	{
		return 500;
	}

	@Override
	protected ProcessExecutor createExecutor() throws Exception
	{
		return this.factory.create();
	}
}
