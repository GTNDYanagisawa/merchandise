/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSContentSlotServiceIntegrationTest extends ServicelayerTest // NOPMD Junit4 allows any names for test methods
{
	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private CMSContentSlotService cmsContentSlotService;
	private ContentSlotModel contentSlot;
	private ContentPageModel page;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void shouldReturnRelatedPagesForGivenContentSlot()
	{
		// given
		createContentSlotStruct();

		// when
		final Collection<AbstractPageModel> pages = cmsContentSlotService.getPagesForContentSlot(contentSlot);

		// then
		assertThat(modelService.isNew(contentSlot)).isFalse();
		assertThat(pages).isNotEmpty();
		assertThat(pages).hasSize(1);
		assertThat(pages).contains(page);
	}

	private void createContentSlotStruct()
	{
		final CatalogVersionModel catalogVersion = getCatalogVersion();

		contentSlot = modelService.create(ContentSlotModel.class);
		contentSlot.setUid("FooBarSlot1");
		contentSlot.setCatalogVersion(catalogVersion);
		modelService.save(contentSlot);

		final PageTemplateModel template = modelService.create(PageTemplateModel.class);
		template.setUid("FooBarTemplate1");
		template.setCatalogVersion(catalogVersion);
		modelService.save(template);

		page = modelService.create(ContentPageModel.class);
		page.setUid("FooBarPage1");
		page.setCatalogVersion(catalogVersion);
		page.setMasterTemplate(template);
		modelService.save(page);

		final ContentSlotForPageModel contentSlotForPage = modelService.create(ContentSlotForPageModel.class);
		contentSlotForPage.setContentSlot(contentSlot);
		contentSlotForPage.setPage(page);
		contentSlotForPage.setPosition("FooBarPos");
		modelService.save(contentSlotForPage);
	}

	private CatalogVersionModel getCatalogVersion()
	{
		final CatalogModel catalog = flexibleSearchService
				.<CatalogModel> search("SELECT {PK} FROM {Catalog} WHERE {id}='testCatalog'").getResult().get(0);
		return flexibleSearchService
				.<CatalogVersionModel> search("SELECT {PK} FROM {CatalogVersion} WHERE {version}='Online' AND {catalog}=?catalog",
						Collections.singletonMap("catalog", catalog)).getResult().get(0);
	}
}
