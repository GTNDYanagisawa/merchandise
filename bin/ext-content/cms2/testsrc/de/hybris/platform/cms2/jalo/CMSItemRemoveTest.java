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
package de.hybris.platform.cms2.jalo;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


public class CMSItemRemoveTest extends ServicelayerTest
{
	@Resource
	private ModelService modelService;

	private class CmsStruct
	{
		public AbstractPageModel page;
		public ContentSlotModel slot;
		public PageTemplateModel template;
	}

	protected CatalogModel createSampleCatalogStructure(final String id)
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId(id);
		modelService.save(catalog);
		final CatalogVersionModel version = modelService.create(CatalogVersionModel.class);
		version.setCatalog(catalog);
		version.setVersion("testVersion");
		modelService.save(version);

		modelService.refresh(catalog);
		return catalog;
	}

	private CmsStruct createSampleCmsStructure(final CatalogVersionModel version)
	{
		final ContentSlotModel slot = modelService.create(ContentSlotModel.class);
		slot.setUid("slot_2");
		slot.setCatalogVersion(version);
		modelService.save(slot);

		final PageTemplateModel template = modelService.create(PageTemplateModel.class);
		template.setUid("temp_1");
		template.setCatalogVersion(version);
		modelService.save(template);

		final ContentPageModel page = modelService.create(ContentPageModel.class);
		page.setUid("page_1");
		page.setCatalogVersion(version);
		page.setMasterTemplate(template);
		modelService.save(page);

		final CmsStruct ret = new CmsStruct();
		ret.page = page;
		ret.slot = slot;
		ret.template = template;
		return ret;
	}

	@Test
	public void testDeleteContentSlotForPage()
	{
		final CatalogModel sampleCatalog = createSampleCatalogStructure("testcat1");
		final CatalogVersionModel version = sampleCatalog.getCatalogVersions().iterator().next();

		final CmsStruct sampleCmsStructure = createSampleCmsStructure(version);

		final ContentSlotForPageModel rel = modelService.create(ContentSlotForPageModel.class);
		rel.setUid("rel_1");
		rel.setPosition("no");
		rel.setCatalogVersion(version);
		rel.setPage(sampleCmsStructure.page);
		rel.setContentSlot(sampleCmsStructure.slot);
		modelService.save(rel);

		modelService.remove(sampleCmsStructure.slot);

		Assert.assertTrue(modelService.isRemoved(rel));
	}

	@Test
	public void testDeleteContentSlotForTemplate()
	{
		final CatalogModel sampleCatalog = createSampleCatalogStructure("testcat1");
		final CatalogVersionModel version = sampleCatalog.getCatalogVersions().iterator().next();

		final CmsStruct sampleCmsStructure = createSampleCmsStructure(version);

		final ContentSlotForTemplateModel rel = modelService.create(ContentSlotForTemplateModel.class);
		rel.setUid("rel_1");
		rel.setPosition("no");
		rel.setCatalogVersion(version);
		rel.setPageTemplate(sampleCmsStructure.template);
		rel.setContentSlot(sampleCmsStructure.slot);
		modelService.save(rel);

		modelService.remove(sampleCmsStructure.slot);

		Assert.assertTrue(modelService.isRemoved(rel));
	}

	@Test
	public void testDeletePage()
	{
		final CatalogModel sampleCatalog = createSampleCatalogStructure("testcat1");
		final CatalogVersionModel version = sampleCatalog.getCatalogVersions().iterator().next();

		final CmsStruct sampleCmsStructure = createSampleCmsStructure(version);

		final ContentSlotForPageModel rel = modelService.create(ContentSlotForPageModel.class);
		rel.setUid("rel_1");
		rel.setPosition("no");
		rel.setCatalogVersion(version);
		rel.setPage(sampleCmsStructure.page);
		rel.setContentSlot(sampleCmsStructure.slot);
		modelService.save(rel);

		modelService.remove(sampleCmsStructure.page);

		Assert.assertTrue(modelService.isRemoved(sampleCmsStructure.slot));
		Assert.assertTrue(modelService.isRemoved(rel));
	}

	@Test
	public void testDeletePageTemplate()
	{
		final CatalogModel sampleCatalog = createSampleCatalogStructure("testcat1");
		final CatalogVersionModel version = sampleCatalog.getCatalogVersions().iterator().next();

		final CmsStruct sampleCmsStructure = createSampleCmsStructure(version);

		final ContentSlotForTemplateModel rel = modelService.create(ContentSlotForTemplateModel.class);
		rel.setUid("rel_1");
		rel.setPosition("no");
		rel.setCatalogVersion(version);
		rel.setPageTemplate(sampleCmsStructure.template);
		rel.setContentSlot(sampleCmsStructure.slot);
		modelService.save(rel);

		modelService.remove(sampleCmsStructure.template);

		Assert.assertTrue(modelService.isRemoved(rel));
	}



	@Test
	public void testDeleteCatalogVersion()
	{
		final CatalogModel sampleCatalog = createSampleCatalogStructure("testcat1");
		final CatalogVersionModel version = sampleCatalog.getCatalogVersions().iterator().next();
		final CMSItemModel item = modelService.create(CMSParagraphComponentModel.class);
		item.setUid("uid1");
		item.setCatalogVersion(version);
		modelService.save(item);

		//removing should fail since there is an cms item
		try
		{
			modelService.remove(version);
			Assert.fail("Remove catalog version should throw exception.");
		}
		catch (final SystemException e)
		{
			// fine
		}

		modelService.remove(item);


		// create new version
		final CatalogVersionModel version2 = modelService.create(CatalogVersionModel.class);
		version2.setCatalog(sampleCatalog);
		version2.setVersion("testVersion2");
		modelService.save(version2);

		final CmsStruct sampleCmsStructure = createSampleCmsStructure(version2);

		final ContentSlotForPageModel rel = modelService.create(ContentSlotForPageModel.class);
		rel.setUid("rel_1");
		rel.setPosition("no");
		rel.setCatalogVersion(version);
		rel.setPage(sampleCmsStructure.page);
		rel.setContentSlot(sampleCmsStructure.slot);
		modelService.save(rel);

		//removing should fail since there is a relation
		try
		{
			modelService.remove(version);
			Assert.fail("Remove catalog version should throw exception.");
		}
		catch (final SystemException e)
		{
			// fine
		}

		modelService.remove(rel);


		//now removing should work
		modelService.remove(version);

	}
}
