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
package de.hybris.platform.secureportaladdon.services;

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;

import java.util.List;


/**
 * Service methods that are used by the B2B registration process
 */
public interface B2BRegistrationService
{

	/**
	 * Gets the list of employees that are part of a given user group
	 * 
	 * @param userGroup
	 *           The name of the user group
	 * @return Employees within the user group
	 */
	public List<EmployeeModel> getEmployeesInUserGroup(String userGroup);

	/**
	 * Gets the contact email address of the specified list of employees
	 * 
	 * @param employees
	 *           List of employees to get email address from
	 * @return List of email addresses. It is possible that the list is empty since employees are not required to have an
	 *         email nor a contact address
	 */
	public List<EmailAddressModel> getEmailAddressesOfEmployees(List<EmployeeModel> employees);

}
