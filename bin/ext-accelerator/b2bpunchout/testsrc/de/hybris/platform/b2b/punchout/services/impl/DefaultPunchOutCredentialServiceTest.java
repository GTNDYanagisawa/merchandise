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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.daos.PunchOutCredentialDao;
import de.hybris.platform.b2b.punchout.model.B2BCustomerPunchOutCredentialMappingModel;
import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutCredentialServiceTest
{

	private final static String DUMMY_EMAIL = "dummy@hybris.com";
	private final static String SHARED_SECRET = "VerySecret1234";
	private final DefaultPunchOutCredentialService punchoutCredentialService = new DefaultPunchOutCredentialService();
	private CXML requestXML;
	private Header header;
	private Credential credential;

	@Mock
	private PunchOutCredentialDao credentialDao;

	@Mock
	private PunchOutCredentialModel credentialModel;

	@Mock
	private B2BCustomerPunchOutCredentialMappingModel mappingModel;

	@Mock
	private B2BCustomerModel customerModel;

	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
		header = (Header) requestXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		credential = header.getSender().getCredential().iterator().next();

		when(customerModel.getEmail()).thenReturn(DUMMY_EMAIL);
		when(mappingModel.getB2bCustomer()).thenReturn(customerModel);
		when(credentialModel.getB2BCustomerPunchOutCredentialMapping()).thenReturn(mappingModel);
		when(credentialModel.getSharedsecret()).thenReturn(SHARED_SECRET);
		punchoutCredentialService.setCredentialDao(credentialDao);

	}


	@Test
	public void testGetCustomerForCredentialNotExistentCustomer()
	{
		when(this.credentialDao.getPunchOutCredential(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		final B2BCustomerModel customerModel = punchoutCredentialService.getCustomerForCredential(credential);
		assertNull(customerModel);
	}

	@Test
	public void testGetCustomerForCredential()
	{
		when(this.credentialDao.getPunchOutCredential(Mockito.anyString(), Mockito.anyString())).thenReturn(credentialModel);
		punchoutCredentialService.setCredentialDao(credentialDao);
		customerModel = punchoutCredentialService.getCustomerForCredential(credential);
		assertTrue(customerModel.getEmail().equals(DUMMY_EMAIL));
	}

	@Test
	public void testGetPunchOutCredential()
	{
		when(this.credentialDao.getPunchOutCredential(Mockito.anyString(), Mockito.anyString())).thenReturn(credentialModel);
		final PunchOutCredentialModel punchoutCredentialModel = punchoutCredentialService.getPunchOutCredential(
				credential.getDomain(), credential.getIdentity().getContent().get(0).toString());

		assertNotNull(punchoutCredentialModel);
		assertEquals(SHARED_SECRET, punchoutCredentialModel.getSharedsecret());

	}

	@Test
	public void shouldRetrieveTheSharedSecret()
	{
		final Header header = (Header) requestXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		final Credential credential = header.getSender().getCredential().iterator().next();

		final String password = punchoutCredentialService.extractSharedSecret(credential);
		assertEquals(SHARED_SECRET, password);
	}
}
