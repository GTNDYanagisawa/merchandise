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

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class ConnectedDocumentImplTest extends TestCase
{

	private ConnectedDocumentImpl classUnderTest;

	@Override
	public void setUp()
	{
		classUnderTest = new ConnectedDocumentImpl();
	}

	public void testBusObjectType()
	{

		String busObjectType = "busType";
		classUnderTest.setBusObjectType(busObjectType);
		assertEquals(busObjectType, classUnderTest.getBusObjectType());
	}

	public void testRefGuid()
	{

		String refGuid = "ABC";
		classUnderTest.setRefGuid(refGuid);
		assertEquals(refGuid, classUnderTest.getRefGuid());
	}

	public void testAppTyp()
	{

		String appTyp = "X3";
		classUnderTest.setAppTyp(appTyp);
		assertEquals(appTyp, classUnderTest.getAppTyp());
	}

}
