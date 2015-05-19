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
 */
package de.hybris.platform.integration.oms.order.populators;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.externaltax.TaxCodeStrategy;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.integration.commons.services.OndemandPromotionService;
import de.hybris.platform.integration.commons.services.OndemandTaxCalculationService;
import de.hybris.platform.integration.commons.strategies.OndemandDeliveryAddressStrategy;
import de.hybris.platform.integration.oms.order.model.OmsZoneDeliveryModeValueModel;
import de.hybris.platform.integration.oms.order.service.OmsShippingAttributeStrategy;
import de.hybris.platform.omsorders.services.delivery.OmsZoneDeliveryModeValueStrategy;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.oms.domain.address.Address;
import com.hybris.oms.domain.order.Order;
import com.hybris.oms.domain.order.OrderLine;
import com.hybris.oms.domain.order.PaymentInfo;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OmsOrderPopulatorTest
{
	private static final String AUTHORIZATION_LOCATION = "http://authorization/location/uri";
	@Mock
	private OmsOrderPopulator omsOrderPopulator;
	@Mock
	private OmsOrderLinePopulator omsOrderLinePopulator;
	@Mock
	private OmsAddressPopulator omsAddressPopulator;
	@Mock
	private OmsPaymentInfoPopulator omsPaymentInfoPopulator;
	@Mock
	private CustomerNameStrategy customerNameStrategy;
	@Mock
	private CustomerEmailResolutionService customerEmailResolutionService;
	@Mock
	private OndemandDeliveryAddressStrategy ondemandDeliveryAddressStrategy;
	@Mock
	private OmsShippingAttributeStrategy omsShippingAttributeStrategy;
	@Mock
	private OndemandPromotionService ondemandPromotionService;
	@Mock
	private OndemandTaxCalculationService ondemandTaxCalculationService;
	@Mock
	private AbstractPopulatingConverter<OndemandDiscountedOrderEntry, OrderLine> omsOrderLineConverter;
	@Mock
	private AbstractPopulatingConverter<AddressModel, Address> omsAddressConverter;
	@Mock
	private AbstractPopulatingConverter<PaymentInfoModel, PaymentInfo> omsPaymentInfoConverter;
	@Mock
	private ImpersonationService impersonationService;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private WarehouseModel warehouseModel;
	@Mock
	private CustomerModel customerModel;
	@Mock
	private OrderModel orderModel;
	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private DeliveryModeModel deliveryModeModel;
	@Mock
	private PaymentTransactionModel paymentTransactionModel;
	@Mock
	private CreditCardPaymentInfoModel paymentInfoModel;
	@Mock
	private OmsZoneDeliveryModeValueModel omsZoneDeliveryModeValueModel;
	@Mock
	private OmsZoneDeliveryModeValueStrategy omsZoneDeliveryModeValueStrategy;
	@Mock
	private TaxCodeStrategy taxCodeStrategy;
	@Mock
	private AddressModel shippingAddress;

	private final Order omsOrder = new Order();

	@Before
	public void beforeTest() throws Exception
	{
		MockitoAnnotations.initMocks(this.getClass());
		omsOrderPopulator = new OmsOrderPopulator();
		omsOrderLineConverter = new ConverterFactory<OndemandDiscountedOrderEntry, OrderLine, OmsOrderLinePopulator>().create(
				OrderLine.class, omsOrderLinePopulator);

		omsAddressConverter = new ConverterFactory<AddressModel, Address, OmsAddressPopulator>().create(Address.class,
				omsAddressPopulator);
		omsPaymentInfoConverter = new ConverterFactory<PaymentInfoModel, PaymentInfo, OmsPaymentInfoPopulator>().create(
				PaymentInfo.class, omsPaymentInfoPopulator);
		omsOrderPopulator.setOrderLineConverter(omsOrderLineConverter);
		omsOrderPopulator.setAddressConverter(omsAddressConverter);
		omsOrderPopulator.setPaymentInfoConverter(omsPaymentInfoConverter);
		omsOrderPopulator.setCustomerNameStrategy(customerNameStrategy);
		omsOrderPopulator.setCustomerEmailResolutionService(customerEmailResolutionService);
		omsOrderPopulator.setOndemandDeliveryAddressStrategy(ondemandDeliveryAddressStrategy);
		omsOrderPopulator.setOndemandPromotionService(ondemandPromotionService);
		omsOrderPopulator.setOndemandTaxCalculationService(ondemandTaxCalculationService);
		omsOrderPopulator.setImpersonationService(impersonationService);
		omsOrderPopulator.setShippingAttributeStrategy(omsShippingAttributeStrategy);
		omsOrderPopulator.setOmsZoneDeliveryModeValueStrategy(omsZoneDeliveryModeValueStrategy);
		omsOrderPopulator.setTaxCodeStrategy(taxCodeStrategy);

		final TaxValue taxValue = new TaxValue("TEST", 8.75D, true, 10.76D, "USD");
		BDDMockito.when(orderModel.getPaymentInfo()).thenReturn(paymentInfoModel);
		BDDMockito.when(customerNameStrategy.splitName("John Smith")).thenReturn(new String[]
		{ "John", "Smith" });
		BDDMockito.when(paymentTransactionModel.getRequestId()).thenReturn(AUTHORIZATION_LOCATION);
		BDDMockito.when(orderModel.getUser()).thenReturn(customerModel);
		BDDMockito.when(orderModel.getCurrency()).thenReturn(currencyModel);
		BDDMockito.when(orderModel.getPaymentTransactions()).thenReturn(Arrays.asList(paymentTransactionModel));
		BDDMockito.when(orderModel.getDeliveryMode()).thenReturn(deliveryModeModel);
		BDDMockito.when(deliveryModeModel.getCode()).thenReturn("standard");
		BDDMockito.when(currencyModel.getIsocode()).thenReturn("USD");
		BDDMockito.when(customerModel.getName()).thenReturn("John Smith");
		BDDMockito.when(orderModel.getCode()).thenReturn("order1");
		BDDMockito.when(orderModel.getDate()).thenReturn(new Date());
		BDDMockito.when(orderModel.getSubtotal()).thenReturn(Double.valueOf("100"));
		BDDMockito.when(orderModel.getTotalDiscounts()).thenReturn(Double.valueOf("10"));
		BDDMockito.when(orderModel.getTotalPrice()).thenReturn(Double.valueOf("108"));
		BDDMockito.when(orderModel.getTotalTax()).thenReturn(Double.valueOf("18"));
		BDDMockito.when(orderModel.getTotalTaxValues()).thenReturn(Collections.singletonList(taxValue));
		BDDMockito.when(customerEmailResolutionService.getEmailForCustomer(customerModel)).thenReturn("test1@test1.com");
		BDDMockito.when(orderModel.getStore()).thenReturn(baseStoreModel);
		BDDMockito.when(warehouseModel.getCode()).thenReturn("1234");
		BDDMockito.when(baseStoreModel.getWarehouses()).thenReturn(Arrays.asList(warehouseModel));
		BDDMockito.when(baseStoreModel.getUid()).thenReturn("BaseStoreId");
		BDDMockito
				.when(omsOrderPopulator.getOndemandTaxCalculationService().calculatePreciseUnitTax(orderModel.getTotalTaxValues(), 1,
						true)).thenReturn(BigDecimal.valueOf(18D));
		BDDMockito.when(omsOrderPopulator.getOndemandTaxCalculationService().calculateShippingTax(orderModel)).thenReturn(
				BigDecimal.valueOf(18));
		BDDMockito.when(omsZoneDeliveryModeValueStrategy.getZoneDeliveryModeValueForOrder(orderModel, deliveryModeModel))
				.thenReturn(omsZoneDeliveryModeValueModel);
		BDDMockito.when(omsZoneDeliveryModeValueModel.getSpecificCarrierShippingMethod()).thenReturn("02");
		BDDMockito.when(taxCodeStrategy.getTaxCodeForCodeAndOrder(Mockito.anyString(), Mockito.any(AbstractOrderModel.class)))
				.thenReturn("shippingTaxCode");
		BDDMockito.when(orderModel.getDeliveryAddress()).thenReturn(shippingAddress);
		BDDMockito.when(shippingAddress.getFirstname()).thenReturn("shippingFirstName");
		BDDMockito.when(shippingAddress.getLastname()).thenReturn("shippingLastName");


	}

	@Test
	public void testOmsOrderPopulator() throws Exception
	{
		omsOrderPopulator.setUseBaseStoreForSourcing(false);

		omsOrderPopulator.populate(orderModel, omsOrder);

		Assert.assertEquals(omsOrder.getOrderId(), orderModel.getCode());
		Assert.assertEquals(omsOrder.getFirstName(), "John");
		Assert.assertEquals(omsOrder.getLastName(), "Smith");
		Assert.assertEquals(omsOrder.getShippingFirstName(), "shippingFirstName");
		Assert.assertEquals(omsOrder.getShippingLastName(), "shippingLastName");
		Assert.assertEquals(omsOrder.getEmailid(), "test1@test1.com");
		Assert.assertEquals(omsOrder.getPaymentInfos().size(), 1);
		Assert.assertEquals(omsOrder.getPaymentInfos().get(0).getAuthUrl(), AUTHORIZATION_LOCATION);
		Assert.assertEquals(omsOrder.getShippingTaxCategory(), "shippingTaxCode");
		Assert.assertEquals(omsOrder.getLocationIds(), Arrays.asList("1234"));
		Assert.assertEquals(omsOrder.getBaseStoreName(), null);
	}

	@Test
	public void testUseBaseStoreForSourcingFlag() throws Exception
	{
		omsOrderPopulator.setUseBaseStoreForSourcing(true);

		omsOrderPopulator.populate(orderModel, omsOrder);

		Assert.assertEquals(omsOrder.getLocationIds(), null);
		Assert.assertEquals(omsOrder.getBaseStoreName(), "BaseStoreId");
	}
}
