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
package de.hybris.platform.order.strategies.calculation;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;


/**
 * Strategy focused on finding {@link TaxValue}s for the given order entry.
 */
public interface FindTaxValuesStrategy
{
	/**
	 * Resolves tax value for the given {@link AbstractOrderEntryModel} basing on the underlying implementation.
	 * 
	 * @param entry
	 *           {@link AbstractOrderEntryModel}
	 * @return collection of {@link TaxValue}s
	 */
	Collection<TaxValue> findTaxValues(AbstractOrderEntryModel entry) throws CalculationException;
}
