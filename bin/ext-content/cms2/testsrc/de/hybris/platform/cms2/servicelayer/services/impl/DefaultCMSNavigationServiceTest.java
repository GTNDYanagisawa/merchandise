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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSNavigationDao;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * @author karol.walczak
 */
@UnitTest
public class DefaultCMSNavigationServiceTest
{
	@Mock
	@SuppressWarnings("unused")
	private ModelService modelService;

	@Mock
	@SuppressWarnings("unused")
	private CMSNavigationDao cmsNavigationDao;
	@InjectMocks
	private final CMSNavigationService cmsNavigationService = new DefaultCMSNavigationService();

	@Mock
	@SuppressWarnings("unused")
	private PersistentKeyGenerator processCodeGenerator;

	private CMSNavigationNodeModel rootNavigationModel;
	private CMSNavigationNodeModel firstNavigationModel;
	private CMSNavigationNodeModel secondNavigationModel;
	private CMSNavigationNodeModel thirdNavigationModel;

	private ContentPageModel firstContentPage;
	private ContentPageModel secondContentPage;
	private ContentPageModel thirdContentPage;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final CatalogModel catalogModel = new CatalogModel();
		catalogModel.setId("testCatalog");

		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		catalogVersionModel.setActive(Boolean.TRUE);
		catalogVersionModel.setVersion("test");
		catalogVersionModel.setCatalog(catalogModel);
		catalogModel.setCatalogVersions(Collections.singleton(catalogVersionModel));

		rootNavigationModel = new CMSNavigationNodeModel();
		rootNavigationModel.setUid("test_uid");
		rootNavigationModel.setCatalogVersion(catalogVersionModel);
		rootNavigationModel.setParent(null);
		rootNavigationModel.setChildren(createNavigationNodes(catalogVersionModel));

