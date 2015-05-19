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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.cxml.Credential;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationCheckPunchOutProcessingActionTest
{

	@Mock
	private PunchOutCredentialService credentialService;

	AuthenticationCheckPunchOutProcessingAction action = new AuthenticationCheckPunchOutProcessingAction();

	@InjectMocks
	private final AuthenticationVerifier punchoutAuthenticationVerifier = new AuthenticationVerifier();


	CXML requestXML;
	CXML responseXML;

	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
		responseXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/defaultSuccessResponse.xml");

		action.setPunchOutAuthenticationVerifier(punchoutAuthenticationVerifier);
	}

	@Test
	public void shouldValidateHeaderOnCreation() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/wrongPunchoutSetupRequest.xml");
		try
		{
			action.process(requestXML, responseXML);
			fail("Should fail since wrong header info");
		}
		catch (final IllegalArgumentException e)
		{
			//ok
		}
	}

	@SuppressWarnings("boxing")
	@Test
	public void shouldThrowExceptionSinceWrongCredentials()
	{
		final Credential credential = new Credential();

		when(credentialService.getCustomerForCredential(credential)).thenReturn(null);

		try
		{
			action.process(requestXML, responseXML);
			fail("Should fail since wrong credentials");
		}
		catch (final PunchOutException e)
		{
			assertEquals("401", e.getErrorCode());
		}
	}

}
