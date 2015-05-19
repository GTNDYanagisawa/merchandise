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
import static org.junit.Assert.fail;

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.catalog.dto.KeywordDTO;
import de.hybris.platform.catalog.dto.KeywordsDTO;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.KeywordModel;
import de.hybris.platform.category.dto.CategoryDTO;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.dto.c2l.LanguageDTO;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;




public class KeywordResourceTest extends AbstractWebServicesTest
{
	public KeywordResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpKeywords() throws ConsistencyCheckException
	{
		createTestCatalogs();
		createTestProductsUnits();
		createTestKeywords();
	}

	@Test
	public void testGetKeyword() throws IOException
	{
		KeywordDTO keyword = null;
		final ClientResponse keywordResult = webResource.path("/keywords").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		keywordResult.bufferEntity();

		final KeywordsDTO keywords = keywordResult.getEntity(KeywordsDTO.class);
		if (keywords.getKeywords().isEmpty())
		{
			fail("No keywords defined");
		}

		for (KeywordDTO tempKeyword : keywords.getKeywords())
		{
			final ClientResponse keywordRes = webResource.path(
					"/catalogs/testCatalog1/catalogversions/testVersion1/keywords/" + tempKeyword.getKeyword()).cookie(tenantCookie)
					.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
					.get(ClientResponse.class);

			keywordRes.bufferEntity();

			tempKeyword = keywordRes.getEntity(KeywordDTO.class);
			if ("before_test".equals(tempKeyword.getKeyword()))
			{
				keyword = tempKeyword;
				break;
			}
		}
		assertNotNull("Expected keyword not found", keyword);
		assertNotNull("Expected keyword not found", keyword.getKeyword()); //NOPMD final List<CartEntryDTO> entries =
	}



	@Test
	public void testPutCreateKeyword() throws IOException
	{
		// name of the keyword to be created
		final String keywordName = "new_keyword";

		// prepare entity for put request
		final KeywordDTO putEntry = new KeywordDTO();
		final ProductDTO product = new ProductDTO();
		product.setCode("testProduct1");
		final CategoryDTO category = new CategoryDTO();
		category.setCode("testCategory1");
		final CatalogVersionDTO version = new CatalogVersionDTO();
		final CatalogDTO catalog = new CatalogDTO();
		catalog.setName("testCatalog1");
		catalog.setId("testCatalog1");
		version.setCatalog(catalog);
		version.setVersion("testVersion1");
		final LanguageDTO language = new LanguageDTO();
		language.setIsocode("en");

		product.setCode("testProduct1");
		putEntry.setKeyword(keywordName);
		putEntry.setProducts(Arrays.asList(product));
		putEntry.setCategories(Arrays.asList(category));
		putEntry.setCatalogVersion(version);
		putEntry.setLanguage(language);


		// create new keyword with PUT
		final ClientResponse entryResult = webResource.path(
				"/catalogs/testCatalog1/catalogversions/testVersion1/keywords/" + keywordName).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(putEntry).put(
				ClientResponse.class);
		entryResult.bufferEntity();

		assertCreated(entryResult, true);



		// retrieve the keyword again and verify that the 'keyword' value was changed
		KeywordDTO temp = null;
		final ClientResponse keywordRes2 = webResource.path(
				"/catalogs/testCatalog1/catalogversions/testVersion1/keywords/new_keyword").cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		keywordRes2.bufferEntity();
		final KeywordDTO keyword = keywordRes2.getEntity(KeywordDTO.class);


		// check if the added keyword is constructed properly
		if ("new_keyword".equals(keyword.getKeyword()))
		{
			temp = keyword;
		}
		assertNotNull("Expected keyword not found", temp);
		assertNotNull("Expected keyword not found", temp.getKeyword()); // NOPMD

		final Collection<ProductDTO> products = keyword.getProducts();
		if (!products.isEmpty())
		{
			temp = keyword;
		}
		assertNotNull("Expected keyword not found", temp);
		assertNotNull("Expected keyword not found", temp.getKeyword()); // NOPMD

		final ClientResponse deleteKeywordRes = webResource.path("keywords/" + keywordName).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		deleteKeywordRes.bufferEntity();

		assertOk(deleteKeywordRes, true);//check delete
	}

