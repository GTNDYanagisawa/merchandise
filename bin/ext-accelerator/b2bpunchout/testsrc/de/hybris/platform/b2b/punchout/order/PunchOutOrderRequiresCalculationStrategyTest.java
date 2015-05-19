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
package de.hybris.platform.b2b.punchout.order;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test cases for {@link PunchOutOrderRequiresCalculationStrategy}.
 */
@UnitTest
@RunWith(Parameterized.class)
public class PunchOutOrderRequiresCalculationStrategyTest
{
	@Mock
	private OrderRequiresCalculationStrategy defaultStrategy;
	@InjectMocks
	private final PunchOutOrderRequiresCalculationStrategy punchOutOrderRequiresCalculationStrategy = new PunchOutOrderRequiresCalculationStrategy();

	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private AbstractOrderModel orderModel;

	private final boolean expectedResult;

	/**
	 * The data is consisted of <br/>
	 * 1) whether the order is a punch out order <br/>
	 * 2) whether the default injected strategy returns true or false<br/>
	 * 3) the expected result
	 */
	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][]
		{
		{ Boolean.TRUE, Boolean.TRUE, Boolean.FALSE },
		{ Boolean.FALSE, Boolean.TRUE, Boolean.FALSE },
		{ null, Boolean.TRUE, Boolean.FALSE },
		{ Boolean.TRUE, Boolean.FALSE, Boolean.FALSE },
		{ Boolean.FALSE, Boolean.FALSE, Boolean.FALSE },
		{ null, Boolean.FALSE, Boolean.FALSE } });
	}

	public PunchOutOrderRequiresCalculationStrategyTest(final Boolean punchOutOrder, final boolean defaultStrategyResult,
			final boolean expectedResult)
	{
		this.expectedResult = expectedResult;

		MockitoAnnotations.initMocks(this);

		when(defaultStrategy.requiresCalculation(orderEntryModel)).thenReturn(defaultStrategyResult);
		when(orderEntryModel.getOrder()).thenReturn(orderModel);
		when(orderModel.getPunchOutOrder()).thenReturn(punchOutOrder);
	}

	@Test
	public void testRequiresCalculationWhenNotPunchOutOrderAndNot() throws Exception
	{
		assertEquals(expectedResult, punchOutOrderRequiresCalculationStrategy.requiresCalculation(orderEntryModel));
	}

	@Test
	public void testRequiresCalculationAbstractOrderModel() throws Exception
	{
		assertEquals(expectedResult, punchOutOrderRequiresCalculationStrategy.requiresCalculation(orderModel));
	}

}
