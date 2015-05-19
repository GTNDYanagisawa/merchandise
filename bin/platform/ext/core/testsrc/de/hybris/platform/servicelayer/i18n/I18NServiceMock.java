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
package de.hybris.platform.servicelayer.i18n;

import de.hybris.platform.servicelayer.i18n.impl.DefaultI18NService;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;



public class I18NServiceMock extends DefaultI18NService
{
	@Override
	public ResourceBundle getBundle(final String baseName)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ResourceBundle getBundle(final String baseName, final Locale[] locales)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public ResourceBundle getBundle(final String baseName, final Locale[] locales, final ClassLoader loader)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCurrentTimeZone(final TimeZone zone)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public TimeZone getCurrentTimeZone()
	{
		throw new UnsupportedOperationException();
	}




}