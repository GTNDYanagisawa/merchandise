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

import de.hybris.platform.b2b.dao.B2BBudgetDao;
import de.hybris.platform.b2b.dao.B2BCostCenterDao;
import de.hybris.platform.b2b.dao.impl.BaseDao;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.TenantScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;

import de.hybris.bootstrap.annotations.IntegrationTest;
import junit.framework.Assert;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@IntegrationTest
public class DefaultB2BCostCenterServiceMockTest extends HybrisMokitoTest
{
	public static final Logger LOG = Logger.getLogger(DefaultB2BCostCenterServiceMockTest.class);
	DefaultB2BCostCenterService defaultB2BCostCenterService;
	@Mock
	public UserService mockUserService;
	@Mock
	public BaseDao mockBaseDao;
	@Mock
	public ModelService mockModelService;
	@Mock
	public B2BUnitService<B2BUnitModel, B2BCustomerModel> mockB2bUnitService;
	@Mock
	public SessionService mockSessionService;
	@Mock
	public TenantScope mockTenantScope;
	@Mock
	public PlatformTransactionManager mockTxManager;
	@Mock
	public RendererService rendererService;

	@Before
	public void setUp()
	{
		defaultB2BCostCenterService = new DefaultB2BCostCenterService();
	}

	@Test
	public void testFindCostCenters() throws Exception
	{
		final B2BCostCenterModel costCenterModel = mock(B2BCostCenterModel.class);
		final B2BCostCenterDao b2bCostCenterDao = mock(B2BCostCenterDao.class);
		when(b2bCostCenterDao.findByCode("cc1")).thenReturn(costCenterModel);
		when(costCenterModel.getCode()).thenReturn("cc1");
		when(mockBaseDao.findFirstByAttribute(B2BCostCenterModel.CODE, "cc1", B2BCostCenterModel.class))
			.thenReturn(costCenterModel);
		defaultB2BCostCenterService.setB2bCostCenterDao(b2bCostCenterDao);
		final B2BCostCenterModel retValCostCenter = defaultB2BCostCenterService.getCostCenterForCode("cc1");
		Assert.assertEquals(costCenterModel.getCode(), retValCostCenter.getCode());

	}

	@Test
	public void shouldFindB2BBudget()
	{
		final B2BBudgetModel budgetModel = mock(B2BBudgetModel.class);
		when(budgetModel.getCode()).thenReturn("b1");

		final B2BBudgetDao b2bBudgetDao = mock(B2BBudgetDao.class);
		when(b2bBudgetDao.findBudgetByCode("b1")).thenReturn(budgetModel);
		defaultB2BCostCenterService.setB2bBudgetDao(b2bBudgetDao);
		final B2BBudgetModel budgetByCode = defaultB2BCostCenterService.getB2BBudgetForCode("b1");
		Assert.assertEquals(budgetModel.getCode(), budgetByCode.getCode());
	}

	@Test
	public void shouldReturnASetOfB2BBudgets()
	{

		final List<B2BBudgetModel> budgets = new ArrayList<B2BBudgetModel>(3);
		final B2BBudgetModel budgetModel = mock(B2BBudgetModel.class);
		when(budgetModel.getCode()).thenReturn("b1");
		budgets.add(budgetModel);
		final B2BBudgetModel budgetModel2 = mock(B2BBudgetModel.class);
		when(budgetModel2.getCode()).thenReturn("b2");
		budgets.add(budgetModel2);
		budgets.add(budgetModel);
		final B2BBudgetDao b2bBudgetDao = mock(B2BBudgetDao.class);
		when(b2bBudgetDao.find()).thenReturn(budgets);


		defaultB2BCostCenterService.setB2bBudgetDao(b2bBudgetDao);
		final Set<B2BBudgetModel> budgetsFound = defaultB2BCostCenterService.getB2BBudgets();
		Assert.assertEquals(2, budgetsFound.size());

	}

	@Test
	public void testGetCostCentersForUnitBranch()
	{
		final B2BCustomerModel employee = mock(B2BCustomerModel.class);
		final CurrencyModel currency = mock(CurrencyModel.class);
		final B2BUnitModel parentUnit = mock(B2BUnitModel.class);

		defaultB2BCostCenterService.setB2bUnitService(mockB2bUnitService);
		when(mockB2bUnitService.getParent(employee)).thenReturn(parentUnit);

		final B2BCostCenterDao costCenterDao = mock(B2BCostCenterDao.class);
		defaultB2BCostCenterService.setB2bCostCenterDao(costCenterDao);

		final B2BCostCenterModel b2bCostCenterModel = mock(B2BCostCenterModel.class);
		final List<B2BCostCenterModel> b2bCostCenterModelList = Collections.singletonList(b2bCostCenterModel);

		when(costCenterDao.findActiveCostCentersByBranchAndCurrency(Mockito.anySet(), Mockito.eq(currency)))
			.thenReturn(b2bCostCenterModelList);

		final List<B2BCostCenterModel> results =
			defaultB2BCostCenterService.getCostCentersForUnitBranch(employee, currency);
		Assert.assertTrue(results.contains(b2bCostCenterModel));
	}

	@Test
	public void testGetAvailableCurrencies()
	{
		final B2BUnitModel unitArgument = mock(B2BUnitModel.class);

		final B2BUnitModel b2bUnitModel = mock(B2BUnitModel.class);
		final Iterator<B2BUnitModel> unitModelIterator = mock(Iterator.class);
		when(Boolean.valueOf(unitModelIterator.hasNext())).thenReturn(Boolean.TRUE, Boolean.FALSE);
		when(unitModelIterator.next()).thenReturn(b2bUnitModel);

		final Set<B2BUnitModel> branch = mock(HashSet.class);
		when(branch.iterator()).thenReturn(unitModelIterator);
		defaultB2BCostCenterService.setB2bUnitService(mockB2bUnitService);
		when(mockB2bUnitService.getBranch(unitArgument)).thenReturn(branch);

		final CurrencyModel currency = mock(CurrencyModel.class);
		final B2BCostCenterModel costCenter = mock(B2BCostCenterModel.class);
		when(costCenter.getCurrency()).thenReturn(currency);

		when(b2bUnitModel.getCostCenters()).thenReturn(Collections.singletonList(costCenter));

		final Set<CurrencyModel> currencies = defaultB2BCostCenterService.getAvailableCurrencies(unitArgument);
		Assert.assertTrue(currencies.size() == 1 && currencies.contains(currency));
	}

	@Test
	public void testGetCostCenters()
	{
		final B2BCostCenterModel costCenter = mock(B2BCostCenterModel.class);

		final AbstractOrderEntryModel abstractOrderEntry = mock(AbstractOrderEntryModel.class);
		when(abstractOrderEntry.getCostCenter()).thenReturn(costCenter);

		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(abstractOrderEntry);

		final Set<B2BCostCenterModel> costCenterModel = defaultB2BCostCenterService.getB2BCostCenters(entries);
		Assert.assertTrue(costCenterModel.size() == 1 && costCenterModel.contains(costCenter));
	}
}
