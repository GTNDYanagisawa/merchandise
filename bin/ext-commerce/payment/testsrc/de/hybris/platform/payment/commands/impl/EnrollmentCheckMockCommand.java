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

import de.hybris.platform.payment.commands.EnrollmentCheckCommand;
import de.hybris.platform.payment.commands.request.EnrollmentCheckRequest;
import de.hybris.platform.payment.commands.result.EnrollmentCheckResult;

import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

/**
 * A mockup Implementation for {@link EnrollmentCheckCommand}.
 */
public class EnrollmentCheckMockCommand extends GenericMockCommand implements EnrollmentCheckCommand
{
	@Override
	public EnrollmentCheckResult perform(final EnrollmentCheckRequest request)
	{
		final EnrollmentCheckResult result = new EnrollmentCheckResult( request.getMerchantTransactionCode() );

        // And the most important
		result.setTransactionStatus( TransactionStatus.ACCEPTED );
		result.setTransactionStatusDetails( TransactionStatusDetails.SUCCESFULL );

        genericPerform( request, result );

		return result;
	}
}
