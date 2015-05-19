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
package de.hybris.platform.commercefacades.customer.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCustomerNameStrategy;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link DefaultCustomerFacade}
 */
@UnitTest
public class DefaultCustomerFacadeTest
{
	private static final String TEST_DUMMY = "dummy";
	private static final String TEST_OLD_PASS = "oldPass";
	private static final String TEST_NEW_PASS = "newPass";
	private static final String TEST_USER_UID = "testUid";
	private static final String TEST_TOKEN = "token";
	private DefaultCustomerFacade defaultCustomerFacade;


	@Mock
	private UserService userService;
	@Mock
	private UserModel user;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private ModelService mockModelService;
	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AbstractPopulatingConverter<UserModel, CustomerData> customerConverter;
	@Mock
	private AddressReversePopulator addressReversePopulator;
	@Mock
	private AbstractPopulatingConverter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private StoreSessionFacade storeSessionFacade;
	@Mock
	private CartService cartService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private UserFacade userFacade;
	@Mock
	private SessionService sessionService;
	@Mock
	private OrderFacade orderFacade;

	private CustomerModel customerModel;

	private CustomerModel guestCustomerModel;

	private AddressModel addressModel;

	private AddressModel addressModel2;

	private AddressData addressData;

	private CreditCardPaymentInfoModel creditCardPaymentInfoModel;

	private CCPaymentInfoData ccPaymentInfoData;

	private CustomerNameStrategy customerNameStrategy;

	private CurrencyData defaultCurrencyData;

	private LanguageData defaultLanguageData;

	protected static class MockAddressModel extends AddressModel
	{
		private final long id;

		public MockAddressModel(final long id)
		{
			this.id = id;
		}

