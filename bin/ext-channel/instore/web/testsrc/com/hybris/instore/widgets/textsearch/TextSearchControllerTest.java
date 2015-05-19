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
package com.hybris.instore.widgets.textsearch;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


/**
 * @author johannesdoberer
 * 
 */
@NullSafeWidget(value = false)
@DeclaredInput(socketType = String.class, value = TextSearchController.SOCKET_IN_FILL_TEXTSEARCH)
@DeclaredViewEvent(componentID = TextSearchController.COMP_ID_SEARCH_BTN, eventName = Events.ON_CLICK)
public class TextSearchControllerTest extends AbstractWidgetUnitTest<TextSearchController>
{
	@InjectMocks
	private final TextSearchController textSearchController = new TextSearchController();

	@SuppressWarnings("unused")
	@Mock
	private Textbox searchInput;

	@Override
	protected TextSearchController getWidgetController()
	{
		return textSearchController;
	}
}
