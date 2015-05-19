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

import de.hybris.platform.cockpit.components.mvc.tree.Tree;
import de.hybris.platform.cockpit.components.mvc.tree.TreeController;

import java.util.Set;

import org.zkoss.zk.ui.event.Event;


/**
 * Just an empty mock, see {@link DummyZKTest}
 */
public class TreeControllerMock implements TreeController<Object>
{

	@Override
	public Set<Object> getSelected()
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void selected(final Tree tree, final Set selectedItems)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void move(final Tree tree, final Object node, final Object target, final boolean addAsChild)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void add(final Tree tree, final Object object, final Object target, final int index)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void add(final Tree tree, final Object object, final Object target)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public Object create(final Tree tree, final Object target)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public Object create(final Tree tree, final Object target, final int index)
	{
		// YTODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(final Tree tree, final Object node)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public void doubleClicked(final Tree tree, final Object node)
	{
		// YTODO Auto-generated method stub

	}

	@Override
	public Object customAction(final Tree tree, final Event event, final Object node)
	{
		// YTODO Auto-generated method stub
		return null;
	}

}
