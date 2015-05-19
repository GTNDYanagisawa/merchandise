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
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontProcessModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 */
@UnitTest
public class DefaultContextResolutionStrategyTest
{
	private DefaultProcessContextResolutionStrategy contextResolutionStrategy;

	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CMSSiteService cmsSiteService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private SessionService sessionService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		contextResolutionStrategy = new DefaultProcessContextResolutionStrategy();
		contextResolutionStrategy.setCatalogVersionService(catalogVersionService);
		contextResolutionStrategy.setCmsSiteService(cmsSiteService);
		contextResolutionStrategy.setCommonI18NService(commonI18NService);
		contextResolutionStrategy.setCommerceCommonI18NService(commerceCommonI18NService);
		contextResolutionStrategy.setSessionService(sessionService);
	}

	@Test
	public void testGetCmsSiteStoreFrontProcess()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);
		final StoreFrontProcessModel businessProcessModel = mock(StoreFrontProcessModel.class);

		given(businessProcessModel.getSite()).willReturn(cmsSiteModel);

		Assert.assertEquals(cmsSiteModel, contextResolutionStrategy.getCmsSite(businessProcessModel));
	}

	@Test
	public void testGetCmsSiteOrderProcess()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);
		final OrderProcessModel orderProcessModel = mock(OrderProcessModel.class);
		final OrderModel orderModel = mock(OrderModel.class);

		given(orderProcessModel.getOrder()).willReturn(orderModel);
		given(orderModel.getSite()).willReturn(cmsSiteModel);

		Assert.assertEquals(cmsSiteModel, contextResolutionStrategy.getCmsSite(orderProcessModel));
	}

	@Test
	public void testGetContentCatalogVersion()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);
		final ContentCatalogModel contentCatalogModel = mock(ContentCatalogModel.class);
		final CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class);
		final StoreFrontProcessModel businessProcessModel = mock(StoreFrontProcessModel.class);

		given(businessProcessModel.getSite()).willReturn(cmsSiteModel);
		given(cmsSiteModel.getContentCatalogs()).willReturn(Collections.singletonList(contentCatalogModel));
		given(contentCatalogModel.getId()).willReturn("test");
		given(catalogVersionService.getSessionCatalogVersionForCatalog("test")).willReturn(catalogVersionModel);

		final CatalogVersionModel result = contextResolutionStrategy.getContentCatalogVersion(businessProcessModel);

		try
		{
			verify(cmsSiteService, times(1)).setCurrentSiteAndCatalogVersions(any(CMSSiteModel.class), anyBoolean());
		}
		catch (final CMSItemNotFoundException e)
		{
			Assert.fail("CMSItemNotFoundException was thrown");
		}
		Assert.assertEquals(catalogVersionModel, result);
	}

	@Test
	public void testInitializeSessionContextWithCustomerSettings()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);

		given(businessProcessModel.getSite()).willReturn(cmsSiteModel);
		given(businessProcessModel.getCustomer()).willReturn(customerModel);
		given(customerModel.getSessionCurrency()).willReturn(currencyModel);
		given(customerModel.getSessionLanguage()).willReturn(languageModel);
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singleton(languageModel));
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.singletonList(currencyModel));

		contextResolutionStrategy.initializeContext(businessProcessModel);

		try
		{
			verify(cmsSiteService, times(1)).setCurrentSiteAndCatalogVersions(any(CMSSiteModel.class), anyBoolean());
		}
		catch (final CMSItemNotFoundException e)
		{
			Assert.fail("CMSItemNotFoundException was thrown");
		}
		verify(commerceCommonI18NService, times(1)).getAllLanguages();
		verify(commerceCommonI18NService, times(0)).getDefaultLanguage();
		verify(commerceCommonI18NService, times(1)).getAllCurrencies();
		verify(commerceCommonI18NService, times(0)).getDefaultCurrency();
		verify(commonI18NService, times(1)).setCurrentLanguage(languageModel);
		verify(commonI18NService, times(1)).setCurrentCurrency(currencyModel);
	}

	@Test
	public void testInitializeSessionContextWithSiteDefaultSettings()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);

		given(businessProcessModel.getSite()).willReturn(cmsSiteModel);
		given(businessProcessModel.getCustomer()).willReturn(customerModel);
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singleton(languageModel));
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.singletonList(currencyModel));
		given(commerceCommonI18NService.getDefaultCurrency()).willReturn(currencyModel);
		given(commerceCommonI18NService.getDefaultLanguage()).willReturn(languageModel);

		contextResolutionStrategy.initializeContext(businessProcessModel);

		try
		{
			verify(cmsSiteService, times(1)).setCurrentSiteAndCatalogVersions(any(CMSSiteModel.class), anyBoolean());
		}
		catch (final CMSItemNotFoundException e)
		{
			Assert.fail("CMSItemNotFoundException was thrown");
		}
		verify(commerceCommonI18NService, times(0)).getAllLanguages();
		verify(commerceCommonI18NService, times(1)).getDefaultLanguage();
		verify(commerceCommonI18NService, times(0)).getAllCurrencies();
		verify(commerceCommonI18NService, times(1)).getDefaultCurrency();
		verify(commonI18NService, times(1)).setCurrentLanguage(languageModel);
		verify(commonI18NService, times(1)).setCurrentCurrency(currencyModel);

	}

	@Test
	public void testInitializeSessionContextWithSiteDefaultSettingsAsCustomerSettingsInvalid()
	{
		final CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final CurrencyModel validCurrencyModel = mock(CurrencyModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final LanguageModel validLanguageModel = mock(LanguageModel.class);
		final StoreFrontCustomerProcessModel businessProcessModel = mock(StoreFrontCustomerProcessModel.class);

		given(businessProcessModel.getSite()).willReturn(cmsSiteModel);
		given(businessProcessModel.getCustomer()).willReturn(customerModel);
		given(commerceCommonI18NService.getAllLanguages()).willReturn(Collections.singleton(validLanguageModel));
		given(commerceCommonI18NService.getAllCurrencies()).willReturn(Collections.singletonList(validCurrencyModel));
		given(commerceCommonI18NService.getDefaultCurrency()).willReturn(currencyModel);
		given(commerceCommonI18NService.getDefaultLanguage()).willReturn(languageModel);
		given(customerModel.getSessionCurrency()).willReturn(currencyModel);
		given(customerModel.getSessionLanguage()).willReturn(languageModel);

		contextResolutionStrategy.initializeContext(businessProcessModel);

		try
		{
			verify(cmsSiteService, times(1)).setCurrentSiteAndCatalogVersions(any(CMSSiteModel.class), anyBoolean());
		}
		catch (final CMSItemNotFoundException e)
		{
			Assert.fail("CMSItemNotFoundException was thrown");
		}
		verify(commerceCommonI18NService, times(1)).getAllLanguages();
		verify(commerceCommonI18NService, times(1)).getDefaultLanguage();
		verify(commerceCommonI18NService, times(1)).getAllCurrencies();
		verify(commerceCommonI18NService, times(1)).getDefaultCurrency();
		verify(commonI18NService, times(1)).setCurrentLanguage(languageModel);
		verify(commonI18NService, times(1)).setCurrentCurrency(currencyModel);

	}

}
