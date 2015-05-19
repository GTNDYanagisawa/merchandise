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
package de.hybris.platform.sap.core.common.util;

import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;


/**
 * Helper class to access the <code>Locale</code> attached with the executing thread.
 * 
 * @author SAP
 */
public final class LocaleUtil
{

	private static final ThreadLocal<Locale> locales = new ThreadLocal<Locale>()
	{
		@Override
		protected Locale initialValue()
		{
			return null;
		}
	};

	/**
	 * Get the current locale.<br>
	 * 
	 * @return locale the current locale
	 */
	public static Locale getLocale()
	{
		if (locales.get() == null)
		{
			return GenericFactoryProvider.getInstance().getBean(I18NService.class).getCurrentLocale();
		}
		else
		{
			return locales.get();
		}
	}

	/**
	 * Set the locale (for test purposes).<br>
	 * 
	 * @param locale
	 *           the default locale to be used in tests
	 */
	public static void setLocale(final Locale locale)
	{
		if (locale == null)
		{
			locales.remove();
		}
		else
		{
			locales.set(locale);
		}
	}
}
