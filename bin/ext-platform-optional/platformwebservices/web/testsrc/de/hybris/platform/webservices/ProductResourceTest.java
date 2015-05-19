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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.fail;

import de.hybris.platform.catalog.dto.ProductFeatureDTO;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.core.dto.product.ProductDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.AtomicType;
import de.hybris.platform.jalo.type.AttributeDescriptor;
import de.hybris.platform.jalo.type.JaloAbstractTypeException;
import de.hybris.platform.jalo.type.JaloDuplicateQualifierException;
import de.hybris.platform.jalo.type.JaloGenericCreationException;
import de.hybris.platform.jalo.type.TypeManager;
import de.hybris.platform.variants.dto.VariantProductDTO;
import de.hybris.platform.variants.jalo.VariantProduct;
import de.hybris.platform.variants.jalo.VariantType;
import de.hybris.platform.variants.jalo.VariantsManager;
import de.hybris.platform.webservices.dto.VariantAttributeDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class ProductResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "catalogs/testCatalog1/catalogversions/testVersion1/products/";
	private static final String[][] variantAttributes =
	{
	{ "variant1", "small", "green" },
	{ "variant2", "big", "blue" } };

	private Product testProduct;


	/**
	 * @throws Exception
	 */
	public ProductResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpProducts() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCatalogs();
		createTestClassificationSystem();
		createTestProductsUnits();
		testProduct = (Product) ProductManager.getInstance().getProductsByCode("testProduct1").iterator().next();
		createProductFeatures(testProduct);

		//setup variants
		final VariantType variantType = createVariantType();
		VariantsManager.getInstance().setVariantType(testProduct, variantType);

		final CatalogManager cmi = CatalogManager.getInstance();
		final CatalogVersion version = cmi.getCatalog("testCatalog1").getCatalogVersion("testVersion1");

		createVariant(variantType, testProduct, variantAttributes[0][0], variantAttributes[0][1], variantAttributes[0][2], version);
		createVariant(variantType, testProduct, variantAttributes[1][0], variantAttributes[1][1], variantAttributes[1][2], version);

	}

	@Ignore("PLA-9062, INFRA-512 PLA-11441")
	@Test
	public void testGetProduct()
	{
		final ClientResponse result = webResource.path(URI + testProduct.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final ProductDTO product = result.getEntity(ProductDTO.class);
		assertNotNull("No product within body at response: " + result, product);
		assertEquals("Wrong product code at response: " + result, "testProduct1", product.getCode());
		assertEquals("Wrong description at response: " + result, "description", product.getDescription());
		assertEquals("Wrong name at response: " + result, "product1", product.getName());
		assertEquals("Wrong orderQuantityInterval at response: " + result, Integer.valueOf(1), product.getOrderQuantityInterval());
		assertEquals("Wrong order at response: " + result, Integer.valueOf(12), product.getOrder());
		assertEquals("Wrong remarks at response: " + result, "Remark1", product.getRemarks());
		assertEquals("Wrong onlineDate at response: " + result, CatalogManager.getInstance().getOnlineDate(testProduct), product
				.getOnlineDate());
		assertEquals("Wrong offlineDate at response: " + result, CatalogManager.getInstance().getOnlineDate(testProduct), product
				.getOfflineDate());
		assertEquals("Wrong manufacturerAID at response: " + result, "manufacturerAID", product.getManufacturerAID());
		assertEquals("Wrong erpGroupBuyer at response: " + result, "erpGroupBuyer", product.getErpGroupBuyer());
		assertEquals("Wrong numberContentUnits at response: " + result, Double.valueOf(1.0), product.getNumberContentUnits());
		assertEquals("Wrong maxOrderQuantity at response: " + result, Integer.valueOf(20), product.getMaxOrderQuantity());
		assertEquals("Wrong erpGroupSupplier at response: " + result, "erpGroupSupplier", product.getErpGroupSupplier());
		assertEquals("Wrong ean at response: " + result, "ean", product.getEan());
		assertEquals("Wrong minOrderQuantity at response: " + result, Integer.valueOf(1), product.getMinOrderQuantity());
		assertEquals("Wrong priceQuantity at response: " + result, Double.valueOf(2.0), product.getPriceQuantity());
		assertEquals("Wrong deliveryTime at response: " + result, Double.valueOf(20.0), product.getDeliveryTime());
		assertEquals("Wrong priceQuantity at response:" + result, CatalogManager.getInstance().getPriceQuantity(testProduct),
				product.getPriceQuantity());
		assertEquals("Wrong segment at response:" + result, "segment", product.getSegment());
		assertEquals("Wrong manufacturerTypeDescription at response:" + result, "manufacturerTypeDescription", product
				.getManufacturerTypeDescription());
		assertEquals("Wrong manufacturerName at response:" + result, "manufacturerName", product.getManufacturerName());
		assertEquals("Wrong supplierAlternativeAID at response:" + result, "supplierAlternativeAID", product
				.getSupplierAlternativeAID());
		assertEquals("Wrong unit id at response:" + result, testProduct.getUnit().getCode(), product.getUnit().getCode());
		assertEquals("Wrong contentUnit name at response:" + result, CatalogManager.getInstance().getContentUnit(testProduct)
				.getName(), product.getContentUnit().getName());

		//test product features
		assertEquals("Wrong number of product features at response:" + result, 2, product.getFeatures().size());
		for (final ProductFeatureDTO feature : product.getFeatures())
		{
			assertNotNull("feature 'qualifier' property must not be null", feature.getQualifier());
			assertNotNull("feature 'code' property must not be null", feature.getCode());
			assertNotNull("feature 'value' property must not be null", feature.getValue());
			assertNotNull("feature 'name' property must not be null", feature.getName());
			assertNotNull("feature 'unit' property must not be null", feature.getUnit());
		}

		//test variants
		assertEquals("Wrong number of product variants at response: " + result, 2, product.getVariants().size());

		for (final VariantProductDTO variant : product.getVariants())
		{
			assertNotNull("variant URI is null", variant.getUri());
			try
			{
				testVariantProduct(variant.getUri(), variant.getCode());
			}
			catch (final URISyntaxException ex)
			{
				fail("URISyntaxException thrown while accesing variant product resource: " + ex.toString());
			}
		}
	}

	private void testVariantProduct(final String uri, final String code) throws URISyntaxException
	{
		final ClientResponse result = webResource.uri(new java.net.URI(uri)).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final VariantProductDTO variantProduct = result.getEntity(VariantProductDTO.class);
		assertEquals("Wrong variant product code at response: " + result, code, variantProduct.getCode());
		assertEquals("Wrong number of product variant attributes at response: " + result, 2, variantProduct.getVariantAttributes()
				.size());

		final int index = Integer.parseInt(variantProduct.getCode().substring(variantProduct.getCode().length() - 1)) - 1;

		for (final VariantAttributeDTO attribute : variantProduct.getVariantAttributes())
		{
			assertNotNull("variant attribute name is null", attribute.getQualifier());
			assertNotNull("variant attribute value is null", attribute.getValue());

			if ("size".equals(attribute.getQualifier()))
			{
				assertEquals("Wrong variant attribute value (size) at response: " + result, variantAttributes[index][1], attribute
						.getValue());
			}
			else if ("color".equals(attribute.getQualifier()))
			{
				assertEquals("Wrong variant attribute value (color) at response: " + result, variantAttributes[index][2], attribute
						.getValue());
			}
			else
			{
				fail();
			}
		}
	}

	@Test
	public void testPutProductNew() throws ConsistencyCheckException
	{
		final ProductDTO product = new ProductDTO();
		product.setCode("newProduct");
		product.setDescription("description");

		final ClientResponse result = webResource.path(URI + "newProduct").cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(product).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		final Product newProduct = (Product) ProductManager.getInstance().getProductsByCode("newProduct").iterator().next();
		assertEquals("Description was not changed correct", "description", newProduct.getDescription());

		//remove created Product
		newProduct.remove();
	}

	@Test
	public void testPutProductUpdate()
	{
		final ProductDTO product = new ProductDTO();
		product.setCode("testProduct1");
		product.setDescription("newDescription");

		final ClientResponse result = webResource.path(URI + testProduct.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(product).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		final Product newProduct = (Product) ProductManager.getInstance().getProductsByCode("testProduct1").iterator().next();
		assertEquals("Description was not changed correct", "newDescription", newProduct.getDescription());
	}

	@Test
	public void testDeleteProduct()
	{
		ClientResponse result = webResource.path(URI + testProduct.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + "testProduct1").cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + testProduct.getCode(), POST);
	}


	private VariantType createVariantType() throws JaloDuplicateQualifierException
	{
		final VariantsManager manager = VariantsManager.getInstance();
		final TypeManager typeManager = JaloSession.getCurrentSession().getTypeManager();
		final VariantType shoeType = manager.createVariantType(Collections.singletonMap(VariantType.CODE, "variantTestType"));

		shoeType.createVariantAttributeDescriptor("size", (AtomicType) typeManager.getAtomicTypesForJavaClass(String.class)
				.iterator().next(), AttributeDescriptor.REMOVE_FLAG + AttributeDescriptor.READ_FLAG + AttributeDescriptor.WRITE_FLAG
				+ AttributeDescriptor.SEARCH_FLAG);

		shoeType.createVariantAttributeDescriptor("color", (AtomicType) typeManager.getAtomicTypesForJavaClass(String.class)
				.iterator().next(), AttributeDescriptor.REMOVE_FLAG + AttributeDescriptor.READ_FLAG + AttributeDescriptor.WRITE_FLAG
				+ AttributeDescriptor.SEARCH_FLAG);

		return shoeType;
	}

	private VariantProduct createVariant(final VariantType variantType, final Product base, final String code, final String size,
			final String color, final CatalogVersion catalogVersion) throws JaloGenericCreationException, JaloAbstractTypeException,
			JaloBusinessException
	{
		final Map attributes = new HashMap();
		attributes.put(Product.CODE, code);
		attributes.put(VariantProduct.BASEPRODUCT, base);
		attributes.put("size", size);
		attributes.put("color", color);

		final VariantProduct result = (VariantProduct) variantType.newInstance(attributes);

		CatalogManager.getInstance().setCatalogVersion(result, catalogVersion);

		return result;
	}
}
