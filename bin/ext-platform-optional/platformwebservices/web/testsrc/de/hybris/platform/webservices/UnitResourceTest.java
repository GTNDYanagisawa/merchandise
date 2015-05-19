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

import de.hybris.platform.core.dto.product.UnitDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * UnitResourceTest.
 */
public class UnitResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "units/";
	private Unit testUnit;

	//	private Product testProduct;

	/**
	 * @throws Exception
	 */
	public UnitResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpUnits() throws ConsistencyCheckException
	{
		createTestCatalogs();
		createTestProductsUnits();
		testUnit = ProductManager.getInstance().getUnit("testUnit1");
	}

	@Test
	public void testGetUnit()
	{
		final ClientResponse result = webResource.path(URI + testUnit.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final UnitDTO unit1 = result.getEntity(UnitDTO.class);
		assertNotNull("No unit within body at response: " + result, unit1);
		//assertEquals("Wrong conversion at response:" + result, new Double(testUnit.getConversionFactor()), unit1.getConversion());
		assertEquals("Wrong name at response:" + result, testUnit.getName(), unit1.getName());
		assertEquals("Wrong unitType at response:" + result, testUnit.getUnitType(), unit1.getUnitType());

	}

	@Test
	public void testPutUnitNew() throws ConsistencyCheckException
	{
		final UnitDTO unit = new UnitDTO();
		unit.setCode("newUnit");
		unit.setConversion(new Double(1.0));
		unit.setName("new unit");
		unit.setUnitType("packaging-newUnit");

		final ClientResponse result = webResource.path(URI + unit.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(unit).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		final Unit newUnit = ProductManager.getInstance().getUnit("newUnit");
		assertEquals("Code was not changed correct", "newUnit", newUnit.getCode());
		assertEquals("Conversion was not changed correct", new Double(1.0), new Double(newUnit.getConversionFactor()));
		assertEquals("Name was not changed correct", "new unit", newUnit.getName());
		assertEquals("Unit type was not changed correct", "packaging-newUnit", newUnit.getUnitType());

		//remove created Unit
		newUnit.remove();
	}

	@Test
	public void testPutUnitUpdate()
	{
		final UnitDTO unit = new UnitDTO();
		unit.setCode("testUnit1");
		unit.setConversion(new Double(1.0));
		unit.setName("test unit");
		unit.setUnitType("packaging-testUnit");

		final ClientResponse result = webResource.path(URI + unit.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(unit).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		final Unit testUnit = ProductManager.getInstance().getUnit("testUnit1");
		assertEquals("Code was not changed correct", "testUnit1", testUnit.getCode());
		assertEquals("Conversion was not changed correct", new Double(1.0), new Double(testUnit.getConversionFactor()));
		assertEquals("Name was not changed correct", "test unit", testUnit.getName());
		assertEquals("Unit type was not changed correct", "packaging-testUnit", testUnit.getUnitType());
	}

	@Test
	public void testDeleteUnit()
	{
		final ClientResponse result = webResource.path(URI + testUnit.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		boolean deleted = true;
		if (ProductManager.getInstance().getUnit("testUnit1") != null)
		{
			deleted = false;
		}
		assertTrue("Found unit however it should be deleted", deleted);
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, POST);
	}
}
