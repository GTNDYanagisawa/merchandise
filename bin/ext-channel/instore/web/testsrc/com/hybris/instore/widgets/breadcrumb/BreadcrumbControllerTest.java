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
package com.hybris.instore.widgets.breadcrumb;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zkoss.zk.device.AjaxDevice;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.ui.Component;
import org.zkoss.zml.device.XmlDevice;
import org.zkoss.zul.Hbox;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


/**
 * @author ciprianosanchez
 * 
 */
@NullSafeWidget(value = false)
@DeclaredInputs(
{ @DeclaredInput(socketType = ProductData.class, value = BreadcrumbController.SOCKET_IN_PRODUCT),
		@DeclaredInput(socketType = CategoryHierarchyData.class, value = BreadcrumbController.SOCKET_IN_CATEGORY),
		@DeclaredInput(socketType = Object.class, value = BreadcrumbController.SOCKET_IN_PIN_PRODUCT),
		@DeclaredInput(socketType = String.class, value = BreadcrumbController.SOCKET_IN_RESET) })
public class BreadcrumbControllerTest extends AbstractWidgetUnitTest<BreadcrumbController>
{
	@InjectMocks
	private final BreadcrumbController controller = new BreadcrumbController();

	@InjectMocks
	private final List<CategoryHierarchyData> categoryNavigation = new ArrayList<CategoryHierarchyData>();

	@SuppressWarnings("unused")
	@Mock
	private ProductData currentProduct;
	@SuppressWarnings("unused")
	@Mock
	private Hbox breadcrumbContainer;

	@Override
	protected BreadcrumbController getWidgetController()
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

	@Test
	public void addHomeCategoryTest()
	{
		final List breadcrumbChildrenMock = Mockito.mock(List.class);
		Mockito.when(breadcrumbContainer.getChildren()).thenReturn(breadcrumbChildrenMock);

		//add home to the breadcrumb
		controller.selectCategory(null);
		Mockito.verify(breadcrumbChildrenMock, Mockito.atLeastOnce()).clear();
		Mockito.verify(breadcrumbContainer, Mockito.times(1)).appendChild(Mockito.any(Component.class));
		assertEquals(categoryNavigation.size(), 0);
	}

	@Test
	public void addOneCategoryTest()
	{
		final List categoryNavigation = Mockito.mock(List.class);

		final CategoryHierarchyData cat = new CategoryHierarchyData();
		cat.setId("cat1");
		cat.setName("category 1");
		categoryNavigation.add(cat);

		controller.selectCategory(cat);

		Mockito.verify(categoryNavigation, Mockito.atLeastOnce()).add(Mockito.any(CategoryHierarchyData.class));

		//Verify 3 ZK Components are added t the breadcrumb Home, Separator and the actual category
		Mockito.verify(breadcrumbContainer, Mockito.times(3)).appendChild(Mockito.any(Component.class));
	}

	@Test
	public void existingCategoryInBredcrumb()
	{
		final List categoryNavigation = new ArrayList<CategoryHierarchyData>();

		final CategoryHierarchyData cat1 = new CategoryHierarchyData();
		final CategoryHierarchyData cat2 = new CategoryHierarchyData();
		final CategoryHierarchyData cat3 = new CategoryHierarchyData();
		cat1.setId("cat1");
		cat1.setName("category 1");
		cat2.setId("cat2");
		cat2.setName("Category 2");
		cat3.setId("cat3");
		cat3.setName("Category 3");
		categoryNavigation.add(cat1);
		categoryNavigation.add(cat2);
		categoryNavigation.add(cat3);

		// Sending an existing element to the Bredcrumb should return an int >= 0  
		Assert.assertTrue(controller.getIndexOf(categoryNavigation, cat2) >= 0);
		Assert.assertTrue(controller.getIndexOf(categoryNavigation, new CategoryHierarchyData()) < 0);

	}
}
