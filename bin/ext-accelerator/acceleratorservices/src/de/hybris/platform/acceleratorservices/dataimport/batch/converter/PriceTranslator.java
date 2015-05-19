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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;

import org.apache.commons.lang.StringUtils;


/**
 * Translator for the price attribute to prevent importing prices < 0
 */
public class PriceTranslator extends AbstractValueTranslator
{

	@Override
	public Object importValue(final String valueExpr, final Item toItem) throws JaloInvalidParameterException
	{
		clearStatus();
		Double result = null;
		if (!StringUtils.isBlank(valueExpr))
		{
			try
			{
				result = Double.valueOf(valueExpr);
			}
			catch (final NumberFormatException exc)
			{
				setError();
			}
			if (result != null && result.doubleValue() < 0.0)
			{
				setError();
			}
		}
		return result;
	}

	@Override
	public String exportValue(final Object value) throws JaloInvalidParameterException
	{
		return value == null ? "" : value.toString();
	}
}
