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
package de.hybris.platform.sap.core.jco.test;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.util.Utilities;


/**
 * Test class for RFCDirectoryDestinationDataProvider.
 */
@UnitTest
@ContextConfiguration(locations =
{ "saptest-rfcdirectorydestinationdataprovider.xml" })
@SuppressWarnings("javadoc")
public class RFCDirectoryDestinationDataProviderTest extends SapcoreJCoJUnitTest
{
	@Test
	public void testParameterPathNotEmpty()
	{
		final RFCDirectoryDestinationDataProvider classUnderTest = genericFactory
				.getBean("sapCoreDefaultRFCDirectoryDestinationProvider");
		Assert.assertNotNull(classUnderTest.getDirectoryPaths());
	}


	@Test
	public void testReadDestinationFileByDirectoryDefinition() throws IOException
	{

		final RFCDirectoryDestinationDataProvider classUnderTest = genericFactory
				.getBean("sapCoreDefaultRFCDirectoryDestinationProvider");


		String testpath = Utilities.getExtensionInfo("sapcorejco").getExtensionDirectory().getCanonicalPath();
		testpath = testpath + "/testsrc/de/hybris/platform/sap/core/jco/test/";

		classUnderTest.getDirectoryPaths().add(testpath);

		final Properties destinationPropertiesTEST_CONNECTION_DUMMY = classUnderTest
				.getDestinationProperties("CONNECTION_DUMMY_FOR_DIRECTORY_FINDING");

		Assert.assertNotNull(destinationPropertiesTEST_CONNECTION_DUMMY);
		Assert.assertEquals("dummy_for_directory_based_finding", destinationPropertiesTEST_CONNECTION_DUMMY.getProperty("type"));

	}


	@Test
	public void testReadDestinationFileByExtensionDefintion()
	{

		final RFCDirectoryDestinationDataProvider classUnderTest = genericFactory
				.getBean("sapCoreDefaultRFCDirectoryDestinationProvider");

		final Properties destinationProperties = classUnderTest.getDestinationProperties("CONNECTION_DUMMY_FOR_EXTENSION_FINDING");

		Assert.assertNotNull(destinationProperties);
		Assert.assertEquals("dummy_for_extension_based_finding", destinationProperties.getProperty("type"));

	}


	@Test
	public void testReadingDestinationFileWhichDoesNotExist()
	{

		final RFCDirectoryDestinationDataProvider classUnderTest = genericFactory
				.getBean("sapCoreDefaultRFCDirectoryDestinationProvider");

		try
		{
			classUnderTest.getDestinationProperties("DOES_NOT_EXIST");
			Assert.fail("In case of destination property file could not be found, throw an exception");
		}
		catch (final RFCDirectoryDestinationDataProviderException e)
		{
			//OK
		}
	}

}
