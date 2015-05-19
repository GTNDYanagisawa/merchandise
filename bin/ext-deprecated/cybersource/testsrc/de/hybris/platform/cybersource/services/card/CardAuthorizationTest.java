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
package de.hybris.platform.cybersource.services.card;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.cybersource.adapter.CybersourceDTOFactory;
import de.hybris.platform.cybersource.adapter.CybersourceExecutor;
import de.hybris.platform.cybersource.adapter.impl.CybersourceFactoryImpl;
import de.hybris.platform.cybersource.adapter.impl.ExecutorFactoryImpl;
import de.hybris.platform.cybersource.api.v1_49.BillTo;
import de.hybris.platform.cybersource.api.v1_49.CCAuthService;
import de.hybris.platform.cybersource.api.v1_49.Card;
import de.hybris.platform.cybersource.api.v1_49.Item;
import de.hybris.platform.cybersource.api.v1_49.ObjectFactory;
import de.hybris.platform.cybersource.api.v1_49.PurchaseTotals;
import de.hybris.platform.cybersource.api.v1_49.ReplyMessage;
import de.hybris.platform.cybersource.api.v1_49.RequestMessage;
import de.hybris.platform.cybersource.api.v1_49.ShipTo;
import de.hybris.platform.cybersource.commands.CybersourceCodeTranslator;

import java.math.BigInteger;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.junit.Test;


@ManualTest
public class CardAuthorizationTest
{

	@Test
	public void testSimpleAuthorization()
	{
		final String currentTime = String.valueOf(System.currentTimeMillis());
		final String testMerchantReferenceCode = "testMerchant" + currentTime.substring(currentTime.length() - 3);

		final CybersourceCodeTranslator codeTranslator = new CybersourceCodeTranslator();
		final CybersourceFactoryImpl csFactory = new CybersourceFactoryImpl(new ObjectFactory());

		final SampleCardAuthorizationRequest scar = new SampleCardAuthorizationRequest(testMerchantReferenceCode, csFactory);

		final ExecutorFactoryImpl exefactory = new ExecutorFactoryImpl();
		exefactory.setCodeTranslator(codeTranslator);
		exefactory.setCybersourceFactory(csFactory);

		final CybersourceExecutor executor = exefactory.getCybersourceExecutor();

		final ReplyMessage replyMessage;
		try
		{
			replyMessage = executor.run(scar.getRequestMessage());

			assertEquals("Invalid decision: ", "ACCEPT", replyMessage.getDecision());
			assertEquals("Invalid merchant reference code: ", testMerchantReferenceCode, replyMessage.getMerchantReferenceCode());
		}
		catch (final JAXBException ex)
		{
			Logger.getLogger(CardAuthorizationTest.class).error(ex);
			fail();
		}
	}
}


/**
 * This is just a prototype test implementation based on Cybersource code samples.
 */
class SampleCardAuthorizationRequest
{

	private final String merchantReferenceCode;
	private final CybersourceDTOFactory dtoFactory;

	public SampleCardAuthorizationRequest(final String merchantReferenceCode, final CybersourceDTOFactory dtoFactory)
	{
		super();
		this.merchantReferenceCode = merchantReferenceCode;
		this.dtoFactory = dtoFactory;
	}

	public RequestMessage getRequestMessage()
	{
		final RequestMessage request = dtoFactory.createRequestMessage();
		request.setMerchantReferenceCode(this.merchantReferenceCode);
		//request.setMerchantID("thertz");
		final BillTo billTo = dtoFactory.createBillTo();
		request.setBillTo(billTo);
		billTo.setFirstName("John");
		billTo.setLastName("Doe");
		billTo.setStreet1("1295 Charleston Road");
		billTo.setCity("Mountain View");
		billTo.setState("CA");
		billTo.setPostalCode("94043");
		billTo.setCountry("US");
		billTo.setPhoneNumber("650-965-6000");
		billTo.setEmail("nobody@cybersource.com");
		billTo.setIpAddress("10.7" + "." + "7.7");

		final ShipTo shipTo = dtoFactory.createShipTo();
		request.setShipTo(shipTo);
		shipTo.setFirstName("Jane");
		shipTo.setLastName("Doe");
		shipTo.setStreet1("100 Elm Street");
		shipTo.setCity("San Mateo");
		shipTo.setState("CA");
		shipTo.setPostalCode("94401");
		shipTo.setCountry("US");


		final Item item0 = dtoFactory.createItem();
		request.getItem().add(item0);
		item0.setId(BigInteger.valueOf(0l));
		item0.setUnitPrice("12.34");

		final Item item1 = dtoFactory.createItem();
		request.getItem().add(item1);
		item1.setId(BigInteger.valueOf(1l));
		item1.setUnitPrice("56.78");


		final PurchaseTotals purchaseTotals = dtoFactory.createPurchaseTotals();
		request.setPurchaseTotals(purchaseTotals);
		purchaseTotals.setCurrency("USD");

		final Card card = dtoFactory.createCard();
		request.setCard(card);
		card.setCardType("001");
		card.setAccountNumber("4111111111111111");
		card.setExpirationMonth(BigInteger.valueOf(12l));
		card.setExpirationYear(BigInteger.valueOf(2020));
		//card.setCvNumber("1234");


		final CCAuthService ccAuthService = dtoFactory.createCCAuthService();
		request.setCcAuthService(ccAuthService);
		ccAuthService.setRun("true");

		return request;
	}

}
