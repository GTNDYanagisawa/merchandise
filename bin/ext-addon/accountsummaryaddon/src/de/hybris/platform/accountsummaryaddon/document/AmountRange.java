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
package de.hybris.platform.accountsummaryaddon.document;

import java.math.BigDecimal;

public class AmountRange implements Range
{

	private final BigDecimal minAmountRange;
	private final BigDecimal maxAmountRange;

	public AmountRange(final BigDecimal minAmountRange, final BigDecimal maxAmountRange)
	{
		this.minAmountRange = minAmountRange;
		this.maxAmountRange = maxAmountRange;
	}

	@Override
	public BigDecimal getMinBoundary()
	{
		return this.minAmountRange;
	}


	@Override
	public BigDecimal getMaxBoundary()
	{
		return maxAmountRange;
	}
}
