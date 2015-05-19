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
package de.hybris.platform.sap.core.module.impl;

import de.hybris.platform.sap.core.module.ModuleResourceAccess;
import de.hybris.platform.servicelayer.i18n.L10NService;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


/**
 * Class to which implements interface {@link ModuleResourceAccess}.
 */
public class ModuleResourceAccessImpl implements ModuleResourceAccess
{

	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(ModuleResourceAccessImpl.class.getName());

	/**
	 * l10n service.
	 */
	private L10NService l10nService;

	/**
	 * Injection setter for l10n service.
	 * 
	 * @param l10nService
	 *           the l10nService to set
	 */
	public void setL10nService(final L10NService l10nService)
	{
		this.l10nService = l10nService;
	}

	@Override
	public String getString(final String resourceKey)
	{
		return getString(resourceKey, null, null);
	}

	@Override
	public String getString(final String resourceKey, final Locale locale)
	{
		return getString(resourceKey, locale, null);
	}

	@Override
	public String getString(final String resourceKey, final Object[] args)
	{
		return getString(resourceKey, null, args);
	}

	@Override
	public String getString(final String resourceKey, final Locale locale, final Object[] args)
	{
		final ResourceBundle resourceBundle = (locale == null ? l10nService.getResourceBundle() : l10nService
				.getResourceBundle(new Locale[]
				{ locale }));
		String returnValue = null;
		try
		{
			final String formatString = resourceBundle.getString(resourceKey);
			final MessageFormat messageFormat = new MessageFormat(formatString, locale);
			returnValue = messageFormat.format(args, new StringBuffer(), null).toString();
		}
		catch (final IllegalArgumentException | MissingResourceException exception)
		{
			returnValue = null;
		}
		if (returnValue == null || returnValue.equals(resourceKey))
		{
			returnValue = "text for key not found: [" + resourceKey + "]";
			log.warn(returnValue);
		}
		return returnValue;
	}
}
