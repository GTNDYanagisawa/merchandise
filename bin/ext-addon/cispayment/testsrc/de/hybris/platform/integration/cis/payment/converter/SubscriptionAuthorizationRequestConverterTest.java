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
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cis.api.payment.model.CisPaymentAuthorization;

import junit.framework.Assert;


/**
 * @author florent
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionAuthorizationRequestConverterTest
{
	private SubscriptionAuthorizationRequestConverter converter;

	@Mock
	private SubscriptionAuthorizationRequest request;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this.getClass());
		converter = new SubscriptionAuthorizationRequestConverter();
	}

	@Test
	public void shouldConvert()
	{
		BDDMockito.when(request.getTotalAmount()).thenReturn(BigDecimal.TEN);
		BDDMockito.when(request.getMerchantTransactionCode()).thenReturn("1234");
		BDDMockito.when(request.getCurrency()).thenReturn(Currency.getInstance("USD"));
		final CisPaymentAuthorization authorization = converter.convert(request);

		Assert.assertEquals(BigDecimal.TEN, authorization.getAmount());
		Assert.assertEquals("1234", authorization.getClientAuthorizationId());
		Assert.assertEquals("USD", request.getCurrency().getCurrencyCode());

	}
}
