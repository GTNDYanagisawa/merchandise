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
package de.hybris.platform.acceleratorfacades.urlencoder.attributes;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.urlencoder.data.UrlEncoderData;
import de.hybris.platform.acceleratorfacades.urlencoder.impl.DefaultUrlEncoderFacade;
import de.hybris.platform.acceleratorservices.urlencoder.attributes.UrlEncodingAttributeManager;
import de.hybris.platform.acceleratorservices.urlencoder.attributes.impl.DefaultCurrencyAttributeManager;
import de.hybris.platform.acceleratorservices.urlencoder.attributes.impl.DefaultLanguageAttributeManager;
import de.hybris.platform.acceleratorservices.urlencoder.impl.DefaultUrlEncoderService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultUrlEncoderFacadeTest
{
	private DefaultUrlEncoderFacade urlEncoderFacade;

	@Mock
	private SessionService sessionService;

	@Mock
	private StoreSessionFacade storeSessionFacade;

	@Mock
	private CMSSiteService cmsSiteService; //NOPMD 

	@Mock
	private DefaultCurrencyAttributeManager currencyAttributeManager;

	@Mock
	private DefaultLanguageAttributeManager languageAttributeManager;

	@Mock
	private DefaultUrlEncoderService urlEncoderService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		urlEncoderFacade = new DefaultUrlEncoderFacade();

		final CMSSiteModel cmsSiteModel = new CMSSiteModel();
		cmsSiteModel.setUid("electronics");

		final List<String> urlEncodingAttrList = new ArrayList<java.lang.String>();
		urlEncodingAttrList.add("language");
		urlEncodingAttrList.add("currency");
		cmsSiteModel.setUrlEncodingAttributes(urlEncodingAttrList);

		final LanguageData languageDataData = new LanguageData();
		languageDataData.setIsocode("en");

		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode("USD");

		given(storeSessionFacade.getDefaultCurrency()).willReturn(currencyData);
		given(storeSessionFacade.getDefaultLanguage()).willReturn(languageDataData);


		final UrlEncoderData currencyUrlEncoderData = new UrlEncoderData();
		currencyUrlEncoderData.setAttributeName("currency");
		currencyUrlEncoderData.setDefaultValue("USD");
		currencyUrlEncoderData.setCurrentValue("USD");

		final UrlEncoderData languageUrlEncoderData = new UrlEncoderData();
		languageUrlEncoderData.setAttributeName("language");
		languageUrlEncoderData.setDefaultValue("en");
		languageUrlEncoderData.setCurrentValue("en");

		final List<UrlEncoderData> urlEncoderDataList = new ArrayList<UrlEncoderData>();
		urlEncoderDataList.add(currencyUrlEncoderData);
		urlEncoderDataList.add(languageUrlEncoderData);

		given(sessionService.getAttribute("urlEncodingData")).willReturn(urlEncoderDataList);

		urlEncoderFacade.setSessionService(sessionService);
		urlEncoderFacade.setUrlEncoderService(urlEncoderService);

		final List<String> availableCurrencyList = new ArrayList<String>();
		availableCurrencyList.add("USD");
		availableCurrencyList.add("JPY");
		given(currencyAttributeManager.getAllAvailableValues()).willReturn(availableCurrencyList);
		given(Boolean.valueOf(currencyAttributeManager.isValid("USD"))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(currencyAttributeManager.isValid("JPY"))).willReturn(Boolean.TRUE);

		final List<String> availableLanguageList = new ArrayList<String>();
		availableLanguageList.add("en");
		availableLanguageList.add("ja");
		given(languageAttributeManager.getAllAvailableValues()).willReturn(availableLanguageList);
		given(Boolean.valueOf(languageAttributeManager.isValid("en"))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(languageAttributeManager.isValid("ja"))).willReturn(Boolean.TRUE);

		final Map<String, UrlEncodingAttributeManager> attributeManagerMap = new HashMap<String, UrlEncodingAttributeManager>();
		attributeManagerMap.put("language", languageAttributeManager);
		attributeManagerMap.put("currency", currencyAttributeManager);

		given(urlEncoderService.getUrlEncodingAttrManagerMap()).willReturn(attributeManagerMap);

	}

	@Test
	public void testPatternForUrlEncodingForDefault()
	{
		Assert.assertEquals("USD/en", urlEncoderFacade.patternForUrlEncoding("/teststorefront/", "teststorefront", false)
				.getPattern());
	}


	@Test
	public void testPatternForUrlEncodingForAValidUrl()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("JPY");
		given(languageAttributeManager.getCurrentValue()).willReturn("ja");
		Assert.assertEquals("JPY/ja", urlEncoderFacade.patternForUrlEncoding("/teststorefront/JPY/ja/", "teststorefront", false)
				.getPattern());
	}


	@Test
	public void testPatternForUrlEncodingForInvalidAttr()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("USD");
		given(languageAttributeManager.getCurrentValue()).willReturn("en");
		Assert.assertEquals("USD/en",
				urlEncoderFacade.patternForUrlEncoding("/teststorefront/invalidLanguage/invalidCurrency/", "teststorefront", false)
						.getPattern());
	}


	@Test
	public void testPatternForUrlEncodingForMissingAttr()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("JPY");
		given(languageAttributeManager.getCurrentValue()).willReturn("en");
		Assert.assertEquals("JPY/en", urlEncoderFacade.patternForUrlEncoding("/teststorefront/JPY/", "teststorefront", false)
				.getPattern());
	}



	@Test
	public void testCalculateAndUpdateUrlEncodingDataForDefault()
	{
		Assert.assertEquals("USD/en", urlEncoderFacade.calculateAndUpdateUrlEncodingData("/teststorefront/", "teststorefront"));
	}


	@Test
	public void testCalculateAndUpdateUrlEncodingDataForAValidUrl()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("JPY");
		given(languageAttributeManager.getCurrentValue()).willReturn("ja");
		Assert.assertEquals("JPY/ja",
				urlEncoderFacade.calculateAndUpdateUrlEncodingData("/teststorefront/JPY/ja/", "teststorefront"));
	}


	@Test
	public void testCalculateAndUpdateUrlEncodingDataForInvalidAttr()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("USD");
		given(languageAttributeManager.getCurrentValue()).willReturn("en");
		Assert.assertEquals("USD/en", urlEncoderFacade.calculateAndUpdateUrlEncodingData(
				"/teststorefront/invalidLanguage/invalidCurrency/", "teststorefront"));
	}


	@Test
	public void testCalculateAndUpdateUrlEncodingDataForMissingAttr()
	{
		given(currencyAttributeManager.getCurrentValue()).willReturn("JPY");
		given(languageAttributeManager.getCurrentValue()).willReturn("en");
		Assert.assertEquals("JPY/en", urlEncoderFacade.calculateAndUpdateUrlEncodingData("/teststorefront/JPY/", "teststorefront"));
	}
}
