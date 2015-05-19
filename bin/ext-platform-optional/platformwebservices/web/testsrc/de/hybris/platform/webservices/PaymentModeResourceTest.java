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

import de.hybris.platform.core.dto.order.payment.PaymentModeDTO;
import de.hybris.platform.core.dto.type.ComposedTypeDTO;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.order.OrderManager;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class PaymentModeResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "paymentmodes/";
	private PaymentModeModel paymentModel = null;

	public PaymentModeResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpPaymentModes() throws ConsistencyCheckException
	{
		createPaymentDeliveryModes();
		getWsUtilService();
		paymentModel = wsUtilService.getPaymentModeByCode("testPaymentMode1");
	}

	@Test
	public void testGetPaymentMode()
	{
		final ClientResponse result = webResource.path(URI + paymentModel.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final PaymentModeDTO paymentModeDTO = result.getEntity(PaymentModeDTO.class);
		assertEqual(paymentModel, paymentModeDTO, "code", "active", "name", "description");
		assertEquals("Wrong paymentMode supportedpaymentmodes at response: " + result, paymentModel.getSupportedDeliveryModes()
				.size(), paymentModeDTO.getSupportedDeliveryModes().size());
	}

	@Test
	public void testPutPaymentModeNew() throws Exception
	{
		final ComposedTypeDTO paymentInfoType = new ComposedTypeDTO();
		paymentInfoType.setCode("AdvancePaymentInfo");
		paymentInfoType.setCatalogItemType(Boolean.TRUE);
		paymentInfoType.setGenerate(Boolean.FALSE);
		paymentInfoType.setSingleton(Boolean.FALSE);

		// prepare DTO
		final PaymentModeDTO paymentModeDTO = new PaymentModeDTO();
		paymentModeDTO.setCode("newPaymentMode1");
		paymentModeDTO.setActive(Boolean.TRUE);
		paymentModeDTO.setDescription("New description");
		paymentModeDTO.setPaymentInfoType(paymentInfoType.getCode()); // code of composed type

		// Testcase1: PUT PaymentMode
		ClientResponse result = webResource.path(URI + paymentModeDTO.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(paymentModeDTO)
				.put(ClientResponse.class);
		result.bufferEntity();
		// assert creation
		super.assertCreated(result, true);

		// Testcase2: GET previously PaymentMode
		result = webResource.path(URI + paymentModeDTO.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		// assert result matches initial DTO
		final PaymentModeDTO actual = result.getEntity(PaymentModeDTO.class);
		super.assertEqual(paymentModeDTO, actual, "name", "code", "active", "description", "paymentInfoType");

		// XXX: generally that should be handled by ItemCreationListener
		OrderManager.getInstance().getPaymentModeByCode("newPaymentMode1").remove();
	}

	@Test
	public void testPutPaymentModeUpdate()
	{
		final ComposedTypeDTO paymentInfoType = new ComposedTypeDTO();
		paymentInfoType.setCode(paymentModel.getPaymentInfoType().getCode());

		final PaymentModeDTO paymentModeDTO = new PaymentModeDTO();
		paymentModeDTO.setCode(paymentModel.getCode());
		paymentModeDTO.setActive(paymentModel.getActive());
		paymentModeDTO.setDescription("New description");
		paymentModeDTO.setPaymentInfoType(paymentInfoType.getCode());
		paymentModeDTO.setName(paymentModel.getName());

		ClientResponse result = webResource.path(URI + paymentModeDTO.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(paymentModeDTO)
				.put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + paymentModeDTO.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final PaymentModeDTO paymentMode2DTO = result.getEntity(PaymentModeDTO.class);
		assertEqual(paymentModeDTO, paymentMode2DTO, "code", "active", "name", "description");
		assertEquals("Wrong paymentMode supportedpaymentmodes at response: " + result, paymentModel.getSupportedDeliveryModes()
				.size(), paymentMode2DTO.getSupportedDeliveryModes().size());
		assertEquals("Wrong paymentMode paymentInfoType at response: " + result, paymentModel.getPaymentInfoType().getCode(),
				paymentMode2DTO.getPaymentInfoType()); //
	}

	@Test
	public void testDeletePaymentMode()
	{
		ClientResponse result = webResource.path(URI + paymentModel.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + paymentModel.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + paymentModel.getCode(), POST);
	}
}
