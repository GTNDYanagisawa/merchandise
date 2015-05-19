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
package de.hybris.platform.storelocator;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.storelocator.constants.GeolocationConstants;
import de.hybris.platform.storelocator.constants.GeolocationMaths;
import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import java.text.DecimalFormat;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit Tests for the Geolocation extension
 */
public class CoordinatesTest extends HybrisJUnit4TransactionalTest
{
	/** Edit the local|project.properties to change logging behavior (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CoordinatesTest.class.getName());

	@Before
	public void setUp()
	{
		// implement here code executed before each test
	}

	@After
	public void tearDown()
	{
		// implement here code executed after each test
	}



	@Test
	public void testGeoLocatorException()
	{
		try
		{
			throw new GeoLocatorException("Sth wen wrong!");
		}
		catch (final GeoLocatorException e)
		{
			Assert.assertNotNull(e);
			Assert.assertNotNull(e.toString());
		}
		catch (final Exception e)
		{
			Assert.fail();
		}
	}


	/**
	 * The following example tests the proper instantiation of GPS when the decimal coordinates are given from within the
	 * limits
	 */
	@Test
	public void testInstantiationGPSParametersWithinLimits()
	{
		GPS location = new DefaultGPS();
		try
		{
			//create 3 GPS locations for existing, real-life values of latitude and longitude
			location = location.create(78, 123);
			location = location.create(90, 180);
			location = location.create(0, 0);
		}
		catch (final Exception e)
		{
			//No exception should be thrown
			Assert.fail();
		}

	}

	/**
	 *The following example tests instantiation of GPS when improper values of decimal coordinates are given as
	 * parameters.
	 * 
	 */

	public void testInstantiationGPSParametersBeyondLimits()
	{
		boolean passed = false;
		GPS location = new DefaultGPS();
		try
		{
			location = location.create(-90.01, 12);
		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);



		passed = false;
		try
		{
			location = location.create(-30, -181);
		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);

	}

	/**
	 * The following example tests the proper instantiation of GPS location when valid DMS coordinates are given
	 */
	@Test
	public void testInstantiationGPSParametersValidDMS()
	{
		GPS location = new DefaultGPS();
		try
		{
			//boundary conditions - only degrees given
			location = location.create("90\u00B0S", "180\u00B0W");
			location = location.create("90\u00B0N", "180\u00B0E");
			//degrees and minutes given
			//...with leading zeroes
			location = location.create("06\u00B009'N", "006\u00B001'W");
			//...without leading zeroes
			location = location.create("6\u00B09'S", "6\u00B01'E");
			//full DMS
			//...with leading zeroes
			location = location.create("36\u00B009'21\"N", "006\u00B041'54\"W");
			//...without leading zeroes
			location = location.create("36\u00B09'21\"S", "6\u00B041'54\"E");
		}
		catch (final Exception e)
		{
			Assert.fail();
		}
	}

	/**
	 * the following example tests the proper instantiation of GPS location when improper DMS format is given as a String
	 * argument
	 */
	@Test
	public void testInstantiationGPSParametersInvalidDMSSyntax()
	{
		boolean passed = false;
		GPS location = new DefaultGPS();
		try
		{
			//here: latitude given with degrees and seconds. Missing minutes among them.
			location = location.create("56\u00B045\"S", "156\u00B03'4\"W");
			Assert.fail(GeoLocatorException.class.toString() + " was expected. ");
		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);

		passed = false;
		try
		{
			//here: forbidden character 'g'
			location = location.create("56\u00B0g4'45\"S", "156\u00B03'4\"W");
			Assert.fail(GeoLocatorException.class.toString() + " was expected. ");

		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);

		passed = false;
		try
		{
			//here: invalid geographic direction S in longitude argument
			location = location.create("56\u00B054'15\"N", "156\u00B032'4\"S");
			Assert.fail(GeoLocatorException.class.toString() + " was expected. ");

		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);

		passed = false;
		try
		{
			//here: invalid minutes mark '' instead of ' in longitude argument
			location = location.create("56\u00B054'15\"N", "156\u00B032''4\"E");
			Assert.fail(GeoLocatorException.class.toString() + " was expected. ");

		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);

	}

	/**
	 * the following example tests the proper instantiation of GPS location when improper DMS values are given
	 */
	@Test
	public void testInstantiationGPSParametersInvalidDMSValues()
	{
		boolean passed = false;
		GPS location = new DefaultGPS();
		try
		{
			//here: invalid (60 minutes)
			location = location.create("16\u00B060'12\"S", "156\u00B03'4\"W");
			Assert.fail(GeoLocatorException.class.toString() + " was expected. ");
		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);

		passed = false;
		try
		{
			//here: latitude DMS vale is outside range -> 90 degrees AND 1 second
			location = location.create("90\u00B00'1\"S", "156\u00B03'4\"W");
			Assert.fail(GeoLocatorException.class.toString() + " was expected. ");
		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);
		passed = false;
		try
		{
			//here: 186 degrees in longitude is inalid
			location = location.create("56\u00B054'15\"N", "186\u00B032'4\"E");
			Assert.fail(GeoLocatorException.class.toString() + " was expected. ");

		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);
		passed = false;
		try
		{
			//here: 72 seconds in longitude argument
			location = location.create("56\u00B054'15\"N", "156\u00B032'72\"E");
			Assert.fail(GeoLocatorException.class.toString() + " was expected. ");

		}
		catch (final GeoLocatorException e)
		{
			passed = true;
		}
		assertTrue(GeoLocatorException.class.toString() + " was expected.", passed);

	}

