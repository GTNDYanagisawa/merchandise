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
package de.hybris.platform.commercefacades.storesession;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


/**
 * Store session facade interface. The main purpose is to load currency and language data from existing services.
 * 
 */
public interface StoreSessionFacade
{
	/**
	 * Gets current language stored in session.
	 * 
	 * @return current language for the current store.
	 */
	LanguageData getCurrentLanguage();

	/**
	 * Gets current currency stored in session.
	 * 
	 * @return current currency for the current store.
	 */
	CurrencyData getCurrentCurrency();

	/**
	 * 
	 * @return all usable languages for the current catalog versions. In case of empty list of languages for versions,
	 *         list of all languages in the system should be returned.
	 */
	Collection<LanguageData> getAllLanguages();

	/**
	 * 
	 * @return all usable currencies for the current store. In case of empty list of currencies for stores, list of all
	 *         currencies in the system should be returned.
	 */
	Collection<CurrencyData> getAllCurrencies();

	/**
	 * Sets the current language and validates, if language chosen is supported for current store.
	 * 
	 * @param isocode
	 *           language iso
	 */
	void setCurrentLanguage(String isocode);

	/**
	 * Sets the current currency and validates, if currency chosen is supported for current currency.
	 * 
	 * @param isocode
	 *           currency iso
	 */
	void setCurrentCurrency(String isocode);

	/**
	 * Gets default language for current store.
	 * 
	 * @return default language for current store.
	 */
	LanguageData getDefaultLanguage();

	/**
	 * Gets default currency for current store.
	 * 
	 * @return default currency for current store.
	 */
	CurrencyData getDefaultCurrency();

	/**
	 * Initialize the session
	 */
	void initializeSession(List<Locale> preferredLocales);
}
