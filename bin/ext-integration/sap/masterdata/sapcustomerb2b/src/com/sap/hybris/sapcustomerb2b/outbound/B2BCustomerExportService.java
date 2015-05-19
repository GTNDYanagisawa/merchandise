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
package com.sap.hybris.sapcustomerb2b.outbound;

import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.DEFAULT_FEED;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.RAW_HYBRIS_B2B_CUSTOMER;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;


/**
 * Class to prepare the customer data and send the data to the Data Hub
 */
public class B2BCustomerExportService
{
	private static final Logger LOGGER = Logger.getLogger(com.sap.hybris.sapcustomerb2b.outbound.B2BCustomerExportService.class
			.getName());

	private CustomerNameStrategy customerNameStrategy;
	private DataHubOutboundService dataHubOutboundService;
	private B2BUnitService<B2BUnitModel, ?> b2bUnitService;


	private String feed = DEFAULT_FEED;

	/**
	 * return Data Hub Outbound Service
	 * 
	 * @return dataHubOutboundService
	 */
	public DataHubOutboundService getDataHubOutboundService()
	{
		return dataHubOutboundService;
	}

	/**
	 * set Data Hub Outbound Service
	 * 
	 * @param dataHubOutboundService
	 */
	public void setDataHubOutboundService(final DataHubOutboundService dataHubOutboundService)
	{
		this.dataHubOutboundService = dataHubOutboundService;
	}

	/**
	 * map B2B customer Model to the target map and send data to the Data Hub
	 * 
	 * @param changedB2bCustomerModel
	 */
	public void prepareAndSend(final B2BCustomerModel changedB2bCustomerModel)
	{

		if (changedB2bCustomerModel.getDefaultB2BUnit() == null)
		{
			return;
		}

		final B2BUnitModel rootUnit = b2bUnitService.getRootUnit(changedB2bCustomerModel.getDefaultB2BUnit());

		final List<Map<String, Object>> rawData = new ArrayList<>();

		// first create a table with all B2BCustomers related to the B2BUnit of the changed B2BCustomer
		// to keep data in ERP consistent, we have to send the B2BUnit (-> will get the ERP Customer) and ALL B2BCustomers (will get the ERP Contact)
		// in one Block
		for (final PrincipalModel member : rootUnit.getMembers())
		{
			if (member instanceof B2BCustomerModel)
			{
				final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) member;
				if (b2bCustomerModel.getCustomerID() != null && !b2bCustomerModel.getCustomerID().isEmpty())
				{
					rawData.add(prepareB2BCustomerData(b2bCustomerModel));
				}
				else
				{
					if (LOGGER.isDebugEnabled())
					{
						LOGGER.debug("Contact " + b2bCustomerModel.getUid() + " of B2BUnit " + rootUnit.getUid()
								+ " was dropped because it has no CustomerID (not replicated from ERP or Customer ID was deleted)");
					}

				}
			}
		}

		// second send the data to Datahub in ONE block to ensure that auto compose and publish cannot occur in between  
		sendCustomerToDataHub(rawData);

	}


	protected void sendCustomerToDataHub(final List<Map<String, Object>> rawData)
	{
		if (rawData != null && !rawData.isEmpty())
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("The following values was send to Data Hub" + rawData + "(to the feed" + getFeed() + " into raw model "
						+ RAW_HYBRIS_B2B_CUSTOMER + ")");
			}
			try
			{
				dataHubOutboundService.sendToDataHub(getFeed(), RAW_HYBRIS_B2B_CUSTOMER, rawData);
			}
			catch (final DataHubOutboundException e)
			{
				LOGGER.warn("Error processing sending data to Data Hub. DataHubOutboundException: " + e.getMessage());
			}
			catch (final DataHubCommunicationException e)
			{
				LOGGER.warn("Error processing sending data to Data Hub. DataHubCommunicationException: " + e.getMessage());
			}
		}
		else
		{
			LOGGER.debug("No send to datahub occured because target is empty");
		}
	}

	protected Map<String, Object> prepareB2BCustomerData(final B2BCustomerModel b2bCustomerModel)
	{
		final Map<String, Object> target;
		final String[] names = customerNameStrategy.splitName(b2bCustomerModel.getName());

		final String sessionLanguage = b2bCustomerModel.getSessionLanguage() != null ? b2bCustomerModel.getSessionLanguage()
				.getIsocode() : null;

		final String titleCode = b2bCustomerModel.getTitle() != null ? b2bCustomerModel.getTitle().getCode() : null;

		//read the address to determine the phone number
		final String expectedPublicKey = b2bCustomerModel.getCustomerID() + "|" + b2bCustomerModel.getCustomerID()
				+ "|BUS1006001|null";
		String phoneNumber = "";
		for (final AddressModel address : b2bCustomerModel.getAddresses())
		{
			if (expectedPublicKey.equals(address.getPublicKey()))
			{
				phoneNumber = address.getPhone1();
				break;
			}
		}


		target = new HashMap<String, Object>();

		target.put("UID", b2bCustomerModel.getUid());
		target.put("customerID", b2bCustomerModel.getCustomerID());
		target.put("firstname", names[0]);
		target.put("lastname", names[1]);
		target.put("sessionLanguage", sessionLanguage);
		target.put("title", titleCode);
		target.put("email", b2bCustomerModel.getUid());
		target.put("phone1", phoneNumber);
		target.put("parent", b2bCustomerModel.getDefaultB2BUnit().getUid());

		return target;
	}

	/**
	 * return data hub feed
	 * 
	 * @return feed
	 */
	public String getFeed()
	{
		return feed;
	}

	/**
	 * set data hub feed (usually set via the local property file)
	 * 
	 * @param feed
	 */
	public void setFeed(final String feed)
	{
		this.feed = feed;
	}

	/**
	 * @return customerNameStrategy
	 */
	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	/**
	 * @param customerNameStrategy
	 */
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	/**
	 * @return B2BUnitService
	 */
	public B2BUnitService<B2BUnitModel, ?> getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * set the B2B unit service
	 * 
	 * @param b2bUnitService
	 */
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, ?> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
