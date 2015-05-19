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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTest;
import de.hybris.platform.b2b.B2BIntegrationTransactionalTest;
import de.hybris.platform.b2b.dao.PrincipalGroupMembersDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import java.util.List;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class DefaultPrincipalGroupMembersDaoTest extends B2BIntegrationTransactionalTest
{
	@Resource
	PrincipalGroupMembersDao defaultPrincipalGroupMembersDao;
	@Resource
	B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Before
	public void setup() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
	}

	public static final Logger LOG = Logger.getLogger(DefaultPrincipalGroupMembersDaoTest.class);

	@Test
	public void shouldFindOnlyMemberB2BUnits()
	{
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("GC");
		Assert.assertNotNull(unit);
		final List<B2BUnitModel> units = defaultPrincipalGroupMembersDao.findAllMembersByType(unit, B2BUnitModel.class);
		Assert.assertEquals(2, units.size());
	}

	@Test
	public void shouldFindOnlyMemberB2BCustomers()
	{
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("GC");
		Assert.assertNotNull(unit);
		final List<B2BCustomerModel> units = defaultPrincipalGroupMembersDao.findAllMembersByType(unit, B2BCustomerModel.class);
		Assert.assertEquals(3, units.size());
	}

	@Test
	public void shouldFindAllMembers()
	{
		final UserGroupModel unit = b2bUnitService.getUnitForUid("GC");
		Assert.assertNotNull(unit);
		final List<PrincipalModel> principals = defaultPrincipalGroupMembersDao.findAllMembersByType(unit, PrincipalModel.class);
		Assert.assertEquals(5, principals.size());
	}


}
