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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.impl.SearchResultImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResult;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
public class DefaultOrderHistoryPopulatorTest
{

	/**
	 * 
	 */
	private static final String STATUS_PARTLY_SHIPPED = "B";
	/**
	 * 
	 */
	private static final String STATUS_SHIPPED = "C";
	/**
	 * 
	 */
	private static final String STATUS_COMPLETED = STATUS_SHIPPED;
	/**
	 * 
	 */
	private static final String PURCHASE_ORDERN_NUMBER = "132465";

	private static final Date DATE = new Date();
	DefaultOrderHistoryPopulator classUnderTest = new DefaultOrderHistoryPopulator();

	@Before
	public void setUp()
	{
		// Currently not needed
	}

	@Test
	public void poplatorTest()
	{
		final SearchResult source = new SearchResultImpl();

		source.setCreationDate(DATE);
		source.setPurchaseOrderNumber(PURCHASE_ORDERN_NUMBER);
		source.setOverallStatus(STATUS_COMPLETED);
		//		source.setShippingStatus(shippingStatus); Will Not be used
		source.setKey(new TechKey("as"));

		final OrderHistoryData target = new OrderHistoryData();

		classUnderTest.populate(source, target);

		Assert.assertEquals(PURCHASE_ORDERN_NUMBER, target.getPurchaseOrderNumber());
		Assert.assertEquals(OrderStatus.COMPLETED, target.getStatus());
		Assert.assertEquals(OrderStatus.COMPLETED.getCode().toLowerCase(), target.getCondensedStatus());
		Assert.assertEquals(DATE, target.getPlaced());
	}

	@Test
	public void testStatusPopulation()
	{
		final SearchResult source = new SearchResultImpl();
		final OrderHistoryData target = new OrderHistoryData();

		source.setKey(new TechKey("as"));
		source.setOverallStatus(" ");
		source.setShippingStatus(STATUS_SHIPPED);

		classUnderTest.populate(source, target);
		Assert.assertEquals(DeliveryStatus.SHIPPED.getCode().toLowerCase(), target.getCondensedStatus());

		source.setShippingStatus(STATUS_PARTLY_SHIPPED);

		classUnderTest.populate(source, target);
		Assert.assertEquals(DeliveryStatus.PARTSHIPPED.getCode().toLowerCase(), target.getCondensedStatus());

		source.setShippingStatus(" ");

		classUnderTest.populate(source, target);
		Assert.assertEquals(DeliveryStatus.NOTSHIPPED.getCode().toLowerCase(), target.getCondensedStatus());
	}

}
