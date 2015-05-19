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

import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;


/**
 * override the generateCustomerID method of class {@link DefaultCustomerAccountService} and use the
 * sapCustomerIdGenerator to generate a customer ID
 */
public class DefaultB2CSapCustomerAccountService extends DefaultCustomerAccountService
{

	private PersistentKeyGenerator sapCustomerIdGenerator;
	private PersistentKeyGenerator sapContactIdGenerator;

	/**
	 * Generates with sapCustomerIdGenerator a customer ID during registration
	 * 
	 * @param customerModel
	 */
	@Override
	protected void generateCustomerId(final CustomerModel customerModel)
	{
		customerModel.setCustomerID((String) getSapCustomerIdGenerator().generate());
	}

	/**
	 * Generates with sapCustomerIdGenerator a customer ID during registration
	 * 
	 * @param customerModel
	 */
	protected void generateContactId(final CustomerModel customerModel)
	{
		customerModel.setSapContactID((String) getSapContactIdGenerator().generate());
	}

	/**
	 * Returns the sapCustomerIdGenerator
	 * 
	 * @return sapCustomerIdGenerator
	 */
	public PersistentKeyGenerator getSapCustomerIdGenerator()
	{
		return sapCustomerIdGenerator;
	}

	/**
	 * Sets the sapCustomerIdGenerator
	 * 
	 * @param sapCustomerIdGenerator
	 */
	public void setSapCustomerIdGenerator(final PersistentKeyGenerator sapCustomerIdGenerator)
	{
		this.sapCustomerIdGenerator = sapCustomerIdGenerator;
	}

	/**
	 * Returns the Generator instance
	 * 
	 * @return sapContactIdGenerator
	 */
	public PersistentKeyGenerator getSapContactIdGenerator()
	{
		return sapContactIdGenerator;
	}

	/**
	 * Sets the Contact ID Generator
	 * 
	 * @param sapContactIdGenerator
	 */
	public void setSapContactIdGenerator(final PersistentKeyGenerator sapContactIdGenerator)
	{
		this.sapContactIdGenerator = sapContactIdGenerator;
	}

}
