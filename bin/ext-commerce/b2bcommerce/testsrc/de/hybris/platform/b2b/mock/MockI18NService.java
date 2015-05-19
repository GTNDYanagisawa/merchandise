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

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Ignore;

@Ignore
public class MockI18NService extends AbstractBusinessService implements I18NService {
	@Override public Locale getCurrentLocale() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public void setCurrentLocale(final Locale loc) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Set<Locale> getSupportedLocales() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Set<Currency> getSupportedJavaCurrencies() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public Set<Locale> getSupportedDataLocales() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public TimeZone getCurrentTimeZone() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public void setCurrentTimeZone(final TimeZone timezone) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Currency getCurrentJavaCurrency() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setCurrentJavaCurrency(final Currency currency) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public ResourceBundle getBundle(final String baseName) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public ResourceBundle getBundle(final String baseName,
			final Locale[] locales) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public ResourceBundle getBundle(final String baseName,
			final Locale[] locales,
			final ClassLoader loader) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public LanguageModel getLanguage(final String isocode) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public Set<LanguageModel> getAllLanguages() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public Set<LanguageModel> getAllActiveLanguages() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public CountryModel getCountry(final String isocode) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public Set<CountryModel> getAllCountries() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public CurrencyModel getCurrency(final String isoCode) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public Set<CurrencyModel> getAllCurrencies() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public CurrencyModel getBaseCurrency() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public CurrencyModel getCurrentCurrency() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public void setCurrentCurrency(final CurrencyModel curr) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public boolean isLocalizationFallbackEnabled() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public void setLocalizationFallbackEnabled(final boolean enabled) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Currency getBestMatchingJavaCurrency(final String isocode) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Locale getBestMatchingLocale(final Locale locale) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Locale[] getAllLocales(final Locale locale) {
		return new Locale[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Locale[] getFallbackLocales(final Locale locale) {
		return new Locale[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public String getEnumLocalizedName(final HybrisEnumValue enumValue) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public void setEnumLocalizedName(final HybrisEnumValue enumValue,
			final String name) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