	/**
	 * The following example tests proper validation of DMS format patterns
	 */
	@Test
	public void testDMSPatterns()
	{
		Assert.assertTrue("180\u00B0E".matches(GeolocationConstants.DMS_LONGITUDE_PATTERN));
		Assert.assertTrue(" 179\u00B0   59'    59\" W".matches(GeolocationConstants.DMS_LONGITUDE_PATTERN));
		Assert.assertTrue(" 0\u00B0  1' 09\" W".matches(GeolocationConstants.DMS_LONGITUDE_PATTERN));
		Assert.assertTrue(" 10\u00B007' W".matches(GeolocationConstants.DMS_LONGITUDE_PATTERN));

		Assert.assertTrue(!" 10\u00B007\" W".matches(GeolocationConstants.DMS_LONGITUDE_PATTERN));
		Assert.assertTrue(!" 10\u00B012'07\" R".matches(GeolocationConstants.DMS_LONGITUDE_PATTERN));

		Assert.assertTrue("90\u00B0N".matches(GeolocationConstants.DMS_LATITUDE_PATTERN));
		Assert.assertTrue(" 89\u00B0   59'    59\" S".matches(GeolocationConstants.DMS_LATITUDE_PATTERN));
		Assert.assertTrue(" 01\u00B0  15' 09\" N".matches(GeolocationConstants.DMS_LATITUDE_PATTERN));
		Assert.assertTrue(" 10\u00B007' S".matches(GeolocationConstants.DMS_LATITUDE_PATTERN));

		Assert.assertTrue(!" 10\u00B007\" N".matches(GeolocationConstants.DMS_LATITUDE_PATTERN));
		Assert.assertTrue(!" 10\u00B012'07\" R".matches(GeolocationConstants.DMS_LATITUDE_PATTERN));

	}

	/**
	 * The following example shows the mutual ralations between two GPS locations created with different 'create' method
	 * calls
	 * 
	 */
	@Test
	public void testOneGPSInstantiatedFromAnothersToDMSString()
	{
		//Two location A, B 
		GPS locationA = new DefaultGPS();
		GPS locationB = null;
		try
		{
			//make A a localization of a given point
			locationA = locationA.create(34.098765, -01.876655);
		}
		catch (final Exception e)
		{
			Assert.fail();
		}

		//get the DMS coordinates from locationA in order to create locationB with create(String, String)
		final String dmsA[] = locationA.toDMSString().split(",");
		for (int i = 0; i < dmsA.length; i++)
		{
			dmsA[i] = dmsA[i].replaceAll("[\\(|\\)]", "");
		}

		try
		{
			//locationB is created with DMS values taken from locationA.toString();
			locationB = locationA.create(dmsA[0], dmsA[1]);
		}
		catch (final Exception e)
		{
			Assert.fail();
		}

		//the locations A,B should represent the same coordinates
		//..so they should have the same DMS output 
		Assert.assertEquals(locationA.toDMSString(), locationB.toDMSString());

		//..and the error of their decimal coordinates shouldn't be greater than 
		// error of introduced by second resolution - that is 1/3600
		Assert.assertEquals(locationA.getDecimalLatitude(), locationB.getDecimalLatitude(), (double) 1 / 3600);
		Assert.assertEquals(locationA.getDecimalLongitude(), locationB.getDecimalLongitude(), (double) 1 / 3600);

	}

	/**
	 * The following example tests if the toString method works as expected
	 * 
	 */
	@Test
	public void testGPSToString()
	{
		GPS locationA = new DefaultGPS();
		try
		{
			locationA = locationA.create(34.098765, -01.876655);
		}
		catch (final Exception e)
		{
			Assert.fail();
		}
		Assert.assertEquals("("
				+ new DecimalFormat(GeolocationConstants.DECIMAL_COORDINATES_FORMAT).format(locationA.getDecimalLatitude()) + ", "
				+ new DecimalFormat(GeolocationConstants.DECIMAL_COORDINATES_FORMAT).format(locationA.getDecimalLongitude()) + ")",
				locationA.toString());
	}

	/**
	 * The following example tests if the toDMSString method works as expected
	 * 
	 */
	@Test
	public void testGPSToDMSString()
	{
		GPS locationA = new DefaultGPS();
		int[] lat = null;
		int[] lon = null;
		try
		{
			//create locationA
			locationA = locationA.create(84.998765, -1.999989);
			//get it's DMS format values: degrees, minutes, seconds
			lat = GeolocationMaths.decimal2DMS(locationA.getDecimalLatitude());
			lon = GeolocationMaths.decimal2DMS(locationA.getDecimalLongitude());
		}
		catch (final Exception e)
		{
			Assert.fail();
		}
		//.. and check if the proper values are in the right place
		Assert.assertEquals("(" + Math.abs(lat[0]) + "\u00b0" + lat[1] + "'" + lat[2] + "\"N, " + Math.abs(lon[0]) + "\u00b0"
				+ lon[1] + "'" + lon[2] + "\"W)", locationA.toDMSString());
	}


}
