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
package de.hybris.platform.stock.impl;

import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 * Unit tests for {@link DefaultStockLevelDao}
 */
public class DefaultStockLevelDaoTest
{

	/**
	 * Verifies Warehouse List is correctly filtered (remove duplicates and null elements) before query is fired.
	 * 
	 * @see "https://jira.hybris.com/browse/BCOM-239"
	 */
	@Test
	public void testShouldFilterOutDuplicateWarehouseInQuery()
	{
		//setup mocks
		final SearchResult<Object> searchResult = Mockito.mock(SearchResult.class);
		final FlexibleSearchService mockService = Mockito.mock(FlexibleSearchService.class);

		Mockito.when(searchResult.getResult()).thenReturn(new ArrayList<Object>());
		Mockito.when(mockService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);

		final DefaultStockLevelDao dao = new DefaultStockLevelDao();
		dao.setFlexibleSearchService(mockService);

		//prepare to capture actual query object to analize it's parameters later on.
		final ArgumentCaptor<FlexibleSearchQuery> argument = ArgumentCaptor.forClass(FlexibleSearchQuery.class);

		//given
		final WarehouseModel wh1 = Mockito.mock(WarehouseModel.class);
		final WarehouseModel wh2 = Mockito.mock(WarehouseModel.class);
		final WarehouseModel wh3 = Mockito.mock(WarehouseModel.class);

		final List<WarehouseModel> testList = new ArrayList<>();
		testList.add(wh1);
		testList.add(null);
		testList.add(wh2);
		testList.add(null);
		testList.add(wh1);
		testList.add(null);
		testList.add(wh3);
		testList.add(null);
		testList.add(wh2);

		//when
		dao.findStockLevels("ABC", testList);

		//then
		Mockito.verify(mockService).search(argument.capture());

		final FlexibleSearchQuery query = argument.getValue();
		final Map<String, Object> queryParams = query.getQueryParameters();

		Assert.assertTrue(queryParams.containsKey("WAREHOUSES_PARAM"));
		final List<WarehouseModel> queryWarehouses = (List<WarehouseModel>) queryParams.get("WAREHOUSES_PARAM");
		Assert.assertEquals("Unexpected WAREHOUSES_PARAM query parameter size ", 3, queryWarehouses.size());
		Assert.assertFalse("null elements should be filtered out", queryWarehouses.contains(null));
		Assert.assertTrue("WAREHOUSES_PARAM should contain certain warehouse", queryWarehouses.contains(wh1));
		Assert.assertTrue("WAREHOUSES_PARAM should contain certain warehouse", queryWarehouses.contains(wh2));
		Assert.assertTrue("WAREHOUSES_PARAM should contain certain warehouse", queryWarehouses.contains(wh3));
	}
}
