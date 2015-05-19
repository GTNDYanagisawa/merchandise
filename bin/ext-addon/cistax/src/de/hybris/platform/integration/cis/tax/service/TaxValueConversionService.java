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
package de.hybris.platform.integration.cis.tax.service;

import com.hybris.cis.api.tax.model.CisTaxLine;
import com.hybris.cis.api.tax.model.CisTaxValue;
import de.hybris.platform.util.TaxValue;
import java.util.List;


/**
 * Interface to create the taxValues from CisTaxLines.
 */
public interface TaxValueConversionService
{
	List<TaxValue> getShippingTaxes(final List<CisTaxLine> taxLines, final String currencyCode, final boolean shippingIncluded);

	List<TaxValue> getLineTaxValues(final List<CisTaxValue> taxLines, final String currencyCode);
}
