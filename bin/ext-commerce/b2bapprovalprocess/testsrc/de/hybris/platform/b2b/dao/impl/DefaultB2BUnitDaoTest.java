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
import de.hybris.platform.b2b.dao.B2BUnitDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
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
public class DefaultB2BUnitDaoTest extends B2BIntegrationTransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultB2BUnitDaoTest.class);

	@Resource
	public B2BUnitDao b2bUnitDao;

	@Before
	public void setup() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
	}


	@Test
	public void shouldFindB2BUnitMembersOfVariousGroups()
	{
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("GC");
		// 
		List<B2BCustomerModel> members = b2bUnitDao.findB2BUnitMembersByGroup(unit, "b2bcustomergroup");
		Assert.assertEquals(1, members.size());
		// GC Admin
		members = b2bUnitDao.findB2BUnitMembersByGroup(unit, "b2badmingroup");
		Assert.assertEquals(1, members.size());
		// GC CEO, GC Mgr
		members = b2bUnitDao.findB2BUnitMembersByGroup(unit, "b2bmanagergroup");
		Assert.assertEquals(2, members.size());

	}



}
