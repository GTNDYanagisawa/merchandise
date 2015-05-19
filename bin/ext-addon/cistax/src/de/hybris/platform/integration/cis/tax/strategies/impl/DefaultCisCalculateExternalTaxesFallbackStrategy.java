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

import de.hybris.platform.commerceservices.externaltax.CalculateExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * A fallback strategy for calculating taxes in case external tax strategy has failed for any reason
 */

public class DefaultCisCalculateExternalTaxesFallbackStrategy implements CalculateExternalTaxesStrategy
{
	private ConfigurationService configurationService;
	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100D);

	@Override
	public ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		Assert.notNull(abstractOrder, "Order should not be null");

		final BigDecimal fixedPercentage = getConfigurationService().getConfiguration().getBigDecimal(
				"cistax.taxCalculation.fallback.fixedpercentage", BigDecimal.valueOf(0d));
		final String currencyCode = abstractOrder.getCurrency().getIsocode();
		final ExternalTaxDocument taxDocument = new ExternalTaxDocument();
		for (final AbstractOrderEntryModel orderEntryModel : abstractOrder.getEntries())
		{
			final BigDecimal taxAmount = BigDecimal.valueOf(orderEntryModel.getTotalPrice().doubleValue())
					.multiply(fixedPercentage.divide(HUNDRED)).setScale(2, RoundingMode.HALF_EVEN);
			taxDocument.setTaxesForOrderEntry(orderEntryModel.getEntryNumber().intValue(),
					new TaxValue("ENTRY DEFAULT", taxAmount.doubleValue(), true, taxAmount.doubleValue(), currencyCode));
		}
		if (abstractOrder.getDeliveryCost() != null)
		{
			final BigDecimal shippingTax = BigDecimal.valueOf(abstractOrder.getDeliveryCost().doubleValue())
					.multiply(fixedPercentage.divide(HUNDRED)).setScale(2, RoundingMode.HALF_EVEN);
			taxDocument.setShippingCostTaxes(new TaxValue("SHIPPING DEFAULT", shippingTax.doubleValue(), true, shippingTax
					.doubleValue(), currencyCode));
		}

		return taxDocument;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
