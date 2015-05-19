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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Before;
import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class ShipToImplTest extends SapordermanagmentBolSpringJunitTest
{

	protected ShipToImpl classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		classUnderTest = (ShipToImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SHIP_TO);
	}

	@Test
	public void testConstructor()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void testClone()
	{

		classUnderTest.setId("123");
		final ShipToImpl clone = classUnderTest.clone();
		assertNotSame(clone, classUnderTest);
		assertEquals(classUnderTest.getId(), clone.getId());


	}

}
