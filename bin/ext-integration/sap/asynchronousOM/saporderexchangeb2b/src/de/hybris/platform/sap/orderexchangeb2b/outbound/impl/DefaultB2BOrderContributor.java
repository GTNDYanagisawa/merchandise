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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultOrderContributor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Order header contributor for B2B orders to be replicated to SAP ERP system. Enhances B2C contributor by channel and
 * external PO number
 * 
 */
public class DefaultB2BOrderContributor extends DefaultOrderContributor
{
	@SuppressWarnings("javadoc")
	public DefaultB2BOrderContributor()
	{
		super();
		getColumns().addAll(Arrays.asList(OrderCsvColumns.CHANNEL, OrderCsvColumns.PURCHASE_ORDER_NUMBER));
	}

	@Override
	public List<Map<String, Object>> createRows(final OrderModel model)
	{
		final List<Map<String, Object>> rows = super.createRows(model);
		return enhanceRowsByB2BFields(model, rows);
	}

	protected List<Map<String, Object>> enhanceRowsByB2BFields(final OrderModel model, final List<Map<String, Object>> rows)
	{
		// There is only one row on order level
		final Map<String, Object> row = rows.get(0);
		row.put(OrderCsvColumns.CHANNEL, model.getSite().getChannel());
		row.put(OrderCsvColumns.PURCHASE_ORDER_NUMBER, model.getPurchaseOrderNumber());

		return rows;
	}

}
