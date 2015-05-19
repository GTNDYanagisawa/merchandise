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

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogsDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;


public class CatalogsResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "catalogs";

	public CatalogsResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCatalogs() throws ConsistencyCheckException
	{
		createTestCatalogs();
	}

	//	@Test
	//	public void testGetCatalogsAnonymous()
	//	{
	//		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).accept(MediaType.APPLICATION_XML).get(
	//				ClientResponse.class);
	//		result.bufferEntity();
	//		assertOk(result, false);
	//
	//		final CatalogsDTO catalogs = result.getEntity(CatalogsDTO.class);
	//		assertNotNull("No catalogs found at response: " + result, catalogs);
	//		assertEquals("Wrong number of catalogs found at response: " + result, 2, catalogs.getCatalogs().size());
	//		assertIsNotNull(catalogs.getCatalogs(), CatalogDTO.class, "id", "uri");
	//		assertIsNull(catalogs.getCatalogs(), CatalogDTO.class, "catalogVersions");
	//
	//	}

	@Test
	public void testGetCatalogs() throws IOException
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

	@Test
	public void testGetCatalogsCatlogsUri() throws IOException
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();

		assertOk(result, false);

		final CatalogsDTO catalogs = result.getEntity(CatalogsDTO.class);

		//check if response contains uri for root 
		assertEquals("Response doesn't contain the uri " + webResource.getURI().toString() + "/" + URI, catalogs.getUri(),
				webResource.getURI().toString() + "/" + URI);

	}


	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, POST, DELETE);
	}

}
