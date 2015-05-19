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
package de.hybris.platform.commercefacades.storelocator.converters.populator;

import de.hybris.platform.commercefacades.storelocator.data.TimeData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * Converter used to populate a {@link de.hybris.platform.commercefacades.storelocator.data.TimeData} for given Date respecting current locale to evaluate a
 * {@link de.hybris.platform.commercefacades.storelocator.data.TimeData#setFormattedHour(String)} respectively.
 */
public class TimeDataPopulator implements Populator<Date, TimeData>
{

	private CommerceCommonI18NService commerceCommonI18NService;

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	protected Locale getCurrentLocale()
	{
		final LanguageModel currentLanguage = commerceCommonI18NService.getDefaultLanguage();
		return commerceCommonI18NService.getLocaleForLanguage(currentLanguage);
	}

	@Override
	public void populate(final Date source, final TimeData target)
	{
		if (source != null)
		{
			final Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(source);
			target.setHour((byte) calendar.get(Calendar.HOUR));
			target.setMinute((byte) calendar.get(Calendar.MINUTE));
			target.setFormattedHour(getDateFormat().format(source));
		}
	}

	protected DateFormat getDateFormat()
	{
		return DateFormat.getTimeInstance(DateFormat.SHORT, getCurrentLocale());
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}
}
