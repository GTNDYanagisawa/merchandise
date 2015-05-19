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
package de.hybris.platform.chinaaccelerator.facades.customer.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.chinaaccelerator.facades.customer.ExtendedCustomerFacade;
import de.hybris.platform.chinaaccelerator.services.customer.impl.CustomerMobileNumberValidateInterceptor;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.customer.impl.DefaultCustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;


/**
 * Default implementation for the {@link CustomerFacade}.
 */
public class DefaultExtendedCustomerFacade extends DefaultCustomerFacade implements ExtendedCustomerFacade
{
	@Override
	public void register(final RegisterData registerData) throws DuplicateUidException
	{
		validateParameterNotNullStandardMessage("registerData", registerData);
		Assert.hasText(registerData.getFirstName(), "The field [FirstName] cannot be empty");
		Assert.hasText(registerData.getLastName(), "The field [LastName] cannot be empty");
		Assert.hasText(registerData.getLogin(), "The field [Login] cannot be empty");

		final CustomerModel newCustomer = getModelService().create(CustomerModel.class);
		newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));

		if (StringUtils.isNotBlank(registerData.getFirstName()) && StringUtils.isNotBlank(registerData.getLastName()))
		{
			newCustomer.setName(getCustomerNameStrategy().getName(registerData.getFirstName(), registerData.getLastName()));
		}
		final TitleModel title = getUserService().getTitleForCode(registerData.getTitleCode());
		newCustomer.setTitle(title);
		setUidForRegister(registerData, newCustomer);

		newCustomer.setMobileNumber(registerData.getMobileNumber());

		newCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		newCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		getCustomerAccountService().register(newCustomer, registerData.getPassword());
	}

	@Override
	public void updateMobileNumber(final CustomerData customerData, final String currentPassword)
			throws PasswordMismatchException, DuplicateUidException
	{
		validateParameterNotNullStandardMessage("customerData", customerData);
		validateParameterNotNullStandardMessage("currentPassword", currentPassword);

		final CustomerModel customer = getUserService().getUserForUID(customerData.getUid().toLowerCase(), CustomerModel.class);
		final String encodedCurrentPassword = getPasswordEncoderService().encode(customer, currentPassword,
				customer.getPasswordEncoding());
		if (!encodedCurrentPassword.equals(customer.getEncodedPassword()))
		{
			throw new PasswordMismatchException(customer.getUid());
		}

		customer.setMobileNumber(customerData.getMobileNumber());

		try
		{
			getModelService().save(customer);
		}
		catch (final ModelSavingException e)
		{
			if (e.getCause() instanceof InterceptorException
					&& ((InterceptorException) e.getCause()).getInterceptor().getClass()
							.equals(CustomerMobileNumberValidateInterceptor.class))
			{
				throw new DuplicateUidException(customer.getMobileNumber(), e);
			}
			else
			{
				throw e;
			}
		}
	}
}
