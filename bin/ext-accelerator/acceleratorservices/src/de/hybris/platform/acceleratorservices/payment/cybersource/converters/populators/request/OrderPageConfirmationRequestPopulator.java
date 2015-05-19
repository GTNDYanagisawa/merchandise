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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.request;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.OrderPageConfirmationData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * 
 */
public class OrderPageConfirmationRequestPopulator extends AbstractRequestPopulator<CreateSubscriptionRequest, PaymentData>
{
	@Override
	public void populate(final CreateSubscriptionRequest source, final PaymentData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [CreateSubscriptionRequest] source cannot be null");
		validateParameterNotNull(target, "Parameter [PaymentData] target cannot be null");

		final OrderPageConfirmationData confirmationData = source.getOrderPageConfirmationData();

		//This is optional data so just return.
		if (confirmationData == null)
		{
			return;
		}

		addRequestQueryParam(target, "orderPage_cancelLinkText", confirmationData.getCancelLinkText());
		addRequestQueryParam(target, "orderPage_cancelResponseURL", confirmationData.getCancelResponseUrl());
		addRequestQueryParam(target, "orderPage_declineLinkText", confirmationData.getDeclineLinkText());
		addRequestQueryParam(target, "orderPage_declineResponseURL", confirmationData.getDeclineResponseUrl());
		addRequestQueryParam(target, "orderPage_emailFromAddress", confirmationData.getEmailFromAddress());
		addRequestQueryParam(target, "orderPage_emailFromName", confirmationData.getEmailFromName());
		addRequestQueryParam(target, "orderPage_merchantEmailAddress", confirmationData.getMerchantEmailAddress());
		addRequestQueryParam(target, "orderPage_receiptLinkText", confirmationData.getReceiptLinkText());
		addRequestQueryParam(target, "orderPage_receiptResponseURL", confirmationData.getReceiptResponseUrl());
		addBooleanParameter(target, "orderPage_returnCardNumber", confirmationData.getReturnCardNumber());
		addBooleanParameter(target, "orderPage_returnBINInCardNumber", confirmationData.getReturnBinInCardNumber());
		addBooleanParameter(target, "orderPage_returnCardBIN", confirmationData.getReturnCardBin());
		addBooleanParameter(target, "orderPage_sendCustomerReceiptEmail", confirmationData.getSendCustomerReceiptEmail());
		addBooleanParameter(target, "orderPage_sendMerchantEmailPost", confirmationData.getSendMerchantEmailPost());
		addRequestQueryParam(target, "orderPage_merchantEmailPostAddress", confirmationData.getMerchantEmailPostAddress());
		addBooleanParameter(target, "orderPage_sendMerchantReceiptEmail", confirmationData.getSendMerchantReceiptEmail());
		addBooleanParameter(target, "orderPage_sendMerchantURLPost", confirmationData.getSendMerchantUrlPost());
		addRequestQueryParam(target, "orderPage_merchantURLPostAddress", confirmationData.getMerchantUrlPostAddress());

	}
}
