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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.AuthReplyData;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SignatureData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cis.api.model.CisAddress;
import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.api.payment.model.CisCreditCard;
import com.hybris.cis.api.payment.model.CisPaymentProfileResult;
import com.hybris.cis.api.payment.model.CisPaymentTransactionResult;
import com.hybris.commons.client.RestResponse;


/**
 * @author florent
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisCreateSubscriptionResultPopulatorTest
{
	private CisCreateSubscriptionResultPopulator populator;

	@Mock
	private RestResponse<CisPaymentProfileResult> source;
	@Mock
	private CisPaymentProfileResult cisPaymentProfileResult;
	@Mock
	private CisPaymentTransactionResult cisPaymentTransactionResult;
	@Mock
	private CisAddress cisAddress;
	@Mock
	private CisCreditCard cisCreditCard;
	@Mock
	private com.hybris.cis.api.model.AnnotationHashMap vendorResponses;
	@Mock
	private Map vendorResponsesMap;

	@Before
	public void setup()
	{
		populator = new CisCreateSubscriptionResultPopulator();
		MockitoAnnotations.initMocks(this.getClass());
	}

	@Test
	public void shouldPopulate() throws URISyntaxException
	{
		final CreateSubscriptionResult result = new CreateSubscriptionResult();
		result.setAuthReplyData(new AuthReplyData());
		result.setSubscriptionInfoData(new SubscriptionInfoData());
		result.setCustomerInfoData(new CustomerInfoData());
		result.setOrderInfoData(new OrderInfoData());
		result.setPaymentInfoData(new PaymentInfoData());
		result.setSignatureData(new SignatureData());


		BDDMockito.when(source.getLocation()).thenReturn(new URI("http://location/of/the/profile/in/cis"));
		BDDMockito.when(source.getResult()).thenReturn(cisPaymentProfileResult);
		BDDMockito.when(vendorResponses.getMap()).thenReturn(vendorResponsesMap);
		BDDMockito.when(cisPaymentProfileResult.getVendorResponses()).thenReturn(vendorResponses);
		BDDMockito.when(cisPaymentProfileResult.getDecision()).thenReturn(CisDecision.ACCEPT);
		BDDMockito.when(cisPaymentProfileResult.getVendorReasonCode()).thenReturn("10");
		BDDMockito.when(cisPaymentProfileResult.getId()).thenReturn("subscriptionIdValue");
		BDDMockito.when(cisPaymentProfileResult.getAmount()).thenReturn(BigDecimal.TEN);
		BDDMockito.when(cisPaymentProfileResult.getValidationResult()).thenReturn(cisPaymentTransactionResult);
		BDDMockito.when(cisPaymentProfileResult.getCustomerAddress()).thenReturn(cisAddress);
		BDDMockito.when(cisPaymentProfileResult.getComments()).thenReturn("comments");
		BDDMockito.when(cisPaymentProfileResult.getCreditCard()).thenReturn(cisCreditCard);
		BDDMockito.when(cisPaymentProfileResult.getCurrency()).thenReturn("USD");
		BDDMockito.when(cisPaymentProfileResult.getClientAuthorizationId()).thenReturn("clientAuthorizationId");
		BDDMockito.when(cisPaymentProfileResult.getTransactionVerificationKey()).thenReturn("transactionVerificationKey");
		BDDMockito.when(cisPaymentTransactionResult.getVendorStatusCode()).thenReturn("vendorStatusCode");
		BDDMockito.when(cisPaymentTransactionResult.getVendorReasonCode()).thenReturn("123");
		BDDMockito.when(cisAddress.getCity()).thenReturn("city");
		BDDMockito.when(cisAddress.getCompany()).thenReturn("company");
		BDDMockito.when(cisAddress.getFirstName()).thenReturn("firstName");
		BDDMockito.when(cisAddress.getLastName()).thenReturn("lastName");
		BDDMockito.when(cisAddress.getPhone()).thenReturn("phone");
		BDDMockito.when(cisAddress.getZipCode()).thenReturn("zipcode");
		BDDMockito.when(cisAddress.getEmail()).thenReturn("email");
		BDDMockito.when(cisAddress.getState()).thenReturn("state");
		BDDMockito.when(cisAddress.getAddressLine1()).thenReturn("line1");
		BDDMockito.when(cisAddress.getAddressLine2()).thenReturn("line2");
		BDDMockito.when(cisAddress.getCountry()).thenReturn("country");
		BDDMockito.when(cisCreditCard.getCcNumber()).thenReturn("creditCardNumber");
		BDDMockito.when(Integer.valueOf(cisCreditCard.getExpirationMonth())).thenReturn(Integer.valueOf(1));
		BDDMockito.when(Integer.valueOf(cisCreditCard.getExpirationYear())).thenReturn(Integer.valueOf(2015));

		populator.populate(source, result);

		Assert.assertEquals(CisDecision.ACCEPT.name(), result.getDecision());
		Assert.assertEquals(Integer.valueOf(10), result.getReasonCode());
		Assert.assertNull(result.getDecisionPublicSignature());

		Assert.assertNotNull(result.getSubscriptionInfoData());
		Assert.assertEquals("http://location/of/the/profile/in/cis", result.getSubscriptionInfoData().getSubscriptionID());
		Assert.assertEquals("subscriptionIdValue", result.getSubscriptionInfoData().getSubscriptionSignedValue());

		Assert.assertNotNull(result.getAuthReplyData());
		Assert.assertEquals(BigDecimal.TEN, result.getAuthReplyData().getCcAuthReplyAmount());
		Assert.assertEquals("vendorStatusCode", result.getAuthReplyData().getCcAuthReplyAuthorizationCode());
		Assert.assertNull(result.getAuthReplyData().getCcAuthReplyAuthorizedDateTime());
		Assert.assertNull(result.getAuthReplyData().getCcAuthReplyAvsCode());
		Assert.assertNull(result.getAuthReplyData().getCcAuthReplyAvsCodeRaw());
		Assert.assertNull(result.getAuthReplyData().getCcAuthReplyCvCode());
		Assert.assertNull(result.getAuthReplyData().getCcAuthReplyProcessorResponse());
		Assert.assertEquals(Integer.valueOf(123), result.getAuthReplyData().getCcAuthReplyReasonCode());

		Assert.assertNotNull(result.getCustomerInfoData());
		Assert.assertEquals("city", result.getCustomerInfoData().getBillToCity());
		Assert.assertEquals("company", result.getCustomerInfoData().getBillToCompany());
		Assert.assertNull(result.getCustomerInfoData().getBillToCompanyTaxId());
		Assert.assertNull(result.getCustomerInfoData().getBillToCustomerIdRef());
		Assert.assertNull(result.getCustomerInfoData().getBillToDateOfBirth());
		Assert.assertEquals("email", result.getCustomerInfoData().getBillToEmail());
		Assert.assertEquals("firstName", result.getCustomerInfoData().getBillToFirstName());
		Assert.assertEquals("lastName", result.getCustomerInfoData().getBillToLastName());
		Assert.assertEquals("phone", result.getCustomerInfoData().getBillToPhoneNumber());
		Assert.assertEquals("zipcode", result.getCustomerInfoData().getBillToPostalCode());
		Assert.assertEquals("state", result.getCustomerInfoData().getBillToState());
		Assert.assertEquals("line1", result.getCustomerInfoData().getBillToStreet1());
		Assert.assertEquals("line2", result.getCustomerInfoData().getBillToStreet2());
		Assert.assertEquals("COUNTRY", result.getCustomerInfoData().getBillToCountry());

		Assert.assertNotNull(result.getOrderInfoData());
		Assert.assertEquals("comments", result.getOrderInfoData().getComments());
		Assert.assertNull(result.getOrderInfoData().getOrderNumber());
		Assert.assertNull(result.getOrderInfoData().getOrderPageRequestToken());
		Assert.assertNull(result.getOrderInfoData().getOrderPageTransactionType());
		Assert.assertNull(result.getOrderInfoData().getSubscriptionTitle());
		Assert.assertNull(result.getOrderInfoData().getTaxAmount());

		Assert.assertNotNull(result.getPaymentInfoData());
		Assert.assertEquals("creditCardNumber", result.getPaymentInfoData().getCardAccountNumber());
		Assert.assertEquals(Integer.valueOf(1), result.getPaymentInfoData().getCardExpirationMonth());
		Assert.assertEquals(Integer.valueOf(2015), result.getPaymentInfoData().getCardExpirationYear());
		Assert.assertNull(result.getPaymentInfoData().getCardStartMonth());
		Assert.assertNull(result.getPaymentInfoData().getCardStartYear());
		Assert.assertNull(result.getPaymentInfoData().getPaymentOption());

		Assert.assertNotNull(result.getSignatureData());
		Assert.assertEquals(BigDecimal.TEN, result.getSignatureData().getAmount());
		Assert.assertNull(result.getSignatureData().getAmountPublicSignature());
		Assert.assertEquals("USD", result.getSignatureData().getCurrency());
		Assert.assertNull(result.getSignatureData().getCurrencyPublicSignature());
		Assert.assertEquals("clientAuthorizationId", result.getSignatureData().getMerchantID());
		Assert.assertNull(result.getSignatureData().getOrderPageSerialNumber());
		Assert.assertNull(result.getSignatureData().getOrderPageVersion());
		Assert.assertNull(result.getSignatureData().getSignedFields());
		Assert.assertEquals("transactionVerificationKey", result.getSignatureData().getTransactionSignature());

	}

}
