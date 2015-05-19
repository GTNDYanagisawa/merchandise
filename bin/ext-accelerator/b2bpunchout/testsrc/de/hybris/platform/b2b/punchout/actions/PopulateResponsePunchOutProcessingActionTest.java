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
package de.hybris.platform.b2b.punchout.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.cxml.PunchOutSetupResponse;
import org.cxml.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PopulateResponsePunchOutProcessingActionTest
{
	@Mock
	private PunchOutConfigurationService configurationService;

	@InjectMocks
	private final PopulateResponsePunchOutProcessingAction action = new PopulateResponsePunchOutProcessingAction();
	private CXML requestXML;
	private CXML responseXML;

	@Before
	public void prepare() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
		responseXML = CXMLBuilder.newInstance().create();
	}

	@Test
	public void shouldProducePunchOutSetupResponseWithRedirectURL()
	{
		final String url = "http://test.login.url";
		when(configurationService.getPunchOutLoginUrl()).thenReturn(url);

		action.process(requestXML, responseXML);

		final Response response = (Response) responseXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		final PunchOutSetupResponse punchoutResponse = (PunchOutSetupResponse) response
				.getProfileResponseOrPunchOutSetupResponseOrProviderSetupResponseOrGetPendingResponseOrSubscriptionListResponseOrSubscriptionContentResponseOrSupplierListResponseOrSupplierDataResponseOrAuthResponseOrDataResponseOrOrganizationDataResponse()
				.get(0);

		assertNotNull(punchoutResponse);
		assertEquals(url, punchoutResponse.getStartPage().getURL().getvalue());
	}
}
