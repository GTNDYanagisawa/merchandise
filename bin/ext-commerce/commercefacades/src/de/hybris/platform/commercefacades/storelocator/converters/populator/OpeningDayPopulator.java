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

import de.hybris.platform.commercefacades.storelocator.data.OpeningDayData;
import de.hybris.platform.commercefacades.storelocator.data.TimeData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.OpeningDayModel;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Base converter for both of {@link de.hybris.platform.storelocator.model.WeekdayOpeningDayModel} , {@link de.hybris.platform.storelocator.model.SpecialOpeningDayModel} types.
 */
public abstract class OpeningDayPopulator<SOURCE, TARGET> implements Populator<SOURCE, TARGET>
{
	protected CommerceCommonI18NService commerceCommonI18NService;

	private Converter<Date, TimeData> timeDataConverter;

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	@Required
	public void setTimeDataConverter(final Converter<Date, TimeData> timeDataConverter)
	{
		this.timeDataConverter = timeDataConverter;
	}


	protected Locale getCurrentLocale()
	{
		final LanguageModel currentLanguage = commerceCommonI18NService.getDefaultLanguage();
		return commerceCommonI18NService.getLocaleForLanguage(currentLanguage);
	}

	protected void populateBase(final OpeningDayModel source, final OpeningDayData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setClosingTime(timeDataConverter.convert(source.getClosingTime()));
		target.setOpeningTime(timeDataConverter.convert(source.getOpeningTime()));
	}

	/**
	 * Assumption order is not a locale related ,removes an empty entries
	 * 
	 **/
	protected List<String> getWeekDaySymbols()
	{
		final List<String> notEmptyWeekDay = new ArrayList<String>();
		final DateFormatSymbols weekDaySymbols = new DateFormatSymbols(getCurrentLocale());
		for (final String anyWeekDay : weekDaySymbols.getShortWeekdays())
		{
			if (StringUtils.isNotBlank(anyWeekDay))
			{
				notEmptyWeekDay.add(anyWeekDay);
			}
		}

		return notEmptyWeekDay;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	protected Converter<Date, TimeData> getTimeDataConverter()
	{
		return timeDataConverter;
	}


}
