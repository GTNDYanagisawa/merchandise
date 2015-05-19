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
package de.hybris.platform.cybersource.commands;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.cybersource.adapter.CardRegistry;
import de.hybris.platform.payment.commands.AuthorizationCommand;
import de.hybris.platform.payment.commands.CaptureCommand;
import de.hybris.platform.payment.commands.CreateSubscriptionCommand;
import de.hybris.platform.payment.commands.EnrollmentCheckCommand;
import de.hybris.platform.payment.commands.FollowOnRefundCommand;
import de.hybris.platform.payment.commands.PartialCaptureCommand;
import de.hybris.platform.payment.commands.StandaloneRefundCommand;
import de.hybris.platform.payment.commands.SubscriptionAuthorizationCommand;
import de.hybris.platform.payment.commands.VoidCommand;
import de.hybris.platform.payment.commands.factory.CommandFactory;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.commands.factory.CommandNotSupportedException;
import de.hybris.platform.payment.commands.request.AuthorizationRequest;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.request.CreateSubscriptionRequest;
import de.hybris.platform.payment.commands.request.EnrollmentCheckRequest;
import de.hybris.platform.payment.commands.request.FollowOnRefundRequest;
import de.hybris.platform.payment.commands.request.PartialCaptureRequest;
import de.hybris.platform.payment.commands.request.StandaloneRefundRequest;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;
import de.hybris.platform.payment.commands.request.VoidRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.commands.result.EnrollmentCheckResult;
import de.hybris.platform.payment.commands.result.RefundResult;
import de.hybris.platform.payment.commands.result.SubscriptionResult;
import de.hybris.platform.payment.commands.result.VoidResult;
import de.hybris.platform.payment.dto.BasicCardInfo;
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
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;


/**
 * Tests for commands that implements cybersource payment provider adapter.
 */
@ManualTest
//manual test: sended request count to external ressouce is limited
public class CybersourceCommandTest
{
	private static final String CYBERSOURCE = "Cybersourse";

	private static final String TEST_CC_NUMBER = "4111111111111111";
	private static final String TEST_CC_NUMBER_VISA_3D_01 = "4000000000000002";
	private static final String TEST_CC_NUMBER_VISA_3D_02 = "4000000000000119";
	private static final String TEST_CC_NUMBER_VISA_3D_NOT_ENROLLED = "4000000000000051";
	private static final String TEST_CC_NUMBER_VISA_3D_ERROR = "4000000000000051";

