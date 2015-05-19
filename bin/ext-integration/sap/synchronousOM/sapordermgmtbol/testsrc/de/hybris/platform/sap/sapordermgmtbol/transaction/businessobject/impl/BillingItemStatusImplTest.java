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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class BillingItemStatusImplTest extends SapordermanagmentBolSpringJunitTest
{

	private BillingItemStatusImpl classUnderTest;


	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		classUnderTest = (BillingItemStatusImpl) genericFactory.getBean(SapordermgmtbolConstants.ALIAS_BEAN_BILLING_ITEM_STATUS);
	}

	@Test
	public void testConstructor()
	{
		assertNotNull(classUnderTest);
	}

	@Test
	public void testIsPartiallyProcessed_false()
	{
		final EStatus dlvStatus = EStatus.PROCESSED;
		final EStatus ordInvoiceStatus = EStatus.PROCESSED;
		final EStatus dlvInvoiceStatus = EStatus.PROCESSED;
		final EStatus rjStatus = EStatus.PROCESSED;
		classUnderTest = new BillingItemStatusImpl(dlvStatus, ordInvoiceStatus, dlvInvoiceStatus, rjStatus);

		assertFalse(classUnderTest.isPartiallyProcessed());
	}

	@Test
	public void testIsPartiallyProcessed_true()
	{
		final EStatus dlvStatus = EStatus.PROCESSED;
		final EStatus ordInvoiceStatus = EStatus.PARTIALLY_PROCESSED;
		final EStatus dlvInvoiceStatus = EStatus.PROCESSED;
		final EStatus rjStatus = EStatus.PROCESSED;
		classUnderTest = new BillingItemStatusImpl(dlvStatus, ordInvoiceStatus, dlvInvoiceStatus, rjStatus);

		assertTrue(classUnderTest.isPartiallyProcessed());
	}

}
