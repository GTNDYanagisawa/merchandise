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
package de.hybris.platform.b2b.mock;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.internal.i18n.LocalizationService;
import de.hybris.platform.servicelayer.internal.service.AbstractService;

import java.util.Locale;
import java.util.Set;

import org.junit.Ignore;


@Ignore
public class MockLocalizationService extends AbstractService implements LocalizationService
{
	@Override
	public Locale getLocaleByString(final String locale)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Locale[] getAllLocales(final Locale loc)
	{
		return new Locale[0]; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Locale[] getFallbackLocales(final Locale loc)
	{
		return new Locale[0]; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Locale getDataLocale(final Locale loc)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Locale getCurrentLocale()
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setCurrentLocale(final Locale loc)
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Locale getCurrentDataLocale()
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getDataLanguageIsoCode(final Locale locale)
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isLocalizationFallbackEnabled()
	{
		return false; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setLocalizationFallbackEnabled(final boolean enabled)
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Set<Locale> getSupportedDataLocales()
	{
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public PK getMatchingPkForDataLocale(final Locale locale)
	{
		return null;
	}
}
