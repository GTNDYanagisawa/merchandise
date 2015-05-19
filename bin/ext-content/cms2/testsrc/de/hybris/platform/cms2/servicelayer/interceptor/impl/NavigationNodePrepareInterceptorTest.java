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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;


public class NavigationNodePrepareInterceptorTest extends ServicelayerTransactionalTest
{

	@Resource
	private ModelService modelService;

	@Test
	public void catalogVersionChange()
	{
		final CatalogModel prepareCatalog = prepareCatalog("nnTestCatalog");
		final CatalogVersionModel staged = prepareCatalogVersion(prepareCatalog, "Staged");
		final CatalogVersionModel online = prepareCatalogVersion(prepareCatalog, "Online");

		final CMSNavigationNodeModel rootStaged = prepareHiddenRootNavigationNode(staged);
		final CMSNavigationNodeModel rootOnline = prepareHiddenRootNavigationNode(online);

		final CMSNavigationNodeModel node1 = prepareNavigationNode("node1", rootStaged);
		node1.setCatalogVersion(online);
		modelService.save(node1);

		Assert.assertEquals(node1.getCatalogVersion(), online);
		Assert.assertEquals(node1.getParent(), rootOnline);
		Assert.assertEquals(node1.getParent().getCatalogVersion(), online);
	}

	@Test
	public void catalogVersionChangeWithChildren()
	{
		final CatalogModel prepareCatalog = prepareCatalog("nnTestCatalog");
		final CatalogVersionModel staged = prepareCatalogVersion(prepareCatalog, "Staged");
		final CatalogVersionModel online = prepareCatalogVersion(prepareCatalog, "Online");

		final CMSNavigationNodeModel rootStaged = prepareHiddenRootNavigationNode(staged);
		final CMSNavigationNodeModel rootOnline = prepareHiddenRootNavigationNode(online);

		final CMSNavigationNodeModel node1 = prepareNavigationNode("node1", rootStaged);
		final CMSNavigationNodeModel node2 = prepareNavigationNode("node2", node1);
		final CMSNavigationNodeModel node3 = prepareNavigationNode("node3", node2);

		node1.setCatalogVersion(online);
		modelService.save(node1);

		Assert.assertEquals(node1.getCatalogVersion(), online);
		Assert.assertEquals(node1.getParent(), rootOnline);
		Assert.assertEquals(node1.getParent().getCatalogVersion(), online);

		Assert.assertEquals(node2.getCatalogVersion(), online);
		Assert.assertEquals(node2.getParent(), node1);
		Assert.assertEquals(node2.getParent().getCatalogVersion(), online);

		Assert.assertEquals(node3.getCatalogVersion(), online);
		Assert.assertEquals(node3.getParent(), node2);
		Assert.assertEquals(node3.getParent().getCatalogVersion(), online);
	}

	@Test
	public void parentChange()
	{
		final CatalogModel prepareCatalog = prepareCatalog("nnTestCatalog");
		final CatalogVersionModel staged = prepareCatalogVersion(prepareCatalog, "Staged");
		final CatalogVersionModel online = prepareCatalogVersion(prepareCatalog, "Online");

		final CMSNavigationNodeModel rootStaged = prepareHiddenRootNavigationNode(staged);
		final CMSNavigationNodeModel rootOnline = prepareHiddenRootNavigationNode(online);

		final CMSNavigationNodeModel node1 = prepareNavigationNode("node1", rootStaged);
		node1.setParent(rootOnline);
		modelService.save(node1);

		Assert.assertEquals(node1.getCatalogVersion(), online);
		Assert.assertEquals(node1.getParent(), rootOnline);
		Assert.assertEquals(node1.getParent().getCatalogVersion(), online);
	}

	@Test
	public void parentChangeWithChildren()
	{
		final CatalogModel prepareCatalog = prepareCatalog("nnTestCatalog");
		final CatalogVersionModel staged = prepareCatalogVersion(prepareCatalog, "Staged");
		final CatalogVersionModel online = prepareCatalogVersion(prepareCatalog, "Online");

		final CMSNavigationNodeModel rootStaged = prepareHiddenRootNavigationNode(staged);
		final CMSNavigationNodeModel rootOnline = prepareHiddenRootNavigationNode(online);

		final CMSNavigationNodeModel node1 = prepareNavigationNode("node1", rootStaged);
		final CMSNavigationNodeModel node2 = prepareNavigationNode("node2", node1);
		final CMSNavigationNodeModel node3 = prepareNavigationNode("node3", node2);

		node1.setParent(rootOnline);
		modelService.save(node1);

		Assert.assertEquals(node1.getCatalogVersion(), online);
		Assert.assertEquals(node1.getParent(), rootOnline);
		Assert.assertEquals(node1.getParent().getCatalogVersion(), online);

		Assert.assertEquals(node2.getCatalogVersion(), online);
		Assert.assertEquals(node2.getParent(), node1);
		Assert.assertEquals(node2.getParent().getCatalogVersion(), online);

		Assert.assertEquals(node3.getCatalogVersion(), online);
		Assert.assertEquals(node3.getParent(), node2);
		Assert.assertEquals(node3.getParent().getCatalogVersion(), online);
	}

	private CMSNavigationNodeModel prepareNavigationNode(final String name, final CMSNavigationNodeModel parent)
	{
		final CMSNavigationNodeModel node = modelService.create(CMSNavigationNodeModel.class);
		node.setName(name);
		node.setCatalogVersion(parent.getCatalogVersion());
		node.setUid(name + System.currentTimeMillis());
		node.setVisible(false);
		node.setParent(parent);
		modelService.save(node);
		return node;
	}

	public CatalogModel prepareCatalog(final String id)
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId(id);
		modelService.save(catalog);
		return catalog;
	}

	public CatalogVersionModel prepareCatalogVersion(final CatalogModel catalog, final String versionName)
	{
		final CatalogVersionModel version = modelService.create(CatalogVersionModel.class);
		version.setCatalog(catalog);
		version.setVersion(versionName);
		modelService.save(version);
		return version;
	}

	private CMSNavigationNodeModel prepareHiddenRootNavigationNode(final CatalogVersionModel catalogVersion)
	{
		final CMSNavigationNodeModel rootNaviNodeModel = modelService.create(CMSNavigationNodeModel.class);
		rootNaviNodeModel.setName("CMSNavigationNode");
		rootNaviNodeModel.setCatalogVersion(catalogVersion);
		rootNaviNodeModel.setUid("ROOT");
		rootNaviNodeModel.setVisible(false);
		modelService.save(rootNaviNodeModel);
		return rootNaviNodeModel;
	}

}
