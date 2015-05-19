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

package com.sap.hybris.sapcustomerb2c.inbound;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.sql.Timestamp;
import java.util.Date;


/**
 * Service for the consumer notification replication via data hub
 */
public class CustomerImportService
{

	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private EventService eventService;

	/**
	 * @return flexible Search Instance
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return model service instance
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return business process service instance
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * Processes the consumer replication notification
	 * 
	 * @param customerId
	 */
	public void processConsumerReplicationNotificationFromHub(final String customerId)
	{
		// read customer
		final CustomerModel customer = readCustomer(customerId);

		// update customer
		customer.setSapConsumerID(customerId);
		customer.setSapReplicationInfo("Status change to 'IsReplicated = true' at: "
				+ (new Timestamp(new Date().getTime())).toString());
		customer.setSapIsReplicated(true);
		getModelService().save(customer);

		// raise event
		final CustomerReplicationEvent repEvent = createCustomerReplicationEvent(customerId);
		getEventService().publishEvent(repEvent);

	}

	/**
	 * Return new created CustomerReplicationEvent
	 * 
	 * @param customerId
	 * @return CustomerReplicationEvent
	 * 
	 */
	protected CustomerReplicationEvent createCustomerReplicationEvent(final String customerId)
	{
		return new CustomerReplicationEvent(customerId);
	}

	/**
	 * Reads the customer via flexible search
	 * 
	 * @param customerId
	 * @return CustomerModel
	 */
	protected CustomerModel readCustomer(final String customerId)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {c:pk} FROM {Customer AS c} WHERE  {c.customerID} = ?customerId");
		flexibleSearchQuery.addQueryParameter("customerId", customerId);

		final CustomerModel customer = this.flexibleSearchService.searchUnique(flexibleSearchQuery);
		if (customer == null)
		{
			final String msg = "Error while IDoc processing. Called with not existing customer for customer ID: " + customerId;
			throw new IllegalArgumentException(msg);
		}
		return customer;
	}

	/**
	 * @return eventService
	 */
	public EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
