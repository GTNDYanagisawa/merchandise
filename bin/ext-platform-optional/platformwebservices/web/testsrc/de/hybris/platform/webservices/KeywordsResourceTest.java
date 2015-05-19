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
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class KeywordsResourceTest extends AbstractWebServicesTest
{
	private final String rootresourceURI = "keywords/";

	public KeywordsResourceTest() throws Exception //NOPMD
	{
		super();
	}


	@Before
	public void setUpCarts() throws ConsistencyCheckException
	{
		createTestCatalogs();
		createTestProductsUnits();
		createTestKeywords();
	}

	//This path is not allowed from 4.2.2. We can do that directly via rootresource.
	//Within rootresource GET method we can use queryParameters to specify catalogVersion.

	//	@Test
	//	public void testPostSubresource() throws IOException
	//	{
	//		// TO DO:
	//		// get all keywords from root/keywords not allowed
	//		// get keyword by name from root/keywords/name not allowed
	//
	//		KeywordDTO keyword = null;
	//		KeywordsDTO keywords = null;
	//
	//		//--- GET ALL KEYWORDS [1] ---
	//		final ClientResponse keywordsResult = webResource.path(subresourceURI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
	//				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
	//		keywordsResult.bufferEntity();
	//		keywords = keywordsResult.getEntity(KeywordsDTO.class);
	//
	//
	//		if (keywords.getKeywords().isEmpty())
	//		{
	//			fail("No keywords defined");
	//		}
	//
	//		for (KeywordDTO tempKeyword : keywords.getKeywords())
	//		{
	//			// --- GET KEYWORD BY KEYWORD NAME [2] ---
	//			final ClientResponse cartResult = webResource.path(subresourceURI + tempKeyword.getKeyword()).cookie(tenantCookie)
	//					.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
	//					.get(ClientResponse.class);
	//			cartResult.bufferEntity();
	//			tempKeyword = cartResult.getEntity(KeywordDTO.class);
	//			if ("empty_keyword".equals(tempKeyword.getKeyword()))
	//			{
	//				keyword = tempKeyword;
	//				break;
	//			}
	//		}
	//		assertNotNull("Expected keyword not found", keyword);
	//		assertNotNull("Expected keyword not found", keyword.getKeyword()); //NOPMD
	//
	//
	//		// --- GET KEYWORD BY KEYWORD NAME [2] ---
	//		KeywordDTO temp = null;
	//		final ClientResponse result = webResource.path(subresourceURI + keyword.getPk()).cookie(tenantCookie).header(
	//				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
	//		result.bufferEntity();
	//		temp = result.getEntity(KeywordDTO.class);
	//		if ("empty_keyword".equals(temp.getKeyword()))
	//		{
	//			keyword = temp;
	//		}
	//		assertNotNull("Expected keyword not found", keyword);
	//		assertNotNull("Expected keyword not found", keyword.getKeyword()); //NOPMD
	//
	//		//prepare entity for post request
	//		final KeywordDTO postEntry = new KeywordDTO();
	//		final ProductDTO product = new ProductDTO();
	//		product.setCode("testProduct1");
	//		final CategoryDTO category = new CategoryDTO();
	//		category.setCode("testCategory1");
	//		final CatalogVersionDTO version = new CatalogVersionDTO();
	//		final CatalogDTO catalog = new CatalogDTO();
	//		catalog.setName("testCatalog1");
	//		catalog.setId("testCatalog1");
	//		version.setCatalog(catalog);
	//		version.setVersion("testVersion1");
	//		final LanguageDTO language = new LanguageDTO();
	//		language.setIsocode("en");
	//
	//		product.setCode("testProduct1");
	//		postEntry.setKeyword("after_post");
	//		postEntry.setProducts(Arrays.asList(product));
	//		postEntry.setCategories(Arrays.asList(category));
	//		postEntry.setCatalogVersion(version);
	//		postEntry.setLanguage(language);
	//
	//
	//		// --- POST KEYWORD FROM SUBRESOURCE[4]
	//		//perform post 
	//		final ClientResponse entryResult = webResource.path(subresourceURI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
	//				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(postEntry).post(ClientResponse.class);
	//		entryResult.bufferEntity();
	//		assertCreated(entryResult, false);
	//
	//		// retrieve the keyword and verify if it was posted successfully
	//		final ClientResponse keywordsResult2 = webResource.path(subresourceURI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
	//				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
	//		keywordsResult2.bufferEntity();
	//		keywords = keywordsResult2.getEntity(KeywordsDTO.class);
	//		if (keywords.getKeywords().isEmpty())
	//		{
	//			fail("No keywords defined");
	//		}
	//		keyword = null;
	//		for (KeywordDTO tempKeyword : keywords.getKeywords())
	//		{
	//			final ClientResponse cartResult = webResource.path(subresourceURI + tempKeyword.getKeyword()).cookie(tenantCookie)
	//					.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
	//					.get(ClientResponse.class);
	//			cartResult.bufferEntity();
	//			tempKeyword = cartResult.getEntity(KeywordDTO.class);
	//			if ("after_post".equals(tempKeyword.getKeyword()))
	//			{
	//				keyword = tempKeyword;
	//				break;
	//			}
	//		}
	//		assertNotNull("Expected keyword not found", keyword);
	//		assertNotNull("Expected keyword not found", keyword.getKeyword()); //NOPMD
	//	}

	@Test
	public void testPostRootresource() throws IOException
	{
		// TO DO:
		// get all keywords from root/keywords not allowed
		// get keyword by name from root/keywords/name not allowed

		KeywordDTO keyword = null;
		KeywordsDTO keywords = null;

		//--- GET ALL KEYWORDS [1] ---
		final ClientResponse keywordsResult = webResource.path(rootresourceURI).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		keywordsResult.bufferEntity();
		keywords = keywordsResult.getEntity(KeywordsDTO.class);

		if (keywords.getKeywords().isEmpty())
		{
			fail("No keywords defined");
		}

		for (KeywordDTO tempKeyword : keywords.getKeywords())
		{
			// --- GET KEYWORD BY KEYWORD NAME [2] ---
			final ClientResponse cartResult = webResource.path(rootresourceURI + tempKeyword.getKeyword()).cookie(tenantCookie)
					.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
					.get(ClientResponse.class);
			cartResult.bufferEntity();
			tempKeyword = cartResult.getEntity(KeywordDTO.class);
			if ("empty_keyword".equals(tempKeyword.getKeyword()))
			{
				keyword = tempKeyword;
				break;
			}
		}
		assertNotNull("Expected keyword not found", keyword);
		assertNotNull("Expected keyword not found", keyword.getKeyword()); //NOPMD

		// --- GET KEYWORD BY KEYWORD NAME [2] ---
		KeywordDTO temp = null;
		final ClientResponse result = webResource.path(rootresourceURI + keyword.getKeyword()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		temp = result.getEntity(KeywordDTO.class);
		if ("empty_keyword".equals(temp.getKeyword()))
		{
			keyword = temp;
		}
		assertNotNull("Expected keyword not found", keyword);
		assertNotNull("Expected keyword not found", keyword.getKeyword()); //NOPMD

		//prepare entity for post request
		final KeywordDTO postEntry = new KeywordDTO();
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
		postEntry.setKeyword("after_post");
		postEntry.setProducts(Arrays.asList(product));
		postEntry.setCategories(Arrays.asList(category));
		postEntry.setCatalogVersion(version);
		postEntry.setLanguage(language);

		// --- PUT KEYWORD
		final ClientResponse entryResult = webResource.path(rootresourceURI + postEntry.getKeyword()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(postEntry)
				.put(ClientResponse.class);
		entryResult.bufferEntity();
		assertCreated(entryResult, true);

		// retrieve the keyword and verify if it was posted successfully
		final ClientResponse keywordsResult2 = webResource.path(rootresourceURI).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		keywordsResult2.bufferEntity();
		keywords = keywordsResult2.getEntity(KeywordsDTO.class);
		if (keywords.getKeywords().isEmpty())
		{
			fail("No keywords defined");
		}
		keyword = null;
		for (KeywordDTO tempKeyword : keywords.getKeywords())
		{
			final ClientResponse cartResult = webResource.path(rootresourceURI + tempKeyword.getKeyword()).cookie(tenantCookie)
					.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
					.get(ClientResponse.class);
			cartResult.bufferEntity();
			tempKeyword = cartResult.getEntity(KeywordDTO.class);
			if ("after_post".equals(tempKeyword.getKeyword()))
			{
				keyword = tempKeyword;
				break;
			}
		}
		assertNotNull("Expected keyword not found", keyword);
		assertNotNull("Expected keyword not found", keyword.getKeyword()); //NOPMD
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
	}
}
