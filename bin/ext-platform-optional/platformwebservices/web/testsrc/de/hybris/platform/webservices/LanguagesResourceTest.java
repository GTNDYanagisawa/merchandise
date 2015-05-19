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
package de.hybris.platform.webservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.core.dto.c2l.LanguageDTO;
import de.hybris.platform.core.dto.c2l.LanguagesDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 *
 */
public class LanguagesResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "languages";

	public LanguagesResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpLanguages() throws ConsistencyCheckException
	{
		createTestsLanguages();
	}

	@Test
	public void testGetLanguages() throws IOException
	{

		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final LanguagesDTO languages = result.getEntity(LanguagesDTO.class);
		assertNotNull("No languages within body at response: " + result, languages);
		assertEquals("Wrong number of languages at response: " + result, 3, languages.getLanguages().size());
		assertIsNotNull(languages.getLanguages(), LanguageDTO.class, "isocode", "uri");
		assertIsNull(languages.getLanguages(), LanguageDTO.class, "active");

	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, DELETE, POST);
	}
}
