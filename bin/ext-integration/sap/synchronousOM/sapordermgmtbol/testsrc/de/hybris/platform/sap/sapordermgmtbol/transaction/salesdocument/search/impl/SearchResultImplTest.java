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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;

import java.util.Date;

import org.junit.Test;



@UnitTest
@SuppressWarnings("javadoc")
public class SearchResultImplTest
{
	private final SearchResultImpl classUnderTest = new SearchResultImpl();

	@Test
	public void testDocumentID()
	{
		final TechKey orderID = new TechKey("A");
		classUnderTest.setKey(orderID);
		assertEquals(orderID, classUnderTest.getKey());
	}

	@Test
	public void testPurchaseOrderNo()
	{
		final String poNr = "Hallo";
		classUnderTest.setPurchaseOrderNumber(poNr);
		assertEquals(poNr, classUnderTest.getPurchaseOrderNumber());
	}

	@Test
	public void testOverallStatus()
	{
		final String overallStatus = SearchResult.OVERALL_STATUS_OPEN;
		classUnderTest.setOverallStatus(overallStatus);
		assertEquals(overallStatus, classUnderTest.getOverallStatus());
	}

	@Test
	public void testShippingStatus()
	{
		final String shippingStatus = SearchResult.SHIPPING_NOT_SHIPPED;
		classUnderTest.setShippingStatus(shippingStatus);
		assertEquals(shippingStatus, classUnderTest.getShippingStatus());
	}

	@Test
	public void testCreationDate()
	{
		final Date date = new Date(System.currentTimeMillis());
		classUnderTest.setCreationDate(date);
	}
}
