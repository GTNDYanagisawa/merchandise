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
package de.hybris.platform.chinaaccelerator.services.customer.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.chinaaccelerator.services.customer.daos.CustomerDao;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;


/**
 * DefaultCustomerDao
 */
public class DefaultCustomerDao extends AbstractItemDao implements CustomerDao
{
	protected static final String CUSTOMER_QUERY = "SELECT {" + CustomerModel.PK + "} FROM {" + CustomerModel._TYPECODE
			+ "} WHERE {" + CustomerModel.MOBILENUMBER + "} = ?mobileNumber";

	@Override
	public List<CustomerModel> findCustomerByMobileNumber(final String mobileNumber)
	{
		validateParameterNotNull(mobileNumber, "Code must not be null");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(CUSTOMER_QUERY);
		query.addQueryParameter("mobileNumber", mobileNumber);
		final SearchResult<CustomerModel> result = search(query);
		return result.getResult();
	}
}
