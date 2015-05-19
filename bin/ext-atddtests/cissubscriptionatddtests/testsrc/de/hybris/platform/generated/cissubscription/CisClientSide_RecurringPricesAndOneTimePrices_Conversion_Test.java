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
package de.hybris.platform.generated.cissubscription;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.atddengine.framework.RobotSettings;
import de.hybris.platform.atddengine.framework.RobotTest;
import de.hybris.platform.atddengine.framework.RobotTestResult;
import de.hybris.platform.atddengine.framework.RobotTestSuite;
import de.hybris.platform.atddengine.framework.RobotTestSuiteFactory;
import de.hybris.platform.atddengine.framework.impl.DefaultPythonProvider;
import de.hybris.platform.atddengine.framework.impl.PythonAware;
import de.hybris.platform.atddengine.framework.impl.PythonRobotTestSuiteFactory;
import de.hybris.platform.atddengine.keywords.ImpExAdaptorAware;
import de.hybris.platform.atddengine.keywords.KeywordLibraryContext;
import de.hybris.platform.atddengine.keywords.KeywordLibraryContextHolder;
import de.hybris.platform.atddengine.keywords.RobotTestContext;
import de.hybris.platform.atddengine.keywords.RobotTestContextAware;
import de.hybris.platform.atddengine.keywords.impl.DefaultImpExAdaptor;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;


/*
 * This is a generated class. Do not edit, changes will be overriden.
 */
@SuppressWarnings("PMD")
@IntegrationTest
public class CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Test extends ServicelayerTest implements RobotTestContext
{
	public static RobotTestSuite robotTestSuite;
	
	private ModelService modelService;

	public static void startSuite() throws IOException
	{
		if (robotTestSuite == null)
		{
			final PythonAware pythonAware = new DefaultPythonProvider(Config.getParameter("atddengine.libraries-path"));

			final RobotSettings robotSettings = new RobotSettings();
			
			robotSettings.setOutputDir(new File(Config.getParameter("atddengine.report-path"), "cissubscription"));
			robotSettings.setLogName("CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Test-log");
			robotSettings.setOutputName("CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Test-output");
			robotSettings.setReportName("CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Test-report");
			
			final RobotTestSuiteFactory robotTestSuiteFactory = new PythonRobotTestSuiteFactory(pythonAware);

			robotTestSuite = robotTestSuiteFactory.parseTestSuite(robotSettings, new File(
					"/home/bamboouser/agent/agent-home/xml-data/build-dir/HYM-SUB4-JOB1/source/cissubscriptionatddtests/genresources/robottests/CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Test.txt"));
		}

		if (!robotTestSuite.isStarted())
		{
			robotTestSuite.start();
		}

	}

	@AfterClass
	public static void tearDownSuite()
	{
		robotTestSuite.close();
	}

	private RobotTest currentRobotTest;

	@Resource(name = "impExAdaptorHolder")
	private ImpExAdaptorAware impExAdaptorHolder;

	@Resource(name = "keywordLibraryContext")
	private KeywordLibraryContext keywordLibraryContext;

	@Resource(name = "robotTestContextHolder")
	private RobotTestContextAware robotTestContextHolder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.atddengine.keywords.RobotTestContext#getCurrentRobotTest()
	 */
	@Override
	public RobotTest getCurrentRobotTest()
	{
		return currentRobotTest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.atddengine.keywords.RobotTestContext#getProjectName()
	 */
	@Override
	public String getProjectName()
	{
		return "cissubscription";
	}

	@Before
	public void setUp() throws IOException
	{
		impExAdaptorHolder.setImpExAdaptor(new DefaultImpExAdaptor());
		
		robotTestContextHolder.setRobotTestContext(this);

		KeywordLibraryContextHolder.setKeywordLibraryContext(keywordLibraryContext);
		
		modelService = (ModelService)Registry.getApplicationContext().getBean("modelService");

		startSuite();
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To12()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To12";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To3_From4To6()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To3_From4To6";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To3_From4To4()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To3_From4To4";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To2_From3To3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To2_From3To3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To2_From3To5_From6To9()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To2_From3To5_From6To9";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To2_From3To4()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To2_From3To4";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To2_From3To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To1_From2To2_From3To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To3_From4To4_From5To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_CycleFrom1To3_From4To4_From5To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To12()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To12";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To3_From4To6()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To3_From4To6";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To3_From4To4()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To3_From4To4";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To2_From3To3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To2_From3To3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To2_From3To5_From6To9()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To2_From3To5_From6To9";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To2_From3To4()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To2_From3To4";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To2_From3To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To1_From2To2_From3To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To3_From4To4_From5To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Paynow_CycleFrom1To3_From4To4_From5To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To12()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To12";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To3_From4To6()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To3_From4To6";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To3_From4To4()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To3_From4To4";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To2_From3To3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To2_From3To3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To2_From3To5_From6To9()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To2_From3To5_From6To9";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To2_From3To4()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To2_From3To4";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To2_From3To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To1_From2To2_From3To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To3_From4To4_From5To_()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_CisClientSide_RecurringPricesAndOneTimePrices_Conversion_Onfirstbill_Paynow_CycleFrom1To3_From4To4_From5To_";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	

}
