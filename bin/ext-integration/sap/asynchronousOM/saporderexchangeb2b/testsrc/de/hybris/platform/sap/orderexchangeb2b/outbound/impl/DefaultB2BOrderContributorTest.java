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
package de.hybris.platform.sap.orderexchangeb2b.outbound.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultB2BOrderContributorTest
{
	private DefaultB2BOrderContributor cut;

	@Before
	public void setUp()
	{
		cut = new DefaultB2BOrderContributor();
	}

	@Test
	public void testGetColumns() throws Exception
	{
		final Set<String> columns = cut.getColumns();
		Assert.assertTrue(columns.contains(OrderCsvColumns.CHANNEL));
		Assert.assertTrue(columns.contains(OrderCsvColumns.PURCHASE_ORDER_NUMBER));
	}

	@Test
	public void testEnhanceColumns() throws Exception
	{
		final OrderModel model = new OrderModel();
		model.setPurchaseOrderNumber("PO123");
		final BaseSiteModel site = new BaseSiteModel();
		model.setSite(site);
		site.setChannel(SiteChannel.B2B);

		final Map<String, Object> row = new HashMap<>();
		final List<Map<String, Object>> rows = Arrays.asList(row);
		final List<Map<String, Object>> enhancedRows = cut.enhanceRowsByB2BFields(model, rows);
		Assert.assertEquals("PO123", enhancedRows.get(0).get(OrderCsvColumns.PURCHASE_ORDER_NUMBER));
		Assert.assertEquals(SiteChannel.B2B, enhancedRows.get(0).get(OrderCsvColumns.CHANNEL));
	}
}
