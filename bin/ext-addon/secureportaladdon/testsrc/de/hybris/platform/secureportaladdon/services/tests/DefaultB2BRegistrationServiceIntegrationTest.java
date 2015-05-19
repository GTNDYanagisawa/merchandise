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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.secureportaladdon.services.B2BRegistrationService;
import de.hybris.platform.secureportaladdon.tests.util.B2BSecurePortalTestsUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BRegistrationServiceIntegrationTest extends ServicelayerTest
{
	@Resource
	private B2BRegistrationService b2bRegistrationService;

	private String impexContent;
	private List<String> uidsFromImpex;
	private List<String> activeEmailsFromImpex;
	private List<String> inactiveEmailsFromImpex;

	private static final String USER_GROUP = "testUserGroup";

	private static final int UID_INDEX = 1;
	private static final int EMAIL_INDEX = 2;
	private static final int IS_CONTACT_ADDRESS_INDEX = 3;
	private static final int USER_GROUP_INDEX = 3;
	private static final String PATH = "/secureportaladdon/test/";
	private static final String IMPEX_TEST_FILE = "testDefaultB2BRegistrationServiceIntegrationTest.impex";

	@Before
	public void setUp() throws Exception
	{
		importCsv(PATH + IMPEX_TEST_FILE, "UTF-8");
		impexContent = B2BSecurePortalTestsUtil.impexFileToString(PATH + IMPEX_TEST_FILE);
		uidsFromImpex = B2BSecurePortalTestsUtil.getEmployeesUidsFromImpex(impexContent, USER_GROUP, UID_INDEX, USER_GROUP_INDEX);

		activeEmailsFromImpex = B2BSecurePortalTestsUtil.getEmailsFromImpex(impexContent, uidsFromImpex, IS_CONTACT_ADDRESS_INDEX,
				UID_INDEX, EMAIL_INDEX, true);
		inactiveEmailsFromImpex = B2BSecurePortalTestsUtil.getEmailsFromImpex(impexContent, uidsFromImpex,
				IS_CONTACT_ADDRESS_INDEX, UID_INDEX, EMAIL_INDEX, false);
	}


	@Test
	public void testGetEmployeesInUserGroup()
	{
		final List<EmployeeModel> result = b2bRegistrationService.getEmployeesInUserGroup(USER_GROUP);

		assertEquals("getEmployeesInUserGroup should return a list of " + uidsFromImpex.size() + " EmployeeModel.",
				uidsFromImpex.size(), result.size());

		assertTrue("Employees should be the same",
				B2BSecurePortalTestsUtil.compareLists(B2BSecurePortalTestsUtil.employeesToUids(result), uidsFromImpex));

	}


	@Test
	public void testGetEmailAddressesOfEmployees()
	{
		final List<EmployeeModel> employees = b2bRegistrationService.getEmployeesInUserGroup(USER_GROUP);
		final List<EmailAddressModel> result = b2bRegistrationService.getEmailAddressesOfEmployees(employees);

		final int totalNumberOfEmails = activeEmailsFromImpex.size() + inactiveEmailsFromImpex.size();

		assertEquals("Total number of emails should be the same as the number of employees.", totalNumberOfEmails, employees.size());

		assertEquals(
				"getEmailAddressesOfEmployees should return a list of " + activeEmailsFromImpex.size() + " EmailAddressModel.",
				activeEmailsFromImpex.size(), result.size());

		assertTrue("Emails should be the same", B2BSecurePortalTestsUtil.compareLists(
				B2BSecurePortalTestsUtil.emailAddressModelsToEmails(result), activeEmailsFromImpex));
	}





}
