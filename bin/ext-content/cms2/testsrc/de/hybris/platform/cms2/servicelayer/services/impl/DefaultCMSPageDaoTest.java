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

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.impl.DefaultCMSPageDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class DefaultCMSPageDaoTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;

	@Resource
	private DefaultCMSPageDao cmsPageDao;

	private ContentPageModel firstContentPage;
	private ContentPageModel secondContentPage;

	private CatalogVersionModel catalogVersionModel;
	private CatalogModel catalog;
	private PageTemplateModel template;

	private static final int THOUSAND_ONE_ELEMENTS = 1001;

	private final List<ContentSlotModel> moreThanThousandSlots = new ArrayList<ContentSlotModel>(THOUSAND_ONE_ELEMENTS);
	private final List<CatalogVersionModel> moreThanThousandCatalogVersions = new ArrayList<CatalogVersionModel>(
			THOUSAND_ONE_ELEMENTS);

	@Before
	public void createSampleCatalogStructure()
	{
		catalog = modelService.create(CatalogModel.class);
		catalog.setId("testCatalog");
		modelService.save(catalog);

		catalogVersionModel = modelService.create(CatalogVersionModel.class);
		catalogVersionModel.setActive(Boolean.TRUE);
		catalogVersionModel.setVersion("testVersion");
		catalogVersionModel.setCatalog(catalog);
		catalog.setCatalogVersions(Collections.singleton(catalogVersionModel));
		modelService.save(catalogVersionModel);

		template = modelService.create(PageTemplateModel.class);
		template.setUid("testTemplate");
		template.setCatalogVersion(catalogVersionModel);
		modelService.save(template);

		firstContentPage = modelService.create(ContentPageModel.class);
		firstContentPage.setUid("content_page_1");
		firstContentPage.setMasterTemplate(template);
		firstContentPage.setCatalogVersion(catalogVersionModel);
		modelService.save(firstContentPage);

		secondContentPage = modelService.create(ContentPageModel.class);
		secondContentPage.setUid("content_page_2");
		secondContentPage.setMasterTemplate(template);
		secondContentPage.setCatalogVersion(catalogVersionModel);
		modelService.save(secondContentPage);

		ContentSlotModel slot = modelService.create(ContentSlotModel.class);
		slot.setUid("slot_1");
		slot.setCatalogVersion(catalogVersionModel);
		moreThanThousandSlots.add(slot);

		setRelationModel((ContentSlotForPageModel) modelService.create(ContentSlotForPageModel.class), slot, "rel_1");
		setRelationModel((ContentSlotForTemplateModel) modelService.create(ContentSlotForTemplateModel.class), slot,
				"relTemplate_1");

		for (int i = 1; i < THOUSAND_ONE_ELEMENTS; i++)
		{
			slot = modelService.create(ContentSlotModel.class);
			slot.setUid("slot_" + (i + 1));
			slot.setCatalogVersion(catalogVersionModel);
			moreThanThousandSlots.add(slot);
		}
		modelService.saveAll(moreThanThousandSlots);

		for (int i = 1; i < THOUSAND_ONE_ELEMENTS; i++)
		{

			final CatalogVersionModel cvm = modelService.create(CatalogVersionModel.class);
			cvm.setActive(Boolean.TRUE);
			cvm.setVersion("testVersion_" + (i + 1));
			cvm.setCatalog(catalog);

			moreThanThousandCatalogVersions.add(cvm);
		}

		moreThanThousandCatalogVersions.add(catalogVersionModel);
		catalog.setCatalogVersions(new HashSet<CatalogVersionModel>(moreThanThousandCatalogVersions));
		modelService.save(catalog);
		modelService.saveAll(moreThanThousandCatalogVersions);

	}

	private void setRelationModel(final ContentSlotForPageModel rel, final ContentSlotModel slot, final String id)
	{
		rel.setPage(firstContentPage);
		rel.setContentSlot(slot);
		rel.setPosition("no");
		setCMSRelationModel(rel, id);
	}

	private void setRelationModel(final ContentSlotForTemplateModel rel, final ContentSlotModel slot, final String id)
	{
		rel.setPageTemplate(template);
		rel.setContentSlot(slot);
		rel.setPosition("no");
		setCMSRelationModel(rel, id);
	}

	private void setCMSRelationModel(final CMSRelationModel rel, final String id)
	{
		rel.setUid(id);
		rel.setCatalogVersion(catalogVersionModel);
		modelService.save(rel);
	}

	@Test
	public void testFindPagesForContentSlots()
	{

		//Assert that firstContentPage exists when sending a related ContentSlot
		Collection<AbstractPageModel> result1 = cmsPageDao.findPagesForContentSlots(moreThanThousandSlots,
				Arrays.asList(catalogVersionModel));

		assertTrue(result1.contains(firstContentPage));
		//
		//Assert that no Pages were found when sending a ContentSlot that does not exist in certain catalogVersion
		result1 = cmsPageDao.findPagesForContentSlots(Arrays.asList(moreThanThousandSlots.get(1)),
				Arrays.asList(catalogVersionModel));
		assertTrue(result1.isEmpty());
	}

	@Test
	public void testFindPagesForTemplateContentSlots()
	{

		//Assert that firstContentPage exists when sending a related ContentSlot with related page template
		Collection<AbstractPageModel> result1 = cmsPageDao.findPagesByPageTemplateContentSlots(moreThanThousandSlots,
				Arrays.asList(catalogVersionModel));

		assertTrue(result1.contains(firstContentPage));
		//
		//Assert that no Pages were found when sending a ContentSlot without a related page template
		result1 = cmsPageDao.findPagesByPageTemplateContentSlots(Arrays.asList(moreThanThousandSlots.get(1)),
				Arrays.asList(catalogVersionModel));
		assertTrue(result1.isEmpty());
	}

	@Test
	public void testFindPagesForContentSlotsWithMoreThanThousandCatalogVersions()
	{

		//Assert that firstContentPage exists when sending a related ContentSlot
		Collection<AbstractPageModel> result1 = cmsPageDao.findPagesForContentSlots(moreThanThousandSlots,
				moreThanThousandCatalogVersions);

		assertTrue(result1.contains(firstContentPage));
		//
		//Assert that no Pages were found when sending a ContentSlot that does not exist in certain catalogVersion
		result1 = cmsPageDao.findPagesForContentSlots(Arrays.asList(moreThanThousandSlots.get(1)), moreThanThousandCatalogVersions);
		assertTrue(result1.isEmpty());
	}

	@Test
	public void testFindPagesForTemplateContentSlotsWithMoreThanThousandCatalogVersions()
	{

		//Assert that firstContentPage exists when sending a related ContentSlot with related page template
		Collection<AbstractPageModel> result1 = cmsPageDao.findPagesByPageTemplateContentSlots(moreThanThousandSlots,
				moreThanThousandCatalogVersions);

		assertTrue(result1.contains(firstContentPage));
		//
		//Assert that no Pages were found when sending a ContentSlot without a related page template
		result1 = cmsPageDao.findPagesByPageTemplateContentSlots(Arrays.asList(moreThanThousandSlots.get(1)),
				moreThanThousandCatalogVersions);
		assertTrue(result1.isEmpty());
	}
}
