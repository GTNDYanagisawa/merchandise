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
package de.hybris.platform.b2b.punchout.services.impl;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.actions.AuthenticationCheckPunchOutProcessingAction;
import de.hybris.platform.b2b.punchout.actions.PopulateResponsePunchOutProcessingAction;
import de.hybris.platform.b2b.punchout.actions.PunchOutProcessingAction;
import de.hybris.platform.b2b.punchout.services.PunchOutService;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.cxml.CXML;
import org.cxml.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutServiceTest
{
	private final DefaultPunchOutService punchOutService = new DefaultPunchOutService();

	@Mock
	private PunchOutProcessingAction<CXML, CXML> errorThrowingAction;
	private CXML request;

	@Before
	public void prepare() throws FileNotFoundException
	{
		request = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
		punchOutService.setSetUpRequestProcessingActions(Lists.newArrayList(new AuthenticationCheckPunchOutProcessingAction(),
				new PopulateResponsePunchOutProcessingAction()));
	}

	/**
	 * Tests that when {@link PunchOutService#processPunchOutSetUpRequest(CXML)} is invoked and an action throws and
	 * exception the result will contain both the error code and the error message.
	 */
	@Test
	public void shouldCreateNotAuthorizedResponse()
	{
		doThrow(new PunchOutException("401", "test error message")).when(errorThrowingAction).process(Mockito.any(CXML.class),
				Mockito.any(CXML.class));

		punchOutService.setSetUpRequestProcessingActions(Arrays.asList(errorThrowingAction));

		final CXML responseXML = punchOutService.processPunchOutSetUpRequest(request);
		final Response response = (Response) responseXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		assertEquals("Error code should match the error code from the exception", "401", response.getStatus().getCode());
		assertEquals("Error message should match the exception error message", "test error message", response.getStatus().getText());
	}
}