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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class GetAllStrategyERP605Test extends SapordermanagmentBolSpringJunitTest
{


	private final GetAllStrategyERP605 classUnderTest = new GetAllStrategyERP605();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest#setUp()
	 */
	@Override
	public void setUp()
	{
		classUnderTest.setGenericFactory(getGenericFactory());
		super.setUp();
	}

	@Test
	public void testOverallStatusBean()
	{
		assertNotNull(classUnderTest.getOverallStatusBean());
	}

	@Test
	public void testShippingStatusBean()
	{
		assertNotNull(classUnderTest.getShippingStatusBean());
	}

	@Test
	public void testBillingItemStatus()
	{
		assertNotNull(classUnderTest.getBillingItemStatus());
	}

	@Test
	public void testProcessingStatus()
	{
		assertNotNull(classUnderTest.getProcessingStatus());
	}
}
