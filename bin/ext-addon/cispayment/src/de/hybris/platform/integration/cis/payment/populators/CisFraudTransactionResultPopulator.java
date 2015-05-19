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
package de.hybris.platform.integration.cis.payment.populators;

import com.hybris.cis.api.fraud.model.CisFraudTransactionResult;
import com.hybris.cis.api.model.CisDecision;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.Assert;


public class CisFraudTransactionResultPopulator implements Populator<CisFraudTransactionResult, PaymentTransactionEntryModel>
{

	@Override
	public void populate(final CisFraudTransactionResult source, final PaymentTransactionEntryModel target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");


		target.setTime(source.getDate());
		setPaymentTransactionType(source, target);
		setPaymentTransactionStatus(source, target);
	}

	protected void setPaymentTransactionType(final CisFraudTransactionResult source, final PaymentTransactionEntryModel target)
	{
		target.setType(PaymentTransactionType.REVIEW_DECISION);
	}

	protected void setPaymentTransactionStatus(final CisFraudTransactionResult source, final PaymentTransactionEntryModel target)
	{
		if (CisDecision.ACCEPT.equals(source.getNewDecision()))
		{
			target.setTransactionStatus(TransactionStatus.ACCEPTED.toString());
			target.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.toString());
		}
		else if (CisDecision.REJECT.equals(source.getNewDecision()))
		{
			target.setTransactionStatus(TransactionStatus.REJECTED.toString());
			target.setTransactionStatusDetails(TransactionStatusDetails.AUTHORIZATION_REJECTED_BY_PSP.toString());
		}
		else if (CisDecision.REVIEW.equals(source.getNewDecision()))
		{
			target.setTransactionStatus(TransactionStatus.REVIEW.toString());
			target.setTransactionStatusDetails(TransactionStatusDetails.REVIEW_NEEDED.toString());
		}
		else
		{
			target.setTransactionStatus(TransactionStatus.ERROR.toString());
		}
	}
}
