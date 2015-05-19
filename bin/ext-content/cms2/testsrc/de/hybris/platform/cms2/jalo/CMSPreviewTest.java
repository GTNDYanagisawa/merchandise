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

import static de.hybris.platform.testframework.Assert.assertCollection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class CMSPreviewTest extends ServicelayerTransactionalTest
{
	protected final static Logger LOG = Logger.getLogger(CMSPreviewTest.class.getName());

	@Resource
	private CMSPreviewService cmsPreviewService;

	@Resource
	private ModelService modelService;

	private PreviewDataModel data;

	private ArrayList<CatalogVersionModel> catalogVersions;

	private CatalogVersionModel activeCatalogVersion;

	private CMSSiteModel site;

	private BaseStoreModel store;

	@Test
	public void testPreviewTicket()
	{
		final CMSPreviewTicketModel ticket = cmsPreviewService.createPreviewTicket(data);
		final CMSPreviewTicketModel secondTicket = cmsPreviewService.createPreviewTicket(data);

		assertCollection(catalogVersions, ticket.getPreviewData().getCatalogVersions());
		assertEquals("Should return the same ticket", ticket, cmsPreviewService.getPreviewTicket(ticket.getId()));
		assertNotSame(secondTicket, ticket);
		assertNotSame(secondTicket.getId(), ticket.getId());

		assertNotSame(data, cmsPreviewService.clonePreviewData(data));

		final String storedTicketId = cmsPreviewService.storePreviewTicket(ticket);

		assertNotNull(storedTicketId);
		assertEquals(ticket.getId(), storedTicketId);

		final Collection<CatalogModel> editableCatalogs = cmsPreviewService.getEditableCatalogs(site, activeCatalogVersion);
		assertEquals("classification and active catalogs are not editable", 2, editableCatalogs.size());
	}

	@Before
	public void setUp()
	{
		prepareCatalogVersions();
		preparePreviewData();
		prepareStore();
		prepareSite();
	}

	private void prepareStore()
	{
		store = modelService.create(BaseStoreModel.class);
		store.setCatalogs(extractCatalogs());
		store.setUid("testStore");
		modelService.save(store);
	}

	private List<CatalogModel> extractCatalogs()
	{
		final List<CatalogModel> catalogs = new ArrayList<CatalogModel>();
		for (final CatalogVersionModel cvm : catalogVersions)
		{
			final CatalogModel catalog = cvm.getCatalog();
			if (!catalogs.contains(catalog))
			{
				catalogs.add(catalog);
			}
		}
		return catalogs;
	}

	private void prepareSite()
	{
		site = modelService.create(CMSSiteModel.class);
		site.setActive(Boolean.TRUE);
		site.setStores(Collections.singletonList(store));
		site.setUid("testSite");
		modelService.save(site);
	}

	private void preparePreviewData()
	{
		data = new PreviewDataModel();
		data.setLiveEdit(Boolean.TRUE);
		data.setCatalogVersions(catalogVersions);
		modelService.save(data);
	}

	private void prepareCatalogVersions()
	{
		catalogVersions = new ArrayList<CatalogVersionModel>();
		catalogVersions.add(createCatalogVersion("hwcatalog", "Online"));
		catalogVersions.add(createCatalogVersion("clothescatalog", "Online"));
		catalogVersions.add(createClassificationCatalogVersion("SampleClassification", "1.0"));
		catalogVersions.add(activeCatalogVersion = createCatalogVersion("contentActive", "Staged"));
	}

	private ClassificationSystemVersionModel createClassificationCatalogVersion(final String name, final String version)
	{
		final CatalogModel catalog = modelService.create(ClassificationSystemModel.class);
		catalog.setName(name);
		catalog.setId(name);
		final ClassificationSystemVersionModel result = modelService.create(ClassificationSystemVersionModel.class);
		result.setCatalog(catalog);
		result.setVersion(version);
		return result;
	}

	private CatalogVersionModel createCatalogVersion(final String name, final String version)
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setName(name);
		catalog.setId(name);
		final CatalogVersionModel result = modelService.create(CatalogVersionModel.class);
		result.setCatalog(catalog);
		result.setVersion(version);
		return result;
	}
}
