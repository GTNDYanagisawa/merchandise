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
package de.hybris.platform.generated.subscriptionservices;

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
public class Cart_Calculation_OneTime_And_Recurring_Charge extends ServicelayerTest implements RobotTestContext
{
	public static RobotTestSuite robotTestSuite;
	
	private ModelService modelService;

	public static void startSuite() throws IOException
	{
		if (robotTestSuite == null)
		{
			final PythonAware pythonAware = new DefaultPythonProvider(Config.getParameter("atddengine.libraries-path"));

			final RobotSettings robotSettings = new RobotSettings();
			
			robotSettings.setOutputDir(new File(Config.getParameter("atddengine.report-path"), "subscriptionservices"));
			robotSettings.setLogName("Cart_Calculation_OneTime_And_Recurring_Charge-log");
			robotSettings.setOutputName("Cart_Calculation_OneTime_And_Recurring_Charge-output");
			robotSettings.setReportName("Cart_Calculation_OneTime_And_Recurring_Charge-report");
			
			final RobotTestSuiteFactory robotTestSuiteFactory = new PythonRobotTestSuiteFactory(pythonAware);

			robotTestSuite = robotTestSuiteFactory.parseTestSuite(robotSettings, new File(
					"/home/bamboouser/agent/agent-home/xml-data/build-dir/HYM-SUB4-JOB1/source/subscriptionatddtests/genresources/robottests/Cart_Calculation_OneTime_And_Recurring_Charge.txt"));
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
		return "subscriptionservices";
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
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To3_From4To4_From5To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To3_From4To4_From5To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProductx2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProductx2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProductx3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_OneProductx3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_TwoProducts()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_CycleFrom1To1_From2To_TwoProducts";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To3_From4To4_From5To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To3_From4To4_From5To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProductx2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProductx2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProductx3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_OneProductx3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_TwoProducts()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Oncancellation_CycleFrom1To1_From2To_TwoProducts";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To3_From4To4_From5To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To3_From4To4_From5To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProductx2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProductx2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProductx3()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_OneProductx3";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_TwoProducts()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Paynow_CycleFrom1To1_From2To_TwoProducts";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_Oncancellation_Paynow_CycleFrom1To1_From2To_OneProduct_OnePlan()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_Oncancellation_Paynow_CycleFrom1To1_From2To_OneProduct_OnePlan";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Multi_Onfirstbill_Oncancellation_Paynow_CycleFrom1To1_From2To_MultipleProducts_MultiplePlans()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Multi_Onfirstbill_Oncancellation_Paynow_CycleFrom1To1_From2To_MultipleProducts_MultiplePlans";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_BillingEvent1Test_CycleFrom1To1_From2To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_BillingEvent1Test_CycleFrom1To1_From2To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_BillingEvent1Test_CycleFrom1To1_From2To_OneProduct()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_BillingEvent1Test_CycleFrom1To1_From2To_OneProduct";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_BillingEvent1Test_CycleFrom1To1_From2To_TwoProducts()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Onfirstbill_BillingEvent1Test_CycleFrom1To1_From2To_TwoProducts";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_Removing_Complex()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_Removing_Complex";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Cart_Calculation_OneTime_And_Recurring_Charge_RemovingAddingRemoving_Complex()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Cart_Calculation_OneTime_And_Recurring_Charge_RemovingAddingRemoving_Complex";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	

}
