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
package de.hybris.platform.sap.sapordermgmtservices.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectException;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcommonbol.common.businessobject.interf.Converter;
import de.hybris.platform.sap.sapcommonbol.constants.SapcommonbolConstants;
import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.ConversionHelper;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtservices.schedline.data.ScheduleLineData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.util.localization.Localization;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;



/**
 * Populates ScheduleLineData from the BOL representation of schedule lines
 */
public class DefaultScheduleLinePopulator implements Populator<Schedline, ScheduleLineData>
{

	static final String SAPORDERMGMT_SCHEDULE_LINE = "sapordermgmt.scheduleLine";
	private static final Logger LOG = Logger.getLogger(DefaultAbstractOrderPopulator.class);
	private I18NService i18NService;
	private GenericFactory genericFactory;
	private Message message;


	@Override
	public void populate(final Schedline source, final ScheduleLineData target) throws ConversionException
	{
		final Date committedDate = source.getCommittedDate();
		target.setConfirmedDate(committedDate);
		target.setConfirmedQuantity(new Double(source.getCommittedQuantity().doubleValue()));

		try
		{
			final Converter converter = genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_CONVERTER);
			target.setQuantityUnit(converter.convertUnitKey2UnitID(source.getUnit()));
		}
		catch (final BusinessObjectException e)
		{
			throw new ApplicationBaseRuntimeException("Could not convert unit", e);
		}

		this.setFormatedScheduleLIne(target, source.getUnit());
		if (LOG.isDebugEnabled())
		{
			LOG.debug(("created new schedule line: " + target.getConfirmedQuantity() + ", " + target.getConfirmedDate()));
		}
	}


	private void setFormatedScheduleLIne(final ScheduleLineData target, final String unitKey)
	{
		final String[] arg = new String[]
		{ convertBigDecimalToString(new BigDecimal(target.getConfirmedQuantity()), unitKey), target.getQuantityUnit(),
				this.convertDateToLongDateString(target.getConfirmedDate()) };

		target.setFormattedValue(Localization.getLocalizedString(SAPORDERMGMT_SCHEDULE_LINE, arg));
	}

	/**
	 * @return the i18NService
	 */
	public I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param i18nService
	 *           the i18NService to set
	 */
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	/**
	 * Formats schedule line date according to the current session locale
	 * 
	 * @param date
	 * @param locale
	 *           Session locale
	 * @return Schedule line date in localized format, using {@link DateFormat#LONG}
	 */
	protected String getFormattedDate(final Date date, final Locale locale)
	{
		final DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.LONG, locale);
		return dateFormat.format(date);
	}

	/**
	 * 
	 * @return Factory to access SAP session beans
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * Sets generic factory (to access SAP session beans) via spring injection
	 * 
	 * @param genericFactory
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/**
	 * Converts a date to a localized string
	 * 
	 * @param date
	 * @return Date string, localized according the session locale
	 */
	protected String convertDateToLongDateString(final Date date)
	{
		return ConversionHelper.convertDateToLocalizedString(date);
	}

	/**
	 * Converts a double to a localized string, according to the session locale
	 * 
	 * @param input
	 *           conversion input
	 * @return String, localized according to the session locale
	 */
	protected String convertDoubleToString(final Double input)
	{
		return ConversionHelper.convertBigDecimalToString(new BigDecimal(input.doubleValue()));
	}



	/**
	 * Converts a double to a localized string, according to the session locale. In case scale is zero, no decimal
	 * separator is displayed. Deprecated, not used any longer. Use convertBigDecimalToString instead
	 * 
	 * @param d
	 *           input
	 * @return Localized string, according to the session locale
	 * @deprecated
	 */
	@Deprecated
	public String formatDouble(final double d)
	{
		if (Double.isInfinite(d))
		{
			throw new ApplicationBaseRuntimeException("Infinite value not allowed");
		}

		int precision = 2;

		if ((d == Math.floor(d)))
		{
			precision = 0;
		}
		else if (d * 10 == Math.floor(d * 10))
		{
			precision = 1;
		}

		final BigDecimal inputBD = (new BigDecimal(d)).setScale(precision, RoundingMode.HALF_EVEN);

		return ConversionHelper.convertBigDecimalToString(inputBD);
	}


	/**
	 * Do not use this method
	 * 
	 * @return BOL message
	 * @deprecated
	 */
	@Deprecated
	public Message getMessage()
	{
		return this.message;
	}


	/**
	 * Do not use this method
	 * 
	 * @param message
	 *           BOL message
	 * @deprecated
	 */
	@Deprecated
	public void setMessage(final Message message)
	{
		this.message = message;
	}


	/**
	 * Converts a big decimal into localized format, taking the unit customizing into account. If there are no decimal
	 * places however, we donn't display a decimal separator
	 * 
	 * @param quantity
	 * @param unit
	 * @return Localized string (considering unit customizing)
	 */
	protected String convertBigDecimalToString(final BigDecimal quantity, final String unit)
	{
		final Converter converter = genericFactory.getBean(SapcommonbolConstants.ALIAS_BO_CONVERTER);
		try
		{
			int scale = 0;
			if (quantity.compareTo(new BigDecimal(quantity.intValue())) != 0)
			{
				scale = converter.getUnitScale(unit);
			}

			final BigDecimal withScale = quantity.setScale(scale, RoundingMode.HALF_EVEN);

			return ConversionHelper.convertBigDecimalToString(withScale);
		}
		catch (final BusinessObjectException e)
		{
			throw new ApplicationBaseRuntimeException("Could not fetch unit customizing", e);
		}
	}

}
