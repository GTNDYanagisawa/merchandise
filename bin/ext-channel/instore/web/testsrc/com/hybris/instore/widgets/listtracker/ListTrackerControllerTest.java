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
package com.hybris.instore.widgets.listtracker;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.hybris.cockpitng.components.Widgetslot;
import com.hybris.cockpitng.core.model.ModelValueHandler;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;


@DeclaredInputs(
{ @DeclaredInput(socketType = List.class, value = ListTrackerController.SOCKET_IN_LIST),
		@DeclaredInput(socketType = Integer.class, value = ListTrackerController.SOCKET_IN_ACTIVE_INDEX),
		@DeclaredInput(socketType = Object.class, value = ListTrackerController.SOCKET_IN_FORWARD),
		@DeclaredInput(socketType = Object.class, value = ListTrackerController.SOCKET_IN_BACKWARD) })
@UnitTest
public class ListTrackerControllerTest extends AbstractWidgetUnitTest<ListTrackerController>
{
	private final ListTrackerController controller = new ListTrackerController();

	@SuppressWarnings("unused")
	@Mock
	private ModelValueHandler modelValueHandler;

	@Override
	protected ListTrackerController getWidgetController()
	{
		return controller;
	}

	@Before
	public void setUp()
	{
		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				widgetModel.put(String.valueOf(invocation.getArguments()[0]), invocation.getArguments()[1]);
				//return widgetModel;
				return null;
			}
		}).when(modelValueHandler).setValue(Mockito.any(), Mockito.anyString(), Mockito.anyString());

		controller.initialize(Mockito.mock(Widgetslot.class));
		final List<Integer> testList = initTestList();
		controller.setList(testList);
	}

	@Test
	public void testInitialState()
	{
		// make sure next/previous nav is not possible
		Assert.assertTrue(BooleanUtils.isNotTrue((Boolean) widgetModel.get(ListTrackerController.MODEL_NEXT_AVAILBLE)));
		Assert.assertTrue(BooleanUtils.isNotTrue((Boolean) widgetModel.get(ListTrackerController.MODEL_PREVIOUS_AVAILBLE)));

		// make sure list is null or empty
		Assert.assertTrue(CollectionUtils.isEmpty((Collection) widgetModel.get(ListTrackerController.MODEL_LIST)));
	}

	protected List<Integer> initTestList()
	{
		final List<Integer> nrList = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++)
		{
			nrList.add(Integer.valueOf(i));
		}

		return nrList;
	}

	@Test
	public void testSetList()
	{
		final List<Integer> testList = initTestList();
		controller.setList(testList);
		final List<Integer> tempList = controller.getValue(ListTrackerController.MODEL_LIST, List.class);
		Assert.assertTrue(testList.equals(tempList));
	}

	@Test
	public void testActiveIndex()
	{
		final Object modelList = controller.getValue(ListTrackerController.MODEL_LIST, Object.class);
		Assert.assertTrue(modelList instanceof List);
		Assert.assertTrue(((List) modelList).size() == 10);
		Assert.assertTrue(Integer.valueOf(0).equals(controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class)));
		controller.setActiveIndex(Integer.valueOf(1));
		Assert.assertTrue(Integer.valueOf(1).equals(controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class)));

		// make sure out of bounds is covered (illegal value should be ignored)
		controller.setActiveIndex(Integer.valueOf(10));
		Assert.assertTrue(Integer.valueOf(10).equals(controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class)));
	}

	@Test
	public void testActivateNext()
	{
		final Integer activeIndex = controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class);
		controller.activateNext();
		final Integer newIndex = controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class);
		Assert.assertNotNull(activeIndex);
		Assert.assertNotNull(newIndex);
		Assert.assertTrue((activeIndex.intValue() + 1) == newIndex.intValue());

		// make sure 'next' is disabled when there are no more elements in list
		controller.setActiveIndex(Integer.valueOf(9));
		Assert.assertTrue(Integer.valueOf(9).equals(controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class)));
		controller.activateNext();
		Assert.assertTrue(Integer.valueOf(9).equals(controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class)));
	}

	@Test
	public void testActivatePrevious()
	{
		final Integer activeIndex = controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class);
		controller.activatePrevious();
		final Integer newIndex = controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class);
		Assert.assertNotNull(activeIndex);
		Assert.assertNotNull(newIndex);
		Assert.assertEquals(activeIndex.intValue(), newIndex.intValue());

		// make sure 'previous' is disabled when there are no more elements in list
		controller.setActiveIndex(Integer.valueOf(0));
		Assert.assertTrue(Integer.valueOf(0).equals(controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class)));
		controller.activatePrevious();
		Assert.assertTrue(Integer.valueOf(0).equals(controller.getValue(ListTrackerController.MODEL_ACTIVE_INDEX, Integer.class)));
	}
}
