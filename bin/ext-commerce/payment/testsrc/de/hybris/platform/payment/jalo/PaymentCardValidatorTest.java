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
package de.hybris.platform.payment.jalo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.payment.commands.result.CardValidationResult;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.methods.impl.CardValidatorImpl;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;


/**
 * @author arkadiusz.balwierz
 * 
 */
public class PaymentCardValidatorTest
{
	private CardValidatorImpl cardValidatorImpl;

	@Before
	public void init()
	{
		cardValidatorImpl = new CardValidatorImpl();

		cardValidatorImpl.setSupportedCardSchemes(Arrays.asList(CreditCardType.MAESTRO));
	}


	@Test
	public void lunCheckTest()
	{
		assertFalse("Wrong CC number is ok", cardValidatorImpl.luhnCheck("4005550000000018"));
		assertTrue("Correct CC number is not ok", cardValidatorImpl.luhnCheck("4005550000000019"));
	}



	@Test
	public void checkCardTest()
	{
		Registry.activateMasterTenant();
		final CardInfo cardInfo = new CardInfo();

		cardInfo.setCardNumber("4005550000000019");
		cardInfo.setCardHolderFullName("name name");


		cardInfo.setExpirationYear(Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 1));
		cardInfo.setExpirationMonth(Integer.valueOf(1));
		cardInfo.setIssueYear(Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		cardInfo.setIssueMonth(Integer.valueOf(1));

		cardInfo.setCardType(CreditCardType.MAESTRO);

		cardInfo.setCv2Number("123");

		final CardValidationResult result = cardValidatorImpl.checkCard(cardInfo);

		assertTrue("ValidationResult not ok", result.isSuccess());

	}
}
