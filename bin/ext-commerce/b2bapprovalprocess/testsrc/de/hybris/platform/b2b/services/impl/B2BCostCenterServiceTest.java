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
import de.hybris.platform.b2b.dao.impl.DefaultB2BBudgetDao;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:/b2bapprovalprocess-spring-test.xml" })
public class B2BCostCenterServiceTest extends B2BIntegrationTransactionalTest
{
	private static final Logger LOG = Logger.getLogger(B2BCostCenterServiceTest.class);

	@Resource
	public DefaultB2BBudgetDao b2bBudgetDao;

	@Before
	public void before() throws Exception
	{
		B2BIntegrationTest.loadTestData();
		importCsv("/b2bapprovalprocess/test/b2borganizations.csv", "UTF-8");
		//importCsv("/b2bapprovalprocess/test/organizationdata.csv", "UTF-8");

		sessionService.getCurrentSession().setAttribute("user",
				this.modelService.<Object> toPersistenceLayer(userService.getAdminUser()));
		i18nService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));
	}

	@Test
	public void testFindCostCenters() throws Exception
	{
		final String userId = "IC CEO";
		final B2BCustomerModel customer = login(userId);
		final List<B2BCostCenterModel> costCenters = b2bCostCenterService.getCostCentersForUnitBranch(customer,
				commonI18NService.getCurrentCurrency());
		Assert.assertNotNull(costCenters);
		Assert.assertTrue(CollectionUtils.isNotEmpty(costCenters));
	}

	@Test
	public void testFindCostCentersAndBudgets() throws Exception
	{
		final String userId = "GC CEO";
		final B2BCustomerModel customer = login(userId);
		LOG.debug("Customer : " + customer.getUid());
		final B2BCostCenterModel costCenter = (B2BCostCenterModel) b2bCostCenterService.getCostCenterForCode("GC 2.4");

		Assert.assertNotNull(costCenter);
		final Set<B2BBudgetModel> budgetSet = costCenter.getBudgets();
		Assert.assertTrue(CollectionUtils.isNotEmpty(budgetSet));
	}

	/**
	 * This test is to verify if all {@link de.hybris.platform.b2b.model.B2BBudgetModel} can be fetched from
	 * {@link de.hybris.platform.b2b.model.B2BCostCenterModel} with disable search restriction for a user.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test1FindCostCentersAndBudgetsNotVisible() throws Exception
	{
		final String userId = "GC Sales Boss";
		final B2BCustomerModel customer = login(userId);
		LOG.debug("Customer : " + customer.getUid());
		final B2BCostCenterModel costCenter = (B2BCostCenterModel) b2bCostCenterService.getCostCenterForCode("GC 2.4");

		//Assert.assertTrue(CollectionUtils.isEmpty(costCenter.getBudgets()));

		final Set<B2BBudgetModel> budgetSet = sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				// we disable restrictions in order to be able to see budgets assigned
				// to units up the tree hierarchy
				searchRestrictionService.disableSearchRestrictions();
				//modelService.refresh(costCenter);
				return costCenter.getBudgets();
			}
		});
		// with restrictions enabled the budgets are not visible
		Assert.assertTrue(CollectionUtils.isNotEmpty(budgetSet));

	}


	/**
	 * This test is to verify if all {@link de.hybris.platform.b2b.model.B2BBudgetModel} can be fetched from
	 * {@link de.hybris.platform.b2b.model.B2BCostCenterModel} with disable search restriction for a user. But a call to
	 * fetch {@link de.hybris.platform.b2b.model.B2BCostCenterModel#getBudgets()} prior to disable search restriction is
	 * made here.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void test2FindCostCentersAndBudgetsNotVisible() throws Exception
	{
		final String userId = "GC Sales Boss";
		final B2BCustomerModel customer = login(userId);
		LOG.debug("Customer : " + customer.getUid());
		final B2BCostCenterModel costCenter = (B2BCostCenterModel) b2bCostCenterService.getCostCenterForCode("GC 2.4");

		Assert.assertTrue(CollectionUtils.isEmpty(costCenter.getBudgets()));

		final Set<B2BBudgetModel> budgetSet = sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				// we disable restrictions in order to be able to see budgets assigned
				// to units up the tree hierarchy
				searchRestrictionService.disableSearchRestrictions();
				//modelService.refresh(costCenter);
				return costCenter.getBudgets();
			}
		});
		// with restrictions enabled the budgets are not visible
		Assert.assertTrue(CollectionUtils.isNotEmpty(budgetSet));

	}


	/**
	 * This test is to verify if all {@link de.hybris.platform.b2b.model.B2BBudgetModel} can be fetched from
	 * {@link de.hybris.platform.b2b.model.B2BCostCenterModel} with disable search restriction for a user. A call to
	 * fetch {@link de.hybris.platform.b2b.model.B2BCostCenterModel#getBudgets()} prior to disable search restriction
	 * made here. But before fetching the budget the
	 * {@link de.hybris.platform.servicelayer.model.ModelService#refresh(Object)} has been called.
	 * 
	 * @throws Exception
	 */
	@Test
	public void test3FindCostCentersAndBudgetsNotVisible() throws Exception
	{
		final String userId = "GC Sales Boss";
		final B2BCustomerModel customer = login(userId);
		LOG.debug("Customer : " + customer.getUid());
		final B2BCostCenterModel costCenter = (B2BCostCenterModel) b2bCostCenterService.getCostCenterForCode("GC 2.4");

		Assert.assertTrue(CollectionUtils.isEmpty(costCenter.getBudgets()));

		final Set<B2BBudgetModel> budgetSet = sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				// we disable restrictions in order to be able to see budgets assigned
				// to units up the tree hierarchy
				searchRestrictionService.disableSearchRestrictions();
				modelService.refresh(costCenter);
				return costCenter.getBudgets();
			}
		});
		// with restrictions enabled the budgets are not visible
		Assert.assertTrue(CollectionUtils.isNotEmpty(budgetSet));

	}
}
