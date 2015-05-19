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
package de.hybris.platform.secureportaladdon.services.impl;

import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.secureportaladdon.dao.B2BRegistrationDao;
import de.hybris.platform.secureportaladdon.services.B2BRegistrationService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;


/**
 * Default implementation of {@link B2BRegistrationService}
 */
public class DefaultB2BRegistrationService implements B2BRegistrationService
{

	private B2BRegistrationDao registrationDao;

	private EmailService emailService;

	/**
	 * @param registrationDao
	 *           the registrationDao to set
	 */
	@Required
	public void setRegistrationDao(final B2BRegistrationDao registrationDao)
	{
		this.registrationDao = registrationDao;
	}

	/**
	 * @param emailService
	 *           the emailService to set
	 */
	@Required
	public void setEmailService(final EmailService emailService)
	{
		this.emailService = emailService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.secureportaladdon.services.B2BRegistrationService#getEmployeesInUserGroup(java.lang.String)
	 */
	@Override
	public List<EmployeeModel> getEmployeesInUserGroup(final String userGroup)
	{
		return registrationDao.getEmployeesInUserGroup(userGroup);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.secureportaladdon.services.B2BRegistrationService#getEmailAddressesOfEmployees(java.util.List)
	 */
	@Override
	public List<EmailAddressModel> getEmailAddressesOfEmployees(final List<EmployeeModel> employees)
	{

		final List<EmailAddressModel> emails = new ArrayList<>();

		for (final EmployeeModel employee : employees)
		{
			for (final AddressModel address : Lists.newArrayList(employee.getAddresses()))
			{
				if (BooleanUtils.isTrue(address.getContactAddress()))
				{
					if (StringUtils.isNotBlank(address.getEmail()))
					{
						final EmailAddressModel emailAddress = emailService.getOrCreateEmailAddressForEmail(address.getEmail(),
								employee.getName());
						emails.add(emailAddress);
					}
					break;
				}
			}
		}

		return emails;

	}
}
