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

import de.hybris.platform.core.dto.order.payment.PaymentInfoDTO;
import de.hybris.platform.core.dto.order.payment.PaymentInfosDTO;
import de.hybris.platform.jalo.JaloBusinessException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class PaymentInfosResourceTest extends AbstractWebServicesTest
{

	private static final String URI = "/paymentinfos";

	/**
	 * @throws Exception
	 */
	public PaymentInfosResourceTest() throws Exception //NOPMD
	{
		super();
	}

	@Before
	public void setUpPaymentInfos() throws JaloBusinessException
	{
		createTestCustomers();
		createTestPaymentInfos();
	}

	@Test
	public void testGetPaymentInfos()
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final PaymentInfosDTO paymentInfos = result.getEntity(PaymentInfosDTO.class);
		assertNotNull("No paymentinfos found at response: " + result, paymentInfos);
		assertEquals("Wrong number of paymentInfo found at response: " + result, 4, paymentInfos.getPaymentInfos().size());
		assertIsNotNull(paymentInfos.getPaymentInfos(), PaymentInfoDTO.class, "pk", "uri");
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, DELETE);
	}
}