		@Override
		public PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(id);
		}
	}


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultCustomerFacade = new DefaultCustomerFacade();
		defaultCustomerFacade.setUserService(userService);
		defaultCustomerFacade.setModelService(mockModelService);
		defaultCustomerFacade.setCustomerAccountService(customerAccountService);
		defaultCustomerFacade.setAddressConverter(addressConverter);
		defaultCustomerFacade.setCustomerConverter(customerConverter);
		defaultCustomerFacade.setAddressReversePopulator(addressReversePopulator);
		defaultCustomerFacade.setCreditCardPaymentInfoConverter(creditCardPaymentInfoConverter);
		defaultCustomerFacade.setCommonI18NService(commonI18NService);
		defaultCustomerFacade.setStoreSessionFacade(storeSessionFacade);
		defaultCustomerFacade.setCartService(cartService);
		defaultCustomerFacade.setCommerceCartService(commerceCartService);
		defaultCustomerFacade.setUserFacade(userFacade);
		defaultCustomerFacade.setSessionService(sessionService);
		defaultCustomerFacade.setOrderFacade(orderFacade);

		customerNameStrategy = new DefaultCustomerNameStrategy();

		defaultCustomerFacade.setCustomerNameStrategy(customerNameStrategy);

		addressModel = new MockAddressModel(9999L);
		addressModel2 = new MockAddressModel(8888L);
		addressData = new AddressData();
		addressData.setId("9999");

		customerModel = new CustomerModel();
		customerModel.setDefaultShipmentAddress(addressModel2);

		creditCardPaymentInfoModel = new CreditCardPaymentInfoModel();
		final List<CreditCardPaymentInfoModel> creditCards = new ArrayList<CreditCardPaymentInfoModel>();
		creditCards.add(creditCardPaymentInfoModel);
		ccPaymentInfoData = new CCPaymentInfoData();

		guestCustomerModel = new CustomerModel();
		guestCustomerModel.setUid(TEST_USER_UID + "|" + TEST_DUMMY);
		guestCustomerModel.setDefaultShipmentAddress(addressModel);
		guestCustomerModel.setDefaultPaymentAddress(addressModel2);

		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(creditCardPaymentInfoConverter.convert(creditCardPaymentInfoModel)).willReturn(ccPaymentInfoData);
		given(userService.getCurrentUser()).willReturn(customerModel);
		given(customerAccountService.getAddressForCode(customerModel, "9999")).willReturn(addressModel);
		given(customerAccountService.getCreditCardPaymentInfos(customerModel, true)).willReturn(creditCards);
		given(customerAccountService.getCreditCardPaymentInfoForCode(customerModel, "code")).willReturn(creditCardPaymentInfoModel);
		given(mockModelService.create(CustomerModel.class)).willReturn(new CustomerModel());

		defaultCurrencyData = new CurrencyData();
		defaultCurrencyData.setIsocode("GBP");

		defaultLanguageData = new LanguageData();
		defaultLanguageData.setIsocode("en");

		given(storeSessionFacade.getDefaultCurrency()).willReturn(defaultCurrencyData);
		given(storeSessionFacade.getDefaultLanguage()).willReturn(defaultLanguageData);
	}


	@Test
	public void testGetCurrentUser()
	{
		final CustomerData customerData = mock(CustomerData.class);

		given(userService.getCurrentUser()).willReturn(customerModel);
		given(customerConverter.convert(customerModel)).willReturn(customerData);

		final CustomerData currentUser = defaultCustomerFacade.getCurrentCustomer();

		Assert.assertEquals(customerData, currentUser);
	}


	@Test
	public void testChangePassword() throws PasswordMismatchException
	{
		given(userService.getCurrentUser()).willReturn(user);
		defaultCustomerFacade.changePassword(TEST_OLD_PASS, TEST_NEW_PASS);
		verify(customerAccountService).changePassword(user, TEST_OLD_PASS, TEST_NEW_PASS);
	}


	@Test(expected = de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException.class)
	public void testChangePasswordMismatchException() throws PasswordMismatchException
	{
		given(userService.getCurrentUser()).willReturn(user);
		doThrow(new PasswordMismatchException("")).when(customerAccountService).changePassword(user, TEST_OLD_PASS, TEST_NEW_PASS);
		defaultCustomerFacade.changePassword(TEST_OLD_PASS, TEST_NEW_PASS);
	}



	@Test
	public void testForgottenPwd()
	{
		given(userService.getUserForUID(TEST_USER_UID.toLowerCase(), CustomerModel.class)).willReturn(customerModel);
		defaultCustomerFacade.forgottenPassword(TEST_USER_UID);
		verify(customerAccountService).forgottenPassword(customerModel);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testForgottenPwdInv2()
	{
		given(userService.getUserForUID(TEST_USER_UID.toLowerCase(), CustomerModel.class)).willThrow(
				new UnknownIdentifierException(""));
		defaultCustomerFacade.forgottenPassword(TEST_USER_UID);
	}


	@Test
	public void testRegister() throws DuplicateUidException
	{
		final RegisterData data = new RegisterData();
		data.setFirstName(TEST_DUMMY);
		data.setLastName(TEST_DUMMY);
		data.setLogin(TEST_DUMMY);
		data.setPassword(TEST_DUMMY);
		data.setTitleCode(TEST_DUMMY);
		final CustomerModel model = new CustomerModel();
		given(mockModelService.create(CustomerModel.class)).willReturn(model);
		defaultCustomerFacade.register(data);
		verify(customerAccountService).register(model, TEST_DUMMY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRegisterErr() throws DuplicateUidException
	{
		final RegisterData data = new RegisterData();
		defaultCustomerFacade.register(data);
	}


	@Test
	public void testRegisterGuest() throws DuplicateUidException
	{
		final String email = "test@test.com";
		final CustomerData guestCustomerData = new CustomerData();
		guestCustomerData.setCurrency(defaultCurrencyData);
		guestCustomerData.setLanguage(defaultLanguageData);
		final CustomerModel guestCustomer = new CustomerModel();
		given(mockModelService.create(CustomerModel.class)).willReturn(guestCustomer);
		given(customerConverter.convert(guestCustomer)).willReturn(guestCustomerData);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		defaultCustomerFacade.createGuestUserForAnonymousCheckout(email, "Guest");

		Assert.assertEquals(StringUtils.substringAfter(guestCustomer.getUid(), "|"), email);
	}

	@Test
	public void testCreateCustomerFromGuest() throws DuplicateUidException
	{
		customerModel.setUid(TEST_USER_UID + "|" + TEST_DUMMY);
		defaultCustomerFacade.changeGuestToCustomer(TEST_DUMMY, "10001");
		verify(customerAccountService).convertGuestToCustomer(TEST_DUMMY, "10001");
	}

	@Test
	public void testUpdateProfile() throws DuplicateUidException
	{
		final String titleCode = "titleCode";
		final String firstName = "firstName";
		final String lastName = "lastName";
		final String uid = "email";
		final CustomerData customerData = new CustomerData();
		customerData.setTitleCode(titleCode);
		customerData.setFirstName(firstName);
		customerData.setLastName(lastName);
		customerData.setUid(uid);
		given(userService.getCurrentUser()).willReturn(customerModel);
		final String name = customerNameStrategy.getName(firstName, lastName);
		defaultCustomerFacade.updateProfile(customerData);
		verify(customerAccountService).updateProfile(customerModel, titleCode, name, uid);
	}



	@Test
	public void testUpdatePassword() throws TokenInvalidatedException
	{
		defaultCustomerFacade.updatePassword(TEST_TOKEN, TEST_NEW_PASS);
		verify(customerAccountService).updatePassword(TEST_TOKEN, TEST_NEW_PASS);
	}


	@Test
	public void testLoginSuccess()
	{
		final CustomerData customerData = new CustomerData();
		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode("PLN");
		customerData.setCurrency(currencyData);
		final Collection<CurrencyData> currencies = new ArrayList<CurrencyData>();
		currencies.add(currencyData);

		final LanguageData languageData = new LanguageData();
		languageData.setIsocode("PL");
		customerData.setLanguage(languageData);
		final Collection<LanguageData> languages = new ArrayList<LanguageData>();
		languages.add(languageData);

		given(customerConverter.convert(customerModel)).willReturn(customerData);
		given(storeSessionFacade.getAllCurrencies()).willReturn(currencies);
		given(storeSessionFacade.getAllLanguages()).willReturn(languages);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		defaultCustomerFacade.loginSuccess();

		verify(storeSessionFacade).setCurrentCurrency(currencyData.getIsocode());
		verify(userFacade).syncSessionLanguage();
	}

	@Test
	public void testLoginSuccessNoCurrencyAndLanguage()
	{
		final CustomerData customerData = new CustomerData();
		customerData.setCurrency(null);
		customerData.setLanguage(null);

		given(customerConverter.convert(customerModel)).willReturn(customerData);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		defaultCustomerFacade.loginSuccess();

		verify(storeSessionFacade).setCurrentCurrency(defaultCurrencyData.getIsocode());
		verify(userFacade).syncSessionLanguage();
	}

	@Test
	public void testLoginSuccessNoCart()
	{
		final CustomerData customerData = new CustomerData();
		customerData.setCurrency(null);
		customerData.setLanguage(null);
		given(customerConverter.convert(customerModel)).willReturn(customerData);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.FALSE);
		defaultCustomerFacade.loginSuccess();
		verify(cartService, Mockito.never()).changeCurrentCartUser(customerModel);
	}

	@Test
	public void testLoginSuccessNotSetCurrent()
	{
		final CustomerData customerData = new CustomerData();
		final CurrencyData userCurrencyData = new CurrencyData();
		userCurrencyData.setIsocode("PLN");
		customerData.setCurrency(userCurrencyData);
		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode("DE");
		final Collection<CurrencyData> currencies = new ArrayList<CurrencyData>();
		currencies.add(currencyData);

		final LanguageData userLanguageData = new LanguageData();
		userLanguageData.setIsocode("PL");
		customerData.setLanguage(userLanguageData);
		final LanguageData languageData = new LanguageData();
		languageData.setIsocode("DE");
		final Collection<LanguageData> languages = new ArrayList<LanguageData>();
		languages.add(languageData);

		given(customerConverter.convert(customerModel)).willReturn(customerData);
		given(storeSessionFacade.getAllCurrencies()).willReturn(currencies);
		given(storeSessionFacade.getAllLanguages()).willReturn(languages);
		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		defaultCustomerFacade.loginSuccess();

		verify(storeSessionFacade).setCurrentCurrency(defaultCurrencyData.getIsocode());
		verify(userFacade).syncSessionLanguage();
	}

}