	private static final int TEST_CC_EXPIRATION_MONTH = 12;
	private static final int TEST_CC_EXPIRATION_YEAR = (Calendar.getInstance().get(Calendar.YEAR) + 2);
	private static final Currency TEST_CURRENCY = Currency.getInstance("USD");
	private static final String HTTP_ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	private static final String HTTP_USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.8) Gecko/20100202 Firefox/3.5.8 (.NET CLR 3.5.30729)";

	private static CommandFactory commandFactory;
	private static CardRegistry cardRegistry;

	// during the init process, we will set log level to DEBUG (if we have to)
	// ... so during the execution of RequestExecutorImpl#run we will still see the dump of 'credit card authorization request' and 'credit card authorization reply'
	//private static final Logger LOG = Logger.getLogger(RequestExecutorImpl.class.getName());

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CybersourceCommandTest.class.getName());

	//	private static final Level INITAL_LEVEL = LOG.getLevel();

	@BeforeClass
	public static void init()
	{
		Registry.activateMasterTenant();
		final ApplicationContext applicationContext = Registry.getGlobalApplicationContext();
		final CommandFactoryRegistry cfp = (CommandFactoryRegistry) applicationContext.getBean("commandFactoryRegistry");
		cardRegistry = (CardRegistry) applicationContext.getBean("cardRegistry");
		commandFactory = cfp.getFactory((CardInfo) null, false);

		// setting log level to DEBUG, so during the execution of RequestExecutorImpl#run we will see the dump of 'credit card authorization request' and 'credit card authorization reply'
		//		if (LOG.getLevel() == null || !LOG.getLevel().isGreaterOrEqual(Level.DEBUG))
		//		{
		//			LOG.setLevel(Level.DEBUG);
		//		}
	}

	//	@AfterClass
	//	public static void tearDown() throws ConsistencyCheckException
	//	{
	//		// reset log level
	//		LOG.setLevel(INITAL_LEVEL);
	//	}

	/**
	 * Tests single CC authorization
	 * 
	 * @throws CommandNotSupportedException
	 */
	@Test
	public void testAuthorizationCommand() throws CommandNotSupportedException
	{
		final double moneyAmount = 117.83;
		final AuthorizationCommand authorizationCommand = commandFactory.createCommand(AuthorizationCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final AuthorizationRequest request = prepareAuthorizationRequest(cardRegistry, testMerchantReferenceCode, moneyAmount);

		final AuthorizationResult resp = authorizationCommand.perform(request);

		Assert.assertEquals(TransactionStatus.ACCEPTED, resp.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, resp.getTransactionStatusDetails());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), resp.getTotalAmount());
		Assert.assertNotNull(resp.getAuthorizationTime());
		Assert.assertNotNull(resp.getRequestId());
		Assert.assertNotNull(resp.getRequestToken());
	}

	@Test
	public void testCreateAndAuthorizeSubscription() throws CommandNotSupportedException
	{
		// PREAUTHORIZE AND CREATE SUBSCRIPTION

		final double moneyAmount = 161.47;
		final AuthorizationCommand authorizationCommand = commandFactory.createCommand(AuthorizationCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();
		final AuthorizationRequest preAuthorizationRequest = prepareAuthorizationRequest(cardRegistry, testMerchantReferenceCode,
				moneyAmount);

		final AuthorizationResult preAuthorizationResult = authorizationCommand.perform(preAuthorizationRequest);

		final CreateSubscriptionCommand creatSubscriptionCommand = commandFactory.createCommand(CreateSubscriptionCommand.class);
		final CreateSubscriptionRequest req = prepareCreateSubscriptionRequest(preAuthorizationResult, testMerchantReferenceCode,
				moneyAmount);
		final SubscriptionResult subscriptionResult = creatSubscriptionCommand.perform(req);

		Assert.assertEquals(TransactionStatus.ACCEPTED, subscriptionResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, subscriptionResult.getTransactionStatusDetails());
		Assert.assertNotNull(subscriptionResult.getRequestId());
		Assert.assertNotNull(subscriptionResult.getRequestToken());

		// AUTHORIZE SUBSCRIPTION

		final SubscriptionAuthorizationRequest subscriptionAuthorizationRequest = prepareSubscriptionAuthorizationRequest(
				subscriptionResult.getSubscriptionID(), testMerchantReferenceCode, moneyAmount);

		final SubscriptionAuthorizationCommand subscriptionAuthorizationCommand = commandFactory
				.createCommand(SubscriptionAuthorizationCommand.class);

		final AuthorizationResult resp = subscriptionAuthorizationCommand.perform(subscriptionAuthorizationRequest);

		Assert.assertEquals(TransactionStatus.ACCEPTED, resp.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, resp.getTransactionStatusDetails());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), resp.getTotalAmount());
		Assert.assertNotNull(resp.getAuthorizationTime());
		Assert.assertNotNull(resp.getRequestId());
		Assert.assertNotNull(resp.getRequestToken());
	}

	/**
	 * Tests authorization capture. It makes two requests: first one to authorize CC, second one to capture the
	 * authorization.
	 * 
	 * @throws CommandNotSupportedException
	 */
	@Test
	public void testCaptureCommand() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;
		final AuthorizationCommand authorizationCommand = commandFactory.createCommand(AuthorizationCommand.class);
		final CaptureCommand captureCommand = commandFactory.createCommand(CaptureCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final AuthorizationRequest authorizationRequest = prepareAuthorizationRequest(cardRegistry, testMerchantReferenceCode,
				moneyAmount);

		final AuthorizationResult authorizationResult = authorizationCommand.perform(authorizationRequest);

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final CaptureRequest captureRequest = prepareCaptureRequest(authorizationResult.getRequestId(),
				authorizationResult.getRequestToken(), testMerchantReferenceCode, moneyAmount);

		final CaptureResult captureResult = captureCommand.perform(captureRequest);

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

	/*
	 * Given a Card and there was successful authorization on the Card and the authorization has not expired and the
	 * capture amount is less than amount in authorization. Then a card partial capture request should return a reply
	 * that indicates a successful operation. Actually the 2nd partial capture fails
	 */
	@Ignore("CYBS-10")
	@Test
	public void testPartialCaptureCommand() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;
		final AuthorizationCommand authorizationCommand = commandFactory.createCommand(AuthorizationCommand.class);
		final PartialCaptureCommand captureCommand = commandFactory.createCommand(PartialCaptureCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final AuthorizationRequest authorizationRequest = prepareAuthorizationRequest(cardRegistry, testMerchantReferenceCode,
				moneyAmount);

		final AuthorizationResult authorizationResult = authorizationCommand.perform(authorizationRequest);

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final PartialCaptureRequest captureRequest1 = preparePartialCaptureRequest(authorizationResult.getRequestId(),
				authorizationResult.getRequestToken(), testMerchantReferenceCode, moneyAmount - 100, "1");
		final CaptureResult captureResult1 = captureCommand.perform(captureRequest1);

		Assert.assertEquals(testMerchantReferenceCode, captureResult1.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult1.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult1.getTransactionStatusDetails());
		Assert.assertEquals(TEST_CURRENCY, captureResult1.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount - 100), captureResult1.getTotalAmount());

		// With Paymentech (20051006_Bill_Me_Later_IG.pdf, page: 20)
		// To process partial captures or credits, you do not need to include any additional fields in
		// your requests. Simply request each capture or credit for the partial amount. For each
		// partial credit, you can send the request ID from a previous capture to reduce the number
		// of customer information fields that you must supply in the credit request. If you
		// processed multiple captures for the order, then use the request ID of the first capture

		final PartialCaptureRequest captureRequest2 = preparePartialCaptureRequest(authorizationResult.getRequestId(),
				authorizationResult.getRequestToken(), testMerchantReferenceCode, moneyAmount - 160, "2");

		final CaptureResult captureResult2 = captureCommand.perform(captureRequest2);

		Assert.assertEquals(testMerchantReferenceCode, captureResult2.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult2.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult2.getTransactionStatusDetails());
	}

	@Test
	public void testEnrollmentAuthRequired() throws CommandNotSupportedException
	{
		final double moneyAmount = 732.19;
		final EnrollmentCheckCommand enrollmentCheckCommand = commandFactory.createCommand(EnrollmentCheckCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final EnrollmentCheckRequest enrollmentCheckRequest = prepareEnrollmentCheckRequest(testMerchantReferenceCode, moneyAmount,
				TEST_CC_NUMBER_VISA_3D_01);

		final EnrollmentCheckResult enrollmentCheckResult = enrollmentCheckCommand.perform(enrollmentCheckRequest);

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


	@Ignore("CYBS-10")
	@Test
	public void testEnrollmentAuthRequired02() throws CommandNotSupportedException
	{
		final double moneyAmount = 732.19;
		final EnrollmentCheckCommand enrollmentCheckCommand = commandFactory.createCommand(EnrollmentCheckCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final EnrollmentCheckRequest enrollmentCheckRequest = prepareEnrollmentCheckRequest(testMerchantReferenceCode, moneyAmount,
				TEST_CC_NUMBER_VISA_3D_02);

		final EnrollmentCheckResult enrollmentCheckResult = enrollmentCheckCommand.perform(enrollmentCheckRequest);

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
	public void testEnrollmentNotSupported() throws CommandNotSupportedException
	{
		final double moneyAmount = 591.33;
		final EnrollmentCheckCommand enrollmentCheckCommand = commandFactory.createCommand(EnrollmentCheckCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final EnrollmentCheckRequest enrollmentCheckRequest = prepareEnrollmentCheckRequest(testMerchantReferenceCode, moneyAmount,
				TEST_CC_NUMBER_VISA_3D_NOT_ENROLLED);

		final EnrollmentCheckResult enrollmentCheckResult = enrollmentCheckCommand.perform(enrollmentCheckRequest);

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
	public void testEnrollmentError() throws CommandNotSupportedException
	{
		final double moneyAmount = 591.33;
		final EnrollmentCheckCommand enrollmentCheckCommand = commandFactory.createCommand(EnrollmentCheckCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final EnrollmentCheckRequest enrollmentCheckRequest = prepareEnrollmentCheckRequest(testMerchantReferenceCode, moneyAmount,
				TEST_CC_NUMBER_VISA_3D_ERROR);

		final EnrollmentCheckResult enrollmentCheckResult = enrollmentCheckCommand.perform(enrollmentCheckRequest);

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
	public void testVoidCommand() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;
		final AuthorizationCommand authorizationCommand = commandFactory.createCommand(AuthorizationCommand.class);
		final CaptureCommand captureCommand = commandFactory.createCommand(CaptureCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final AuthorizationRequest authorizationRequest = prepareAuthorizationRequest(cardRegistry, testMerchantReferenceCode,
				moneyAmount);

		final AuthorizationResult authorizationResult = authorizationCommand.perform(authorizationRequest);

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final CaptureRequest captureRequest = prepareCaptureRequest(authorizationResult.getRequestId(),
				authorizationResult.getRequestToken(), testMerchantReferenceCode, moneyAmount);

		final CaptureResult captureResult = captureCommand.perform(captureRequest);

		Assert.assertEquals(testMerchantReferenceCode, captureResult.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult.getTransactionStatusDetails());
		Assert.assertEquals(TEST_CURRENCY, captureResult.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), captureResult.getTotalAmount());
		Assert.assertNotNull(captureResult.getRequestTime());
		Assert.assertNotNull(captureResult.getRequestId());
		Assert.assertNotNull(captureResult.getRequestToken());
		Assert.assertNotNull(captureResult.getReconciliationId());

		final VoidCommand refundCommand = commandFactory.createCommand(VoidCommand.class);
		final VoidRequest refundRequest = new VoidRequest(testMerchantReferenceCode, captureResult.getRequestId(),
				captureResult.getRequestToken(), CYBERSOURCE);
		final VoidResult refundResult = refundCommand.perform(refundRequest);
		Assert.assertNotNull(refundResult);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult.getTransactionStatus());
		Assert.assertEquals(refundResult.getAmount(), BigDecimal.valueOf(moneyAmount));
	}


	@Test
	public void testVoidCommandNotVoidable() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;
		final AuthorizationCommand authorizationCommand = commandFactory.createCommand(AuthorizationCommand.class);
		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final AuthorizationRequest authorizationRequest = prepareAuthorizationRequest(cardRegistry, testMerchantReferenceCode,
				moneyAmount);

		final AuthorizationResult authorizationResult = authorizationCommand.perform(authorizationRequest);

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final VoidCommand refundCommand = commandFactory.createCommand(VoidCommand.class);
		final VoidRequest refundRequest = new VoidRequest(testMerchantReferenceCode, authorizationResult.getRequestId(),
				authorizationResult.getRequestToken(), CYBERSOURCE);
		final VoidResult refundResult = refundCommand.perform(refundRequest);
		Assert.assertNotNull(refundResult);
		Assert.assertEquals(TransactionStatus.REJECTED, refundResult.getTransactionStatus());
		Assert.assertEquals(refundResult.getTransactionStatusDetails(), TransactionStatusDetails.NOT_VOIDABLE);
	}

	@Test
	public void testFollowOnRefundCommand() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;
		final AuthorizationCommand authorizationCommand = commandFactory.createCommand(AuthorizationCommand.class);
		final CaptureCommand captureCommand = commandFactory.createCommand(CaptureCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final AuthorizationRequest authorizationRequest = prepareAuthorizationRequest(cardRegistry, testMerchantReferenceCode,
				moneyAmount);

		final AuthorizationResult authorizationResult = authorizationCommand.perform(authorizationRequest);

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final CaptureRequest captureRequest = prepareCaptureRequest(authorizationResult.getRequestId(),
				authorizationResult.getRequestToken(), testMerchantReferenceCode, moneyAmount);

		final CaptureResult captureResult = captureCommand.perform(captureRequest);

		Assert.assertEquals(testMerchantReferenceCode, captureResult.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult.getTransactionStatusDetails());
		Assert.assertEquals(TEST_CURRENCY, captureResult.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), captureResult.getTotalAmount());
		Assert.assertNotNull(captureResult.getRequestTime());
		Assert.assertNotNull(captureResult.getRequestId());
		Assert.assertNotNull(captureResult.getRequestToken());
		Assert.assertNotNull(captureResult.getReconciliationId());

		final FollowOnRefundCommand refundCommand = commandFactory.createCommand(FollowOnRefundCommand.class);
		final FollowOnRefundRequest refundRequest = new FollowOnRefundRequest(testMerchantReferenceCode,
				captureResult.getRequestId(), captureResult.getRequestToken(), captureResult.getCurrency(), captureResult
						.getTotalAmount().subtract(new BigDecimal(25)), CYBERSOURCE);
		final RefundResult refundResult = refundCommand.perform(refundRequest);
		Assert.assertNotNull(refundResult);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult.getReconciliationId());
		Assert.assertNotNull(refundResult.getRequestTime());
		Assert.assertEquals(refundResult.getTotalAmount(), captureResult.getTotalAmount().subtract(new BigDecimal(25)));
		Assert.assertNotNull(refundResult.getCurrency());


		final FollowOnRefundRequest refundRequest2 = new FollowOnRefundRequest(testMerchantReferenceCode,
				captureResult.getRequestId(), captureResult.getRequestToken(), captureResult.getCurrency(), new BigDecimal(12),
				CYBERSOURCE);
		final RefundResult refundResult2 = refundCommand.perform(refundRequest2);
		Assert.assertNotNull(refundResult2);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult2.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult2.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult2.getReconciliationId());
		Assert.assertNotNull(refundResult2.getRequestTime());

		Assert.assertEquals(refundResult2.getTotalAmount(), new BigDecimal("12.00"));
		Assert.assertNotNull(refundResult2.getCurrency());

		final FollowOnRefundRequest refundRequest3 = new FollowOnRefundRequest(testMerchantReferenceCode,
				captureResult.getRequestId(), captureResult.getRequestToken(), captureResult.getCurrency(), new BigDecimal(25),
				CYBERSOURCE);
		final RefundResult refundResult3 = refundCommand.perform(refundRequest3);
		Assert.assertNotNull(refundResult3);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult3.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult3.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult3.getReconciliationId());
		Assert.assertNotNull(refundResult3.getRequestTime());
		Assert.assertEquals(refundResult3.getTotalAmount(), new BigDecimal("25.00"));
		Assert.assertNotNull(refundResult3.getCurrency());
	}

	@Test
	public void testStandaloneRefundCommand() throws CommandNotSupportedException
	{
		final double moneyAmount = 161.47;
		final AuthorizationCommand authorizationCommand = commandFactory.createCommand(AuthorizationCommand.class);
		final CaptureCommand captureCommand = commandFactory.createCommand(CaptureCommand.class);

		final String testMerchantReferenceCode = generateMerchantReferenceCode();

		final AuthorizationRequest authorizationRequest = prepareAuthorizationRequest(cardRegistry, testMerchantReferenceCode,
				moneyAmount);

		final AuthorizationResult authorizationResult = authorizationCommand.perform(authorizationRequest);

		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), authorizationResult.getTotalAmount());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertNotNull(authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getRequestToken());

		final CaptureRequest captureRequest = prepareCaptureRequest(authorizationResult.getRequestId(),
				authorizationResult.getRequestToken(), testMerchantReferenceCode, moneyAmount);

		final CaptureResult captureResult = captureCommand.perform(captureRequest);

		Assert.assertEquals(testMerchantReferenceCode, captureResult.getMerchantTransactionCode());
		Assert.assertEquals(TransactionStatus.ACCEPTED, captureResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, captureResult.getTransactionStatusDetails());
		Assert.assertEquals(TEST_CURRENCY, captureResult.getCurrency());
		Assert.assertEquals(BigDecimal.valueOf(moneyAmount), captureResult.getTotalAmount());
		Assert.assertNotNull(captureResult.getRequestTime());
		Assert.assertNotNull(captureResult.getRequestId());
		Assert.assertNotNull(captureResult.getRequestToken());
		Assert.assertNotNull(captureResult.getReconciliationId());

		final StandaloneRefundCommand refundCommand = commandFactory.createCommand(StandaloneRefundCommand.class);
		final StandaloneRefundRequest refundRequest = new StandaloneRefundRequest(testMerchantReferenceCode, createBillingInfo(),
				getCardInfo(createBillingInfo()), captureResult.getCurrency(), captureResult.getTotalAmount());
		final RefundResult refundResult = refundCommand.perform(refundRequest);
		Assert.assertNotNull(refundResult);
		Assert.assertEquals(TransactionStatus.ACCEPTED, refundResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL, refundResult.getTransactionStatusDetails());
		Assert.assertNotNull(refundResult.getReconciliationId());
		Assert.assertNotNull(refundResult.getRequestTime());
		Assert.assertEquals(refundResult.getTotalAmount(), captureResult.getTotalAmount());
		Assert.assertNotNull(refundResult.getCurrency());

	}


	@SuppressWarnings("unused")
	private AuthorizationRequest prepareAuthorizationRequest(final CardRegistry cardRegistry, final String merchantReferenceCode,
			final double moneyAmount)
	{

		final BillingInfo billingInfo = createBillingInfo();
		//billingInfo.setStreet2("");

		final BillingInfo shippingInfo = createShippingInfo();

		final CardInfo card = getCardInfo(billingInfo);

		final AuthorizationRequest request = new AuthorizationRequest(merchantReferenceCode, card, TEST_CURRENCY,
				BigDecimal.valueOf(moneyAmount), shippingInfo);
		return request;
	}

	@SuppressWarnings("unused")
	private SubscriptionAuthorizationRequest prepareAuthorizationRequest(final String merchantReferenceCode,
			final String subscriptionID, final String paymentProvider, final double totalAmount)
	{

		final BillingInfo shippingInfo = createShippingInfo();

		final SubscriptionAuthorizationRequest request = new SubscriptionAuthorizationRequest(merchantReferenceCode,
				subscriptionID, TEST_CURRENCY, BigDecimal.valueOf(totalAmount), shippingInfo);
		return request;
	}

	@SuppressWarnings("unused")
	private CreateSubscriptionRequest prepareCreateSubscriptionRequest(final AuthorizationResult authorizationResult,
			final String merchantReferenceCode, final double moneyAmount)
	{
		final BillingInfo billingInfo = createBillingInfo();
		final BillingInfo shippingInfo = createShippingInfo();
		final CardInfo card = getCardInfo(billingInfo);

		final CreateSubscriptionRequest request = new CreateSubscriptionRequest(merchantReferenceCode, shippingInfo, TEST_CURRENCY,
				card, authorizationResult.getRequestId(), authorizationResult.getRequestToken(),
				authorizationResult.getPaymentProvider());
		return request;
	}

	@SuppressWarnings("unused")
	private SubscriptionAuthorizationRequest prepareSubscriptionAuthorizationRequest(final String subscriptionID,
			final String merchantReferenceCode, final double moneyAmount)
	{
		final BillingInfo shippingInfo = createShippingInfo();

		final SubscriptionAuthorizationRequest request = new SubscriptionAuthorizationRequest(merchantReferenceCode,
				subscriptionID, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount), shippingInfo);
		return request;
	}


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

	private CaptureRequest prepareCaptureRequest(final String requestId, final String requestToken,
			final String merchantReferenceCode, final double moneyAmount)
	{

		return new CaptureRequest(merchantReferenceCode, requestId, requestToken, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount),
				CYBERSOURCE);
	}

	private PartialCaptureRequest preparePartialCaptureRequest(final String requestId, final String requestToken,
			final String merchantReferenceCode, final double moneyAmount, final String partialPaymentID)
	{

		return new PartialCaptureRequest(merchantReferenceCode, requestId, requestToken, TEST_CURRENCY,
				BigDecimal.valueOf(moneyAmount), partialPaymentID, CYBERSOURCE);
	}

	private EnrollmentCheckRequest prepareEnrollmentCheckRequest(final String merchantReferenceCode, final double moneyAmount,
			final String cardNumber)
	{
		final BasicCardInfo cardInfo = new BasicCardInfo(cardNumber, Integer.valueOf(TEST_CC_EXPIRATION_MONTH),
				Integer.valueOf(TEST_CC_EXPIRATION_YEAR));
		return new EnrollmentCheckRequest(merchantReferenceCode, cardInfo, TEST_CURRENCY, BigDecimal.valueOf(moneyAmount),
				HTTP_ACCEPT, HTTP_USER_AGENT);
	}

	private String generateMerchantReferenceCode()
	{
		final String currentTime = String.valueOf(System.currentTimeMillis());

		return "testMerchant" + currentTime.substring(currentTime.length() - 3);
	}

}
