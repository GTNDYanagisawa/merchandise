/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 */
package com.hybris.datahub.core.rest.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.hybris.datahub.core.dto.ResultData;
import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultDataHubOutboundClientUnitTest
{
	private static final String DATA_HUB_URL = "http://localhost:9797/datahub-webapp";
	private static final String POOL_NAME = "some_pool";
	private static final String FEED_NAME = "some_feed";
	private static final String CANONICAL_TYPE = "TestCanonicalProduct";
	private static final String RAW_TYPE = "TestRawProduct";
	private static final Map<String, String> CANONICAL_FIELDS = ImmutableMap.of("attr1", "one", "attr2", "two");
	private static final Map<String, Object> RAW_FIELDS;

	static
	{
		RAW_FIELDS = new HashMap<String, Object>();
		RAW_FIELDS.put("attr1", new Integer(1));
		RAW_FIELDS.put("attr2", "two");
	}

	private final Client jerseyClient = mock(Client.class);
	private final DefaultDataHubOutboundClient datahubClient = new DefaultDataHubOutboundClient(jerseyClient);

	@Before
	public void setup() throws Exception
	{
		datahubClient.setDataHubUrl(DATA_HUB_URL);
	}

	@Test
	public void testSuccessfulDelete() throws Exception
	{
		onDelete(poolResource(), Response.Status.OK, new ResultData());

		final ResultData result = datahubClient.deleteItem(POOL_NAME, CANONICAL_TYPE, CANONICAL_FIELDS);
		assertNotNull(result);
	}

	@Test
	public void testFailedDelete() throws Exception
	{
		final String error = "An error occured";
		confirmErrorHandlingOnDeleteFromPool(Response.Status.BAD_REQUEST, error);
	}

	@Test(expected = DataHubCommunicationException.class)
	public void testCommunicationFailureDuringDelete() throws Exception
	{
		onDelete(poolResource(), new ClientHandlerException());
		datahubClient.deleteItem(POOL_NAME, CANONICAL_TYPE, CANONICAL_FIELDS);
	}

	@Test
	public void testResourceToDeleteNotFound() throws Exception
	{
		final String error = "Pool " + POOL_NAME + " not found";
		confirmErrorHandlingOnDeleteFromPool(Response.Status.NOT_FOUND, error);
	}

	@Test
	public void testDataHubInternalError() throws Exception
	{
		final String error = "Data Hub is bleeding";
		confirmErrorHandlingOnDeleteFromPool(Response.Status.INTERNAL_SERVER_ERROR, error);
	}

	@Test
	public void testDeleteByRawItemKeys() throws Exception
	{
		onDelete(feedRawItemResource(), Response.Status.OK, new ResultData());
		final ResultData res = datahubClient.deleteByFeed(FEED_NAME, RAW_TYPE, RAW_FIELDS);
		assertNotNull(res);
	}

	@Test
	public void testDeleteByRawType() throws Exception
	{
		onDelete(feedRawTypeResource(), Response.Status.OK, new ResultData());
		final ResultData res = datahubClient.deleteByFeed(FEED_NAME, RAW_TYPE);
		assertNotNull(res);
	}

	private void confirmErrorHandlingOnDeleteFromPool(final Response.Status status, final String msg) throws Exception
	{
		onDelete(poolResource(), status, msg);

		try
		{
			datahubClient.deleteItem(POOL_NAME, CANONICAL_TYPE, CANONICAL_FIELDS);
			fail("Exception expected");
		}
		catch (final Exception e)
		{
			assertTrue(e.getMessage().contains(msg));
		}
	}

	private void onDelete(final WebResource wr, final ClientHandlerException ex)
	{
		doThrow(ex).when(wr).delete(ClientResponse.class);
	}

	private ClientResponse onDelete(final WebResource wr, final Response.Status status, final Object payload)
	{
		final ClientResponse response = response(status, payload);
		doReturn(response).when(wr).delete(ClientResponse.class);
		return response;
	}

	private ClientResponse response(final Response.Status status, final Object payload)
	{
		final ClientResponse response = mock(ClientResponse.class);
		doReturn(status.getStatusCode()).when(response).getStatus();
		doReturn(payload).when(response).getEntity(payload.getClass());
		return response;
	}

	private WebResource poolResource() throws Exception
	{
		return resource("/pools/" + POOL_NAME + "/items/" + CANONICAL_TYPE, "keyFields", CANONICAL_FIELDS);
	}

	private WebResource feedRawTypeResource() throws Exception
	{
		return resource("/data-feeds/" + FEED_NAME + "/types/" + RAW_TYPE, "rawFields", null);
	}

	private WebResource feedRawItemResource() throws Exception
	{
		return resource("/data-feeds/" + FEED_NAME + "/types/" + RAW_TYPE, "rawFields", RAW_FIELDS);
	}

	private WebResource resource(final String uri, final String paramName, final Map<String, ?> attribs) throws Exception
	{
		final WebResource res = resource(uri);
		if (attribs != null && !attribs.isEmpty())
		{
			final ObjectMapper mapper = new ObjectMapper();
			final String qry = mapper.writeValueAsString(attribs);
			doReturn(res).when(res).queryParam(paramName, qry);
		}
		doReturn(res).when(jerseyClient).resource(DATA_HUB_URL + uri);
		return res;
	}

	private WebResource resource(final String uri)
	{
		final WebResource res = mock(WebResource.class, uri);
		doReturn(res).when(jerseyClient).resource(DATA_HUB_URL + uri);
		return res;
	}
}
