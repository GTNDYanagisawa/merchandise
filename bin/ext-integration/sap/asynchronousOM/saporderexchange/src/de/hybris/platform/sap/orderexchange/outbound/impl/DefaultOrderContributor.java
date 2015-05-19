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
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Builds the Row map for the CSV files for the Order
 */
public class DefaultOrderContributor implements RawItemContributor<OrderModel>
{

	private final Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, OrderCsvColumns.DATE,
			OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, OrderCsvColumns.PAYMENT_MODE, OrderCsvColumns.DELIVERY_MODE,
			OrderCsvColumns.BASE_STORE));

	@Override
	public Set<String> getColumns()
	{
		return columns;
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		final Map<String, Object> row = new HashMap<>();

		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(OrderCsvColumns.DATE, order.getDate());
		row.put(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());
		final DeliveryModeModel deliveryMode = order.getDeliveryMode();
		row.put(OrderCsvColumns.DELIVERY_MODE, deliveryMode != null ? deliveryMode.getCode() : "");
		row.put(OrderCsvColumns.BASE_STORE, order.getStore().getUid());
		return Arrays.asList(row);
	}
}
