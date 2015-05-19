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

import de.hybris.platform.payment.commands.CreateSubscriptionCommand;
import de.hybris.platform.payment.commands.request.CreateSubscriptionRequest;
import de.hybris.platform.payment.commands.result.SubscriptionResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;


/**
 * A mockup Implementation for {@link CreateSubscriptionCommand}. The implementation allows to run tests of the payment
 * module without actually paying a dime for it.
 */
public class CreateSubscriptionMockCommand extends GenericMockCommand implements CreateSubscriptionCommand
{

	@Override
	public SubscriptionResult perform(final CreateSubscriptionRequest request)
	{
		final SubscriptionResult result = new SubscriptionResult();

		result.setSubscriptionID("MockedSubscriptionID");

		// And the most important
		result.setTransactionStatus(TransactionStatus.ACCEPTED);
		result.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);

		genericPerform(request, result);

		return result;
	}
}
