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
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.task.RetryLaterException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Action class to publish the registered customer to the Data Hub
 */
public class CustomerPublishAction extends AbstractSimpleDecisionAction<BusinessProcessModel>
{

	private CustomerExportService sendCustomerToDataHub;

	private DefaultStoreSessionFacade storeSessionFacade;

	private DefaultB2CSapCustomerAccountService customerAccountService;

	/**
	 * @return businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
	}

	/**
	 * /** action method to the update the customer and trigger the publish to Data Hub
	 */
	@Override
	public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
	{

		// set the time stamp in the sap replication info field
		final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer().setSapReplicationInfo(
				"Sent to datahub " + dateFormat.format(Calendar.getInstance().getTime()));
		setSapContactId(businessProcessModel);
		modelService.save(((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer());

		final BaseStoreModel store = ((StoreFrontCustomerProcessModel) businessProcessModel).getStore();

		// prepare sending data to Data Hub
		final String baseStoreUid = store != null ? store.getUid() : null;
		getSendCustomerToDataHub().sendCustomerData(((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer(),
				baseStoreUid, getStoreSessionFacade().getCurrentLanguage().getIsocode());
		return Transition.OK;
	}

	/**
	 * Generates a new Contact ID and set it to the Customer Model
	 * 
	 * @param businessProcessModel
	 */
	protected void setSapContactId(final BusinessProcessModel businessProcessModel)
	{
		getCustomerAccountService().generateContactId(((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer());
	}

	/**
	 * return instance to send customer to the Data Hub
	 * 
	 * @return sendCustomerToDataHub
	 */
	public CustomerExportService getSendCustomerToDataHub()
	{
		return sendCustomerToDataHub;
	}

	/**
	 * set instance to send customer to the Data Hub
	 * 
	 * @param sendCustomerToDataHub
	 */
	public void setSendCustomerToDataHub(final CustomerExportService sendCustomerToDataHub)
	{
		this.sendCustomerToDataHub = sendCustomerToDataHub;
	}

	/**
	 * Return Session Store Facade instance
	 * 
	 * @return storeSessionFacade
	 */
	public DefaultStoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	/**
	 * 
	 * Set Session Store Facade instance
	 * 
	 * @param storeSessionFacade
	 */
	public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	/**
	 * Return the Customer Account Service
	 * 
	 * @return customerAccountService
	 */
	public DefaultB2CSapCustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * Sets the Customer Account Service
	 * 
	 * @param customerAccountService
	 */
	public void setCustomerAccountService(final DefaultB2CSapCustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}
}
