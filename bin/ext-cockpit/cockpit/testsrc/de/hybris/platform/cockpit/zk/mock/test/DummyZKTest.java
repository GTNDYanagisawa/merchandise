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
package de.hybris.platform.cockpit.zk.mock.test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cockpit.components.mvc.tree.Tree;
import de.hybris.platform.cockpit.zk.mock.DummyExecution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.ExecutionsCtrl;
import org.zkoss.zul.SimpleTreeModel;
import org.zkoss.zul.SimpleTreeNode;
import org.zkoss.zul.Treeitem;


/**
 * Just a POC of zk mocking
 * 
 * @author Jacek
 */
@UnitTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "classpath:/cockpit/cockpit-junit-spring.xml" })
public class DummyZKTest
{
	@Autowired
	private ApplicationContext applicationContext;

	@Before
	public void setUp() throws Exception
	{
		initDummyZkAppContext();
	}

	private void initDummyZkAppContext() throws Exception
	{
		ExecutionsCtrl.setCurrent(new DummyExecution(applicationContext));
	}

	@Test
	public void testNavigationNodeController() throws Exception
	{
		/* 1. we can use SpringUtils ! so object under the test also can use it! */
		final TreeControllerMock treeController = (TreeControllerMock) SpringUtil.getBean("treeControllerMock");

		/* 2. creating zk components will no break the test - e.g. the tree which extends zk tree: */
		final Tree tree = new Tree(treeController);
		final SimpleTreeNode root = createRoot();
		final SimpleTreeModel stm = new SimpleTreeModel(root);
		tree.setModel(stm);
		final List<Treeitem> treeitems = tree.getTreechildren().getChildren();
		final Treeitem node3 = treeitems.get(2);
		tree.setSelectedItem(node3);

		/* 3. sending events will not break the test (but events are not thrown) */
		Events.sendEvent(new Event(Events.ON_SELECT, tree));

		/* 4. if execution is here and test was not broken, we can be tempted to use these mocks - any comments ? */
		Assert.assertTrue(true);
	}

	/**
	 * tree dummy data
	 */
	private SimpleTreeNode createRoot()
	{
		final List children = new ArrayList();
		children.add(new SimpleTreeNode("node1", Collections.EMPTY_LIST));
		children.add(new SimpleTreeNode("node2", Collections.EMPTY_LIST));
		children.add(new SimpleTreeNode("node3", Collections.EMPTY_LIST));
		final SimpleTreeNode root = new SimpleTreeNode("node0", children);
		return root;
	}
}
