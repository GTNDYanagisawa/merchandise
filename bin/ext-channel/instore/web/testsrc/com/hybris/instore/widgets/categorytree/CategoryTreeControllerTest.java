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
package com.hybris.instore.widgets.categorytree;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvents;


/**
 * @author ciprianosanchez
 * 
 */
@DeclaredInputs(
{ @DeclaredInput(socketType = CategoryHierarchyData.class, value = CategoryTreeController.SOCKET_IN_CATEGORY),
		@DeclaredInput(socketType = Boolean.class, value = CategoryTreeController.SOCKET_IN_RESET) })
@DeclaredViewEvents(@DeclaredViewEvent(componentID = CategoryTreeController.COMPID_LIST_BOX, eventName = Events.ON_SELECT))
public class CategoryTreeControllerTest extends AbstractWidgetUnitTest<CategoryTreeController>
{
	@InjectMocks
	private final CategoryTreeController controller = new CategoryTreeController();

	@SuppressWarnings("unused")
	@Mock
	private CatalogVersionData catalogVersionData;
	@SuppressWarnings("unused")
	@Mock
	private CatalogModel catalog;
	@SuppressWarnings("unused")
	@Mock
	private CatalogVersionModel activeCatalogVersion;
	@SuppressWarnings("unused")
	@Mock
	private CatalogFacade catalogFacade;
	@SuppressWarnings("unused")
	@Mock
	private Listbox listBox;

	@SuppressWarnings("unused")
	@Mock
	private ListModel listmodel;

	@SuppressWarnings("unused")
	@Mock
	private CatalogVersionService catalogVersionService;

	@Override
	protected CategoryTreeController getWidgetController()
	{
		return controller;
	}

	private List<CategoryHierarchyData> categoryHierarchyDataList;

	@Before
	public void setUp()
	{
		listmodel = null;
		Mockito.doAnswer(new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				return listmodel = (ListModel) invocation.getArguments()[0];
			}
		}).when(listBox).setModel(Mockito.any(ListModel.class));

		final CategoryHierarchyData categoryHierarchyData1 = new CategoryHierarchyData();
		categoryHierarchyData1.setId("categoryHierarchyDataD");
		categoryHierarchyData1.setName("categoryHierarchyDataD");
		final CategoryHierarchyData categoryHierarchyData2 = new CategoryHierarchyData();
		categoryHierarchyData2.setId("categoryHierarchyDataA");
		categoryHierarchyData2.setName("categoryHierarchyDataA");
		final CategoryHierarchyData categoryHierarchyData3 = new CategoryHierarchyData();
		categoryHierarchyData3.setId("categoryHierarchyDataC");
		categoryHierarchyData3.setName("categoryHierarchyDataC");
		final CategoryHierarchyData categoryHierarchyData4 = new CategoryHierarchyData();
		categoryHierarchyData4.setId("categoryHierarchyDataB");
		categoryHierarchyData4.setName("categoryHierarchyDataB");

		categoryHierarchyDataList = new ArrayList<CategoryHierarchyData>();
		categoryHierarchyDataList.add(categoryHierarchyData1);
		categoryHierarchyDataList.add(categoryHierarchyData2);
		categoryHierarchyDataList.add(categoryHierarchyData3);
		categoryHierarchyDataList.add(categoryHierarchyData4);

		controller.setCategoryHierarchyTreeComparator(new AlphaNumericCategoryHierarchyTreeComparator());

		final CatalogVersionModel catalogVersionMock = Mockito.mock(CatalogVersionModel.class);
		final CatalogModel catalogMock = Mockito.mock(CatalogModel.class);
		Mockito.when(catalogVersionMock.getCatalog()).thenReturn(catalogMock);
		Mockito.when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Collections.singletonList(catalogVersionMock));
	}

	@Test
	public void initializeTest()
	{
		final CatalogVersionData mockCatalogVersionData = new CatalogVersionData();
		mockCatalogVersionData.setCategoriesHierarchyData(categoryHierarchyDataList);

		Mockito.when(
				catalogFacade.getProductCatalogVersionForTheCurrentSite(Mockito.anyString(), Mockito.anyString(), Mockito.anySet()))
				.thenReturn(mockCatalogVersionData);

		controller.initialize(Mockito.mock(Component.class));

		Assert.assertNotNull(listmodel);
		Assert.assertEquals(listmodel.getSize(), 4);
		Assert.assertEquals(((CategoryHierarchyData) listmodel.getElementAt(0)).getName(), "categoryHierarchyDataD");
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(CategoryTreeController.SOCKET_OUT_SELECTEDCATEGORY), Mockito.eq(null));
	}

	@Test
	public void selectCategoryTest()
	{
		/*
		 * catalogHierarchyData has no subcategories
		 */
		final CategoryHierarchyData mockCategoryHierarchyData1 = new CategoryHierarchyData();
		Mockito.when(
				catalogFacade.getCategoryById(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
						Mockito.any(PageOption.class), Mockito.anySet())).thenReturn(mockCategoryHierarchyData1);
		Assert.assertNull(mockCategoryHierarchyData1.getSubcategories());
		controller.selectCategory(mockCategoryHierarchyData1);

		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(CategoryTreeController.SOCKET_OUT_SELECTEDCATEGORY), Mockito.eq(mockCategoryHierarchyData1));


		/*
		 * catalogHierarchyData has subcategories
		 */
		final CategoryHierarchyData mockCategoryHierarchyData2 = new CategoryHierarchyData();
		mockCategoryHierarchyData2.setSubcategories(categoryHierarchyDataList);
		Mockito.when(
				catalogFacade.getCategoryById(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
						Mockito.any(PageOption.class), Mockito.anySet())).thenReturn(mockCategoryHierarchyData2);


		controller.selectCategory(mockCategoryHierarchyData2);
		Assert.assertNotNull(listmodel);
		Assert.assertEquals(listmodel.getSize(), 4);
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(CategoryTreeController.SOCKET_OUT_SELECTEDCATEGORY), Mockito.eq(mockCategoryHierarchyData2));
	}


	@Test
	public void updateAndSortListBoxTest()
	{
		controller.updateListBox(categoryHierarchyDataList);
		Assert.assertEquals("categoryHierarchyDataD", ((CategoryHierarchyData) listmodel.getElementAt(0)).getId());

		/*
		 * Set the widgetsetting 'custom sort' to TRUE the list will sorted alphanumeric
		 */
		widgetInstanceManager.getWidgetSettings().put(CategoryTreeController.SETTING_CUSTOM_SORTING, Boolean.TRUE);
		controller.updateListBox(categoryHierarchyDataList);
		Assert.assertEquals("categoryHierarchyDataA", ((CategoryHierarchyData) listmodel.getElementAt(0)).getId());

		/*
		 * Check if the listmodel is updated
		 */
		Assert.assertNotNull(listmodel);
		Assert.assertEquals(listmodel.getSize(), 4);
	}
}