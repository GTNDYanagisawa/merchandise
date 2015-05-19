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

import de.hybris.platform.acceleratorservices.payment.data.AuthReplyData;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionSignatureData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.util.Config;

import java.util.Map;

import org.springframework.util.Assert;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisCvnDecision;
import com.hybris.cis.api.payment.model.CisPaymentProfileResult;
import com.hybris.commons.client.RestResponse;


public class CisCreateSubscriptionResultPopulator implements
		Populator<RestResponse<CisPaymentProfileResult>, CreateSubscriptionResult>
{
	@Override
	public void populate(final RestResponse<CisPaymentProfileResult> source, final CreateSubscriptionResult target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final CisPaymentProfileResult cisPaymentProfileResult = source.getResult();
		final Map<String, String> venderResponses = cisPaymentProfileResult.getVendorResponses().getMap();

		target.setDecision(cisPaymentProfileResult.getDecision().name());
		target.setReasonCode(Integer.valueOf(cisPaymentProfileResult.getVendorReasonCode()));

		final SubscriptionInfoData subscriptionInfoData = new SubscriptionInfoData();
		subscriptionInfoData.setSubscriptionID(source.getLocation().toString());
		subscriptionInfoData.setSubscriptionSignedValue(cisPaymentProfileResult.getId());
		subscriptionInfoData.setSubscriptionIDPublicSignature(venderResponses
				.get("paySubscriptionCreateReply_subscriptionIDPublicSignature"));
		target.setSubscriptionInfoData(subscriptionInfoData);

		final AuthReplyData authReplyData = new AuthReplyData();
		authReplyData.setCcAuthReplyAmount(cisPaymentProfileResult.getAmount());
		authReplyData.setCcAuthReplyCvCode(venderResponses.get("ccAuthReply_cvCode"));
		authReplyData.setCvnDecision(Boolean.valueOf(CisCvnDecision.ACCEPT.equals(cisPaymentProfileResult.getCvnDecision())));

		if (cisPaymentProfileResult.getValidationResult() != null)
		{
			authReplyData.setCcAuthReplyAuthorizationCode(cisPaymentProfileResult.getValidationResult().getVendorStatusCode());
			authReplyData.setCcAuthReplyAuthorizedDateTime(null);
			authReplyData.setCcAuthReplyAvsCode(null);
			authReplyData.setCcAuthReplyAvsCodeRaw(null);
			authReplyData.setCcAuthReplyCvCode(null);
			authReplyData.setCcAuthReplyProcessorResponse(null);
			if (cisPaymentProfileResult.getValidationResult().getVendorReasonCode() != null)
			{
				authReplyData.setCcAuthReplyReasonCode(Integer.valueOf(cisPaymentProfileResult.getValidationResult()
						.getVendorReasonCode()));
			}
		}
		target.setAuthReplyData(authReplyData);

		final CustomerInfoData customerInfoData = new CustomerInfoData();
		target.setCustomerInfoData(customerInfoData);

		final CisAddress customerAddress = cisPaymentProfileResult.getCustomerAddress();
		if (customerAddress != null)
		{
			customerInfoData.setBillToCity(customerAddress.getCity());
			customerInfoData.setBillToCompany(customerAddress.getCompany());
			customerInfoData.setBillToCompanyTaxId(null);
			customerInfoData.setBillToCustomerIdRef(null);
			customerInfoData.setBillToDateOfBirth(null);
			customerInfoData.setBillToEmail(customerAddress.getEmail());
			customerInfoData.setBillToFirstName(customerAddress.getFirstName());
			customerInfoData.setBillToLastName(customerAddress.getLastName());
			customerInfoData.setBillToPhoneNumber(customerAddress.getPhone());
			customerInfoData.setBillToPostalCode(customerAddress.getZipCode());
			customerInfoData.setBillToState(customerAddress.getState());
			customerInfoData.setBillToStreet1(customerAddress.getAddressLine1());
			customerInfoData.setBillToStreet2(customerAddress.getAddressLine2());
			customerInfoData.setBillToCountry(customerAddress.getCountry() == null ? null : customerAddress.getCountry()
					.toUpperCase());
		}

		final OrderInfoData orderInfoData = new OrderInfoData();
		orderInfoData.setComments(cisPaymentProfileResult.getComments());
		orderInfoData.setOrderNumber(null);
		orderInfoData.setOrderPageRequestToken(null);
		orderInfoData.setOrderPageTransactionType(null);
		orderInfoData.setSubscriptionTitle(null);
		orderInfoData.setTaxAmount(null);
		target.setOrderInfoData(orderInfoData);

		final PaymentInfoData paymentInfoData = new PaymentInfoData();
		if (cisPaymentProfileResult.getCreditCard() != null)
		{
			paymentInfoData.setCardCardType(cisPaymentProfileResult.getCreditCard().getCardType());
			paymentInfoData.setCardAccountNumber(cisPaymentProfileResult.getCreditCard().getCcNumber());
			paymentInfoData.setCardExpirationMonth(Integer.valueOf(cisPaymentProfileResult.getCreditCard().getExpirationMonth()));
			paymentInfoData.setCardExpirationYear(getFourDigitYear(cisPaymentProfileResult.getCreditCard().getExpirationYear()));
			paymentInfoData.setCardStartMonth(null);
			paymentInfoData.setCardStartYear(null);
			paymentInfoData.setCardStartYear(null);
			paymentInfoData.setPaymentOption(null);
		}
		target.setPaymentInfoData(paymentInfoData);

		final SignatureData signatureData = new SignatureData();
		signatureData.setAmount(cisPaymentProfileResult.getValidationResult() == null ? null : cisPaymentProfileResult.getAmount());
		signatureData.setAmountPublicSignature(null);
		signatureData.setCurrency(cisPaymentProfileResult.getCurrency());
		signatureData.setCurrencyPublicSignature(null);
		signatureData.setMerchantID(cisPaymentProfileResult.getClientAuthorizationId());
		signatureData.setOrderPageSerialNumber(null);
		signatureData.setOrderPageVersion(null);
		signatureData.setSignedFields(null);
		signatureData.setTransactionSignature(cisPaymentProfileResult.getTransactionVerificationKey());
		target.setSignatureData(signatureData);

		final SubscriptionSignatureData subscriptionSignatureData = new SubscriptionSignatureData();
		target.setSubscriptionSignatureData(subscriptionSignatureData);
	}

	protected Integer getFourDigitYear(final int year)
	{
		if (Integer.toString(year).length() < 4)
		{
			return Integer.valueOf(Config.getInt("cispayment.fourdigityear.valuetoadd", 2000) + year);
		}
		else
		{
			return Integer.valueOf(year);
		}
	}
}
