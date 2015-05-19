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
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.dto.c2l.LanguageDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Language;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


// ignored only for bamboo reasons because someone (some other extension) does a bad job in clean-up 
// test-data and let some languages alive in the testsystem
@Ignore("PLA-11441")
public class LanguageResourceTest extends AbstractWebServicesTest
{

	private static final String URI = "languages/";
	private Language testLanguage;


	public LanguageResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpLanguages() throws ConsistencyCheckException
	{
		createTestsLanguages();
		testLanguage = C2LManager.getInstance().getLanguageByIsoCode("testLang1");
	}

	@Test
	public void testGetLanguage() throws IOException
	{

		final ClientResponse result = webResource.path(URI + testLanguage.getIsoCode()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final LanguageDTO language = result.getEntity(LanguageDTO.class);
		assertNotNull("No language within body at response: " + result, language);
		assertEquals("Wrong language isocode at response: " + result, testLanguage.getIsoCode(), language.getIsocode());
		assertEquals("Wrong language name at response: " + result, testLanguage.getName(), language.getName());
		assertEquals("Wrong language active status at response: " + result, Boolean.FALSE, language.getActive());
		assertEquals("Wrong number of fallbackLanguages at response: " + result, 1, language.getFallbackLanguages().size());

	}

	@Test
	public void testPutLanguageNew() throws Exception
	{

		final LanguageDTO language = new LanguageDTO();
		language.setIsocode("newLang");
		language.setName("name");
		language.setActive(Boolean.FALSE);

		final ClientResponse result = webResource.path(URI + language.getIsocode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(language).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		final Language newLanguage = C2LManager.getInstance().getLanguageByIsoCode("newLang");
		assertEquals("Name was not changed correct", "name", newLanguage.getName());
		assertTrue("FallbackLanguages were not empty", newLanguage.getFallbackLanguages().isEmpty());

		//remove created Language
		newLanguage.remove();

	}

	@Test
	public void testPutLanguageUpdate() throws Exception
	{

		final LanguageDTO language = new LanguageDTO();
		language.setIsocode("testLang1");
		language.setName("changedName");
		language.setActive(Boolean.FALSE);

		final ClientResponse result = webResource.path(URI + testLanguage.getIsoCode()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(language).put(
				ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		assertEquals("Name was not changed correct", "changedName", testLanguage.getName());
		assertEquals("FallbackLanguages were changed", 1, testLanguage.getFallbackLanguages().size());

	}

	@Test
	public void testDeleteLanguage()
	{

		ClientResponse result = webResource.path(URI + testLanguage.getIsoCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + "testLang1").cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());

	}

	/**
	 * for a generated resource we generate hardcoded set of the CRUD method HOR-496
	 */
	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + testLanguage.getIsoCode(), POST);
	}

}
