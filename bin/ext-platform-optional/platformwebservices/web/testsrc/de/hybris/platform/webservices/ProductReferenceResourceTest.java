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

import de.hybris.platform.catalog.dto.CatalogDTO;
import de.hybris.platform.catalog.dto.CatalogVersionDTO;
import de.hybris.platform.catalog.dto.ProductReferenceDTO;
import de.hybris.platform.catalog.dto.ProductReferencesDTO;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.jalo.ProductReference;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.dto.enumeration.EnumerationValueDTO;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.JaloDuplicateCodeException;

import java.util.NoSuchElementException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class ProductReferenceResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "catalogs/testCatalog1/catalogversions/testVersion1/products/testProduct1/productreferences/";

	public ProductReferenceResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpProductReferences() throws ConsistencyCheckException, JaloDuplicateCodeException
	{
		createTestCatalogs();
		createTestProductsUnits();

		final CatalogManager cmi = CatalogManager.getInstance();
		final CatalogVersion version2 = cmi.getCatalog("testCatalog1").getCatalogVersion("testVersion2");
		final Product product1 = ProductManager.getInstance().createProduct("testProduct1");
		cmi.setCatalogVersion(product1, version2);
		final Product product3 = ProductManager.getInstance().createProduct("testProduct3");
		cmi.setCatalogVersion(product3, version2);

		getWsUtilService();
		wsUtilService.createEnumerationType("EnumType1");
		wsUtilService.createEnumerationValue("EnumType1", "EnumValue1", "EnumName1");
		wsUtilService.createEnumerationValue("EnumType1", "EnumValue2", "EnumName1");
	}

	@Test
	public void testGetProductReferences()
	{
		//create test product reference
		final Product source = (Product) ProductManager.getInstance().getProductsByCode("testProduct1").iterator().next();
		final Product target = (Product) ProductManager.getInstance().getProductsByCode("testProduct3").iterator().next();
		final ProductReference reference = CatalogManager.getInstance().createProductReference("Referrence13", source, target,
				Integer.valueOf(1));

		//GET all product references
		final ClientResponse result = webResource.path("productreferences/").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final ProductReferencesDTO references = result.getEntity(ProductReferencesDTO.class);
		assertNotNull("No product references found at response: " + result, references);
		assertIsNotNull(references.getProductReferences(), ProductReferenceDTO.class, "pk");

		//remove test product reference
		CatalogManager.getInstance().removeFromProductReferences(source, reference);
	}

	@Test
	public void testGetProductReference()
	{
		//create test product reference
		final Product source = (Product) ProductManager.getInstance().getProductsByCode("testProduct1").iterator().next();
		final Product target = (Product) ProductManager.getInstance().getProductsByCode("testProduct3").iterator().next();
		final ProductReference orygReference = CatalogManager.getInstance().createProductReference("Reference13", source, target,
				Integer.valueOf(1));

		//GET product reference
		final ClientResponse result = webResource.path(URI + orygReference.getPK().toString()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final ProductReferenceDTO reference = result.getEntity(ProductReferenceDTO.class);
		assertNotNull("No product references found at response: " + result, reference);
		assertEqual(orygReference.getPK(), reference.getPk());

		//remove test product reference
		CatalogManager.getInstance().removeFromProductReferences(source, orygReference);
	}

	@Test
	public void testPostProductReferenceNew() throws Exception
	{
		final ProductReferenceDTO productReference = new ProductReferenceDTO();

		final CatalogDTO catalog1DTO = new CatalogDTO();
		catalog1DTO.setId("testCatalog1");

		final CatalogVersionDTO catalogVersion1DTO = new CatalogVersionDTO();
		catalogVersion1DTO.setVersion("testVersion1");
		catalogVersion1DTO.setCatalog(catalog1DTO);

		final ProductDTO product1DTO = new ProductDTO();
		product1DTO.setCode("testProduct1");
		product1DTO.setCatalogVersion(catalogVersion1DTO);

		final ProductDTO product2DTO = new ProductDTO();
		product2DTO.setCode("testProduct2");

		final EnumerationValueDTO enumerationValueDTO = new EnumerationValueDTO();
		enumerationValueDTO.setCode("EnumValue1");

		productReference.setSource(product1DTO);
		productReference.setTarget(product2DTO);
		// generated DTO
		//productReference.setReferenceType(enumerationValueDTO);
		productReference.setActive(Boolean.TRUE);
		productReference.setQuantity(Integer.valueOf(1));
		productReference.setPreselected(Boolean.FALSE);


		final ClientResponse result = webResource.path("productreferences/").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(productReference).post(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);

		final CatalogVersionModel catalogVersion = catalogService.getCatalogVersion("testCatalog1", "testVersion1");
		final ProductModel product = productService.getProduct(catalogVersion, "testProduct1");
		final ProductReferenceModel productReferenceModel = product.getProductReferences().iterator().next();
		modelService.remove(productReferenceModel);
	}

	@Test
	public void testDeleteProductReference()
	{
		//create test product reference
		final Product source = (Product) ProductManager.getInstance().getProductsByCode("testProduct1").iterator().next();
		final Product target = (Product) ProductManager.getInstance().getProductsByCode("testProduct3").iterator().next();
		final ProductReference reference = CatalogManager.getInstance().createProductReference("Reference13", source, target,
				Integer.valueOf(1));

		//DELETE one enumeration value and check (JALO)
		final ClientResponse result = webResource.path(URI + reference.getPK()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		try
		{
			final ProductReference delReference = CatalogManager.getInstance().getProductReferences("Reference13", source, target)
					.iterator().next();
			assertNull("The product reference wasn't deleted", delReference);
		}
		catch (final JaloItemNotFoundException e)
		{
			//do nothing: correct way
		}
		catch (final NoSuchElementException e)
		{
			//do nothing: correct way
		}

	}

}
