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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigDecimal;
import java.util.Collections;

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCisCalculateExternalTaxesFallbackStrategyTest
{

	private DefaultCisCalculateExternalTaxesFallbackStrategy defaultCisCalculateExternalTaxesFallbackStrategy;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private AbstractOrderModel abstractOrder;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		defaultCisCalculateExternalTaxesFallbackStrategy = new DefaultCisCalculateExternalTaxesFallbackStrategy();
		defaultCisCalculateExternalTaxesFallbackStrategy.setConfigurationService(configurationService);
		given(configuration.getBigDecimal("cistax.taxCalculation.fallback.fixedpercentage", BigDecimal.valueOf(0D))).willReturn(
				BigDecimal.valueOf(9.70d));
		given(configurationService.getConfiguration()).willReturn(configuration);

		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final AbstractOrderEntryModel entry = mock(AbstractOrderEntryModel.class);
		given(currencyModel.getIsocode()).willReturn("USD");
		given(abstractOrder.getCurrency()).willReturn(currencyModel);
		given(abstractOrder.getEntries()).willReturn(Collections.singletonList(entry));
		given(entry.getTotalPrice()).willReturn(Double.valueOf(100D));
		given(abstractOrder.getDeliveryCost()).willReturn(Double.valueOf(9.9D));
	}

	@Test
	public void shouldCalculateTaxesWithFallback()
	{

		final ExternalTaxDocument taxDocument = defaultCisCalculateExternalTaxesFallbackStrategy
				.calculateExternalTaxes(abstractOrder);
		Assert.assertNotNull(taxDocument);
		Assert.assertTrue(taxDocument.getAllTaxes().size() > 0);
		Assert.assertTrue(taxDocument.getShippingCostTaxes().size() > 0);

	}

	@Test
	public void shouldCalculateTaxesWithFallbackForOrderWithZeroDeliveryCost()
	{
		given(abstractOrder.getDeliveryCost()).willReturn(null);
		final ExternalTaxDocument taxDocument = defaultCisCalculateExternalTaxesFallbackStrategy
				.calculateExternalTaxes(abstractOrder);
		Assert.assertNotNull(taxDocument);
		Assert.assertTrue(taxDocument.getAllTaxes().size() > 0);
		Assert.assertTrue(taxDocument.getShippingCostTaxes().size() == 0);
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptIllegalArguments()
	{
		defaultCisCalculateExternalTaxesFallbackStrategy.calculateExternalTaxes(null);
	}

}
