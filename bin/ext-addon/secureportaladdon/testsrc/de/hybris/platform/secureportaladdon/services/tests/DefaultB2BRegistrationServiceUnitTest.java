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
package de.hybris.platform.secureportaladdon.services.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.secureportaladdon.dao.B2BRegistrationDao;
import de.hybris.platform.secureportaladdon.services.impl.DefaultB2BRegistrationService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultB2BRegistrationServiceUnitTest
{

	private DefaultB2BRegistrationService b2bRegistrationService;
	private EmailService emailService;
	private B2BRegistrationDao registrationDao;

	EmployeeModel employeeWithActiveAddress, employeeWithInctiveAddress;
	EmailAddressModel emailAddressModel1, emailAddressModel2;

	private List<EmployeeModel> employeeModels;

	private static final String USER_GROUP = "userGroupTest";

	private static final String DEFAULT_NAME_1 = "Richard Feynman";
	private static final String DEFAULT_EMAIL_1 = "richard.feynman@Ttest.ca";

	private static final String DEFAULT_NAME_2 = "Kurt Godel";
	private static final String DEFAULT_EMAIL_2 = "kurt.godel@Ttest.ca";


	@Before
	public void setUp()
	{
		b2bRegistrationService = new DefaultB2BRegistrationService();
		b2bRegistrationService.setRegistrationDao(registrationDao = mock(B2BRegistrationDao.class));
		b2bRegistrationService.setEmailService(emailService = mock(EmailService.class));

		emailAddressModel1 = createEmailAddressModel(DEFAULT_EMAIL_1, DEFAULT_NAME_1);
		emailAddressModel2 = createEmailAddressModel(DEFAULT_EMAIL_2, DEFAULT_NAME_2);

		final AddressModel addressModel1 = createAddressModel(DEFAULT_EMAIL_1, true);
		employeeWithActiveAddress = createEmployee(DEFAULT_NAME_1, DEFAULT_EMAIL_1, addressModel1);

		final AddressModel addressModel2 = createAddressModel(DEFAULT_EMAIL_2, false);
		employeeWithInctiveAddress = createEmployee(DEFAULT_NAME_2, DEFAULT_EMAIL_2, addressModel2);

		employeeModels = Arrays.asList(employeeWithActiveAddress, employeeWithInctiveAddress);
	}

	@Test
	public void testGetEmployeesInUserGroup()
	{
		when(registrationDao.getEmployeesInUserGroup(USER_GROUP)).thenReturn(employeeModels);

		final List<EmployeeModel> result = b2bRegistrationService.getEmployeesInUserGroup(USER_GROUP);

		assertEquals("getEmployeesInUserGroup should return a list of one EmployeeModel.", 2, result.size());
		assertEquals("And should equals what the mock returned", employeeWithActiveAddress, result.get(0));
		assertEquals("And should equals what the mock returned", employeeWithInctiveAddress, result.get(1));

	}


	@Test
	public void testGetEmailAddressesOfEmployees()
	{
		when(emailService.getOrCreateEmailAddressForEmail(DEFAULT_EMAIL_1, DEFAULT_NAME_1)).thenReturn(emailAddressModel1);
		when(emailService.getOrCreateEmailAddressForEmail(DEFAULT_EMAIL_2, DEFAULT_NAME_2)).thenReturn(emailAddressModel2);

		List<EmailAddressModel> result = b2bRegistrationService.getEmailAddressesOfEmployees(employeeModels);

		assertEquals("getEmailAddressesOfEmployees should return a list of one EmployeeModel.", 1, result.size());

		assertEquals("getEmailAddressesOfEmployees should return the same list as the one returned by the mock",
				emailAddressModel1.getEmailAddress(), result.get(0).getEmailAddress());

		//Make the second address active
		((AddressModel) (employeeModels.get(1).getAddresses().toArray()[0])).setContactAddress(true);

		result = b2bRegistrationService.getEmailAddressesOfEmployees(employeeModels);

		assertEquals("getEmailAddressesOfEmployees should return a list of one EmployeeModel.", 2, result.size());

		assertEquals("getEmailAddressesOfEmployees should return the same list as the one returned by the mock",
				emailAddressModel1.getEmailAddress(), result.get(0).getEmailAddress());

		assertEquals("getEmailAddressesOfEmployees should return the same list as the one returned by the mock",
				emailAddressModel2.getEmailAddress(), result.get(1).getEmailAddress());

	}


	/**
	 * 
	 * */
	private EmployeeModel createEmployee(final String name, final String email, final AddressModel addressModel)
	{
		final EmployeeModel employee = new EmployeeModel();

		employee.setName(name);
		employee.setUid(email);
		employee.setAddresses(Arrays.asList(addressModel));

		return employee;
	}

	/**
	 * 
	 * */

	private AddressModel createAddressModel(final String email, final boolean isContactAddress)
	{
		final AddressModel address = new AddressModel();

		address.setEmail(email);
		address.setContactAddress(isContactAddress);

		return address;
	}


	/**
	 *
	 */
	private EmailAddressModel createEmailAddressModel(final String email, final String name)
	{
		final EmailAddressModel emailModel = new EmailAddressModel();

		emailModel.setEmailAddress(email);
		emailModel.setDisplayName(name);

		return emailModel;
	}


}
