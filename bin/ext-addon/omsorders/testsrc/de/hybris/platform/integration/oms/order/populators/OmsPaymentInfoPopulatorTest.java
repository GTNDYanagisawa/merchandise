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
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;

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
import com.hybris.oms.domain.order.PaymentInfo;


/**
 * Created with IntelliJ IDEA. User: SRNaidu Date: 04/10/12 Time: 12:07 PM To change this template use File | Settings |
 * File Templates.
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OmsPaymentInfoPopulatorTest
{

	private OmsPaymentInfoPopulator omsPaymentInfoPopulator;
	private PaymentInfo paymentInfo;
	private OmsAddressPopulator omsAddressPopulator;
	private CreditCardPaymentInfoModel creditCardPaymentInfoModel;
	private CountryModel countryModel;
	@Mock
	private CustomerNameStrategy customerNameStrategy;

	private AbstractPopulatingConverter<AddressModel, Address> addressConverter;

	@Before
	public void beforeTest() throws Exception
	{
		MockitoAnnotations.initMocks(this.getClass());
		omsPaymentInfoPopulator = new OmsPaymentInfoPopulator();
	}

	@Test
	public void testOmsPaymentInfoPopulator() throws Exception
	{

		paymentInfo = new PaymentInfo();
		countryModel = Mockito.mock(CountryModel.class);
		omsAddressPopulator = new OmsAddressPopulator();
		omsAddressPopulator.setCustomerNameStrategy(customerNameStrategy);

		addressConverter = new ConverterFactory<AddressModel, Address, OmsAddressPopulator>().create(Address.class,
				omsAddressPopulator);

		creditCardPaymentInfoModel = Mockito.mock(CreditCardPaymentInfoModel.class);
		final AddressModel addressModel = Mockito.mock(AddressModel.class);
		omsPaymentInfoPopulator.setAddressConverter(addressConverter);

		BDDMockito.when(creditCardPaymentInfoModel.getType()).thenReturn(CreditCardType.VISA);
		BDDMockito.when(creditCardPaymentInfoModel.getSubscriptionId()).thenReturn("test1");
		BDDMockito.when(creditCardPaymentInfoModel.getBillingAddress()).thenReturn(addressModel);
		BDDMockito.when(creditCardPaymentInfoModel.getPk()).thenReturn(PK.fromLong(123456));

		BDDMockito.when(addressModel.getTown()).thenReturn("testTown");
		BDDMockito.when(addressModel.getCountry()).thenReturn(countryModel);
		BDDMockito.when(countryModel.getName()).thenReturn("Canada");
		BDDMockito.when(addressModel.getLine1()).thenReturn("testAddressline1");
		BDDMockito.when(addressModel.getLine2()).thenReturn("testAddressline2");
		BDDMockito.when(addressModel.getStreetnumber()).thenReturn("testAddressline2");//TODO fix confusion betweem line1 and street name etc..
		BDDMockito.when(addressModel.getStreetname()).thenReturn("testAddressline1");
		BDDMockito.when(addressModel.getPostalcode()).thenReturn("testpostalcode");


		omsPaymentInfoPopulator.populate(creditCardPaymentInfoModel, paymentInfo);

		Assert.assertEquals(paymentInfo.getBillingAddress().getCityName(), creditCardPaymentInfoModel.getBillingAddress().getTown());
		Assert.assertEquals(paymentInfo.getBillingAddress().getCountryName(), creditCardPaymentInfoModel.getBillingAddress()
				.getCountry().getName());
		Assert.assertEquals(paymentInfo.getBillingAddress().getAddressLine1(), creditCardPaymentInfoModel.getBillingAddress()
				.getLine1());
		Assert.assertEquals(paymentInfo.getBillingAddress().getAddressLine2(), creditCardPaymentInfoModel.getBillingAddress()
				.getLine2());
		Assert.assertEquals(paymentInfo.getBillingAddress().getPostalZone(), creditCardPaymentInfoModel.getBillingAddress()
				.getPostalcode());
		Assert.assertEquals(paymentInfo.getPaymentInfoType(), creditCardPaymentInfoModel.getType().getType());
		Assert.assertNull(paymentInfo.getAuthUrl());
	}
}
