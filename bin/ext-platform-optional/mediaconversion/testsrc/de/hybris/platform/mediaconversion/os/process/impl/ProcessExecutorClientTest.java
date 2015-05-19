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
package de.hybris.platform.mediaconversion.os.process.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaconversion.mock.MockConfigurationService;
import de.hybris.platform.mediaconversion.os.ProcessExecutor;
import de.hybris.platform.mediaconversion.os.process.AbstractProcessExecutorTestCase;

import java.rmi.registry.Registry;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;


/**
 * @author pohl
 */
@UnitTest
public class ProcessExecutorClientTest extends AbstractProcessExecutorTestCase
{

	private static final Logger LOG = Logger.getLogger(ProcessExecutorClientTest.class);
	private static DefaultRMIRegistryService rmiService;
	private static DefaultRemoteDrain remoteDrain;

	public static DefaultRMIRegistryService startRegistry()
	{
		final int port = (new Random()).nextInt(Registry.REGISTRY_PORT) + Registry.REGISTRY_PORT;
		final DefaultRMIRegistryService regService = new DefaultRMIRegistryService();
		regService.setPort(port);
		regService.init();
		return regService;
	}

	@BeforeClass
	public static void init()
	{
		ProcessExecutorClientTest.rmiService = ProcessExecutorClientTest.startRegistry();
		ProcessExecutorClientTest.remoteDrain = new DefaultRemoteDrain();
		ProcessExecutorClientTest.remoteDrain.setRmiRegistryService(ProcessExecutorClientTest.rmiService);
		ProcessExecutorClientTest.remoteDrain.setProcessContextRegistry(new DefaultProcessContextRegistry());
		ProcessExecutorClientTest.remoteDrain.init();
	}

	@AfterClass
	public static void destroy()
	{
		ProcessExecutorClientTest.remoteDrain.destroy();
		ProcessExecutorClientTest.rmiService.destroy();
	}

	/**
	 * @see de.hybris.platform.mediaconversion.os.process.AbstractProcessExecutorTestCase#createExecutor()
	 */
	@Override
	protected ProcessExecutor createExecutor() throws Exception
	{
		LOG.debug("Creating RMIProcessExecutorClient.");
		return new ProcessExecutorClient(new MockConfigurationService().getConfiguration(), ProcessExecutorClientTest.rmiService,
				ProcessExecutorClientTest.remoteDrain.getProcessContextRegistry(), "junit");
	}

	/**
	 * @see de.hybris.platform.mediaconversion.os.process.AbstractProcessExecutorTestCase#amountOfThreads()
	 */
	@Override
	protected int amountOfThreads()
	{
		return 50;
	}

}
