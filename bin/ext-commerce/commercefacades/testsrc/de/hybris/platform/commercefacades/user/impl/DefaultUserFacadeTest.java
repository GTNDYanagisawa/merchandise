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
package de.hybris.platform.commercefacades.user.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.converters.populator.TitlePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Test suit for {@link DefaultUserFacade}
 */
@UnitTest
public class DefaultUserFacadeTest
{
	private static final String ADDRESS_ID = "ADDRESS_ID";

	private DefaultUserFacade defaultUserFacade;


	@Mock
	private UserService userService;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private ModelService mockModelService;
	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AddressReversePopulator addressReversePopulator;
	@Mock
	private AbstractPopulatingConverter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	@Mock
	private StoreSessionFacade storeSessionFacade;
	@Mock
	private CartService cartService;
	@Mock
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	private CustomerModel customerModel;

	private AddressModel addressModel;

	private AddressModel addressModel2;

	private AddressData addressData;

	private CreditCardPaymentInfoModel creditCardPaymentInfoModel;

	private CCPaymentInfoData ccPaymentInfoData;

	private CurrencyData defaultCurrencyData;

	private LanguageData defaultLanguageData;

	protected static class MockAddressModel extends AddressModel
	{
		private final long id;
		private CountryModel country;

		public MockAddressModel(final long id)
		{
			this.id = id;
		}

		@Override
		public PK getPk()
		{
			return de.hybris.platform.core.PK.fromLong(id);
		}

		@Override
		public CountryModel getCountry()
		{
			return country;
		}

		@Override
		public void setCountry(final CountryModel value)
		{
			country = value;
		}
	}

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		defaultUserFacade = new DefaultUserFacade();
		defaultUserFacade.setUserService(userService);
		defaultUserFacade.setModelService(mockModelService);
		defaultUserFacade.setCustomerAccountService(customerAccountService);
		defaultUserFacade.setAddressConverter(addressConverter);
		defaultUserFacade.setAddressReversePopulator(addressReversePopulator);
		defaultUserFacade.setCreditCardPaymentInfoConverter(creditCardPaymentInfoConverter);
		defaultUserFacade.setCommerceCommonI18NService(commerceCommonI18NService);
		defaultUserFacade.setCommonI18NService(commonI18NService);
		defaultUserFacade.setCartService(cartService);
		defaultUserFacade.setCheckoutCustomerStrategy(checkoutCustomerStrategy);

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

		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(creditCardPaymentInfoConverter.convert(creditCardPaymentInfoModel)).willReturn(ccPaymentInfoData);
		given(userService.getCurrentUser()).willReturn(customerModel);
		given(customerAccountService.getAddressForCode(customerModel, "9999")).willReturn(addressModel);
		given(customerAccountService.getCreditCardPaymentInfos(customerModel, true)).willReturn(creditCards);
		given(customerAccountService.getCreditCardPaymentInfoForCode(customerModel, "code")).willReturn(creditCardPaymentInfoModel);
		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(customerModel);

		defaultCurrencyData = new CurrencyData();
		defaultCurrencyData.setIsocode("GBP");

		defaultLanguageData = new LanguageData();
		defaultLanguageData.setIsocode("en");

