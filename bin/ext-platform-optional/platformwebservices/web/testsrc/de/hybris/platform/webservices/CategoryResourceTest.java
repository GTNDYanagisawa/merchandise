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
import static org.junit.Assert.fail;

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.catalog.dto.CatalogsDTO;
import de.hybris.platform.category.dto.CategoryDTO;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class CategoryResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "catalogs/testCatalog1/catalogversions/testVersion1/categories/";

	public CategoryResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCatalogs() throws ConsistencyCheckException
	{
		createTestCatalogs();
	}



	@Test
	public void testGetCategoryXML() throws IOException //NOPMD
	{
		testGetCategory(MediaType.APPLICATION_XML);
	}


	@Test
	public void testGetCategoryJSON() throws IOException //NOPMD
	{
		testGetCategory(MediaType.APPLICATION_JSON);
	}

	private void testGetCategory(final String format) throws IOException
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
		//assertOk not used because of different format
		assertEquals("Wrong HTTP status at response: " + versionResult, Response.Status.OK, versionResult.getResponseStatus());
		assertTrue("Wrong content-type at response: " + versionResult, MediaType.valueOf(format).getType().equals(
				versionResult.getType().getType()));
		assertTrue("Wrong content-type at response: " + versionResult, MediaType.valueOf(format).getSubtype().equals(
				versionResult.getType().getSubtype()));
		catalogVersion = versionResult.getEntity(CatalogVersionDTO.class);
		assertNotNull("Expected catalog version not found", catalogVersion.getVersion());
		if (0 == catalogVersion.getRootCategories().size())
		{
			fail("Catalog has no categories");
		}
		CategoryDTO category = catalogVersion.getRootCategories().get(0);
		final ClientResponse categoryResult = webResource.path(
				"/catalogs/" + catalog.getId() + "/catalogversions/" + catalogVersion.getVersion() + "/categories/"
						+ category.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(format).get(ClientResponse.class);
		categoryResult.bufferEntity();
		category = categoryResult.getEntity(CategoryDTO.class);

		assertNotNull("No category within body at response: " + categoryResult, category);
		assertNotNull("Category code needs to be set at response: " + categoryResult, category.getCode());
	}

	@Test
	public void testPutCategoryNew() throws Exception
	{
		final CategoryDTO category = new CategoryDTO();
		category.setCode("newCategory");
		category.setName("Name");
		category.setDescription("Description");

		final ClientResponse result = webResource.path(URI + "newCategory").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(category).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);


		final Category newCategory = CategoryManager.getInstance().getCategoriesByCode("newCategory").iterator().next();
		assertEquals("Name was not changed correct", "Name", newCategory.getName());
	}

	@Test
	public void testPutCategoryUpdate() throws Exception
	{
		final CategoryDTO category = new CategoryDTO();
		category.setCode("testCategory1");
		category.setName("updatedName");
		category.setDescription("Description");

		final ClientResponse result = webResource.path(URI + "testCategory1").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(category).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		final Category testCategory1 = CategoryManager.getInstance().getCategoriesByCode("testCategory1").iterator().next();
		assertEquals("Name was not changed correctly", "updatedName", testCategory1.getName());
	}


	@Test
	public void testDeleteCategory()
	{

		ClientResponse result = webResource.path(URI + "testCategory2").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + "testCategory2").cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());

	}

}
