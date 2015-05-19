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

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * A strategy for creating a {@link CreateSubscriptionRequest} in Accelerator
 * 
 */
public interface CreateSubscriptionRequestStrategy
{
	CreateSubscriptionRequest createSubscriptionRequest(final String siteName, final String requestUrl, final String responseUrl,
			final String merchantCallbackUrl, final CustomerModel customerModel, final CreditCardPaymentInfoModel cardInfo,
			final AddressModel paymentAddress) throws IllegalArgumentException;

}
