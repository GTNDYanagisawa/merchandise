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

import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.catalog.dto.CatalogsDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;


/**
 * Various tests of selector API based on Catalogs root resource.
 */
public class CatalogsResourceSelectorTest extends AbstractWebServicesTest
{
	private static final String URI = "catalogs";

	public CatalogsResourceSelectorTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCatalogs() throws ConsistencyCheckException
	{
		createTestCatalogs();
	}

	/**
	 * This tests that factory defaults apply for Catalog and CatalogVerion resources in absence of selector API
	 * configuration
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetCatalogsNoConfig() throws IOException
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CatalogsDTO catalogs = result.getEntity(CatalogsDTO.class);
		assertNotNull("No catalogs found at response: " + result, catalogs);
		assertEquals("Wrong number of catalogs found at response: " + result, 2, catalogs.getCatalogs().size());
		assertIsNotNull(catalogs.getCatalogs(), CatalogDTO.class, "id", "uri");
		assertIsNull(catalogs.getCatalogs(), CatalogDTO.class, "catalogVersions");
	}

	/**
	 * This tests selector API use on Catalog and CatalogVerion. "Short form" of selector API query parameters (no
	 * suffix) is used. configuration
	 */
	@Test
	public void testGetCatalogsRuntimeConfigNoSuffix() throws IOException
	{
		final ClientResponse result = webResource.path(URI).queryParam("catalog_attributes", " creatioNtime , catalOgversIons ")
				.queryParam("catalogversion_attributes", "PK , activE , modifIedTime ").cookie(tenantCookie).header(HEADER_AUTH_KEY,
						HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CatalogsDTO catalogs = result.getEntity(CatalogsDTO.class);
		assertNotNull("No catalogs found at response: " + result, catalogs);
		assertEquals("Wrong number of catalogs found at response: " + result, 2, catalogs.getCatalogs().size());
		assertIsNotNull(catalogs.getCatalogs(), CatalogDTO.class, "creationtime", "catalogVersions", "uri");
		assertIsNull(catalogs.getCatalogs(), CatalogDTO.class, "id");
		assertNotNull("Catalog version should not be null", catalogs.getCatalogs().get(0).getCatalogVersions());
		assertIsNotNull(new ArrayList(catalogs.getCatalogs().get(0).getCatalogVersions()), CatalogVersionDTO.class, "pk", "active",
				"modifiedtime", "uri");
		assertIsNull(new ArrayList(catalogs.getCatalogs().get(0).getCatalogVersions()), CatalogVersionDTO.class, "version");

	}

	/**
	 * This tests selector API use on Catalog and CatalogVerion. "Full form" of selector API query parameters is used.
	 * For CatalogVersion invalid entry is used: The configured data refers to "detail representation", while in this
	 * request only "reference representation" of CatalogVerion exist. Thus the entry is ignored and factory defaults for
	 * CatalogVersion apply. configuration
	 */
	@Test
	public void testGetCatalogsRuntimeConfigSuffix() throws IOException
	{
		final ClientResponse result = webResource.path(URI).queryParam("catalog_attributes_reference",
				" creatioNtime , catalOgversIons ").queryParam("catalogversion_attributes_detail", "PK , activE , modifIedTime ")
				.cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(
						ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CatalogsDTO catalogs = result.getEntity(CatalogsDTO.class);
		assertNotNull("No catalogs found at response: " + result, catalogs);
		assertEquals("Wrong number of catalogs found at response: " + result, 2, catalogs.getCatalogs().size());
		assertIsNotNull(catalogs.getCatalogs(), CatalogDTO.class, "creationtime", "catalogVersions", "uri");
		assertIsNull(catalogs.getCatalogs(), CatalogDTO.class, "id");
		assertNotNull("Catalog version should not be null", catalogs.getCatalogs().get(0).getCatalogVersions());
		assertIsNull(new ArrayList(catalogs.getCatalogs().get(0).getCatalogVersions()), CatalogVersionDTO.class, "active",
				"modifiedtime");
		assertIsNotNull(new ArrayList(catalogs.getCatalogs().get(0).getCatalogVersions()), CatalogVersionDTO.class, "pk",
				"version", "uri");
	}

}
