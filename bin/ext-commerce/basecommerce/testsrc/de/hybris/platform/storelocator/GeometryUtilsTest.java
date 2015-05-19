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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.storelocator.impl.DefaultGPS;
import de.hybris.platform.storelocator.impl.GeometryUtils;

import java.util.List;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticMeasurement;
import org.gavaghan.geodesy.GlobalPosition;
import org.junit.Test;


public class GeometryUtilsTest
{
	private static final double METRES_IN_KM = 1000.0;
	private static final double RADIUS = 10.0; //km
	private static final double DISTANCE_TO_CORNER = RADIUS * Math.sqrt(2.0);
	private static final double EXPECTED_DIFFERENCE = 0.389; //km
	static int NUMBER_OF_LATITUDES = 359;
	static int NUMBER_OF_LONGITUDES = 179;
	private static double[][] COORDINATES = new double[NUMBER_OF_LATITUDES * NUMBER_OF_LONGITUDES][];
	final GeodeticCalculator geodeticCalculator = new GeodeticCalculator();

	static
	{
		initializeCoordinatesForGlobe();
	}

	private static void initializeCoordinatesForGlobe()
	{
		int count = 0;
		for (int i = 0; i < NUMBER_OF_LATITUDES; i++)
		{
			for (int j = 0; j < NUMBER_OF_LONGITUDES; j++)
			{
				COORDINATES[count] = new double[]
				{ j - 89, i - 179 };
				count++;
			}
		}
	}

	@Test
	public void testGetSquareOfTolerance() throws Exception
	{
		for (int i = 0; i < COORDINATES.length; i++)
		{
			final double latitude = COORDINATES[i][0];
			final double longitude = COORDINATES[i][1];
			final GlobalPosition start = new GlobalPosition(latitude, longitude, 0.0);

			final GPS center = new DefaultGPS(latitude, longitude);
			final List<GPS> gps = GeometryUtils.getSquareOfTolerance(center, RADIUS);
			final GPS upperLeftCorner = gps.get(0);
			final GPS bottomRightCorner = gps.get(1);

			final Double distanceInKilometresForUpperLeftCorner = getDistanceInKilometresForCorner(upperLeftCorner, start);
			final Double distanceInKilometresForBottomRightCorner = getDistanceInKilometresForCorner(bottomRightCorner, start);

			assertThat(distanceInKilometresForUpperLeftCorner, closeTo(DISTANCE_TO_CORNER, EXPECTED_DIFFERENCE));
			assertThat(distanceInKilometresForBottomRightCorner, closeTo(DISTANCE_TO_CORNER, EXPECTED_DIFFERENCE));
		}
	}

	@Test
	public void testIsPointInEuropeAndReturnTrue() throws Exception
	{
		final GPS[] pointsInEurope = new GPS[]
		{ new DefaultGPS(48.12, 11.58), new DefaultGPS(59.33, 18.06), new DefaultGPS(59.33, 18.06), new DefaultGPS(59.33, 18.06),
				new DefaultGPS(38.70, -9.13), new DefaultGPS(44.88, 34.14) };

		for (final GPS point : pointsInEurope)
		{
			assertTrue(GeometryUtils.isPointInEurope(point));
		}
	}

	@Test
	public void testIsPointInEuropeAndReturnFalse() throws Exception
	{
		final GPS[] pointsOutsideEurope = new GPS[]
		{ new DefaultGPS(14.40, -90.22), new DefaultGPS(30.01, 31.14), new DefaultGPS(-18.06, 178.30),
				new DefaultGPS(28.37, 77.13), new DefaultGPS(31.47, 35.12), new DefaultGPS(24.45, 25.57) };

		for (final GPS point : pointsOutsideEurope)
		{
			assertFalse(GeometryUtils.isPointInEurope(point));
		}
	}

	private Double getDistanceInKilometresForCorner(final GPS point, final GlobalPosition start)
	{
		final GlobalPosition endFirstCorner = new GlobalPosition(point.getDecimalLatitude(), point.getDecimalLongitude(), 0.0);
		final GeodeticMeasurement measurementForFirstCorner = geodeticCalculator.calculateGeodeticMeasurement(Ellipsoid.KRASSOWSKI,
				start, endFirstCorner);
		return Double.valueOf(measurementForFirstCorner.getPointToPointDistance() / METRES_IN_KM);
	}
}