	@Test
	public void testPutCreateKeywordWithoutVersion() throws IOException
	{
		// name of the keyword to be created
		final String keywordName = "new_keyword_no_version";

		// prepare entity for put request
		final KeywordDTO putEntry = new KeywordDTO();
		final ProductDTO product = new ProductDTO();
		product.setCode("testProduct1");
		final CategoryDTO category = new CategoryDTO();
		category.setCode("testCategory1");
		final CatalogVersionDTO version = new CatalogVersionDTO();
		final CatalogDTO catalog = new CatalogDTO();
		catalog.setName("testCatalog1");
		catalog.setId("testCatalog1");
		version.setCatalog(catalog);
		version.setVersion("testVersion1");
		final LanguageDTO language = new LanguageDTO();
		language.setIsocode("en");

		product.setCode("testProduct1");
		putEntry.setKeyword(keywordName);
		putEntry.setProducts(Arrays.asList(product));
		putEntry.setCategories(Arrays.asList(category));
		putEntry.setCatalogVersion(version);
		putEntry.setLanguage(language);

		//before creation should return 404
		final ClientResponse keywordRes = webResource.path("keywords/" + keywordName).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		keywordRes.bufferEntity();
		//assertCreated(keywordRes, true);

		assertEquals("Wrong HTTP status at response: " + keywordRes, 404, keywordRes.getStatus());

		// create new keyword with PUT
		final ClientResponse putResult = webResource.path("/keywords/" + keywordName).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(putEntry).put(ClientResponse.class);
		putResult.bufferEntity();

		assertCreated(putResult, true);



		// retrieve the keyword again and verify that the 'keyword' value was changed
		KeywordDTO temp = null;
		final ClientResponse getVerifyKeywordRes = webResource.path("keywords/" + keywordName).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		getVerifyKeywordRes.bufferEntity();
		final KeywordDTO keyword = getVerifyKeywordRes.getEntity(KeywordDTO.class);


		// check if the added keyword is constructed properly
		if (keywordName.equals(keyword.getKeyword()))
		{
			temp = keyword;
		}
		assertNotNull("Expected keyword not found", temp);
		assertNotNull("Expected keyword not found", temp.getKeyword()); // NOPMD

		final Collection<ProductDTO> products = keyword.getProducts();
		if (!products.isEmpty())
		{
			temp = keyword;
		}
		assertNotNull("Expected keyword not found", temp);
		assertNotNull("Expected keyword not found", temp.getKeyword()); // NOPMD

		final ClientResponse deleteVerifyKeywordRes = webResource.path("keywords/" + keywordName).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		deleteVerifyKeywordRes.bufferEntity();

		assertOk(deleteVerifyKeywordRes, true);//check delete
	}


