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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.catalog.dto.CatalogsDTO;
import de.hybris.platform.catalog.jalo.Catalog;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class CatalogVersionResourceTest extends AbstractWebServicesTest
{
	private static final String TEST_CATALOG_ID = "testCatalog1";
	private static final String TEST_CATALOGVERSION_VERSION1 = "testVersion1";
	private static final String CATALOGVERSION_VERSION = "Online";

	public CatalogVersionResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCatalogs() throws ConsistencyCheckException
	{
		createTestCatalogs();
	}

	@Test
	public void testGetCatalogVersionXML() throws IOException //NOPMD
	{
		testGetCatalogVersion(MediaType.APPLICATION_XML);
	}


	@Test
	public void testGetCatalogVersionJSON() throws IOException //NOPMD
	{
		testGetCatalogVersion(MediaType.APPLICATION_JSON);
	}

	private void testGetCatalogVersion(final String format) throws IOException
	{

		final ClientResponse catalogsResult = webResource.path("/catalogs").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(format).get(ClientResponse.class);
		catalogsResult.bufferEntity();
		final CatalogsDTO catalogs = catalogsResult.getEntity(CatalogsDTO.class);
		CatalogDTO catalog = new CatalogDTO();
		for (CatalogDTO tempCatalog : catalogs.getCatalogs())
		{
			final ClientResponse cartResult = webResource.path("/catalogs/" + tempCatalog.getId()).cookie(tenantCookie).header(
					HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(format).get(ClientResponse.class);
			cartResult.bufferEntity();
			tempCatalog = cartResult.getEntity(CatalogDTO.class);
			if ("testCatalog1".equals(tempCatalog.getId()))
			{
				catalog = tempCatalog;
				break;
			}
		}
		assertNotNull("Expected catalog not found", catalog.getId());
		if (0 == catalog.getCatalogVersions().size())
		{
			fail("Catalog has no versions");
		}
		CatalogVersionDTO catalogVersion = null;
		for (final CatalogVersionDTO itCatalogVersion : catalog.getCatalogVersions())
		{
			if (itCatalogVersion.getVersion().compareTo("testVersion1") == 0)
			{
				catalogVersion = itCatalogVersion;
			}
		}
		final ClientResponse versionResult = webResource.path(
				"/catalogs/" + catalog.getId() + "/catalogversions/" + catalogVersion.getVersion()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(format).get(ClientResponse.class);
		versionResult.bufferEntity();
		catalogVersion = versionResult.getEntity(CatalogVersionDTO.class);
		assertNotNull("No catalog version within body at response: " + versionResult, catalogVersion);
		assertNotNull("Version needs to be set at response: " + versionResult, catalogVersion.getVersion());
	}

	@Test
	public void testPutNewCatalogVesion() throws ConsistencyCheckException
	{
		final CatalogVersionDTO cvDto = new CatalogVersionDTO();
		cvDto.setVersion(CATALOGVERSION_VERSION);

		//send message for creating of the catalog version
		final ClientResponse result = webResource.path(
				"/catalogs/" + TEST_CATALOG_ID + "/catalogversions/" + CATALOGVERSION_VERSION).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(cvDto).put(
				ClientResponse.class);
		result.bufferEntity();
		//check the response 
		assertCreated(result, true);

		//read the catalog 
		final Catalog catalog = CatalogManager.getInstance().getCatalog(TEST_CATALOG_ID);

		//check if the catalog version has been created
		final CatalogVersion catalogVersion = catalog.getCatalogVersion(CATALOGVERSION_VERSION);
		assertNotNull("The catalog version wasn't created", catalogVersion);

		//remove catalog version
		catalogVersion.remove();
	}

	@Test
	public void testPutUpdateCatalogVersion() throws IOException
	{
		//create catalog DTO
		final CatalogVersionDTO cvDto = new CatalogVersionDTO();
		cvDto.setVersion(CATALOGVERSION_VERSION);

		//send message for creating of the catalog version
		final ClientResponse result = webResource.path(
				"/catalogs/" + TEST_CATALOG_ID + "/catalogversions/" + TEST_CATALOGVERSION_VERSION1).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(cvDto).put(
				ClientResponse.class);
		result.bufferEntity();
		//check the response
		assertOk(result, true);

		//read the catalog 
		final Catalog catalog = CatalogManager.getInstance().getCatalog(TEST_CATALOG_ID);

		//check if the catalog version has been created
		final CatalogVersion catalogVersion = catalog.getCatalogVersion(CATALOGVERSION_VERSION);
		assertNotNull("The catalog version wasn't created", catalogVersion);
	}

	@Test
	public void testDeleteCatalogVersion() throws IOException
	{
		final CatalogVersionDTO cvDto = new CatalogVersionDTO();
		cvDto.setVersion(CATALOGVERSION_VERSION);

		//send message for creating of the catalog version
		ClientResponse result = webResource.path("/catalogs/" + TEST_CATALOG_ID + "/catalogversions/" + CATALOGVERSION_VERSION)
				.cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
				.entity(cvDto).put(ClientResponse.class);
		result.bufferEntity();

		//send message for deleting of the test catalog version
		result = webResource.path("/catalogs/" + TEST_CATALOG_ID + "/catalogversions/" + CATALOGVERSION_VERSION).cookie(
				tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(
				ClientResponse.class);
		result.bufferEntity();
		//check the response
		assertOk(result, true);

		//read the catalog 
		final Catalog catalog = CatalogManager.getInstance().getCatalog(TEST_CATALOG_ID);

		//check if the catalog has been deleted
		final CatalogVersion catalogVersion = catalog.getCatalogVersion(CATALOGVERSION_VERSION);
		assertNull("The catalog version wasn't deleted", catalogVersion);
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed("/catalogs/" + TEST_CATALOG_ID + "/catalogversions/" + TEST_CATALOGVERSION_VERSION1, POST);
	}
}
