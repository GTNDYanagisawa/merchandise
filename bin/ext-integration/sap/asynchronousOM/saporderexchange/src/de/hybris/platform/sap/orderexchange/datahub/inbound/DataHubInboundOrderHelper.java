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
package de.hybris.platform.sap.orderexchange.datahub.inbound;

import de.hybris.platform.impex.jalo.ImpExException;


/**
 * Data Hub Inbound Helper for Order related notifications
 */
public interface DataHubInboundOrderHelper
{

	/**
	 * Trigger subsequent actions after order conformation has arrived
	 * @param orderNumber
	 */
	void processOrderConfirmationFromHub(final String orderNumber);
	
	/**
	 * Cancel order which was cancelled on ERP side also on hybris side
	 * @param orderInformation
	 * @param orderCode
	 * @throws ImpExException
	 */
	void cancelOrder(final String orderInformation, final String orderCode) throws ImpExException;
	
}
