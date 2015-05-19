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
package de.hybris.platform.commercefacades.order.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCardTypeService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCheckoutCustomerStrategy;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeValueModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCheckoutFacadeTest
{
	@Mock
	private AbstractOrderModel order;
	@Mock
	private CartService cartService;
	@Mock
	private CartFacade mockCartFacade;
	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AbstractPopulatingConverter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	@Mock
	private DeliveryService deliveryService;
	@Mock
	private AbstractPopulatingConverter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private UserService userService;
	@Mock
	private CommerceCheckoutService commerceCheckoutService;
	@Mock
	private CommerceCardTypeService commerceCardTypeService;
	@Mock
	private AddressReversePopulator addressReversePopulator;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private ModelService modelService;
	@Mock
	private AbstractPopulatingConverter<OrderModel, OrderData> orderConverter;
	@Mock
	private AbstractPopulatingConverter<CardType, CardTypeData> cardTypeConverter;
	@Mock
	private DefaultCheckoutCustomerStrategy checkoutCustomerStrategy;
	@Mock
	private AbstractPopulatingConverter<CountryModel, CountryData> countryConverter;

	private CommerceCheckoutParameter commerceCheckoutParameter;

	private CommerceOrderResult commerceOrderResult;

	private DefaultCartFacade defaultCartFacade;

	private DefaultCheckoutFacade defaultCheckoutFacade;

	private CartData cartData;

	private CartModel cartModel;

	private AddressData addressData;

	private CustomerModel userModel;

	private AddressModel addressModel;

	private DeliveryModeModel deliveryModeModel;

	private CCPaymentInfoData paymentInfoData;

	protected static class MockAddressModel extends AddressModel
	{
		@Override
		public PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(9999l);
		}
	}

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultCheckoutFacade = new DefaultCheckoutFacade();
		defaultCartFacade = new DefaultCartFacade();
		defaultCheckoutFacade.setCartFacade(mockCartFacade);
		defaultCheckoutFacade.setAddressConverter(addressConverter);
		defaultCheckoutFacade.setZoneDeliveryModeConverter(zoneDeliveryModeConverter);
		defaultCheckoutFacade.setDeliveryService(deliveryService);
		defaultCheckoutFacade.setCreditCardPaymentInfoConverter(creditCardPaymentInfoConverter);
		defaultCheckoutFacade.setCartService(cartService);
		defaultCheckoutFacade.setPriceDataFactory(priceDataFactory);
		defaultCheckoutFacade.setUserService(userService);
		defaultCheckoutFacade.setCommerceCheckoutService(commerceCheckoutService);
		defaultCheckoutFacade.setCommerceCardTypeService(commerceCardTypeService);
		defaultCheckoutFacade.setAddressReversePopulator(addressReversePopulator);
		defaultCheckoutFacade.setCustomerAccountService(customerAccountService);
		defaultCheckoutFacade.setModelService(modelService);
		defaultCheckoutFacade.setOrderConverter(orderConverter);
		defaultCheckoutFacade.setCardTypeConverter(cardTypeConverter);
		defaultCheckoutFacade.setCheckoutCustomerStrategy(checkoutCustomerStrategy);
		defaultCheckoutFacade.setCountryConverter(countryConverter);

		cartData = new CartData();
		cartModel = new CartModel();
		addressModel = new MockAddressModel();
		commerceCheckoutParameter = new CommerceCheckoutParameter();
		commerceCheckoutParameter.setEnableHooks(true);
		commerceOrderResult = new CommerceOrderResult();

		final CountryModel countryModel = new CountryModel();
		final CountryData countryData = new CountryData();
		countryData.setIsocode("PL");

		addressModel.setCountry(countryModel);
		addressData = new AddressData();
		addressData.setId("9999");
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
		cartData.setDeliveryAddress(addressData);
		cartData.setDeliveryMode(zoneDeliveryModeData);
		cartData.setPaymentInfo(paymentInfoData);

		final List<CountryModel> deliveryCountries = new ArrayList<CountryModel>();
		deliveryCountries.add(countryModel);

		userModel = new CustomerModel();
		userModel.setDefaultShipmentAddress(addressModel);
		cartModel.setUser(userModel);

		deliveryModeModel = new DeliveryModeModel();

		final List<CardType> cardTypes = new ArrayList<CardType>();
		final CardType cardType = new CardType(CreditCardType.MASTER.getCode(), CreditCardType.MASTER,
				CreditCardType.MASTER.getCode());
		cardTypes.add(cardType);

		final OrderModel orderModel = new OrderModel();
		commerceOrderResult.setOrder(orderModel);

		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(zoneDeliveryModeConverter.convert(Mockito.any(ZoneDeliveryModeModel.class))).willReturn(zoneDeliveryModeData);
		given(creditCardPaymentInfoConverter.convert(paymentInfoModel)).willReturn(paymentInfoData);
		given(deliveryService.getDeliveryCountriesForOrder(order)).willReturn(deliveryCountries);
		given(mockCartFacade.getSessionCart()).willReturn(cartData);
		given(Boolean.valueOf(mockCartFacade.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(userService.getCurrentUser()).willReturn(userModel);
		given(deliveryService.getDeliveryModeForCode(Mockito.anyString())).willReturn(deliveryModeModel);
		given(customerAccountService.getAddressForCode(Mockito.any(CustomerModel.class), Mockito.anyString())).willReturn(
				addressModel);
		given(modelService.create(Mockito.any(Class.class))).willReturn(addressModel);
		given(commerceCardTypeService.getCardTypes()).willReturn(cardTypes);
		given(commerceCardTypeService.getCardTypeForCode(CreditCardType.MASTER.getCode())).willReturn(cardType);
		given(deliveryService.getSupportedDeliveryAddressesForOrder(Matchers.<AbstractOrderModel> any(), anyBoolean())).willReturn(
				Collections.singletonList(addressModel));
		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(userModel);
		try
		{
			given(commerceCheckoutService.placeOrder(any(CommerceCheckoutParameter.class))).willReturn(commerceOrderResult);
		}
		catch (final InvalidCartException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testHasCheckoutCart()
	{
		defaultCartFacade.setCartService(cartService);
		defaultCheckoutFacade.setCartFacade(defaultCartFacade);
		final boolean hasCart = defaultCheckoutFacade.hasCheckoutCart();
		Assert.assertEquals(true, hasCart);
	}

	@Test
	public void testGetCheckoutCart()
	{
		final CartData checkoutCartData = defaultCheckoutFacade.getCheckoutCart();
		Assert.assertEquals("paymentId", checkoutCartData.getPaymentInfo().getId());
		Assert.assertEquals(addressData, checkoutCartData.getDeliveryAddress());
	}

	@Test
	public void testGetSupportedDeliveryModes()
	{
		final PriceValue priceValue = Mockito.mock(PriceValue.class);
		given(Double.valueOf(priceValue.getValue())).willReturn(Double.valueOf(12.34));
		final ZoneDeliveryModeModel deliveryModeModel = new ZoneDeliveryModeModel();
		final List<DeliveryModeModel> supportedDeliveryModes = new ArrayList<DeliveryModeModel>();
		supportedDeliveryModes.add(deliveryModeModel);
		supportedDeliveryModes.add(deliveryModeModel);
		final ZoneDeliveryModeValueModel zoneDeliveryModeValueModel = new ZoneDeliveryModeValueModel();
		zoneDeliveryModeValueModel.setValue(Double.valueOf(12.34));
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("EUR");
		zoneDeliveryModeValueModel.setCurrency(currencyModel);
		given(deliveryService.getSupportedDeliveryModeListForOrder(Mockito.any(CartModel.class)))
				.willReturn(supportedDeliveryModes);
		given(
				deliveryService.getDeliveryCostForDeliveryModeAndAbstractOrder(Mockito.eq(deliveryModeModel),
						Mockito.any(CartModel.class))).willReturn(priceValue);
		final PriceData priceData = new PriceData();
		priceData.setCurrencyIso("EUR");
		priceData.setValue(BigDecimal.valueOf(12.34));
		given(priceDataFactory.create(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class), Mockito.anyString()))
				.willReturn(priceData);
		final List<? extends DeliveryModeData> listZoneDeliveryModeData = defaultCheckoutFacade.getSupportedDeliveryModes();
		Assert.assertEquals(priceData.getValue(), ((ZoneDeliveryModeData) listZoneDeliveryModeData.iterator().next())
				.getDeliveryCost().getValue());
	}

	@Test
	public void testSetDeliveryAddressIfAvailable()
	{
		final CartModel cartModel = new CartModel();
		cartModel.setDeliveryAddress(null);
		cartModel.setUser(userModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		defaultCheckoutFacade.setDeliveryAddressIfAvailable();

		verify(commerceCheckoutService).setDeliveryAddress(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testNoSetDeliveryAddressIfAvailable()
	{
		final CartModel cartModel = new CartModel();
		cartModel.setDeliveryAddress(addressModel);
		cartModel.setUser(userModel);
		given(cartService.getSessionCart()).willReturn(cartModel);
		defaultCheckoutFacade.setDeliveryAddressIfAvailable();
		verify(commerceCheckoutService, Mockito.never()).setDeliveryAddress(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testSetDeliveryModeIfAvailable()
	{
		final ZoneDeliveryModeModel deliveryModeModel = new ZoneDeliveryModeModel();
		final Collection<DeliveryModeModel> supportedDeliveryModes = new ArrayList<DeliveryModeModel>();
		supportedDeliveryModes.add(deliveryModeModel);
		supportedDeliveryModes.add(deliveryModeModel);
		final ZoneDeliveryModeValueModel zoneDeliveryModeValueModel = new ZoneDeliveryModeValueModel();
		zoneDeliveryModeValueModel.setValue(Double.valueOf(12.34));
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("EUR");
		zoneDeliveryModeValueModel.setCurrency(currencyModel);
		given(deliveryService.getSupportedDeliveryModesForOrder(Mockito.any(CartModel.class))).willReturn(supportedDeliveryModes);
		final PriceData priceData = new PriceData();
		priceData.setCurrencyIso("EUR");
		priceData.setValue(BigDecimal.valueOf(12.34));
		given(priceDataFactory.create(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class), Mockito.anyString()))
				.willReturn(priceData);
		cartModel.setDeliveryMode(null);

		defaultCheckoutFacade.setDeliveryModeIfAvailable();
		//verify(commerceCheckoutService).setDeliveryMode(cartModel, deliveryModeModel);
	}

	@Test
	public void testSetNoDeliveryModeIfAvailable()
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setDeliveryMode(deliveryModeModel);
		defaultCheckoutFacade.setDeliveryModeIfAvailable();
		verify(commerceCheckoutService, Mockito.never()).setDeliveryMode(parameter);
	}

	@Test
	public void testSetNoDeliveryMode()
	{
		given(deliveryService.getDeliveryModeForCode(Mockito.anyString())).willReturn(null);
		defaultCheckoutFacade.setDeliveryMode("test");
		verify(commerceCheckoutService, Mockito.never()).setDeliveryMode(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testGetDeliveryMode()
	{
		defaultCheckoutFacade.getDeliveryMode();
		verify(zoneDeliveryModeConverter).convert(Mockito.any(ZoneDeliveryModeModel.class));
	}

	@Test
	public void testGetNoDeliveryMode()
	{
		cartModel.setDeliveryMode(null);
		given(cartService.getSessionCart()).willReturn(cartModel);
		final DeliveryModeData data = defaultCheckoutFacade.getDeliveryMode();
		verify(zoneDeliveryModeConverter, Mockito.never()).convert(Mockito.any(ZoneDeliveryModeModel.class));
		Assert.assertEquals(null, data);
	}

	@Test
	public void testSetPaymentDetailsIfAvailable()
	{
		final ZoneDeliveryModeModel deliveryModeModel = new ZoneDeliveryModeModel();
		final Collection<DeliveryModeModel> supportedDeliveryModes = new ArrayList<DeliveryModeModel>();
		supportedDeliveryModes.add(deliveryModeModel);
		supportedDeliveryModes.add(deliveryModeModel);
		final ZoneDeliveryModeValueModel zoneDeliveryModeValueModel = new ZoneDeliveryModeValueModel();
		zoneDeliveryModeValueModel.setValue(Double.valueOf(12.34));
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("EUR");
		zoneDeliveryModeValueModel.setCurrency(currencyModel);
		given(deliveryService.getSupportedDeliveryModesForOrder(Mockito.any(CartModel.class))).willReturn(supportedDeliveryModes);
		final PriceData priceData = new PriceData();
		priceData.setCurrencyIso("EUR");
		priceData.setValue(BigDecimal.valueOf(12.34));
		final PaymentInfoModel paymentInfoModel = new PaymentInfoModel();
		final CustomerModel customerModel = new CustomerModel();
		customerModel.setDefaultPaymentInfo(paymentInfoModel);
		given(priceDataFactory.create(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class), Mockito.anyString()))
				.willReturn(priceData);
		given(userService.getCurrentUser()).willReturn(customerModel);
		cartModel.setPaymentInfo(null);
		cartModel.setUser(customerModel);
		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(customerModel);

		defaultCheckoutFacade.setPaymentInfoIfAvailable();
		verify(commerceCheckoutService).setPaymentInfo(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testSetNoPaymentDetailsIfAvailable()
	{
		final ZoneDeliveryModeModel deliveryModeModel = new ZoneDeliveryModeModel();
		final Collection<DeliveryModeModel> supportedDeliveryModes = new ArrayList<DeliveryModeModel>();
		supportedDeliveryModes.add(deliveryModeModel);
		supportedDeliveryModes.add(deliveryModeModel);
		final ZoneDeliveryModeValueModel zoneDeliveryModeValueModel = new ZoneDeliveryModeValueModel();
		zoneDeliveryModeValueModel.setValue(Double.valueOf(12.34));
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("EUR");
		zoneDeliveryModeValueModel.setCurrency(currencyModel);
		given(deliveryService.getSupportedDeliveryModesForOrder(Mockito.any(CartModel.class))).willReturn(supportedDeliveryModes);
		final PriceData priceData = new PriceData();
		priceData.setCurrencyIso("EUR");
		priceData.setValue(BigDecimal.valueOf(12.34));
		given(priceDataFactory.create(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class), Mockito.anyString()))
				.willReturn(priceData);
		defaultCheckoutFacade.setPaymentInfoIfAvailable();
	}

	@Test
	public void testSetDeliveryAddress()
	{
		defaultCheckoutFacade.setDeliveryAddress(addressData);
		verify(commerceCheckoutService).setDeliveryAddress(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testSetNoDeliveryAddress()
	{
		given(deliveryService.getSupportedDeliveryAddressesForOrder(Matchers.<AbstractOrderModel> any(), anyBoolean())).willReturn(
				null);
		defaultCheckoutFacade.setDeliveryAddress(addressData);
		verify(modelService, Mockito.never()).save(any(AddressModel.class));
	}

	@Test
	public void testGetDeliveryCountries()
	{
		final CountryData country = Mockito.mock(CountryData.class);
		final List<CountryData> deliveryCountries = new ArrayList<>();
		deliveryCountries.add(country);
		deliveryCountries.add(country);
		given(mockCartFacade.getDeliveryCountries()).willReturn(deliveryCountries);
		given(country.getName()).willReturn("PL");
		final List<CountryData> results = defaultCheckoutFacade.getDeliveryCountries();
		verify(mockCartFacade).getDeliveryCountries();
		Assert.assertEquals(results.size(), 2);
		Assert.assertEquals(results.get(0).getName(), "PL");
	}

	@Test
	public void testGetCountryForIsocode()
	{
		final CountryModel country = Mockito.mock(CountryModel.class);
		given(deliveryService.getCountryForCode(Mockito.anyString())).willReturn(country);
		given(country.getName()).willReturn("PL");
		defaultCheckoutFacade.getCountryForIsocode("PL");
		verify(countryConverter).convert(Mockito.any(CountryModel.class));
	}

	@Test
	public void testGetNoCountryForIsocode()
	{
		given(deliveryService.getCountryForCode(Mockito.anyString())).willReturn(null);
		final CountryData countryData = defaultCheckoutFacade.getCountryForIsocode("PL");
		Assert.assertEquals(null, countryData);
	}

	@Test
	public void testGetNoPaymentDetails()
	{
		given(cartService.getSessionCart()).willReturn(null);
		final CCPaymentInfoData cCPaymentInfoData = defaultCheckoutFacade.getPaymentDetails();
		Assert.assertNull(cCPaymentInfoData);
		verify(creditCardPaymentInfoConverter, Mockito.never()).convert(Mockito.any(CreditCardPaymentInfoModel.class));
	}

	@Test
	public void testSetPaymentDetails()
	{
		final CreditCardPaymentInfoModel ccPaymentInfoModel = new CreditCardPaymentInfoModel();
		given(customerAccountService.getCreditCardPaymentInfoForCode(Mockito.any(CustomerModel.class), Mockito.anyString()))
				.willReturn(ccPaymentInfoModel);
		defaultCheckoutFacade.setPaymentDetails("paymentId");
		verify(commerceCheckoutService).setPaymentInfo(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testSetNoPaymentDetails()
	{
		defaultCheckoutFacade.setPaymentDetails("paymentId");
		verify(commerceCheckoutService, Mockito.never()).setPaymentInfo(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testGetSupportedCardTypes()
	{
		final CardTypeData cardTypeData = new CardTypeData();
		given(cardTypeConverter.convert(Matchers.<CardType> any())).willReturn(cardTypeData);

		final List<CardTypeData> list = defaultCheckoutFacade.getSupportedCardTypes();
		Assert.assertEquals(1, list.size());
	}

	@Test
	public void testCreatePaymentSubscription()
	{
		final CreditCardPaymentInfoModel ccPaymentInfoModel = new CreditCardPaymentInfoModel();
		given(
				customerAccountService.createPaymentSubscription(Mockito.any(CustomerModel.class), Mockito.any(CardInfo.class),
						Mockito.any(BillingInfo.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean())).willReturn(
				ccPaymentInfoModel);
		defaultCheckoutFacade.createPaymentSubscription(paymentInfoData);
		verify(customerAccountService).createPaymentSubscription(Mockito.any(CustomerModel.class), Mockito.any(CardInfo.class),
				Mockito.any(BillingInfo.class), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
	}

	@Test
	public void testCreatePaymentSubscriptionNoInfoModel()
	{
		final CCPaymentInfoData data = defaultCheckoutFacade.createPaymentSubscription(paymentInfoData);
		Assert.assertNull(data);
	}

	@Test
	public void testAuthorizePayment()
	{
		final String provider = "paymentProvider";
		final PaymentTransactionEntryModel paymentTransactionEntryModel = new PaymentTransactionEntryModel();
		given(commerceCheckoutService.getPaymentProvider()).willReturn(provider);

		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setSecurityCode("1234");
		parameter.setPaymentProvider(provider);

		given(commerceCheckoutService.authorizePayment(any(CommerceCheckoutParameter.class))).willReturn(
				paymentTransactionEntryModel);
		defaultCheckoutFacade.authorizePayment("1234");
		verify(commerceCheckoutService).authorizePayment(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testNoAuthorizePayment()
	{
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setSecurityCode("1234");
		parameter.setPaymentProvider("paymentProvider");
		cartModel.setPaymentInfo(null);
		defaultCheckoutFacade.authorizePayment("1234");
		verify(commerceCheckoutService, Mockito.never()).authorizePayment(parameter);
	}

	@Test
	public void testPlaceOrder() throws InvalidCartException
	{
		defaultCheckoutFacade.placeOrder();
		verify(orderConverter).convert(Mockito.any(OrderModel.class));
	}

	@Test
	public void testPlaceNoOrder() throws InvalidCartException
	{
		final CommerceOrderResult nullOrderResult = Mockito.mock(CommerceOrderResult.class);
		given(nullOrderResult.getOrder()).willReturn(null);
		given(commerceCheckoutService.placeOrder(any(CommerceCheckoutParameter.class))).willReturn(nullOrderResult);
		defaultCheckoutFacade.placeOrder();
		verify(orderConverter, Mockito.never()).convert(Mockito.any(OrderModel.class));
	}

	@Test
	public void testSetDefaultDeliveryAddressForCheckout()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
		given(customerModel.getPk()).willReturn(de.hybris.platform.core.PK.fromLong(12345L));
		given(cart.getUser()).willReturn(customerModel);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cart);
		given(userService.getCurrentUser()).willReturn(customerModel);
		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(customerModel);
		final AddressModel addressModel = Mockito.mock(AddressModel.class);
		given(addressModel.getPk()).willReturn(de.hybris.platform.core.PK.fromLong(9999L));
		given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
		given(Boolean.valueOf(commerceCheckoutService.setDeliveryAddress(any(CommerceCheckoutParameter.class)))).willReturn(
				Boolean.TRUE);
		Assert.assertTrue(defaultCheckoutFacade.setDefaultDeliveryAddressForCheckout());
		verify(commerceCheckoutService).setDeliveryAddress(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testSetDefaultPaymentInfoForCheckout()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
		given(customerModel.getPk()).willReturn(de.hybris.platform.core.PK.fromLong(12345L));
		given(cart.getUser()).willReturn(customerModel);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(cartService.getSessionCart()).willReturn(cart);
		given(userService.getCurrentUser()).willReturn(customerModel);
		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(customerModel);
		final CreditCardPaymentInfoModel creditCardPaymentInfoModel = Mockito.mock(CreditCardPaymentInfoModel.class);
		given(creditCardPaymentInfoModel.getPk()).willReturn(de.hybris.platform.core.PK.fromLong(9999L));
		given(customerModel.getDefaultPaymentInfo()).willReturn(creditCardPaymentInfoModel);
		given(Boolean.valueOf(commerceCheckoutService.setPaymentInfo(any(CommerceCheckoutParameter.class)))).willReturn(
				Boolean.TRUE);
		given(Boolean.valueOf(creditCardPaymentInfoModel.isSaved())).willReturn(Boolean.TRUE);
		Assert.assertTrue(defaultCheckoutFacade.setDefaultPaymentInfoForCheckout());
		verify(commerceCheckoutService).setPaymentInfo(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void testSetCheapestDeliveryModeForCheckout()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		given(cartService.getSessionCart()).willReturn(cart);
		final List<DeliveryModeModel> deliveryModeModelList = new ArrayList<DeliveryModeModel>();

		final PriceValue priceValue1 = Mockito.mock(PriceValue.class);
		given(Double.valueOf(priceValue1.getValue())).willReturn(Double.valueOf(8.99));
		final ZoneDeliveryModeModel zoneDeliveryModeModel1 = Mockito.mock(ZoneDeliveryModeModel.class);
		final ZoneDeliveryModeValueModel zoneDeliveryModeValueModel1 = Mockito.mock(ZoneDeliveryModeValueModel.class);
		given(zoneDeliveryModeValueModel1.getValue()).willReturn(new Double("8.99"));
		final CurrencyModel currencyModel = Mockito.mock(CurrencyModel.class);
		given(currencyModel.getIsocode()).willReturn("EUR");
		given(zoneDeliveryModeValueModel1.getCurrency()).willReturn(currencyModel);
		final Set<ZoneDeliveryModeValueModel> valueList1 = new HashSet<ZoneDeliveryModeValueModel>();
		valueList1.add(zoneDeliveryModeValueModel1);
		given(zoneDeliveryModeModel1.getCode()).willReturn("standard");
		given(zoneDeliveryModeModel1.getValues()).willReturn(valueList1);
		deliveryModeModelList.add(zoneDeliveryModeModel1);
		final PriceData priceData1 = new PriceData();
		priceData1.setCurrencyIso("EUR");
		priceData1.setValue(BigDecimal.valueOf(8.99));
		final ZoneDeliveryModeData zoneDeliveryModeData1 = new ZoneDeliveryModeData();
		zoneDeliveryModeData1.setCode("standard");
		given(zoneDeliveryModeConverter.convert(zoneDeliveryModeModel1)).willReturn(zoneDeliveryModeData1);
		given(priceDataFactory.create(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class), Mockito.anyString()))
				.willReturn(priceData1);
		given(deliveryService.getDeliveryCostForDeliveryModeAndAbstractOrder(zoneDeliveryModeModel1, cart)).willReturn(priceValue1);

		final ZoneDeliveryModeModel zoneDeliveryModeModel2 = Mockito.mock(ZoneDeliveryModeModel.class);
		final PriceValue priceValue2 = Mockito.mock(PriceValue.class);
		given(Double.valueOf(priceValue2.getValue())).willReturn(Double.valueOf(11.99));
		final ZoneDeliveryModeValueModel zoneDeliveryModeValueModel2 = Mockito.mock(ZoneDeliveryModeValueModel.class);
		given(zoneDeliveryModeValueModel2.getValue()).willReturn(new Double("11.99"));
		given(zoneDeliveryModeValueModel2.getCurrency()).willReturn(currencyModel);
		final Set<ZoneDeliveryModeValueModel> valueList2 = new HashSet<ZoneDeliveryModeValueModel>();
		valueList1.add(zoneDeliveryModeValueModel2);
		given(zoneDeliveryModeModel2.getCode()).willReturn("premium");
		given(zoneDeliveryModeModel2.getValues()).willReturn(valueList2);
		deliveryModeModelList.add(zoneDeliveryModeModel2);
		final PriceData priceData2 = new PriceData();
		priceData2.setCurrencyIso("EUR");
		priceData2.setValue(BigDecimal.valueOf(11.99));
		final ZoneDeliveryModeData zoneDeliveryModeData2 = new ZoneDeliveryModeData();
		zoneDeliveryModeData2.setCode("premium");
		given(zoneDeliveryModeConverter.convert(zoneDeliveryModeModel2)).willReturn(zoneDeliveryModeData2);
		given(priceDataFactory.create(Mockito.any(PriceDataType.class), Mockito.any(BigDecimal.class), Mockito.anyString()))
				.willReturn(priceData2);
		given(deliveryService.getDeliveryCostForDeliveryModeAndAbstractOrder(zoneDeliveryModeModel2, cart)).willReturn(priceValue2);
		given(deliveryService.getSupportedDeliveryModeListForOrder(cart)).willReturn(deliveryModeModelList);
		given(deliveryService.getDeliveryModeForCode("standard")).willReturn(zoneDeliveryModeModel1);


		given(Boolean.valueOf(commerceCheckoutService.setDeliveryMode(any(CommerceCheckoutParameter.class)))).willReturn(
				Boolean.TRUE);
		Assert.assertTrue(defaultCheckoutFacade.setCheapestDeliveryModeForCheckout());
		verify(commerceCheckoutService).setDeliveryMode(any(CommerceCheckoutParameter.class));
	}

	@Test
	public void shouldNothaveShippingItemsNullCart()
	{
		given(defaultCheckoutFacade.getCart()).willReturn(null);
		Assert.assertFalse(defaultCheckoutFacade.hasShippingItems());
	}

	@Test
	public void shouldHaveShippingItems()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		final AbstractOrderEntryModel orderEntry = Mockito.mock(AbstractOrderEntryModel.class);
		given(cart.getEntries()).willReturn(Collections.singletonList(orderEntry));
		given(defaultCheckoutFacade.getCart()).willReturn(cart);
		Assert.assertTrue(defaultCheckoutFacade.hasShippingItems());
	}

	@Test
	public void shouldNotHaveShippingItems()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		final AbstractOrderEntryModel orderEntry = Mockito.mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel pos = Mockito.mock(PointOfServiceModel.class);
		given(orderEntry.getDeliveryPointOfService()).willReturn(pos);
		given(cart.getEntries()).willReturn(Collections.singletonList(orderEntry));
		given(defaultCheckoutFacade.getCart()).willReturn(cart);
		Assert.assertFalse(defaultCheckoutFacade.hasShippingItems());
	}

	@Test
	public void shouldNotHavePickupItemsNullCart()
	{
		given(defaultCheckoutFacade.getCart()).willReturn(null);
		Assert.assertFalse(defaultCheckoutFacade.hasPickUpItems());
	}

	@Test
	public void shouldNotHavePickupItems()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		final AbstractOrderEntryModel orderEntry = Mockito.mock(AbstractOrderEntryModel.class);
		given(cart.getEntries()).willReturn(Collections.singletonList(orderEntry));
		given(defaultCheckoutFacade.getCart()).willReturn(cart);
		Assert.assertFalse(defaultCheckoutFacade.hasPickUpItems());
	}

	@Test
	public void shouldHavePickupItems()
	{
		final CartModel cart = Mockito.mock(CartModel.class);
		final AbstractOrderEntryModel orderEntry = Mockito.mock(AbstractOrderEntryModel.class);
		final PointOfServiceModel pos = Mockito.mock(PointOfServiceModel.class);
		given(orderEntry.getDeliveryPointOfService()).willReturn(pos);
		given(cart.getEntries()).willReturn(Collections.singletonList(orderEntry));
		given(defaultCheckoutFacade.getCart()).willReturn(cart);
		Assert.assertTrue(defaultCheckoutFacade.hasPickUpItems());
	}

}
