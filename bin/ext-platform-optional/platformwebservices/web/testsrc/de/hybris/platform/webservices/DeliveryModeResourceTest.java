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

import de.hybris.platform.core.dto.order.delivery.DeliveryModeDTO;
import de.hybris.platform.core.dto.order.payment.PaymentModeDTO;
import de.hybris.platform.core.dto.type.ComposedTypeDTO;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.jalo.ConsistencyCheckException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;



public class DeliveryModeResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "deliverymodes/";
	private DeliveryModeModel deliveryModel = null;
	private PaymentModeModel paymentModel = null;

	public DeliveryModeResourceTest() throws Exception
	{
		super();
	}

	@Before
	public void setUpDeliveryModes() throws ConsistencyCheckException
	{
		createPaymentDeliveryModes();
		getWsUtilService();
		deliveryModel = wsUtilService.getDeliveryModeByCode("testDeliveryMode1");
		paymentModel = wsUtilService.getPaymentModeByCode("testPaymentMode1");
	}

	@Test
	public void testGetDeliveryMode()
	{
		final ClientResponse result = webResource.path(URI + deliveryModel.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final DeliveryModeDTO deliveryModeDTO = result.getEntity(DeliveryModeDTO.class);
		assertEqual(deliveryModel, deliveryModeDTO, "code", "active", "name", "description");
		assertEquals("Wrong deliveryMode supportedpaymentmodes at response: " + result, deliveryModel.getSupportedPaymentModes()
				.size(), deliveryModeDTO.getSupportedPaymentModes().size());
	}

	@Test
	public void testPutDeliveryModeNew()
	{
		final DeliveryModeDTO deliveryModeDTO = new DeliveryModeDTO();
		deliveryModeDTO.setCode("newDeliveryMode");
		deliveryModeDTO.setActive(deliveryModel.getActive());
		deliveryModeDTO.setDescription("New description");
		deliveryModeDTO.setName(deliveryModel.getName());

		final ComposedTypeDTO paymentInfoType = new ComposedTypeDTO();
		paymentInfoType.setCode(paymentModel.getPaymentInfoType().getCode());

		final PaymentModeDTO paymentModeDTO = new PaymentModeDTO();
		paymentModeDTO.setCode(paymentModel.getCode());
		paymentModeDTO.setActive(paymentModel.getActive());
		paymentModeDTO.setDescription("New description");
		paymentModeDTO.setPaymentInfoType(paymentInfoType.getCode());
		paymentModeDTO.setName(paymentModel.getName());

		final List<PaymentModeDTO> listPaymentModeDTOs = new ArrayList<PaymentModeDTO>();
		listPaymentModeDTOs.add(paymentModeDTO);
		deliveryModeDTO.setSupportedPaymentModes(listPaymentModeDTOs);

		ClientResponse result = webResource.path(URI + deliveryModeDTO.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(deliveryModeDTO)
				.put(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, true);

		result = webResource.path(URI + deliveryModeDTO.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final DeliveryModeModel deliveryModeModelNew = wsUtilService.getDeliveryModeByCode("newDeliveryMode");
		final DeliveryModeDTO deliveryMode2DTO = result.getEntity(DeliveryModeDTO.class);
		assertEqual(deliveryModeDTO, deliveryMode2DTO, "code", "active", "name", "description");
		assertEquals("Wrong deliveryMode supporteddeliverymodes at response: " + result, deliveryModeDTO.getSupportedPaymentModes()
				.size(), deliveryMode2DTO.getSupportedPaymentModes().size());

		//remove created DeliveryModeModel
		modelService.remove(deliveryModeModelNew);
	}

	@Test
	public void testPutDeliveryModeUpdate()
	{
		final DeliveryModeDTO deliveryModeDTO = new DeliveryModeDTO();
		deliveryModeDTO.setCode(deliveryModel.getCode());
		deliveryModeDTO.setActive(deliveryModel.getActive());
		deliveryModeDTO.setDescription("New description");
		deliveryModeDTO.setName(deliveryModel.getName());

		ClientResponse result = webResource.path(URI + deliveryModeDTO.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(deliveryModeDTO)
				.put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + deliveryModeDTO.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final DeliveryModeDTO deliveryMode2DTO = result.getEntity(DeliveryModeDTO.class);
		assertEqual(deliveryModeDTO, deliveryMode2DTO, "code", "active", "name", "description");
		assertEquals("Wrong deliveryMode supporteddeliverymodes at response: " + result, deliveryModel.getSupportedPaymentModes()
				.size(), deliveryMode2DTO.getSupportedPaymentModes().size());
	}

	@Test
	public void testDeleteDeliveryMode()
	{
		ClientResponse result = webResource.path(URI + deliveryModel.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + deliveryModel.getCode()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + deliveryModel.getCode(), POST);
	}
}
