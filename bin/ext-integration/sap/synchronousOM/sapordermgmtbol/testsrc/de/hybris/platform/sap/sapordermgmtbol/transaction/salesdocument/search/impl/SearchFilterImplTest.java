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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class SearchFilterImplTest
{

	SearchFilterImpl classUnderTest = new SearchFilterImpl();

	@Test
	public void testNotShipped()
	{
		classUnderTest.setShippingStatus(SearchResult.SHIPPING_NOT_SHIPPED);
		assertEquals(SearchResult.SHIPPING_NOT_SHIPPED, classUnderTest.getShippingStatus());
	}

	@Test
	public void testNoStatus()
	{
		assertNull(classUnderTest.getShippingStatus());
	}

	@Test
	public void testProduct()
	{
		final String product = "A";
		classUnderTest.setProductID(product);
		assertEquals(product, classUnderTest.getProductID());
	}

}
