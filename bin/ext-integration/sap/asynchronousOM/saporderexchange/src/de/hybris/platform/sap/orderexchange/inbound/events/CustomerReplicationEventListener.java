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
package de.hybris.platform.sap.orderexchange.inbound.events;

import com.sap.hybris.sapcustomerb2c.inbound.CustomerReplicationEvent;

import de.hybris.platform.sap.orderexchange.outbound.B2CCustomerHelper;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;


/**
 * Listener for the event that a B2C customer replication was confirmed by the ERP backend system. Triggers the
 * subsequent steps of the waiting order fulfillment processes
 * 
 */
public class CustomerReplicationEventListener extends AbstractEventListener<CustomerReplicationEvent>
{
	private B2CCustomerHelper b2CCustomerHelper;

	@SuppressWarnings("javadoc")
	public B2CCustomerHelper getB2CCustomerHelper()
	{
		return b2CCustomerHelper;
	}

	@SuppressWarnings("javadoc")
	public void setB2CCustomerHelper(final B2CCustomerHelper b2cCustomerHelper)
	{
		b2CCustomerHelper = b2cCustomerHelper;
	}

	@Override
	protected void onEvent(final CustomerReplicationEvent event)
	{
		final String customerID = event.getCustomerID();
		b2CCustomerHelper.processWaitingCustomerOrders(customerID);
	}

}
