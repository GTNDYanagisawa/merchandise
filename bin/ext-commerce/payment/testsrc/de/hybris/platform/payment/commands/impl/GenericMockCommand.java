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

import de.hybris.platform.payment.commands.request.AbstractRequest;
import de.hybris.platform.payment.commands.result.AbstractResult;

import org.apache.log4j.Logger;


/**
 * A mockup Implementation for a generic Command. The implementation allows to run tests of the payment module without
 * actually paying a dime for it.
 */
public abstract class GenericMockCommand
{
	protected AbstractResult genericPerform(final AbstractRequest request, final AbstractResult result)
	{
		// Set the common fields
		result.setMerchantTransactionCode(request.getMerchantTransactionCode());

		Logger.getLogger(getClass()).info(
				"Payment command: " + getClass() + " executed [status: " + result.getTransactionStatus().toString() + "]");

		return result;
	}
}