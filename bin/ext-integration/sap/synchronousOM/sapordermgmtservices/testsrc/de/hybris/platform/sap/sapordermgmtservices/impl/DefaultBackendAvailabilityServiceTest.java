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
package de.hybris.platform.sap.sapordermgmtservices.impl;

import static org.junit.Assert.assertFalse;

import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings(
{ "boxing", "javadoc" })
public class DefaultBackendAvailabilityServiceTest
{
	DefaultBackendAvailabilityService classUnderTest = new DefaultBackendAvailabilityService();
	BolCartFacade bolCartFacade;


	@Before
	public void init()
	{
		bolCartFacade = EasyMock.createMock(BolCartFacade.class);

		EasyMock.expect(bolCartFacade.isBackendDown()).andReturn(false);
		EasyMock.replay(bolCartFacade);
		classUnderTest.setBolCartFacade(bolCartFacade);
	}

	@Test
	public void testBackendDown()
	{
		assertFalse(classUnderTest.isBackendDown());
	}
}
