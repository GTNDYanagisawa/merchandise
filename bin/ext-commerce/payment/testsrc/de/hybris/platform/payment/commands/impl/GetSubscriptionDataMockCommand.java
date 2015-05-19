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
package de.hybris.platform.payment.commands.impl;

import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.payment.commands.GetSubscriptionDataCommand;
import de.hybris.platform.payment.commands.request.SubscriptionDataRequest;
import de.hybris.platform.payment.commands.result.SubscriptionDataResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;


/**
 * A mockup Implementation for {@link GetSubscriptionDataCommand}. The implementation allows to run tests of the payment
 * module without actually paying a dime for it.
 */
public class GetSubscriptionDataMockCommand extends GenericMockCommand implements GetSubscriptionDataCommand
{

	@Override
	public SubscriptionDataResult perform(final SubscriptionDataRequest request)
	{
		final SubscriptionDataResult result = new SubscriptionDataResult();

		// Just the ID
		result.setSubscriptionID(request.getSubscriptionID());

		// And the info
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setFirstName("Quentin");
		billingInfo.setLastName("Tarantino");
		billingInfo.setStreet1("1234 N Holywood Avenue");
		billingInfo.setStreet2("Apt No #4321");
		billingInfo.setCity("The city of Angels");
		billingInfo.setPostalCode("12321");
		billingInfo.setState("Golden State");
		billingInfo.setCountry("USA");
		result.setBillingInfo(billingInfo);

		// And the card
		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardType(CreditCardType.MASTER);
		cardInfo.setCardNumber("1111222233334444");
		cardInfo.setExpirationMonth(Integer.valueOf(6));
		cardInfo.setExpirationYear(Integer.valueOf(6));
		result.setCard(cardInfo);

		// And the most important
		result.setTransactionStatus(TransactionStatus.ACCEPTED);
		result.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);

		genericPerform(request, result);

		return result;
	}
}
