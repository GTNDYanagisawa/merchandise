/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package de.hybris.platform.subscriptionfacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AbstractSubscriptionOrderPopulatorTest
{

	private static final String CART_CODE = "cartCode";

	@Mock
	private ModelService modelService;

	@Mock
	private PriceDataFactory priceDataFactory;

	private AbstractSubscriptionOrderPopulator subsOrderPopulator;

	private CartModel cartModel;

	private BillingTimeModel billingTimeModel;

	private CartData cartData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		subsOrderPopulator = new SubscriptionOrderPopulator();
		subsOrderPopulator.setModelService(modelService);
		subsOrderPopulator.setPriceDataFactory(priceDataFactory);
		cartModel = mock(CartModel.class);
		billingTimeModel = mock(BillingTimeModel.class);
		cartData = new CartData();
	}

	@Test
	public void testAddCommon()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		given(cartModel.getCode()).willReturn(CART_CODE);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		subsOrderPopulator.addCommon(cartModel, cartData);
		Assert.assertEquals(Integer.valueOf(1), cartData.getTotalItems());
	}

	@Test
	public void testBillingFreqContainer()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final PriceData priceData = mock(PriceData.class);
		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
		given(cartModel.getDeliveryMode()).willReturn(deliveryMode);

		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(currencyModel.getIsocode()).willReturn("isoCode");
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel)).willReturn(priceData);

		given(cartModel.getBillingTime()).willReturn(billingTimeModel);
		given(cartModel.getBillingTime().getCode()).willReturn("paynow");

		subsOrderPopulator.addTotals(cartModel, cartData);
		Assert.assertEquals(priceData, cartData.getDeliveryCost());
	}

}
