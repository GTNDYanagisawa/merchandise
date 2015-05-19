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
package de.hybris.platform.payment.methods.impl;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.payment.commands.factory.CommandNotSupportedException;
import de.hybris.platform.payment.commands.request.AuthorizationRequest;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.request.CreateSubscriptionRequest;
import de.hybris.platform.payment.commands.request.EnrollmentCheckRequest;
import de.hybris.platform.payment.commands.request.FollowOnRefundRequest;
import de.hybris.platform.payment.commands.request.StandaloneRefundRequest;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.request.SubscriptionDataRequest;
import de.hybris.platform.payment.commands.request.UpdateSubscriptionRequest;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.commands.result.EnrollmentCheckResult;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.commands.result.SubscriptionDataResult;
import de.hybris.platform.payment.commands.result.SubscriptionResult;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


@ManualTest
//manual test: sended request count to external ressouce is limited
public class DefaultCardPaymentServiceImplTest
{
	private static final String TEST_CC_NUMBER = "4111111111111111";
	private static final String TEST_CC_NUMBER_VISA_3D_01 = "4000000000000002";
	//private static final String TEST_CC_NUMBER_VISA_3D_02 = "4000000000000119";
	private static final String TEST_CC_NUMBER_VISA_3D_NOT_ENROLLED = "4000000000000051";
	private static final String TEST_CC_NUMBER_VISA_3D_ERROR = "4000000000000051";
	private static final int TEST_CC_EXPIRATION_MONTH = 12;
	private static final int TEST_CC_EXPIRATION_YEAR = (Calendar.getInstance().get(Calendar.YEAR) + 2);
	private static final Currency TEST_CURRENCY = Currency.getInstance("USD");
	private static final String HTTP_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	private static final String HTTP_USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.8) Gecko/20100202 Firefox/3.5.8 (.NET CLR 3.5.30729)";

	private static DefaultCardPaymentServiceImpl cardPaymentService;

	@BeforeClass
	public static void init()
	{
		Registry.activateMasterTenant();
		final ApplicationContext applicationContext = Registry.getGlobalApplicationContext();
		cardPaymentService = (DefaultCardPaymentServiceImpl) applicationContext.getBean("cardPaymentService");
	}


	@Test
	public void testAuthorize() throws CommandNotSupportedException
	{
		final double moneyAmount = 117.83;
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo billingInfo = createBillingInfo();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(billingInfo);
		final AuthorizationResult resp = cardPaymentService.authorize(new AuthorizationRequest(testMerchantReferenceCode, card,
				TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, resp.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, resp.getTransactionStatusDetails());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), resp.getTotalAmount());
		Assert.assertNotNull(resp.getAuthorizationTime());
		Assert.assertNotNull(resp.getRequestId());
		Assert.assertNotNull(resp.getRequestToken());
	}


	@Test
	public void testSubscriptionAuthorizeFail() throws CommandNotSupportedException
	{
		final double moneyAmount = 117.83;
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo billingInfo = createBillingInfo();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(billingInfo);
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		final AuthorizationResult resp = cardPaymentService.authorize(new SubscriptionAuthorizationRequest(
				testMerchantReferenceCode, "9852331711410008284319", TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo,
				authorizationResult.getPaymentProvider()));

		Assert.assertEquals(TransactionStatus.REJECTED, resp.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.INVALID_REQUEST, resp.getTransactionStatusDetails());
	}


	@Test
	public void testAuthorizeFail() throws CommandNotSupportedException
	{
		final double moneyAmount = 117.83;
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo billingInfo = createBillingInfo();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(billingInfo);

		card.setCardNumber("12354657489");
		final AuthorizationResult resp = cardPaymentService.authorize(new AuthorizationRequest(testMerchantReferenceCode, card,
				TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.REJECTED, resp.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.INVALID_ACCOUNT_NUMBER, resp.getTransactionStatusDetails());

	}

	/**
	 * Tests authorization capture. It makes two requests: first one to authorize CC, second one to capture the
	 * authorization.
	 * 
	 * @throws CommandNotSupportedException
	 */

	@Test
	public void testCapture() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;

		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(createBillingInfo());
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final CaptureResult captureResult = cardPaymentService.capture(new CaptureRequest(testMerchantReferenceCode,
				authorizationResult.getRequestId(), authorizationResult.getRequestToken(), TEST_CURRENCY, BigDecimal
						.valueOf(moneyAmount), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(testMerchantReferenceCode, captureResult.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult.getTransactionStatusDetails());
		Assert.assertEquals(TEST_CURRENCY, captureResult.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), captureResult.getTotalAmount());
		Assert.assertNotNull(captureResult.getRequestTime());
		Assert.assertNotNull(captureResult.getRequestId());
		Assert.assertNotNull(captureResult.getRequestToken());
		Assert.assertNotNull(captureResult.getReconciliationId());
	}


	@Test
	public void testEnrollmentCheckRequired() throws CommandNotSupportedException
	{
		final double moneyAmount = 732.19;

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardNumber(TEST_CC_NUMBER_VISA_3D_01);
		cardInfo.setExpirationMonth(Integer.valueOf(TEST_CC_EXPIRATION_MONTH));
		cardInfo.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR));

		final EnrollmentCheckResult enrollmentCheckResult = cardPaymentService.enrollmentCheck(new EnrollmentCheckRequest(
				testMerchantReferenceCode, cardInfo, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), HTTP_ACCEPT, HTTP_USER_AGENT));

		Assert.assertNotNull(enrollmentCheckResult);
		Assert.assertNotNull(enrollmentCheckResult.getAcsURL());
		Assert.assertNotNull(enrollmentCheckResult.getPaReq());
		Assert.assertNotNull(enrollmentCheckResult.getProxyPAN());
		Assert.assertNotNull(enrollmentCheckResult.getXid());
		Assert.assertNotNull(enrollmentCheckResult.getProofXml());
		Assert.assertEquals("Y", enrollmentCheckResult.getVeresEnrolled());
		Assert.assertEquals(TransactionStatus.REJECTED, enrollmentCheckResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.THREE_D_SECURE_AUTHENTICATION_REQUIRED,
				enrollmentCheckResult.getTransactionStatusDetails());
	}


	@Test
	public void testEnrollmentCheckNotSupported() throws CommandNotSupportedException
	{
		final double moneyAmount = 732.19;

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardNumber(TEST_CC_NUMBER_VISA_3D_NOT_ENROLLED);
		cardInfo.setExpirationMonth(Integer.valueOf(TEST_CC_EXPIRATION_MONTH));
		cardInfo.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR));

		final EnrollmentCheckResult enrollmentCheckResult = cardPaymentService.enrollmentCheck(new EnrollmentCheckRequest(
				testMerchantReferenceCode, cardInfo, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), HTTP_ACCEPT, HTTP_USER_AGENT));

		Assert.assertNotNull(enrollmentCheckResult);
		Assert.assertNull(enrollmentCheckResult.getAcsURL());
		Assert.assertNull(enrollmentCheckResult.getPaReq());
		Assert.assertNull(enrollmentCheckResult.getProxyPAN());
		Assert.assertNull(enrollmentCheckResult.getXid());
		Assert.assertNotNull(enrollmentCheckResult.getProofXml());
		Assert.assertNotNull(enrollmentCheckResult.getCommerceIndicator());
		Assert.assertNotSame("Y", enrollmentCheckResult.getVeresEnrolled());
		Assert.assertEquals(TransactionStatus.ACCEPTED, enrollmentCheckResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.THREE_D_SECURE_NOT_SUPPORTED,
				enrollmentCheckResult.getTransactionStatusDetails());
	}


	@Test
	public void testEnrollmentCheckError() throws CommandNotSupportedException
	{
		final double moneyAmount = 591.33;

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final CardInfo cardInfo = new CardInfo();
		cardInfo.setCardNumber(TEST_CC_NUMBER_VISA_3D_ERROR);
		cardInfo.setExpirationMonth(Integer.valueOf(TEST_CC_EXPIRATION_MONTH));
		cardInfo.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR));

		final EnrollmentCheckResult enrollmentCheckResult = cardPaymentService.enrollmentCheck(new EnrollmentCheckRequest(
				testMerchantReferenceCode, cardInfo, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), HTTP_ACCEPT, HTTP_USER_AGENT));

		Assert.assertNotNull(enrollmentCheckResult);
		Assert.assertNull(enrollmentCheckResult.getAcsURL());
		Assert.assertNull(enrollmentCheckResult.getPaReq());
		Assert.assertNull(enrollmentCheckResult.getProxyPAN());
		Assert.assertNull(enrollmentCheckResult.getXid());
		Assert.assertNotNull(enrollmentCheckResult.getProofXml());
		Assert.assertNotNull(enrollmentCheckResult.getCommerceIndicator());
		Assert.assertNotSame("Y", enrollmentCheckResult.getVeresEnrolled());
		Assert.assertEquals(TransactionStatus.ACCEPTED, enrollmentCheckResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.THREE_D_SECURE_NOT_SUPPORTED,
				enrollmentCheckResult.getTransactionStatusDetails());
	}


	@Test
	public void testVoidCreditOrCapture() throws CommandNotSupportedException
	{
		final double moneyAmount = 117.83;
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo billingInfo = createBillingInfo();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(billingInfo);
		final AuthorizationResult resp = cardPaymentService.authorize(new AuthorizationRequest(testMerchantReferenceCode, card,
				TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, resp.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, resp.getTransactionStatusDetails());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), resp.getTotalAmount());
		Assert.assertNotNull(resp.getAuthorizationTime());
		Assert.assertNotNull(resp.getRequestId());
		Assert.assertNotNull(resp.getRequestToken());

		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final CaptureResult captureResult = cardPaymentService.capture(new CaptureRequest(testMerchantReferenceCode,
				authorizationResult.getRequestId(), authorizationResult.getRequestToken(), TEST_CURRENCY, BigDecimal
						.valueOf(moneyAmount), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(testMerchantReferenceCode, captureResult.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult.getTransactionStatusDetails());
		Assert.assertEquals(TEST_CURRENCY, captureResult.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), captureResult.getTotalAmount());
		Assert.assertNotNull(captureResult.getRequestTime());
		Assert.assertNotNull(captureResult.getRequestId());
		Assert.assertNotNull(captureResult.getRequestToken());
		Assert.assertNotNull(captureResult.getReconciliationId());

		final VoidResult refundResult = cardPaymentService.voidCreditOrCapture(new VoidRequest(testMerchantReferenceCode,
				captureResult.getRequestId(), captureResult.getRequestToken(), authorizationResult.getPaymentProvider()));
		Assert.assertNotNull(refundResult);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult.getTransactionStatus());
		Assert.assertEquals(refundResult.getAmount(), BigDecimal.valueOf(moneyAmount));
	}


	@Test
	public void testVoidCreditOrCaptureNotVoidable() throws CommandNotSupportedException
	{
		final double moneyAmount = 117.83;
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo billingInfo = createBillingInfo();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(billingInfo);
		final AuthorizationResult resp = cardPaymentService.authorize(new AuthorizationRequest(testMerchantReferenceCode, card,
				TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, resp.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, resp.getTransactionStatusDetails());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), resp.getTotalAmount());
		Assert.assertNotNull(resp.getAuthorizationTime());
		Assert.assertNotNull(resp.getRequestId());
		Assert.assertNotNull(resp.getRequestToken());

		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final VoidResult refundResult = cardPaymentService.voidCreditOrCapture(new VoidRequest(testMerchantReferenceCode,
				authorizationResult.getRequestId(), authorizationResult.getRequestToken(), authorizationResult.getPaymentProvider()));

		Assert.assertNotNull(refundResult);
		Assert.assertEquals(TransactionStatus.REJECTED, refundResult.getTransactionStatus());
		Assert.assertEquals(refundResult.getTransactionStatusDetails(), TransactionStatusDetails.NOT_VOIDABLE);
	}


	@Test
	public void testRefundFollowOn() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;

		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(createBillingInfo());
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final CaptureResult captureResult = cardPaymentService.capture(new CaptureRequest(testMerchantReferenceCode,
				authorizationResult.getRequestId(), authorizationResult.getRequestToken(), TEST_CURRENCY, BigDecimal
						.valueOf(moneyAmount), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(testMerchantReferenceCode, captureResult.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult.getTransactionStatusDetails());
		Assert.assertEquals(TEST_CURRENCY, captureResult.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), captureResult.getTotalAmount());
		Assert.assertNotNull(captureResult.getRequestTime());
		Assert.assertNotNull(captureResult.getRequestId());
		Assert.assertNotNull(captureResult.getRequestToken());
		Assert.assertNotNull(captureResult.getReconciliationId());

		final RefundResult refundResult = cardPaymentService.refundFollowOn(new FollowOnRefundRequest(testMerchantReferenceCode,
				captureResult.getRequestId(), captureResult.getRequestToken(), captureResult.getCurrency(), captureResult
						.getTotalAmount().subtract(new BigDecimal(25)), authorizationResult.getPaymentProvider()));

		Assert.assertNotNull(refundResult);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult.getReconciliationId());
		Assert.assertNotNull(refundResult.getRequestTime());
		Assert.assertEquals(refundResult.getTotalAmount(), captureResult.getTotalAmount().subtract(new BigDecimal(25)));
		Assert.assertNotNull(refundResult.getCurrency());


		final RefundResult refundResult2 = cardPaymentService.refundFollowOn(new FollowOnRefundRequest(testMerchantReferenceCode,
				captureResult.getRequestId(), captureResult.getRequestToken(), captureResult.getCurrency(), new BigDecimal(12),
				authorizationResult.getPaymentProvider()));

		Assert.assertNotNull(refundResult2);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult2.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult2.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult2.getReconciliationId());
		Assert.assertNotNull(refundResult2.getRequestTime());

		Assert.assertEquals(refundResult2.getTotalAmount(), new BigDecimal("12.00"));
		Assert.assertNotNull(refundResult2.getCurrency());


		final RefundResult refundResult3 = cardPaymentService.refundFollowOn(new FollowOnRefundRequest(testMerchantReferenceCode,
				captureResult.getRequestId(), captureResult.getRequestToken(), captureResult.getCurrency(), new BigDecimal(25),
				authorizationResult.getPaymentProvider()));
		Assert.assertNotNull(refundResult3);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult3.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult3.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult3.getReconciliationId());
		Assert.assertNotNull(refundResult3.getRequestTime());
		Assert.assertEquals(refundResult3.getTotalAmount(), new BigDecimal("25.00"));
		Assert.assertNotNull(refundResult3.getCurrency());
	}


	@Test
	public void testRefundStandalone() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;

		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(createBillingInfo());
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final CaptureResult captureResult = cardPaymentService.capture(new CaptureRequest(testMerchantReferenceCode,
				authorizationResult.getRequestId(), authorizationResult.getRequestToken(), TEST_CURRENCY, BigDecimal
						.valueOf(moneyAmount), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(testMerchantReferenceCode, captureResult.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult.getTransactionStatusDetails());
		Assert.assertEquals(TEST_CURRENCY, captureResult.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), captureResult.getTotalAmount());
		Assert.assertNotNull(captureResult.getRequestTime());
		Assert.assertNotNull(captureResult.getRequestId());
		Assert.assertNotNull(captureResult.getRequestToken());
		Assert.assertNotNull(captureResult.getReconciliationId());

		final RefundResult refundResult = cardPaymentService.refundStandalone(new StandaloneRefundRequest(
				testMerchantReferenceCode, createBillingInfo(), getCardInfo(createBillingInfo()), captureResult.getCurrency(),
				captureResult.getTotalAmount()));
		Assert.assertNotNull(refundResult);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult.getReconciliationId());
		Assert.assertNotNull(refundResult.getRequestTime());
		Assert.assertEquals(refundResult.getTotalAmount(), captureResult.getTotalAmount());
		Assert.assertNotNull(refundResult.getCurrency());

	}

	/**
	 * Tests subscription create and authorize functionality.
	 * 
	 * @throws CommandNotSupportedException
	 */

	@Test
	public void testCreateAndAuthorizeSubscription() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;

		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(createBillingInfo());
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final SubscriptionResult subscriptionResult = cardPaymentService.createSubscription(new CreateSubscriptionRequest(
				testMerchantReferenceCode, shippingInfo, TEST_CURRENCY, card, authorizationResult.getRequestId(), authorizationResult
						.getRequestToken(), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(TransactionStatus.ACCEPTED, subscriptionResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, subscriptionResult.getTransactionStatusDetails());
		Assert.assertNotNull(subscriptionResult.getRequestId());
		Assert.assertNotNull(subscriptionResult.getRequestToken());
		Assert.assertNotNull(subscriptionResult.getSubscriptionID());

		final AuthorizationResult resp = cardPaymentService.authorize(new SubscriptionAuthorizationRequest(
				testMerchantReferenceCode, subscriptionResult.getSubscriptionID(), TEST_CURRENCY, BigDecimal.valueOf(moneyAmount),
				shippingInfo, authorizationResult.getPaymentProvider()));

		Assert.assertEquals(TransactionStatus.ACCEPTED, resp.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, resp.getTransactionStatusDetails());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), resp.getTotalAmount());
		Assert.assertNotNull(resp.getAuthorizationTime());
		Assert.assertNotNull(resp.getRequestId());
		Assert.assertNotNull(resp.getRequestToken());

	}

	/**
	 * Tests subscription create standalone.
	 * 
	 * @throws CommandNotSupportedException
	 */

	@Test
	public void testCreateSubscriptionStandalone() throws CommandNotSupportedException
	{
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(createBillingInfo());

		final CreateSubscriptionRequest request = new CreateSubscriptionRequest(testMerchantReferenceCode, shippingInfo,
				TEST_CURRENCY, card, null, null, "Cybersource");


		final SubscriptionResult subscriptionResult = cardPaymentService.createSubscription(request);

		Logger.getLogger(DefaultCardPaymentServiceImplTest.class).info(
				" *** TXN RESULT: " + subscriptionResult.getTransactionStatus() + " ["
						+ subscriptionResult.getTransactionStatusDetails() + "] ***");

		Assert.assertEquals(TransactionStatus.ACCEPTED, subscriptionResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, subscriptionResult.getTransactionStatusDetails());
		Assert.assertNotNull(subscriptionResult.getRequestId());
		Assert.assertNotNull(subscriptionResult.getRequestToken());
		Assert.assertNotNull(subscriptionResult.getSubscriptionID());
	}

	/**
	 * Tests subscription use. It makes three requests: first one to authorize CC, second one to make the subscription
	 * and third to use it
	 * 
	 * @throws CommandNotSupportedException
	 */

	@Test
	public void testUseSubscriptionForAuthorization() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;
		final double secondMoneyAmount = 29.92;

		// authorize
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(createBillingInfo());
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		// create subscription
		final SubscriptionResult subscriptionResult = cardPaymentService.createSubscription(new CreateSubscriptionRequest(
				testMerchantReferenceCode, shippingInfo, TEST_CURRENCY, card, authorizationResult.getRequestId(), authorizationResult
						.getRequestToken(), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(TransactionStatus.ACCEPTED, subscriptionResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, subscriptionResult.getTransactionStatusDetails());
		Assert.assertNotNull(subscriptionResult.getRequestId());
		Assert.assertNotNull(subscriptionResult.getRequestToken());
		Assert.assertNotNull(subscriptionResult.getSubscriptionID());

		// authorize using subscription
		final String subscriptionID = subscriptionResult.getSubscriptionID();
		final AuthorizationResult secondAuthorizationResult = cardPaymentService.authorize(new SubscriptionAuthorizationRequest(
				testMerchantReferenceCode, subscriptionID, TEST_CURRENCY, BigDecimal.valueOf(secondMoneyAmount), null,
				authorizationResult.getPaymentProvider()));

		Assert.assertEquals(TransactionStatus.ACCEPTED, secondAuthorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(secondMoneyAmount), secondAuthorizationResult.getTotalAmount());
		Assert.assertNotNull(secondAuthorizationResult.getAuthorizationTime());
		Assert.assertNotNull(secondAuthorizationResult.getRequestId());
		Assert.assertNotNull(secondAuthorizationResult.getRequestToken());
	}

	/**
	 * Tests subscription update. It makes three requests: first one to authorize CC, second one to make the subscription
	 * and third to update it
	 * 
	 * @throws CommandNotSupportedException
	 */

	@Test
	public void testUpdateSubscription() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;

		// authorize
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(createBillingInfo());
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());
		Assert.assertNotNull(authorizationResult.getPaymentProvider());

		// create subscription
		final SubscriptionResult subscriptionResult = cardPaymentService.createSubscription(new CreateSubscriptionRequest(
				testMerchantReferenceCode, shippingInfo, TEST_CURRENCY, card, authorizationResult.getRequestId(), authorizationResult
						.getRequestToken(), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(TransactionStatus.ACCEPTED, subscriptionResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, subscriptionResult.getTransactionStatusDetails());
		Assert.assertNotNull(subscriptionResult.getRequestId());
		Assert.assertNotNull(subscriptionResult.getRequestToken());
		Assert.assertNotNull(subscriptionResult.getSubscriptionID());

		// update subscription
		shippingInfo.setStreet1("1221 N NewStreet");
		card.setExpirationYear(Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR) + 4));

		final String subscriptionID = subscriptionResult.getSubscriptionID();
		final SubscriptionResult secondSubscriptionResult = cardPaymentService.updateSubscription(new UpdateSubscriptionRequest(
				testMerchantReferenceCode, subscriptionID, authorizationResult.getPaymentProvider(), shippingInfo, card));

		Assert.assertEquals(TransactionStatus.ACCEPTED, secondSubscriptionResult.getTransactionStatus());
		Assert.assertNotNull(secondSubscriptionResult.getRequestId());
		Assert.assertNotNull(secondSubscriptionResult.getRequestToken());
	}

	@Test
	public void testGetSubscriptionData() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;

		// authorize
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfoOrig = createShippingInfo();
		final BillingInfo billingInfo = createBillingInfo();
		final CardInfo cardOrig = getCardInfo(billingInfo);
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, cardOrig, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfoOrig));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());
		Assert.assertNotNull(authorizationResult.getPaymentProvider());

		// create subscription first
		final SubscriptionResult subscriptionResult = cardPaymentService.createSubscription(new CreateSubscriptionRequest(
				testMerchantReferenceCode, shippingInfoOrig, TEST_CURRENCY, cardOrig, authorizationResult.getRequestId(),
				authorizationResult.getRequestToken(), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(TransactionStatus.ACCEPTED, subscriptionResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, subscriptionResult.getTransactionStatusDetails());
		Assert.assertNotNull(subscriptionResult.getRequestId());
		Assert.assertNotNull(subscriptionResult.getRequestToken());
		Assert.assertNotNull(subscriptionResult.getSubscriptionID());

		// get info back
		final SubscriptionDataResult subscriptionDataResult = cardPaymentService.getSubscriptionData(new SubscriptionDataRequest(
				testMerchantReferenceCode, subscriptionResult.getSubscriptionID(), authorizationResult.getPaymentProvider()));

		Logger.getLogger(DefaultCardPaymentServiceImplTest.class).info(
				" *** TXN RESULT: " + subscriptionDataResult.getTransactionStatus() + " ["
						+ subscriptionDataResult.getTransactionStatusDetails() + "] ***");

		Assert.assertEquals(TransactionStatus.ACCEPTED, subscriptionDataResult.getTransactionStatus());
		Assert.assertNotNull(subscriptionDataResult.getRequestId());
		Assert.assertNotNull(subscriptionDataResult.getRequestToken());

		Assert.assertEquals("411111XXXXXX1111", subscriptionDataResult.getCard().getCardNumber());
		Assert.assertEquals(cardOrig.getCardType(), subscriptionDataResult.getCard().getCardType());
		Assert.assertEquals(cardOrig.getExpirationMonth(), subscriptionDataResult.getCard().getExpirationMonth());
		Assert.assertEquals(cardOrig.getExpirationYear(), subscriptionDataResult.getCard().getExpirationYear());

		Assert.assertEquals(billingInfo.getFirstName().toUpperCase(), subscriptionDataResult.getBillingInfo().getFirstName());
		Assert.assertEquals(billingInfo.getLastName().toUpperCase(), subscriptionDataResult.getBillingInfo().getLastName());
		Assert.assertEquals(billingInfo.getStreet1(), subscriptionDataResult.getBillingInfo().getStreet1());
		Assert.assertEquals(billingInfo.getStreet2(), subscriptionDataResult.getBillingInfo().getStreet2());
		Assert.assertEquals(billingInfo.getCity(), subscriptionDataResult.getBillingInfo().getCity());
		Assert.assertEquals(billingInfo.getState(), subscriptionDataResult.getBillingInfo().getState());
		Assert.assertEquals(billingInfo.getCountry(), subscriptionDataResult.getBillingInfo().getCountry());
		Assert.assertEquals(billingInfo.getPostalCode(), subscriptionDataResult.getBillingInfo().getPostalCode());
	}

	/**
	 * Tests subscription use. It makes three requests: first one to authorize CC, second one to make the subscription
	 * and third to use it
	 * 
	 * @throws CommandNotSupportedException
	 */

	@Test
	public void testUseSubscriptionForStandaloneRefund() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;
		final double secondMoneyAmount = 29.92;

		// authorize
		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(createBillingInfo());
		final AuthorizationResult authorizationResult = cardPaymentService.authorize(new AuthorizationRequest(
				testMerchantReferenceCode, card, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo));

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		// create subscription
		final SubscriptionResult subscriptionResult = cardPaymentService.createSubscription(new CreateSubscriptionRequest(
				testMerchantReferenceCode, shippingInfo, TEST_CURRENCY, card, authorizationResult.getRequestId(), authorizationResult
						.getRequestToken(), authorizationResult.getPaymentProvider()));

		Assert.assertEquals(TransactionStatus.ACCEPTED, subscriptionResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, subscriptionResult.getTransactionStatusDetails());
		Assert.assertNotNull(subscriptionResult.getRequestId());
		Assert.assertNotNull(subscriptionResult.getRequestToken());
		Assert.assertNotNull(subscriptionResult.getSubscriptionID());

		// standalone refund using subscription
		final String subscriptionID = subscriptionResult.getSubscriptionID();
		final RefundResult refundResult = cardPaymentService.refundStandalone(new StandaloneRefundRequest(
				testMerchantReferenceCode, subscriptionID, null, null, TEST_CURRENCY, BigDecimal.valueOf(secondMoneyAmount)));

		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult.getReconciliationId());
		Assert.assertNotNull(refundResult.getRequestTime());
		Assert.assertNotNull(refundResult.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(secondMoneyAmount), refundResult.getTotalAmount());
		Assert.assertNotNull(refundResult.getRequestId());
		Assert.assertNotNull(refundResult.getRequestToken());
	}

	///////////// TESTING IS OVER AT THIS POINT ///////////////

	private BillingInfo createShippingInfo()
	{
		final BillingInfo shippingInfo = new BillingInfo();
		shippingInfo.setFirstName("Jane");
		shippingInfo.setLastName("Doe");
		shippingInfo.setStreet1("100 Elm Street");
		shippingInfo.setCity("San Mateo");
		shippingInfo.setState("CA");
		shippingInfo.setPostalCode("94401");
		shippingInfo.setCountry("US");
		shippingInfo.setEmail("a@a.com");
		return shippingInfo;
	}

	private BillingInfo createBillingInfo()
	{
		final BillingInfo billingInfo = new BillingInfo();
		billingInfo.setCity("Mountain View");
		billingInfo.setCountry("US");
		billingInfo.setEmail("nobody@cybersource.com");
		billingInfo.setFirstName("John");
		billingInfo.setIpAddress("10.7" + "." + "7.7");
		billingInfo.setLastName("Doe");
		billingInfo.setPhoneNumber("650-965-6000");
		billingInfo.setPostalCode("94043");
		billingInfo.setState("CA");
		billingInfo.setStreet1("1295 Charleston Road");
		return billingInfo;
	}

	private CardInfo getCardInfo(final BillingInfo billingInfo)
	{
		final CardInfo card = new CardInfo();
		card.setCardHolderFullName("John Doe");
		card.setCardNumber(TEST_CC_NUMBER);
		card.setExpirationMonth(Integer.valueOf(TEST_CC_EXPIRATION_MONTH));
		card.setExpirationYear(Integer.valueOf(TEST_CC_EXPIRATION_YEAR));
		card.setBillingInfo(billingInfo);
		card.setCardType(CreditCardType.VISA);
		return card;
	}


	private String generateMerchantReferenceCode()
	{
		final String currentTime = String.valueOf(System.currentTimeMillis());

		return "testMerchant" + currentTime.substring(currentTime.length() - 3);
	}

}
