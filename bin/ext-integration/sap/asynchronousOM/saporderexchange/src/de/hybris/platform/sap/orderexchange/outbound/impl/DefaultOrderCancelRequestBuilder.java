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

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;

import org.apache.log4j.Logger;


/**
 * Default raw item builder as AbstractRawItemBuilder and additional Logger
 */
public class DefaultOrderCancelRequestBuilder extends AbstractRawItemBuilder<OrderCancelRecordEntryModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultOrderCancelRequestBuilder.class);

	@Override
	protected Logger getLogger()
	{
		return LOG;
	}

}
