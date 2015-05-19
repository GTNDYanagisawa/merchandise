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

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.core.dto.media.MediaDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaManager;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class MediaResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "catalogs/testCatalog1/catalogversions/testVersion1/medias/";
	private Media testMedia;

	public MediaResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpMedias() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCatalogs();
		createTestMedias();
		testMedia = (Media) MediaManager.getInstance().getMediaByCode("testMedia1").iterator().next();
	}

	@Test
	public void testGetMedia() throws IOException
	{
		final ClientResponse result = webResource.path(URI + testMedia.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final MediaDTO media = result.getEntity(MediaDTO.class);
		assertNotNull("No media within body at response: " + result, media);
		assertEquals("Wrong media code at response: " + result, testMedia.getCode(), media.getCode());
		assertEquals("Wrong description name at response: " + result, testMedia.getDescription(), media.getDescription());
		assertEquals("Wrong mime name at response: " + result, testMedia.getMime(), media.getMime());
		assertEquals("Wrong realfilename name at response: " + result, testMedia.getRealFileName(), media.getRealfilename());
		assertEquals("Wrong size at response: " + result, testMedia.getSize(), media.getSize());
		assertNotNull("Empty url at response: " + result, media.getURL());
		assertNotNull("Empty url at response: " + result, media.getDownloadURL());
	}

	@Test
	public void testPutMediaNew() throws Exception
	{
		final CatalogDTO catalog = new CatalogDTO();
		catalog.setId("testCatalog1");
		final CatalogVersionDTO catalogVersion = new CatalogVersionDTO();
		catalogVersion.setVersion("testVersion1");
		catalogVersion.setCatalog(catalog);
		final MediaDTO media = new MediaDTO();
		media.setCode("newMedia");
		media.setDescription("description");
		media.setCatalogVersion(catalogVersion);

		final ClientResponse result = webResource.path(URI + "newMedia").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(media).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		final Media newMedia = (Media) MediaManager.getInstance().getMediaByCode("newMedia").iterator().next();
		assertEquals("Description was not changed correct", "description", media.getDescription());

		//remove created Media
		newMedia.remove();
	}

	@Test
	public void testPutMediaUpdate() throws Exception
	{
		final MediaDTO media = new MediaDTO();
		media.setCode("testMedia1");
		media.setDescription("newDescription");

		final ClientResponse result = webResource.path(URI + testMedia.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(media).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);
		assertEquals("Description was not changed correct", "newDescription", testMedia.getDescription());
	}

	@Test
	public void testDeleteMedia() throws IOException
	{
		final ClientResponse result = webResource.path(URI + testMedia.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);
		assertTrue("Found media however it should be deleted", MediaManager.getInstance().getMediaByCode("testMedia1").isEmpty());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + testMedia.getCode(), POST);
	}
}
