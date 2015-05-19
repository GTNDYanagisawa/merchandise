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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AbstractOrderPopulatorTest
{
	private static final String CART_CODE = "cartCode";
	private static final String ISOCODE = "isoCode";

	@Mock
	private ModelService modelService;
	@Mock
	private PromotionsService promotionsService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private AbstractPopulatingConverter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AbstractPopulatingConverter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	@Mock
	private AbstractPopulatingConverter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	@Mock
	private AbstractPopulatingConverter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	@Mock
	private AbstractPopulatingConverter<PromotionResultModel, PromotionResultData> promotionResultConverter;

	private final CartPopulator<CartData> cartPopulator = new CartPopulator<CartData>();

	private CartModel cartModel;

	private CartData cartData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		cartPopulator.setAddressConverter(addressConverter);
		cartPopulator.setCreditCardPaymentInfoConverter(creditCardPaymentInfoConverter);
		cartPopulator.setDeliveryModeConverter(deliveryModeConverter);
		cartPopulator.setOrderEntryConverter(orderEntryConverter);
		cartPopulator.setModelService(modelService);
		cartPopulator.setPriceDataFactory(priceDataFactory);
		cartPopulator.setPromotionResultConverter(promotionResultConverter);
		cartPopulator.setPromotionsService(promotionsService);
		cartPopulator.setZoneDeliveryModeConverter(zoneDeliveryModeConverter);

		cartModel = mock(CartModel.class);
		cartData = new CartData();
	}

	@Test
	public void testAddCommon()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		given(cartModel.getCode()).willReturn(CART_CODE);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		cartPopulator.addCommon(cartModel, cartData);
		Assert.assertEquals(CART_CODE, cartData.getCode());
		Assert.assertEquals(Integer.valueOf(1), cartData.getTotalItems());
	}

	@Test
	public void testAddTotals()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final PriceData priceData = mock(PriceData.class);
		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.2));
		given(cartModel.getDeliveryMode()).willReturn(deliveryMode);
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel)).willReturn(priceData);
		cartPopulator.addTotals(cartModel, cartData);
		Assert.assertEquals(priceData, cartData.getTotalPrice());
		Assert.assertEquals(priceData, cartData.getTotalTax());
		Assert.assertEquals(priceData, cartData.getSubTotal());
		Assert.assertEquals(priceData, cartData.getDeliveryCost());
	}

	@Test
	public void testAddTotalsNoDeliveryMode()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final PriceData priceData = mock(PriceData.class);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.2));
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel)).willReturn(priceData);
		cartPopulator.addTotals(cartModel, cartData);
		Assert.assertEquals(priceData, cartData.getTotalPrice());
		Assert.assertEquals(priceData, cartData.getTotalTax());
		Assert.assertEquals(priceData, cartData.getSubTotal());
		Assert.assertNull(cartData.getDeliveryCost());
	}

	@Test
	public void testAddTotalsNetCart()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final PriceData priceData = mock(PriceData.class);
		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
		given(cartModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(cartModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(cartModel.getSubtotal()).willReturn(Double.valueOf(1.2));
		given(cartModel.getDeliveryMode()).willReturn(deliveryMode);
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(cartModel.getNet()).willReturn(Boolean.TRUE);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(2.5), currencyModel)).willReturn(priceData);
		cartPopulator.addTotals(cartModel, cartData);
		Assert.assertEquals(priceData, cartData.getTotalPrice());
		Assert.assertEquals(priceData, cartData.getTotalTax());
		Assert.assertEquals(priceData, cartData.getSubTotal());
		Assert.assertEquals(priceData, cartData.getDeliveryCost());
		Assert.assertEquals(priceData, cartData.getTotalPriceWithTax());
	}

	@Test
	public void testAddEntries()
	{
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final OrderEntryData entryData = mock(OrderEntryData.class);
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		given(orderEntryConverter.convert(abstractOrderEntryModel)).willReturn(entryData);
		cartPopulator.addEntries(cartModel, cartData);
		Assert.assertEquals(entryData, cartData.getEntries().iterator().next());
	}

	@Test
	public void testAddPromotions()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final SessionContext sessioncontext = mock(SessionContext.class);
		final AbstractOrder abstractOrder = mock(AbstractOrder.class);
		final PromotionResult promotionResult = mock(PromotionResult.class);
		final PromotionOrderResults promotionOrderResults = new PromotionOrderResults(sessioncontext, abstractOrder,
				Collections.singletonList(promotionResult), 2.1);
		final DiscountValue discountValue = mock(DiscountValue.class);
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final PromotionResultModel promotionResultModel = mock(PromotionResultModel.class);
		final List<PromotionResultModel> promotionResultModelList = new ArrayList<PromotionResultModel>();
		promotionResultModelList.add(promotionResultModel);
		final AbstractPromotionModel abstractPromotionModel = mock(AbstractPromotionModel.class);
		final PromotionOrderEntryConsumedModel promotionOrderEntryConsumedModel = mock(PromotionOrderEntryConsumedModel.class);
		final PromotionResultData promotionResultData = mock(PromotionResultData.class);

		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(abstractOrderEntryModel.getDiscountValues()).willReturn(Collections.singletonList(discountValue));
		given(cartModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		given(promotionsService.getPromotionResults(cartModel)).willReturn(promotionOrderResults);
		given(cartModel.getGlobalDiscountValues()).willReturn(Collections.singletonList(discountValue));
		given(Double.valueOf(discountValue.getAppliedValue())).willReturn(Double.valueOf(2.3));
		given(modelService.getAll(Mockito.anyCollection(), Mockito.anyCollection())).willReturn(promotionResultModelList);
		given(promotionResultModel.getPromotion()).willReturn(abstractPromotionModel);
		given(promotionResultModel.getConsumedEntries()).willReturn(Collections.singletonList(promotionOrderEntryConsumedModel));
		given(promotionResultConverter.convert(promotionResultModel)).willReturn(promotionResultData);

		cartPopulator.addPromotions(cartModel, cartData);
		Assert.assertEquals(promotionResultData, cartData.getPotentialOrderPromotions().iterator().next());
		Assert.assertEquals(promotionResultData, cartData.getPotentialProductPromotions().iterator().next());
		Assert.assertEquals(promotionResultData, cartData.getAppliedOrderPromotions().iterator().next());
		Assert.assertEquals(promotionResultData, cartData.getAppliedProductPromotions().iterator().next());
	}

	@Test
	public void testAddPaymentInformation()
	{
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
		final CCPaymentInfoData ccPaymentInfoData = mock(CCPaymentInfoData.class);
		given(cartModel.getPaymentInfo()).willReturn(creditCardPaymentInfoModel);
		given(creditCardPaymentInfoConverter.convert(creditCardPaymentInfoModel)).willReturn(ccPaymentInfoData);
		cartPopulator.addPaymentInformation(cartModel, cartData);
		Assert.assertEquals(ccPaymentInfoData, cartData.getPaymentInfo());
	}

	@Test
	public void testAddDeliveryAddress()
	{
		final AddressModel addressModel = mock(AddressModel.class);
		final AddressData addressData = mock(AddressData.class);
		given(cartModel.getDeliveryAddress()).willReturn(addressModel);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		cartPopulator.addDeliveryAddress(cartModel, cartData);
		Assert.assertEquals(addressData, cartData.getDeliveryAddress());
	}

	@Test
	public void testAddDeliveryMethod()
	{
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		final DeliveryModeData deliveryModeData = mock(DeliveryModeData.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		given(currencyModel.getIsocode()).willReturn(ISOCODE);
		given(cartModel.getDeliveryMode()).willReturn(deliveryModeModel);
		given(deliveryModeConverter.convert(deliveryModeModel)).willReturn(deliveryModeData);
		given(cartModel.getCurrency()).willReturn(currencyModel);
		cartPopulator.addDeliveryMethod(cartModel, cartData);
		Assert.assertEquals(deliveryModeData, cartData.getDeliveryMode());
	}

	@Test
	public void testAddDeliveryMethodZone()
	{
		final ZoneDeliveryModeModel zoneDeliveryModeModel = mock(ZoneDeliveryModeModel.class);
		final ZoneDeliveryModeData zoneDeliveryModeData = mock(ZoneDeliveryModeData.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		given(cartModel.getDeliveryMode()).willReturn(zoneDeliveryModeModel);
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.3));
		given(zoneDeliveryModeConverter.convert(Mockito.any(ZoneDeliveryModeModel.class))).willReturn(zoneDeliveryModeData);
		given(currencyModel.getIsocode()).willReturn(ISOCODE);
		given(cartModel.getCurrency()).willReturn(currencyModel);
		cartPopulator.addDeliveryMethod(cartModel, cartData);
		Assert.assertEquals(zoneDeliveryModeData, cartData.getDeliveryMode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePriceSourceNull()
	{
		cartPopulator.createPrice(null, null);
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePriceCurrencyNull()
	{
		cartPopulator.createPrice(cartModel, null);
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}
}
