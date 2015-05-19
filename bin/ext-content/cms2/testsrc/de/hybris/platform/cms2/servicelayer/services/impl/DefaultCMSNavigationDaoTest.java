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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSNavigationDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class DefaultCMSNavigationDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private CMSNavigationDao cmsNavigationDao;

	private CMSNavigationNodeModel rootNavigationModel;
	private CMSNavigationNodeModel firstNavigationModel;
	private CMSNavigationNodeModel secondNavigationModel;
	private ContentPageModel firstContentPage;

	private CatalogVersionModel catalogVersionModel;
	private CatalogModel catalog;

	private CatalogVersionModel emptyCatalogVersionModel;
	private CatalogModel emptyCatalog;


	@Before
	public void createSampleCatalogStructure()
	{
		catalog = modelService.create(CatalogModel.class);
		catalog.setId("testcCategory");
		modelService.save(catalog);

		emptyCatalog = modelService.create(CatalogModel.class);
		emptyCatalog.setId("empty_catalog");
		modelService.save(emptyCatalog);

		catalogVersionModel = modelService.create(CatalogVersionModel.class);
		catalogVersionModel.setActive(Boolean.TRUE);
		catalogVersionModel.setVersion("testVersion");
		catalogVersionModel.setCatalog(catalog);
		catalog.setCatalogVersions(Collections.singleton(catalogVersionModel));
		modelService.save(catalogVersionModel);

		emptyCatalogVersionModel = modelService.create(CatalogVersionModel.class);
		emptyCatalogVersionModel.setActive(Boolean.TRUE);
		emptyCatalogVersionModel.setVersion("emptyTestVersion");
		emptyCatalogVersionModel.setCatalog(catalog);
		emptyCatalog.setCatalogVersions(Collections.singleton(emptyCatalogVersionModel));
		modelService.save(emptyCatalogVersionModel);

		rootNavigationModel = new CMSNavigationNodeModel();
		rootNavigationModel.setUid("testRootNavigationModel");
		rootNavigationModel.setCatalogVersion(catalogVersionModel);
		rootNavigationModel.setChildren(createNavigationNodes(catalogVersionModel));

		modelService.save(rootNavigationModel);
		modelService.refresh(catalog);

	}

	private List<CMSNavigationNodeModel> createNavigationNodes(final CatalogVersionModel catalogVersionModel)
	{
		createContentPages();
		final List<CMSNavigationNodeModel> navigationNodesChildren = new ArrayList<CMSNavigationNodeModel>();

		firstNavigationModel = new CMSNavigationNodeModel();
		firstNavigationModel.setUid("level_1_0");
		firstNavigationModel.setCatalogVersion(catalogVersionModel);
		navigationNodesChildren.add(firstNavigationModel);
		firstNavigationModel.setPages(Arrays.asList(firstContentPage));
		modelService.save(firstNavigationModel);

		secondNavigationModel = new CMSNavigationNodeModel();
		secondNavigationModel.setUid("level_1_1");
		secondNavigationModel.setCatalogVersion(catalogVersionModel);
		navigationNodesChildren.add(secondNavigationModel);
		modelService.save(secondNavigationModel);

		return navigationNodesChildren;
	}

	private void createContentPages()
	{

		final PageTemplateModel template = modelService.create(PageTemplateModel.class);
		template.setUid("testTemplate");
		template.setCatalogVersion(catalogVersionModel);
		modelService.save(template);

		firstContentPage = new ContentPageModel();
		firstContentPage.setUid("content_page_1");
		firstContentPage.setMasterTemplate(template);
		firstContentPage.setCatalogVersion(catalogVersionModel);

		modelService.save(firstContentPage);

	}

	@Test
	public void testFindCMSNavigationNodes() throws CMSItemNotFoundException
	{

		//Assert that firstNavigationModel exists when sending a page that exists in a catalogVersion
		List<CMSNavigationNodeModel> result1 = cmsNavigationDao.findNavigationNodesByContentPage(firstContentPage,
				Arrays.asList(catalogVersionModel));
		assertTrue(result1.contains(firstNavigationModel));

		//Assert that secondNavigationModel does not contain page "firstContentPage"
		result1 = cmsNavigationDao.findNavigationNodesByContentPage(firstContentPage, Arrays.asList(catalogVersionModel));
		assertFalse(result1.contains(secondNavigationModel));

		//Assert that no CMSNavigationNodeModels were found when sending a page that does not exist in certain catalogVersion
		result1 = cmsNavigationDao.findNavigationNodesByContentPage(firstContentPage, Arrays.asList(emptyCatalogVersionModel));
		assertTrue(result1.isEmpty());

	}

}
