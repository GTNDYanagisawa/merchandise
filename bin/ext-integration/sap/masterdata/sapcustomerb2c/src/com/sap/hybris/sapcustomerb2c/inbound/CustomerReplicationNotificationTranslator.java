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


import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.security.JaloSecurityException;


/**
 * This class includes the translator for customer replication notification
 */
public class CustomerReplicationNotificationTranslator extends AbstractSpecialValueTranslator
{

	private CustomerImportService dataHubConsumerImportService;

	/**
	 * @return dataHubConsumerImportService
	 */
	public CustomerImportService getDataHubConsumerImportService()
	{
		return dataHubConsumerImportService;
	}

	/**
	 * Set dataHubConsumerImportService
	 * 
	 * @param dataHubConsumerImportService
	 */
	public void setDataHubConsumerImportService(final CustomerImportService dataHubConsumerImportService)
	{
		this.dataHubConsumerImportService = dataHubConsumerImportService;
	}

	@Override
	public void init(final SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException
	{
		if (dataHubConsumerImportService == null)
		{
			dataHubConsumerImportService = (CustomerImportService) Registry.getApplicationContext().getBean("customerImportService");
		}
	}

	@Override
	public void performImport(final String transformationExpression, final Item processedItem) throws ImpExException
	{
		final String customerId = getCustomerIdItem(processedItem);
		dataHubConsumerImportService.processConsumerReplicationNotificationFromHub(customerId);
	}

	private String getCustomerIdItem(final Item processedItem) throws ImpExException
	{
		String customerId = null;
		try
		{
			customerId = processedItem.getAttribute("customerID").toString();
		}
		catch (final JaloSecurityException e)
		{
			throw new ImpExException(e);
		}
		return customerId;
	}

	@Override
	public void validate(final String paramString) throws HeaderValidationException
	{

	}

	@Override
	public String performExport(final Item paramItem) throws ImpExException
	{
		return null;
	}

	@Override
	public boolean isEmpty(final String paramString)
	{
		return false;
	}
}
