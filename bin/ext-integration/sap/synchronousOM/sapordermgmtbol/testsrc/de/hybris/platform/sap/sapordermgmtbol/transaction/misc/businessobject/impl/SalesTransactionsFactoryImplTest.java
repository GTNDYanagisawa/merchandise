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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.constants.SapordermgmtbolConstants;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import javax.annotation.Resource;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SalesTransactionsFactoryImplTest extends SapordermanagmentBolSpringJunitTest
{
	@Resource(name = SapordermgmtbolConstants.ALIAS_BEAN_TRANSACTIONS_FACTORY)
	private SalesTransactionsFactoryImpl classUnderTest;

	@Test
	public void testShipTo()
	{
		final ShipTo shipTo = classUnderTest.createShipTo();

		assertNotNull("no object created", shipTo);
	}

	@Test
	public void testBillTo()
	{
		final BillTo billTo = classUnderTest.createBillTo();

		assertNotNull("no object created", billTo);
	}

	@Test
	public void testItem()
	{
		final Item item = classUnderTest.createSalesDocumentItem();

		assertNotNull("no object created", item);
	}

}
