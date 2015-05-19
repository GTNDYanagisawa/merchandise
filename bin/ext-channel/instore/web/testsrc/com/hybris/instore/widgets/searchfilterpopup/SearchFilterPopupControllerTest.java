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
package com.hybris.instore.widgets.searchfilterpopup;

import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

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
{ @DeclaredInput(socketType = ProductSearchPageData.class, value = SearchFilterPopupController.SOCKET_IN_SEARCHRESULT),
		@DeclaredInput(value = SearchFilterPopupController.SOCKET_IN_CLOSEPOPUP) })
@DeclaredViewEvents(
{ @DeclaredViewEvent(componentID = SearchFilterPopupController.COMP_ID_ADDFILTER, eventName = Events.ON_CLICK),
		@DeclaredViewEvent(componentID = SearchFilterPopupController.COMP_ID_FACET_WINDOW, eventName = Events.ON_CLOSE) })
public class SearchFilterPopupControllerTest extends AbstractWidgetUnitTest<SearchFilterPopupController>
{
	@InjectMocks
	private final SearchFilterPopupController controller = new SearchFilterPopupController();

	@SuppressWarnings("unused")
	@Mock
	private Button addFilter;

	@SuppressWarnings("unused")
	@Mock
	private Window facetWindow;

	@Override
	protected SearchFilterPopupController getWidgetController()
	{
		return this.controller;
	}
}
