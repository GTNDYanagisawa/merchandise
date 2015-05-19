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
package de.hybris.platform.secureportaladdon.dao.impl.tests;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.secureportaladdon.dao.B2BRegistrationDao;
import de.hybris.platform.secureportaladdon.tests.util.B2BSecurePortalTestsUtil;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BRegistrationDaoIntegrationTest extends ServicelayerTest
{
	@Resource
	private B2BRegistrationDao b2bRegistrationDao;

	private String impexContent;
	private List<String> userGroups;

	private static final String PATH = "/secureportaladdon/test/";
	private static final String IMPEX_TEST_FILE = "testDefaultB2BRegistrationDaoIntegrationTest.impex";

	private static final int UID_INDEX = 1;
	private static final int USER_GROUP_INDEX = 3;
	private static final String INEXISTANT_USER_GROUP = "inexistantUserGroup";


	@Before
	public void setUp() throws Exception
	{
		importCsv(PATH + IMPEX_TEST_FILE, "UTF-8");
		impexContent = B2BSecurePortalTestsUtil.impexFileToString(PATH + IMPEX_TEST_FILE);

		userGroups = B2BSecurePortalTestsUtil.getUserGroupsFromImpex(impexContent, UID_INDEX);
	}


	/*
	 * Make sure no user is associated to userGroup INEXISTANT_USER_GROUP in file IMPEX_TEST_FILE.
	 */
	@Test
	public void testGetEmployeesInUserGroup()
	{

		List<EmployeeModel> employees;
		List<String> uidsFromDao;
		List<String> uidsFromImpex;

		for (final String userGroup : userGroups)
		{
			uidsFromImpex = B2BSecurePortalTestsUtil.getEmployeesUidsFromImpex(impexContent, userGroup, UID_INDEX, USER_GROUP_INDEX);

			employees = b2bRegistrationDao.getEmployeesInUserGroup(userGroup);
			uidsFromDao = B2BSecurePortalTestsUtil.employeesToUids(employees);

			assertTrue("Dao should return same employees as those in impex file",
					B2BSecurePortalTestsUtil.compareLists(uidsFromImpex, uidsFromDao));
		}

		//Testing inexistant userGroup		
		employees = b2bRegistrationDao.getEmployeesInUserGroup(INEXISTANT_USER_GROUP);
		assertTrue("Dao should not return any employee for userGroup " + INEXISTANT_USER_GROUP, employees.isEmpty());
	}

}
