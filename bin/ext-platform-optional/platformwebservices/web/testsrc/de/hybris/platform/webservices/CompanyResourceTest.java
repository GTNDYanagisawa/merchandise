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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.dto.CompanyDTO;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.Company;
import de.hybris.platform.core.dto.c2l.CountryDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.c2l.C2LManager;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class CompanyResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "companies/";
	private static final String TEST_COMPANY_UID = "testCompany1";
	private static final String NEW_COMPANY_UID = "testCompanyNew";
	private static final String NEW_COMPANY_NAME = "testCompanyNew name";
	private static final String NEW_COMPANY_VATID = "TC 123456789";
	private static final Boolean NEW_COMPANY_BUYER = Boolean.FALSE;
	private static final Boolean NEW_COMPANY_MANUFACTURER = Boolean.TRUE;
	private static final String NEW_COMPANY_COUNTRY_ISOCODE = "newCountryToCreate";


	private Company testCompany;

	public CompanyResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpCompanies() throws ConsistencyCheckException, JaloBusinessException
	{
		createTestCompanies();
		testCompany = CatalogManager.getInstance().getCompanyByUID(TEST_COMPANY_UID);
	}

	@Test
	public void testGetCompany()
	{
		final ClientResponse result = webResource.path(URI + testCompany.getUID()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CompanyDTO company = result.getEntity(CompanyDTO.class);
		assertNotNull("No company within body at response: " + result, company);
		assertTrue("Invalid company in response: " + result, company.getUid().equals(testCompany.getUID()));
		assertFalse("Company name must be different from its UID: " + result, company.getUid().equals(company.getName()));
		assertEquals("Invalid country in response: ", "testCountry1", company.getCountry().getIsocode());
		assertEquals("Invalid number of company's addresses in response: ", 2, company.getAddresses().size());
	}

	@Test
	public void testGetCompanyJsonFormat()
	{
		final ClientResponse result = webResource.path(URI + testCompany.getUID()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		result.bufferEntity();
		//assertOk() not used because of different media type: APPLICATION_JSON_TYPE
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		assertTrue("Wrong content-type at response: " + result, MediaType.APPLICATION_JSON_TYPE.isCompatible(result.getType()));

		final CompanyDTO company = result.getEntity(CompanyDTO.class);
		assertNotNull("No company within body at response: " + result, company);
		assertTrue("Invalid company in response: " + result, company.getUid().equals(testCompany.getUID()));
		assertFalse("Company name must be different from its UID: " + result, company.getUid().equals(company.getName()));
	}

	@Test
	public void testPutNewCompany() throws ConsistencyCheckException
	{
		final CountryDTO newCountryDto = new CountryDTO();
		newCountryDto.setIsocode(NEW_COMPANY_COUNTRY_ISOCODE);
		newCountryDto.setName(NEW_COMPANY_COUNTRY_ISOCODE + " name");

		final CompanyDTO newCompanyDto = new CompanyDTO();
		newCompanyDto.setUid(NEW_COMPANY_UID);
		newCompanyDto.setName(NEW_COMPANY_NAME);
		newCompanyDto.setBuyer(NEW_COMPANY_BUYER);
		newCompanyDto.setManufacturer(NEW_COMPANY_MANUFACTURER);
		newCompanyDto.setVatID(NEW_COMPANY_VATID);
		newCompanyDto.setCountry(newCountryDto);

		//send message for creating of the company 
		final ClientResponse result = webResource.path(URI + NEW_COMPANY_UID).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(newCompanyDto).put(ClientResponse.class);
		result.bufferEntity();

		//check the response 
		assertCreated(result, true);

		//check if the company has been created
		final Company createdCompany = CatalogManager.getInstance().getCompanyByUID(NEW_COMPANY_UID);
		assertNotNull("The company wasn't created", createdCompany);
		assertEquals("The company's name property wasn't saved correctly", createdCompany.getName(), NEW_COMPANY_NAME);
		assertEquals("The company's buyer property wasn't saved correctly", createdCompany.isBuyer(), NEW_COMPANY_BUYER);
		assertEquals("The company's manufacturer property wasn't saved correctly", createdCompany.isManufacturer(),
				NEW_COMPANY_MANUFACTURER);
		assertEquals("The company's vatID property wasn't saved correctly", createdCompany.getVatID(), NEW_COMPANY_VATID);
		assertEquals("The company's country property wasn't saved correctly", NEW_COMPANY_COUNTRY_ISOCODE, createdCompany
				.getCountry().getIsoCode());
		assertEquals("The company's country property: name wasn't saved correctly", NEW_COMPANY_COUNTRY_ISOCODE + " name",
				createdCompany.getCountry().getName());

		//remove company
		createdCompany.remove();

		//remove country
		C2LManager.getInstance().getCountryByIsoCode(NEW_COMPANY_COUNTRY_ISOCODE).remove();

	}

	@Test
	public void testDeleteCompany() throws IOException
	{
		//create new company DTO 
		final CompanyDTO companyDto = new CompanyDTO();
		companyDto.setUid(NEW_COMPANY_UID);
		companyDto.setName(NEW_COMPANY_NAME);

		//send message for creating of the company 
		ClientResponse result = webResource.path(URI + NEW_COMPANY_UID).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(companyDto).put(ClientResponse.class);
		result.bufferEntity();

		//check if the company has been created 
		final Company company = CatalogManager.getInstance().getCompanyByUID(NEW_COMPANY_UID);
		assertEquals("The company wasn't created", NEW_COMPANY_UID, company.getUID());

		//send message for deleting of the test company
		result = webResource.path(URI + NEW_COMPANY_UID).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);
		result.bufferEntity();
		//check the response
		assertOk(result, true);

		//check if the company has been deleted 
		final Company removedCompany = CatalogManager.getInstance().getCompanyByUID(NEW_COMPANY_UID);
		assertNull("The company wasn't deleted", removedCompany);
	}

	@Test
	public void testDeleteNonExistingCompany() throws IOException
	{
		//create new company DTO 
		final CompanyDTO companyDto = new CompanyDTO();
		companyDto.setUid("nonExisting");
		companyDto.setName("nonExisting");

		//send message for deleting of the non-existing company
		final ClientResponse result = webResource.path(URI + NEW_COMPANY_UID).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();

		//check the response
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());
	}

	@Test
	public void testPutUpdateCompanyWithExistingCountry() throws IOException
	{
		final String EXISTING_COUNTRY_ISOCODE = "testCountry2";

		//Get an existing country using webresource
		final ClientResponse countryResult = webResource.path("countries/" + EXISTING_COUNTRY_ISOCODE).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		countryResult.bufferEntity();
		assertOk(countryResult, false);
		final CountryDTO existingCountry = countryResult.getEntity(CountryDTO.class);
		assertEquals("Country " + EXISTING_COUNTRY_ISOCODE + " not found!", EXISTING_COUNTRY_ISOCODE, existingCountry.getIsocode());

		testUpdateCompany("testCompany2", existingCountry);
	}

	@Ignore("PLA-11441 This does not work due to PLA-7687 (PLA-8262)")
	@Test
	public void testPutUpdateCompanyWithNewCountry() throws ConsistencyCheckException
	{
		final CountryDTO newCountryDto = new CountryDTO();
		newCountryDto.setIsocode(NEW_COMPANY_COUNTRY_ISOCODE);
		newCountryDto.setName(NEW_COMPANY_COUNTRY_ISOCODE + " name");

		testUpdateCompany("testCompany3", newCountryDto);

		//if we did actually create new country, it should be removed now. 
		C2LManager.getInstance().getCountryByIsoCode(NEW_COMPANY_COUNTRY_ISOCODE).remove();
	}

	/*
	 * Helper method to test update of a Company with existing country or newly created countryDTO
	 */
	private void testUpdateCompany(final String companyUid, final CountryDTO countryDTO)
	{
		final Boolean CHANGED_COMPANY_BUYER = Boolean.FALSE;
		final String CHANGED_COMPANY_DESCRIPTION = "testCompanyChanged description";
		final Boolean CHANGED_COMPANY_MANUFACTURER = Boolean.TRUE;
		final String CHANGED_COMPANY_NAME = "testCompanyChanged name";
		final String CHANGED_COMPANY_VATID = "TC 123456789";

		//create new company DTO 
		final CompanyDTO companyDto = new CompanyDTO();
		companyDto.setUid(companyUid);
		companyDto.setBuyer(CHANGED_COMPANY_BUYER);
		companyDto.setDescription(CHANGED_COMPANY_DESCRIPTION);
		companyDto.setManufacturer(CHANGED_COMPANY_MANUFACTURER);
		companyDto.setName(CHANGED_COMPANY_NAME);
		companyDto.setVatID(CHANGED_COMPANY_VATID);
		companyDto.setCountry(countryDTO);

		//send message for updating of the test company
		final ClientResponse result = webResource.path(URI + companyUid).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(companyDto).put(ClientResponse.class);
		result.bufferEntity();
		//check the response 
		assertOk(result, true);

		//check if the company has been updated
		final Company retrievedCompany = CatalogManager.getInstance().getCompanyByUID(companyUid);
		assertEquals("Company's buyer property wasn't updated correctly", retrievedCompany.isBuyer(), CHANGED_COMPANY_BUYER);
		assertEquals("Company's description property wasn't updated correctly", retrievedCompany.getDescription(),
				CHANGED_COMPANY_DESCRIPTION);
		assertEquals("Company's manufacturer property wasn't updated correctly", retrievedCompany.isManufacturer(),
				CHANGED_COMPANY_MANUFACTURER);
		assertEquals("Company's name property wasn't updated correctly", retrievedCompany.getName(), CHANGED_COMPANY_NAME);
		assertEquals("Company's vatID property wasn't updated correctly", retrievedCompany.getVatID(), CHANGED_COMPANY_VATID);
		assertEquals("Company's country property wasn't updated correctly", countryDTO.getIsocode(), retrievedCompany.getCountry()
				.getIsoCode());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + testCompany.getId(), POST);
	}

}
