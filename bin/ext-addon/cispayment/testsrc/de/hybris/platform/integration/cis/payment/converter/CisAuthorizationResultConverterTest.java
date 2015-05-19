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
package de.hybris.platform.integration.cis.payment.converter;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.commands.result.AuthorizationResult;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cis.api.model.CisDecision;
import com.hybris.cis.api.payment.model.CisPaymentRequest;
import com.hybris.cis.api.payment.model.CisPaymentTransactionResult;
import com.hybris.commons.client.RestResponse;

import junit.framework.Assert;


/**
 * @author florent
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CisAuthorizationResultConverterTest
{
	/**
	 * 
	 */
	private static final String CLIENT_AUHT_ID = "5678";
	private static final String AUTH_ID = "1234";
	private static final String AUTH_LOCATION = "http://location/of/the/authorization/in/cis";

	private CisAuthorizationResultConverter cisAuthorizationResultConverter;

	@Mock
	private RestResponse<CisPaymentTransactionResult> restResponse;
	@Mock
	private CisPaymentTransactionResult cisPaymentTransactionResult;
	@Mock
	private CisPaymentRequest cisPaymentRequest;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this.getClass());
		cisAuthorizationResultConverter = new CisAuthorizationResultConverter();
	}

	@Test
	public void shouldConvertRestResponse() throws URISyntaxException
	{
		BDDMockito.when(restResponse.getLocation()).thenReturn(new URI(AUTH_LOCATION));
		BDDMockito.when(restResponse.getResult()).thenReturn(cisPaymentTransactionResult);
		BDDMockito.when(cisPaymentTransactionResult.getId()).thenReturn(AUTH_ID);
		BDDMockito.when(cisPaymentTransactionResult.getClientAuthorizationId()).thenReturn(CLIENT_AUHT_ID);
		BDDMockito.when(cisPaymentTransactionResult.getRequest()).thenReturn(cisPaymentRequest);
		BDDMockito.when(cisPaymentTransactionResult.getAmount()).thenReturn(BigDecimal.TEN);
		BDDMockito.when(cisPaymentTransactionResult.getDecision()).thenReturn(CisDecision.ACCEPT);
		BDDMockito.when(cisPaymentRequest.getCurrency()).thenReturn("USD");

		final AuthorizationResult authorizationResult = cisAuthorizationResultConverter.convert(restResponse);

		Assert.assertEquals(AUTH_LOCATION, authorizationResult.getRequestId());
		Assert.assertNotNull(authorizationResult.getAuthorizationTime());
		Assert.assertEquals(AUTH_ID, authorizationResult.getAuthorizationCode());
		Assert.assertNull(authorizationResult.getAvsStatus());
		Assert.assertEquals("USD", authorizationResult.getCurrency().getCurrencyCode());
		Assert.assertNull(authorizationResult.getCvnStatus());
		Assert.assertEquals(CLIENT_AUHT_ID, authorizationResult.getMerchantTransactionCode());
		Assert.assertEquals("cisCybersource", authorizationResult.getPaymentProvider());
		Assert.assertNull(authorizationResult.getReconciliationId());
		Assert.assertNull(authorizationResult.getRequestToken());
		Assert.assertEquals(BigDecimal.TEN, authorizationResult.getTotalAmount());
		Assert.assertEquals(TransactionStatus.ACCEPTED, authorizationResult.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.UNKNOWN_CODE, authorizationResult.getTransactionStatusDetails());

	}
}
