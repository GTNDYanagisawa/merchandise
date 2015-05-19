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
package de.hybris.platform.commerceservices.order.i18n.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.i18n.impl.DefaultCommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Testing proper loading of {@link CommerceCommonI18NService}
 */
@IntegrationTest
public class DefaultCommerceCommonI18NServiceTest extends ServicelayerTransactionalTest
{
	@Resource
	private DefaultCommerceCommonI18NService defaultCommerceCommonI18NService;

	@Resource
	private ModelService modelService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private BaseStoreService baseStoreService;

	@Resource
	private CommonI18NService commonI18NService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();

		final BaseSiteModel siteModel = modelService.create(BaseSiteModel.class);
		siteModel.setName("siteModel");
		siteModel.setUid("siteModel");

		final BaseStoreModel store = modelService.create(BaseStoreModel.class);
		store.setUid("store1");
		store.setLanguages(Collections.singleton(commonI18NService.getLanguage("en")));
		store.setCurrencies(Collections.singleton(commonI18NService.getCurrency("USD")));
		store.setDeliveryCountries(Collections.singleton(commonI18NService.getCountry("US")));
		modelService.save(store);
		siteModel.setStores(Arrays.asList(store));
		modelService.save(siteModel);
		baseSiteService.setCurrentBaseSite(siteModel, false);
		defaultCommerceCommonI18NService.setBaseSiteService(baseSiteService);
		defaultCommerceCommonI18NService.setBaseStoreService(baseStoreService);
	}

	@Test
	public void testGetAllSitesCurrencies()
	{

		final List<CurrencyModel> currencies = defaultCommerceCommonI18NService.getAllCurrencies();
		Assert.assertNotNull(currencies);
		Assert.assertEquals(1, currencies.size());
	}

	@Test
	public void testGetAllSitesLanguages()
	{

		final Collection<LanguageModel> languages = defaultCommerceCommonI18NService.getAllLanguages();
		Assert.assertNotNull(languages);
		Assert.assertEquals(1, languages.size());
	}

	@Test
	public void testGetAllSitesDeliveryCountries()
	{

		final Collection<CountryModel> countries = defaultCommerceCommonI18NService.getAllCountries();
		Assert.assertNotNull(countries);
		Assert.assertEquals(1, countries.size());
	}
}
