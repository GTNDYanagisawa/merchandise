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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class CatalogResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "catalogs/";
	private static final String TEST_CATALOG_ID = "testCatalog1";
	private static final String CATALOG_ID = "newTestCatalogId";

	private Catalog testCatalog;

	public CatalogResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCatalogs() throws ConsistencyCheckException
	{
		createTestCatalogs();
		testCatalog = CatalogManager.getInstance().getCatalog(CatalogResourceTest.TEST_CATALOG_ID);
	}

	@Test
	public void testGetCatalog() throws IOException
	{
		final ClientResponse result = webResource.path(URI + testCatalog.getId()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CatalogDTO catalog = result.getEntity(CatalogDTO.class);
		assertNotNull("No catalog within body at response: " + result, catalog);
		assertEquals("Wrong number of versions at response: " + result, 2, catalog.getCatalogVersions().size());
		assertIsNotNull(new ArrayList(catalog.getCatalogVersions()), CatalogVersionDTO.class, "uri", "version");
		assertIsNull(new ArrayList(catalog.getCatalogVersions()), CatalogVersionDTO.class, "rootCategories");
	}


	@Test
	public void testGetCatalogJsonFormat() throws IOException
	{
		final ClientResponse result = webResource.path(URI + testCatalog.getId()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		result.bufferEntity();
		//assertOk() not used because of different media type: APPLICATION_JSON_TYPE
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		assertTrue("Wrong content-type at response: " + result, MediaType.APPLICATION_JSON_TYPE.isCompatible(result.getType()));

		final CatalogDTO catalog = result.getEntity(CatalogDTO.class);
		assertNotNull("No catalog within body at response: " + result, catalog);
		assertEquals("Wrong number of versions at response: " + result, 2, catalog.getCatalogVersions().size());
		assertIsNotNull(new ArrayList(catalog.getCatalogVersions()), CatalogVersionDTO.class, "uri", "version");
		assertIsNull(new ArrayList(catalog.getCatalogVersions()), CatalogVersionDTO.class, "rootCategories");
	}

	@Test
	public void testPutNewCatalog() throws ConsistencyCheckException
	{
		final String CATALOG_NAME = "newTestCatalogName";

		//create new catalog DTO 
		final CatalogDTO catalogDto = new CatalogDTO();
		catalogDto.setId(CATALOG_ID);
		catalogDto.setName(CATALOG_NAME);

		//send message for creating of the catalog 
		final ClientResponse result = webResource.path(URI + CATALOG_ID).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(catalogDto).put(ClientResponse.class);
		result.bufferEntity();

		//check the response 
		assertCreated(result, true);

		//check if the catalog has been created
		final Catalog catalog = CatalogManager.getInstance().getCatalog(CATALOG_ID);
		assertNotNull("The catalog wasn't created", catalog);
		assertEquals("The name wasn't updated correctly", catalog.getName(), CATALOG_NAME);

		//remove catalog
		catalog.remove();
	}

	@Test
	public void testPutUpdateCatalog() throws IOException
	{
		final String CATALOG_NAME = "changedTestCatalogName";

		//create catalog DTO
		final CatalogDTO catalogDto = new CatalogDTO();
		catalogDto.setId(TEST_CATALOG_ID);
		catalogDto.setName(CATALOG_NAME);

		//send message for updating of the test catalog
		final ClientResponse result = webResource.path(URI + TEST_CATALOG_ID).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(catalogDto).put(ClientResponse.class);
		result.bufferEntity();
		//check the response
		assertOk(result, true);

		//check if the catalog has been created
		final Catalog catalog = CatalogManager.getInstance().getCatalog(TEST_CATALOG_ID);
		assertEquals("The name wasn't updated correctly", catalog.getName(), CATALOG_NAME);
	}

	@Test
	public void testDeleteCatalog() throws IOException
	{
		final String CATALOG_NAME = "newTestCatalogName";

		//create new catalog DTO 
		final CatalogDTO catalogDto = new CatalogDTO();
		catalogDto.setId(CATALOG_ID);
		catalogDto.setName(CATALOG_NAME);

		//send message for creating of the catalog 
		ClientResponse result = webResource.path(URI + CATALOG_ID).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(catalogDto).put(ClientResponse.class);
		result.bufferEntity();

		//send message for deleting of the test catalog
		result = webResource.path(URI + CATALOG_ID).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		//check the response
		assertOk(result, true);

		//check if the catalog has been deleted 
		final Catalog catalog = CatalogManager.getInstance().getCatalog(CATALOG_ID);
		assertNull("The catalog wasn't deleted", catalog);
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + testCatalog.getId(), POST);
	}
}
