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
package com.hybris.instore.widgets.pageablelist;

import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zkoss.zk.device.AjaxDevice;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.http.ExecutionImpl;
import org.zkoss.zml.device.XmlDevice;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvents;


/**
 * @author johannesdoberer
 * 
 */
@DeclaredInputs(
{ @DeclaredInput(socketType = Pageable.class, value = PageableListController.SOCKET_IN_PAGEABLE),
		@DeclaredInput(socketType = List.class, value = PageableListController.SOCKET_IN_PAGEABLELIST) })
@DeclaredViewEvents(
{ @DeclaredViewEvent(componentID = PageableListController.COMP_ID_GRIDLIST, eventName = Events.ON_SELECT) })
public class PageableListControllerTest extends AbstractWidgetUnitTest<PageableListController>
{
	@InjectMocks
	private final PageableListController pageableListController = new PageableListController();

	@SuppressWarnings("unused")
	@Mock
	private Component gridContainer;
	@SuppressWarnings("unused")
	@Mock
	private Listbox gridList;
	@Mock
	private ExecutionImpl executionMock;
	@Mock
	private Desktop desktopMock;

	@Override
	protected PageableListController getWidgetController()
	{
		return pageableListController;
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

		Mockito.when(executionMock.getDesktop()).thenReturn(desktopMock);
	}

	@Test
	public void getAvailabilityTest()
	{
		final ProductData product = new ProductData();
		product.setAvailableInCurrentStore(Boolean.TRUE);
		product.setAvailableForPickup(Boolean.TRUE);
		Assert.assertEquals(pageableListController.getAvailableInEnterprise(product), Boolean.TRUE);
		Assert.assertEquals(pageableListController.getAvailableInStoreFlag(product), Boolean.TRUE);
	}

	@Test
	public void createShowMoreButtonTest()
	{
		final Pageable pageable = new PageableList<Object>(Collections.emptyList(), 2)
		{
			@Override
			public boolean hasNextPage()
			{
				return true;
			}
		};
		final Component component = Mockito.mock(Component.class);
		final Button button = pageableListController.createShowMoreButton(pageable, component);
		Assert.assertEquals(button.isVisible(), true);
	}

}
