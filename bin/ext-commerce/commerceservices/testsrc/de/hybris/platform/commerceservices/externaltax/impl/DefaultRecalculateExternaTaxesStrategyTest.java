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
package de.hybris.platform.commerceservices.externaltax.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.externaltax.RecalculateExternalTaxesStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartHashCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceOrderParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultRecalculateExternaTaxesStrategyTest
{
	private DefaultRecalculateExternalTaxesStrategy recalculateExternalTaxesStrategy;

	private CartModel cart;
	@Mock
	private ModelService modelService;

	@Mock
	private SessionService sessionService;

	@Mock
	private CommerceCartHashCalculationStrategy commerceCartHashCalculationStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		cart = new CartModel();
		given(modelService.getModelType(cart)).willReturn("Cart");
		given(commerceCartHashCalculationStrategy.buildHashForAbstractOrder(Mockito.any(AbstractOrderModel.class), Mockito.any(List.class)))
				.willReturn("hash");
		recalculateExternalTaxesStrategy = new DefaultRecalculateExternalTaxesStrategy();
		recalculateExternalTaxesStrategy.setModelService(modelService);
		recalculateExternalTaxesStrategy.setCommerceCartHashCalculationStrategy(commerceCartHashCalculationStrategy);
		recalculateExternalTaxesStrategy.setSessionService(sessionService);
	}

	@Test
	public void testShouldNotRecalculate()
	{
		given(commerceCartHashCalculationStrategy.buildHashForAbstractOrder(Mockito.any(CommerceOrderParameter.class)))
				.willReturn("aHash");
		given(sessionService.getAttribute(RecalculateExternalTaxesStrategy.SESSION_ATTIR_ORDER_RECALCULATION_HASH)).willReturn(
				"aHash");
		recalculateExternalTaxesStrategy.recalculate(cart);
		Assert.assertEquals(false, recalculateExternalTaxesStrategy.recalculate(cart));
	}

	@Test
	public void testShouldRecalculateEmptyHash()
	{
		Assert.assertEquals(true, recalculateExternalTaxesStrategy.recalculate(cart));
	}

	@Test
	public void testShouldRecalculateAttributeChanged()
	{
		recalculateExternalTaxesStrategy.recalculate(cart);
		given(commerceCartHashCalculationStrategy.buildHashForAbstractOrder(Mockito.any(AbstractOrderModel.class), Mockito.any(List.class)))
				.willReturn("changedhash");
		Assert.assertEquals(true, recalculateExternalTaxesStrategy.recalculate(cart));
	}
}
