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
package com.hybris.instore.widgets.facetfilter;

import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zkoss.zk.device.AjaxDevice;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zml.device.XmlDevice;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


/**
 * @author johannesdoberer
 * 
 */
@NullSafeWidget(value = false)
@DeclaredInput(socketType = ProductSearchPageData.class, value = FacetFilterController.SOCKET_IN_SMALLSEARCHRESULT)
@DeclaredViewEvent(componentID = FacetFilterController.COMP_ID_APPLY_BTN, eventName = Events.ON_CLICK)
public class FacetFilterControllerTest extends AbstractWidgetUnitTest<FacetFilterController>
{
	@InjectMocks
	private final FacetFilterController facetFilterController = new FacetFilterController();

	@SuppressWarnings("unused")
	@Mock
	private Div facetContainer;
	@SuppressWarnings("unused")
	@Mock
	private Tabs tabContainer;
	@SuppressWarnings("unused")
	@Mock
	private Tabbox tabbox;

	private BreadcrumbData<SearchStateData> breadcrumb;
	private FacetData<SearchStateData> facet1;
	private List<BreadcrumbData<SearchStateData>> breadcrumbList;
	private Map<String, FacetData<SearchStateData>> mockMap;

	@Override
	protected FacetFilterController getWidgetController()
	{
		return facetFilterController;
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

		breadcrumb = new BreadcrumbData();
		breadcrumb.setFacetValueName("facet1");
		breadcrumb.setFacetCode("facet1");
		final BreadcrumbData breadcrumb2 = new BreadcrumbData();
		breadcrumb2.setFacetValueName("test2");

		breadcrumbList = new ArrayList<BreadcrumbData<SearchStateData>>();
		breadcrumbList.add(breadcrumb);
		breadcrumbList.add(breadcrumb2);

		final FacetValueData<SearchStateData> facetValueData1 = new FacetValueData<SearchStateData>();
		facetValueData1.setCode("facetValueData1");
		facetValueData1.setName("facetValueData1");
		final FacetValueData<SearchStateData> facetValueData2 = new FacetValueData<SearchStateData>();
		facetValueData2.setCode("facetValueData2");
		facetValueData2.setName("facetValueData2");
		final FacetValueData<SearchStateData> facetValueData3 = new FacetValueData<SearchStateData>();
		facetValueData3.setCode("facetValueData3");
		facetValueData3.setName("facetValueData3");
		final List<FacetValueData<SearchStateData>> facetValueList = new ArrayList<FacetValueData<SearchStateData>>();
		facetValueList.add(facetValueData1);
		facetValueList.add(facetValueData2);
		facetValueList.add(facetValueData3);


		facet1 = new FacetData<SearchStateData>();
		facet1.setCode("facet1");
		facet1.setValues(facetValueList);
		final FacetData<SearchStateData> facet2 = new FacetData<SearchStateData>();
		facet2.setCode("facet2");
		final FacetData<SearchStateData> facet3 = new FacetData<SearchStateData>();
		facet3.setCode("facet3");
		mockMap = new HashMap<String, FacetData<SearchStateData>>();
		mockMap.put("facet1", facet1);
		mockMap.put("facet2", facet2);
		mockMap.put("facet3", facet3);
	}

	@Test
	public void createBreadcrumbTest()
	{
		final Div divMock = (Div) facetFilterController.createBreadcrumb(breadcrumb);
		final Label label = (Label) divMock.getChildren().iterator().next();
		Assert.assertEquals("facet1", label.getValue());
	}

	@Test
	public void removeBreadcrumbTest()
	{
		facetFilterController.setValue(FacetFilterController.BREADCRUMB_LIST_MODEL_KEY, breadcrumbList);
		facetFilterController.setValue(FacetFilterController.CURRENTFACET_LIST_MODEL_KEY, mockMap);
		Assert.assertEquals(breadcrumbList.size(), 2);
		facetFilterController.removeBreadcrumb(breadcrumb);
		//test the breadcrumbList after removeBreadcrumb method was called
		Assert.assertEquals(breadcrumbList.size(), 1);
		//test the output
		final List<FacetData<SearchStateData>> mockList = new ArrayList<FacetData<SearchStateData>>(mockMap.values());
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(FacetFilterController.SOCKET_OUT_SELECTEDFACETS), Mockito.eq(mockList));
	}

	@Test
	public void removeAllBreadcrumbsTest()
	{
		facetFilterController.setValue(FacetFilterController.BREADCRUMB_LIST_MODEL_KEY, breadcrumbList);
		Assert.assertEquals(breadcrumbList.size(), 2);
		facetFilterController.removeAllBreadCrumbs();
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(FacetFilterController.SOCKET_OUT_SELECTEDFACETS), Mockito.eq(Collections.EMPTY_LIST));
	}

	@Test
	public void sendFacetValueTest()
	{
		Assert.assertEquals(mockMap.size(), 3);
		facetFilterController.setValue(FacetFilterController.CURRENTFACET_LIST_MODEL_KEY, mockMap);

		final FacetData<SearchStateData> mockFacet = Mockito.mock(FacetData.class);
		final FacetValueData<SearchStateData> mockFacetValue = Mockito.mock(FacetValueData.class);

		facetFilterController.sendFacetValue(mockFacet, mockFacetValue);
		mockMap = facetFilterController.getValue(FacetFilterController.CURRENTFACET_LIST_MODEL_KEY, Map.class);
		Assert.assertEquals(mockMap.size(), 4);
		final List<FacetData<SearchStateData>> mockList = new ArrayList<FacetData<SearchStateData>>(mockMap.values());
		Mockito.verify(this.widgetInstanceManager, Mockito.times(1)).sendOutput(
				Mockito.eq(FacetFilterController.SOCKET_OUT_SELECTEDFACETS), Mockito.eq(mockList));
	}

	@Test
	public void removeValuesFromFacetTest()
	{
		final FacetValueData<SearchStateData> facetValueData1 = new FacetValueData<SearchStateData>();
		facetValueData1.setCode("facetValueData1");
		final List<FacetValueData<SearchStateData>> values2remove = new ArrayList<FacetValueData<SearchStateData>>();
		values2remove.add(facetValueData1);
		final FacetData mockFacetData = facetFilterController.removeValuesFromFacet(facet1, values2remove);
		Assert.assertEquals(mockFacetData.getValues().size(), 2);
	}

}
