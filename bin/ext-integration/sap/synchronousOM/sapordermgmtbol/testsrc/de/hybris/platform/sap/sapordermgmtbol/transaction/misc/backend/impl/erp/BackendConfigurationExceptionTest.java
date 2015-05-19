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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class BackendConfigurationExceptionTest extends TestCase
{
	private BackendConfigurationException classUnderTest;
	final private static String MESSAGE = "myMessage";

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		classUnderTest = new BackendConfigurationException(MESSAGE);
	}


	public void testGetMessage()
	{
		assertEquals(MESSAGE, classUnderTest.getMessage());
	}

}
