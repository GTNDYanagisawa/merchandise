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
package com.hybris.instore.widgets.instorelayout;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Div;
import org.zkoss.zul.West;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredInputs;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvents;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


/**
 * @author johannesdoberer
 * 
 */
@NullSafeWidget(value = false)
@DeclaredInputs(
{ @DeclaredInput(socketType = Object.class, value = InstoreLayoutController.SOCKET_IN_DO_BACK),
		@DeclaredInput(socketType = Object.class, value = InstoreLayoutController.SOCKET_IN_SHOW_LIST),
		@DeclaredInput(socketType = Object.class, value = InstoreLayoutController.SOCKET_IN_SHOW_DETAIL),
		@DeclaredInput(socketType = Object.class, value = InstoreLayoutController.SOCKET_IN_CLOSE_WEST_IN_PORTRAIT),
		@DeclaredInput(socketType = Boolean.class, value = InstoreLayoutController.SOCKET_IN_SHOW_WEST), })
@DeclaredViewEvents(
{ @DeclaredViewEvent(componentID = InstoreLayoutController.COMP_ID_SCAN_QR_BTN, eventName = Events.ON_CLICK),
		@DeclaredViewEvent(componentID = InstoreLayoutController.COMP_ID_INSTORELAYOUT_WEST, eventName = Events.ON_OPEN) })
public class InstoreLayoutControllerTest extends AbstractWidgetUnitTest<InstoreLayoutController>
{
	@InjectMocks
	private final InstoreLayoutController instoreLayoutController = new InstoreLayoutController();

	@SuppressWarnings("unused")
	@Mock
	private West instorelayoutWest;
	@SuppressWarnings("unused")
	@Mock
	private Borderlayout instorelayout;
	@SuppressWarnings("unused")
	@Mock
	private Div detailSlot;
	@SuppressWarnings("unused")
	@Mock
	private Div listSlot;

	@Override
	protected InstoreLayoutController getWidgetController()
	{
		return instoreLayoutController;
	}
}
