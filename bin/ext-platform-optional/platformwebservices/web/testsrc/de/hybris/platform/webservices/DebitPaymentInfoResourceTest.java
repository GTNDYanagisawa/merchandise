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
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.dto.order.payment.DebitPaymentInfoDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.JaloBusinessException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class DebitPaymentInfoResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "/debitpaymentinfos";
	private DebitPaymentInfoModel debitModel = null;

	/**
	 * @throws Exception
	 */
	public DebitPaymentInfoResourceTest() throws Exception //NOPMD
	{
		super();
	}


	@Before
	public void setUpPaymentInfos() throws JaloBusinessException
	{
		createTestCustomers();
		createTestPaymentInfos();
		final CustomerModel customerModel = (CustomerModel) userService.getUserForUID("testCustomer1");
		for (final PaymentInfoModel paymentInfo : customerModel.getPaymentInfos())
		{
			if (paymentInfo instanceof DebitPaymentInfoModel)
			{
				debitModel = (DebitPaymentInfoModel) paymentInfo;
			}
		}
	}

	@Test
	public void testGetDebitPaymentInfo()
	{
		final ClientResponse result = webResource.path(URI + "/" + debitModel.getPk().toString()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		assertEqual(debitModel, result, DebitPaymentInfoDTO.class, "code", "duplicate", "bankIDNumber", "bank", "accountNumber",
				"baOwner");
	}

	@Test
	public void testPostDebitPaymentInfoNew()
	{
		final UserDTO userDto = new UserDTO();
		userDto.setUid("testCustomer1");

		final DebitPaymentInfoDTO debitPaymentInfoDTO = new DebitPaymentInfoDTO();
		debitPaymentInfoDTO.setCode("newDebit");
		debitPaymentInfoDTO.setDuplicate(Boolean.FALSE);
		debitPaymentInfoDTO.setBank("NEW BANK");
		debitPaymentInfoDTO.setBankIDNumber("987654321");
		debitPaymentInfoDTO.setBaOwner("Jurgen");
		debitPaymentInfoDTO.setAccountNumber("1111");
		debitPaymentInfoDTO.setUser(userDto);

		ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).entity(debitPaymentInfoDTO).post(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);
		final DebitPaymentInfoDTO debitPaymentInfoPostResultDTO = result.getEntity(DebitPaymentInfoDTO.class);

		result = webResource.path(URI + "/" + debitPaymentInfoPostResultDTO.getPk().toString()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();

		assertEquals("Wrong HTTP status at response: " + result, Response.Status.OK, result.getResponseStatus());
		assertTrue("Wrong content-type at response: " + result, MediaType.APPLICATION_XML_TYPE.isCompatible(result.getType()));

		assertEqual(debitPaymentInfoDTO, result, "code", "duplicate", "bankIDNumber", "bank", "accountNumber", "baOwner");
	}

	//Commented because the payment mode is always created, not updated
	@Test
	public void testPutDebitPaymentInfoUpdate()
	{
		final DebitPaymentInfoDTO debitPaymentInfoDTO = new DebitPaymentInfoDTO();
		debitPaymentInfoDTO.setCode("debit1");
		debitPaymentInfoDTO.setDuplicate(Boolean.FALSE);
		debitPaymentInfoDTO.setBank("NEW BANK");
		debitPaymentInfoDTO.setBankIDNumber("987654321");
		debitPaymentInfoDTO.setBaOwner("Jurgen");
		debitPaymentInfoDTO.setAccountNumber("1111");

		ClientResponse result = webResource.path(URI + "/" + debitModel.getPk().toString()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).entity(debitPaymentInfoDTO).put(
				ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + "/" + debitModel.getPk().toString()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);
		assertEqual(debitPaymentInfoDTO, result, "code", "duplicate", "bankIDNumber", "bank", "accountNumber", "baOwner");
	}

	@Test
	public void testDeleteDebitPaymentInfo()
	{
		ClientResponse result = webResource.path(URI + "/" + debitModel.getPk().toString()).cookie(tenantCookie).header(
				HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + debitModel.getCode()).cookie(tenantCookie).header(HEADER_AUTH_KEY,
				HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + "/" + debitModel.getPk().toString());
	}

}
