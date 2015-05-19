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
package de.hybris.platform.commerceservices.customer.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.commerceservices.event.ForgottenPwdEvent;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commerceservices.strategies.impl.DefaultCustomerNameStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;
import de.hybris.platform.payment.dto.NewSubscription;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.PasswordEncoderService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



/**
 * JUnit test suite for {@link DefaultCustomerAccountService}
 */
@UnitTest
public class DefaultCustomerAccountServiceTest
{
	private static final String PASSWORD_ENCODING = "md5";
	private static final String TEST_USER_UID = "testUid";
	private static final String TEST_USER_NAME = "testName";
	private static final String TEST_OLD_PASS = "oldPass";
	private static final String TEST_NEW_PASS = "newPass";
	private static final String TEST_TOKEN = "token";
	private static final String TEST_PASS_ENCODING = "complicated";
	private static final long TEST_VALIDITY_INTERVAL = 10L;
	private static final String TEST_DUMMY = "dummy";

	private DefaultCustomerAccountService customerAccountService;

	@Mock
	private PaymentService paymentService;
	@Mock
	private ModelService modelService;
	@Mock
	private I18NService i18nService;
	@Mock
	private UserService userService;
	@Mock
	private PasswordEncoderService passwordEncoderService;
	@Mock
	private SecureTokenService secureTokenService;
	@Mock
	private UserModel user;
	@Mock
	private CustomerModel customer;
	@Mock
	private CustomerModel guestCustomer;
	@Mock
	private EventService eventService;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseSiteService siteService;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private BaseSiteModel site;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private TypeService typeService;
	@Mock
	private CustomerEmailResolutionService customerEmailResolutionService;
	@Mock
	private FlexibleSearchService flexibleSearchService;

	private AddressModel addressModel;

	private CustomerModel customerModel;

	private CustomerNameStrategy customerNameStrategy;

	protected static class MockAddressModel extends AddressModel
	{
		private final long id;

		public MockAddressModel(final long id)
		{
			this.id = id;
		}

