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
package de.hybris.platform.b2bpunchoutaddon.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;


/**
 * Unit test for class {@link PunchOutBeforeViewHandler}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutCartBeforeViewHandlerTest
{
	public final static String OLD_VIEW = "/oldPage";
	public final static String NEW_VIEW = "/newPage";

	private PunchOutBeforeViewHandler viewHandler;

	@Mock
	private MockHttpServletRequest request;
	@Mock
	private MockHttpServletResponse response;

	private HttpSession session;
	private ModelMap model;


	@Before
	public void setup()
	{
		Map<String, Map<String, String>> viewMap;
		viewHandler = new PunchOutBeforeViewHandler();
		viewMap = new HashMap<String, Map<String, String>>();
		final Map<String, String> viewName = new HashMap<>();
		viewName.put("viewName", NEW_VIEW);
		viewMap.put(OLD_VIEW, viewName);
		viewHandler.setViewMap(viewMap);

		session = Mockito.mock(HttpSession.class);
		model = Mockito.mock(ModelMap.class);
	}

	@Test
	public void changesViewForPunchOutUser() throws Exception
	{
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(session.getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)).thenReturn("myUser");
		final String view = viewHandler.beforeView(request, response, model, OLD_VIEW);
		Assert.assertNotNull(view);
		Assert.assertEquals(B2bpunchoutaddonConstants.VIEW_PAGE_PREFIX + NEW_VIEW, view);
	}

	@Test
	public void keepsViewForNonPunchOutUser() throws Exception
	{
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(session.getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)).thenReturn(null);
		final String view = viewHandler.beforeView(request, response, model, OLD_VIEW);
		Assert.assertNotNull(view);
		Assert.assertEquals(OLD_VIEW, view);
	}

}
