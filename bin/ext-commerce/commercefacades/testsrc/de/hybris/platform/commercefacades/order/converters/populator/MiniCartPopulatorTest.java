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
package de.hybris.platform.commercefacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MiniCartPopulatorTest
{
	private static final String CUR_ISOCODE = "currIsoCode";
	private static final String MODEL_CODE = "code";
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private PriceDataFactory priceDataFactory;

	private final MiniCartPopulator miniCartPopulator = new MiniCartPopulator();
	private AbstractPopulatingConverter<CartModel, CartData> miniCartConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		miniCartPopulator.setCommonI18NService(commonI18NService);
		miniCartPopulator.setPriceDataFactory(priceDataFactory);

		miniCartConverter = new ConverterFactory<CartModel, CartData, MiniCartPopulator>()
				.create(CartData.class, miniCartPopulator);
	}

	@Test
	public void testConvert()
	{
		final CartModel cartModel = mock(CartModel.class);
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		given(cartModel.getCode()).willReturn(MODEL_CODE);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		given(abstractOrderEntryModel.getQuantity()).willReturn(Long.valueOf(12));
		given(cartModel.getNet()).willReturn(Boolean.TRUE);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.4));
		given(cartModel.getDeliveryMode()).willReturn(deliveryModeModel);
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(currencyModel.getIsocode()).willReturn(CUR_ISOCODE);
		final CartData cartData = miniCartConverter.convert(cartModel);
		Assert.assertEquals(MODEL_CODE, cartData.getCode());
		Assert.assertEquals(Integer.valueOf(1), cartData.getTotalItems());
		Assert.assertEquals(Integer.valueOf(12), cartData.getTotalUnitCount());
		verify(cartModel, times(2)).getNet();
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel);
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel);
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.4), currencyModel);
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel);
	}

	@Test
	public void testConvertNoDeliveryMode()
	{
		final CartModel cartModel = mock(CartModel.class);
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		given(cartModel.getCode()).willReturn(MODEL_CODE);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		given(abstractOrderEntryModel.getQuantity()).willReturn(Long.valueOf(12));
		given(cartModel.getNet()).willReturn(Boolean.TRUE);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.4));
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(currencyModel.getIsocode()).willReturn(CUR_ISOCODE);
		final CartData cartData = miniCartConverter.convert(cartModel);
		Assert.assertEquals(MODEL_CODE, cartData.getCode());
		Assert.assertEquals(Integer.valueOf(1), cartData.getTotalItems());
		Assert.assertEquals(Integer.valueOf(12), cartData.getTotalUnitCount());
		verify(cartModel, times(2)).getNet();
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel);
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel);
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.4), currencyModel);
		verify(priceDataFactory, times(0)).create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel);
	}

	@Test
	public void testConvertNullSource()
	{
		final PriceData priceData = mock(PriceData.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		given(commonI18NService.getCurrentCurrency()).willReturn(currencyModel);
		given(currencyModel.getIsocode()).willReturn(CUR_ISOCODE);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.ZERO, currencyModel)).willReturn(priceData);
		final CartData cartData = miniCartConverter.convert(null);
		Assert.assertEquals(priceData, cartData.getTotalPrice());
		Assert.assertNull(cartData.getDeliveryCost());
		Assert.assertEquals(priceData, cartData.getSubTotal());
		Assert.assertEquals(Integer.valueOf(0), cartData.getTotalItems());
		Assert.assertEquals(Integer.valueOf(0), cartData.getTotalUnitCount());
	}
}
