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
package de.hybris.platform.commercefacades.user.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CustomerPopulatorTest
{
	@Mock
	private AbstractConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AbstractConverter<CurrencyModel, CurrencyData> currencyConverter;
	@Mock
	private AbstractConverter<LanguageModel, LanguageData> languageConverter;
	@Mock
	private CustomerNameStrategy customerNameStrategy;

	private AbstractConverter<UserModel, CustomerData> customerConverter;


	private static final String DUMMY = "dummy";


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final CustomerPopulator customerPopulator = new CustomerPopulator();
		customerPopulator.setCurrencyConverter(currencyConverter);
		customerPopulator.setCustomerNameStrategy(customerNameStrategy);
		customerPopulator.setLanguageConverter(languageConverter);
		customerConverter = new ConverterFactory<UserModel, CustomerData, CustomerPopulator>().create(CustomerData.class,
				customerPopulator);
	}

	@Test
	public void testConvertAll()
	{

		final CustomerModel userModel = mock(CustomerModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final CurrencyData currencyData = mock(CurrencyData.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final LanguageData languageData = mock(LanguageData.class);
		final TitleModel titleModel = mock(TitleModel.class);
		given(userModel.getSessionCurrency()).willReturn(currencyModel);
		given(currencyConverter.convert(currencyModel)).willReturn(currencyData);

		given(userModel.getSessionLanguage()).willReturn(languageModel);
		given(languageConverter.convert(languageModel)).willReturn(languageData);
		given(userModel.getName()).willReturn("userName1 userName2");
		given(userModel.getTitle()).willReturn(titleModel);
		given(titleModel.getCode()).willReturn("titleCode");
		given(userModel.getUid()).willReturn("userUid");
		given(customerNameStrategy.splitName("userName1 userName2")).willReturn(new String[]
		{ "userName1", "userName2" });

		final CustomerData customerData = customerConverter.convert(userModel);

		Assert.assertNotNull(customerData);
		Assert.assertEquals(currencyData, customerData.getCurrency());
		Assert.assertEquals(languageData, customerData.getLanguage());
		Assert.assertEquals("userName1", customerData.getFirstName());
		Assert.assertEquals("userName2", customerData.getLastName());
		Assert.assertEquals("titleCode", customerData.getTitleCode());
		Assert.assertEquals("userName1 userName2", customerData.getName());
		Assert.assertEquals("userUid", customerData.getUid());
	}

	@Test
	public void testConvertEssential()
	{
		final CustomerModel userModel = mock(CustomerModel.class);
		final TitleModel titleModel = mock(TitleModel.class);
		given(userModel.getSessionCurrency()).willReturn(null);
		given(userModel.getDefaultPaymentAddress()).willReturn(null);
		given(userModel.getDefaultShipmentAddress()).willReturn(null);
		given(userModel.getSessionLanguage()).willReturn(null);
		given(userModel.getName()).willReturn("userName1 userName2");
		given(titleModel.getCode()).willReturn("titleCode");
		given(userModel.getUid()).willReturn("userUid");
		given(customerNameStrategy.splitName("userName1 userName2")).willReturn(new String[]
		{ "userName1", "userName2" });

		final CustomerData customerData = customerConverter.convert(userModel);

		Assert.assertNotNull(customerData);
		Assert.assertNull(customerData.getCurrency());
		Assert.assertNull(customerData.getDefaultBillingAddress());
		Assert.assertNull(customerData.getDefaultShippingAddress());
		Assert.assertNull(customerData.getLanguage());
		Assert.assertNull(customerData.getTitleCode());
		Assert.assertEquals("userName1", customerData.getFirstName());
		Assert.assertEquals("userName2", customerData.getLastName());
		Assert.assertEquals("userName1 userName2", customerData.getName());
		Assert.assertEquals("userUid", customerData.getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertSourceNull()
	{
		customerConverter.convert(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertPrototypeNull()
	{
		final CustomerModel userModel = mock(CustomerModel.class);
		customerConverter.convert(userModel, null);
	}

	@Test
	public void testConvertWithUidForCustomer()
	{

		final CustomerModel user = new CustomerModel();
		user.setUid(DUMMY);

		final CustomerData customer = new CustomerData();

		customerConverter.populate(user, customer);

		Assert.assertEquals(DUMMY, customer.getUid());
		Assert.assertNull(customer.getDisplayUid());
	}

	@Test
	public void testConvertWithOrignalUidForCustomer()
	{

		final CustomerModel user = new CustomerModel();
		user.setOriginalUid(DUMMY);
		user.setUid(null);

		final CustomerData customer = new CustomerData();

		customerConverter.populate(user, customer);

		Assert.assertNull(customer.getUid());
		Assert.assertEquals(DUMMY, customer.getDisplayUid());

	}

	@Test
	public void testConvertWithoutOrignalUidOrUidForCustomer()
	{

		final CustomerModel user = new CustomerModel();
		user.setOriginalUid(null);
		user.setUid(null);

		final CustomerData customer = new CustomerData();

		customerConverter.populate(user, customer);

		Assert.assertNull(customer.getUid());
		Assert.assertNull(customer.getDisplayUid());
	}
}
