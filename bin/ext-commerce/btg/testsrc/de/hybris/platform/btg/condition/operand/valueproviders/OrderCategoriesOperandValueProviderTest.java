/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.btg.condition.operand.valueproviders;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.btg.enums.BTGConditionEvaluationScope;
import de.hybris.platform.btg.model.BTGCategoriesInOrdersOperandModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Preconditions;


/**
 *
 */
@IntegrationTest
public class OrderCategoriesOperandValueProviderTest extends ServicelayerBaseTest
{

	private static final int ENTRIES_HIGH = 1500;

	@Mock
	private FlexibleSearchService flexibleSearchService;

	private List<de.hybris.platform.core.model.order.OrderModel> orders;

	@Mock
	private UserModel user;

	@Mock
	private BTGConditionEvaluationScope evaluationScope;

	@Mock
	private BTGCategoriesInOrdersOperandModel operand;

	private OrderCategoriesOperandValueProvider provider = null;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		provider = new OrderCategoriesOperandValueProvider()
		{
			@Override
			protected java.util.List<de.hybris.platform.core.model.order.OrderModel> getUsersLastOrders(
					final de.hybris.platform.core.model.user.UserModel user, final Integer lastOrders)
			{
				return orders;
			}


			@Override
			protected de.hybris.platform.servicelayer.search.FlexibleSearchService getFlexibleSearchService()
			{
				return flexibleSearchService;
			}
		};
	}

	@Test
	public void testGetValueQueryForBelowLimitOrder()
	{
		testQueryForEntries(500);
	}

	/**
	 * 
	 */
	private void testQueryForEntries(final int entries)
	{
		orders = prepareOrderEntries(entries);

		final Map<String, ?> expectedOrders = Collections.singletonMap(OrderCategoriesOperandValueProvider.ORDER_QUERY_PARAM,
				orders);

		final FlexibleSearchQueryArgumentMatcher matcher = new FlexibleSearchQueryArgumentMatcher(
				"SELECT DISTINCT {rel:source} FROM {OrderEntry as oe join CategoryProductRelation as rel on {oe.product}={rel:target}}  WHERE {oe:order} in ( ?orders )",
				expectedOrders);


		Mockito.doReturn(new SearchResultImpl<CategoryModel>(Collections.EMPTY_LIST, 0, 0, 0)).when(flexibleSearchService)
				.search(Mockito.any(FlexibleSearchQuery.class));

		provider.getValue(operand, user, evaluationScope);

		BDDMockito.verify(flexibleSearchService).search(Mockito.argThat(matcher));
	}


	@Test
	public void testGetValueQueryForOverLimitOrder()
	{

		if (Config.isOracleUsed())
		{

			orders = prepareOrderEntries(ENTRIES_HIGH);

			final Map<String, Object> expectedOrders = new HashMap<String, Object>(2);
			expectedOrders.put(OrderCategoriesOperandValueProvider.ORDER_QUERY_PARAM + "0", orders.subList(0, 1000));//1st bunch
			expectedOrders.put(OrderCategoriesOperandValueProvider.ORDER_QUERY_PARAM + "1", orders.subList(1000, ENTRIES_HIGH));//2nd bunch

			final FlexibleSearchQueryArgumentMatcher matcher = new FlexibleSearchQueryArgumentMatcher(
					"SELECT DISTINCT {rel:source} FROM {OrderEntry as oe join CategoryProductRelation as rel on {oe.product}={rel:target}}  WHERE ( ( {oe:order} in ( ?orders0 ) )  OR  ( {oe:order} in ( ?orders1 ) ) )",
					expectedOrders);


			Mockito.doReturn(new SearchResultImpl<CategoryModel>(Collections.EMPTY_LIST, 0, 0, 0)).when(flexibleSearchService)
					.search(Mockito.any(FlexibleSearchQuery.class));

			provider.getValue(operand, user, evaluationScope);

			BDDMockito.verify(flexibleSearchService).search(Mockito.argThat(matcher));
		}
		else
		{
			testQueryForEntries(1500);
		}
	}


	class FlexibleSearchQueryArgumentMatcher extends ArgumentMatcher<FlexibleSearchQuery>
	{

		private final String expectedQuery;
		private final Map<String, ?> expactedParamateres;

		FlexibleSearchQueryArgumentMatcher(final String expectedQuery, final Map expactedParamateres)
		{
			Preconditions.checkNotNull(expactedParamateres);
			Preconditions.checkNotNull(expectedQuery);
			this.expactedParamateres = expactedParamateres;
			this.expectedQuery = expectedQuery;
		}


		@Override
		public boolean matches(final Object argument)
		{
			Assert.assertTrue(argument instanceof FlexibleSearchQuery);
			final FlexibleSearchQuery query = (FlexibleSearchQuery) argument;
			Assert.assertEquals("Queries should match", expectedQuery, query.getQuery());
			assertEqualsMap(expactedParamateres, query.getQueryParameters());
			return true;
		}


		private void assertEqualsMap(final Map<String, ?> expectedMap, final Map givenMap)
		{
			Assert.assertEquals(expectedMap.size(), givenMap.size());
			for (final String key : expectedMap.keySet())
			{
				Assert.assertTrue(givenMap.containsKey(key));
				Assert.assertEquals(((List) expectedMap.get(key)).size(), ((List) givenMap.get(key)).size());
				Assert.assertEquals(expectedMap.get(key), givenMap.get(key));
			}
			//Assert.assertEquals("Params should match", expactedParamateres, query.getQueryParameters());
		}

	}


	private List<OrderModel> prepareOrderEntries(final int size)
	{
		final List<de.hybris.platform.core.model.order.OrderModel> result = new ArrayList<OrderModel>(size);
		for (int i = 0; i < size; i++)
		{
			result.add(Mockito.mock(OrderModel.class));
		}
		return result;
	}

}
