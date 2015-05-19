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
package de.hybris.platform.chinaaccelerator.services.customer.impl;

import de.hybris.platform.chinaaccelerator.services.customer.daos.CustomerDao;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Validates for CustomerModel if attribute mobileNumber for this model is unique. 
 * Throws CustomerMobileNumberValidateInterceptor if the unique constraint is violated.
 */
public class CustomerMobileNumberValidateInterceptor implements ValidateInterceptor
{
	private CustomerDao customerDao;
	private TypeService typeService;


	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) model;
			final String mobileNumber = customer.getMobileNumber();

			if (mobileNumber!= null && !mobileNumber.isEmpty() && ctx.isModified(customer, CustomerModel.MOBILENUMBER))
			{
				final List<CustomerModel> result = getCustomerDao().findCustomerByMobileNumber(mobileNumber);
				if (!result.isEmpty())
				{
					if (customer.getPk() == null || !customer.getPk().equals(result.get(0).getPk()))
					{
						throw new InterceptorException("this mobile number has been registered");
					}
				}
			}
		}

	}

	protected CustomerDao getCustomerDao()
	{
		return customerDao;
	}

	@Required
	public void setCustomerDao(final CustomerDao customerDao)
	{
		this.customerDao = customerDao;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