		Mockito.when(modelService.create(CMSNavigationEntryModel.class)).thenReturn(new CMSNavigationEntryModel(),
				new CMSNavigationEntryModel(), new CMSNavigationEntryModel());


	}

	private List<ItemModel> createContentPages()
	{

		final List<ItemModel> contentPages = new ArrayList<ItemModel>();

		firstContentPage = new ContentPageModel();
		firstContentPage.setUid("content_page_1");
		contentPages.add(firstContentPage);

		secondContentPage = new ContentPageModel();
		secondContentPage.setUid("content_page_2");
		contentPages.add(secondContentPage);

		thirdContentPage = new ContentPageModel();
		thirdContentPage.setUid("content_page_3");
		contentPages.add(thirdContentPage);
		return contentPages;
	}

	private List<CMSNavigationNodeModel> createNavigationNodes(final CatalogVersionModel catalogVersionModel)
	{

		final List<CMSNavigationNodeModel> navigationNodesChildren = new ArrayList<CMSNavigationNodeModel>();

		firstNavigationModel = new CMSNavigationNodeModel();
		firstNavigationModel.setChildren(Collections.EMPTY_LIST);
		firstNavigationModel.setUid("level_1_0");
		firstNavigationModel.setName("level_1_0");
		firstNavigationModel.setCatalogVersion(catalogVersionModel);
		firstNavigationModel.setParent(rootNavigationModel);
		firstNavigationModel.setChildren(Collections.EMPTY_LIST);
		firstNavigationModel.setEntries(Collections.EMPTY_LIST);
		navigationNodesChildren.add(firstNavigationModel);

		secondNavigationModel = new CMSNavigationNodeModel();
		secondNavigationModel.setUid("level_1_1");
		secondNavigationModel.setName("level_1_1");
		secondNavigationModel.setCatalogVersion(catalogVersionModel);
		secondNavigationModel.setParent(rootNavigationModel);
		secondNavigationModel.setChildren(Collections.EMPTY_LIST);
		secondNavigationModel.setEntries(wrapItemModel(createContentPages(), catalogVersionModel));
		navigationNodesChildren.add(secondNavigationModel);

		thirdNavigationModel = new CMSNavigationNodeModel();
		thirdNavigationModel.setUid("level_1_2");
		thirdNavigationModel.setName("level_1_2");
		thirdNavigationModel.setCatalogVersion(catalogVersionModel);
		thirdNavigationModel.setParent(rootNavigationModel);
		thirdNavigationModel.setChildren(Collections.EMPTY_LIST);
		thirdNavigationModel.setEntries(Collections.EMPTY_LIST);
		navigationNodesChildren.add(thirdNavigationModel);

		return navigationNodesChildren;
	}


	/**
	 * Test method for {@link DefaultCMSNavigationService#move(CMSNavigationNodeModel, CMSNavigationNodeModel)} .
	 */
	@Test
	public void shouldMoveCMSNavigationNodeModelCMSNavigationNodeModel()
	{
		cmsNavigationService.move(firstNavigationModel, thirdNavigationModel);

		assertThat(rootNavigationModel.getChildren()).isNotEmpty();
		assertThat(rootNavigationModel.getChildren()).hasSize(2);
		assertThat(rootNavigationModel.getChildren().get(0)).isEqualTo(secondNavigationModel);
		assertThat(rootNavigationModel.getChildren().get(1)).isEqualTo(thirdNavigationModel);
		assertThat(rootNavigationModel.getChildren().get(1).getChildren()).isNotEmpty();
		assertThat(rootNavigationModel.getChildren().get(1).getChildren().get(0)).isEqualTo(firstNavigationModel);
	}

	/**
	 * Test method for
	 * {@link DefaultCMSNavigationService#move(ItemModel, CMSNavigationNodeModel, CMSNavigationNodeModel)} .
	 */
	@Test
	public void shouldMoveCMSNavigationNodeModelContentPageModelContentPageModel()
	{
		cmsNavigationService.move(secondNavigationModel, firstContentPage, thirdContentPage);

		assertThat(secondNavigationModel.getEntries()).isNotEmpty();
		assertThat(secondNavigationModel.getEntries()).hasSize(3);
		assertThat(secondNavigationModel.getEntries().get(0).getItem()).isEqualTo(secondContentPage);
		assertThat(secondNavigationModel.getEntries().get(1).getItem()).isEqualTo(firstContentPage);
		assertThat(secondNavigationModel.getEntries().get(2).getItem()).isEqualTo(thirdContentPage);
	}

	/**
	 * Test method for
	 * {@link DefaultCMSNavigationService#move(ItemModel, CMSNavigationNodeModel, CMSNavigationNodeModel)} .
	 */
	@Test
	public void shouldMoveContentPageModelCMSNavigationNodeModelCMSNavigationNodeModel()
	{
		cmsNavigationService.move(firstContentPage, secondNavigationModel, firstNavigationModel);

		assertThat(firstNavigationModel.getEntries()).isNotEmpty();
		assertThat(firstNavigationModel.getEntries()).hasSize(1);
		assertThat(firstNavigationModel.getEntries().get(0).getItem()).isEqualTo(firstContentPage);

		assertThat(secondNavigationModel.getEntries()).isNotEmpty();
		assertThat(secondNavigationModel.getEntries()).hasSize(2);
		assertThat(extractCmsNavigationEntries(secondNavigationModel)).contains(secondContentPage, thirdContentPage);
	}

	/**
	 * Test method for {@link DefaultCMSNavigationService#addCmsNavigationEntry(ItemModel, CMSNavigationNodeModel)}.
	 */
	@Test
	public void shouldAddPage()
	{
		cmsNavigationService.addCmsNavigationEntry(firstContentPage, thirdNavigationModel);

		assertThat(thirdNavigationModel.getEntries()).isNotEmpty();
		assertThat(thirdNavigationModel.getEntries()).hasSize(1);
		assertThat(extractCmsNavigationEntries(thirdNavigationModel).contains(firstContentPage));
	}


	/**
	 * Test method for {@link DefaultCMSNavigationService#delete(CMSNavigationNodeModel)}.
	 */
	@Test
	public void shouldDeleteNavigationNode()
	{
		cmsNavigationService.delete(firstNavigationModel);

		assertThat(rootNavigationModel.getChildren()).isNotEmpty();
		assertThat(rootNavigationModel.getChildren()).hasSize(2);
		assertThat(rootNavigationModel.getChildren()).contains(secondNavigationModel, thirdNavigationModel);
	}


	/**
	 * Test method for {@link DefaultCMSNavigationService#remove(CMSNavigationNodeModel, ItemModel)}.
	 */
	@Test
	public void shouldRemovePageFromNavigationNode()
	{
		cmsNavigationService.remove(secondNavigationModel, firstContentPage);
		assertThat(secondNavigationModel.getEntries()).isNotEmpty();
		assertThat(secondNavigationModel.getEntries()).hasSize(2);
		assertThat(extractCmsNavigationEntries(secondNavigationModel)).contains(secondContentPage, thirdContentPage);
	}

	/**
	 * Wrap given items to become a {@link CMSNavigationEntryModel}.
	 * <p/>
	 */
	private List<CMSNavigationEntryModel> wrapItemModel(final Collection<ItemModel> items, final CatalogVersionModel catalogVersion)
	{
		final List<CMSNavigationEntryModel> wrappedEntries = new ArrayList<CMSNavigationEntryModel>();
		if (CollectionUtils.isEmpty(items))
		{
			return wrappedEntries;
		}

		for (final ItemModel item : items)
		{
			final CMSNavigationEntryModel cmsNavigationEntry = new CMSNavigationEntryModel();
			cmsNavigationEntry.setItem(item);
			cmsNavigationEntry.setUid(Long.toString(System.currentTimeMillis()));
			cmsNavigationEntry.setCatalogVersion(catalogVersion);
			wrappedEntries.add(cmsNavigationEntry);
		}
		return wrappedEntries;
	}


	/**
	 * Extracts items from given navigation node.
	 * 
	 * @param navigatioNode
	 *           given navigation node
	 */
	private List<ItemModel> extractCmsNavigationEntries(final CMSNavigationNodeModel navigatioNode)
	{
		final List<ItemModel> extractedEntries = new ArrayList<ItemModel>();
		if (navigatioNode == null)
		{
			return extractedEntries;
		}

		for (final CMSNavigationEntryModel entry : navigatioNode.getEntries())
		{
			extractedEntries.add(entry.getItem());
		}
		return extractedEntries;
	}


}
