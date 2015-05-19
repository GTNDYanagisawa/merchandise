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
package de.hybris.platform.acceleratorfacades.customerlocation.impl;

import de.hybris.platform.acceleratorfacades.customerlocation.CustomerLocationFacade;
import de.hybris.platform.acceleratorservices.customer.CustomerLocationService;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link de.hybris.platform.acceleratorfacades.customerlocation.CustomerLocationFacade}
 * 
 */
public class DefaultCustomerLocationFacade implements CustomerLocationFacade
{
	private CustomerLocationService customerLocationService;

	@Override
	public void setUserLocationData(final UserLocationData userLocationData)
	{
		userLocationData.setSearchTerm(StringUtils.trimToEmpty(userLocationData.getSearchTerm()));
		getCustomerLocationService().setUserLocation(userLocationData);
	}

	@Override
	public UserLocationData getUserLocationData()
	{
		return getCustomerLocationService().getUserLocation();
	}

	protected CustomerLocationService getCustomerLocationService()
	{
		return customerLocationService;
	}

	@Required
	public void setCustomerLocationService(final CustomerLocationService customerLocationService)
	{
		this.customerLocationService = customerLocationService;
	}

}