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
package de.hybris.platform.acceleratorfacades.payment;

import de.hybris.platform.acceleratorservices.payment.data.HostedOrderPageRequest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;

import java.util.Map;


/**
 * HOP/SOP facade interface. Service is responsible for getting all necessary information required to build
 * request and .. response from the implemented hosted order page or silent order post provider.
 */
public interface PaymentFacade
{
	/**
	 * Gets the Hosted Order Page request data which includes all data required to create a
	 * subscription (customer profile).
	 * 
	 * @param responseUrl
	 *           - requires a {@link HostedOrderPageRequest} object containing all the request data.
	 * @param merchantCallbackUrl
	             - The URL of the MerchantCallbackController used by the Provider to send validation responses.
	 * @return a {@link PaymentData} object which contains all data required to create a subscription.
	 */
	PaymentData beginHopCreateSubscription(String responseUrl, String merchantCallbackUrl);

	/**
	 * Gets the Silent Order Post request data which includes all data required to create a
	 * subscription (customer profile).
	 *
	 * @param responseUrl
	 *           - requires a {@link HostedOrderPageRequest} object containing all the request data.
	 * @param merchantCallbackUrl
	             - The URL of the MerchantCallbackController used by the Provider to send validation responses.
	 * @return a {@link PaymentData} object which contains all data required to create a subscription.
	 */
	PaymentData beginSopCreateSubscription(String responseUrl, String merchantCallbackUrl);

	/**
	 * Called to create a subscription internally with the result parameters obtained from the provider's Hosted Order
	 * Page.
	 * 
	 * @param parameters
	 *           - a Map of key-value paired Strings with the result data returned from the HOP/SOP response.
	 * @param saveInAccount
	 *           - a Flag to tell if the PaymentInfo created should saved to the account. During guest checkout the
	 *           Anonymous user should not hav the PaymentInfos assigned
	 * @return - a {@link CCPaymentInfoData} object representing the subscription created by the HOP/SOP response.
	 */
	PaymentSubscriptionResultData completeHopCreateSubscription(Map<String, String> parameters, boolean saveInAccount);

	/**
	 * Called to create a subscription internally with the result parameters obtained from the provider's Silent Order
	 * Post.
	 *
	 * @param parameters
	 *           - a Map of key-value paired Strings with the result data returned from the HOP/SOP response.
	 * @param saveInAccount
	 *           - a Flag to tell if the PaymentInfo created should saved to the account. During guest checkout the
	 *           Anonymous user should not hav the PaymentInfos assigned
	 * @return - a {@link CCPaymentInfoData} object representing the subscription created by the HOP/SOP response.
	 */
	PaymentSubscriptionResultData completeSopCreateSubscription(Map<String, String> parameters, boolean saveInAccount);
}
