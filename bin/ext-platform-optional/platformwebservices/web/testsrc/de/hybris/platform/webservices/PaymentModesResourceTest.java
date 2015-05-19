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

import de.hybris.platform.core.dto.order.payment.PaymentModeDTO;
import de.hybris.platform.core.dto.order.payment.PaymentModesDTO;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class PaymentModesResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "paymentmodes/";

	public PaymentModesResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpDeliveryModes() throws ConsistencyCheckException
	{
		createPaymentDeliveryModes();
	}

	@Test
	public void testGetPaymentModes()
	{
		final ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final PaymentModesDTO paymentModes = result.getEntity(PaymentModesDTO.class);
		assertNotNull("No paymentmodes found at response: " + result, paymentModes);
		assertEquals("Wrong number of paymentMode found at response: " + result, 1, paymentModes.getPaymentModes().size());

		assertIsNotNull(paymentModes.getPaymentModes(), PaymentModeDTO.class, "code", "uri");
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI, PUT, POST, DELETE);
	}
}
