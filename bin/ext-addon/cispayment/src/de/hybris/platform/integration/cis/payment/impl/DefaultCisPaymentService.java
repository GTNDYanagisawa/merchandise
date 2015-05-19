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
package de.hybris.platform.integration.cis.payment.impl;

import de.hybris.platform.acceleratorservices.payment.impl.DefaultAcceleratorPaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.api.fraud.model.CisFraudTransactionResult;
import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.client.rest.fraud.FraudClient;
import com.hybris.commons.client.RestResponse;


/**
 * Implementation of CyberSourceAcceleratorPaymentService using CIS web services calls.
 */
public class DefaultCisPaymentService extends DefaultAcceleratorPaymentService
{
	private static final Logger LOG = Logger.getLogger(DefaultCisPaymentService.class);
	private FraudClient fraudClient;
	private Converter<CisFraudTransactionResult, PaymentTransactionEntryModel> transactionResultConverter;

	@Override
	public void handleFraudUpdateCallback(final Map<String, String> parameters)
	{
		final String orderUpdate = getContentParameter(parameters);
		if (orderUpdate != null)
		{
			final RestResponse<CisFraudTransactionResult> result = getFraudClient().handleOrderStatusUpdate(getClientReferenceLookupStrategy().lookupClientReferenceId(),
					orderUpdate);
			final CisFraudTransactionResult fraudTransactionResult = result.getResult();
			setPaymentTransactionReviewResult(createReviewTransactionEntry(fraudTransactionResult),
					fraudTransactionResult.getClientAuthorizationId());
		}
	}

	/**
	 * Method return value for parameter holding update command
	 * 
	 * @param parameters
	 *           - parameters map
	 * @return update command or null
	 */
	protected String getContentParameter(final Map<String, String> parameters)
	{
		final String orderUpdate = parameters.get("content");
		if (orderUpdate == null)
		{
			LOG.error(String.format(
					"DefaultCISPaymentService->handleFraudUpdate : There is no 'content' parameter. Got the following parameters: %s",
					parameters));
		}
		return orderUpdate;
	}

	/**
	 * Method create PaymentTransactionEntryModel of type REVIEW_DECISION
	 * 
	 * @param fraudTransactionResult
	 *           - object contains information about review decision
	 * @return paymentTransactionEntryModel of type REVIEW_DECISION
	 */
	protected PaymentTransactionEntryModel createReviewTransactionEntry(final CisFraudTransactionResult fraudTransactionResult)
	{
		final PaymentTransactionEntryModel paymentTransactionEntry = getTransactionResultConverter()
				.convert(fraudTransactionResult);
		if (!CisDecision.REVIEW.equals(fraudTransactionResult.getOriginalDecision()))
		{
			LOG.warn(String.format("DefaultCISPaymentService->handleFraudUpdate : Original Decision wasn't REVIEW but was %s",
					fraudTransactionResult));
		}
		return paymentTransactionEntry;
	}

	public Converter<CisFraudTransactionResult, PaymentTransactionEntryModel> getTransactionResultConverter()
	{
		return transactionResultConverter;
	}

	@Required
	public void setTransactionResultConverter(
			final Converter<CisFraudTransactionResult, PaymentTransactionEntryModel> transactionResultConverter)
	{
		this.transactionResultConverter = transactionResultConverter;
	}

	public FraudClient getFraudClient()
	{
		return this.fraudClient;
	}

	@Required
	public void setFraudClient(final FraudClient fraudClient)
	{
		this.fraudClient = fraudClient;
	}
}
