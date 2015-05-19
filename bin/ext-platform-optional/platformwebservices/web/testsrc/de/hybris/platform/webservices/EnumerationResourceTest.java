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

import de.hybris.platform.core.dto.enumeration.EnumerationMetaTypeDTO;
import de.hybris.platform.core.dto.enumeration.EnumerationMetaTypesDTO;
import de.hybris.platform.core.dto.enumeration.EnumerationValueDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationType;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.type.JaloDuplicateCodeException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class EnumerationResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "enumerationmetatypes/";
	private static final String TEST_ENUMERATION_TYPE_CODE = "TestEnumType";
	private static final String TEST_ENUMERATION_VALUE[] =
	{ "TestEnumValue1", "TestEnumValue2" };

	//NOPMD
	public EnumerationResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpEnumerations() throws ConsistencyCheckException
	{
		//set up the enumerations before tests
	}

	@After
	public void tearDownenumerations()
	{
		try
		{
			EnumerationManager.getInstance().getEnumerationValue(TEST_ENUMERATION_TYPE_CODE, TEST_ENUMERATION_VALUE[0]).remove();
		}
		catch (final Throwable e)
		{
			//nothing to do 
		}
		try
		{
			EnumerationManager.getInstance().getEnumerationValue(TEST_ENUMERATION_TYPE_CODE, TEST_ENUMERATION_VALUE[1]).remove();
		}
		catch (final Throwable e)
		{
			//nothing to do 
		}
		try
		{
			EnumerationManager.getInstance().getEnumerationType(TEST_ENUMERATION_TYPE_CODE).remove();
		}
		catch (final Throwable e)
		{
			//nothing to do 
		}
	}

	@Test
	public void testGetEnumerations() throws JaloDuplicateCodeException, ConsistencyCheckException
	{
		//create enumeration type with one enumeration value - JALO
		EnumerationManager.getInstance().createDefaultEnumerationType(TEST_ENUMERATION_TYPE_CODE);
		EnumerationManager.getInstance().createEnumerationValue(TEST_ENUMERATION_TYPE_CODE, TEST_ENUMERATION_VALUE[0]);

		//GET all enumeration types
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final EnumerationMetaTypesDTO enumerations = result.getEntity(EnumerationMetaTypesDTO.class);
		assertNotNull("No enumerations found at response: " + result, enumerations);
		assertIsNotNull(enumerations.getEnumerationMetaTypes(), EnumerationMetaTypeDTO.class, "code", "uri");
	}

	@Test
	public void testGetEnumeration() throws JaloDuplicateCodeException, ConsistencyCheckException
	{
		//create enumeration type with one enumeration value - JALO
		final EnumerationType enumType = EnumerationManager.getInstance().createDefaultEnumerationType(TEST_ENUMERATION_TYPE_CODE);
		final EnumerationValue enumValue = EnumerationManager.getInstance().createEnumerationValue(TEST_ENUMERATION_TYPE_CODE,
				TEST_ENUMERATION_VALUE[0]);

		//GET this enumeration type
		ClientResponse result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final EnumerationMetaTypeDTO enumTypeDto = result.getEntity(EnumerationMetaTypeDTO.class);
		assertNotNull("Not enumeration in body: ", enumTypeDto);
		assertEquals("Wrong code: ", enumType.getCode(), enumTypeDto.getCode());

		//GET this enumeration value and check (Assert)
		result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE + "/" + TEST_ENUMERATION_VALUE[0]).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final EnumerationValueDTO enumValueDto = result.getEntity(EnumerationValueDTO.class);
		assertNotNull("Not enumeration in body: ", enumValueDto);
		assertEquals("Wrong code: ", enumValue.getCode(), enumValueDto.getCode());
	}


	@Test
	public void testPutNewEnumeration() throws ConsistencyCheckException
	{
		//PUT new enumeration type
		final EnumerationMetaTypeDTO enumTypeDto = new EnumerationMetaTypeDTO();
		enumTypeDto.setCode(TEST_ENUMERATION_TYPE_CODE);

		ClientResponse result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(enumTypeDto).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);


		//load this enumeration type and check - JALO
		final EnumerationType enumType = EnumerationManager.getInstance().getEnumerationType(TEST_ENUMERATION_TYPE_CODE);
		assertNotNull("The enumeration type wasn't created", enumType);
		assertEquals("The enumeration type code wasn't saved correctly: ", enumType.getCode(), enumTypeDto.getCode());

		//PUT new enumeration value 
		final EnumerationValueDTO enumValueDto = new EnumerationValueDTO();
		enumValueDto.setName(TEST_ENUMERATION_VALUE[0]);
		enumValueDto.setCode(TEST_ENUMERATION_VALUE[0]);

		result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE + "/" + TEST_ENUMERATION_VALUE[0]).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(enumValueDto).put(
				ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		//load new enumeration value and check - JALO
		final EnumerationValue enumValue = EnumerationManager.getInstance().getEnumerationValue(TEST_ENUMERATION_TYPE_CODE,
				TEST_ENUMERATION_VALUE[0]);
		assertNotNull("The enumeration value wasn't created", enumValue);
		assertEquals("The enumeration value wasn't saved correctly: ", enumValue.getCode(), enumValueDto.getCode());
	}

	@Test
	public void testPutUpdateEnumeration()
	{
		//PUT new enumeration type
		final EnumerationMetaTypeDTO enumTypeDto = new EnumerationMetaTypeDTO();
		enumTypeDto.setCode(TEST_ENUMERATION_TYPE_CODE);

		ClientResponse result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(enumTypeDto).put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		//PUT new enumeration value 
		final EnumerationValueDTO enumValueDto = new EnumerationValueDTO();
		enumValueDto.setCode(TEST_ENUMERATION_VALUE[0]); //code
		enumValueDto.setName(TEST_ENUMERATION_VALUE[0]);

		result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE + "/" + TEST_ENUMERATION_VALUE[0]).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(enumValueDto).put(
				ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		//PUT update the enumeration value and check (JALO)
		enumValueDto.setCode(TEST_ENUMERATION_VALUE[0]); //code
		enumValueDto.setName(TEST_ENUMERATION_VALUE[1]);
		result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE + "/" + TEST_ENUMERATION_VALUE[0]).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(enumValueDto).put(
				ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		//load this enumeration value and check - JALO
		final EnumerationValue enumValue = EnumerationManager.getInstance().getEnumerationValue(TEST_ENUMERATION_TYPE_CODE,
				TEST_ENUMERATION_VALUE[0]);
		assertNotNull("The enumeration value wasn't created", enumValue);
		assertEquals("The enumeration value wasn't saved correctly: ", enumValue.getName(), enumValueDto.getName());
	}

	@Test
	public void testDeleteEnumeration() throws JaloDuplicateCodeException, ConsistencyCheckException
	{

		//create enumeration type with two enumeration value - JALO
		EnumerationManager.getInstance().createDefaultEnumerationType(TEST_ENUMERATION_TYPE_CODE);
		EnumerationManager.getInstance().createEnumerationValue(TEST_ENUMERATION_TYPE_CODE, TEST_ENUMERATION_VALUE[0]);
		EnumerationManager.getInstance().createEnumerationValue(TEST_ENUMERATION_TYPE_CODE, TEST_ENUMERATION_VALUE[1]);

		//DELETE one enumeration value and check (JALO)
		ClientResponse result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE + "/" + TEST_ENUMERATION_VALUE[1]).cookie(
				tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(
				ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		try
		{
			final EnumerationValue enumValue = EnumerationManager.getInstance().getEnumerationValue(TEST_ENUMERATION_TYPE_CODE,
					TEST_ENUMERATION_VALUE[1]);
			assertNull("The enumeration value wasn't deleted", enumValue);
		}
		catch (final JaloItemNotFoundException e)
		{
			//do nothing: correct way
		}

		// switch off logger of com.sun.jersey.server.impl.application.WebApplicationImpl to
		// avoid jersey logging a error due to 500 response
		final java.util.logging.Logger loggerWebAppImpl = java.util.logging.Logger
				.getLogger("com.sun.jersey.server.impl.application.WebApplicationImpl");
		final java.util.logging.Level levelWebAppImpl = loggerWebAppImpl.getLevel();
		loggerWebAppImpl.setLevel(java.util.logging.Level.OFF);

		//switch off logger also for de.hybris.platform.webservices.AbstractResource 
		//avoid stack trace in standalone mode, printed from YWebservicesException#notifyLogger
		final Logger loggerAbstractResource = Logger.getLogger(AbstractResource.class);
		final Level levelAbstractResource = loggerAbstractResource.getLevel();
		loggerAbstractResource.setLevel(Level.OFF);

		// switch off logger of AbstractYResponseBuilder which is also
		// logging the exception
		final Logger loggerYResponseBuilder = Logger.getLogger(AbstractYResponseBuilder.class);
		final Level levelYResponseBuilder = loggerYResponseBuilder.getLevel();
		loggerYResponseBuilder.setLevel(Level.OFF);
		try
		{
			//try to DELETE the enumeration type with enumeration value
			result = webResource.path(URI + TEST_ENUMERATION_TYPE_CODE).cookie(tenantCookie).header(HEADER_AUTH_KEY,
					HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
			result.bufferEntity();
			assertEquals("Wrong HTTP status at response: " + result, 200, result.getStatus());
		}
		finally
		{
			loggerYResponseBuilder.setLevel(levelYResponseBuilder);
			loggerWebAppImpl.setLevel(levelWebAppImpl);
			loggerAbstractResource.setLevel(levelAbstractResource);
		}

		try
		{
			final EnumerationType enumType = EnumerationManager.getInstance().getEnumerationType(TEST_ENUMERATION_TYPE_CODE);
			assertNull("The enumeration type wasn't deleted", enumType);
		}
		catch (final JaloItemNotFoundException e)
		{
			//do nothing: correct way
		}
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + TEST_ENUMERATION_TYPE_CODE, POST);
	}

}
