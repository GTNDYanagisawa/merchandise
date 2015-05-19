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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.List;
import java.util.Locale;


/**
 * Dummy I18N service for unit tests
 * 
 */
public class DummyI18NService implements CommonI18NService
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#convertAndRoundCurrency(double, double, int, double)
	 */
	@Override
	public double convertAndRoundCurrency(final double arg0, final double arg1, final int arg2, final double arg3)
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#convertCurrency(double, double, double)
	 */
	@Override
	public double convertCurrency(final double arg0, final double arg1, final double arg2)
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getAllCountries()
	 */
	@Override
	public List<CountryModel> getAllCountries()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getAllCurrencies()
	 */
	@Override
	public List<CurrencyModel> getAllCurrencies()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getAllLanguages()
	 */
	@Override
	public List<LanguageModel> getAllLanguages()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getAllRegions()
	 */
	@Override
	public List<RegionModel> getAllRegions()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getBaseCurrency()
	 */
	@Override
	public CurrencyModel getBaseCurrency()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getCountry(java.lang.String)
	 */
	@Override
	public CountryModel getCountry(final String arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getCurrency(java.lang.String)
	 */
	@Override
	public CurrencyModel getCurrency(final String arg0)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getCurrentCurrency()
	 */
	@Override
	public CurrencyModel getCurrentCurrency()
	{
		final CurrencyModel currency = new CurrencyModel();
		currency.setSapCode("USD");
		return currency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getCurrentLanguage()
	 */
	@Override
	public LanguageModel getCurrentLanguage()
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#getLanguage(java.lang.String)
	 */
	@Override
	public LanguageModel getLanguage(final String arg0)
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.servicelayer.i18n.CommonI18NService#getLocaleForLanguage(de.hybris.platform.core.model.c2l.
	 * LanguageModel)
	 */
	@Override
	public Locale getLocaleForLanguage(final LanguageModel arg0)
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.servicelayer.i18n.CommonI18NService#getRegion(de.hybris.platform.core.model.c2l.CountryModel,
	 * java.lang.String)
	 */
	@Override
	public RegionModel getRegion(final CountryModel arg0, final String arg1)
	{

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#roundCurrency(double, int)
	 */
	@Override
	public double roundCurrency(final double arg0, final int arg1)
	{

		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#setCurrentCurrency(de.hybris.platform.core.model.c2l.
	 * CurrencyModel)
	 */
	@Override
	public void setCurrentCurrency(final CurrencyModel arg0)
	{

		//
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.servicelayer.i18n.CommonI18NService#setCurrentLanguage(de.hybris.platform.core.model.c2l.
	 * LanguageModel)
	 */
	@Override
	public void setCurrentLanguage(final LanguageModel arg0)
	{

		//
	}

}
