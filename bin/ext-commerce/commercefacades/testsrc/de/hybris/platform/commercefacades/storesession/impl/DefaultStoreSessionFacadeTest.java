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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.storesession.impl.DefaultStoreSessionService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link StoreSessionFacade}
 * 
 */
@UnitTest
public class DefaultStoreSessionFacadeTest
{

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CartService cartService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private SessionService sessionService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private AbstractPopulatingConverter<CurrencyModel, CurrencyData> currencyConverter;
	@Mock
	private AbstractPopulatingConverter<LanguageModel, LanguageData> languageConverter;
	@Mock
	private DefaultStoreSessionService storeSessionService;

	private DefaultStoreSessionFacade defaultStoreSessionFacade;

	private LanguageModel languageModel;
	private LanguageModel languageModel2;

	private CurrencyModel currencyModel;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultStoreSessionFacade = new DefaultStoreSessionFacade();
		defaultStoreSessionFacade.setCommerceCommonI18NService(commerceCommonI18NService);
		defaultStoreSessionFacade.setCommonI18NService(commonI18NService);
		defaultStoreSessionFacade.setBaseStoreService(baseStoreService);
		defaultStoreSessionFacade.setCartService(cartService);
		defaultStoreSessionFacade.setCommerceCartService(commerceCartService);
		defaultStoreSessionFacade.setCurrencyConverter(currencyConverter);
		defaultStoreSessionFacade.setSessionService(sessionService);
		defaultStoreSessionFacade.setStoreSessionService(storeSessionService);

		languageModel = new LanguageModel();
		languageModel.setName("EN", new Locale("EN"));
		languageModel.setIsocode("EN");
		languageModel.setActive(Boolean.TRUE);

		languageModel2 = new LanguageModel();
		languageModel2.setName("DE", new Locale("DE"));
		languageModel2.setIsocode("DE");
		languageModel2.setActive(Boolean.TRUE);

		final LanguageData languageData = new LanguageData();
		languageData.setName("EN");
		languageData.setIsocode("EN");
		languageData.setActive(true);

		given(languageConverter.convert(languageModel)).willReturn(languageData);
		defaultStoreSessionFacade.setLanguageConverter(languageConverter);

		currencyModel = new CurrencyModel();
		currencyModel.setName("PL", new Locale("PL"));
		currencyModel.setIsocode("PL");
		currencyModel.setActive(Boolean.TRUE);
		currencyModel.setSymbol("PL");

		final CurrencyData currencyData = new CurrencyData();
		currencyData.setName("PL");
		currencyData.setIsocode("PL");
		currencyData.setActive(true);
		currencyData.setSymbol("PL");

		given(currencyConverter.convert(currencyModel)).willReturn(currencyData);
	}

	@Test
	public void testGetCurrentLanguage()
	{
		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(languageModel);
		final LanguageData language = defaultStoreSessionFacade.getCurrentLanguage();
		Assert.assertEquals(languageModel.getIsocode(), language.getIsocode());
	}

	@Test
	public void testGetCurrentCurrency()
	{
		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currencyModel);
		final CurrencyData currency = defaultStoreSessionFacade.getCurrentCurrency();
		Assert.assertEquals(currencyModel.getIsocode(), currency.getIsocode());
	}

	@Test
	public void testGetAllSitesLanguages()
	{
		final List<LanguageModel> languageslist = new ArrayList<LanguageModel>();
		languageslist.add(languageModel);
		languageslist.add(languageModel2);

		given(commerceCommonI18NService.getAllLanguages()).willReturn(languageslist);

		final Collection<LanguageData> languagesData = defaultStoreSessionFacade.getAllLanguages();
		Assert.assertEquals(2, languagesData.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAllLanguagesCatalogVerEmpty()
	{
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.EMPTY_LIST);

		final Collection<LanguageData> languagesData = defaultStoreSessionFacade.getAllLanguages();
		Assert.assertEquals(0, languagesData.size());
	}

	@Test
	public void testGetAllSitesCurrencies()
	{
		final List<CurrencyModel> currenciesList = new ArrayList<CurrencyModel>();
		currenciesList.add(currencyModel);
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(currenciesList);
		given(commonI18NService.getAllCurrencies()).willReturn(Collections.EMPTY_LIST);
		final Collection<CurrencyData> currencies = defaultStoreSessionFacade.getAllCurrencies();
		Assert.assertEquals(1, currencies.size());
	}

	@Test
	public void testGetAllSystemCurrencies()
	{
		final List<CurrencyModel> currenciesList = new ArrayList<CurrencyModel>();
		currenciesList.add(currencyModel);
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.EMPTY_LIST);
		given(commonI18NService.getAllCurrencies()).willReturn(currenciesList);
		final Collection<CurrencyData> currencies = defaultStoreSessionFacade.getAllCurrencies();
		Assert.assertEquals(1, currencies.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAllCurrenciesEmpty()
	{
		final List<CurrencyModel> currenciesList = new ArrayList<CurrencyModel>();
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(currenciesList);
		given(commonI18NService.getAllCurrencies()).willReturn(currenciesList);
		final Collection<CurrencyData> currencies = defaultStoreSessionFacade.getAllCurrencies();
		Assert.assertEquals(1, currencies.size());
	}

	@Test
	public void testInitializeSession() throws CalculationException
	{
		final Locale preferredLocale = Locale.ENGLISH;
		final BaseStoreModel currentBaseStore = Mockito.mock(BaseStoreModel.class);
		final UserTaxGroup taxGroup = Mockito.mock(UserTaxGroup.class);
		final CartModel cartModel = Mockito.mock(CartModel.class);

		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singletonList(languageModel));
		given(commerceCommonI18NService.getDefaultCurrency()).willReturn(currencyModel);
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.singletonList(currencyModel));
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);
		given(currentBaseStore.getTaxGroup()).willReturn(taxGroup);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);

		defaultStoreSessionFacade.initializeSession(Collections.singletonList(preferredLocale));

		Mockito.verify(sessionService).setAttribute(Europe1Constants.PARAMS.UTG, taxGroup);
		Mockito.verify(storeSessionService).setCurrentCurrency(currencyModel.getIsocode());
	}

	@Test
	public void testInitializeSessionNoDefaultValues()
	{
		final Locale preferredLocale = Locale.ENGLISH;
		final BaseStoreModel currentBaseStore = Mockito.mock(BaseStoreModel.class);
		final UserTaxGroup taxGroup = Mockito.mock(UserTaxGroup.class);

		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singletonList(languageModel));
		given(commerceCommonI18NService.getDefaultCurrency()).willReturn(null);
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.singletonList(currencyModel));
		given(baseStoreService.getCurrentBaseStore()).willReturn(currentBaseStore);
		given(currentBaseStore.getTaxGroup()).willReturn(taxGroup);

		defaultStoreSessionFacade.initializeSession(Collections.singletonList(preferredLocale));

		Mockito.verify(sessionService).setAttribute(Europe1Constants.PARAMS.UTG, taxGroup);
	}
}
