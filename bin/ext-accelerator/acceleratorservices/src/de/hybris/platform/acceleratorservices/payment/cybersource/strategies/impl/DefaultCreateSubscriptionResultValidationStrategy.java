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
package de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.strategies.CreateSubscriptionResultValidationStrategy;
import de.hybris.platform.acceleratorservices.payment.strategies.ErroCodeToFormFieldMappingStrategy;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class DefaultCreateSubscriptionResultValidationStrategy implements CreateSubscriptionResultValidationStrategy
{

	private static final String CARD_VERIFICATION_NUMBER = "card_cvNumber";
	private ErroCodeToFormFieldMappingStrategy erroCodeToFormFieldMappingStrategy;

	protected ErroCodeToFormFieldMappingStrategy getErroCodeToFormFieldMappingStrategy()
	{
		return erroCodeToFormFieldMappingStrategy;
	}

	@Required
	public void setErroCodeToFormFieldMappingStrategy(final ErroCodeToFormFieldMappingStrategy erroCodeToFormFieldMappingStrategy)
	{
		this.erroCodeToFormFieldMappingStrategy = erroCodeToFormFieldMappingStrategy;
	}

	@Override
	public Map<String, PaymentErrorField> validateCreateSubscriptionResult(final Map<String, PaymentErrorField> errors,
	                                                                       final CreateSubscriptionResult response)
	{
		// check cvv2 number
		if (!DecisionsEnum.ERROR.name().equals(response.getDecision()) && response.getAuthReplyData() != null
				&& response.getAuthReplyData().getCvnDecision() != null
				&& !response.getAuthReplyData().getCvnDecision().booleanValue())
		{
			getOrCreatePaymentErrorField(errors, CARD_VERIFICATION_NUMBER).setInvalid(true);
		}

		if (!errors.isEmpty())
		{
			return errors;
		}


		final Map<Integer, String> pspErrors = response.getErrors();
		if (pspErrors != null)
		{
			for (final Integer key : pspErrors.keySet())
			{
				final List<String> errorFields = getErroCodeToFormFieldMappingStrategy().getFieldForErrorCode(key);
				if (errorFields != null)
				{
					for (final String errorField : errorFields)
					{
						getOrCreatePaymentErrorField(errors, errorField).setInvalid(true);
					}
				}
			}
		}
		return errors;
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
