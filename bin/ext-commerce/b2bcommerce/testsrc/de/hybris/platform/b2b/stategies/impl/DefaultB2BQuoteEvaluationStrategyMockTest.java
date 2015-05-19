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
package de.hybris.platform.b2b.stategies.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.b2b.mock.HybrisMokitoTest;
import de.hybris.platform.b2b.model.B2BQuoteLimitModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BCurrencyConversionService;
import de.hybris.platform.b2b.services.impl.DefaultB2BUnitService;
import de.hybris.platform.b2b.strategies.impl.DefaultB2BQuoteEvaluationStrategy;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DefaultB2BQuoteEvaluationStrategyMockTest extends HybrisMokitoTest
{

	public final static Logger LOG = Logger.getLogger(DefaultB2BQuoteEvaluationStrategyMockTest.class);

	private final DefaultB2BQuoteEvaluationStrategy defaultB2BQuoteEvaluationStrategy = new DefaultB2BQuoteEvaluationStrategy();

	@Mock
	private DefaultB2BCurrencyConversionService b2bCurrencyConversionService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private CurrencyModel currencyModel;


	@Before
	public void setup()
	{
		LOG.debug("SetUp");
		when(currencyModel.getConversion()).thenReturn(new Double(1)); // all conversion 1:1 
		when(commonI18NService.getCurrency(Mockito.anyString())).thenReturn(currencyModel);

		// Call the real convert method here
		when(
				b2bCurrencyConversionService.convertAmount(Mockito.anyDouble(), Mockito.any(CurrencyModel.class), Mockito
						.any(CurrencyModel.class))).thenCallRealMethod();

		defaultB2BQuoteEvaluationStrategy.setB2bCurrencyConversionService(b2bCurrencyConversionService);
		defaultB2BQuoteEvaluationStrategy.setCommonI18NService(commonI18NService);
		defaultB2BQuoteEvaluationStrategy.setQuoteLimit(new BigDecimal(1000));
		defaultB2BQuoteEvaluationStrategy.setQuoteLimitCurrency("USD");


	}

	@Test
	public void testShouldFindLimitAssignedImmediatelyToOrdersUnit()
	{
		// Create Quote Limit of 150
		final B2BQuoteLimitModel mockQuoteLimit = mock(B2BQuoteLimitModel.class);
		when(mockQuoteLimit.getAmount()).thenReturn(new BigDecimal(150));
		when(mockQuoteLimit.getCurrency()).thenReturn(currencyModel);

		// Set Quote Limit to Unit
		final B2BUnitModel unit = mock(B2BUnitModel.class);
		when(unit.getQuoteLimit()).thenReturn(mockQuoteLimit);

		// Set Unit to Order
		final AbstractOrderModel mockOrder = mock(AbstractOrderModel.class);
		when(mockOrder.getUnit()).thenReturn(unit);

		// Set the Order total equal to quote limit
		when(mockOrder.getTotalPrice()).thenReturn(new Double(150.0));
		when(mockOrder.getCurrency()).thenReturn(currencyModel);

		boolean quoteAllowed = defaultB2BQuoteEvaluationStrategy.isQuoteAllowed(mockOrder);
		Assert.assertTrue(quoteAllowed);
		// Subtract a buck

		when(mockOrder.getTotalPrice()).thenReturn(new Double(149.99));
		quoteAllowed = defaultB2BQuoteEvaluationStrategy.isQuoteAllowed(mockOrder);
		Assert.assertFalse(quoteAllowed);
	}


	@Test
	public void testShouldFindLimitAssignedToParentOfOrdersUnit()
	{
		// Create Quote Limit of 150
		final B2BQuoteLimitModel mockQuoteLimit = mock(B2BQuoteLimitModel.class);
		when(mockQuoteLimit.getAmount()).thenReturn(new BigDecimal(150));
		when(mockQuoteLimit.getCurrency()).thenReturn(currencyModel);

		// Create GrandParentUnit and assign Quote Limit 
		final B2BUnitModel grandParentUnit = mock(B2BUnitModel.class);
		when(grandParentUnit.getQuoteLimit()).thenReturn(mockQuoteLimit);

		// Mock the b2bUnitService to always return the "GrandParentUnit" as a unit's parent Unit
		final DefaultB2BUnitService b2bUnitService = mock(DefaultB2BUnitService.class);
		when(b2bUnitService.getParent(Mockito.any(B2BUnitModel.class))).thenReturn(grandParentUnit);
		defaultB2BQuoteEvaluationStrategy.setB2bUnitService(b2bUnitService);

		// Parent has no QL
		final B2BUnitModel parentUnit = mock(B2BUnitModel.class);
		when(parentUnit.getQuoteLimit()).thenReturn(null);

		// Create Mock Order set it's parent
		final AbstractOrderModel mockOrder = mock(AbstractOrderModel.class);
		when(mockOrder.getUnit()).thenReturn(parentUnit);

		// Set the Order total equal to quote limit
		when(mockOrder.getTotalPrice()).thenReturn(new Double(150.0));
		when(mockOrder.getCurrency()).thenReturn(currencyModel);

		boolean quoteAllowed = defaultB2BQuoteEvaluationStrategy.isQuoteAllowed(mockOrder);
		Assert.assertTrue(quoteAllowed);

		// Subtract a buck thus no longer eligible
		when(mockOrder.getTotalPrice()).thenReturn(new Double(149.99));
		quoteAllowed = defaultB2BQuoteEvaluationStrategy.isQuoteAllowed(mockOrder);
		Assert.assertFalse(quoteAllowed);

		Assert.assertFalse(false);
	}

	@Test
	public void testShouldFindLimitAssignedToGrandParentOfOrdersUnit()
	{
		// Create Quote Limit of 150
		final B2BQuoteLimitModel mockQuoteLimit = mock(B2BQuoteLimitModel.class);
		when(mockQuoteLimit.getAmount()).thenReturn(new BigDecimal(150));
		when(mockQuoteLimit.getCurrency()).thenReturn(currencyModel);


		// Create GreatGrandParentUnit and assign Quote Limit 
		final B2BUnitModel greatGrandParentUnit = mock(B2BUnitModel.class);
		when(greatGrandParentUnit.getQuoteLimit()).thenReturn(mockQuoteLimit);

		// Create GrandParentUnit and with no Quote Limit 
		final B2BUnitModel grandParentUnit = mock(B2BUnitModel.class);
		when(grandParentUnit.getQuoteLimit()).thenReturn(null);


		// Mock the b2bUnitService to first return grandParent then greatGrandparent
		final DefaultB2BUnitService b2bUnitService = mock(DefaultB2BUnitService.class);
		when(b2bUnitService.getParent(Mockito.any(B2BUnitModel.class))).thenReturn(grandParentUnit, greatGrandParentUnit);
		defaultB2BQuoteEvaluationStrategy.setB2bUnitService(b2bUnitService);

		// Parent has no QL
		final B2BUnitModel parentUnit = mock(B2BUnitModel.class);
		when(parentUnit.getQuoteLimit()).thenReturn(null);

		// Create Mock Order set it's parent
		final AbstractOrderModel mockOrder = mock(AbstractOrderModel.class);
		when(mockOrder.getUnit()).thenReturn(parentUnit);

		// Set the Order total equal to quote limit
		when(mockOrder.getTotalPrice()).thenReturn(new Double(150.0));
		when(mockOrder.getCurrency()).thenReturn(currencyModel);


		boolean quoteAllowed = defaultB2BQuoteEvaluationStrategy.isQuoteAllowed(mockOrder);
		Assert.assertTrue(quoteAllowed);

		// Subtract a buck thus no longer eligible
		when(mockOrder.getTotalPrice()).thenReturn(new Double(149.99));
		quoteAllowed = defaultB2BQuoteEvaluationStrategy.isQuoteAllowed(mockOrder);
		Assert.assertFalse(quoteAllowed);

		Assert.assertFalse(false);

	}

	@Test
	public void testShouldFindDefaultLimit()
	{
		LOG.debug("Find Default Limit");

		// Create GrandParentUnit wit not Quote limit 
		final B2BUnitModel grandParentUnit = mock(B2BUnitModel.class);
		when(grandParentUnit.getQuoteLimit()).thenReturn(null);

		// Mock the b2bUnitService to always return the "GrandParentUnit" as a unit's parent Unit
		final DefaultB2BUnitService b2bUnitService = mock(DefaultB2BUnitService.class);
		when(b2bUnitService.getParent(Mockito.any(B2BUnitModel.class))).thenReturn(grandParentUnit, (B2BUnitModel) null);
		defaultB2BQuoteEvaluationStrategy.setB2bUnitService(b2bUnitService);

		// Create unit with not quote
		final B2BUnitModel parentUnit = mock(B2BUnitModel.class);
		when(parentUnit.getQuoteLimit()).thenReturn(null);

		// Create Mock Order set it's parent
		final AbstractOrderModel mockOrder = mock(AbstractOrderModel.class);
		when(mockOrder.getUnit()).thenReturn(parentUnit);

		// Set the Order total equal to quote limit
		when(mockOrder.getTotalPrice()).thenReturn(new Double(1000.0));
		when(mockOrder.getCurrency()).thenReturn(currencyModel);

		boolean quoteAllowed = defaultB2BQuoteEvaluationStrategy.isQuoteAllowed(mockOrder);
		Assert.assertTrue(quoteAllowed);

		// Subtract a buck thus no longer eligible
		when(mockOrder.getTotalPrice()).thenReturn(new Double(999.9));
		quoteAllowed = defaultB2BQuoteEvaluationStrategy.isQuoteAllowed(mockOrder);
		Assert.assertFalse(quoteAllowed);

		Assert.assertFalse(false);


	}

}
