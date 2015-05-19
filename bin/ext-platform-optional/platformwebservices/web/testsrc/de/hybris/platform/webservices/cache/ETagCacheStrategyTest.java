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
package de.hybris.platform.webservices.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import de.hybris.platform.core.dto.user.TitleDTO;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservices.AbstractWebServicesTest;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 * Integrational test for etag cached strategy.
 * 
 */
public class ETagCacheStrategyTest extends AbstractWebServicesTest
{

	private static final Logger LOG = Logger.getLogger(ETagCacheStrategyTest.class);

	private static final String URI = "titles/";

	private static final String newCode = "newCode1";

	private static final String code = "testCode1";


	@Resource
	private ModelService modelService;

	@Before
	public void setUpProducts() throws ConsistencyCheckException, JaloBusinessException
	{
		final TitleModel model = modelService.create(TitleModel.class);
		model.setCode(code);
		model.setName("name1");


		modelService.save(model);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>" + model.getPk());
		modelService.detach(model);

	}

	@After
	public void clearUpProducts() throws ConsistencyCheckException, JaloBusinessException
	{
		final TitleModel modelExample = modelService.create(TitleModel.class);
		modelExample.setCode(newCode);

		try
		{

			final TitleModel model = modelService.getByExample(modelExample);
			LOG.debug(">>>>>>>>>>>>>>>>>>>>>" + model.getPk());
			modelService.remove(model);
			modelService.detach(model);
		}
		catch (final ModelNotFoundException mnfe)
		{
			LOG.info("There was nothing to clear after test.");
		}

	}


	@Test
	public void testSimpleEtagCache()
	{

		//check single resource
		final ClientResponse resultSingle = webResource.path(URI + code).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);

		final String etag = resultSingle.getHeaders().getFirst("ETag");
		Assert.assertNotNull(etag);
		resultSingle.bufferEntity();
		assertOk(resultSingle, false);

		final ClientResponse resultSingle2 = webResource.path(URI + code).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).header("If-None-Match", etag)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);


		resultSingle2.bufferEntity();
		assertNotModified(resultSingle2);
		assertNull("Cached response should have null etag ", resultSingle2.getHeaders().getFirst("ETag"));
		//check collections resource

		final ClientResponse resultColl = webResource.path(URI).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);

		final String etag2 = resultColl.getHeaders().getFirst("ETag");
		Assert.assertNotNull(etag);
		resultColl.bufferEntity();
		assertOk(resultColl, false);

		final ClientResponse resultColl2 = webResource.path(URI).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).header("If-None-Match", etag2)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);


		resultColl2.bufferEntity();
		assertNotModified(resultColl2);
		assertNull("Cached response should have null etag ", resultColl2.getHeaders().getFirst("ETag"));


	}


	@Test
	public void testChangedEtagCache()
	{
		TitleDTO titleDto = new TitleDTO();
		titleDto.setCode(newCode);
		titleDto.setName("newOneName");

		final ClientResponse resultCreate = webResource.path(URI + newCode).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(titleDto)
				.put(ClientResponse.class);
		resultCreate.bufferEntity();
		assertCreated(resultCreate, false);

		//check single resource
		final ClientResponse resultSingle = webResource.path(URI + newCode).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);

		final String etag = resultSingle.getHeaders().getFirst("ETag");
		Assert.assertNotNull(etag);
		resultSingle.bufferEntity();
		assertOk(resultSingle, false);

		titleDto = resultSingle.getEntity(TitleDTO.class);
		LOG.debug(">>>>>>>>>>>>>>>>>>>>>" + titleDto.getPk());

		titleDto.setName("_changed");

		try
		{
			//sleep a while to assure that modification time will be different after PUT
			Thread.sleep(1000);
		}
		catch (final InterruptedException e)
		{
			//nothing here
		}

		//change by post
		final ClientResponse resultUpdate = webResource.path(URI + newCode).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(titleDto)
				.put(ClientResponse.class);
		resultUpdate.bufferEntity();
		assertOk(resultUpdate, false);

		final ClientResponse resultSingle2 = webResource.path(URI + newCode).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).header("If-None-Match", etag)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);

		resultSingle2.bufferEntity();
		assertOk(resultSingle2, false);
		assertFalse("Etags should not be equal " + etag + ":" + resultSingle2.getHeaders().getFirst("ETag"),
				etag.equals(resultSingle2.getHeaders().getFirst("ETag")));


		//check collections resource
		final ClientResponse resultColl = webResource.path(URI).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);

		final String etag2 = resultColl.getHeaders().getFirst("ETag");
		Assert.assertNotNull(etag);
		resultColl.bufferEntity();
		assertOk(resultColl, false);



		final ClientResponse result = webResource.path(URI + newCode).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);


		final ClientResponse resultColl2 = webResource.path(URI).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).header("If-None-Match", etag2)
				.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);


		resultColl2.bufferEntity();
		assertOk(resultColl2, false);
		assertFalse("Etags should not be equal " + etag2 + ":" + resultColl2.getHeaders().getFirst("ETag"),
				etag2.equals(resultColl2.getHeaders().getFirst("ETag")));

	}


	protected void assertNotModified(final ClientResponse response)
	{
		assertEquals("Wrong HTTP status at response: " + response, Response.Status.NOT_MODIFIED, response.getResponseStatus());
	}

}
