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
package de.hybris.platform.storelocator.route.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.impl.DefaultGPS;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultGeolocationDirectionsUrlBuilderTest
{

	private final static String BASEURL = "http://maps.googleapis.com/maps";
	private DefaultGeolocationDirectionsUrlBuilder builder;
	private Map params;
	private GPS destination;
	private GPS start;

	@Before
	public void setUp()
	{
		builder = new DefaultGeolocationDirectionsUrlBuilder();
		//48.15069,11.5468
		start = new DefaultGPS().create(48.15069d, 11.5468d);
		//48.15093,11.5426
		destination = new DefaultGPS().create(48.15093d, 11.5426d);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullStart()
	{
		builder.getWebServiceUrl(BASEURL, null, destination, params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullDestination()
	{
		builder.getWebServiceUrl(BASEURL, start, null, params);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullBaseUrl()
	{
		builder.getWebServiceUrl(null, start, destination, params);
	}

	@Test
	public void testDefaultSetup()
	{
		final String webServiceUrl = builder.getWebServiceUrl(BASEURL, start, destination, params);
		Assert.assertEquals(
				"http://maps.googleapis.com/maps/api/directions/xml?origin=48.15069,11.5468&destination=48.15093,11.5426&sensor=true&mode=driving",
				webServiceUrl);
	}

	@Test
	public void testUrlWithWalkingMode()
	{
		builder.setMode("walking");
		final String webServiceUrl = builder.getWebServiceUrl(BASEURL, start, destination, params);
		Assert.assertEquals(
				"http://maps.googleapis.com/maps/api/directions/xml?origin=48.15069,11.5468&destination=48.15093,11.5426&sensor=true&mode=walking",
				webServiceUrl);
	}

	@Test
	public void testJson()
	{
		builder.setResponseType("json");
		final String webServiceUrl = builder.getWebServiceUrl(BASEURL, start, destination, params);
		Assert.assertEquals(
				"http://maps.googleapis.com/maps/api/directions/json?origin=48.15069,11.5468&destination=48.15093,11.5426&sensor=true&mode=driving",
				webServiceUrl);
	}

	@Test
	public void testSensorIsFalse()
	{
		builder.setSensor(false);
		final String webServiceUrl = builder.getWebServiceUrl(BASEURL, start, destination, params);
		Assert.assertEquals(
				"http://maps.googleapis.com/maps/api/directions/xml?origin=48.15069,11.5468&destination=48.15093,11.5426&sensor=false&mode=driving",
				webServiceUrl);
	}
}
