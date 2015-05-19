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

import de.hybris.platform.payment.commands.AuthorizationCommand;
import de.hybris.platform.payment.commands.request.AuthorizationRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.AvsStatus;
import de.hybris.platform.payment.dto.CvnStatus;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

import java.util.Calendar;
import java.util.Date;


/**
 * A mockup Implementation for {@link AuthorizationCommand}. Set a wrong card expiration date if you need your
 * transaction rejected. Transaction will have set REVIEW state if amount is greater then
 * {@link AuthorizationMockCommand#REVIEW_AMOUNT} . In other cases it will be accepted.
 */
public class AuthorizationMockCommand extends GenericMockCommand implements AuthorizationCommand
{
	public static final long REVIEW_AMOUNT = 5000;

	@Override
	public AuthorizationResult perform(final AuthorizationRequest request)
	{
		final AuthorizationResult result = new AuthorizationResult();

		// Let's be as much polite as possible and return the requested data where it makes sense
		result.setCurrency(request.getCurrency());
		result.setTotalAmount(request.getTotalAmount());

		result.setAvsStatus(AvsStatus.NO_RESULT);
		result.setCvnStatus(CvnStatus.NOT_PROCESSED);

		result.setAuthorizationTime(new Date());

		// And the most important is the authorization algorithm
		final Calendar today = Calendar.getInstance();
		if (today.get(Calendar.YEAR) > request.getCard().getExpirationYear().intValue())
		{
			result.setTransactionStatus(TransactionStatus.REJECTED);
			result.setTransactionStatusDetails(TransactionStatusDetails.INVALID_CARD_EXPIRATION_DATE);
		}
		else if (today.get(Calendar.YEAR) == request.getCard().getExpirationYear().intValue()
				&& today.get(Calendar.MONTH) > request.getCard().getExpirationMonth().intValue())
		{
			result.setTransactionStatus(TransactionStatus.REJECTED);
			result.setTransactionStatusDetails(TransactionStatusDetails.INVALID_CARD_EXPIRATION_DATE);
		}
		else if (request.getTotalAmount().longValue() > REVIEW_AMOUNT) //if total amount is greater then REVIEW_AMOUNT simulate REVIEW status
		{
			result.setTransactionStatus(TransactionStatus.REVIEW);
			result.setTransactionStatusDetails(TransactionStatusDetails.REVIEW_NEEDED);
		}
		else
		{
			result.setTransactionStatus(TransactionStatus.ACCEPTED);
			result.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
		}

		genericPerform(request, result);

		return result;
	}
}