		given(storeSessionFacade.getDefaultCurrency()).willReturn(defaultCurrencyData);
		given(storeSessionFacade.getDefaultLanguage()).willReturn(defaultLanguageData);
	}



	@Test
	public void testGetTitles()
	{
		final AbstractPopulatingConverter<TitleModel, TitleData> converter = new ConverterFactory<TitleModel, TitleData, TitlePopulator>()
				.create(TitleData.class, new TitlePopulator());
		defaultUserFacade.setTitleConverter(converter);
		final Collection result = defaultUserFacade.getTitles();
		verify(customerAccountService).getTitles();
		Assert.assertEquals(Collections.EMPTY_LIST, result);
	}


	@Test
	public void getAllAddresses()
	{
		final List<AddressModel> addressesList = new ArrayList<AddressModel>();
		given(customerAccountService.getAddressBookEntries(customerModel)).willReturn(addressesList);
		Assert.assertTrue(defaultUserFacade.isAddressBookEmpty());
	}

	@Test
	public void testGetDeliveryAddress()
	{
		final CountryModel countryModel = new CountryModel();
		final List<CountryModel> deliveryCountries = new ArrayList<CountryModel>();
		deliveryCountries.add(countryModel);
		final List<AddressModel> addressesList = new ArrayList<AddressModel>();
		addressModel.setCountry(countryModel);
		addressModel.setShippingAddress(Boolean.TRUE);
		addressesList.add(addressModel);
		customerModel.setDefaultShipmentAddress(addressModel);

		given(customerAccountService.getAddressBookDeliveryEntries(customerModel)).willReturn(addressesList);
		given(commerceCommonI18NService.getAllCountries()).willReturn(deliveryCountries);

		final List<AddressData> list = defaultUserFacade.getAddressBook();
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
	}

	@Test
	public void testGetDeliveryAddressEmpty()
	{
		given(customerAccountService.getAddressBookEntries(customerModel)).willReturn(null);
		final List<AddressData> list = defaultUserFacade.getAddressBook();
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void testGetDeliveryAddressContainingInvalidAddress()
	{
		final CountryModel countryModel = new CountryModel();
		final List<CountryModel> deliveryCountries = new ArrayList<CountryModel>();
		deliveryCountries.add(countryModel);
		final List<AddressModel> addressesList = new ArrayList<AddressModel>();
		addressModel.setCountry(countryModel);
		addressModel.setShippingAddress(Boolean.TRUE);
		addressesList.add(addressModel);
		customerModel.setDefaultShipmentAddress(addressModel);

		final AddressModel invalidAddress = mock(AddressModel.class);
		final CountryModel invalidCountry = mock(CountryModel.class);
		invalidAddress.setShippingAddress(Boolean.TRUE);
		invalidAddress.setCountry(invalidCountry);
		addressesList.add(invalidAddress);

		given(customerAccountService.getAddressBookDeliveryEntries(customerModel)).willReturn(addressesList);
		given(commerceCommonI18NService.getAllCountries()).willReturn(deliveryCountries);

		final List<AddressData> list = defaultUserFacade.getAddressBook();
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
	}

	@Test
	public void testGetDeliveryAddressNotValidForSite()
	{
		final CountryModel countryModel = new CountryModel();
		final List<CountryModel> deliveryCountries = new ArrayList<CountryModel>();
		deliveryCountries.add(countryModel);
		final List<AddressModel> addressesList = new ArrayList<AddressModel>();
		addressModel.setCountry(countryModel);
		addressModel.setShippingAddress(Boolean.TRUE);
		addressesList.add(addressModel);
		customerModel.setDefaultShipmentAddress(addressModel);

		given(customerAccountService.getAddressBookEntries(customerModel)).willReturn(addressesList);
		given(commerceCommonI18NService.getAllCountries()).willReturn(deliveryCountries);

		final List<AddressData> list = defaultUserFacade.getAddressBook();
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void testGetDeliveryAddressNoDefault()
	{
		final CountryModel countryModel = new CountryModel();
		final List<CountryModel> deliveryCountries = new ArrayList<CountryModel>();
		deliveryCountries.add(countryModel);
		final List<AddressModel> addressesList = new ArrayList<AddressModel>();
		addressModel.setCountry(countryModel);
		addressModel.setShippingAddress(Boolean.TRUE);
		addressesList.add(addressModel);
		customerModel.setDefaultShipmentAddress(addressModel);
		addressData.setId(null);
		addressData.setDefaultAddress(false);
		given(customerAccountService.getAddressBookDeliveryEntries(customerModel)).willReturn(addressesList);
		given(commerceCommonI18NService.getAllCountries()).willReturn(deliveryCountries);

		final List<AddressData> list = defaultUserFacade.getAddressBook();
		Assert.assertNotNull(list);
		Assert.assertEquals(1, list.size());
		Assert.assertFalse(list.iterator().next().isDefaultAddress());
	}

	@Test
	public void testIsDefaultAddress()
	{
		given(customerAccountService.getDefaultAddress(customerModel)).willReturn(addressModel);
		Assert.assertFalse(defaultUserFacade.isDefaultAddress(ADDRESS_ID));
		Assert.assertTrue(defaultUserFacade.isDefaultAddress("9999"));
	}

	@Test
	public void testAddAddress()
	{
		given(mockModelService.create(AddressModel.class)).willReturn(addressModel);
		addressData.setDefaultAddress(true);
		defaultUserFacade.addAddress(addressData);
		verify(customerAccountService).setDefaultAddressEntry(customerModel, addressModel);
		verify(customerAccountService).saveAddressEntry(customerModel, addressModel);
	}

	@Test
	public void testAddAddressNoDefault()
	{
		final CountryModel countryModel = new CountryModel();
		final List<CountryModel> deliveryCountries = new ArrayList<CountryModel>();
		deliveryCountries.add(countryModel);
		given(commerceCommonI18NService.getAllCountries()).willReturn(deliveryCountries);

		final AddressModel tmpAddress = new MockAddressModel(8888L);
		tmpAddress.setCountry(countryModel);
		given(customerAccountService.getAddressBookDeliveryEntries(Matchers.<CustomerModel> any())).willReturn(
				Collections.singletonList(tmpAddress));

		given(mockModelService.create(AddressModel.class)).willReturn(addressModel);
		addressData.setDefaultAddress(false);
		defaultUserFacade.addAddress(addressData);
		verify(customerAccountService, Mockito.never()).setDefaultAddressEntry(customerModel, addressModel);
		verify(customerAccountService).saveAddressEntry(customerModel, addressModel);
	}

	@Test
	public void testEditAddressDefault()
	{
		addressData.setDefaultAddress(true);
		defaultUserFacade.editAddress(addressData);
		verify(customerAccountService).setDefaultAddressEntry(customerModel, addressModel);
		verify(customerAccountService, Mockito.never()).clearDefaultAddressEntry(customerModel);
	}

	@Test
	public void testEditAddressDefaultShipment()
	{
		addressData.setDefaultAddress(false);
		customerModel.setDefaultShipmentAddress(addressModel);
		defaultUserFacade.editAddress(addressData);
		verify(customerAccountService, Mockito.never()).setDefaultAddressEntry(customerModel, addressModel);
		verify(customerAccountService).clearDefaultAddressEntry(customerModel);
	}

	@Test
	public void testEditAddressNoDefault()
	{
		final AddressModel mockAddress = mock(AddressModel.class);
		addressData.setDefaultAddress(false);
		customerModel.setDefaultShipmentAddress(mockAddress);
		defaultUserFacade.editAddress(addressData);
		verify(customerAccountService, Mockito.never()).setDefaultAddressEntry(customerModel, addressModel);
		verify(customerAccountService, Mockito.never()).clearDefaultAddressEntry(customerModel);
	}

	@Test
	public void testRemoveAddress()
	{
		final List<AddressModel> addressesList = new ArrayList<AddressModel>();
		addressModel = mock(AddressModel.class);
		addressesList.add(addressModel);
		final PK pk = PK.parse("123");
		addressData.setId(pk.getLongValueAsString());
		given(customerAccountService.getAddressBookEntries(customerModel)).willReturn(addressesList);
		given(addressModel.getPk()).willReturn(pk);
		defaultUserFacade.removeAddress(addressData);
		verify(customerAccountService).deleteAddressEntry(customerModel, addressModel);
	}

	@Test
	public void testRemoveNoAddress()
	{
		final List<AddressModel> addressesList = new ArrayList<AddressModel>();
		addressModel = mock(AddressModel.class);
		addressesList.add(addressModel);
		final PK pk = PK.parse("123");
		addressData.setId(pk.getLongValueAsString());
		given(customerAccountService.getAddressBookEntries(customerModel)).willReturn(addressesList);
		given(addressModel.getPk()).willReturn(PK.parse("222"));
		defaultUserFacade.removeAddress(addressData);
		verify(customerAccountService, Mockito.never()).deleteAddressEntry(customerModel, addressModel);
	}

	@Test
	public void testSetDefaultAddress()
	{
		defaultUserFacade.setDefaultAddress(addressData);
		verify(customerAccountService).setDefaultAddressEntry(customerModel, addressModel);
	}

	@Test
	public void testGetDefaultAddress()
	{
		customerModel.setDefaultShipmentAddress(addressModel);
		given(customerAccountService.getDefaultAddress(customerModel)).willReturn(addressModel);
		final AddressData address = defaultUserFacade.getDefaultAddress();
		verify(addressConverter).convert(addressModel);
		Assert.assertEquals(addressData, address);
	}

	@Test
	public void testGetNewDefaultAddress()
	{
		customerModel.setDefaultShipmentAddress(null);
		final AddressData defaultAddress = defaultUserFacade.getDefaultAddress();
		Assert.assertNull(defaultAddress);
		verify(addressConverter, Mockito.never()).convert(Mockito.any(AddressModel.class));
	}

	@Test
	public void testGetCCPaymentInfos()
	{
		ccPaymentInfoData.setDefaultPaymentInfo(false);
		customerModel.setDefaultPaymentInfo(creditCardPaymentInfoModel);

		final List<CCPaymentInfoData> list = defaultUserFacade.getCCPaymentInfos(true);
		Assert.assertNotNull(list);
		Assert.assertEquals(true, list.iterator().next().isDefaultPaymentInfo());
	}

	@Test
	public void testGetCCPaymentInfosNoDefaultInfo()
	{
		final CreditCardPaymentInfoModel mockCreditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
		ccPaymentInfoData.setDefaultPaymentInfo(false);
		customerModel.setDefaultPaymentInfo(mockCreditCardPaymentInfoModel);

		final List<CCPaymentInfoData> list = defaultUserFacade.getCCPaymentInfos(true);
		Assert.assertNotNull(list);
		Assert.assertEquals(false, list.iterator().next().isDefaultPaymentInfo());
	}

	@Test
	public void testGetCCPaymentInfoForCode()
	{
		ccPaymentInfoData.setDefaultPaymentInfo(false);
		customerModel.setDefaultPaymentInfo(creditCardPaymentInfoModel);
		final CCPaymentInfoData paymentInfoData = defaultUserFacade.getCCPaymentInfoForCode("code");
		Assert.assertNotNull(paymentInfoData);
	}

	@Test
	public void testGetCCPaymentInfoForCodeBlank()
	{
		Assert.assertNull(defaultUserFacade.getCCPaymentInfoForCode(""));
	}

	@Test
	public void testGetCCPaymentInfoForCodeNoInfoModel()
	{
		ccPaymentInfoData.setDefaultPaymentInfo(false);
		customerModel.setDefaultPaymentInfo(creditCardPaymentInfoModel);
		given(customerAccountService.getCreditCardPaymentInfoForCode(customerModel, "code")).willReturn(null);
		final CCPaymentInfoData paymentInfoData = defaultUserFacade.getCCPaymentInfoForCode("code");
		Assert.assertNull(paymentInfoData);
	}

	@Test
	public void testGetCCPaymentInfoForCodeNoDefault()
	{
		final CreditCardPaymentInfoModel mockCreditCardPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
		ccPaymentInfoData.setDefaultPaymentInfo(false);
		customerModel.setDefaultPaymentInfo(mockCreditCardPaymentInfoModel);
		final CCPaymentInfoData paymentInfoData = defaultUserFacade.getCCPaymentInfoForCode("code");
		Assert.assertEquals(false, paymentInfoData.isDefaultPaymentInfo());

	}

	@Test
	public void testSetDefaultPaymentInfo()
	{
		ccPaymentInfoData.setId("code");
		defaultUserFacade.setDefaultPaymentInfo(ccPaymentInfoData);
		verify(customerAccountService).setDefaultPaymentInfo(customerModel, creditCardPaymentInfoModel);
	}

	@Test
	public void testNoSetDefaultPaymentInfo()
	{
		ccPaymentInfoData.setId("code");
		given(customerAccountService.getCreditCardPaymentInfoForCode(customerModel, "code")).willReturn(null);
		defaultUserFacade.setDefaultPaymentInfo(ccPaymentInfoData);
		verify(customerAccountService, Mockito.never()).setDefaultPaymentInfo(customerModel, creditCardPaymentInfoModel);
	}

	@Test
	public void testSyncSessionLanguage()
	{
		final LanguageModel language = new LanguageModel();
		given(commonI18NService.getCurrentLanguage()).willReturn(language);
		customerModel = mock(CustomerModel.class);
		given(userService.getCurrentUser()).willReturn(customerModel);
		defaultUserFacade.syncSessionLanguage();
		verify(customerModel).setSessionLanguage(language);
		verify(mockModelService).save(customerModel);
	}

	@Test
	public void testSyncSessionCurrency()
	{
		final CurrencyModel currency = new CurrencyModel();
		given(commonI18NService.getCurrentCurrency()).willReturn(currency);
		customerModel = mock(CustomerModel.class);
		given(userService.getCurrentUser()).willReturn(customerModel);
		defaultUserFacade.syncSessionCurrency();
		verify(customerModel).setSessionCurrency(currency);
		verify(mockModelService).save(customerModel);
	}

}
