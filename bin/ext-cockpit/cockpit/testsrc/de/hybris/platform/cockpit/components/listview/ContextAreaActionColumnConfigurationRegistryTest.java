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
package de.hybris.platform.cockpit.components.listview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.platform.cockpit.components.listview.impl.DefaultActionColumnConfiguration;
import de.hybris.platform.cockpit.components.listview.impl.DeleteCommentAction;
import de.hybris.platform.cockpit.model.meta.ExtendedType;
import de.hybris.platform.cockpit.model.meta.impl.DefaultObjectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


/**
 *
 */
public class ContextAreaActionColumnConfigurationRegistryTest
{

	@Test
	public void testGetActionColumnConfigurationForType()
	{
		ActionColumnConfiguration actions = null;
		final ContextAreaActionColumnConfigurationRegistry registry = new ContextAreaActionColumnConfigurationRegistry();
		actions = registry.getActionColumnConfigurationForType(null);
		assertNull(actions);

		final Map<String, ActionColumnConfiguration> testMap = new HashMap<String, ActionColumnConfiguration>();
		registry.setMap(testMap);
		actions = registry.getActionColumnConfigurationForType(null);
		assertNull(actions);

		final AbstractListViewAction[] testActionsArray =
		{ new DeleteCommentAction() };
		final DefaultActionColumnConfiguration testActions = new DefaultActionColumnConfiguration("TestActions");
		testActions.setActions(Arrays.asList(testActionsArray));
		final TestType testType = new TestType("ObjType");

		testMap.put(testType.getCode(), testActions);
		actions = registry.getActionColumnConfigurationForType(testType);
		assertEquals(testActions, actions);

		testMap.remove(testType.getCode());
		final TestType testTypeParent = new TestType("ParentObjType");
		testType.setSupertypes(Collections.singleton(testTypeParent));
		testMap.put(testTypeParent.getCode(), testActions);

		// we check with testType again, to make sure parents are traversed
		actions = registry.getActionColumnConfigurationForType(testType);
		assertEquals(testActions, actions);

		final TestType invalidType = new TestType("InvalidType");
		actions = registry.getActionColumnConfigurationForType(invalidType);
		assertNull(actions);

		// testMap already has Parent, also add GrandParent into it.
		// we check if we look for Parent, that we definitely get Parent, not GrandParent
		final TestType testTypeGrandParent = new TestType("GrandParentObjType");
		testTypeParent.setSupertypes(Collections.singleton(testTypeGrandParent));

		final DefaultActionColumnConfiguration testActionsGrandParent = new DefaultActionColumnConfiguration("TestActions");
		testMap.put(testTypeGrandParent.getCode(), testActionsGrandParent);

		actions = registry.getActionColumnConfigurationForType(testTypeGrandParent);
		assertEquals(testActionsGrandParent, actions);

		actions = registry.getActionColumnConfigurationForType(testTypeParent);
		assertEquals(testActions, actions);

		actions = registry.getActionColumnConfigurationForType(testType);
		assertEquals(testActions, actions);
	}

	private class TestType extends DefaultObjectType implements ExtendedType
	{

		public TestType(final String code)
		{
			super(code);
		}

		@Override
		public boolean isAbstract()
		{
			return false;
		}

		@Override
		public String getName()
		{
			return "foo";
		}

		@Override
		public String getName(final String languageIsoCode)
		{
			return getName();
		}

		@Override
		public String getDescription()
		{
			return null;
		}

		@Override
		public String getDescription(final String languageIsoCode)
		{
			return null;
		}

		@Override
		public String toString()
		{
			return getCode();
		}
	}
}
