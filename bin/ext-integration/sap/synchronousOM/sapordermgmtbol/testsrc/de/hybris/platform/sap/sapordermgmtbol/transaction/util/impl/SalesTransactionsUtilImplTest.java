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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SalesTransactionsUtilImplTest extends SapordermanagmentBolSpringJunitTest
{

	private SalesTransactionsUtilImpl classUnderTest;

	@Override
	@Before
	public void setUp()
	{
		classUnderTest = (SalesTransactionsUtilImpl) genericFactory
				.getBean(SapordermgmtbolConstants.ALIAS_BEAN_SALES_TRANSACTIONS_UTIL);
	}

	@Test
	public void testRemoveLeadingZeros()
	{

		Assert.assertEquals("1", classUnderTest.removeLeadingZeros("0001"));

	}



}
