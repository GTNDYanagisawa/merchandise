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
import de.hybris.platform.b2b.dao.B2BCostCenterDao;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class DefaultB2BCostCenterDaoTest extends B2BIntegrationTransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultB2BCostCenterDaoTest.class);

	@Resource
	public B2BCostCenterDao b2bCostCenterDao;

	@Before
	public void setup() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
	}


	@Test
	public void shouldFindB2BCostCenterForCode()
	{
		final B2BUnitModel unit = b2bUnitService.getUnitForUid("GC");
		final Set<B2BUnitModel> branch = b2bUnitService.getBranch(unit);
		final CurrencyModel currency = commonI18NService.getCurrency("GBP");
		final List<B2BCostCenterModel> costCenters = b2bCostCenterDao.findActiveCostCentersByBranchAndCurrency(branch, currency);
		Assert.assertEquals(6, costCenters.size());
	}



}
