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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.dao.B2BBudgetDao;
import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.util.StandardDateRange;
import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultB2BBudgetServiceMockTest extends HybrisMokitoTest
{
	private final DefaultB2BBudgetService defaultB2BBudgetService = new DefaultB2BBudgetService();
	private final Set<B2BBudgetModel> budgetModelSet = mock(Set.class);
	private final List<B2BBudgetModel> budgetModelList = mock(List.class);
	private final B2BBudgetModel budgetModel = mock(B2BBudgetModel.class);
	private final CurrencyModel currency = mock(CurrencyModel.class);
	private final Iterator iterator = mock(Iterator.class);
	private final B2BBudgetDao b2bBudgetDao = mock(B2BBudgetDao.class);

	public static final Logger LOG = Logger.getLogger(DefaultB2BBudgetServiceMockTest.class);

	@Before
	public void setup() throws Exception
	{
		when(budgetModel.getCurrency()).thenReturn(currency);

		budgetModelList.add(budgetModel);
		when(budgetModelList.iterator()).thenReturn(iterator);

		budgetModelSet.add(budgetModel);
		when(budgetModelSet.iterator()).thenReturn(iterator);
		when(Boolean.valueOf(iterator.hasNext())).thenReturn(Boolean.TRUE, Boolean.FALSE);
		when(iterator.next()).thenReturn(budgetModel);
	}

	@Test
	public void testGetCurrentBudgets() throws Exception
	{
		final B2BCostCenterModel costCenter = mock(B2BCostCenterModel.class);
		costCenter.setBudgets(budgetModelSet);
		when(costCenter.getBudgets()).thenReturn(budgetModelSet);
		when(costCenter.getCurrency()).thenReturn(currency);

		when(budgetModel.getActive()).thenReturn(Boolean.TRUE);

		final StandardDateRange dateRange = mock(StandardDateRange.class);
		when(budgetModel.getDateRange()).thenReturn(dateRange);
		when(Boolean.valueOf(dateRange.encloses(any(Date.class)))).thenReturn(Boolean.TRUE);

		final Collection<B2BBudgetModel> budgetModelCollection = defaultB2BBudgetService.getCurrentBudgets(costCenter);
		Assert.assertTrue(budgetModelCollection.size() == 1 && budgetModelCollection.contains(budgetModel));
	}

	@Test
	public void testGetB2BBudgets() throws Exception
	{
		when(b2bBudgetDao.find()).thenReturn(budgetModelList);
		defaultB2BBudgetService.setB2bBudgetDao(b2bBudgetDao);

		final Set<B2BBudgetModel> budgetSet = defaultB2BBudgetService.getB2BBudgets();
		Assert.assertTrue(budgetSet.size() == 1);
		Assert.assertTrue(budgetSet.contains(budgetModel));
	}
}
