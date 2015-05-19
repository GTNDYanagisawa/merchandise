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
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.externaltax.ExternalTaxDocument;
import de.hybris.platform.integration.cis.tax.service.CisTaxCalculationService;

import org.apache.log4j.Logger;


/**
 * Cis implementation of {@link CalculateExternalTaxesStrategy} that calls the CIS to receive the tax values. If the
 * AbstractOrder is a cart rather than an order it will quote the taxes, otherwise it will post the taxes.
 */
public class DefaultCisCalculateExternalTaxesStrategy implements CalculateExternalTaxesStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultCisCalculateExternalTaxesStrategy.class);//NOPMD

	private CisTaxCalculationService cisTaxCalculationService;

	@Override
	public ExternalTaxDocument calculateExternalTaxes(final AbstractOrderModel abstractOrder)
	{
		return cisTaxCalculationService.calculateExternalTaxes(abstractOrder);
	}

	public CisTaxCalculationService getCisTaxCalculationService()
	{
		return cisTaxCalculationService;
	}

	public void setCisTaxCalculationService(final CisTaxCalculationService cisTaxCalculationService)
	{
		this.cisTaxCalculationService = cisTaxCalculationService;
	}

}
