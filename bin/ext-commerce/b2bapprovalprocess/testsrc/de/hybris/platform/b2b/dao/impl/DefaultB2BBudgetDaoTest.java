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
import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.util.B2BDateUtils;
import de.hybris.platform.core.model.user.UserModel;
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
public class DefaultB2BBudgetDaoTest extends B2BIntegrationTransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultB2BBudgetDaoTest.class);

	@Resource
	private DefaultB2BBudgetDao b2bBudgetDao;
	@Resource
	private B2BDateUtils b2bDateUtils;

	@Before
	public void setup() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
	}

	@Test
	public void shouldFindBudgetForCode() throws Exception
	{

		final UserModel user = userService.getUserForUID("GC CEO");
		final B2BUnitModel unit = b2bUnitService.getParent(((B2BCustomerModel) user));
		final B2BBudgetModel budgetModel = (B2BBudgetModel) modelService.create(B2BBudgetModel.class);
		final String code = "myBudget";
		budgetModel.setCode(code);
		budgetModel.setUnit(unit);
		budgetModel.setBudget(java.math.BigDecimal.valueOf(90));
		budgetModel.setCurrency(commonI18NService.getCurrency("USD"));
		budgetModel.setDateRange(b2bDateUtils.createDateRange(B2BPeriodRange.YEAR));

		modelService.save(budgetModel);
		modelService.refresh(budgetModel);

		Assert.assertNotNull(b2bBudgetDao.findBudgetByCode(code));
	}

	@Test
	public void shouldNotFindBudgetForCode() throws Exception
	{
		final String code = "budget1";
		final B2BBudgetModel budget = b2bBudgetDao.findBudgetByCode(code);
		Assert.assertNull(budget);
	}

	@Test
	public void shouldFindAllB2BBudgets() throws Exception
	{
		final List<B2BBudgetModel> budgets = b2bBudgetDao.find();
		Assert.assertNotNull(budgets);
	}



}
