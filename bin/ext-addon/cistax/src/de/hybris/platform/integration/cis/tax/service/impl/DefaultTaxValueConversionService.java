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
package de.hybris.platform.integration.cis.tax.service.impl;

import com.hybris.cis.api.tax.model.CisTaxLine;
import com.hybris.cis.api.tax.model.CisTaxValue;
import de.hybris.platform.integration.cis.tax.service.TaxValueConversionService;
import de.hybris.platform.util.TaxValue;
import java.util.ArrayList;
import java.util.List;


/**
 * Default implementation of {@link TaxValueConversionService} which determines if shipping taxes need to be created and
 * creates taxValues from the cisTaxValues.
 */
public class DefaultTaxValueConversionService implements TaxValueConversionService
{

	@Override
	public List<TaxValue> getShippingTaxes(final List<CisTaxLine> taxLines, final String currencyCode,
			final boolean shippingIncluded)
	{
		List<TaxValue> shippingTaxes = null;
		if (shippingIncluded)
		{
			shippingTaxes = getLineTaxValues(taxLines.get(taxLines.size() - 1).getTaxValues(), currencyCode);
		}
		return shippingTaxes;
	}

	@Override
	public List<TaxValue> getLineTaxValues(final List<CisTaxValue> taxLines, final String currencyCode)
	{
		final List<TaxValue> taxValues = new ArrayList<TaxValue>();
		for (final CisTaxValue cisTaxValue : taxLines)
		{
			taxValues.add(new TaxValue(cisTaxValue.getLevel(), cisTaxValue.getValue().doubleValue(), true, cisTaxValue.getValue()
					.doubleValue(), currencyCode));
		}
		return taxValues;
	}
}
