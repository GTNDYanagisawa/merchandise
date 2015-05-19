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

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class ERPLO_APICustomerExitsImplTest
{
	ERPLO_APICustomerExitsImpl classUnderTest = new ERPLO_APICustomerExitsImpl();

	@Test
	public void testItemChanged()
	{
		final Item currentItem = null;
		final Item existingItem = null;
		assertFalse(classUnderTest.customerExitIsItemChanged(currentItem, existingItem));
	}
}
