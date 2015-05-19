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
package de.hybris.platform.b2b.services.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTransactionalTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BApproverService;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BApproverServiceTest extends B2BIntegrationTransactionalTest
{

	@Resource
	public B2BApproverService<B2BCustomerModel> b2bApproverService;
	@Resource
	public B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("EUR"));
	}


	@Test
	public void getEmployeeForUserGroup() throws Exception
	{
		final B2BCustomerModel user = userService.getUserForUID("IC CEO", B2BCustomerModel.class);
		final UserGroupModel groupToFind = modelService.create(UserGroupModel.class);
		groupToFind.setUid("testgroup");

		modelService.save(groupToFind);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(user.getGroups());
		groups.add(groupToFind);
		user.setGroups(groups);
		modelService.save(user);

		final B2BUnitModel unit = b2bUnitService.getUnitForUid("IC");
		b2bCustomerService.addMember(user, unit);
		modelService.save(user);

		final Collection<B2BCustomerModel> employeesForUserGroup = b2bUnitService.getUsersOfUserGroup(
				b2bUnitService.getUnitForUid("IC"), "testgroup", true);
		Assert.assertNotNull(employeesForUserGroup);
		Assert.assertEquals(1, employeesForUserGroup.size());
		Assert.assertEquals(user.getUid(), employeesForUserGroup.iterator().next().getUid());
	}

	@Test
	public void shouldGetMembersOfGroup() throws Exception
	{
		final String userId = "IC CEO";
		login(userId);
		final Set<PrincipalModel> approversOfGroup = userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP).getMembers();
		Assert.assertNotNull(approversOfGroup);
	}

	@Test
	public void shouldGetAccountManagerApprovers() throws Exception
	{
		final String userId = "GC CEO";
		login(userId);
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("GC");

		final UserGroupModel salesRepGroup = userService.getUserGroupForUID("acctmgrgroup");
		final Set<UserGroupModel> groups = new HashSet<UserGroupModel>();
		groups.add(salesRepGroup);

		unit.setAccountManagerGroups(groups);
		modelService.save(unit);

		final List<UserModel> approversOfGroup = b2bApproverService.getAccountManagerApprovers(unit);
		Assert.assertNotNull(approversOfGroup);

	}
}