		@Override
		public de.hybris.platform.core.PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(id);
		}
	}

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		customerAccountService = new DefaultCustomerAccountService();
		customerAccountService.setPaymentService(paymentService);
		customerAccountService.setModelService(modelService);
		customerAccountService.setFlexibleSearchService(flexibleSearchService);
		customerAccountService.setI18nService(i18nService);
		customerAccountService.setUserService(userService);
		customerAccountService.setPasswordEncoderService(passwordEncoderService);
		customerAccountService.setUserService(userService);
		customerAccountService.setSecureTokenService(secureTokenService);
		customerAccountService.setEventService(eventService);
		customerAccountService.setBaseStoreService(baseStoreService);
		customerAccountService.setBaseSiteService(siteService);
		customerAccountService.setCommonI18NService(commonI18NService);
		customerAccountService.setTypeService(typeService);
		customerAccountService.setCustomerEmailResolutionService(customerEmailResolutionService);
		customerNameStrategy = new DefaultCustomerNameStrategy();
		customerAccountService.setCustomerNameStrategy(customerNameStrategy);
		given(customer.getName()).willReturn(TEST_USER_NAME);
		given(customer.getUid()).willReturn(TEST_USER_UID);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
		given(siteService.getCurrentBaseSite()).willReturn(site);
		given(guestCustomer.getName()).willReturn(TEST_USER_NAME);
		given(guestCustomer.getUid()).willReturn(TEST_USER_UID + "|" + TEST_TOKEN);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddPaymentInfoWhenCustomerIsNull()
	{
		customerAccountService.addPaymentInfo(null, mock(PaymentInfoModel.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddPaymentInfoWhenInfoIsNull()
	{
		customerAccountService.addPaymentInfo(customer, null);
	}


	@Test
	public void testAddPaymentInfo()
	{
		final PaymentInfoModel infoModel = mock(PaymentInfoModel.class);

		given(customer.getPaymentInfos()).willReturn(Collections.EMPTY_LIST);

		customerAccountService.addPaymentInfo(customer, infoModel);

		verify(customer, times(1)).setPaymentInfos(anyCollectionOf(PaymentInfoModel.class));
		verify(modelService, times(1)).saveAll(customer, infoModel);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testAddCreditCardPaymentInfoWhenCurrencyIsNull()
	{
		given(customer.getSessionCurrency()).willReturn(null);
		given(commonI18NService.getCurrentCurrency()).willReturn(null);
		customerAccountService.createPaymentSubscription(customer, mock(CardInfo.class), mock(BillingInfo.class), null, "", false);
	}


	@Test
	public void testAddCreditCardPaymentInfo()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final Currency currency = Currency.getInstance("USD");
		final BillingInfo billingInfo = mock(BillingInfo.class);
		final CardInfo cardInfo = mock(CardInfo.class);
		final NewSubscription subscription = mock(NewSubscription.class);
		final AddressModel address = mock(AddressModel.class);
		final CreditCardPaymentInfoModel ccPaymentInfo = mock(CreditCardPaymentInfoModel.class);
		final CountryModel country = mock(CountryModel.class);
		country.setIsocode("DE");

		given(modelService.create(AddressModel.class)).willReturn(address);
		given(modelService.create(CreditCardPaymentInfoModel.class)).willReturn(ccPaymentInfo);
		given(flexibleSearchService.getModelByExample(country)).willReturn(country);

		given(subscription.getSubscriptionID()).willReturn("dummySubID");
		given(cardInfo.getCardHolderFullName()).willReturn("dummy user");
		given(cardInfo.getExpirationMonth()).willReturn(Integer.valueOf(1));
		given(cardInfo.getExpirationYear()).willReturn(Integer.valueOf(2013));

		given(billingInfo.getFirstName()).willReturn("firstName");
		given(billingInfo.getLastName()).willReturn("lastName");
		given(billingInfo.getStreet1()).willReturn("street1");
		given(billingInfo.getStreet2()).willReturn("street2");
		given(billingInfo.getCity()).willReturn("city");
		given(billingInfo.getPostalCode()).willReturn("postalcode");
		given(billingInfo.getPhoneNumber()).willReturn("1234567890");
		given(billingInfo.getCountry()).willReturn("DE");

		given(customerEmailResolutionService.getEmailForCustomer(customer)).willReturn("a@b.com");

		given(customer.getSessionCurrency()).willReturn(currencyModel);
		given(currencyModel.getIsocode()).willReturn("USD");
		given(i18nService.getBestMatchingJavaCurrency("USD")).willReturn(currency);
		given(
				paymentService.createSubscription(anyString(), anyString(), any(Currency.class), any(AddressModel.class),
						any(CardInfo.class))).willReturn(subscription);

		customerAccountService.createPaymentSubscription(customer, cardInfo, billingInfo, null, "dummyProvider", true);

		verify(paymentService, times(1)).createSubscription(anyString(), anyString(), any(Currency.class), any(AddressModel.class),
				any(CardInfo.class));
		verify(modelService, times(1)).create(CreditCardPaymentInfoModel.class);
		verify(modelService, times(1)).create(AddressModel.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetDefaultPaymentInfoWhenCustomerIsNull()
	{
		customerAccountService.setDefaultPaymentInfo(null, mock(PaymentInfoModel.class));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testSetDefaultPaymentInfoWhenPaymentIsNull()
	{
		customerAccountService.setDefaultPaymentInfo(customer, null);
	}


	@Test
	public void testSetDefaultPaymentInfo()
	{
		final CreditCardPaymentInfoModel infoModel = mock(CreditCardPaymentInfoModel.class);
		given(Boolean.valueOf(infoModel.isSaved())).willReturn(Boolean.TRUE);
		final PaymentInfoModel otherInfoModel = mock(PaymentInfoModel.class);
		given(Boolean.valueOf(otherInfoModel.isSaved())).willReturn(Boolean.TRUE);
		final List<PaymentInfoModel> infoModels = new ArrayList<PaymentInfoModel>();
		infoModels.add(infoModel);
		infoModels.add(otherInfoModel);

		given(customer.getPaymentInfos()).willReturn(infoModels);

		customerAccountService.setDefaultPaymentInfo(customer, infoModel);

		verify(customer, times(1)).setDefaultPaymentInfo(infoModel);
		verify(modelService, times(1)).refresh(customer);
	}


	@Test
	public void testGetTitles()
	{
		final TitleModel titleModel = mock(TitleModel.class);

		given(userService.getAllTitles()).willReturn(Collections.singletonList(titleModel));

		final Collection<TitleModel> titleModels = customerAccountService.getTitles();

		Assert.assertEquals(titleModel, titleModels.iterator().next());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testGetAddressBookEntriesWhenCustomerIsNull()
	{
		customerAccountService.getAddressBookEntries(null);
	}

	@Test
	public void testGetAllAddressEntries()
	{
		final List<AddressModel> addressModels = new ArrayList<AddressModel>();
		final AddressModel addressModel = mock(AddressModel.class);
		addressModels.add(addressModel);

		given(customer.getAddresses()).willReturn(addressModels);

		final List<AddressModel> result = customerAccountService.getAllAddressEntries(customer);

		Assert.assertEquals(addressModel, result.get(0));
	}

	@Test
	public void testGetAddressBookEntries()
	{
		final List<AddressModel> addressModels = new ArrayList<AddressModel>();
		final AddressModel addressModel = mock(AddressModel.class);
		given(addressModel.getVisibleInAddressBook()).willReturn(Boolean.TRUE);
		addressModels.add(addressModel);

		given(customer.getAddresses()).willReturn(addressModels);

		final List<AddressModel> result = customerAccountService.getAddressBookEntries(customer);

		Assert.assertEquals(addressModel, result.get(0));
	}


	@Test(expected = IllegalArgumentException.class)
	public void testSaveAddressEntryWhenCustomerIsNull()
	{
		customerAccountService.saveAddressEntry(null, mock(AddressModel.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSaveAddressEntryWhenAddressIsNull()
	{
		customerAccountService.saveAddressEntry(customer, null);
	}


	@Test
	public void testSaveAddressEntry()
	{
		final List<AddressModel> addressModels = new ArrayList<AddressModel>();
		final AddressModel addressModel = mock(AddressModel.class);
		addressModels.add(addressModel);

		given(customer.getAddresses()).willReturn(addressModels);

		customerAccountService.saveAddressEntry(customer, addressModel);

		verify(modelService).save(addressModel);
		verify(modelService).save(customer);
	}

	@Test
	public void testSaveAddressEntryAlienAddress()
	{
		final List<AddressModel> addressModels = new ArrayList<AddressModel>();
		final AddressModel addressModel = mock(AddressModel.class);
		addressModels.add(addressModel);
		final AddressModel alienAddress = mock(AddressModel.class);

		given(customer.getAddresses()).willReturn(addressModels);

		customerAccountService.saveAddressEntry(customer, alienAddress);

		verify(alienAddress).setOwner(customer);
		verify(modelService).save(customer);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testDeleteAddressEntryWhenAddressIsNull()
	{
		customerAccountService.deleteAddressEntry(customer, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteAddressEntryWhenCustomerIsNull()
	{
		customerAccountService.deleteAddressEntry(null, mock(AddressModel.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteAddressEntryWhenAddressIsAlien()
	{
		final List<AddressModel> addressModels = new ArrayList<AddressModel>();
		final AddressModel addressModel = mock(AddressModel.class);
		addressModels.add(addressModel);
		final AddressModel alienAddress = mock(AddressModel.class);

		given(customer.getAddresses()).willReturn(addressModels);

		customerAccountService.deleteAddressEntry(customer, alienAddress);
	}

	@Test
	public void testDeleteAddressEntryWhenAddress()
	{
		final List<AddressModel> addressModels = new ArrayList<AddressModel>();
		final AddressModel addressModel = mock(AddressModel.class);
		addressModels.add(addressModel);

		given(customer.getAddresses()).willReturn(addressModels);

		customerAccountService.deleteAddressEntry(customer, addressModel);

		verify(modelService).remove(addressModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetDefaultAddressEntryWhenAddressIsNull()
	{
		customerAccountService.setDefaultAddressEntry(customer, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetDefaultAddressEntryWhenCustomerIsNull()
	{
		customerAccountService.setDefaultAddressEntry(null, mock(AddressModel.class));
	}

	@Test
	public void testSetDefaultAddressEntry()
	{
		final List<AddressModel> addressModels = new ArrayList<AddressModel>();
		final AddressModel addressModel = mock(AddressModel.class);
		addressModels.add(addressModel);

		given(customer.getAddresses()).willReturn(addressModels);

		customerAccountService.setDefaultAddressEntry(customer, addressModel);

		verify(customer).setDefaultPaymentAddress(addressModel);
		verify(customer).setDefaultShipmentAddress(addressModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdatePwdInv() throws TokenInvalidatedException
	{
		final SecureToken data = new SecureToken(TEST_USER_UID, 0L);
		given(userService.getUserForUID(TEST_USER_UID)).willReturn(user);
		given(secureTokenService.decryptData(TEST_TOKEN)).willReturn(data);
		customerAccountService.updatePassword(TEST_TOKEN, TEST_NEW_PASS);
	}

	@Test(expected = TokenInvalidatedException.class)
	public void testUpdatePwdInv2() throws TokenInvalidatedException
	{
		final SecureToken data = new SecureToken(TEST_USER_UID, 0L);
		given(userService.getUserForUID(TEST_USER_UID)).willReturn(user);
		given(secureTokenService.decryptData(TEST_TOKEN)).willReturn(data);
		given(userService.getUserForUID(TEST_USER_UID, CustomerModel.class)).willReturn(customer);
		given(customer.getToken()).willReturn(null);
		customerAccountService.updatePassword(TEST_TOKEN, TEST_NEW_PASS);
	}

	@Test
	public void testUpdatePwd() throws TokenInvalidatedException
	{
		final SecureToken data = new SecureToken(TEST_USER_UID, 0L);
		given(userService.getUserForUID(TEST_USER_UID)).willReturn(user);
		given(secureTokenService.decryptData(TEST_TOKEN)).willReturn(data);
		given(userService.getUserForUID(TEST_USER_UID, CustomerModel.class)).willReturn(customer);
		given(customer.getToken()).willReturn(TEST_TOKEN);
		customerAccountService.updatePassword(TEST_TOKEN, TEST_NEW_PASS);
		verify(userService).setPassword(TEST_USER_UID, TEST_NEW_PASS, PASSWORD_ENCODING);
		verify(customer).setToken(null);
		verify(modelService).save(customer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdatePwdParams() throws TokenInvalidatedException
	{
		customerAccountService.updatePassword(" ", " ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdatePwdExpiry() throws TokenInvalidatedException
	{
		final SecureToken data = new SecureToken(TEST_USER_UID, new Date().getTime() - (TEST_VALIDITY_INTERVAL + 1) * 1000);
		customerAccountService.setTokenValiditySeconds(TEST_VALIDITY_INTERVAL);
		given(secureTokenService.decryptData(TEST_TOKEN)).willReturn(data);
		customerAccountService.updatePassword(TEST_TOKEN, TEST_NEW_PASS);
	}

	@Test
	public void testRegister() throws DuplicateUidException
	{
		customerAccountService.register(customer, TEST_NEW_PASS);
		verify(modelService).save(customer);
		verify(eventService).publishEvent(getMatcher(customer, null));
	}

	@Test
	public void testGuestRegister() throws DuplicateUidException
	{
		customerAccountService.registerGuestForAnonymousCheckout(guestCustomer, TEST_NEW_PASS);
		verify(modelService).save(guestCustomer);
		verify(eventService, never()).publishEvent(getMatcher(guestCustomer, null));
	}

	protected AbstractCommerceUserEvent getMatcher(final CustomerModel customer, final String token)
	{
		return Mockito.argThat(new ArgumentMatcher<AbstractCommerceUserEvent>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				boolean result = false;
				if (argument instanceof AbstractCommerceUserEvent)
				{
					final AbstractCommerceUserEvent event = (AbstractCommerceUserEvent) argument;
					result = event.getSite() == site && event.getBaseStore() == baseStore && event.getCustomer() == customer;
				}
				if (argument instanceof ForgottenPwdEvent)
				{
					final ForgottenPwdEvent event = (ForgottenPwdEvent) argument;
					result &= event.getToken().equals(token);
				}
				return result;
			}
		});
	}

	@Test
	public void testForgottenPwd()
	{
		final ArgumentMatcher<SecureToken> matcher = new ArgumentMatcher<SecureToken>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				final SecureToken token = (SecureToken) argument;
				return token.getData().equals(TEST_USER_UID) && token.getTimeStamp() > 0L;
			}
		};
		customerAccountService.setTokenValiditySeconds(1L);
		internalForgottenPwd(matcher);
	}

	@Test
	public void testForgottenPwdNoExpiry()
	{
		final ArgumentMatcher<SecureToken> matcher = new ArgumentMatcher<SecureToken>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				final SecureToken token = (SecureToken) argument;
				return token.getData().equals(TEST_USER_UID) && token.getTimeStamp() == 0L;
			}
		};
		customerAccountService.setTokenValiditySeconds(0L);
		internalForgottenPwd(matcher);
	}

	protected void internalForgottenPwd(final ArgumentMatcher<SecureToken> matcher)
	{
		given(customer.getUid()).willReturn(TEST_USER_UID);
		given(userService.getUserForUID(TEST_USER_UID, CustomerModel.class)).willReturn(customer);
		given(secureTokenService.encryptData(argThat(matcher))).willReturn(TEST_TOKEN);
		customerAccountService.forgottenPassword(customer);
		verify(secureTokenService).encryptData(argThat(matcher));
		verify(eventService).publishEvent(getMatcher(customer, TEST_TOKEN));
		verify(customer).setToken(TEST_TOKEN);
		verify(modelService).save(customer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testForgottenPwdInv()
	{
		customerAccountService.forgottenPassword(null);
	}

	@Test(expected = PasswordMismatchException.class)
	public void testChangePasswordPasswordsDontMatch() throws PasswordMismatchException
	{
		final String encodedPassword = "encodedOne";
		final String differentPassword = "differentOne";

		given(Boolean.valueOf(userService.isAnonymousUser(customer))).willReturn(Boolean.FALSE);
		given(passwordEncoderService.encode(customer, TEST_OLD_PASS, TEST_PASS_ENCODING)).willReturn(encodedPassword);
		given(customer.getEncodedPassword()).willReturn(differentPassword);
		given(customer.getPasswordEncoding()).willReturn(TEST_PASS_ENCODING);

		customerAccountService.changePassword(customer, TEST_OLD_PASS, TEST_NEW_PASS);
	}


	@Test
	public void testChangePasswordAnonymousUser() throws PasswordMismatchException
	{
		given(Boolean.valueOf(userService.isAnonymousUser(customer))).willReturn(Boolean.TRUE);
		customerAccountService.changePassword(customer, TEST_OLD_PASS, TEST_NEW_PASS);
		verify(passwordEncoderService, never()).encode(any(UserModel.class), any(String.class), any(String.class));
	}


	@Test
	public void testChangePassword() throws PasswordMismatchException
	{
		final String encodedPassword = "encodedOne";
		given(Boolean.valueOf(userService.isAnonymousUser(customer))).willReturn(Boolean.FALSE);
		given(passwordEncoderService.encode(customer, TEST_OLD_PASS, TEST_PASS_ENCODING)).willReturn(encodedPassword);
		given(customer.getEncodedPassword()).willReturn(encodedPassword);
		given(customer.getPasswordEncoding()).willReturn(TEST_PASS_ENCODING);
		customerAccountService.changePassword(customer, TEST_OLD_PASS, TEST_NEW_PASS);
		verify(userService).setPassword(customer, TEST_NEW_PASS, TEST_PASS_ENCODING);
	}

	@Test
	public void testFillMissingCustomerInfo() throws DuplicateUidException
	{
		final TitleModel title = new TitleModel();
		title.setCode("rev");
		addressModel = new MockAddressModel(9999L);
		addressModel.setTitle(title);
		addressModel.setFirstname("firstName");
		addressModel.setLastname("lastName");

		customerModel = new CustomerModel();
		customerModel.setUid(TEST_USER_UID + "|" + TEST_DUMMY);
		customerModel.setDefaultPaymentAddress(addressModel);

		customerAccountService.fillValuesForCustomerInfo(customerModel);
		Assert.assertEquals("Customer title was not populated correctly!", title, customerModel.getTitle());
		Assert.assertEquals("Customer name was not populated correctly!", "firstName lastName", customerModel.getName());
	}
}
