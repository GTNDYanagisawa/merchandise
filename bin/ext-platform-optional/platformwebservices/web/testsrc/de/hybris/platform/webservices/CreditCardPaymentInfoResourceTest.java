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

import de.hybris.platform.core.dto.order.payment.CreditCardPaymentInfoDTO;
import de.hybris.platform.core.dto.user.UserDTO;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.JaloBusinessException;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;


public class CreditCardPaymentInfoResourceTest extends AbstractWebServicesTest
{
	private static final String URI = "/creditcardpaymentinfos";
	private CreditCardPaymentInfoModel creditCardModel = null;

	/**
	 * @throws Exception
	 */
	public CreditCardPaymentInfoResourceTest() throws Exception //NOPMD
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
			if (paymentInfo instanceof CreditCardPaymentInfoModel)
			{
				creditCardModel = (CreditCardPaymentInfoModel) paymentInfo;
			}
		}
	}

	@Test
	public void testGetCreditCardPaymentInfo()
	{
		final ClientResponse result = webResource.path(URI + "/" + creditCardModel.getPk().toString()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);

		final CreditCardPaymentInfoDTO creditCardPaymentInfo = result.getEntity(CreditCardPaymentInfoDTO.class);
		assertEqual(creditCardModel, creditCardPaymentInfo, "code", "duplicate", "validFromMonth", "validToMonth", "validFromYear",
				"validToYear", "ccOwner", "number");
		assertEquals("Wrong creditCardPaymentInfo type at response: " + result, creditCardModel.getType(),
				CreditCardType.valueOf(creditCardPaymentInfo.getType().toUpperCase()));
	}

	@Test
	public void testPostCreditCardPaymentInfoNew()
	{
		final UserDTO userDto = new UserDTO();
		userDto.setUid("testCustomer1");

		final CreditCardPaymentInfoDTO creditCardPaymentInfoDTO = new CreditCardPaymentInfoDTO();
		creditCardPaymentInfoDTO.setCode("newCreditCard");
		creditCardPaymentInfoDTO.setDuplicate(Boolean.FALSE);
		creditCardPaymentInfoDTO.setType("visa");
		creditCardPaymentInfoDTO.setValidFromMonth("5");
		creditCardPaymentInfoDTO.setValidToMonth("6");
		creditCardPaymentInfoDTO.setValidFromYear("1999");
		creditCardPaymentInfoDTO.setValidToYear("2002");
		creditCardPaymentInfoDTO.setCcOwner("Jurgen");
		creditCardPaymentInfoDTO.setNumber("4111 1111 1111 1111");
		creditCardPaymentInfoDTO.setUser(userDto);

		ClientResponse result = webResource.path(URI).cookie(tenantCookie).header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN)
				.accept(MediaType.APPLICATION_XML).entity(creditCardPaymentInfoDTO).post(ClientResponse.class);
		result.bufferEntity();
		assertCreated(result, false);
		final CreditCardPaymentInfoDTO creditPaymentInfoPostResultDTO = result.getEntity(CreditCardPaymentInfoDTO.class);

		result = webResource.path(URI + "/" + creditPaymentInfoPostResultDTO.getPk().toString()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);
		assertEqual(creditCardPaymentInfoDTO, result, "code", "duplicate", "type", "validFromMonth", "validToMonth",
				"validFromYear", "validToYear", "ccOwner", "number");
	}

	//Commented because the payment mode is always created, not updated
	@Test
	public void testPutCreditCardPaymentInfoUpdate()
	{
		final CreditCardPaymentInfoDTO creditCardPaymentInfoDTO = new CreditCardPaymentInfoDTO();
		creditCardPaymentInfoDTO.setCode("creditCard1");
		creditCardPaymentInfoDTO.setDuplicate(Boolean.FALSE);
		creditCardPaymentInfoDTO.setType("visa");
		creditCardPaymentInfoDTO.setValidFromMonth("5");
		creditCardPaymentInfoDTO.setValidToMonth("6");
		creditCardPaymentInfoDTO.setValidFromYear("1999");
		creditCardPaymentInfoDTO.setValidToYear("2002");
		creditCardPaymentInfoDTO.setCcOwner("Jurgen");
		creditCardPaymentInfoDTO.setNumber("4111 1111 1111 1111");

		ClientResponse result = webResource.path(URI + "/" + creditCardModel.getPk().toString()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
				.entity(creditCardPaymentInfoDTO).put(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + "/" + creditCardModel.getPk().toString()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, false);
		assertEqual(creditCardPaymentInfoDTO, result, "code", "duplicate", "type", "validFromMonth", "validToMonth",
				"validFromYear", "validToYear", "ccOwner", "number");
	}

	@Test
	public void testDeleteCreditCardPaymentInfo()
	{
		ClientResponse result = webResource.path(URI + "/" + creditCardModel.getPk().toString()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML)
				.delete(ClientResponse.class);
		result.bufferEntity();
		assertOk(result, true);

		result = webResource.path(URI + "/" + creditCardModel.getPk().toString()).cookie(tenantCookie)
				.header(HEADER_AUTH_KEY, HEADER_AUTH_VALUE_BASIC_ADMIN).accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
		result.bufferEntity();
		assertEquals("Wrong HTTP status at response: " + result, Response.Status.NOT_FOUND, result.getResponseStatus());
	}

	@Test
	public void testMethodNotAllowed() throws IOException
	{
		assertMethodNotAllowed(URI + "/" + creditCardModel.getCode());
	}

}
