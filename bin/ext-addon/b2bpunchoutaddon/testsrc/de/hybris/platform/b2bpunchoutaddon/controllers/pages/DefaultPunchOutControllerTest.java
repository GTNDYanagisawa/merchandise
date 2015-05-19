/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *  
 */
package de.hybris.platform.b2bpunchoutaddon.controllers.pages;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.b2bpunchoutaddon.interceptors.PunchOutBeforeViewHandler;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.cxml.CXML;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for class {@link PunchOutBeforeViewHandler}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutControllerTest
{

	@Mock
	private PunchOutService punchOutService;

	@Mock
	private ConfigurationService configurationService;

	//	DefaultPunchOutController controller;

	@Before
	public void setup()
	{
		configurationService = Mockito.mock(ConfigurationService.class, Mockito.RETURNS_DEEP_STUBS);

		//		controller = new DefaultPunchOutController();
		//		controller.setPunchOutService(punchoutService);
		//		controller.setConfigurationService(configurationService);
	}

	@Test
	@Ignore("Ignored until Denis moves it over to the service")
	public void changesViewForPunchOutUser() throws Exception
	{
		final CartData cart = new CartData();
		cart.setCode("myCode");
		final CXML cxml = new CXML();
		when(punchOutService.processPunchOutSetUpRequest(isA(CXML.class))).thenReturn(cxml);

		//		when(configurationService.getConfiguration().getString(DefaultPunchOutController.REQUISITION_PATH_KEY)).thenReturn(
		//				"http://localhost:9001");

		//		controller.sendTheOrderToProcurementSystem(cart);
	}
}
