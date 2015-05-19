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
package de.hybris.platform.generated.entitlements;

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
public class Entitlement_CreateAndVerifyXML_MixedConditions_Test extends ServicelayerTest implements RobotTestContext
{
	public static RobotTestSuite robotTestSuite;
	
	private ModelService modelService;

	public static void startSuite() throws IOException
	{
		if (robotTestSuite == null)
		{
			final PythonAware pythonAware = new DefaultPythonProvider(Config.getParameter("atddengine.libraries-path"));

			final RobotSettings robotSettings = new RobotSettings();
			
			robotSettings.setOutputDir(new File(Config.getParameter("atddengine.report-path"), "entitlements"));
			robotSettings.setLogName("Entitlement_CreateAndVerifyXML_MixedConditions_Test-log");
			robotSettings.setOutputName("Entitlement_CreateAndVerifyXML_MixedConditions_Test-output");
			robotSettings.setReportName("Entitlement_CreateAndVerifyXML_MixedConditions_Test-report");
			
			final RobotTestSuiteFactory robotTestSuiteFactory = new PythonRobotTestSuiteFactory(pythonAware);

			robotTestSuite = robotTestSuiteFactory.parseTestSuite(robotSettings, new File(
					"/home/bamboouser/agent/agent-home/xml-data/build-dir/HYM-ENT0-JOB1/source/entitlementatddtests/genresources/robottests/Entitlement_CreateAndVerifyXML_MixedConditions_Test.txt"));
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
		return "entitlements";
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
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Simple_Path()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Simple_Path";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Simple_Metered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Simple_Metered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_StringAndPath()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_StringAndPath";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_String_Path()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_String_Path";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_StringAndMetered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_StringAndMetered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_String_Metered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_String_Metered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Path_Metered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Path_Metered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_PathAndMetered_x1()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_PathAndMetered_x1";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_TimeframeAndMetered_x1()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_TimeframeAndMetered_x1";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_TimeframeAndMetered_x2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_TimeframeAndMetered_x2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_GeoAndMetered_x1()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_GeoAndMetered_x1";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_GeoAndMetered_x2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_GeoAndMetered_x2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String_Path_Metered_No1()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String_Path_Metered_No1";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String_Path_Metered_No2()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String_Path_Metered_No2";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_StringAndPathAndMetered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_StringAndPathAndMetered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Simple_StringAndPathAndMetered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Simple_StringAndPathAndMetered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_String_StringAndPathAndMetered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_String_StringAndPathAndMetered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Path_StringAndPathAndMetered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Path_StringAndPathAndMetered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Metered_StringAndPathAndMetered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Metered_StringAndPathAndMetered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String_Path_Metered()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_Simple_String_Path_Metered";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_MeteredAndString_Metered_String_Simple()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_MeteredAndString_Metered_String_Simple";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_MeteredAndPath_Metered_Path_Simple()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_MeteredAndPath_Metered_Path_Simple";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_MeteredAndStringAndPath_Metered_StringAndPath_Simple()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_MeteredAndStringAndPath_Metered_StringAndPath_Simple";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Test_Entitlement_CreateAndVerifyXML_MixedConditions_MeteredAndStringAndPath_Metered_StringAndPath_Simple()
	{
		modelService.detachAll();
		
		final String robotTestName = "Test_Entitlement_CreateAndVerifyXML_MixedConditions_MeteredAndStringAndPath_Metered_StringAndPath_Simple";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Complex_No1()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Complex_No1";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_NonMetered_Full()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_NonMetered_Full";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CreateAndVerifyXML_MixedConditions_Metred_Full()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CreateAndVerifyXML_MixedConditions_Metred_Full";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CompareXML_MixedConditions_DifferentTypes()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CompareXML_MixedConditions_DifferentTypes";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	
	@Test
	public void Entitlement_CompareXML_MixedConditions_DifferentProducts()
	{
		modelService.detachAll();
		
		final String robotTestName = "Entitlement_CompareXML_MixedConditions_DifferentProducts";
		
		final RobotTest robotTest = robotTestSuite.getRobotTest(robotTestName);
		
		currentRobotTest = robotTestSuite.getRobotTest(robotTestName);
		assertNotNull("Robot test: '" + robotTestName + "' not found", currentRobotTest);

		final RobotTestResult robotTestResult = robotTest.run();
		assertTrue(robotTestResult.getMessage(), robotTestResult.isSuccess());
	}
	

}
