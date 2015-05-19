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
package de.hybris.platform.commercefacades.storesession.impl;

import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Store session facade implementation. The main purpose is to load currency and language data from existing services.
 */
public class DefaultStoreSessionFacade implements StoreSessionFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultStoreSessionFacade.class); //NOPMD

	private CommerceCommonI18NService commerceCommonI18NService;
	private CommonI18NService commonI18NService;
	private CartService cartService;
	private CommerceCartService commerceCartService;
	private SessionService sessionService;
	private BaseStoreService baseStoreService;
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	private Converter<LanguageModel, LanguageData> languageConverter;
	private StoreSessionService storeSessionService;

	@Override
	public void initializeSession(final List<Locale> preferredLocales)
	{
		initializeSessionTaxGroup();
		initializeSessionLanguage(preferredLocales);
		initializeSessionCurrency();
	}

	@Deprecated
	protected void removeSessionCart()
	{
		getCartService().removeSessionCart();
	}

	protected void initializeSessionTaxGroup()
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		if (currentBaseStore != null)
		{
			final UserTaxGroup taxGroup = currentBaseStore.getTaxGroup();
			if (taxGroup != null)
			{
				getSessionService().setAttribute(Europe1Constants.PARAMS.UTG, taxGroup);
			}
		}
	}

	protected void initializeSessionLanguage(final List<Locale> preferredLocales)
	{
		if (preferredLocales != null && !preferredLocales.isEmpty())
		{
			// Find the preferred locale that is in our set of supported languages
			final Collection<LanguageData> storeLanguages = getAllLanguages();
			if (storeLanguages != null && !storeLanguages.isEmpty())
			{
				final LanguageData bestLanguage = findBestLanguage(storeLanguages, preferredLocales);
				if (bestLanguage != null)
				{
					setCurrentLanguage(StringUtils.defaultString(bestLanguage.getIsocode()));
					return;
				}
			}
		}

		// Try to use the default language for the site
		final LanguageData defaultLanguage = getDefaultLanguage();
		if (defaultLanguage != null)
		{
			setCurrentLanguage(defaultLanguage.getIsocode());
		}
	}

	protected LanguageData findBestLanguage(final Collection<LanguageData> availableLanguages, final List<Locale> preferredLocales)
	{
		// Iterate in request locale order
		for (final Locale locale : preferredLocales)
		{
			final LanguageData bestLanguage = findMatchingLanguageByLocale(locale, availableLanguages);
			if (bestLanguage != null)
			{
				return bestLanguage;
			}
		}
		return null;
	}

	protected LanguageData findMatchingLanguageByLocale(final Locale locale, final Collection<LanguageData> availableLanguages)
	{
		return findMatchingLanguageByIsoCode(locale.getLanguage(), availableLanguages);
	}

	protected LanguageData findMatchingLanguageByIsoCode(final String languageIsoCode,
			final Collection<LanguageData> availableLanguages)
	{
		for (final LanguageData availableLanguage : availableLanguages)
		{
			if (availableLanguage.getIsocode().equalsIgnoreCase(languageIsoCode))
			{
				return availableLanguage;
			}
		}
		return null;
	}

	protected void initializeSessionCurrency()
	{
		final CurrencyData defaultCurrency = getDefaultCurrency();
		if (defaultCurrency != null)
		{
			setCurrentCurrency(defaultCurrency.getIsocode());
		}
		else
		{
			// No default currency, just use the first currency
			final Collection<CurrencyData> storeCurrencies = getAllCurrencies();
			if (storeCurrencies != null && !storeCurrencies.isEmpty())
			{
				setCurrentCurrency(storeCurrencies.iterator().next().getIsocode());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storesession.StoreSessionFacade#getCurrentLanguage()
	 */
	@Override
	public LanguageData getCurrentLanguage()
	{
		final LanguageModel languageModel = getCommonI18NService().getCurrentLanguage();
		return getLanguageConverter().convert(languageModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storesession.StoreSessionFacade#getDefaultLanguage()
	 */
	@Override
	public LanguageData getDefaultLanguage()
	{
		final LanguageModel languageModel = getCommerceCommonI18NService().getDefaultLanguage();
		if (languageModel != null)
		{
			return getLanguageConverter().convert(languageModel);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storesession.StoreSessionFacade#getCurrentCurrency()
	 */
	@Override
	public CurrencyData getCurrentCurrency()
	{
		final CurrencyModel currencyModel = getCommonI18NService().getCurrentCurrency();
		return getCurrencyConverter().convert(currencyModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storesession.StoreSessionFacade#getDefaultCurrency()
	 */
	@Override
	public CurrencyData getDefaultCurrency()
	{
		final CurrencyModel currencyModel = getCommerceCommonI18NService().getDefaultCurrency();
		if (currencyModel != null)
		{
			return getCurrencyConverter().convert(currencyModel);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storesession.StoreSessionFacade#getAllLanguages()
	 */
	@Override
	public Collection<LanguageData> getAllLanguages()
	{
		Collection<LanguageModel> languages = getCommerceCommonI18NService().getAllLanguages();
		if (languages.isEmpty())
		{
			languages = getCommonI18NService().getAllLanguages();
		}
		Assert.notEmpty(languages, "No supported languages found for the current site.");

		return Converters.convertAll(languages, getLanguageConverter());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storesession.StoreSessionFacade#getAllCurrencies()
	 */
	@Override
	public Collection<CurrencyData> getAllCurrencies()
	{
		List<CurrencyModel> currencyModelList = getCommerceCommonI18NService().getAllCurrencies();
		if (currencyModelList.isEmpty())
		{
			currencyModelList = getCommonI18NService().getAllCurrencies();
		}
		Assert.notEmpty(currencyModelList, "No supported currencies found for the current site.");

		return Converters.convertAll(currencyModelList, getCurrencyConverter());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storesession.StoreSessionFacade#setLanguage(java.lang.String)
	 */
	@Override
	public void setCurrentLanguage(final String isocode)
	{
		getStoreSessionService().setCurrentLanguage(isocode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.commercefacades.storesession.StoreSessionFacade#setCurrency(java.lang.String)
	 */
	@Override
	public void setCurrentCurrency(final String isocode)
	{
		getStoreSessionService().setCurrentCurrency(isocode);
	}

	/**
	 * @return the commerceCommonI18NService
	 */
	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	/**
	 * @param commerceCommonI18NService
	 *           the commerceCommonI18NService to set
	 */
	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService service)
	{
		this.baseStoreService = service;
	}

	protected Converter<CurrencyModel, CurrencyData> getCurrencyConverter()
	{
		return currencyConverter;
	}

	@Required
	public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}

	protected Converter<LanguageModel, LanguageData> getLanguageConverter()
	{
		return languageConverter;
	}

	@Required
	public void setLanguageConverter(final Converter<LanguageModel, LanguageData> languageConverter)
	{
		this.languageConverter = languageConverter;
	}

	protected StoreSessionService getStoreSessionService()
	{
		return storeSessionService;
	}

	@Required
	public void setStoreSessionService(final StoreSessionService storeSessionService)
	{
		this.storeSessionService = storeSessionService;
	}
}
