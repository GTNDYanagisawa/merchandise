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
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class ContentSlotForTemplateInitDefaultsInterceptorTest extends ServicelayerTest // NOPMD: Junit4 allows any name for test method
{

	@Resource
	private ModelService modelService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	private CatalogVersionModel secondCtgVer;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		secondCtgVer = createCatalogVersion();
	}

	@Test
	public void shouldCreateContentSlotForTemplateWithCatalogVersionTakenFromPageTemplateIfPageTemplateExists()
	{
		// given
		final ContentSlotForTemplateModel contentSlotForTemplate = modelService.create(ContentSlotForTemplateModel.class);
		contentSlotForTemplate.setPageTemplate(getPageTemplate());
		contentSlotForTemplate.setPosition("FooBar1");
		contentSlotForTemplate.setContentSlot(getContentSlot());
		modelService.save(contentSlotForTemplate);

		// when
		final CatalogVersionModel catalogVersion = contentSlotForTemplate.getCatalogVersion();

		// then
		assertThat(catalogVersion).isNotNull();
		assertThat(catalogVersion).isEqualTo(contentSlotForTemplate.getPageTemplate().getCatalogVersion());
	}

	@Test
	public void shouldCreateContentSlotForTemplateWithoutGetingCatalogVersionFromPageTemplateIfCatalogVersionIsGivenExplicite()
	{
		// given
		final ContentSlotForTemplateModel contentSlotForTemplate = modelService.create(ContentSlotForTemplateModel.class);
		contentSlotForTemplate.setPageTemplate(getPageTemplate());
		contentSlotForTemplate.setPosition("FooBar1");
		contentSlotForTemplate.setContentSlot(getContentSlot());
		contentSlotForTemplate.setCatalogVersion(secondCtgVer);
		modelService.save(contentSlotForTemplate);

		// when
		final CatalogVersionModel catalogVersion = contentSlotForTemplate.getCatalogVersion();

		// then
		assertThat(catalogVersion).isNotNull();
		assertThat(catalogVersion).isNotEqualTo(contentSlotForTemplate.getPageTemplate().getCatalogVersion());
		assertThat(catalogVersion).isEqualTo(secondCtgVer);
	}

	private ContentSlotModel getContentSlot()
	{
		final ContentSlotModel contentSlot = modelService.create(ContentSlotModel.class);
		contentSlot.setUid("FooBar3");
		return contentSlot;
	}

	private CatalogVersionModel getCatalogVersion()
	{
		return getCatalog().getActiveCatalogVersion();
	}

	private CatalogModel getCatalog()
	{
		final CatalogModel example = new CatalogModel();
		example.setId("testCatalog");
		return flexibleSearchService.getModelByExample(example);
	}

	private PageTemplateModel getPageTemplate()
	{
		final PageTemplateModel pageTemplate = modelService.create(PageTemplateModel.class);
		pageTemplate.setUid("FooBar4");
		pageTemplate.setCatalogVersion(getCatalogVersion());
		return pageTemplate;
	}

	private CatalogVersionModel createCatalogVersion()
	{
		final CatalogVersionModel catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setActive(Boolean.TRUE);
		catalogVersion.setVersion("Staged");
		catalogVersion.setCatalog(getCatalog());
		return catalogVersion;
	}

}
