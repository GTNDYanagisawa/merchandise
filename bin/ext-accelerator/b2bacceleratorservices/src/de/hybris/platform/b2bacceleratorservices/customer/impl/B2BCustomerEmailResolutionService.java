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
package de.hybris.platform.b2bacceleratorservices.customer.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.user.CustomerModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class B2BCustomerEmailResolutionService implements CustomerEmailResolutionService
{
	private CustomerEmailResolutionService defaultCustomerEmailResolutionService;

	protected CustomerEmailResolutionService getDefaultCustomerEmailResolutionService()
	{
		return defaultCustomerEmailResolutionService;
	}

	@Required
	public void setDefaultCustomerEmailResolutionService(final CustomerEmailResolutionService defaultCustomerEmailResolutionService)
	{
		this.defaultCustomerEmailResolutionService = defaultCustomerEmailResolutionService;
	}

	@Override
	public String getEmailForCustomer(final CustomerModel customerModel)
	{
		validateParameterNotNullStandardMessage("customerModel", customerModel);

		if (customerModel instanceof B2BCustomerModel
				&& StringUtils.equals(customerModel.getUid(), ((B2BCustomerModel) customerModel).getEmail()))
		{
			return ((B2BCustomerModel) customerModel).getEmail();
		}

		return getDefaultCustomerEmailResolutionService().getEmailForCustomer(customerModel);
	}
}
