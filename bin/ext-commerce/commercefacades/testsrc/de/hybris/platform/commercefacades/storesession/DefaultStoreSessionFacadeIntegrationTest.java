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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link DefaultStoreSessionFacade}
 * 
 */
@IntegrationTest
public class DefaultStoreSessionFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String TEST_CATALOG_1 = "testCatalog1";
	private static final String SPRING_VERSION = "Spring";

	@Resource
	private StoreSessionFacade storeSessionFacade;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private ModelService modelService;
	@Resource
	private BaseSiteService baseSiteService;

	private CatalogVersionModel test1Spring = null;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/platformservices/test/catalog/testdata_catalogVersion.csv", "UTF-8");
		importCsv("/commercefacades/test/testProductFacade.csv", "UTF-8");
		importCsv("/commercefacades/test/testBaseSite.csv", "UTF-8");
		test1Spring = catalogVersionService.getCatalogVersion(TEST_CATALOG_1, SPRING_VERSION);

	}

	@Test
	public void testGetCurrentLanguage()
	{
		final LanguageData language = storeSessionFacade.getCurrentLanguage();
		Assert.assertNotNull(language);
	}

	@Test
	public void testGetCurrentCurrency()
	{
		final CurrencyData currency = storeSessionFacade.getCurrentCurrency();
		Assert.assertNotNull(currency);
	}

	@Test
	public void testGetAllLanguages()
	{
		final Collection<LanguageModel> languages = new ArrayList<LanguageModel>();
		final LanguageModel language = modelService.create(LanguageModel.class);
		language.setIsocode("PL");
		languages.add(language);
		test1Spring.setLanguages(languages);
		catalogVersionService.addSessionCatalogVersion(test1Spring);
		final Collection<LanguageData> languagesData = storeSessionFacade.getAllLanguages();
		Assert.assertNotNull(languagesData);
		Assert.assertEquals(1, languages.size());
	}

	@Test
	public void testGetAllCurrencies()
	{

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("storetemplate"), false);

		final Collection<CurrencyData> currencies = storeSessionFacade.getAllCurrencies();
		Assert.assertNotNull(currencies);
		Assert.assertEquals("CHF", currencies.iterator().next().getIsocode());

	}

	@Test
	public void testSetCurrentCurrency()
	{
		storeSessionFacade.setCurrentCurrency("EUR");
		Assert.assertEquals("EUR", storeSessionFacade.getCurrentCurrency().getIsocode());
	}

	@Test
	public void testSetCurrentLanguage()
	{
		storeSessionFacade.setCurrentLanguage("de");
		Assert.assertEquals("de", storeSessionFacade.getCurrentLanguage().getIsocode());
	}
}