	@Test
	public void testPutUpdateKeyword() throws IOException
	{
		final ClientResponse keywordResult = webResource.path("/keywords").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		keywordResult.bufferEntity();
		final KeywordsDTO keywords = keywordResult.getEntity(KeywordsDTO.class);
		if (keywords.getKeywords().isEmpty())
		{
			fail("No keywords defined");
		}
		KeywordDTO keyword = null;

		for (KeywordDTO tempKeyword : keywords.getKeywords())
		{
			final ClientResponse keywordRes = webResource.path(
					"/catalogs/testCatalog1/catalogversions/testVersion1/keywords/" + tempKeyword.getKeyword()).cookie(tenantCookie)
					.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
					.get(ClientResponse.class);
			keywordRes.bufferEntity();
			tempKeyword = keywordRes.getEntity(KeywordDTO.class);
			if ("no_product_keyword".equals(tempKeyword.getKeyword()))
			{
				keyword = tempKeyword;
				break;
			}
		}
		assertNotNull("Expected keyword not found", keyword);
		assertNotNull("Expected keyword not found", keyword.getKeyword()); //NOPMD
		// ---- koniec GET -----


		KeywordDTO temp = null;
		final ClientResponse keywordRes = webResource.path(
				"/catalogs/testCatalog1/catalogversions/testVersion1/keywords/no_product_keyword").cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		keywordRes.bufferEntity();
		keyword = keywordRes.getEntity(KeywordDTO.class);

		Collection<ProductDTO> products = keyword.getProducts();
		temp = keyword;
		// we expect 'no_product_keyowod' which has no products
		assertEquals("no_product_keyword", temp.getKeyword());
		Assert.assertNull(keyword.getProducts());


		//prepare entity for post request
		final KeywordDTO putEntry = new KeywordDTO();
		final ProductDTO product = new ProductDTO();
		product.setCode("testProduct1");
		final CategoryDTO category = new CategoryDTO();
		category.setCode("testCategory1");
		final CatalogVersionDTO version = new CatalogVersionDTO();
		final CatalogDTO catalog = new CatalogDTO();
		catalog.setName("testCatalog1");
		catalog.setId("testCatalog1");
		version.setCatalog(catalog);
		version.setVersion("testVersion1");
		final LanguageDTO language = new LanguageDTO();
		language.setIsocode("en");

		product.setCode("testProduct1");
		putEntry.setKeyword("no_product_keyword");
		putEntry.setProducts(Arrays.asList(product));
		putEntry.setCategories(Arrays.asList(category));
		putEntry.setCatalogVersion(version);
		putEntry.setLanguage(language);


		// perform put with "after_put" value
		final ClientResponse entryResult = webResource.path(
				"/catalogs/testCatalog1/catalogversions/testVersion1/keywords/" + keyword.getKeyword()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(putEntry).put(
				ClientResponse.class);
		entryResult.bufferEntity();

		// retrieve the keyword again and verify that the 'keyword' value was changed
		temp = null;
		final ClientResponse keywordRes2 = webResource.path(
				"/catalogs/testCatalog1/catalogversions/testVersion1/keywords/" + keyword.getKeyword()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		keywordRes2.bufferEntity();
		keyword = keywordRes2.getEntity(KeywordDTO.class);

		if ("no_product_keyword".equals(keyword.getKeyword()))
		{
			temp = keyword;
		}
		assertNotNull("Expected keyword not found", temp);
		assertNotNull("Expected keyword not found", temp.getKeyword()); // NOPMD

		products = keyword.getProducts();
		if (!products.isEmpty())
		{
			temp = keyword;
		}
		assertNotNull("Expected keyword not found", temp);
		assertNotNull("Expected keyword not found", temp.getKeyword()); // NOPMD
	}

	@Test
	public void testDeleteKeyword()
	{
		//loading of the necessary data
		final CatalogVersionModel catalogVersion = catalogService.getCatalogVersion("testCatalog1", "testVersion1");
		final ProductModel productModel = productService.getProduct(catalogVersion, "testProduct1");
		final List<ProductModel> products = Arrays.asList(productModel);
		final List<CategoryModel> categories = catalogVersion.getRootCategories();

		// creation of the test keyword for deleting
		final String keywordName = "keyword_for_delete";
		final KeywordModel keywordModel1 = modelService.create(KeywordModel.class);
		keywordModel1.setKeyword(keywordName);
		keywordModel1.setLanguage(i18nService.getLanguage("en"));
		keywordModel1.setCatalogVersion(catalogVersion);
		keywordModel1.setProducts(products);
		keywordModel1.setCategories(categories);
		modelService.save(keywordModel1);
		modelService.refresh(keywordModel1);

		//prepare DTOs for creating of keyword
		final ProductDTO product = new ProductDTO();
		product.setCode("testProduct1");
		final CategoryDTO category = new CategoryDTO();
		category.setCode("testCategory1");
		final CatalogVersionDTO version = new CatalogVersionDTO();
		final CatalogDTO catalog = new CatalogDTO();
		catalog.setName("testCatalog1");
		catalog.setId("testCatalog1");
		version.setCatalog(catalog);
		version.setVersion("testVersion1");
		final LanguageDTO language = new LanguageDTO();
		language.setIsocode("en");
		product.setCode("testProduct1");

		//define keyword DTO
		final KeywordDTO keyword = new KeywordDTO();
		keyword.setKeyword(keywordName);
		keyword.setProducts(Arrays.asList(product));
		keyword.setCategories(Arrays.asList(category));
		keyword.setCatalogVersion(version);
		keyword.setLanguage(language);

		// delete keyword with DELETE
		final ClientResponse result = webResource.path(
				"/catalogs/testCatalog1/catalogversions/testVersion1/keywords/" + keywordName).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);
	}


	protected void createTestKeywords() throws ConsistencyCheckException
	{
		final CatalogVersionModel catalogVersion = catalogService.getCatalogVersion("testCatalog1", "testVersion1");
		final ProductModel product = productService.getProduct(catalogVersion, "testProduct1");
		final List<ProductModel> products = Arrays.asList(product);
		final List<CategoryModel> categories = catalogVersion.getRootCategories();

		// creation of the first test keyword
		final KeywordModel keywordModel1 = modelService.create(KeywordModel.class);
		keywordModel1.setKeyword("before_test");
		keywordModel1.setLanguage(i18nService.getLanguage("en"));
		keywordModel1.setCatalogVersion(catalogVersion);
		keywordModel1.setProducts(products);
		keywordModel1.setCategories(categories);
		modelService.save(keywordModel1);
		modelService.refresh(keywordModel1);

		// creation of the second test keyword
		final KeywordModel keywordModel2 = modelService.create(KeywordModel.class);
		keywordModel2.setKeyword("empty_keyword");
		keywordModel2.setLanguage(i18nService.getLanguage("en"));
		keywordModel2.setCatalogVersion(catalogVersion);
		keywordModel2.setProducts(products);
		keywordModel2.setCategories(categories);
		modelService.save(keywordModel2);
		modelService.refresh(keywordModel2);

		// creation of the third test keyword
		final KeywordModel keywordModel3 = modelService.create(KeywordModel.class);
		keywordModel3.setKeyword("no_product_keyword");
		keywordModel3.setLanguage(i18nService.getLanguage("en"));
		keywordModel3.setCatalogVersion(catalogVersion);

		final List<ProductModel> myproducts = new ArrayList<ProductModel>();
		keywordModel2.setProducts(myproducts);

		keywordModel2.setCategories(categories);
		modelService.save(keywordModel3);
		modelService.refresh(keywordModel3);
	}
}
