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
package de.hybris.platform.b2bacceleratorfacades.order.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCostCenterData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultB2BCheckoutFacadeTest
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BCheckoutFacadeTest.class); //NOPMD

	private DefaultB2BCheckoutFacade b2BCheckoutFacade;
	@Mock
	private B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> b2bCostCenterService;
	@Mock
	private Converter<B2BCostCenterModel, B2BCostCenterData> b2bCostCenterConverter;
	@Mock
	private B2BOrderService orderService;
	@Mock
	private CartService cartService;
	@Mock
	private GenericDao<AbstractOrderModel> abstractOrderGenericDao;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<AddressModel, AddressData> addressConverter;
	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService; //NOPMD
	@Mock
	private CartFacade mockCartFacade;
	@Mock
	private Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	@Mock
	private DeliveryService deliveryService; //NOPMD
	@Mock
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private UserService userService;
	@Mock
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	@Mock
	private CommerceCheckoutService b2bCommerceCheckoutService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private AddressReversePopulator addressReversePopulator;
	@Mock
	private CustomerAccountService customerAccountService;

	@Mock
	private Converter<OrderModel, OrderData> orderConverter;
	@Mock
	private Converter<CardType, CardTypeData> cardTypeConverter;
	@Mock
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryDataConverter;

	private CartData cartData;

	private CartModel cartModel;

	private AddressData addressData;

	private CustomerModel userModel;

	private AddressModel addressModel;

	private DeliveryModeModel deliveryModeModel; //NOPMD

	private CommerceOrderResult commerceOrderResult;

	private CommerceCheckoutParameter commerceCheckoutParameter;

	private CCPaymentInfoData paymentInfoData;

	private final String COST_CENTER_CODE = "CC1";
	private final String COST_CENTER_NAME = "Cost Center 1";
	private final String ORDER_CODE = "0000000001";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		b2BCheckoutFacade = new DefaultB2BCheckoutFacade();
		b2BCheckoutFacade.setAbstractOrderGenericDao(abstractOrderGenericDao);
		b2BCheckoutFacade.setB2BOrderService(orderService);
		b2BCheckoutFacade.setCartService(cartService);
		b2BCheckoutFacade.setB2bCostCenterConverter(b2bCostCenterConverter);
		b2BCheckoutFacade.setB2bCostCenterService(b2bCostCenterService);
		b2BCheckoutFacade.setModelService(modelService);
		b2BCheckoutFacade.setAddressConverter(addressConverter);
		b2BCheckoutFacade.setPriceDataFactory(priceDataFactory);
		b2BCheckoutFacade.setUserService(userService);
		b2BCheckoutFacade.setEnumerationService(enumerationService);
		b2BCheckoutFacade.setAddressReversePopulator(addressReversePopulator);
		b2BCheckoutFacade.setCustomerAccountService(customerAccountService);
		b2BCheckoutFacade.setModelService(modelService);
		b2BCheckoutFacade.setOrderConverter(orderConverter);
		b2BCheckoutFacade.setCardTypeConverter(cardTypeConverter);
		b2BCheckoutFacade.setCartFacade(mockCartFacade);
		b2BCheckoutFacade.setCommerceCheckoutService(b2bCommerceCheckoutService);
		b2BCheckoutFacade.setCheckoutCustomerStrategy(checkoutCustomerStrategy);

		cartData = new CartData();
		cartModel = new CartModel();
		addressModel = new AddressModel();
		final CountryModel countryModel = new CountryModel();
		final CountryData countryData = new CountryData();
		countryData.setIsocode("PL");

		addressModel.setCountry(countryModel);
		addressData = new AddressData();
		addressData.setId("addressId");
		addressData.setTown("Koluszki");
		addressData.setCountry(countryData);
		final ZoneDeliveryModeModel zoneDeliveryModeModel = new ZoneDeliveryModeModel();
		final ZoneDeliveryModeData zoneDeliveryModeData = new ZoneDeliveryModeData();
		final CreditCardPaymentInfoModel paymentInfoModel = new CreditCardPaymentInfoModel();
		paymentInfoModel.setSubscriptionId("subsId");
		paymentInfoData = new CCPaymentInfoData();
		paymentInfoData.setId("paymentId");
		paymentInfoData.setBillingAddress(addressData);
		paymentInfoData.setExpiryMonth("5");
		paymentInfoData.setExpiryYear("2012");
		paymentInfoData.setIssueNumber("22");

		cartModel.setDeliveryAddress(addressModel);
		cartModel.setDeliveryMode(zoneDeliveryModeModel);
		cartModel.setPaymentInfo(paymentInfoModel);
		cartModel.setB2bcomments(Collections.EMPTY_LIST);
		cartData.setDeliveryAddress(addressData);
		cartData.setDeliveryMode(zoneDeliveryModeData);
		cartData.setPaymentInfo(paymentInfoData);

		final List<CountryModel> deliveryCountries = new ArrayList<CountryModel>();
		deliveryCountries.add(countryModel);

		userModel = new CustomerModel();
		userModel.setDefaultShipmentAddress(addressModel);
		cartModel.setUser(userModel);

		final List<HybrisEnumValue> cardTypes = new ArrayList<HybrisEnumValue>();
		cardTypes.add(CreditCardType.MASTER);

		commerceCheckoutParameter = new CommerceCheckoutParameter();
		commerceCheckoutParameter.setEnableHooks(true);
		commerceOrderResult = new CommerceOrderResult();

		final OrderModel orderModel = new OrderModel();
		commerceOrderResult.setOrder(orderModel);

		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(zoneDeliveryModeConverter.convert(Mockito.any(ZoneDeliveryModeModel.class))).willReturn(zoneDeliveryModeData);
		//given(deliveryService.getZoneDeliveryModeValueForAbstractOrder(zoneDeliveryModeModel, cartModel)).willReturn(null);
		given(creditCardPaymentInfoConverter.convert(paymentInfoModel)).willReturn(paymentInfoData);
		//given(deliveryService.getDeliveryCountries(null)).willReturn(deliveryCountries);
		given(mockCartFacade.getSessionCart()).willReturn(cartData);
		given(Boolean.valueOf(mockCartFacade.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(userService.getCurrentUser()).willReturn(userModel);
		//given(deliveryService.getDeliveryModeForCode(Mockito.anyString())).willReturn(deliveryModeModel);
		given(customerAccountService.getAddressForCode(Mockito.any(CustomerModel.class), Mockito.anyString())).willReturn(
				addressModel);
		given(modelService.create(Mockito.any(Class.class))).willReturn(addressModel);
		given(enumerationService.getEnumerationValues(CreditCardType.class.getSimpleName())).willReturn(cardTypes);
		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(userModel);
		try
		{
			given(b2bCommerceCheckoutService.placeOrder(any(CommerceCheckoutParameter.class))).willReturn(commerceOrderResult);
		}
		catch (final InvalidCartException e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testGetCostCenters() throws Exception
	{
		final B2BCostCenterModel b2BCostCenterModel = mock(B2BCostCenterModel.class);
		given(b2BCostCenterModel.getCode()).willReturn(COST_CENTER_CODE);
		given(b2BCostCenterModel.getName()).willReturn(COST_CENTER_NAME);
		given(b2BCostCenterModel.getActive()).willReturn(Boolean.TRUE);
		final B2BCostCenterData b2bCostCenterData = mock(B2BCostCenterData.class);
		given(b2bCostCenterData.getCode()).willReturn(COST_CENTER_CODE);
		given(b2bCostCenterData.getName()).willReturn(COST_CENTER_NAME);
		given(Boolean.valueOf(b2bCostCenterData.isActive())).willReturn(Boolean.TRUE);
		given(b2bCostCenterService.getAllCostCenters()).willReturn(Collections.singletonList(b2BCostCenterModel));
		given(b2bCostCenterConverter.convert(b2BCostCenterModel)).willReturn(b2bCostCenterData);

		final List<B2BCostCenterData> visibleCostCenters = b2BCheckoutFacade.getVisibleCostCenters();
		Assert.assertNotNull(visibleCostCenters);
		Assert.assertEquals(visibleCostCenters.iterator().next().getCode(), b2BCostCenterModel.getCode());

	}

	@Test
	public void testSetCostCenterForEntry() throws Exception
	{
		final OrderData orderData = mock(OrderData.class);
		given(orderData.getCode()).willReturn(ORDER_CODE);

		final AbstractOrderModel orderModel = mock(OrderModel.class);
		given(orderModel.getCode()).willReturn(ORDER_CODE);

		final AbstractOrderEntryModel orderEntry = mock(OrderEntryModel.class);
		given(orderEntry.getEntryNumber()).willReturn(Integer.valueOf(1));

		final OrderEntryData orderEntryData = mock(OrderEntryData.class);
		given(orderEntryData.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(orderEntryDataConverter.convert(orderEntry)).willReturn(orderEntryData);

		final B2BCostCenterModel b2BCostCenterModel = mock(B2BCostCenterModel.class);
		given(b2BCostCenterModel.getCode()).willReturn(COST_CENTER_CODE);
		given(b2BCostCenterModel.getName()).willReturn(COST_CENTER_NAME);
		given(b2BCostCenterModel.getActive()).willReturn(Boolean.TRUE);

		final B2BCostCenterData b2bCostCenterData = mock(B2BCostCenterData.class);
		given(b2bCostCenterData.getCode()).willReturn(COST_CENTER_CODE);
		given(b2bCostCenterData.getName()).willReturn(COST_CENTER_NAME);
		given(Boolean.valueOf(b2bCostCenterData.isActive())).willReturn(Boolean.TRUE);
		given(orderEntry.getCostCenter()).willReturn(b2BCostCenterModel);
		given(orderModel.getEntries()).willReturn(Collections.singletonList(orderEntry));
		given(abstractOrderGenericDao.find(Mockito.anyMap())).willReturn(Collections.singletonList(orderModel));
		given(orderService.getEntryForNumber((OrderModel) orderModel, 1)).willReturn((OrderEntryModel) orderEntry);
		given(b2bCostCenterConverter.convert(b2BCostCenterModel)).willReturn(b2bCostCenterData);
		given(b2bCostCenterService.getCostCenterForCode(COST_CENTER_CODE)).willReturn(b2BCostCenterModel);

	}

	@Test
	public void testPlaceOrder() throws InvalidCartException
	{

		b2BCheckoutFacade.placeOrder();
		verify(orderConverter).convert(Mockito.any(OrderModel.class));

	}



	@Ignore("has this ever worked ?? commented after commiting ACC-1885 ")
	@Test
	public void testScheduleOrder()
	{
		final TriggerData triggerData = new TriggerData();
		triggerData.setDay(Integer.valueOf(1));
		triggerData.setActivationTime(new Date());
		triggerData.setRelative(Boolean.TRUE);
		Assert.assertEquals(Boolean.TRUE, b2BCheckoutFacade.scheduleOrder(triggerData));

	}

	@Test
	public void testSetPaymentTypeSelectedForCheckout()
	{
		final String paymentType = "account";
		final AbstractOrderModel cartModel = mock(CartModel.class);
		given(cartModel.getCode()).willReturn(ORDER_CODE);
		cartModel.setPaymentType(CheckoutPaymentType.valueOf(paymentType));
		given(cartModel.getPaymentType()).willReturn(CheckoutPaymentType.ACCOUNT);

	}
}