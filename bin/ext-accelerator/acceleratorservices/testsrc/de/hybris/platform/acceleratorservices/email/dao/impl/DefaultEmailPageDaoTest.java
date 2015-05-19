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
package de.hybris.platform.acceleratorservices.email.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.email.dao.EmailPageDao;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 */
@IntegrationTest
public class DefaultEmailPageDaoTest extends ServicelayerTransactionalBaseTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private EmailPageDao emailPageDao;

	private EmailPageModel emailPageModel;
	private CatalogVersionModel catalogVersionModel;

	@Before
	public void setUp()
	{
		final CMSSiteModel cmsSiteModel = modelService.create(CMSSiteModel.class);
		cmsSiteModel.setName("cmsSiteModel");
		cmsSiteModel.setUid("cmsSiteModel");
		cmsSiteModel.setActive(Boolean.TRUE);

		final CurrencyModel currency1 = modelService.create(CurrencyModel.class);
		currency1.setIsocode("PL");
		currency1.setSymbol("PL");

		final Set<CurrencyModel> currencies = new HashSet<CurrencyModel>();
		currencies.add(currency1);

		final BaseStoreModel store1 = modelService.create(BaseStoreModel.class);
		store1.setCurrencies(currencies);
		store1.setUid("store1");

		final List<BaseStoreModel> stores = new ArrayList<BaseStoreModel>();
		stores.add(store1);

		cmsSiteModel.setStores(stores);

		final LanguageModel languageModel = modelService.create(LanguageModel.class);
		languageModel.setIsocode("PL");
		modelService.save(languageModel);
		final List<LanguageModel> languagesModel = new ArrayList<LanguageModel>();
		languagesModel.add(languageModel);

		final ContentCatalogModel contentCatalog = modelService.create(ContentCatalogModel.class);
		contentCatalog.setId("contentCatalog1");
		modelService.save(contentCatalog);

		catalogVersionModel = modelService.create(CatalogVersionModel.class);
		catalogVersionModel.setVersion("1.0");
		catalogVersionModel.setLanguages(languagesModel);
		catalogVersionModel.setCatalog(contentCatalog);
		modelService.save(catalogVersionModel);
		final CatalogVersionModel model2 = modelService.create(CatalogVersionModel.class);
		model2.setVersion("2.0");
		model2.setLanguages(languagesModel);
		model2.setCatalog(contentCatalog);
		modelService.save(model2);

		final Set<CatalogVersionModel> catalogVersions = new HashSet<CatalogVersionModel>();
		catalogVersions.add(catalogVersionModel);
		catalogVersions.add(model2);
		contentCatalog.setCatalogVersions(catalogVersions);
		modelService.save(contentCatalog);

		contentCatalog.setActiveCatalogVersion(model2);
		modelService.save(contentCatalog);

		final List<ContentCatalogModel> contents = new ArrayList<ContentCatalogModel>();
		contents.add(contentCatalog);

		cmsSiteModel.setContentCatalogs(contents);
		modelService.save(cmsSiteModel);


		final EmailPageTemplateModel emailPageTemplateModel = modelService.create(EmailPageTemplateModel.class);
		emailPageTemplateModel.setUid("testTemplate");
		emailPageTemplateModel.setFrontendTemplateName("testTemplate");
		emailPageTemplateModel.setActive(Boolean.TRUE);
		emailPageTemplateModel.setCatalogVersion(catalogVersionModel);
		emailPageTemplateModel.setName("testTemplate");
		modelService.save(emailPageTemplateModel);

		emailPageModel = modelService.create(EmailPageModel.class);
		emailPageModel.setCatalogVersion(catalogVersionModel);
		emailPageModel.setUid("testPage");
		emailPageModel.setMasterTemplate(emailPageTemplateModel);
		modelService.save(emailPageModel);
	}

	@Test
	public void testFindEmailPageByFrontendTemplateName()
	{
		final EmailPageModel testTemplate = emailPageDao.findEmailPageByFrontendTemplateName("testTemplate", catalogVersionModel);
		assertNotNull(testTemplate);
		assertEquals(testTemplate, emailPageModel);
	}

	@Test
	public void testFindEmailPageByFrontendTemplateNameNull()
	{
		final EmailPageModel testTemplate = emailPageDao.findEmailPageByFrontendTemplateName("notfoundTemplate",
				catalogVersionModel);
		assertNull(testTemplate);
	}

}
