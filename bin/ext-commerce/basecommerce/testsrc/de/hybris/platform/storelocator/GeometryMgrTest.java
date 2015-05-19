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

import de.hybris.platform.storelocator.exception.GeoLocatorException;
import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.impl.GeometryUtils;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner;
import net.sourceforge.groboutils.junit.v1.TestRunnable;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * 
 */
public class GeometryMgrTest extends TestCase
{
	private static final Logger LOG = Logger.getLogger(GeometryMgrTest.class.getName()); //NOPMD

	//native threads may be OS limited
	private static final int THREAD_LIMIT = 250;

	/**
	 * the following example tests the geodesy calculations. Here the equator's length is examined basing on the global
	 * international GPS ellipsoid model WGS84
	 */
	@Test
	public void testElipticalDistanceCalculator()
	{
		try
		{
			GPS from = new DefaultGPS();
			//we are looking for the equator's length
			from = from.create("0\u00b00'0\"N", "0\u00b00'0\"E");
			final GPS gspTo = from.create("0\u00b00'0\"N", "180\u00b0E");
			//the error is probably big.. 100km
			//we should get half of equator's length
			Assert.assertEquals(20000, GeometryUtils.getElipticalDistanceKM(from, gspTo), 100);

		}
		catch (final GeoLocatorException e)
		{
			Assert.fail();
		}

	}

	/**
	 * The following test examines the behavior of the GeometryMgr.getElipticalDistanceKM() loaded with 250 multithreaded
	 * queries
	 * 
	 * @throws Throwable
	 * @throws GeoLocatorException
	 */
	@Test
	public void testLoadedElipticalDistanceCalculator() throws Throwable, GeoLocatorException
	{
		//init points - two points on the same latitude,  1.9 degrees offset between them 
		final double latA = 34.123456;
		double lonA = 0;
		final double latB = latA;
		double lonB = lonA + 1.9;

		final TestThread[] trs = new TestThread[THREAD_LIMIT];
		for (int i = 0; i < THREAD_LIMIT; i++)
		{
			//instantiation of single test threads with different locations
			trs[i] = new TestThread(latA, lonA, latB, lonB);
			//shifting the points at the same latitude should give 250 test cases with the same resulting distance
			lonA += 1.3;
			lonB += 1.3;
			//assures valid crossing of 180 degree longitude. 
			if (lonA > 180)
			{
				lonA = -360 + lonA;
			}
			if (lonB > 180)
			{
				lonB = -360 + lonB;
			}
		}
		//net.sourceforge.groboutils.junit.v1.MultiThreadedTestRunner assures correct multithreaded tests.
		final MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
		//starts the MTTR & fires off threads
		mttr.runTestRunnables();
	}

	/**
	 * Private class representing single thread of the test case. Extends import
	 * net.sourceforge.groboutils.junit.v1.TestRunnable which assures correct multithreaded testing.
	 */
	private class TestThread extends TestRunnable
	{

		GPS one;
		GPS two;

		private TestThread(final double latA, final double lonA, final double latB, final double lonB) throws GeoLocatorException
		{
			super();
			final GPS creator = new DefaultGPS();
			this.one = creator.create(latA, lonA);
			this.two = creator.create(latB, lonB);

		}

		@Override
		public void runTest() throws Throwable
		{
			final double distance = GeometryUtils.getElipticalDistanceKM(this.one, this.two);
			//every thread calculating at different starting point should be very close to the experimental value
			Assert.assertEquals(175.27422, distance, 0.00001);
		}
	}


}
