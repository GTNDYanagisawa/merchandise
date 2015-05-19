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
package com.hybris.instore.widgets.sort;


import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.testframework.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zkoss.zk.device.AjaxDevice;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.ui.Component;
import org.zkoss.zml.device.XmlDevice;
import org.zkoss.zul.Button;
import org.zkoss.zul.Popup;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


/**
 * @author johannesdoberer
 * 
 */
@NullSafeWidget(value = false)
@DeclaredInput(socketType = List.class, value = SortController.SOCKET_IN_SORTOPTIONS)
public class SortControllerTest extends AbstractWidgetUnitTest<SortController>
{

	@InjectMocks
	private final SortController controller = new SortController();

	@SuppressWarnings("unused")
	@Mock
	private Button sortbutton;
	@SuppressWarnings("unused")
	@Mock
	private Popup sortoptionspopup;


	@Override
	protected SortController getWidgetController()
	{
		return controller;
	}


	@Before
	public void setUp()
	{
		// ZK device setup
		if (!Devices.exists("ajax"))
		{
			Devices.add("ajax", AjaxDevice.class);
		}
		if (!Devices.exists("xml"))
		{
			Devices.add("xml", XmlDevice.class);
		}
	}


	/**
	 * Check if something has been added to the sortoption popup after a socket input
	 */
	@Test
	public void chooseSortOptionsTest()
	{
		final List<SortData> sortOptions = Arrays.asList(new SortData(), new SortData());
		controller.chooseSortOptions(sortOptions);
		Mockito.verify(sortoptionspopup, Mockito.atLeastOnce()).appendChild(Mockito.any(Component.class));
		Assert.assertCollection(sortOptions, controller.getValue(SortController.MODEL_SORTOPTIONLIST, Collection.class));
	}
}
