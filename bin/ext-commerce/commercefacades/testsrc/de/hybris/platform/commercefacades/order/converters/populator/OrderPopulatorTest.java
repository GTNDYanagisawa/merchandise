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
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class OrderPopulatorTest
{
	@Mock
	private ModelService modelService;
	@Mock
	private PromotionsService promotionsService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private TypeService typeService;
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
	@Mock
	private AbstractPopulatingConverter<ConsignmentModel, ConsignmentData> consignmentConverter;
	@Mock
	private AbstractPopulatingConverter<PrincipalModel, PrincipalData> principalConverter;


	private final OrderPopulator orderPopulator = new OrderPopulator();
	private AbstractPopulatingConverter<OrderModel, OrderData> orderConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		orderPopulator.setAddressConverter(addressConverter);
		orderPopulator.setCreditCardPaymentInfoConverter(creditCardPaymentInfoConverter);
		orderPopulator.setDeliveryModeConverter(deliveryModeConverter);
		orderPopulator.setOrderEntryConverter(orderEntryConverter);
		orderPopulator.setModelService(modelService);
		orderPopulator.setPriceDataFactory(priceDataFactory);
		orderPopulator.setPromotionResultConverter(promotionResultConverter);
		orderPopulator.setPromotionsService(promotionsService);
		orderPopulator.setZoneDeliveryModeConverter(zoneDeliveryModeConverter);
		orderPopulator.setTypeService(typeService);
		orderPopulator.setPrincipalConverter(principalConverter);

		orderConverter = new ConverterFactory<OrderModel, OrderData, OrderPopulator>().create(OrderData.class, orderPopulator);
	}

	@Test
	public void testConvert()
	{
		final OrderModel orderModel = mock(OrderModel.class);
		final AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		final Date date = mock(Date.class);
		final OrderStatus orderStatus = mock(OrderStatus.class);
		final ConsignmentModel consignmentModel = mock(ConsignmentModel.class);
		final ConsignmentData consignmentData = mock(ConsignmentData.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final OrderEntryData entryData = mock(OrderEntryData.class);
		final AddressModel addressModel = mock(AddressModel.class);
		final AddressData addressData = mock(AddressData.class);
		final DeliveryModeModel deliveryModeModel = mock(DeliveryModeModel.class);
		final DeliveryModeData deliveryModeData = mock(DeliveryModeData.class);
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
		final CCPaymentInfoData ccPaymentInfoData = mock(CCPaymentInfoData.class);
		final CustomerModel customerModel = mock(CustomerModel.class);
		final CustomerData customerData = mock(CustomerData.class);
		given(orderModel.getDeliveryAddress()).willReturn(addressModel);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(consignmentConverter.convert(consignmentModel)).willReturn(consignmentData);
		given(orderModel.getEntries()).willReturn(Collections.singletonList(abstractOrderEntryModel));
		given(orderEntryConverter.convert(abstractOrderEntryModel)).willReturn(entryData);
		given(orderModel.getDate()).willReturn(date);
		given(orderModel.getStatus()).willReturn(orderStatus);
		given(orderModel.getCurrency()).willReturn(currencyModel);
		given(orderModel.getNet()).willReturn(Boolean.TRUE);
		given(orderModel.getTotalPrice()).willReturn(Double.valueOf(1.2));
		given(orderModel.getTotalTax()).willReturn(Double.valueOf(1.3));
		given(orderModel.getSubtotal()).willReturn(Double.valueOf(1.4));
		given(orderModel.getDeliveryCost()).willReturn(Double.valueOf(3.4));
		given(orderModel.getUser()).willReturn(customerModel);
		given(currencyModel.getIsocode()).willReturn("isocode");
		given(promotionsService.getPromotionResults(orderModel)).willReturn(null);
		given(orderModel.getDeliveryMode()).willReturn(deliveryModeModel);
		given(deliveryModeConverter.convert(deliveryModeModel)).willReturn(deliveryModeData);
		given(orderModel.getPaymentInfo()).willReturn(creditCardPaymentInfoModel);
		given(creditCardPaymentInfoConverter.convert(creditCardPaymentInfoModel)).willReturn(ccPaymentInfoData);
		given(principalConverter.convert(customerModel)).willReturn(customerData);

		final OrderData orderData = orderConverter.convert(orderModel);
		verify(orderModel).getCode();
		verify(orderModel).getDate();
		Assert.assertEquals(date, orderData.getCreated());
		verify(orderModel).getStatus();
		Assert.assertEquals(orderStatus, orderData.getStatus());
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.2), currencyModel);
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.3), currencyModel);
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(1.4), currencyModel);
		verify(priceDataFactory).create(PriceDataType.BUY, BigDecimal.valueOf(3.4), currencyModel);
		Assert.assertEquals(entryData, orderData.getEntries().iterator().next());
		verify(promotionsService).getPromotionResults(orderModel);
		Assert.assertEquals(addressData, orderData.getDeliveryAddress());
		Assert.assertEquals(deliveryModeData, orderData.getDeliveryMode());
		Assert.assertEquals(ccPaymentInfoData, orderData.getPaymentInfo());
		Assert.assertNotNull(orderData.getUser());
		Assert.assertEquals(customerData, orderData.getUser());
	}
}
