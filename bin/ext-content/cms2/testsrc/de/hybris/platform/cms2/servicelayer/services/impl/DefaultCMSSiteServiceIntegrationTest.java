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
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSSiteServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private CMSSiteService cmsSiteService;
	@Resource
	private ModelService modelService;
	@Resource
	private CatalogVersionService catalogVersionService;
	private CMSSiteModel site;
	private ContentPageModel startingPage;
	private PageTemplateModel pageTemplate;


	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		pageTemplate = modelService.create(PageTemplateModel.class);
		pageTemplate.setUid("FooPageTemplate");
		pageTemplate.setCatalogVersion(getCatalogVersion());
		modelService.save(pageTemplate);

		startingPage = modelService.create(ContentPageModel.class);
		startingPage.setUid("FooPage");
		startingPage.setCatalogVersion(getCatalogVersion());
		startingPage.setMasterTemplate(pageTemplate);
		modelService.save(startingPage);

		site = modelService.create(CMSSiteModel.class);
		site.setUid("FooSite");
		site.setStartingPage(startingPage);
		modelService.save(site);
	}

	@Test
	public void testShouldReturnLabelOrIdForStartingPage()
	{
		// when
		final String startPageLabelOrId = cmsSiteService.getStartPageLabelOrId(site);

		// then
		assertThat(startPageLabelOrId).isNotNull();
		assertThat(startPageLabelOrId).isEqualTo("FooPage");
	}

	private CatalogVersionModel getCatalogVersion()
	{
		return catalogVersionService.getCatalogVersion("testCatalog", "Online");
	}
}
