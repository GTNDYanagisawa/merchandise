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
package de.hybris.platform.acceleratorservices.payment.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.strategies.PaymentResponseInterpretationStrategy;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;

import java.util.Map;

/**
 *
 */
public abstract class AbstractPaymentResponseInterpretationStrategy implements PaymentResponseInterpretationStrategy
{
	protected void parseMissingFields(final Map<String, String> parameters, final Map<String, PaymentErrorField> errors)
	{
		for (final Map.Entry<String, String> paramEntry : parameters.entrySet())
		{
			if (paramEntry.getKey().startsWith("MissingField"))
			{
				getOrCreatePaymentErrorField(errors, paramEntry.getValue()).setMissing(true);
			}
			if (paramEntry.getKey().startsWith("InvalidField"))
			{
				getOrCreatePaymentErrorField(errors, paramEntry.getValue()).setInvalid(true);
			}
		}
	}

	protected PaymentErrorField getOrCreatePaymentErrorField(final Map<String, PaymentErrorField> errors, final String fieldName)
	{
		if (errors.containsKey(fieldName))
		{
			return errors.get(fieldName);
		}
		final PaymentErrorField result = new PaymentErrorField();
		result.setName(fieldName);
		errors.put(fieldName, result);
		return result;
	}
}
