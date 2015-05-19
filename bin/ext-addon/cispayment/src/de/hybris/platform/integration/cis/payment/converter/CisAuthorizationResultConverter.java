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
package de.hybris.platform.integration.cis.payment.converter;

import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.api.payment.model.CisPaymentTransactionResult;
import com.hybris.commons.client.RestResponse;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import java.util.Currency;
import java.util.Date;


public class CisAuthorizationResultConverter
{
	public AuthorizationResult convert(final RestResponse<CisPaymentTransactionResult> restResponse)
	{
		final AuthorizationResult authorizationResult = new AuthorizationResult();
		authorizationResult.setAuthorizationCode(restResponse.getResult().getId());
		authorizationResult.setAuthorizationTime(new Date());
		authorizationResult.setAvsStatus(null);//TODO
		authorizationResult.setCurrency(Currency.getInstance(restResponse.getResult().getRequest().getCurrency().toUpperCase()));
		authorizationResult.setCvnStatus(null);
		authorizationResult.setMerchantTransactionCode(restResponse.getResult().getClientAuthorizationId());
		authorizationResult.setPaymentProvider("cisCybersource");
		authorizationResult.setReconciliationId(null);
		authorizationResult.setRequestId(restResponse.getLocation().toString());
		authorizationResult.setRequestToken(null);
		authorizationResult.setTotalAmount(restResponse.getResult().getAmount());
		authorizationResult.setTransactionStatus(convertCisDecisionToTransactionStatus(restResponse.getResult().getDecision()));
		authorizationResult.setTransactionStatusDetails(TransactionStatusDetails.UNKNOWN_CODE);//TODO
		authorizationResult.setMerchantTransactionCode(restResponse.getResult().getClientAuthorizationId());
		return authorizationResult;
	}

	private TransactionStatus convertCisDecisionToTransactionStatus(final CisDecision cisDecision)
	{
		if (CisDecision.ACCEPT.equals(cisDecision))
		{
			return TransactionStatus.ACCEPTED;
		}
		else if (CisDecision.ERROR.equals(cisDecision))
		{
			return TransactionStatus.ERROR;
		}
		else if (CisDecision.REJECT.equals(cisDecision))
		{
			return TransactionStatus.REJECTED;
		}
		else if (CisDecision.REVIEW.equals(cisDecision))
		{
			return TransactionStatus.REVIEW;
		}
		else
		{
			throw new IllegalArgumentException("unknown cisDecision for authorization : " + cisDecision.toString());
		}
	}
}
