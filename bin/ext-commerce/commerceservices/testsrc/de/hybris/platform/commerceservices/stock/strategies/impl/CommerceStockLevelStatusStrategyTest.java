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
package de.hybris.platform.commerceservices.stock.strategies.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.ordersplitting.model.StockLevelModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;




@UnitTest
public class CommerceStockLevelStatusStrategyTest
{

	private final CommerceStockLevelStatusStrategy stockLevelStrategy = new CommerceStockLevelStatusStrategy();
	private CommerceAvailabilityCalculationStrategy commerceStockLevelCalculationStrategy;

	@Before
	public void setUp()
	{
		commerceStockLevelCalculationStrategy = mock(CommerceAvailabilityCalculationStrategy.class);
		stockLevelStrategy.setCommerceStockLevelCalculationStrategy(commerceStockLevelCalculationStrategy);
	}

	@Test
	public void shouldReturnInstockstatusWhenStockLevelIsForceinstockstatus()
	{
		final StockLevelModel stockLevel = stockLevel(InStockStatus.FORCEINSTOCK);

		final StockLevelStatus result = stockLevelStrategy.checkStatus(stockLevel);

		assertThat(result).isEqualTo(StockLevelStatus.INSTOCK);
	}

	@Test
	public void shouldReturnOutOfStockWhenStockLevelIsForceoutofstock()
	{
		final StockLevelModel stockLevel = stockLevel(InStockStatus.FORCEOUTOFSTOCK);

		final StockLevelStatus result = stockLevelStrategy.checkStatus(stockLevel);

		assertThat(result).isEqualTo(StockLevelStatus.OUTOFSTOCK);
	}

	@Test
	public void shouldReturnOutOfStockWhenAvailabilityIsLessThan0()
	{
		final StockLevelModel stockLevel = stockLevel(InStockStatus.NOTSPECIFIED);
		setAvailability(-1L);

		final StockLevelStatus result = stockLevelStrategy.checkStatus(stockLevel);

		assertThat(result).isEqualTo(StockLevelStatus.OUTOFSTOCK);
	}

	@Test
	public void shouldReturnOutOfStockWhenAvailabilityIsEqual0()
	{
		final StockLevelModel stockLevel = stockLevel(InStockStatus.NOTSPECIFIED);
		setAvailability(0L);

		final StockLevelStatus result = stockLevelStrategy.checkStatus(stockLevel);

		assertThat(result).isEqualTo(StockLevelStatus.OUTOFSTOCK);
	}

	@Test
	public void shouldReturnInstockWhenAvailabilityIsBiggerThanDefaultLowStockThreshold()
	{
		final StockLevelModel stockLevel = stockLevel(InStockStatus.NOTSPECIFIED);
		setAvailability(10L);

		final StockLevelStatus result = stockLevelStrategy.checkStatus(stockLevel);

		assertThat(result).isEqualTo(StockLevelStatus.INSTOCK);
	}

	@Test
	public void shouldReturnLowstockWhenAvailabilityIsBiggerThan0AndLessOrEqualDefaultLowStockThreshold()
	{
		final StockLevelModel stockLevel = stockLevel(InStockStatus.NOTSPECIFIED);
		setAvailability(4L);

		final StockLevelStatus result = stockLevelStrategy.checkStatus(stockLevel);

		assertThat(result).isEqualTo(StockLevelStatus.LOWSTOCK);
	}

	private StockLevelModel stockLevel(final InStockStatus inStockStatus)
	{
		final StockLevelModel stockLevel = mock(StockLevelModel.class);
		when(stockLevel.getInStockStatus()).thenReturn(inStockStatus);
		return stockLevel;
	}

	private void setAvailability(final long availability)
	{
		when(commerceStockLevelCalculationStrategy.calculateAvailability(Mockito.anyCollection())).thenReturn(
				Long.valueOf(availability));
	}
}
