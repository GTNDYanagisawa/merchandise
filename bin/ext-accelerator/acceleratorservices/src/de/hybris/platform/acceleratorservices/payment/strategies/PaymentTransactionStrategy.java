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
package de.hybris.platform.acceleratorservices.payment.strategies;

import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

/**
 *
 *
 */
public interface PaymentTransactionStrategy
{
	PaymentTransactionEntryModel savePaymentTransactionEntry(CustomerModel customerModel, String requestId,
	                                                         OrderInfoData orderInfoData);

	void setPaymentTransactionReviewResult(PaymentTransactionEntryModel reviewDecisionEntry, String guid);
}
