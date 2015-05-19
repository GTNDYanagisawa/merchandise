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
package de.hybris.platform.sap.sapordermgmtservices.partner;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Collection;


/**
 * Allows to access customer and contact information in the context of SAP synchronous order management.
 */
public interface SapPartnerService
{

	/**
	 * Retrieving sold-to
	 * 
	 * @return Technical SAP key of sold-to party connected to the current hybris user
	 * 
	 */
	String getCurrentSapCustomerId();



	/**
	 * Retrieving contact person
	 * 
	 * @return Technical SAP key of contact person connected to the current hybris user
	 */
	String getCurrentSapContactId();

	/**
	 * Retrieving hybris address for an SAP customer (who can act as sold-to or ship-to party in the system)
	 * 
	 * @param sapCustomerId
	 *           Technical key of SAP customer
	 * @return hybris representation of the customer's address
	 */
	AddressModel getHybrisAddressForSAPCustomerId(String sapCustomerId);

	/**
	 * Retrieving delivery addresses belonging to the current session customer. These addresses correspond to ship-to
	 * parties assigned to the current customer in the SAP back end
	 * 
	 * @return Possible delivery addresses
	 */
	Collection<AddressModel> getAllowedDeliveryAddresses();

	/**
	 * Retrieving the hybris representation of a customer.
	 * 
	 * @param sapContactId
	 *           Technical key of an SAP contact person
	 * @return Customer
	 */
	CustomerModel getB2BCustomerForSapContactId(String sapContactId);
}
