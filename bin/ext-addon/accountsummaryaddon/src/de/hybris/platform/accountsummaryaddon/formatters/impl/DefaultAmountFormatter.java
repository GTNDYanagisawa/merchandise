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
package de.hybris.platform.accountsummaryaddon.formatters.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import de.hybris.platform.accountsummaryaddon.formatters.AmountFormatter;
import de.hybris.platform.core.model.c2l.CurrencyModel;

public class DefaultAmountFormatter implements AmountFormatter
{
	@Override
	public String formatAmount( BigDecimal value, CurrencyModel currency, Locale locale )
	{
		final NumberFormat currencyFormat = createCurrencyFormat( locale, currency );
		return currencyFormat.format( value );
	}

	private static NumberFormat createCurrencyFormat( final Locale locale, final CurrencyModel currency )
	{
		final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance( locale );
		adjustDigits( (DecimalFormat) currencyFormat, currency );
		adjustSymbol( (DecimalFormat) currencyFormat, currency );
		return currencyFormat;
	}

	/**
	 * Adjusts {@link DecimalFormat}'s fraction digits according to given {@link CurrencyModel}.
	 */
	private static DecimalFormat adjustDigits( final DecimalFormat format, final CurrencyModel currencyModel )
	{
		final int tempDigits = currencyModel.getDigits() == null ? 0 : currencyModel.getDigits().intValue();
		final int digits = Math.max( 0, tempDigits );
		format.setMaximumFractionDigits( digits );
		format.setMinimumFractionDigits( digits );
		
		if( digits == 0 )
		{
			format.setDecimalSeparatorAlwaysShown( false );
		}
		return format;
	}

	/**
	 * Adjusts {@link DecimalFormat}'s symbol according to given {@link CurrencyModel}.
	 */
	private static DecimalFormat adjustSymbol( final DecimalFormat format, final CurrencyModel currencyModel )
	{
		final String symbol = currencyModel.getSymbol();
		if( symbol != null )
		{
			final DecimalFormatSymbols symbols = format.getDecimalFormatSymbols(); // does cloning
			final String iso = currencyModel.getIsocode();
			boolean changed = false;
			if( !iso.equalsIgnoreCase( symbols.getInternationalCurrencySymbol() ) )
			{
				symbols.setInternationalCurrencySymbol( iso );
				changed = true;
			}
			if( !symbol.equals( symbols.getCurrencySymbol() ) )
			{
				symbols.setCurrencySymbol( symbol );
				changed = true;
			}
			if( changed )
			{
				format.setDecimalFormatSymbols( symbols );
			}
		}
		return format;
	}
}
