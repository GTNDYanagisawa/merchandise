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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocumentItem;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class ConnectedDocumentItemImplTest extends TestCase
{

	private final ConnectedDocumentItemImpl classUnderTest = new ConnectedDocumentItemImpl();

	public void testClone()
	{
		final String appType = "appType";
		classUnderTest.setAppTyp(appType);
		final ConnectedDocumentItem clone = (ConnectedDocumentItem) classUnderTest.clone();

		assertNotSame(clone, classUnderTest);
		assertEquals(appType, classUnderTest.getAppTyp());
		assertFalse(clone == classUnderTest);
	}

	public void testAppTyp()
	{
		final String appTyp = "appTyp";
		classUnderTest.setAppTyp(appTyp);

		assertEquals(appTyp, classUnderTest.getAppTyp());
	}

	public void testBusObjectTyp()
	{
		final String busObjectType = "objectType";
		classUnderTest.setBusObjectType(busObjectType);

		assertEquals(busObjectType, classUnderTest.getBusObjectType());
	}

	public void testDate()
	{
		final Date date = new Date();
		classUnderTest.setDate(date);

		assertEquals(date, classUnderTest.getDate());
	}

	public void testDocOrigin()
	{
		final String origin = "origin";
		classUnderTest.setDocumentOrigin(origin);

		assertEquals(origin, classUnderTest.getDocumentOrigin());
	}

	public void testDocumentKey()
	{
		final TechKey documentKey = new TechKey("ABC");
		classUnderTest.setDocumentKey(documentKey);

		assertEquals(documentKey, classUnderTest.getDocumentKey());
	}

	public void testQuantity()
	{
		final BigDecimal quantity = BigDecimal.TEN;
		classUnderTest.setQuantity(quantity);

		assertEquals(quantity, classUnderTest.getQuantity());
	}

	public void testTrackingURL()
	{
		final String url = "www.dhl.de";
		classUnderTest.setTrackingURL(url);

		assertEquals(url, classUnderTest.getTrackingURL());
	}

	public void testUnit()
	{
		final String unit = "PC";
		classUnderTest.setUnit(unit);

		assertEquals(unit, classUnderTest.getUnit());
	}
}
