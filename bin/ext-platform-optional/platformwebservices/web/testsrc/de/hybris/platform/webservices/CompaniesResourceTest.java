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

import de.hybris.platform.catalog.dto.CompaniesDTO;
import de.hybris.platform.catalog.dto.CompanyDTO;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.Company;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class CompaniesResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "companies";

	public CompaniesResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCompanies() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCompanies();
	}

	@Test
	public void testGetCompanies() throws IOException
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();

		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		assertTrue("Wrong content-type at response: " + result, MediaType.APPLICATION_XML_TYPE.isCompatible(result.getType()));

		final CompaniesDTO companies = result.getEntity(CompaniesDTO.class);
		assertNotNull("No companies found at response: " + result, companies);
		assertEquals("Wrong number of companies found at response: " + result, 3, companies.getCompanies().size());
		for (final CompanyDTO company : companies.getCompanies())
		{
			assertNotNull("Company UID has to be set at response: " + result, company.getUid());
			assertNotNull("Company has no URI: " + result, company.getUri());
			//			assertNotNull("Company name has to be set at response: " + result, company.getName());
			//			assertFalse("Company name must be different from UID: " + result, company.getUid().equals(company.getName()));
			assertNull("No locname should be present at response: " + result, company.getLocName());
		}
	}

	@Test
	public void testPostCompanies() throws ConsistencyCheckException
	{
		final CompanyDTO newCompany = new CompanyDTO();
		final String newTestCompanyToken = "new_Test_Company";

		newCompany.setBuyer(Boolean.TRUE);
		newCompany.setManufacturer(Boolean.FALSE);
		newCompany.setUid(newTestCompanyToken);
		newCompany.setName(newTestCompanyToken + " name");
		newCompany.setDescription(newTestCompanyToken + " description");

		final ClientResponse result = webResource.path(URI).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(newCompany)
				.post(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);

		//check the DTO retrieved from response
		final CompanyDTO resultCompany = result.getEntity(CompanyDTO.class);
		assertEquals("Wrong company uid at response: " + result, newTestCompanyToken, resultCompany.getUid());
		assertEquals("Wrong company name at response: " + result, newTestCompanyToken + " name", resultCompany.getName());
		assertEquals("Wrong company description at response: " + result, newTestCompanyToken + " description",
				resultCompany.getDescription());

		//check the actual item retrieved from the system.
		final Company foundCompany = CatalogManager.getInstance().getCompanyByUID(newTestCompanyToken);
		assertEquals("Wrong company uid: " + result, newTestCompanyToken, foundCompany.getUID());
		assertEquals("Wrong company name: " + result, newTestCompanyToken + " name", foundCompany.getName());
		assertEquals("Wrong company description: " + result, newTestCompanyToken + " description", foundCompany.getDescription());

		foundCompany.remove();
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, DELETE);
	}

}
