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
 *
 *
 */
package de.hybris.platform.webservices;

import static org.fest.assertions.Assertions.assertThat;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.test.TestThreadsHolder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.MediaType;


public class ConcurrencyTest extends AbstractWebServicesTest
{
	private static final String CATALOGS_RESOURCE = "catalogs";
	private static final int HTTP_OK = 200;
	private static final int NUMBER_OF_THREADS = 15;
	private static final int WAIT_TIME = 60;

	@Before
	public void setUpCatalogs() throws ConsistencyCheckException
	{
		createTestCatalogs();
	}

	@Test
	public void shouldBeAbleToExecuteMultipleRequestsConcurrently()
	{
		final AtomicInteger succeededRequests = new AtomicInteger(0);
		final Runnable doRequest = new Runnable()
		{
			@Override
			public void run()
			{
				final ClientResponse result = webResource. //
						path(CATALOGS_RESOURCE).cookie(tenantCookie).
						header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN). //
						accept(MediaType.APPLICATION_XML). //
						get(ClientResponse.class);
				assertThat(result.getStatus()).isEqualTo(HTTP_OK);
				succeededRequests.incrementAndGet();
			}
		};
		
		final TestThreadsHolder<Runnable> testThreads = new TestThreadsHolder<>(NUMBER_OF_THREADS, doRequest);
		testThreads.startAll();
		
		assertThat(testThreads.stopAndDestroy(WAIT_TIME)).isTrue();
		assertThat(succeededRequests.intValue()).overridingErrorMessage(
				String.format("Expecting all %d requests to finish successfully but sadly only %d of them managed to do this.",
						NUMBER_OF_THREADS, succeededRequests.intValue())).isEqualTo(NUMBER_OF_THREADS);
	}
}
