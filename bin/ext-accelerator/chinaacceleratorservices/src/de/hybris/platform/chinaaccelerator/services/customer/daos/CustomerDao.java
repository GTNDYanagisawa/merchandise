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
package de.hybris.platform.chinaaccelerator.services.customer.daos;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * CustomerDao
 */
public interface CustomerDao extends Dao
{
	/**
	 * Find customer for given mobileNumber
	 *
	 * @param mobileNumber
	 *           of the customer
	 * @return the list of customer
	 */
	public List<CustomerModel> findCustomerByMobileNumber(final String mobileNumber);
}
