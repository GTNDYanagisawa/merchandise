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
package de.hybris.platform.sap.core.test.property;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * ProperyAccess test.
 */
@UnitTest
public class PropertyAccessImplTest
{

	/**
	 * Test simple properties.
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	@Test
	public void testSimple() throws IOException
	{
		final PropertyAccessImpl classUnderTest = new PropertyAccessImpl();
		classUnderTest.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest() + "testsrc\\");
		classUnderTest.addPropertyFile("simple.properties");
		classUnderTest.loadProperties();
		final boolean booleanData = classUnderTest.getBooleanProperty("testboolean");
		Assert.assertEquals(true, booleanData);
		final String testString = classUnderTest.getStringProperty("teststring");
		Assert.assertEquals("teststring", testString);

		final List<String> stringList = classUnderTest.getStringList("colors");
		Assert.assertEquals(3, stringList.size());

		try
		{
			classUnderTest.getStringProperty("ThisIsAPropertyNameNotAvailableInPropertyFile"); //NOPMD
			Assert.fail("IllegalArgumentException should be thrown since property is not available");
		}
		catch (final IllegalArgumentException e)
		{
			//OK
		}

	}

	/**
	 * Test dynamic properties.
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	@Test
	public void testAddPropertyDynamically() throws IOException
	{
		final PropertyAccessImpl classUnderTest = new PropertyAccessImpl();
		classUnderTest.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest() + "testsrc\\");
		classUnderTest.addPropertyFile("simple.properties");
		classUnderTest.loadProperties();

		classUnderTest.setStringProperty("brandNewProperty", "im_new");
		final String brandNewProperty = classUnderTest.getStringProperty("brandNewProperty");
		Assert.assertEquals("im_new", brandNewProperty);

		Assert.assertTrue(classUnderTest.isPropertySet("teststring"));
		classUnderTest.setStringProperty("teststring", "overwrite_it");
		final String testString = classUnderTest.getStringProperty("teststring");
		Assert.assertEquals("overwrite_it", testString);

	}

	/**
	 * Test language dependency.
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	@Test
	public void testLanguageDependency() throws IOException
	{
		final PropertyAccessImpl classUnderTest = new PropertyAccessImpl();
		classUnderTest.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest() + "testsrc\\");
		classUnderTest.addPropertyFile("languageDependency.properties");
		classUnderTest.loadProperties();

		Assert.assertEquals("rouge", classUnderTest.getStringProperty("color"));

	}


	/**
	 * Test additional property files by suffix.
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	@Test
	public void testAdditionalPropertyFilesBySuffix() throws IOException
	{
		final PropertyAccessImpl classUnderTest = new PropertyAccessImpl();
		classUnderTest.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest() + "testsrc\\");
		classUnderTest.addPropertyFile("additionalPropertyFilesBySuffix.properties");
		classUnderTest.loadProperties();

		Assert.assertEquals("abc", classUnderTest.getStringProperty("abc_prop"));
		Assert.assertEquals("def", classUnderTest.getStringProperty("def_prop"));
		Assert.assertFalse(classUnderTest.isPropertySet("xyz_prop"));

	}


	/**
	 * Test multiple property files.
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	@Test
	public void testMultiplePropertyFilesAndToString() throws IOException
	{
		final PropertyAccessImpl classUnderTest = new PropertyAccessImpl();

		classUnderTest.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest() + "testsrc\\");

		classUnderTest.addPropertyFile("multiple1.properties");
		classUnderTest.addPropertyFile("multiple2.properties");
		classUnderTest.addPropertyFile("multiple3.properties");
		classUnderTest.loadProperties();

		Assert.assertEquals("valuefrom_m1", classUnderTest.getStringProperty("m1"));
		Assert.assertEquals("valuefrom_m2", classUnderTest.getStringProperty("m2"));
		Assert.assertEquals("valuefrom_m3", classUnderTest.getStringProperty("m3"));

		Assert.assertEquals("valuefrom_m1_overwritten_in_m3", classUnderTest.getStringProperty("m1_2"));


		final String propertiesAsString = classUnderTest.toString();
		// System.out.println(propertiesAsString);
		Assert.assertTrue(propertiesAsString.contains("m1_2 = valuefrom_m1_overwritten_in_m3"));
	}

	/**
	 * Test load from another extension.
	 * 
	 * @throws IOException
	 *            {@link IOException}
	 */
	@Test
	public void testLoadFromAnotherExtension() throws IOException
	{

		final PropertyAccessImpl classUnderTest = new PropertyAccessImpl();
		classUnderTest.setPropertyPathPrefix(PropertyAccessTestUtil.getCanonicalPathOfExtensionSapCoreTest());
		classUnderTest.addPropertyFile("..\\sapcore\\testsrc\\PropertyAccessImplTest.properties");
		classUnderTest.loadProperties();
		Assert.assertTrue(classUnderTest.isPropertySet("teststring"));
	}

	/**
	 * Test if property file is not available.
	 */
	@Test
	public void testIfPropertiesFileIsNotAvailable()
	{
		final PropertyAccessImpl classUnderTest = new PropertyAccessImpl();
		classUnderTest.addPropertyFile("does_not_exists.properties");

		try
		{
			classUnderTest.loadProperties();
			Assert.fail("IOException expected but not raised");
		}
		catch (final IOException e)
		{
			//OK
		}

	}



}
