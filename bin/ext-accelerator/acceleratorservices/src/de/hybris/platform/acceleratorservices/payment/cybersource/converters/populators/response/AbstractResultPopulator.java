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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.i18n.FormatFactory;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractResultPopulator<SOURCE, TARGET> implements Populator<SOURCE, TARGET>
{
	private static final Logger LOG = Logger.getLogger(AbstractResultPopulator.class);

	private FormatFactory formatFactory;

	protected FormatFactory getFormatFactory()
	{
		return formatFactory;
	}

	@Required
	public void setFormatFactory(final FormatFactory formatFactory)
	{
		this.formatFactory = formatFactory;
	}

	/**
	 * Common method to convert a String to a BigDecimal using a formatFactory.
	 * 
	 * @param value
	 *           - the String value to be converted.
	 * @return - a BigDecimal object.
	 */
	protected BigDecimal getBigDecimalForString(final String value)
	{
		if (value != null && !value.isEmpty())
		{
			try
			{
				return BigDecimal.valueOf(getFormatFactory().createNumberFormat().parse(value).doubleValue());
			}
			catch (final Exception e)
			{
				LOG.debug("Error converting to BigDecimal of String value: " + value, e);
			}
		}

		return null;
	}

	/**
	 * Common method to convert a String to an Integer.
	 * 
	 * @param value
	 *           - the String value to be converted.
	 * @return - an Integer object.
	 */
	protected Integer getIntegerForString(final String value)
	{
		if (value != null && !value.isEmpty())
		{
			try
			{
				return Integer.valueOf(value);
			}
			catch (final Exception e)
			{
				LOG.debug("Error converting to Integer of String value: " + value, e);
			}
		}

		return null;
	}
}
