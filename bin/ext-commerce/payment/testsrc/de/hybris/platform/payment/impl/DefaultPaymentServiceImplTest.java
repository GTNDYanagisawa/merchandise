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
package de.hybris.platform.payment.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.commands.request.AuthorizationRequest;
import de.hybris.platform.payment.commands.request.CaptureRequest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.commands.result.CaptureResult;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.methods.CardPaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.payment.strategy.PaymentInfoCreatorStrategy;
import de.hybris.platform.payment.strategy.TransactionCodeGenerator;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
public class DefaultPaymentServiceImplTest
{
	@InjectMocks
	private final DefaultPaymentServiceImpl defaultPaymentServiceImpl = new DefaultPaymentServiceImpl();


	@Mock
	private CardPaymentService cardPaymentService;
	@Mock
	private CommonI18NService CommonI18nService; //NOPMD
	@Mock
	private ModelService modelService;
	@Mock
	private TransactionCodeGenerator merchantTransactionCode; //NOPMD
	@Mock
	private PaymentInfoCreatorStrategy paymentInfoCreator; //NOPMD
	@Mock
	private FlexibleSearchService flexibleSearchService; //NOPMD




	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void testCodeGeneration()
	{
		final PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
		paymentTransactionModel.setEntries(Collections.EMPTY_LIST);
		Mockito.when(modelService.create(PaymentTransactionModel.class)).thenReturn(paymentTransactionModel);

		final CaptureResult captureResult = new CaptureResult();
		captureResult.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
		captureResult.setTransactionStatus(TransactionStatus.ACCEPTED);
		Mockito.when(cardPaymentService.capture((CaptureRequest) Mockito.anyObject())).thenReturn(captureResult);

		Mockito.when(modelService.create(PaymentTransactionEntryModel.class)).thenReturn(new PaymentTransactionEntryModel());

		final AuthorizationResult result = new AuthorizationResult();
		result.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL);
		result.setTransactionStatus(TransactionStatus.ACCEPTED);
		result.setCurrency(Currency.getInstance("USD"));
		Mockito.when(cardPaymentService.authorize((AuthorizationRequest) Mockito.anyObject())).thenReturn(result);

		final PaymentTransactionEntryModel paymentTransactionEntry = defaultPaymentServiceImpl.authorize("MTC", new BigDecimal(11),
				Currency.getInstance("USD"), new AddressModel(), new AddressModel(), new CardInfo());


		final CurrencyModel curr = new CurrencyModel();
		curr.setIsocode("USD");
		paymentTransactionEntry.setCurrency(curr);
		paymentTransactionModel.setEntries(Arrays.asList(paymentTransactionEntry));

		Assertions.assertThat(paymentTransactionEntry.getCode()).isEqualTo("MTC-AUTHORIZATION-1");

		final PaymentTransactionEntryModel paymentTransaction2 = defaultPaymentServiceImpl.capture(paymentTransactionModel);


		Assertions.assertThat(paymentTransaction2.getCode()).isEqualTo("MTC-CAPTURE-2");
	}
}
